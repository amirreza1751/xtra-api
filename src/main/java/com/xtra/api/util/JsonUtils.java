package com.xtra.api.util;

import org.json.JSONObject;

import java.util.SortedSet;
import java.util.TreeSet;

public class JsonUtils {
    public static JSONObject sortJson(JSONObject jsonObject) {
        var result = new JSONObject();
        var map = jsonObject.toMap();
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        for (String key : keys) {
            result.put(key, jsonObject.get(key));
        }
        return result;
    }
}

