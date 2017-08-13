package com.atexpose.dispatcher.logging;

import io.schinzel.basicutils.crypto.cipher.ICipher;

import java.util.Map;

public interface ILogEntry {
    Map<String, String> getLogData(ICipher crypto);


    boolean isError();
}
