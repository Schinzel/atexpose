package com.atexpose.api.datatypes;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassDTTest {


    @Test
    public void verifyValue_ClassSerialized_True() {
        ClassDT<MyClass> classDT = new ClassDT<>(MyClass.class);
        String jsonString = classDT.convertFromDataTypeToString(new MyClass());
        boolean actual = classDT.verifyValue(jsonString);
        assertThat(actual).isTrue();
    }

    @Test
    public void verifyValue_EnumSerialized_True() {
        ClassDT<MyEnum> classDT = new ClassDT<>(MyEnum.class);
        String jsonString = classDT.convertFromDataTypeToString(MyEnum.FIRST);
        boolean actual = classDT.verifyValue(jsonString);
        assertThat(actual).isTrue();
    }


    @Test
    public void castToDataType_ClassSerialized_SameAsSerializedClass() {
        ClassDT<MyClass> classDT = new ClassDT<>(MyClass.class);
        MyClass myClass = new MyClass();
        String jsonString = classDT.convertFromDataTypeToString(myClass);
        MyClass actual = classDT.castToDataType(jsonString);
        assertThat(actual).usingRecursiveComparison().isEqualTo(myClass);
    }


    @Test
    public void castToDataType_EnumSerialized_SameAsSerializedEnum() {
        ClassDT<MyEnum> classDT = new ClassDT<>(MyEnum.class);
        String jsonString = classDT.convertFromDataTypeToString(MyEnum.FIRST);
        MyEnum actual = classDT.castToDataType(jsonString);
        assertThat(actual).isEqualTo(MyEnum.FIRST);
    }


    static class MyClass {
        @SuppressWarnings({"unused", "RedundantSuppression"})
        public int age = 44;
    }

    enum MyEnum {
        FIRST
    }

}