package com.android.admin.module;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class WardStore {
    private static final String KEY_WARD_LIST = "ward_list_json";

    private final SharedPreferences prefs;

    public WardStore(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public void add(String title, String content, String url) {
        if (prefs == null) {
            return;
        }
        try {
            JSONArray arr = new JSONArray(prefs.getString(KEY_WARD_LIST, "[]"));
            JSONObject item = new JSONObject();
            item.put("title", safe(title, "未命名"));
            item.put("content", safe(content, ""));
            item.put("url", safe(url, ""));
            item.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date()));
            arr.put(item);
            prefs.edit().putString(KEY_WARD_LIST, arr.toString()).apply();
        } catch (Throwable ignored) {
        }
    }

    public JSONArray list() {
        if (prefs == null) {
            return new JSONArray();
        }
        try {
            return new JSONArray(prefs.getString(KEY_WARD_LIST, "[]"));
        } catch (Throwable ignored) {
            return new JSONArray();
        }
    }

    public void clear() {
        if (prefs != null) {
            prefs.edit().putString(KEY_WARD_LIST, "[]").apply();
        }
    }

    private String safe(String text, String fallback) {
        if (text == null || text.trim().isEmpty()) {
            return fallback;
        }
        return text.trim();
    }
}
