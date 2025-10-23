package app;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        JsonDownloader downloader = new JsonDownloader();
        ConfigHistory history = new ConfigHistory();
        JsonComparator comparator = new JsonComparator();

        // 1. Завантажуємо попередні конфігурації
        Map<String, String> previousConfigs = history.loadPreviousConfigs();

        // 2. Ініціалізуємо мапу для поточних конфігурацій
        Map<String, String> currentConfigs = new HashMap<>();

        System.out.println("\n--- ПОЧАТОК АНАЛІЗУ КОНФІГІВ ---");

        // 3. Перебираємо всі URL
        for (Map.Entry<String, String> entry : ConfigUrls.getUrls().entrySet()) {
            String name = entry.getKey();
            String url = entry.getValue();

            try {
                // А. Завантажуємо поточний JSON
                String currentJson = downloader.downloadJson(url);

                // Б. Зберігаємо його для наступного запуску
                currentConfigs.put(name, currentJson);

                // В. Порівнюємо з попереднім
                String previousJson = previousConfigs.get(name);
                comparator.compare(name, previousJson, currentJson);

            } catch (IOException e) {
                System.err.println("\n[ERROR] Не вдалося завантажити " + name + " з " + url + ": " + e.getMessage());
            }
        }

        System.out.println("\n--- АНАЛІЗ ЗАВЕРШЕНО ---");

        // 4. Зберігаємо поточні конфігурації як нову історію
        if (!currentConfigs.isEmpty()) {
            history.saveCurrentConfigs(currentConfigs);
        }
    }
}