package app;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JsonDownloader {

    /**
     * Завантажує вміст JSON за заданим URL.
     * @param urlString URL JSON-файлу.
     * @return Вміст JSON у вигляді рядка.
     * @throws IOException Якщо виникають проблеми з мережею або зчитуванням.
     */
    public String downloadJson(String urlString) throws IOException {
        System.out.println("-> Завантаження: " + urlString);
        try (InputStream inputStream = new URL(urlString).openStream()) {
            // Зчитуємо весь потік як рядок
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}