package com.android.admin.module;

import android.R;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Rect;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import io.github.libxposed.api.XposedInterface;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: DouyinMigratedHooks.class */
public final class DouyinMigratedHooks {
    private static final String TAG = "DouPlusApi101";
    private static final String DOUYIN_MAIN_ACTIVITY = "com.ss.android.ugc.aweme.main.MainActivity";
    private static final String AWEME_INTRO_INFO_LAYOUT_CLASS = "com.ss.android.ugc.aweme.feed.ui.AwemeIntroInfoLayout";
    private static final String FEED_RIGHT_SCALE_VIEW_CLASS = "com.ss.android.ugc.aweme.feed.ui.FeedRightScaleView";
    private static final String DIGG_LAYOUT_CLASS = "com.ss.android.ugc.aweme.common.widget.DiggLayout";
    private static final String MAIN_TITLE_BAR_CONTAINER_CLASS = "com.ss.android.ugc.aweme.homepage.ui.titlebar.MainTitleBarContainer";
    private static final String MAIN_TITLE_BAR_CLASS = "com.ss.android.ugc.aweme.homepage.ui.titlebar.MainTitleBar";
    private static final String MAIN_BOTTOM_TAB_CONTAINER_CLASS = "com.ss.android.ugc.aweme.homepage.ui.bottombar.MainBottomTabContainer";
    private static final String BOTTOM_TAB_CLASS = "com.ss.android.ugc.aweme.homepage.tab.ui.bottom.BottomTab";
    private static final String PUBLISH_BUTTON_CLASS = "com.ss.android.ugc.aweme.tools.external.hometab.bottom.PublishButton";
    private static final String TOP_TAB_TEXT_CLASS = "com.ss.android.ugc.aweme.homepage.antiburn.ui.AntiBurnDuxTextView";
    private static final String TOP_TAB_TEXT_ID = "42_";
    private static final String TOP_MENU_CONTAINER_ID = "qrj";
    private static final String TOP_MENU_PARENT_ID = "t3s";
    private static final String TOP_TAB_ITEM_CLASS = "X.1H5B";
    private static final String TOP_TAB_LAYOUT_ID = "xpm";
    private static final String TOP_RETURN_RECOMMEND_ID = "5_i";
    private static final String BOTTOM_MENU_PARENT_ID = "ckc";
    private static final String BOTTOM_MENU_LAYOUT_ID = "root_view";
    private static final String MUSIC_FPS_CLASS = "com.bytedance.highperformanceview.fps.FpsControlFrameLayout";
    private static final String MUSIC_LAYOUT_COMPAT_CLASS = "androidx.appcompat.widget.LinearLayoutCompat";
    private static final String MUSIC_FPS_ID = "57=";
    private static final String MUSIC_LAYOUT_ID = "cbw";
    private static final String LOG_FILE_NAME = "resource-id-log.txt";
    private static final Set<String> FIXED_TOP_HIDE_LABELS = Collections.unmodifiableSet(tokenSet("recommend", "follow", "live", "samecity", "mall", "groupbuy", "hot", "experience", "featured"));
    private static final Set<String> FIXED_BOTTOM_HIDE_LABELS = Collections.unmodifiableSet(tokenSet("friends", "publish"));
    private static final int ID_BOTTOM_LEFT_DOUBLE = 1107886193;
    private static final int ID_BOTTOM_LEFT_DOUBLE_TEXT = 1107886194;
    private static final int ID_BOTTOM_LEFT_LONG = 1107886195;
    private static final int ID_BOTTOM_LEFT_LONG_TEXT = 1107886196;
    private static final int ID_MUSIC_VIEW = 1107886587;
    private static final int ID_TEXT_COMMENT_COUNT = 1107886813;
    private static final int ID_TEXT_FAVORITE_COUNT = 1107886814;
    private static final int ID_TEXT_LIKE_COUNT = 1107886816;
    private static final int ID_TEXT_SHARE_COUNT = 1107886820;
    private static final int ID_VIDEO_TEXT = 1107886951;
    private final XposedInterface xposed;
    private volatile SharedPreferences prefs;
    private final ClassLoader targetClassLoader;
    private volatile long lastPrefsRefreshAt;
    private volatile String lastPrefsSnapshot;
    private volatile boolean probeShown;
    private final WeakHashMap<View, Boolean> hiddenTopViews = new WeakHashMap<>();
    private final WeakHashMap<View, Boolean> hiddenBottomViews = new WeakHashMap<>();
    private final WeakHashMap<Activity, UiProfile> activityProfiles = new WeakHashMap<>();
    private final WeakHashMap<Activity, Boolean> profileLogged = new WeakHashMap<>();
    private final WeakHashMap<Activity, Boolean> runtimeDumpScheduled = new WeakHashMap<>();
    private final WeakHashMap<Activity, Boolean> uiApplyLogged = new WeakHashMap<>();
    private final WeakHashMap<Activity, Boolean> uiSkipLogged = new WeakHashMap<>();
    private final WeakHashMap<View, Boolean> hookedIntroViews = new WeakHashMap<>();
    private final WeakHashMap<View, Boolean> hookedRightViews = new WeakHashMap<>();
    private final WeakHashMap<View, Boolean> hookedDiggViews = new WeakHashMap<>();
    private final WeakHashMap<View, Boolean> hookedTitleViews = new WeakHashMap<>();
    private final WeakHashMap<View, Boolean> hookedBottomViews = new WeakHashMap<>();
    private final HashMap<String, Integer> resolvedIdCache = new HashMap<>();
    private final Object fileLogLock = new Object();
    private static final Pattern DURATION_PATTERN = Pattern.compile("^(\\d{1,2}:\\d{2}(?::\\d{2})?)$");
    private static final UiProfile LEGACY_PROFILE = new UiProfile("legacy", 319999, tokenSet("status_bar", "statusview", "toolbar", "titlebar", "top_bar", "topbar", "appbar", "search_bar", "searchbar", "title_bar"), tokenSet("bottom", "tab", "nav", "navigation", "dock", "tabbar", "bottombar", "homepage_bottom", "main_bottom"), tokenSet("digg", "like", "comment", "share", "collect", "favorite", "right_container", "right_btn", "rightcontrol", "actionbar", "video_control", "sidebar", "avatar"), tokenSet("music", "song", "album", "disc", "record", "rotation", "cd", "audio"), tokenSet("title", "desc", "caption", "subtitle", "author", "nickname", "bottom_left", "left_bottom", "video_text", "feed_desc"), tokenSet("comment_panel", "comment_list", "comment_container", "comment_root", "reply", "input", "edit", "danmaku", "barrage", "bullet", "live_chat", "chat_room"));
    private static final UiProfile MODERN_PROFILE = new UiProfile("modern", Integer.MAX_VALUE, tokenSet("status_bar", "top_bar", "topbar", "title_bar", "discovery_top", "search_bar", "searchbar", "channel_tab"), tokenSet("bottom", "tab", "tabbar", "main_bottom", "navigation", "homepage_bottom", "bottom_nav", "bottom_bar"), tokenSet("digg", "like", "comment", "share", "collect", "favorite", "right_btn", "right_action", "actionbar", "sidebar", "avatar", "social"), tokenSet("music", "song", "album", "disc", "record", "rotation", "music_cover", "music_layout", "audio"), tokenSet("title", "desc", "caption", "subtitle", "author", "nickname", "video_text", "feed_desc", "bottom_left", "left_bottom"), tokenSet("comment_panel", "comment_list", "comment_container", "comment_root", "reply", "input", "edit", "danmaku", "barrage", "bullet", "live_chat", "chat_room"));

    public DouyinMigratedHooks(XposedInterface xposed, SharedPreferences prefs, ClassLoader targetClassLoader) {
        this.xposed = xposed;
        this.prefs = prefs;
        this.targetClassLoader = targetClassLoader;
    }

    public void install() {
        installProbe();
        installVideoFilter();
        installUiApplier();
        installRealClassHooks();
    }

