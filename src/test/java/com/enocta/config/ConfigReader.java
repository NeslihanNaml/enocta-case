package com.enocta.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;

/**
 * Test konfigürasyonunu okur.
 * Öncelik sırası: JVM system property (-Dkey=value) > ortam değişkeni (KEY_NAME) > config.properties.
 * Örn. "api.base.url" anahtarı için ortam değişkeni adı "API_BASE_URL" olur.
 */
public final class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = loadProperties();

    private ConfigReader() {
    }

    public static String get(String key) {
        String systemValue = System.getProperty(key);
        if (isPresent(systemValue)) {
            return systemValue;
        }
        String envValue = System.getenv(toEnvName(key));
        if (isPresent(envValue)) {
            return envValue;
        }
        String fileValue = PROPERTIES.getProperty(key);
        if (isPresent(fileValue)) {
            return fileValue.trim();
        }
        throw new IllegalStateException("Konfigürasyon değeri bulunamadı: " + key
                + " (-D" + key + ", " + toEnvName(key) + " ortam değişkeni veya " + CONFIG_FILE + ")");
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException(CONFIG_FILE + " classpath üzerinde bulunamadı");
            }
            properties.load(new InputStreamReader(input, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException(CONFIG_FILE + " okunamadı", e);
        }
        return properties;
    }

    private static String toEnvName(String key) {
        return key.toUpperCase(Locale.ROOT).replace('.', '_');
    }

    private static boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }
}
