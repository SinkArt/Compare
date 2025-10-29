package app;

public class Main {
    public static void main(String[] args) {

        JsonComparator comparator = new JsonComparator();
        String configName = "settings.json";

        // Стара версія конфігу (Previous)
        String previous = "{\"server\": {\"port\": 8080, \"timeout\": 5000, \"users\": [{\"id\": 1, \"active\": true}, {\"id\": 2, \"active\": false}]}}";

        // Нова версія конфігу (Current)
        // Зміни:
        // 1. 'port' змінено: 8080 -> 8081 (replace)
        // 2. 'timeout' видалено (remove)
        // 3. 'new_key' додано (add)
        // 4. Елементи масиву 'users' переміщено (move, if IDs are used, but zjsonpatch handles index change)
        String current = "{\"server\": {\"port\": 8081, \"new_key\": \"value\", \"users\": [{\"id\": 2, \"active\": false}, {\"id\": 1, \"active\": true}]}}";

        comparator.compare(configName, previous, current);
    }
}