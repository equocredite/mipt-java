package com.hometask;

import java.io.IOException;
import java.util.*;

import static com.hometask.NetworkUtils.*;
import static com.hometask.ByteUtils.*;

public class NetClassLoader extends ClassLoader {
    private final String[] urls;
    private final Decoder decoder;
    private final List<byte[]> sources = new ArrayList<>();

    public NetClassLoader(String[] urls, Decoder decoder) throws RuntimeException {
        this.urls = urls;
        this.decoder = decoder;
        downloadSources();
    }

    public NetClassLoader(String[] urls) {
        this(urls, new DecrementDecoder());
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        for (var bytes : sources) {
            if (bytesStartWith(bytes, name)) {
                return defineClass(name, bytes, name.length(), bytes.length - name.length());
            }
        }
        throw new ClassNotFoundException();
    }

    private void downloadSources() throws RuntimeException {
        for (var url : urls) {
            byte[] bytes;
            try {
                bytes = downloadDataFromUrl(url);
            } catch (IOException e) {
                throw new RuntimeException("couldn't download class bytes from " + url);
            }
            sources.add(decoder.decode(bytes));
        }
    }
}
