package ru.storageplace.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class DatabaseConfig {
    private static final Properties PROPERTIES = loadProperties();

    private DatabaseConfig() {
    }

    public static String url() {
        return getRequiredProperty("db.url");
    }

    public static String user() {
        return getRequiredProperty("db.user");
    }

    public static String password() {
        return getRequiredProperty("db.password");
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = DatabaseConfig.class
                .getClassLoader()
                .getResourceAsStream("ru/storageplace/application.properties")) {

            if (inputStream == null) {
                throw new IllegalStateException("Файл application.properties не найден");
            }

            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка чтения application.properties", e);
        }
    }

    private static String getRequiredProperty(String key) {
        String value = PROPERTIES.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Не задан параметр " + key);
        }

        return value;
    }
}