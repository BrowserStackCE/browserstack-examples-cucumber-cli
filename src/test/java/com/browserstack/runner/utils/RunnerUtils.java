package com.browserstack.runner.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class RunnerUtils {

    public static void createDirectories(String dirPath) throws IOException {

        final File reportDir = new File(dirPath);

        if (!reportDir.exists()) {
            if (!reportDir.mkdirs()) {
                throw new IOException(String.format("Unable to create the %s directory", reportDir));
            }
        }
    }

    public static void writeToFile(String filePath, String content, boolean isAppend) {

        try {
            File file = new File(filePath);
            if(!file.exists()) {
                file.createNewFile();
            }

            if(isAppend) {
                Files.write(
                        Paths.get(filePath),
                        content.getBytes(),
                        StandardOpenOption.APPEND);
            } else {
                Files.write(
                        Paths.get(filePath),
                        content.getBytes(),
                        StandardOpenOption.WRITE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
