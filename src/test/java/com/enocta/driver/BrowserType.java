package com.enocta.driver;

import java.util.Arrays;
import java.util.Locale;

public enum BrowserType {
    CHROME,
    FIREFOX,
    EDGE;

    public static BrowserType from(String name) {
        return Arrays.stream(values())
                .filter(type -> type.name().equals(name.trim().toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Desteklenmeyen tarayıcı: " + name + ". Geçerli değerler: " + Arrays.toString(values())));
    }
}
