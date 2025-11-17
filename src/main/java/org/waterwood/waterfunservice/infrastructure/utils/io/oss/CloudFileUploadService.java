package org.waterwood.waterfunservice.infrastructure.utils.io.oss;

import java.io.FileInputStream;
import java.io.InputStream;

public interface CloudFileUploadService {
    /**
     * Upload a file to cloud storage
     * @param key the key of the file
     * @param stream file input stream {@link InputStream}
     * @param size file size
     * @param contentType file content type
     */
    void uploadFile(String key, InputStream stream, long size, String contentType);
}
