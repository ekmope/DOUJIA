package com.ss.android.ugc.awemes;

import android.util.Log;

import com.android.admin.module.DouyinMigratedHooks;

import java.util.Set;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface;

public class ModuleMain extends XposedModule {
    private static final String TAG = "DouPlusApi101";
    private static final Set<String> TARGET_SCOPES = Set.of("com.ss.android.ugc.aweme");

    @Override
    public void onModuleLoaded(XposedModuleInterface.ModuleLoadedParam param) {
        log(
                Log.INFO,
                TAG,
                "Module loaded in process=" + param.getProcessName()
                        + ", framework=" + getFrameworkName() + " " + getFrameworkVersion()
        );
    }

    @Override
    public void onPackageLoaded(XposedModuleInterface.PackageLoadedParam param) {
        if (!param.isFirstPackage()) {
            return;
        }
        if (!TARGET_SCOPES.contains(param.getPackageName())) {
            return;
        }
        log(
                Log.INFO,
                TAG,
                "Package loaded=" + param.getPackageName()
                        + ", fixedConfig"
                        + " topMenu=[同城,商城,团购,热点,经验,精选]"
                        + " bottomMenu=[朋友,发布]"
        );

        new DouyinMigratedHooks(this, null, param.getDefaultClassLoader()).install();
    }
}