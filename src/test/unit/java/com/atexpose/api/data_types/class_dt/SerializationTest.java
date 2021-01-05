package com.atexpose.api.data_types.class_dt;

import lombok.val;
import org.junit.Test;
import java.time.Instant;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SerializationTest {
    static String DATE_TIME_STRING = "1997-08-04T06:14:18.000Z";
    static Instant TIME_STAMP = Instant.parse(DATE_TIME_STRING);

    @Test
    public void objectToJsonString_ContainsInstant_InstantInCorrectFormat() {
        val myTestClass = new MyTestClass();
        String serializedString = Serialization.objectToJsonString(myTestClass);
        assertThat(serializedString).contains(DATE_TIME_STRING);
    }


    @Test
    public void apa() {
        String serializedString = "{\"firstName\":\"Henrik\",\"timeStamp\":\"" + DATE_TIME_STRING + "\"}";
        MyTestClass deserializedClass = Serialization
                .jsonStringToObject(serializedString, MyTestClass.class);
        assertThat(deserializedClass.timeStamp).isEqualTo(TIME_STAMP);
    }
}