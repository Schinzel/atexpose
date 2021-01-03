package com.atexpose;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.SneakyThrows;

import java.time.Instant;
import java.time.format.DateTimeFormatterBuilder;

import static com.fasterxml.jackson.module.kotlin.ExtensionsKt.jacksonObjectMapper;

public class Serialization {
    private static final ObjectMapper serializer = jacksonObjectMapper();
    private static final ObjectMapper deserializer = jacksonObjectMapper();

    static {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Instant.class, new CustomInstantSerializer());
        serializer.registerModule(javaTimeModule);
        deserializer.registerModule(javaTimeModule);
    }


    private Serialization() {
    }


    @SneakyThrows
    public static String objectToJsonString(Object o) {
        return serializer.writeValueAsString(o);
    }


    @SneakyThrows
    public static <T> T jsonStringToObject(String value, Class<T> clazz) {
        return deserializer.readValue(value, clazz);
    }


    private static class CustomInstantSerializer extends InstantSerializer {
        CustomInstantSerializer() {
            super(INSTANCE, false, new DateTimeFormatterBuilder()
                    .appendInstant(3)
                    .toFormatter());
        }
    }
}
