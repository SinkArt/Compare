package app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Iterator;
import java.util.Map;

public class JsonComparator {

    private final ObjectMapper mapper = new ObjectMapper();

    public void compare(String name, String previousJson, String currentJson) {
        if (previousJson == null || previousJson.isEmpty()) {
            System.out.println("\n[!] Конфіг " + name + ": Немає попередньої історії. Невможливо порівняти.");
            return;
        }

        if (previousJson.equals(currentJson)) {
            System.out.println("\n[OK] Конфіг " + name + ": Без структурних змін.");
            return;
        }

        System.out.println("\n[>>>] Конфіг " + name + ": Виявлено СТРУКТУРНІ зміни (Додано/Видалено):");

        try {
            JsonNode prevNode = mapper.readTree(previousJson);
            JsonNode currNode = mapper.readTree(currentJson);

            // Виклик рекурсивного методу для виведення структурних змін
            findStructureChanges(prevNode, currNode, name, "");

        } catch (JsonProcessingException e) {
            System.err.println("Помилка обробки JSON для " + name + ": " + e.getMessage());
        }
    }

    /**
     * Рекурсивно порівнює два JsonNode і виводить ТІЛЬКИ ДОДАНІ або ВИДАЛЕНІ елементи/ключі.
     * @param oldNode Попередній вузол.
     * @param newNode Поточний вузол.
     * @param configName Назва конфігу.
     * @param path Поточний шлях до вузла.
     */
    private void findStructureChanges(JsonNode oldNode, JsonNode newNode, String configName, String path) {

        // --- 1. ПОРІВНЯННЯ ОБ'ЄКТІВ (КЛЮЧІ) ---
        if (oldNode.isObject() && newNode.isObject()) {

            // Перевірка на ВИДАЛЕННЯ (Ключі є у старому, але відсутні у новому)
            Iterator<Map.Entry<String, JsonNode>> oldFields = oldNode.fields();
            while (oldFields.hasNext()) {
                String key = oldFields.next().getKey();
                if (!newNode.has(key)) {
                    String newPath = path + "." + key;
                    System.out.printf("  - ВИДАЛЕНО: [%s] Шлях: %s\n", configName, newPath);
                }
            }

            // Перевірка на ДОДАВАННЯ (Ключі є у новому, але відсутні у старому)
            Iterator<Map.Entry<String, JsonNode>> newFields = newNode.fields();
            while (newFields.hasNext()) {
                Map.Entry<String, JsonNode> field = newFields.next();
                String key = field.getKey();
                String newPath = path + "." + key;

                if (!oldNode.has(key)) {
                    // Це новий елемент. Виводимо його і не йдемо далі рекурсивно.
                    System.out.printf("  + ДОДАНО:   [%s] Шлях: %s, Значення: %s\n", configName, newPath, field.getValue().toString());
                } else {
                    // Ключ існує в обох: рекурсивно перевіряємо, чи не було структурних змін всередині
                    findStructureChanges(oldNode.get(key), newNode.get(key), configName, newPath);
                }
            }

            // --- 2. ПОРІВНЯННЯ МАСИВІВ (РОЗМІР) ---
        } else if (oldNode.isArray() && newNode.isArray()) {

            // Видалення елементів масиву (новий масив менший)
            if (oldNode.size() > newNode.size()) {
                System.out.printf("  - ВИДАЛЕНО: [%s] Масив за шляхом %s втратив %d елементів (%d -> %d)\n",
                        configName, path, oldNode.size() - newNode.size(), oldNode.size(), newNode.size());
            }

            // Додавання елементів масиву (новий масив більший)
            if (newNode.size() > oldNode.size()) {
                // Виводимо нові елементи, що виходять за межі старого масиву
                for (int i = oldNode.size(); i < newNode.size(); i++) {
                    System.out.printf("  + ДОДАНО:   [%s] Шлях: %s[%d], Значення: %s\n",
                            configName, path, i, newNode.get(i).toString());
                }
            }

            // Рекурсивно порівнюємо спільні елементи масиву,
            // якщо вони є об'єктами, щоб знайти структурні зміни всередині них.
            int minSize = Math.min(oldNode.size(), newNode.size());
            for (int i = 0; i < minSize; i++) {
                findStructureChanges(oldNode.get(i), newNode.get(i), configName, path + "[" + i + "]");
            }

            // Примітивні типи або зміна значень ігноруються, оскільки це не структурна зміна.
        }
        // Також ігноруємо випадок, коли змінився ТИП вузла (наприклад, з об'єкта на рядок).
    }
}