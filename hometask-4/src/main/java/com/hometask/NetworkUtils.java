package com.hometask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class NetworkUtils {
    public static byte[] downloadDataFromUrl(String url) throws IOException {
        var outputStream = new ByteArrayOutputStream();
        try (var inputStream = new URL(url).openStream()) {
            byte[] chunk = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }
        }
        return outputStream.toByteArray();
    }
}
