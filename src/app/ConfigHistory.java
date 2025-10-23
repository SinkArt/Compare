package app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Collections;

public class ConfigHistory {

    private static final String HISTORY_FILE = "history.json";
    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Завантажує попередні результати виконання з history.json.
     * @return Map<String, String> - Імена конфігів та їх вміст.
     */
    public Map<String, String> loadPreviousConfigs() {
        File file = new File(HISTORY_FILE);
        if (!file.exists()) {
            System.out.println("Файл історії не знайдено. Перший запуск.");
            return Collections.emptyMap();
        }

        try {
            System.out.println("Завантаження попередніх конфігів з " + HISTORY_FILE);
            return objectMapper.readValue(file, new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {
            System.err.println("Помилка читання файлу історії: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * Зберігає поточні конфігурації для наступного порівняння.
     * @param currentConfigs Мапа поточних конфігурацій.
     */
    public void saveCurrentConfigs(Map<String, String> currentConfigs) {
        try {
            objectMapper.writeValue(new File(HISTORY_FILE), currentConfigs);
            System.out.println("\nПоточні конфігурації збережено у " + HISTORY_FILE);
        } catch (IOException e) {
            System.err.println("Помилка запису файлу історії: " + e.getMessage());
        }
    }
}