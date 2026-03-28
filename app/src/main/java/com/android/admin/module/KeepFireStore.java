package com.android.admin.module;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class KeepFireStore {
    private static final String KEY_KEEP_FIRE_LIST = "keep_fire_list_json";

    private final SharedPreferences prefs;

    public KeepFireStore(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public void add(String nickname, String uid, String note) {
        if (prefs == null) {
            return;
        }
        try {
            JSONArray arr = new JSONArray(prefs.getString(KEY_KEEP_FIRE_LIST, "[]"));
            JSONObject item = new JSONObject();
            item.put("nickname", safe(nickname, "未知用户"));
            item.put("uid", safe(uid, "unknown"));
            item.put("note", safe(note, ""));
            item.put("createdAt", now());
            item.put("lastActiveAt", now());
            item.put("days", 0);
            arr.put(item);
            prefs.edit().putString(KEY_KEEP_FIRE_LIST, arr.toString()).apply();
        } catch (Throwable ignored) {
        }
    }

    public JSONArray list() {
        if (prefs == null) {
            return new JSONArray();
        }
        try {
            return new JSONArray(prefs.getString(KEY_KEEP_FIRE_LIST, "[]"));
        } catch (Throwable ignored) {
            return new JSONArray();
        }
    }

    public void touch(int index) {
        if (prefs == null) {
            return;
        }
        try {
            JSONArray arr = list();
            if (index < 0 || index >= arr.length()) {
                return;
            }
            JSONObject item = arr.getJSONObject(index);
            item.put("lastActiveAt", now());
            item.put("days", item.optInt("days", 0) + 1);
            prefs.edit().putString(KEY_KEEP_FIRE_LIST, arr.toString()).apply();
        } catch (Throwable ignored) {
        }
    }

    public void clear() {
        if (prefs != null) {
            prefs.edit().putString(KEY_KEEP_FIRE_LIST, "[]").apply();
        }
    }

    private String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
    }

    private String safe(String text, String fallback) {
        if (text == null || text.trim().isEmpty()) {
            return fallback;
        }
        return text.trim();
    }
}
