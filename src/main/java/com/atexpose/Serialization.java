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
        RemoveMeClass2 class2 = new RemoveMeClass2("Two", 2);
        String s = Serialization.objectToJsonString(class2);
        RemoveMeClass2 removeMeClass2 = Serialization.jsonStringToObject(s, RemoveMeClass2.class);
        System.out.println(s);
        System.out.println(removeMeClass2.toString());
    }
}
