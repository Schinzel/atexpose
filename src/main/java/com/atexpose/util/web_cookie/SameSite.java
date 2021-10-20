package com.atexpose.util.web_cookie;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public enum SameSite {
    STRICT("Strict"), LAX("Lax"), NONE("None");
    @Getter
    private final String mAttributeValue;

    SameSite(String attributeValue) {
        mAttributeValue = attributeValue;
    }
}
