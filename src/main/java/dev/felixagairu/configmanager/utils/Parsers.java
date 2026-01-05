package dev.felixagairu.configmanager.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Locale;

public final class Parsers {
    private Parsers() {}

    public static boolean parseBooleanSafe(JsonElement element) {
        if (element == null || element.isJsonNull()) return false;
        if (!element.isJsonPrimitive()) return false;

        JsonPrimitive p = element.getAsJsonPrimitive();

        if (p.isBoolean()) {
            return p.getAsBoolean();
        }

        if (p.isNumber()) {
            return p.getAsInt() != 0;
        }

        if (p.isString()) {
            String s = p.getAsString().trim().toLowerCase(Locale.ROOT);
            return s.equals("true") || s.equals("1") || s.equals("yes") || s.equals("on");
        }

        return false;
    }

    public static int parseIntegerSafe(JsonElement element) {
        if (element == null || element.isJsonNull()) return 0;
        if (!element.isJsonPrimitive()) return 0;

        JsonPrimitive p = element.getAsJsonPrimitive();

        if (p.isNumber()) {
            return p.getAsInt();
        }

        if (p.isString()) {
            try {
                return Integer.parseInt(p.getAsString().trim());
            } catch (NumberFormatException ignored) {}
        }

        return 0;
    }

    public static float parseFloatSafe(JsonElement element) {
        if (element == null || element.isJsonNull()) return 0f;
        if (!element.isJsonPrimitive()) return 0f;

        JsonPrimitive p = element.getAsJsonPrimitive();

        if (p.isNumber()) {
            return p.getAsFloat();
        }

        if (p.isString()) {
            try {
                return Float.parseFloat(p.getAsString().trim());
            } catch (NumberFormatException ignored) {}
        }

        return 0f;
    }

    public static String parseStringSafe(JsonElement element) {
        if (element == null || element.isJsonNull()) return "";
        if (!element.isJsonPrimitive()) return "";

        try {
            return element.getAsString();
        } catch (Exception ignored) {
            return "";
        }
    }
}
