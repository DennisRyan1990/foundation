package com.zchi.common.ftp;

import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Predicate;

/**
 * Interface specifying a basic set of FTP operations.
 */
public interface FTPClientOperations {
    <T> T execute(FTPClientCallback<T> callback) throws IOException;

    List<String> ls(String file) throws IOException;

    List<String> ls() throws IOException;

    List<String> ls(String file, Predicate<FTPFile> fileFilter) throws IOException;

    String pwd() throws IOException;

    boolean cd(String path) throws IOException;

    boolean exists(String file) throws IOException;

    boolean isFile(String file) throws IOException;

    boolean isDirectory(String file) throws IOException;

    boolean mkdir(String file) throws IOException;

    boolean delete(String file) throws IOException;

    boolean rmdir(String file) throws IOException;

    InputStream get(String file) throws IOException;

    boolean get(String file, OutputStream outputStream) throws IOException;

    OutputStream put(String file) throws IOException;

    boolean put(String file, InputStream inputStream) throws IOException;

}
