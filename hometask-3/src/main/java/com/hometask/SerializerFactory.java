package com.hometask;

public interface SerializerFactory<T> {
    Serializer<T> createSerializer();
}
