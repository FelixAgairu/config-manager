package dev.felixagairu.configmanager.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class Getter {
    private Getter() {}

    public static JsonElement deepGet(JsonElement element, String targetKey) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();

            // Direct hit
            if (obj.has(targetKey)) {
                return obj.get(targetKey);
            }

            // Search children
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                JsonElement found = deepGet(entry.getValue(), targetKey);
                if (found != null) {
                    return found;
                }
            }
        }

        else if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                JsonElement found = deepGet(child, targetKey);
                if (found != null) {
                    return found;
                }
            }
        }

        return null; // not found anywhere
    }
}
