package app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.flipkart.zjsonpatch.JsonDiff;
import com.flipkart.zjsonpatch.JsonPatch;
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

        System.out.println("\n[>>>] Конфіг " + name + ": Виявлено зміни (JSON-Patch Google):");

        try {
            JsonNode prevNode = mapper.readTree(previousJson);
            JsonNode currNode = mapper.readTree(currentJson);

            // ГЕНЕРАЦІЯ DIFF: використовуємо JsonDiff.asJson() від Google
            JsonNode diffArray = JsonDiff.asJson(prevNode, currNode);

            if (diffArray.isArray() && diffArray.size() > 0) {
                System.out.printf("--- ЗМІНИ У КОНФІГУ %s ---\n", name);

                // Проходимо по кожній операції patch
                for (JsonNode operation : diffArray) {
                    String op = operation.get("op").asText();
                    String path = operation.get("path").asText();

                    String valueInfo = "";
                    if (operation.has("value")) {
                        valueInfo = " Нове значення: " + operation.get("value").toString();
                    }
                    if (operation.has("from")) {
                        valueInfo += " (Зі шляху: " + operation.get("from").asText() + ")";
                    }

                    // Виведення у форматі, схожому на Git
                    switch (op) {
                        case "add":
                            System.out.printf("  + ДОДАНО:   Шлях: %s,%s\n", path, valueInfo);
                            break;
                        case "remove":
                            System.out.printf("  - ВИДАЛЕНО: Шлях: %s\n", path);
                            break;
                        case "replace":
                            System.out.printf("  ~ ЗМІНЕНО:  Шлях: %s,%s\n", path, valueInfo);
                            break;
                        case "move":
                            System.out.printf("  < ПЕРЕМІЩЕНО: Шлях: %s%s\n", path, valueInfo);
                            break;
                        default:
                            System.out.printf("  ? ІНШЕ:    %s, Шлях: %s\n", op, path);
                    }
                }
                System.out.println("------------------------------------");
            } else {
                System.out.println("[Помилка Diff] Зміни виявлено, але JsonDiff не знайшов операцій.");
            }

        } catch (JsonProcessingException e) {
            System.err.println("Помилка обробки JSON для " + name + ": " + e.getMessage());
        }
    }
}