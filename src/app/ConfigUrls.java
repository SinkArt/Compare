package app;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ConfigUrls {

    // Всі URL-адреси в одному місці для легкого керування
    private static final Map<String, String> URL_MAP;

    static {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("BASE_CONFIG", "https://ua-mob-native-uat.playtechgaming.com/UAT/WhiteLabel/25.3/configs/stable/_shared_configs/base/minor_configs_version/base.json");
        map.put("BRAND_CONFIG", "https://ua-mob-native-uat.playtechgaming.com/UAT/WhiteLabel/25.3/configs/stable/brand_configs/minor_configs_version/brand.json");
        map.put("ENV_CONFIG", "https://ua-mob-native-uat.playtechgaming.com/UAT/WhiteLabel/25.3/configs/stable/brand_configs/minor_configs_version/horizon.json");
        map.put("CONTENT_JSON", "https://ua-mob-native-uat.playtechgaming.com/UAT/WhiteLabel/25.3/configs/stable/brand_configs/minor_configs_version/content.json");
        map.put("SHARED_TRANSLATIONS", "https://ua-mob-native-uat.playtechgaming.com/UAT/WhiteLabel/25.3/configs/stable/_shared_configs/translations/en.json");
        map.put("BRANDED_TRANSLATIONS", "https://ua-mob-native-uat.playtechgaming.com/UAT/WhiteLabel/25.3/configs/stable/brand_configs/minor_configs_version/translations/en.json");

        // Перетворюємо на незмінну мапу
        URL_MAP = Collections.unmodifiableMap(map);
    }

    public static Map<String, String> getUrls() {
        return URL_MAP;
    }
}