    private void installProbe() {
        try {
            Method onResume = Activity.class.getDeclaredMethod("onResume", new Class[0]);
            this.xposed.hook(onResume).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object result = chain.proceed();
                Object patt0$temp = chain.getThisObject();
                if (patt0$temp instanceof Activity) {
                    Activity a = (Activity) patt0$temp;
                    this.probeShown = true;
                    try {
                        applyGeneralUiFeatures(a);
                    } catch (Throwable t) {
                        this.xposed.log(5, TAG, "installProbe applyGeneralUiFeatures failed", t);
                    }
                }
                return result;
            });
        } catch (Throwable t) {
            this.xposed.log(5, TAG, "installProbe failed", t);
        }
    }

    private void installVideoFilter() {
        if (getBool("video_filter_enable", false)) {
            try {
                Method setText = TextView.class.getDeclaredMethod("setText", CharSequence.class);
                this.xposed.hook(setText).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object result = chain.proceed();
                    Object patt0$temp = chain.getThisObject();
                    if (!(patt0$temp instanceof TextView)) {
                        return result;
                    }
                    TextView tv = (TextView) patt0$temp;
                    List<Object> args = chain.getArgs();
                    if (!args.isEmpty()) {
                        Object patt1$temp = args.get(0);
                        if (patt1$temp instanceof CharSequence) {
                            CharSequence cs = (CharSequence) patt1$temp;
                            String text = cs.toString().trim();
                            if (!text.isEmpty()) {
                                if (matchesVideoFilter(text)) {
                                    hideNearestContainer(tv, "keyword", text);
                                } else if (matchesLiveStreamFilter(tv, text)) {
                                    hideNearestContainer(tv, "live", text);
                                } else if (matchesLowLikeCandidate(tv, text)) {
                                    confirmAndHideLowLike(tv, text);
                                }
                            }
                            return result;
                        }
                    }
                    return result;
                });
            } catch (Throwable t) {
                this.xposed.log(5, TAG, "installVideoFilter failed", t);
            }
        }
    }
    private void installUiApplier() {
        try {
            Method onPostResume = Activity.class.getDeclaredMethod("onPostResume", new Class[0]);
            this.xposed.hook(onPostResume).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object result = chain.proceed();
                Object patt0$temp = chain.getThisObject();
                if (patt0$temp instanceof Activity) {
                    Activity a = (Activity) patt0$temp;
                    applyGeneralUiFeatures(a);
                }
                return result;
            });
        } catch (Throwable t) {
            this.xposed.log(5, TAG, "installUiApplier failed", t);
        }
        try {
            Method onWindowFocusChanged = Activity.class.getDeclaredMethod("onWindowFocusChanged", Boolean.TYPE);
            this.xposed.hook(onWindowFocusChanged).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain2 -> {
                Object result = chain2.proceed();
                Object patt0$temp = chain2.getThisObject();
                if (patt0$temp instanceof Activity) {
                    Activity a = (Activity) patt0$temp;
                    List<Object> args = chain2.getArgs();
                    if (!args.isEmpty()) {
                        Object patt1$temp = args.get(0);
                        if (patt1$temp instanceof Boolean) {
                            Boolean b = (Boolean) patt1$temp;
                            boolean z = b.booleanValue();
                            boolean hasFocus = z;
                            if (hasFocus) {
                                applyGeneralUiFeatures(a);
                            }
                        }
                    }
                }
                return result;
            });
        } catch (Throwable t2) {
            this.xposed.log(5, TAG, "installUiApplier window-focus hook failed", t2);
        }
        try {
            Method callActivityOnResume = Instrumentation.class.getDeclaredMethod("callActivityOnResume", Activity.class);
            this.xposed.hook(callActivityOnResume).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain3 -> {
                Object result = chain3.proceed();
                List<Object> args = chain3.getArgs();
                if (!args.isEmpty()) {
                    Object patt0$temp = args.get(0);
                    if (patt0$temp instanceof Activity) {
                        Activity a = (Activity) patt0$temp;
                        applyGeneralUiFeatures(a);
                    }
                }
                return result;
            });
        } catch (Throwable t3) {
            this.xposed.log(5, TAG, "installUiApplier instrumentation-resume hook failed", t3);
        }
        try {
            Method callActivityOnPostResume = Instrumentation.class.getDeclaredMethod("callActivityOnPostResume", Activity.class);
            this.xposed.hook(callActivityOnPostResume).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain4 -> {
                Object result = chain4.proceed();
                List<Object> args = chain4.getArgs();
                if (!args.isEmpty()) {
                    Object patt0$temp = args.get(0);
                    if (patt0$temp instanceof Activity) {
                        Activity a = (Activity) patt0$temp;
                        applyGeneralUiFeatures(a);
                    }
                }
                return result;
            });
        } catch (Throwable t4) {
            if (!(t4 instanceof NoSuchMethodException)) {
                this.xposed.log(5, TAG, "installUiApplier instrumentation-post-resume hook failed", t4);
            }
        }
    }

    private void applyGeneralUiFeatures(Activity a) {
        if (isTargetActivity(a)) {
            refreshPrefsIfNeeded(true);
            maybeLogUiApply(a, "enter");
            ViewGroup root = (ViewGroup) a.findViewById(R.id.content);
            if (root == null) {
                maybeLogUiSkip(a, "root_null");
                return;
            }
            UiProfile profile = resolveProfile(a);
            if (profile == null) {
                maybeLogUiSkip(a, "profile_null");
                return;
            }
            maybeLogUiApply(a, "ready");
            maybeLogProfile(a, profile);
            if (getBool("status_bar_transparent", true)) {
                applyStatusBarTransparent(a);
            }
        }
    }

    private void installRealClassHooks() {
        installGlobalTargetLayoutHook();
        hookViewClass(AWEME_INTRO_INFO_LAYOUT_CLASS, this.hookedIntroViews, this::applyIntroInfoHook);
        hookViewClass(FEED_RIGHT_SCALE_VIEW_CLASS, this.hookedRightViews, this::applyFeedRightHook);
        hookViewClass(DIGG_LAYOUT_CLASS, this.hookedDiggViews, this::applyDiggLayoutHook);
        hookViewClass(MAIN_TITLE_BAR_CONTAINER_CLASS, this.hookedTitleViews, this::applyMainTitleBarHook);
        hookViewClass(MAIN_TITLE_BAR_CLASS, this.hookedTitleViews, this::applyMainTitleBarHook);
        hookViewClass(MAIN_BOTTOM_TAB_CONTAINER_CLASS, this.hookedBottomViews, this::applyMainBottomTabHook);
        hookViewClass(BOTTOM_TAB_CLASS, this.hookedBottomViews, this::applyBottomTabDirectHook);
        hookViewClass(PUBLISH_BUTTON_CLASS, this.hookedBottomViews, this::applyPublishButtonDirectHook);
        hookViewClass(TOP_TAB_TEXT_CLASS, this.hookedTitleViews, this::applyTopTabTextDirectHook);
    }

    private void installGlobalTargetLayoutHook() {
        try {
            Method layout = View.class.getDeclaredMethod("layout", Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            this.xposed.hook(layout).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                Object result = chain.proceed();
                Object patt0$temp = chain.getThisObject();
                if (patt0$temp instanceof View) {
                    View view = (View) patt0$temp;
                    dispatchRealTargetHook(view, "View#layout");
                }
                return result;
            });
        } catch (Throwable t) {
            this.xposed.log(5, TAG, "installGlobalTargetLayoutHook failed", t);
        }
    }

    private void dispatchRealTargetHook(View view, String phase) {
        String className = view.getClass().getName();
        if (AWEME_INTRO_INFO_LAYOUT_CLASS.equals(className)) {
            runRealInstaller(view, this.hookedIntroViews, this::applyIntroInfoHook, phase);
            return;
        }
        if (FEED_RIGHT_SCALE_VIEW_CLASS.equals(className)) {
            runRealInstaller(view, this.hookedRightViews, this::applyFeedRightHook, phase);
            return;
        }
        if (DIGG_LAYOUT_CLASS.equals(className)) {
            runRealInstaller(view, this.hookedDiggViews, this::applyDiggLayoutHook, phase);
            return;
        }
        if (BOTTOM_TAB_CLASS.equals(className)) {
            runRealInstaller(view, this.hookedBottomViews, this::applyBottomTabDirectHook, phase);
            return;
        }
        if (PUBLISH_BUTTON_CLASS.equals(className)) {
            runRealInstaller(view, this.hookedBottomViews, this::applyPublishButtonDirectHook, phase);
            return;
        }
        if (TOP_TAB_TEXT_CLASS.equals(className) && TOP_TAB_TEXT_ID.equals(readResName(view))) {
            runRealInstaller(view, this.hookedTitleViews, this::applyTopTabTextDirectHook, phase);
            return;
        }
        if ((MUSIC_FPS_CLASS.equals(className) && MUSIC_FPS_ID.equals(readResName(view)))
                || (MUSIC_LAYOUT_COMPAT_CLASS.equals(className) && MUSIC_LAYOUT_ID.equals(readResName(view)))) {
            runRealInstaller(view, this.hookedIntroViews, v -> applyOpacityToRealContainer(v, "real_hook_music"), phase);
            return;
        }
        if (MAIN_TITLE_BAR_CONTAINER_CLASS.equals(className) || MAIN_TITLE_BAR_CLASS.equals(className) || TOP_MENU_CONTAINER_ID.equals(readResName(view))) {
            runRealInstaller(view, this.hookedTitleViews, this::applyMainTitleBarHook, phase);
        } else if (MAIN_BOTTOM_TAB_CONTAINER_CLASS.equals(className)) {
            runRealInstaller(view, this.hookedBottomViews, this::applyMainBottomTabHook, phase);
        }
    }

    private void hookViewClass(String className, WeakHashMap<View, Boolean> cache, ViewInstaller installer) {
        try {
            Class<?> cls = Class.forName(className, false, this.targetClassLoader);
            for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
                this.xposed.hook(constructor).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object result = chain.proceed();
                    Object patt0$temp = chain.getThisObject();
                    if (patt0$temp instanceof View) {
                        View view = (View) patt0$temp;
                        runRealInstaller(view, cache, installer, className + "#<init>");
                    }
                    return result;
                });
            }
            try {
                Method onAttachedToWindow = cls.getDeclaredMethod("onAttachedToWindow", new Class[0]);
                this.xposed.hook(onAttachedToWindow).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain2 -> {
                    Object result = chain2.proceed();
                    Object patt0$temp = chain2.getThisObject();
                    if (patt0$temp instanceof View) {
                        View view = (View) patt0$temp;
                        runRealInstaller(view, cache, installer, className + "#onAttachedToWindow");
                    }
                    return result;
                });
            } catch (NoSuchMethodException e) {
            }
            hookViewMethodByName(cls, className, cache, installer, "onLayout", 5);
            hookViewMethodByName(cls, className, cache, installer, "dispatchDraw", 1);
            this.xposed.log(4, TAG, "real_hook installed for " + className);
        } catch (Throwable t) {
            this.xposed.log(5, TAG, "real_hook install failed for " + className, t);
        }
    }

    private void hookViewMethodByName(Class<?> cls, String className, WeakHashMap<View, Boolean> cache, ViewInstaller installer, String methodName, int paramCount) {
        for (Method method : cls.getDeclaredMethods()) {
            if (method.getName().equals(methodName) && method.getParameterCount() == paramCount) {
                this.xposed.hook(method).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(chain -> {
                    Object result = chain.proceed();
                    Object patt0$temp = chain.getThisObject();
                    if (patt0$temp instanceof View) {
                        View view = (View) patt0$temp;
                        runRealInstaller(view, cache, installer, className + "#" + methodName);
                    }
                    return result;
                });
            }
        }
    }

    private void runRealInstaller(View view, WeakHashMap<View, Boolean> cache, ViewInstaller installer, String phase) {
        try {
            installer.install(view);
            Activity activity = findActivity(view.getContext());
            if (activity != null && !cache.containsKey(view)) {
                cache.put(view, Boolean.TRUE);
                appendFileLog(activity, "real_hook_hit phase=" + phase + " class=" + view.getClass().getName() + " id=" + readResName(view) + " size=" + view.getWidth() + "x" + view.getHeight());
            }
        } catch (Throwable t) {
            Activity activity2 = findActivity(view.getContext());
            if (activity2 != null) {
                appendFileLog(activity2, "real_hook_error phase=" + phase + " class=" + view.getClass().getName() + " message=" + t.getClass().getSimpleName() + ":" + t.getMessage());
            }
        }
    }

    private void applyDiggLayoutHook(View view) {
        float alpha = getBool("opacity_control", true) ? 0.35f : 1.0f;
        int width = view.getWidth() > 0 ? view.getWidth() : view.getMeasuredWidth();
        int height = view.getHeight() > 0 ? view.getHeight() : view.getMeasuredHeight();
        if (width <= 0 || height <= 0) {
            view.post(() -> {
                applyDiggLayoutHook(view);
            });
            return;
        }
        try {
            view.setAlpha(alpha);
        } catch (Throwable th) {
        }
    }

    private void applyIntroInfoHook(View view) {
        applyOpacityToRealContainer(view, "real_hook_intro");
    }

    private void applyFeedRightHook(View view) {
        applyOpacityToRealContainer(view, "real_hook_right");
    }

    private void applyOpacityToRealContainer(View view, String phase) {
        float alpha = getBool("opacity_control", true) ? 0.35f : 1.0f;
        int width = view.getWidth() > 0 ? view.getWidth() : view.getMeasuredWidth();
        int height = view.getHeight() > 0 ? view.getHeight() : view.getMeasuredHeight();
        if (width <= 0 || height <= 0) {
            view.post(() -> {
                applyOpacityToRealContainer(view, phase);
            });
            return;
        }
        try {
            view.setAlpha(alpha);
        } catch (Throwable th) {
        }
    }

    private void applyMainTitleBarHook(View view) {
        if (!getBool("top_menu", true)) {
            return;
        }
        int width = view.getWidth() > 0 ? view.getWidth() : view.getMeasuredWidth();
        int height = view.getHeight() > 0 ? view.getHeight() : view.getMeasuredHeight();
        if (width <= 0 || height <= 0) {
            view.post(() -> applyMainTitleBarHook(view));
            return;
        }
        Set<String> labels = parseLabelSet(getString("top_hide_labels", ""));
        if (labels.isEmpty()) {
            return;
        }
        applyTopMenuFilter(view.getRootView(), labels);
        hideTopReturnRecommend(view.getRootView());
    }

    private void applyMainBottomTabHook(View view) {
        if (!getBool("bottom_menu", true)) {
            return;
        }
        int width = view.getWidth() > 0 ? view.getWidth() : view.getMeasuredWidth();
        int height = view.getHeight() > 0 ? view.getHeight() : view.getMeasuredHeight();
        if (width <= 0 || height <= 0) {
            view.post(() -> applyMainBottomTabHook(view));
            return;
        }
        Set<String> labels = parseLabelSet(getString("bottom_hide_labels", ""));
        if (labels.isEmpty()) {
            return;
        }
        applyBottomMenuFilter(view.getRootView(), labels);
        recenterBottomMenu(view.getRootView());
    }

    private void applyBottomTabDirectHook(View view) {
        if (!getBool("bottom_menu", true)) {
            return;
        }
        Set<String> labels = parseLabelSet(getString("bottom_hide_labels", ""));
        if (labels.isEmpty()) {
            return;
        }
        String label = readMenuLabel(view);
        if (!matchesMenuLabel(label, labels) || this.hiddenBottomViews.containsKey(view)) {
            return;
        }
        hideMenuItem(view, this.hiddenBottomViews, "hide_bottom_real", label);
        recenterBottomMenu(view.getRootView());
    }

    private void applyPublishButtonDirectHook(View view) {
        if (!getBool("bottom_menu", true)) {
            return;
        }
        Set<String> labels = parseLabelSet(getString("bottom_hide_labels", ""));
        if (labels.isEmpty() || !labels.contains("publish") || this.hiddenBottomViews.containsKey(view)) {
            return;
        }
        hideMenuItem(view, this.hiddenBottomViews, "hide_bottom_real", "publish");
        recenterBottomMenu(view.getRootView());
    }

    private void applyTopTabTextDirectHook(View view) {
        if (!getBool("top_menu", true)) {
            return;
        }
        if (!TOP_TAB_TEXT_ID.equals(readResName(view)) || !(view instanceof TextView)) {
            return;
        }
        Set<String> labels = parseLabelSet(getString("top_hide_labels", ""));
        if (labels.isEmpty()) {
            return;
        }
        String label = readMenuLabel(view);
        if (!matchesMenuLabel(label, labels)) {
            return;
        }
        View itemView = resolveTopMenuItemView(view, view.getRootView());
        if (itemView == null || this.hiddenTopViews.containsKey(itemView)) {
            return;
        }
        hideMenuItem(itemView, this.hiddenTopViews, "hide_top_real", label);
    }

    private void applyTopMenuFilter(View root, Set<String> labels) {
        if (!(root instanceof ViewGroup)) {
            return;
        }
        View container = findFirstByResName((ViewGroup) root, TOP_TAB_LAYOUT_ID);
        if (!(container instanceof ViewGroup)) {
            container = findFirstByResName((ViewGroup) root, TOP_MENU_PARENT_ID);
        }
        if (!(container instanceof ViewGroup)) {
            return;
        }
        ViewGroup tabContainer = (ViewGroup) container;
        List<View> toHide = new ArrayList<>();
        for (int i = 0; i < tabContainer.getChildCount(); i++) {
            View child = tabContainer.getChildAt(i);
            if (child.getVisibility() != View.VISIBLE) {
                continue;
            }
            if (!TOP_TAB_ITEM_CLASS.equals(child.getClass().getName())) {
                continue;
            }
            String label = readTopCandidateLabel(child, i);
            if (!matchesMenuLabel(label, labels) || this.hiddenTopViews.containsKey(child)) {
                continue;
            }
            toHide.add(child);
        }
        for (View child : toHide) {
            String label = readTopCandidateLabel(child, findChildIndex(tabContainer, child));
            hideMenuItem(child, this.hiddenTopViews, "hide_top_real", label);
        }
    }

    private void applyBottomMenuFilter(View root, Set<String> labels) {
        if (!(root instanceof ViewGroup)) {
            return;
        }
        View container = findFirstByResName((ViewGroup) root, BOTTOM_MENU_LAYOUT_ID);
        if (!(container instanceof ViewGroup)) {
            container = findFirstByResName((ViewGroup) root, BOTTOM_MENU_PARENT_ID);
        }
        if (!(container instanceof ViewGroup)) {
            return;
        }
        ViewGroup bottomContainer = (ViewGroup) container;
        List<View> toHide = new ArrayList<>();
        for (int i = 0; i < bottomContainer.getChildCount(); i++) {
            View child = bottomContainer.getChildAt(i);
            String label = readBottomCandidateLabel(child, i);
            if (!matchesMenuLabel(label, labels) || this.hiddenBottomViews.containsKey(child)) {
                continue;
            }
            int width = child.getWidth() > 0 ? child.getWidth() : child.getMeasuredWidth();
            int height = child.getHeight() > 0 ? child.getHeight() : child.getMeasuredHeight();
            if (width <= 0 || height <= 0) {
                continue;
            }
            toHide.add(child);
        }
        for (View child : toHide) {
            String label = readBottomCandidateLabel(child, findChildIndex(bottomContainer, child));
            hideMenuItem(child, this.hiddenBottomViews, "hide_bottom_real", label);
        }
    }

    private void logMenuCandidate(View view, String phase, String label) {
        
    }

    private String readTopCandidateLabel(View view, int indexInContainer) {
        String textLabel = readTopTextLabel(view);
        if (!textLabel.isEmpty()) {
            return textLabel;
        }
        switch (indexInContainer) {
            case 0:
                return "recommend";
            case 1:
                return "mall";
            case 2:
                return "follow";
            case 3:
                return "samecity";
            case 4:
                return "groupbuy";
            case 5:
                return "live";
            case 6:
                return "hot";
            case 7:
                return "experience";
            case 8:
                return "featured";
            default:
                return "";
        }
    }

    private String readBottomCandidateLabel(View view, int indexInRoot) {
        String label = readBottomTextLabel(view);
        if (!label.isEmpty()) {
            return label;
        }
        if (indexInRoot == 0) return "home";
        if (indexInRoot == 1) return "friends";
        if (indexInRoot == 2) return "publish";
        if (indexInRoot == 3) return "message";
        if (indexInRoot == 4) return "me";
        return "";
    }

    private String readTopTextLabel(View view) {
        if (view == null) {
            return "";
        }
        if (TOP_TAB_TEXT_CLASS.equals(view.getClass().getName()) && TOP_TAB_TEXT_ID.equals(readResName(view))) {
            String text = normalizeMenuLabel(readViewText(view));
            if (!text.isEmpty() && !text.startsWith("com.bytedance.")) {
                return text;
            }
            String desc = normalizeMenuLabel(readContentDesc(view));
            if (!desc.isEmpty() && !desc.startsWith("com.bytedance.")) {
                return desc;
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                String child = readTopTextLabel(group.getChildAt(i));
                if (!child.isEmpty()) {
                    return child;
                }
            }
        }
        return "";
    }

    private String readBottomTextLabel(View view) {
        if (view == null) {
            return "";
        }
        String text = normalizeMenuLabel(readViewText(view));
        if (!text.isEmpty() && !text.startsWith("com.bytedance.")) {
            return text;
        }
        String desc = normalizeMenuLabel(readContentDesc(view));
        if (!desc.isEmpty() && !desc.startsWith("com.bytedance.")) {
            return desc;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                String child = readBottomTextLabel(group.getChildAt(i));
                if (!child.isEmpty()) {
                    return child;
                }
            }
        }
        return "";
    }

    private void hideMenuItem(View itemView, WeakHashMap<View, Boolean> cache, String phase, String label) {
        try {
            ViewParent parent = itemView.getParent();
            collapseMenuItem(itemView);
            cache.put(itemView, Boolean.TRUE);
            if (phase.startsWith("hide_bottom_real") && parent instanceof ViewGroup) {
                try {
                    ((ViewGroup) parent).removeView(itemView);
                } catch (Throwable ignored) {
                }
            }
            UiProfile profile = MODERN_PROFILE;
            logMatchedView(phase + "[" + label + "]", itemView, profile, viewTokens(itemView));
            Activity activity = findActivity(itemView.getContext());
            if (activity != null) {
                appendFileLog(activity, phase + " label=" + label + " class=" + itemView.getClass().getName() + " id=" + readResName(itemView) + " size=" + itemView.getWidth() + "x" + itemView.getHeight());
            }
        } catch (Throwable ignored) {
        }
    }

    private View resolveTopMenuItemView(View view, View root) {
        View direct = view;
        for (int i = 0; i < 6; i++) {
            if (TOP_TAB_ITEM_CLASS.equals(direct.getClass().getName())) {
                return direct;
            }
            Object parent = direct.getParent();
            if (!(parent instanceof View)) {
                break;
            }
            direct = (View) parent;
            if (direct == root) {
                break;
            }
        }
        View current = view;
        for (int i = 0; i < 6; i++) {
            if (current == root) {
                break;
            }
            Object parent = current.getParent();
            if (!(parent instanceof View)) {
                break;
            }
            View parentView = (View) parent;
            if (parentView == root) {
                break;
            }
            int width = parentView.getWidth() > 0 ? parentView.getWidth() : parentView.getMeasuredWidth();
            int height = parentView.getHeight() > 0 ? parentView.getHeight() : parentView.getMeasuredHeight();
            if (width <= 0 || height <= 0) {
                break;
            }
            current = parentView;
            if (current.isClickable()) {
                return current;
            }
            String className = current.getClass().getName();
            if (className.contains("MainTab") || className.contains("1H5B")) {
                return current;
            }
        }
        return resolveMenuItemView(view, root, dpFromRoot(root, 240), dpFromRoot(root, 180));
    }

    private View resolveBottomMenuItemView(View view, View root) {
        String className = view.getClass().getName();
        if (BOTTOM_TAB_CLASS.equals(className) || PUBLISH_BUTTON_CLASS.equals(className)) {
            return view;
        }
        return resolveMenuItemView(view, root, dpFromRoot(root, 220), dpFromRoot(root, 180));
    }

    private View resolveMenuItemView(View view, View root, int maxWidth, int maxHeight) {
        View current = view;
        for (int i = 0; i < 5; i++) {
            if (current == root) {
                break;
            }
            Object parent = current.getParent();
            if (!(parent instanceof View)) {
                break;
            }
            View parentView = (View) parent;
            if (parentView == root) {
                break;
            }
            int width = parentView.getWidth() > 0 ? parentView.getWidth() : parentView.getMeasuredWidth();
            int height = parentView.getHeight() > 0 ? parentView.getHeight() : parentView.getMeasuredHeight();
            if (width <= 0 || height <= 0 || width > maxWidth || height > maxHeight) {
                break;
            }
            current = parentView;
        }
        return current;
    }

    private void collapseMenuItem(View view) {
        view.setAlpha(0.0f);
        view.setVisibility(8);
        view.setClickable(false);
        view.setEnabled(false);
        view.setFocusable(false);
        view.setTranslationX(0.0f);
        view.setTranslationY(0.0f);
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp != null) {
            lp.width = 0;
            lp.height = 0;
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
                mlp.leftMargin = 0;
                mlp.topMargin = 0;
                mlp.rightMargin = 0;
                mlp.bottomMargin = 0;
            }
            view.setLayoutParams(lp);
        }
        view.requestLayout();
    }

    private void recenterTopMenu(View root) {
        if (!(root instanceof ViewGroup)) {
            return;
        }
        View viewport = findFirstByResName((ViewGroup) root, "lg+");
        View container = findFirstByResName((ViewGroup) root, TOP_TAB_LAYOUT_ID);
        if (!(container instanceof ViewGroup)) {
            container = findFirstByResName((ViewGroup) root, TOP_MENU_PARENT_ID);
        }
        if (!(container instanceof ViewGroup)) {
            return;
        }
        ViewGroup tabContainer = (ViewGroup) container;
        int viewportWidth = viewport != null ? (viewport.getWidth() > 0 ? viewport.getWidth() : viewport.getMeasuredWidth()) : 0;
        if (viewportWidth <= 0) {
            viewportWidth = tabContainer.getWidth() > 0 ? tabContainer.getWidth() : tabContainer.getMeasuredWidth();
        }
        if (viewportWidth <= 0) {
            tabContainer.post(() -> recenterTopMenu(root));
            return;
        }
        if (viewport != null) {
            viewport.scrollTo(0, 0);
        }
        List<View> visibleTabs = new ArrayList<>();
        HashMap<String, View> labelToView = new HashMap<>();
        for (int i = 0; i < tabContainer.getChildCount(); i++) {
            View child = tabContainer.getChildAt(i);
            if (child.getVisibility() != View.VISIBLE) {
                continue;
            }
            if (!containsTopTabText(child)) {
                continue;
            }
            visibleTabs.add(child);
            String label = readTopCandidateLabel(child, i);
            if (!label.isEmpty()) {
                labelToView.put(label, child);
            }
        }
        int count = visibleTabs.size();
        if (count == 0) {
            return;
        }
        List<View> ordered = new ArrayList<>();
        addIfVisible(ordered, labelToView.get("live"));
        addIfVisible(ordered, labelToView.get("follow"));
        addIfVisible(ordered, labelToView.get("recommend"));
        if (ordered.isEmpty()) {
            ordered = visibleTabs;
        }
        int baseScreenLeft = readScreenLeft(viewport != null ? viewport : tabContainer);
        placeViewsIntoScreenSlots(ordered, viewportWidth, baseScreenLeft);
    }

    private void hideTopReturnRecommend(View root) {
        if (!(root instanceof ViewGroup)) {
            return;
        }
        View target = findFirstByResName((ViewGroup) root, TOP_RETURN_RECOMMEND_ID);
        if (target == null || this.hiddenTopViews.containsKey(target)) {
            return;
        }
        hideMenuItem(target, this.hiddenTopViews, "hide_top_real", "return_recommend");
    }

    private void recenterBottomMenu(View root) {
        if (!(root instanceof ViewGroup)) {
            return;
        }
        View container = findFirstByResName((ViewGroup) root, BOTTOM_MENU_LAYOUT_ID);
        if (!(container instanceof ViewGroup)) {
            container = findFirstByResName((ViewGroup) root, BOTTOM_MENU_PARENT_ID);
        }
        if (!(container instanceof ViewGroup)) {
            return;
        }
        ViewGroup bottomContainer = (ViewGroup) container;
        int containerWidth = bottomContainer.getWidth() > 0 ? bottomContainer.getWidth() : bottomContainer.getMeasuredWidth();
        if (containerWidth <= 0) {
            bottomContainer.post(() -> recenterBottomMenu(root));
            return;
        }
        List<View> visibleItems = new ArrayList<>();
        HashMap<String, View> labelToView = new HashMap<>();
        for (int i = 0; i < bottomContainer.getChildCount(); i++) {
            View child = bottomContainer.getChildAt(i);
            if (child.getVisibility() != View.VISIBLE) {
                continue;
            }
            visibleItems.add(child);
            String label = readBottomCandidateLabel(child, i);
            if (!label.isEmpty()) {
                labelToView.put(label, child);
            }
        }
        int count = visibleItems.size();
        if (count == 0) {
            return;
        }
        List<View> ordered = new ArrayList<>();
        addIfVisible(ordered, labelToView.get("home"));
        addIfVisible(ordered, labelToView.get("message"));
        addIfVisible(ordered, labelToView.get("me"));
        if (ordered.isEmpty()) {
            ordered = visibleItems;
        }
        normalizeBottomLinearChildren(bottomContainer, ordered, containerWidth);
        placeViewsIntoScreenSlots(ordered, containerWidth, readScreenLeft(bottomContainer));
    }

    private void normalizeBottomLinearChildren(ViewGroup bottomContainer, List<View> visibleItems, int containerWidth) {
        if (!(bottomContainer instanceof LinearLayout)) {
            return;
        }
        int slotWidth = visibleItems.isEmpty() ? 0 : containerWidth / visibleItems.size();
        LinearLayout linear = (LinearLayout) bottomContainer;
        for (int i = 0; i < linear.getChildCount(); i++) {
            View child = linear.getChildAt(i);
            ViewGroup.LayoutParams raw = child.getLayoutParams();
            if (!(raw instanceof LinearLayout.LayoutParams)) {
                continue;
            }
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) raw;
            if (child.getVisibility() == View.VISIBLE && visibleItems.contains(child)) {
                lp.width = slotWidth;
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.weight = 0.0f;
                lp.leftMargin = 0;
                lp.rightMargin = 0;
            } else {
                lp.width = 0;
                lp.height = 0;
                lp.weight = 0.0f;
            }
            child.setLayoutParams(lp);
        }
        linear.requestLayout();
    }

    private void placeViewsIntoSlots(List<View> views, int containerWidth, float baseX) {
        int count = views.size();
        if (count == 0 || containerWidth <= 0) {
            return;
        }
        float slotWidth = containerWidth / (float) count;
        for (int i = 0; i < count; i++) {
            View child = views.get(i);
            int childWidth = child.getWidth() > 0 ? child.getWidth() : child.getMeasuredWidth();
            if (childWidth <= 0) {
                continue;
            }
            float targetLeft = baseX + (slotWidth * i) + ((slotWidth - childWidth) / 2.0f);
            child.setTranslationX(targetLeft - child.getLeft());
        }
    }

    private void placeViewsIntoScreenSlots(List<View> views, int containerWidth, int baseScreenLeft) {
        int count = views.size();
        if (count == 0 || containerWidth <= 0) {
            return;
        }
        float slotWidth = containerWidth / (float) count;
        Rect rect = new Rect();
        for (int i = 0; i < count; i++) {
            View child = views.get(i);
            int childWidth = child.getWidth() > 0 ? child.getWidth() : child.getMeasuredWidth();
            if (childWidth <= 0) {
                continue;
            }
            child.getGlobalVisibleRect(rect);
            float targetScreenLeft = baseScreenLeft + (slotWidth * i) + ((slotWidth - childWidth) / 2.0f);
            float delta = targetScreenLeft - rect.left;
            child.setTranslationX(child.getTranslationX() + delta);
        }
    }

    private int readScreenLeft(View view) {
        if (view == null) {
            return 0;
        }
        Rect rect = new Rect();
        try {
            view.getGlobalVisibleRect(rect);
            return rect.left;
        } catch (Throwable ignored) {
            return view.getLeft();
        }
    }

    private int findChildIndex(ViewGroup group, View child) {
        for (int i = 0; i < group.getChildCount(); i++) {
            if (group.getChildAt(i) == child) {
                return i;
            }
        }
        return -1;
    }

    private void addIfVisible(List<View> out, View view) {
        if (view != null && view.getVisibility() == View.VISIBLE) {
            out.add(view);
        }
    }

    private int topOrderPriority(String label) {
        if ("live".equals(label)) return 0;
        if ("follow".equals(label)) return 1;
        if ("recommend".equals(label)) return 2;
        return 99;
    }

    private int bottomOrderPriority(String label) {
        if ("home".equals(label)) return 0;
        if ("message".equals(label)) return 1;
        if ("me".equals(label)) return 2;
        return 99;
    }

    private boolean containsTopTabText(View view) {
        if (view == null) {
            return false;
        }
        if (TOP_TAB_TEXT_CLASS.equals(view.getClass().getName()) && TOP_TAB_TEXT_ID.equals(readResName(view))) {
            return true;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                if (containsTopTabText(group.getChildAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    private View findFirstByResName(ViewGroup root, String resName) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (resName.equals(readResName(child))) {
                return child;
            }
            if (child instanceof ViewGroup) {
                View found = findFirstByResName((ViewGroup) child, resName);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private void applyStatusBarTransparent(Activity a) {
        try {
            Window w = a.getWindow();
            if (w != null) {
                w.setStatusBarColor(0);
            }
        } catch (Throwable th) {
        }
    }

    private void applyBottomTransparent(ViewGroup root, UiProfile profile) {
        traverse(root, v -> {
            String all = viewTokens(v);
            if (looksLikeBottomUi(v, all, profile)) {
                try {
                    v.setAlpha(0.35f);
                } catch (Throwable th) {
                }
            }
        });
    }

    private void applyHomeControlOpacity(ViewGroup root, UiProfile profile) {
        Activity activity = findActivity(root.getContext());
        if (activity != null) {
            applyPreciseHomeOpacity(activity, root);
        }
    }

    private void applyPreciseHomeOpacity(Activity activity, ViewGroup root) {
        float alpha = getBool("opacity_control", true) ? 0.35f : 1.0f;
        boolean rightMatched = applyAlphaByTargets(activity, root, alpha, 0, new String[]{"gh="}, new int[]{0});
        if (!rightMatched) {
            applyAlphaByTargets(activity, root, alpha, 0, new String[]{"gho", "comment_container", "d=q", "share_container", "text_like_count", "text_comment_count", "text_favorite_count", "text_share_count"}, new int[]{0, 0, 0, 0, ID_TEXT_LIKE_COUNT, ID_TEXT_COMMENT_COUNT, ID_TEXT_FAVORITE_COUNT, ID_TEXT_SHARE_COUNT});
        }
        applyAlphaByTargets(activity, root, alpha, 0, new String[]{"sm+", "music_view"}, new int[]{0, ID_MUSIC_VIEW});
        applyAlphaByTargets(activity, root, alpha, 0, new String[]{"desc", "title", "video_text", "bottom_left_double", "bottom_left_double_text", "bottom_left_long", "bottom_left_long_text"}, new int[]{0, 0, ID_VIDEO_TEXT, ID_BOTTOM_LEFT_DOUBLE, ID_BOTTOM_LEFT_DOUBLE_TEXT, ID_BOTTOM_LEFT_LONG, ID_BOTTOM_LEFT_LONG_TEXT});
    }


    private void scheduleGeneralUiRefresh(ViewGroup root, UiProfile profile, long delayMs) {
        root.postDelayed(() -> {
            try {
                if (getBool("bottom_bar_transparent", true)) {
                    applyBottomTransparent(root, profile);
                }
                Activity activity = findActivity(root.getContext());
                if (activity != null) {
                    applyPreciseHomeOpacity(activity, root);
                }
                applyTopBottomMenuFilter(root, profile);
            } catch (Throwable th) {
            }
        }, delayMs);
    }

    private void scheduleRuntimeViewDump(Activity activity, ViewGroup root, UiProfile profile) {
        synchronized (this.runtimeDumpScheduled) {
            if (this.runtimeDumpScheduled.containsKey(activity)) {
                return;
            }
            this.runtimeDumpScheduled.put(activity, Boolean.TRUE);
            root.postDelayed(() -> {
                try {
                    dumpRuntimeViewSnapshot(activity, root, profile);
                } catch (Throwable t) {
                    this.xposed.log(5, TAG, "dumpRuntimeViewSnapshot failed", t);
                }
            }, 1200L);
        }
    }

    private void dumpRuntimeViewSnapshot(Activity activity, ViewGroup root, UiProfile profile) {
        ArrayList<String> lines = new ArrayList<>();
        traverse(root, v -> {
            String bucket;
            if (isInterestingForDump(v, profile) && (bucket = classifyDebugBucket(v, profile)) != null) {
                lines.add(bucket + " " + describeViewForDump(v));
            }
        });
        this.xposed.log(5, TAG, "runtime_dump activity=" + activity.getClass().getName() + " versionCode=" + readVersionCode(activity) + " candidates=" + lines.size());
        appendFileLog(activity, "runtime_dump activity=" + activity.getClass().getName() + " versionCode=" + readVersionCode(activity) + " candidates=" + lines.size());
        int limit = Math.min(lines.size(), 80);
        for (int i = 0; i < limit; i++) {
            this.xposed.log(5, TAG, "runtime_dump[" + i + "] " + lines.get(i));
            appendFileLog(activity, "runtime_dump[" + i + "] " + lines.get(i));
        }
    }

    private boolean isInterestingForDump(View v, UiProfile profile) {
        if (v.getVisibility() != 0) {
            return false;
        }
        int h = v.getHeight() > 0 ? v.getHeight() : v.getMeasuredHeight();
        int w = v.getWidth() > 0 ? v.getWidth() : v.getMeasuredWidth();
        if (h <= 0 || w <= 0) {
            return false;
        }
        String id = readResName(v);
        String text = readViewText(v);
        String desc = readContentDesc(v);
        if (!id.isEmpty() || !text.isEmpty() || !desc.isEmpty()) {
            return true;
        }
        String tokens = viewTokens(v);
        return containsAny(tokens, profile.rightTokens) || containsAny(tokens, profile.musicTokens) || containsAny(tokens, profile.textTokens);
    }

    private String classifyDebugBucket(View v, UiProfile profile) {
        String tokens = viewTokens(v);
        if (containsAny(tokens, profile.rightTokens)) {
            return "[RIGHT]";
        }
        if (containsAny(tokens, profile.musicTokens)) {
            return "[MUSIC]";
        }
        if (containsAny(tokens, profile.textTokens)) {
            return "[TEXT]";
        }
        if (!readResName(v).isEmpty()) {
            return "[ID]";
        }
        if (!readViewText(v).isEmpty()) {
            return "[TEXTVAL]";
        }
        if (readContentDesc(v).isEmpty()) {
            return null;
        }
        return "[DESC]";
    }

    private String describeViewForDump(View v) {
        Rect rect = new Rect();
        try {
            v.getGlobalVisibleRect(rect);
        } catch (Throwable th) {
        }
        return "view=" + v.getClass().getName() + " id=" + readResName(v) + " text=" + abbreviate(readViewText(v), 80) + " desc=" + abbreviate(readContentDesc(v), 80) + " size=" + v.getWidth() + "x" + v.getHeight() + " rect=" + rect.left + "," + rect.top + "," + rect.right + "," + rect.bottom + " tokens=" + abbreviate(viewTokens(v), 180);
    }

    private boolean applyAlphaByTargets(Activity activity, ViewGroup root, float alpha, int parentDepth, String[] names, int[] fallbackIds) {
        View found;
        boolean matched = false;
        for (int i = 0; i < names.length; i++) {
            int id = resolveTargetId(activity, names[i], fallbackIds[i]);
            if (id != 0 && (found = root.findViewById(id)) != null && !shouldSkipOpacityTarget(found)) {
                try {
                    found.setAlpha(alpha);
                    if (parentDepth > 0) {
                        propagateAlphaUp(found, alpha, parentDepth);
                    }
                    logExactMatchedView("home_opacity", found, names[i], id);
                    matched = true;
                } catch (Throwable th) {
                }
            }
        }
        return matched;
    }

    private void applyTopBottomMenuFilter(ViewGroup root, UiProfile profile) {
        boolean hideTop = getBool("top_menu", true);
        boolean hideBottom = getBool("bottom_menu", true);
        Set<String> topLabels = parseLabelSet(getString("top_hide_labels", ""));
        Set<String> bottomLabels = parseLabelSet(getString("bottom_hide_labels", ""));
        if ((!hideTop || topLabels.isEmpty()) && (!hideBottom || bottomLabels.isEmpty())) {
            return;
        }
        traverse(root, v -> {
            if (shouldSkipHiding(v)) {
                return;
            }
            String all = viewTokens(v);
            String label = readMenuLabel(v);
            if (hideTop && !topLabels.isEmpty() && looksLikeTopUi(v, all, profile) && matchesMenuLabel(label, topLabels)) {
                hideView(resolveMenuItemView(v), this.hiddenTopViews);
                logMatchedView("hide_top[" + label + "]", v, profile, all);
            } else if (hideBottom && !bottomLabels.isEmpty() && looksLikeBottomUi(v, all, profile) && matchesMenuLabel(label, bottomLabels)) {
                hideView(resolveMenuItemView(v), this.hiddenBottomViews);
                logMatchedView("hide_bottom[" + label + "]", v, profile, all);
            }
        });
    }

    private boolean shouldSkipHiding(View v) {
        String all = viewTokens(v);
        if (all.contains("content") || all.contains("container") || all.contains("root") || all.contains("fragment") || all.contains("recycler") || all.contains("viewpager") || all.contains("pager") || all.contains("feed")) {
            return true;
        }
        if (!(v instanceof ViewGroup)) {
            return false;
        }
        ViewGroup g = (ViewGroup) v;
        return g.getChildCount() > 5;
    }

    private boolean looksLikeTopUi(View v, String all, UiProfile profile) {
        if (isSmallUiNode(v) && !containsAny(all, profile.excludeTokens)) {
            return containsAny(all, profile.topTokens);
        }
        return false;
    }

    private boolean looksLikeBottomUi(View v, String all, UiProfile profile) {
        if (isSmallUiNode(v) && !containsAny(all, profile.excludeTokens)) {
            return containsAny(all, profile.bottomTokens);
        }
        return false;
    }

    private void hideView(View v, WeakHashMap<View, Boolean> cache) {
        if (cache.containsKey(v)) {
            return;
        }
        try {
            v.setVisibility(8);
            cache.put(v, Boolean.TRUE);
        } catch (Throwable th) {
        }
    }

    private int dpFromRoot(View v, int dp) {
        try {
            return (int) ((v.getResources().getDisplayMetrics().density * dp) + 0.5f);
        } catch (Throwable th) {
            return dp;
        }
    }

    private void propagateAlphaUp(View view, float alpha, int maxDepth) {
        View current = view;
        for (int i = 0; i < maxDepth; i++) {
            Object parent = current.getParent();
            if (!(parent instanceof View)) {
                return;
            }
            View parent2 = (View) parent;
            try {
                parent2.setAlpha(alpha);
                current = parent2;
            } catch (Throwable th) {
                return;
            }
        }
    }

    private boolean isSmallUiNode(View v) {
        if (v.getId() == 16908290 || !(v.getParent() instanceof ViewGroup)) {
            return false;
        }
        if (v instanceof ViewGroup) {
            ViewGroup g = (ViewGroup) v;
            if (g.getChildCount() > 5) {
                return false;
            }
        }
        int h = v.getHeight() > 0 ? v.getHeight() : v.getMeasuredHeight();
        int w = v.getWidth() > 0 ? v.getWidth() : v.getMeasuredWidth();
        return h <= dpFromRoot(v, 220) && w <= dpFromRoot(v, 420);
    }

    private boolean isHomeOpacityNode(View v) {
        if (v.getId() == 16908290 || !(v.getParent() instanceof ViewGroup)) {
            return false;
        }
        if (v instanceof ViewGroup) {
            ViewGroup g = (ViewGroup) v;
            if (g.getChildCount() > 8) {
                return false;
            }
        }
        int h = v.getHeight() > 0 ? v.getHeight() : v.getMeasuredHeight();
        int w = v.getWidth() > 0 ? v.getWidth() : v.getMeasuredWidth();
        return h <= dpFromRoot(v, 360) && w <= dpFromRoot(v, 720);
    }

    private boolean looksLikeRightActionTarget(View v, String all, UiProfile profile) {
        if (containsAny(all, profile.rightTokens)) {
            return isHomeOpacityNode(v) || isRightActionNode(v);
        }
        return false;
    }

    private boolean looksLikeMusicTarget(View v, String all, UiProfile profile) {
        if (containsAny(all, profile.musicTokens)) {
            return isHomeOpacityNode(v) || isMusicNode(v);
        }
        return false;
    }

    private boolean looksLikeCaptionTarget(View v, String all, UiProfile profile) {
        if (containsAny(all, profile.textTokens)) {
            return isHomeOpacityNode(v) || isCaptionNode(v);
        }
        return false;
    }

    private boolean isRightActionNode(View v) {
        if (v.getId() == 16908290) {
            return false;
        }
        int h = v.getHeight() > 0 ? v.getHeight() : v.getMeasuredHeight();
        int w = v.getWidth() > 0 ? v.getWidth() : v.getMeasuredWidth();
        return h > 0 && w > 0 && h <= dpFromRoot(v, 500) && w <= dpFromRoot(v, 260);
    }

    private boolean isMusicNode(View v) {
        if (v.getId() == 16908290) {
            return false;
        }
        int h = v.getHeight() > 0 ? v.getHeight() : v.getMeasuredHeight();
        int w = v.getWidth() > 0 ? v.getWidth() : v.getMeasuredWidth();
        return h > 0 && w > 0 && h <= dpFromRoot(v, 260) && w <= dpFromRoot(v, 360);
    }

    private boolean isCaptionNode(View v) {
        if (v.getId() == 16908290) {
            return false;
        }
        int h = v.getHeight() > 0 ? v.getHeight() : v.getMeasuredHeight();
        int w = v.getWidth() > 0 ? v.getWidth() : v.getMeasuredWidth();
        return h > 0 && w > 0 && h <= dpFromRoot(v, 320) && w <= dpFromRoot(v, 900);
    }

    private boolean isTargetActivity(Activity a) {
        try {
            String name = a.getClass().getName();
            if (name != null) {
                if (name.startsWith("com.ss.android.ugc.aweme")) {
                    return true;
                }
            }
            return false;
        } catch (Throwable th) {
            return false;
        }
    }

    private String viewTokens(View v) {
        String n = readResName(v).toLowerCase(Locale.ROOT);
        String c = v.getClass().getName().toLowerCase(Locale.ROOT);
        String text = readViewText(v).toLowerCase(Locale.ROOT);
        String desc = readContentDesc(v).toLowerCase(Locale.ROOT);
        return n + " " + c + " " + text + " " + desc;
    }

    private String ancestryTokens(View v, int maxDepth) {
        StringBuilder sb = new StringBuilder();
        View current = v;
        for (int i = 0; i < maxDepth && current != null; i++) {
            sb.append(' ').append(viewTokens(current));
            Object parent = current.getParent();
            if (!(parent instanceof View)) {
                break;
            }
            View parent2 = (View) parent;
            current = parent2;
        }
        return sb.toString();
    }

    private UiProfile resolveProfile(Activity a) {
        UiProfile profile;
        UiProfile cached = this.activityProfiles.get(a);
        if (cached != null) {
            return cached;
        }
        String override = getString("profile_override", "auto").trim().toLowerCase(Locale.ROOT);
        if ("legacy".equals(override)) {
            profile = LEGACY_PROFILE;
        } else if ("modern".equals(override)) {
            profile = MODERN_PROFILE;
        } else {
            int versionCode = readVersionCode(a);
            profile = versionCode <= LEGACY_PROFILE.maxVersionCode ? LEGACY_PROFILE : MODERN_PROFILE;
        }
        this.activityProfiles.put(a, profile);
        return profile;
    }

    private int resolveTargetId(Activity activity, String name, int fallbackId) {
        synchronized (this.resolvedIdCache) {
            Integer cached = this.resolvedIdCache.get(name);
            if (cached != null) {
                return cached.intValue();
            }
            int resolved = 0;
            try {
                ClassLoader cl = activity.getClassLoader();
                Class<?> rid = Class.forName(activity.getPackageName() + ".R$id", false, cl);
                resolved = rid.getField(name).getInt(null);
            } catch (Throwable th) {
            }
            if (resolved == 0) {
                try {
                    resolved = activity.getResources().getIdentifier(name, "id", activity.getPackageName());
                } catch (Throwable th2) {
                }
            }
            if (resolved == 0) {
                resolved = fallbackId;
            }
            synchronized (this.resolvedIdCache) {
                this.resolvedIdCache.put(name, Integer.valueOf(resolved));
            }
            return resolved;
        }
    }

    private int readVersionCode(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return (int) info.getLongVersionCode();
        } catch (Throwable th) {
            return Integer.MAX_VALUE;
        }
    }

    private void maybeLogProfile(Activity a, UiProfile profile) {
        if (!getBool("debug_log", false) || this.profileLogged.containsKey(a)) {
            return;
        }
        this.profileLogged.put(a, Boolean.TRUE);
        this.xposed.log(4, TAG, "profile=" + profile.name + ", versionCode=" + readVersionCode(a) + ", activity=" + a.getClass().getName());
    }

    private void maybeLogUiApply(Activity a, String phase) {
        synchronized (this.uiApplyLogged) {
            if (this.uiApplyLogged.containsKey(a)) {
                return;
            }
            this.uiApplyLogged.put(a, Boolean.TRUE);
            this.xposed.log(5, TAG, "ui_apply phase=" + phase + " activity=" + a.getClass().getName() + ", versionCode=" + readVersionCode(a));
            appendFileLog(a, "ui_apply phase=" + phase + " activity=" + a.getClass().getName() + ", versionCode=" + readVersionCode(a));
        }
    }

    private void maybeLogUiSkip(Activity a, String reason) {
        synchronized (this.uiSkipLogged) {
            if (this.uiSkipLogged.containsKey(a)) {
                return;
            }
            this.uiSkipLogged.put(a, Boolean.TRUE);
            this.xposed.log(5, TAG, "ui_skip reason=" + reason + " activity=" + a.getClass().getName() + ", versionCode=" + readVersionCode(a));
            appendFileLog(a, "ui_skip reason=" + reason + " activity=" + a.getClass().getName() + ", versionCode=" + readVersionCode(a));
        }
    }

    private void logMatchedView(String phase, View v, UiProfile profile, String all) {
        if (getBool("debug_match_log", false)) {
            this.xposed.log(4, TAG, phase + " profile=" + profile.name + " view=" + v.getClass().getName() + " id=" + readResName(v) + " size=" + v.getWidth() + "x" + v.getHeight() + " tokens=" + abbreviate(all, 220));
            Activity activity = findActivity(v.getContext());
            if (activity != null) {
                appendFileLog(activity, phase + " profile=" + profile.name + " view=" + v.getClass().getName() + " id=" + readResName(v) + " size=" + v.getWidth() + "x" + v.getHeight() + " tokens=" + abbreviate(all, 220));
            }
        }
    }

    private void logExactMatchedView(String phase, View v, String idName, int idValue) {
        if (getBool("debug_match_log", false)) {
            this.xposed.log(4, TAG, phase + " exactId=" + idName + "/0x" + Integer.toHexString(idValue) + " view=" + v.getClass().getName() + " id=" + readResName(v) + " size=" + v.getWidth() + "x" + v.getHeight());
            Activity activity = findActivity(v.getContext());
            if (activity != null) {
                appendFileLog(activity, phase + " exactId=" + idName + "/0x" + Integer.toHexString(idValue) + " view=" + v.getClass().getName() + " id=" + readResName(v) + " size=" + v.getWidth() + "x" + v.getHeight() + " text=" + abbreviate(readViewText(v), 120) + " desc=" + abbreviate(readContentDesc(v), 120));
            }
        }
    }

    private void appendFileLog(Activity activity, String line) {
        if (activity == null) {
            return;
        }
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (dir == null) {
                return;
            }
            if (dir.exists() || dir.mkdirs()) {
                File file = new File(dir, LOG_FILE_NAME);
                String ts = String.format(Locale.ROOT, "%1$tF %1$tT", new Date());
                synchronized (this.fileLogLock) {
                    FileWriter writer = new FileWriter(file, true);
                    try {
                        writer.write(ts + " " + line + "\n");
                        writer.close();
                    } catch (Throwable th) {
                        writer.close();
                        throw th;
                    }
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private String abbreviate(String value, int max) {
        return value == null ? "" : value.length() <= max ? value : value.substring(0, max);
    }

    private Set<String> parseLabelSet(String raw) {
        Set<String> result = new HashSet<>();
        if (raw == null) {
            return result;
        }
        String normalizedRaw = raw.replace('\uFF0C', ',');
        for (String part : normalizedRaw.split(",")) {
            addNormalizedMenuToken(result, part);
        }
        return result;
    }

    private boolean matchesMenuLabel(String label, Set<String> targets) {
        String normalized = normalizeMenuLabel(label);
        if (normalized.isEmpty()) {
            return false;
        }
        for (String target : targets) {
            if (normalized.equals(target) || normalized.contains(target) || target.contains(normalized)) {
                return true;
            }
            if ("publish".equals(target) && normalized.contains("publish")) {
                return true;
            }
        }
        return false;
    }

    private String readMenuLabel(View view) {
        String fieldValue = normalizeMenuLabel(readTopTabFieldLabel(view));
        if (!fieldValue.isEmpty()) {
            return fieldValue;
        }
        String text = normalizeMenuLabel(readViewText(view));
        if (!text.isEmpty()) {
            return text;
        }
        String desc = normalizeMenuLabel(readContentDesc(view));
        if (!desc.isEmpty()) {
            return desc;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = Math.min(group.getChildCount(), 6);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count; i++) {
                String child = readMenuLabel(group.getChildAt(i));
                if (!child.isEmpty()) {
                    if (sb.length() > 0) {
                        sb.append(' ');
                    }
                    sb.append(child);
                }
            }
            return sb.length() > 0 ? sb.toString() : "";
        }
        return "";
    }

    private String readTopTabFieldLabel(View view) {
        if (view == null) {
            return "";
        }
        if (TOP_TAB_TEXT_CLASS.equals(view.getClass().getName())) {
            try {
                Field field = view.getClass().getDeclaredField("l");
                field.setAccessible(true);
                Object value = field.get(view);
                return value != null ? String.valueOf(value) : "";
            } catch (Throwable ignored) {
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = Math.min(group.getChildCount(), 6);
            for (int i = 0; i < count; i++) {
                String child = readTopTabFieldLabel(group.getChildAt(i));
                if (!child.isEmpty()) {
                    return child;
                }
            }
        }
        return "";
    }
    private String normalizeMenuLabel(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.trim()
                .replace('\n', ' ')
                .replace('\r', ' ')
                .replace("tab", " ")
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ")
                .trim();
        if (normalized.isEmpty()) {
            return "";
        }
        if (normalized.contains("\u63a8\u8350")) return "recommend";
        if (normalized.contains("recommend")) return "recommend";
        if (normalized.contains("\u5546\u57ce")) return "mall";
        if (normalized.contains("mall")) return "mall";
        if (normalized.contains("\u5173\u6ce8")) return "follow";
        if (normalized.contains("follow")) return "follow";
        if (normalized.contains("\u540c\u57ce")) return "samecity";
        if (normalized.contains("samecity")) return "samecity";
        if (normalized.contains("\u56e2\u8d2d")) return "groupbuy";
        if (normalized.contains("groupbuy")) return "groupbuy";
        if (normalized.contains("\u76f4\u64ad")) return "live";
        if (normalized.contains("live")) return "live";
        if (normalized.contains("\u70ed\u70b9")) return "hot";
        if (normalized.contains("hot")) return "hot";
        if (normalized.contains("\u7ecf\u9a8c")) return "experience";
        if (normalized.contains("experience")) return "experience";
        if (normalized.contains("\u7cbe\u9009")) return "featured";
        if (normalized.contains("featured")) return "featured";
        if (normalized.contains("\u9996\u9875")) return "home";
        if (normalized.contains("home")) return "home";
        if (normalized.contains("\u670b\u53cb")) return "friends";
        if (normalized.contains("friends")) return "friends";
        if (normalized.contains("\u6d88\u606f")) return "message";
        if (normalized.contains("message")) return "message";
        if ("\u6211".equals(normalized) || normalized.endsWith(" \u6211")) return "me";
        if ("+".equals(normalized)
                || normalized.contains("\u53d1\u5e03")
                || normalized.contains("\u62cd\u6444")
                || normalized.contains("publish")
                || normalized.contains("shoot")
                || normalized.contains("camera")) {
            return "publish";
        }
        return normalized;
    }

    private void addNormalizedMenuToken(Set<String> result, String raw) {
        String item = normalizeMenuLabel(raw);
        if (item.isEmpty()) {
            return;
        }
        result.add(item);
    }

    private View resolveMenuItemView(View view) {
        View current = view;
        for (int i = 0; i < 3; i++) {
            Object parent = current.getParent();
            if (!(parent instanceof View)) {
                break;
            }
            View parent2 = (View) parent;
            int width = parent2.getWidth() > 0 ? parent2.getWidth() : parent2.getMeasuredWidth();
            int height = parent2.getHeight() > 0 ? parent2.getHeight() : parent2.getMeasuredHeight();
            if (width <= 0 || height <= 0 || width > dpFromRoot(parent2, 420) || height > dpFromRoot(parent2, 260)) {
                break;
            }
            current = parent2;
        }
        return current;
    }

    private boolean shouldSkipOpacityTarget(View v) {
        String ancestry = ancestryTokens(v, 6);
        String self = viewTokens(v);
        if (containsAny(self, tokenSet("danmaku", "barrage", "bullet", "chat_room", "live_chat")) || containsAny(ancestry, tokenSet("danmaku", "barrage", "bullet", "chat_room", "live_chat", "comment_panel", "comment_list", "comment_root", "reply", "input", "edit"))) {
            return true;
        }
        return false;
    }

    private static Set<String> tokenSet(String... values) {
        return new HashSet(Arrays.asList(values));
    }

    private boolean containsAny(String all, Set<String> tokens) {
        if (all == null || tokens == null || tokens.isEmpty()) {
            return false;
        }
        for (String token : tokens) {
            if (token != null && !token.isEmpty() && all.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private boolean isExcludedOpacityTarget(View v, UiProfile profile) {
        String ancestry = ancestryTokens(v, 5);
        if (containsAny(ancestry, profile.excludeTokens)) {
            String self = viewTokens(v);
            if ((!containsAny(self, profile.rightTokens) && !containsAny(self, profile.musicTokens) && !containsAny(self, profile.textTokens)) || !looksLikeFeedAnchor(self)) {
                return true;
            }
        }
        return !isLikelyFeedContext(v);
    }

    private boolean isLikelyFeedContext(View v) {
        String ancestry = ancestryTokens(v, 6);
        if (containsAny(ancestry, tokenSet("comment_panel", "comment_list", "comment_root", "reply", "input", "edit", "danmaku", "barrage", "bullet", "chat_room", "live_chat"))) {
            return false;
        }
        return containsAny(ancestry, tokenSet("feed", "aweme", "detail", "player", "video", "right", "bottom_left", "desc", "music", "actionbar", "sidebar")) || v.getRootView() != null;
    }

    private boolean looksLikeFeedAnchor(String self) {
        return containsAny(self, tokenSet("feed", "aweme", "video", "music", "desc", "caption", "title", "actionbar", "sidebar", "digg", "share", "collect", "comment"));
    }

    private Set<String> getExtraOpacityTokens() {
        String raw = getString("home_opacity_extra_tokens", "");
        if (raw.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> out = new HashSet<>();
        for (String line : raw.split("\\r?\\n")) {
            String token = line.trim().toLowerCase(Locale.ROOT);
            if (!token.isEmpty()) {
                out.add(token);
            }
        }
        return out;
    }

    private Activity findActivity(Context context) {
        Context current = context;
        for (int i = 0; i < 10 && current != null; i++) {
            if (current instanceof Activity) {
                Activity activity = (Activity) current;
                return activity;
            }
            if (!(current instanceof ContextWrapper)) {
                return null;
            }
            ContextWrapper wrapper = (ContextWrapper) current;
            current = wrapper.getBaseContext();
        }
        return null;
    }
    private boolean matchesVideoFilter(String text) {
        String keys = getString("video_filter_keywords", "");
        if (!keys.isEmpty()) {
            String lower = text.toLowerCase(Locale.ROOT);
            for (String line : keys.split("\\r?\\n")) {
                String k = line.trim().toLowerCase(Locale.ROOT);
                if (!k.isEmpty() && lower.contains(k)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matchesLowLikeCandidate(TextView tv, String text) {
        if (!isLikelyFeedContext(tv)) {
            return false;
        }
        try {
            Integer likes = parseLikeCount(text);
            if (likes == null || likes.intValue() <= 0 || likes.intValue() >= 100) {
                return false;
            }
            String resName = readResName(tv);
            String tokens = viewTokens(tv) + " " + ancestryTokens(tv, 8);
            if ("text_like_count".equals(resName) || tv.getId() == ID_TEXT_LIKE_COUNT) {
                logVideoFilterCandidate(tv, "low_like_id", text);
                return true;
            }
            boolean inLikeZone = containsAny(tokens, tokenSet("digg", "like", "zan", "rightscale", "feedrightscaleview", "actionbar", "sidebar"));
            boolean excluded = containsAny(tokens, tokenSet("comment", "share", "forward", "collect", "favorite", "music", "desc", "title"));
            if (inLikeZone && !excluded) {
                logVideoFilterCandidate(tv, "low_like_context", text);
                return true;
            }
            return false;
        } catch (Throwable th) {
            return false;
        }
    }

    private Integer parseLikeCount(String text) {
        String normalized = text.replaceAll("[^0-9.\\u4e07wWkK]", "").trim();
        if (normalized.isEmpty()) {
            return null;
        }
        if (normalized.endsWith("\u4e07") || normalized.endsWith("w") || normalized.endsWith("W")) {
            String base = normalized.substring(0, normalized.length() - 1);
            return Integer.valueOf((int) (Float.parseFloat(base) * 10000.0f));
        }
        if (normalized.endsWith("k") || normalized.endsWith("K")) {
            String base = normalized.substring(0, normalized.length() - 1);
            return Integer.valueOf((int) (Float.parseFloat(base) * 1000.0f));
        }
        return Integer.valueOf(Integer.parseInt(normalized));
    }

    private void confirmAndHideLowLike(TextView tv, String originalText) {
        tv.postDelayed(() -> {
            try {
                if (tv.getVisibility() != View.VISIBLE) {
                    return;
                }
                String current = readViewText(tv).trim();
                Integer likes = parseLikeCount(current);
                if (likes != null && likes.intValue() > 0 && likes.intValue() < 100 && matchesLowLikeCandidate(tv, current)) {
                    hideNearestContainer(tv, "low_like", current);
                } else {
                    Activity activity = findActivity(tv.getContext());
                    if (activity != null) {
                        appendFileLog(activity, "video_filter_skip reason=low_like_recheck original=" + originalText + " current=" + current);
                    }
                }
            } catch (Throwable ignored) {
            }
        }, 350L);
    }

    private boolean matchesLiveStreamFilter(TextView tv, String text) {
        if (!isLikelyFeedContext(tv)) {
            return false;
        }
        String lower = text.toLowerCase(Locale.ROOT);
        String tokens = viewTokens(tv) + " " + ancestryTokens(tv, 8);
        boolean liveText = text.contains("\u76f4\u64ad\u4e2d") || text.contains("\u6b63\u5728\u76f4\u64ad") || text.contains("\u76f4\u64ad") || lower.contains("live") || lower.contains("preview");
        boolean liveContext = containsAny(tokens, tokenSet("live", "preview", "room", "broadcast", "\u76f4\u64ad"));
        boolean excluded = containsAny(tokens, tokenSet("titlebar", "maintab", "bottomtab", "homepage.ui.titlebar", "comment_panel", "comment_list", "comment_root"));
        if ((!liveText && !liveContext) || excluded) {
            return false;
        }
        logVideoFilterCandidate(tv, liveText ? "live_text" : "live_context", text);
        return true;
    }


    private void hideNearestContainer(View v, String reason, String text) {
        View cur = v;
        for (int i = 0; i < 8 && cur != null; i++) {
            ViewParent parent = cur.getParent();
            if (!(parent instanceof ViewGroup)) {
                return;
            }
            ViewGroup p = (ViewGroup) parent;
            if (p.getChildCount() > 2) {
                p.setVisibility(8);
                Activity activity = findActivity(v.getContext());
                if (activity != null) {
                    appendFileLog(activity, "video_filter_hit reason=" + reason + " text=" + abbreviate(text, 120) + " view=" + v.getClass().getName() + " id=" + readResName(v) + " container=" + p.getClass().getName() + " containerId=" + readResName(p));
                }
                return;
            }
            cur = p;
        }
    }


    private void logVideoFilterCandidate(View v, String reason, String text) {
        Activity activity = findActivity(v.getContext());
        if (activity == null) {
            return;
        }
        appendFileLog(activity, "video_filter_candidate reason=" + reason + " text=" + abbreviate(text, 80) + " view=" + v.getClass().getName() + " id=" + readResName(v) + " ancestry=" + abbreviate(ancestryTokens(v, 6), 180));
    }
    private boolean clickByName(View v, String... names) {
        View found = findClickable(v, names, 0);
        return found != null && found.performClick();
    }

    private View findClickable(View v, String[] names, int depth) {
        if (v == null || depth > 10) {
            return null;
        }
        if (v.isClickable() && matchesAny(v, names)) {
            return v;
        }
        if (!(v instanceof ViewGroup)) {
            return null;
        }
        ViewGroup g = (ViewGroup) v;
        for (int i = 0; i < g.getChildCount(); i++) {
            View f = findClickable(g.getChildAt(i), names, depth + 1);
            if (f != null) {
                return f;
            }
        }
        return null;
    }

    private boolean matchesAny(View v, String... keywords) {
        String name = (readResName(v) + " " + v.getClass().getName()).toLowerCase(Locale.ROOT);
        for (String k : keywords) {
            if (name.contains(k)) {
                return true;
            }
        }
        return false;
    }

    private void traverse(View v, Consumer<View> action) {
        action.accept(v);
        if (v instanceof ViewGroup) {
            ViewGroup g = (ViewGroup) v;
            for (int i = 0; i < g.getChildCount(); i++) {
                traverse(g.getChildAt(i), action);
            }
        }
    }

    private boolean getBool(String key, boolean d) {
        try {
            if (ModulePrefs.KEY_ENABLE_MODULE.equals(key)) {
                return true;
            }
            if (ModulePrefs.KEY_TOP_MENU.equals(key)) {
                return true;
            }
            if (ModulePrefs.KEY_BOTTOM_MENU.equals(key)) {
                return true;
            }
            if (ModulePrefs.KEY_STATUS_BAR_TRANSPARENT.equals(key) || ModulePrefs.KEY_BOTTOM_BAR_TRANSPARENT.equals(key)) {
                SharedPreferences current = getCurrentPrefs();
                return current != null ? current.getBoolean(ModulePrefs.KEY_OPACITY_CONTROL, d) : d;
            }
            if (ModulePrefs.KEY_VIDEO_FILTER_ENABLE.equals(key)) {
                return true;
            }
            if (ModulePrefs.KEY_OPACITY_CONTROL.equals(key)) {
                return d;
            }
            SharedPreferences current = getCurrentPrefs();
            return current != null ? current.getBoolean(key, d) : d;
        } catch (Throwable th) {
            return d;
        }
    }

    private void refreshPrefsIfNeeded(boolean force) {
    }

    private int getInt(String key, int d) {
        try {
            SharedPreferences current = getCurrentPrefs();
            return current != null ? current.getInt(key, d) : d;
        } catch (Throwable th) {
            return d;
        }
    }

    private String getString(String key, String d) {
        try {
            if (ModulePrefs.KEY_TOP_HIDE_LABELS.equals(key)) {
                return "\u63a8\u8350,\u5173\u6ce8,\u76f4\u64ad,\u540c\u57ce,\u5546\u57ce,\u56e2\u8d2d,\u70ed\u70b9,\u7ecf\u9a8c,\u7cbe\u9009";
            }
            if (ModulePrefs.KEY_BOTTOM_HIDE_LABELS.equals(key)) {
                return "\u670b\u53cb,\u53d1\u5e03";
            }
            if (ModulePrefs.KEY_VIDEO_FILTER_KEYWORDS.equals(key)) {
                return "\u5e7f\u544a\n\u63a8\u5e7f";
            }
            SharedPreferences current = getCurrentPrefs();
            if (current == null) {
                return d;
            }
            String v = current.getString(key, d);
            return v != null ? v : d;
        } catch (Throwable th) {
            return d;
        }
    }

    private SharedPreferences getCurrentPrefs() {
        return null;
    }

    private int dp(Activity a, int v) {
        return (int) ((a.getResources().getDisplayMetrics().density * v) + 0.5f);
    }

    private String readResName(View v) {
        try {
            int id = v.getId();
            return id == -1 ? "" : v.getResources().getResourceEntryName(id);
        } catch (Throwable th) {
            return "";
        }
    }

    private String readViewText(View v) {
        try {
            if (!(v instanceof TextView)) {
                return "";
            }
            TextView tv = (TextView) v;
            if (tv.getText() != null) {
                return tv.getText().toString();
            }
            return "";
        } catch (Throwable th) {
            return "";
        }
    }

    private String readContentDesc(View v) {
        try {
            CharSequence cd = v.getContentDescription();
            return cd != null ? cd.toString() : "";
        } catch (Throwable th) {
            return "";
        }
    }

}

