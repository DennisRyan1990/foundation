package com.zchi.common.ftp;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

/**
 * Generic callback interface for code that operates on a FTPClient.
 * Imitate Spring JdbcTemplate
 */
public interface FTPClientCallback<T> {

    T doInClient(FTPClient ftp) throws IOException;
}
