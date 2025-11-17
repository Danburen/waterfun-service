package org.waterwood.waterfunservice.infrastructure.utils;

public class FileUtil {
    public static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
