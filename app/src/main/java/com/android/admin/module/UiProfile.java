package com.android.admin.module;

import java.util.Set;

final class UiProfile {
    final String name;
    final int maxVersionCode;
    final Set<String> topTokens;
    final Set<String> bottomTokens;
    final Set<String> rightTokens;
    final Set<String> musicTokens;
    final Set<String> textTokens;
    final Set<String> excludeTokens;

    UiProfile(
            String name,
            int maxVersionCode,
            Set<String> topTokens,
            Set<String> bottomTokens,
            Set<String> rightTokens,
            Set<String> musicTokens,
            Set<String> textTokens,
            Set<String> excludeTokens
    ) {
        this.name = name;
        this.maxVersionCode = maxVersionCode;
        this.topTokens = topTokens;
        this.bottomTokens = bottomTokens;
        this.rightTokens = rightTokens;
        this.musicTokens = musicTokens;
        this.textTokens = textTokens;
        this.excludeTokens = excludeTokens;
    }
}
