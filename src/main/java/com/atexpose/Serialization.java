package com.atexpose;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class Serialization {
    private static final ObjectMapper serializer = new ObjectMapper();
    private static final ObjectMapper deserializer = new ObjectMapper();


    @SneakyThrows
    public static String objectToJsonString(Object o) {
        return serializer.writeValueAsString(o);
    }

    @SneakyThrows
    public static <T> T jsonStringToObject(String value, Class<T> clazz){
        return deserializer.readValue(value, clazz);
    }

    public static void main(String[] args) {
        RemoveMeVar class2 = new RemoveMeVar("Two", 2);
        String s = Serialization.objectToJsonString(class2);
        RemoveMeVar removeMeVar = Serialization.jsonStringToObject(s, RemoveMeVar.class);
        System.out.println(s);
        System.out.println(removeMeVar.toString());
    }
}
