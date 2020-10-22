package com.atexpose;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

public class NativeSetupTest {
    @Test
    public void constructor() {
        assertThatCode(NativeSetup::new)
                .doesNotThrowAnyException();
    }

}