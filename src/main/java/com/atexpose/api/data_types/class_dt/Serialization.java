package com.atexpose.api.data_types.class_dt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.SneakyThrows;
import lombok.val;
import java.time.Instant;
import java.time.format.DateTimeFormatterBuilder;

/**
 * The purpose of this class is to serialize and deserialize.
 * <p>
 * To be able to handle (de)serialization in JavaScript
 * an instant is serialized as "1997-08-04T06:14:18.000Z"
 * instead of default "{"epochSecond":870675258,"nano":0}"
 */
class Serialization {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        val javaTimeModule = new JavaTimeModule()
                .addSerializer(Instant.class, new CustomInstantSerializer());
        OBJECT_MAPPER.registerModule(javaTimeModule);
    }


    /**
     * Private constructor to prevent incorrect instantiation
     */
    private Serialization() {
    }


    /**
     * Serialization of an object
     *
     * @param obj The object to serialize
     * @return The argument object as a string
     */
    @SneakyThrows
    static String objectToJsonString(Object obj) {
        return OBJECT_MAPPER.writeValueAsString(obj);
    }


    /**
     * Deserialization of a string to an object
     *
     * @param str   The string to deserialize to
     * @param clazz The class to return
     * @param <T>   The class to return
     * @return An instance of the argument string
     */
    @SneakyThrows
    public static <T> T jsonStringToObject(String str, Class<T> clazz) {
        return OBJECT_MAPPER.readValue(str, clazz);
    }


    private static class CustomInstantSerializer extends InstantSerializer {
        CustomInstantSerializer() {
            this(3, false);
        }

        private CustomInstantSerializer(int fractionalDigits, boolean useTimestamp) {
            super(InstantSerializer.INSTANCE, useTimestamp, new DateTimeFormatterBuilder()
                    .appendInstant(fractionalDigits)
                    .toFormatter());

        }
    }
}
