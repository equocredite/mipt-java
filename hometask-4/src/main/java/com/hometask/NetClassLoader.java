package com.hometask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class NetClassLoader extends ClassLoader {
    private final String[] urls;
    Decoder decoder;

    public NetClassLoader(String[] urls, Decoder decoder) {
        this.urls = urls;
        this.decoder = decoder;
    }

    public NetClassLoader(String[] urls) {
        this(urls, new DecrementDecoder());
    }

    private static byte[] downloadDataFromUrl(String url) throws IOException {
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

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        for (var url : urls) {
            try {
                byte[] data = downloadDataFromUrl(url);
                data = decoder.decode(data);
                if (name.equals(new String(Arrays.copyOfRange(data, 0, name.length())))) {
                    return defineClass(name, data, name.length(), data.length - name.length());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        throw new ClassNotFoundException();
    }

    public static void main(String[] args) {
        var cl = new NetClassLoader(new String[]{
                "https://www.googleapis.com/drive/v3/files/1Hda8Kn9m96LBiNDUvBP7gTmrLustg70H?alt=media&key=AIzaSyCnyt2lgtvTEVvITi-mD7v0s49OaxLcEog",
                "https://www.googleapis.com/drive/v3/files/1wG0bcva7AcA2v2TUEADYJFWCgSyL7KzN?alt=media&key=AIzaSyCnyt2lgtvTEVvITi-mD7v0s49OaxLcEog"
        });
        try {
            Runnable obj1 = (Runnable) cl.loadClass("ru.sbt.java.tasks.Secret").getConstructor().newInstance();
            obj1.run();

            Runnable obj2 = (Runnable) cl.loadClass("ru.sbt.java.tasks.VeryStrangeSecret").getConstructor().newInstance();
            obj2.run();
        } catch (Exception ignored) {}
    }
}
