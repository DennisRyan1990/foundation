package com.zchi.common.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Imitate Spring JdbcTemplate
 */
public class FTPClientTemplate implements FTPClientOperations, AutoCloseable {

    /**
     * 默认端口
     */
    private static int DEFAULT_REMOTE_PORT = 21;
    /**
     * 远程字符集
     */
    private final Charset remoteCharset;
    /**
     * 控制字符集
     */
    private final Charset controlCharset;

    //连接属性

    private final String host;
    private final String username;
    private final String password;
    private final Integer port;
    private final Boolean localActive;

    private String wd = "/";

    /**
     * client
     */
    private FTPClient ftpClient;

    public FTPClientTemplate(String host, String username, String password, Charset remoteCharset)
        throws IOException {
        this(host, username, password, DEFAULT_REMOTE_PORT, remoteCharset, false);
    }

    public FTPClientTemplate(String host, String username, String password) throws IOException {
        this(host, username, password, DEFAULT_REMOTE_PORT, Charset.defaultCharset(), false);
    }

    public FTPClientTemplate(String host, String username, String password, int port,
        Charset remoteCharset, boolean localActive) throws IOException {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
        this.localActive = localActive;
        this.controlCharset = Charset.forName(getClient().getControlEncoding());
        this.remoteCharset = remoteCharset;
    }

    /**
     * 获取ftpClient,需要判断是否关闭连接,需要有reconnect的机制
     *
     * @return client
     * @throws IOException
     */
    private synchronized FTPClient getClient() throws IOException {
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.getStatus();
                return ftpClient;
            }
        } catch (Exception ignored) {
        }
        ftpClient = new FTPClient();
        ftpClient.configure(new FTPClientConfig());
        ftpClient.connect(host, port);
        ftpClient.login(username, password);
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        ftpClient.setControlKeepAliveTimeout(300);
        if (localActive) {
            ftpClient.enterLocalActiveMode();
        } else {
            ftpClient.enterLocalPassiveMode();
        }
        if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            throw new RuntimeException("FTP server refused connection.");
        }
        ftpClient.changeWorkingDirectory(wd);
        return ftpClient;
    }

    /**
     * 对输入的字符串做做转码
     */
    public String mapInput(String path) {
        return new String(path.getBytes(remoteCharset), controlCharset);
    }

    /**
     * 对输出的字符串做做转码
     */
    public String mapOutput(String path) {
        return new String(path.getBytes(controlCharset), remoteCharset);
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public Charset getRemoteCharset() {
        return remoteCharset;
    }

    /**
     * 自动关闭
     *
     * @throws Exception
     */
    @Override public void close() throws Exception {
        if (ftpClient.isConnected()) {
            ftpClient.disconnect();
        }
    }

    @Override public <T> T execute(FTPClientCallback<T> callback) throws IOException {
        return callback.doInClient(getClient());
    }

    // list files and directories

    @Override public List<String> ls(String file) throws IOException {
        return ls(file, f -> true);
    }

    @Override public List<String> ls() throws IOException {
        return ls(".");
    }

    @Override public List<String> ls(String file, Predicate<FTPFile> fileFilter)
        throws IOException {
        return execute(ftp -> Arrays.stream(ftp.listFiles(mapInput(file), fileFilter::test))
            .map(f -> mapOutput(f.getName())).collect(Collectors.toList()));
    }

    // working directory and change directory

    @Override public String pwd() throws IOException {
        return execute(ftp -> mapOutput(ftp.printWorkingDirectory()));
    }

    @Override public boolean cd(String path) throws IOException {
        Boolean result = execute(ftp -> ftp.changeWorkingDirectory(mapInput(path)));
        wd = execute(FTPClient::printWorkingDirectory);
        return result;
    }

    // test path

    /**
     * True if file exists
     */
    @Override public boolean exists(String file) throws IOException {
        return isFile(file) || isDirectory(file);
    }

    /**
     * True if file exists and is a regular file
     */
    @Override public boolean isFile(String file) throws IOException {
        try (InputStream inputStream = get(file)) {
            return inputStream != null;
        }
    }

    /**
     * True if file exists and is a directory
     */
    @Override public boolean isDirectory(String file) throws IOException {
        String before = pwd();
        if (cd(file)) {
            cd(before);
            return true;
        } else {
            return false;
        }
    }

    // mkdir

    @Override public boolean mkdir(String file) throws IOException {
        if (exists(file)) {
            throw new RuntimeException(file + " exists");
        }
        return execute(ftp -> ftp.makeDirectory(mapInput(file)));
    }

    // delete

    @Override public boolean delete(String file) throws IOException {
        if (isFile(file)) {
            return execute(ftp -> ftp.deleteFile(mapInput(file)));
        }
        throw new RuntimeException(file + " not exists or is not a file");
    }

    @Override public boolean rmdir(String file) throws IOException {
        if (isDirectory(file)) {
            return execute(ftp -> ftp.removeDirectory(mapInput(file)));
        }
        throw new RuntimeException(file + " not exists or is not a directory");
    }


    // upload and download

    @Override public InputStream get(String file) throws IOException {
        return execute(ftp -> ftp.retrieveFileStream(mapInput(file)));
    }

    /**
     * 将文件内容写入输出流
     */
    @Override public boolean get(String file, OutputStream outputStream) throws IOException {
        if (isFile(file)) {
            return execute(ftp -> ftp.retrieveFile(mapInput(file), outputStream));
        }
        return false;
    }

    /**
     * 在远程写入文件
     */
    @Override public OutputStream put(String file) throws IOException {
        if (exists(file)) {
            throw new RuntimeException(file + " exists");
        } else {
            return execute(f -> f.storeFileStream(mapInput(file)));
        }
    }

    /**
     * 在远程写入文件
     */
    @Override public boolean put(String file, InputStream inputStream) throws IOException {
        if (exists(file)) {
            throw new RuntimeException(file + " exists");
        } else {
            return execute(f -> f.storeFile(mapInput(file), inputStream));
        }
    }
}
