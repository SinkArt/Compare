package app;

import com.flipkart.zjsonpatch.JsonDiff;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;
// Імпорт нової бібліотеки

public class JsonComparator {

    private final ObjectMapper mapper = new ObjectMapper();

    public void compare(String name, String previousJson, String currentJson) {
        if (previousJson == null || previousJson.isEmpty()) {
            System.out.println("\n[!] Конфіг " + name + ": Немає попередньої історії. Неможливо порівняти.");
            return;
        }

        if (previousJson.equals(currentJson)) {
            System.out.println("\n[OK] Конфіг " + name + ": Без змін.");
            return;
        }

        System.out.println("\n[>>>] Конфіг " + name + ": Виявлено зміни (JSON-Patch):");

        try {
            // Перетворюємо JSON-рядки на JsonNode
            JsonNode prevNode = mapper.readTree(previousJson);
            JsonNode currNode = mapper.readTree(currentJson);

            // ГЕНЕРАЦІЯ DIFF: Використовуємо JsonDiff для отримання масиву операцій
            JsonNode diff = JsonDiff.asJson(prevNode, currNode);

            if (diff.isArray() && diff.size() > 0) {
                System.out.printf("--- ЗМІНИ У КОНФІГУ %s ---\n", name);

                // Проходимо по кожній операції patch
                for (JsonNode operation : diff) {
                    String op = operation.get("op").asText(); // 'add', 'remove', 'replace', 'move'
                    String path = operation.get("path").asText(); // Шлях до зміненого елемента

                    String valueInfo = "";
                    if (operation.has("value")) {
                        valueInfo = " Значення: " + operation.get("value").toString();
                    }
                    if (operation.has("from")) {
                        // Це трапляється при 'move' (переміщенні)
                        valueInfo += " (З: " + operation.get("from").asText() + ")";
                    }

                    // Виведення у форматі, схожому на Git
                    switch (op) {
                        case "add":
                            System.out.printf("  + ДОДАНО:   Шлях: %s,%s\n", path, valueInfo);
                            break;
                        case "remove":
                            // Для видалення значення часто відсутнє, але path завжди є
                            System.out.printf("  - ВИДАЛЕНО: Шлях: %s\n", path);
                            break;
                        case "replace":
                            // 'replace' означає зміну значення
                            System.out.printf("  ~ ЗМІНЕНО:  Шлях: %s, Нове%s\n", path, valueInfo);
                            break;
                        case "move":
                            // 'move' означає переміщення елемента масиву, що тепер коректно обробляється!
                            System.out.printf("  < ПЕРЕМІЩЕНО: Шлях: %s, Зі шляху: %s\n", path, operation.get("from").asText());
                            break;
                        default:
                            System.out.printf("  ? ІНШЕ:    %s, Шлях: %s\n", op, path);
                    }
                }
                System.out.println("------------------------------------");
            } else {
                System.out.println("[OK] Зміни виявлено, але JsonDiff не знайшов коректних операцій.");
            }

        } catch (JsonProcessingException e) {
            System.err.println("Помилка обробки JSON для " + name + ": " + e.getMessage());
        }
    }
}