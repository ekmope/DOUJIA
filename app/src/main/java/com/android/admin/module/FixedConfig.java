package com.android.admin.module;

final class FixedConfig {
    static final String TOP_HIDE_LABELS = "推荐,关注,直播,同城,商城,团购,热点,经验,精选";
    static final String BOTTOM_HIDE_LABELS = "朋友,发布";
    static final String VIDEO_FILTER_KEYWORDS = "广告\n推广";

    private FixedConfig() {
    }

    static boolean getBoolean(String key, boolean defaultValue) {
        if (ModulePrefs.KEY_ENABLE_MODULE.equals(key)) {
            return true;
        }
        if (ModulePrefs.KEY_TOP_MENU.equals(key)) {
            return true;
        }
        if (ModulePrefs.KEY_BOTTOM_MENU.equals(key)) {
            return true;
        }
        if (ModulePrefs.KEY_VIDEO_FILTER_ENABLE.equals(key)) {
            return true;
        }
        return defaultValue;
    }

    static String getString(String key, String defaultValue) {
        if (ModulePrefs.KEY_TOP_HIDE_LABELS.equals(key)) {
            return TOP_HIDE_LABELS;
        }
        if (ModulePrefs.KEY_BOTTOM_HIDE_LABELS.equals(key)) {
            return BOTTOM_HIDE_LABELS;
        }
        if (ModulePrefs.KEY_VIDEO_FILTER_KEYWORDS.equals(key)) {
            return VIDEO_FILTER_KEYWORDS;
        }
        return defaultValue;
    }
}