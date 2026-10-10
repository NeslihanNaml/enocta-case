package com.enocta.utils;

import com.enocta.config.ConfigReader;

import java.time.Duration;

/**
 * UI adımları arasında sayfanın ve ağın oturması için sabit süreli bekleme.
 * Süre config.properties içindeki "ui.pause.seconds" değeri ile ayarlanır.
 */
public final class Pause {

    private Pause() {
    }

    public static void configuredDuration() {
        sleep(Duration.ofSeconds(ConfigReader.getInt("ui.pause.seconds")));
    }

    private static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Bekleme kesintiye uğradı", e);
        }
    }
}
