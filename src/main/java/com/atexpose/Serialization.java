package com.atexpose;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import static com.fasterxml.jackson.module.kotlin.ExtensionsKt.jacksonObjectMapper;

public class Serialization {
    private static final ObjectMapper serializer = jacksonObjectMapper();
    private static final ObjectMapper deserializer = jacksonObjectMapper();


    @SneakyThrows
    public static String objectToJsonString(Object o) {
        return serializer.writeValueAsString(o);
    }

    @SneakyThrows
    public static <T> T jsonStringToObject(String value, Class<T> clazz) {
        return deserializer.readValue(value, clazz);
    }

}
