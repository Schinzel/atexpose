package com.atexpose;


import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

public class MyPropertiesTest {

    @Test
    public void constructor() {
        assertThatCode(MyProperties::new)
                .doesNotThrowAnyException();
    }
}