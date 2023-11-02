package me.weishu.anylauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import java.util.concurrent.atomic.AtomicBoolean;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author weishu
 * @date 2023/11/2.
 */
public final class Entry implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        hookMiuiHome(lpparam.packageName, lpparam.classLoader);
        hookSystemUi(lpparam.packageName, lpparam.classLoader);
    }

    private void hookSystemUi(String pkg, ClassLoader classLoader) {
        if (!"com.android.systemui".equals(pkg)) {
            return;
        }

        XposedHelpers.findAndHookMethod("com.android.systemui.assist.PhoneStateMonitor", classLoader, "onDefaultHomeChanged", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(null);
            }
        });
    }

    private void hookMiuiHome(String pkg, ClassLoader classLoader) {
        if (!"com.miui.home".equals(pkg)) {
            return;
        }

        try {
            // make ourself be treated as default home
            XposedHelpers.findAndHookMethod("com.miui.home.launcher.common.Utilities", classLoader, "isUsePocoHomeAsDefaultHome", android.content.Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(true);
                }
            });

            XposedHelpers.findAndHookMethod("com.miui.home.recents.BaseRecentsImpl", classLoader, "setIsUseMiuiHomeAsDefaultHome", boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    log("setIsUseMiuiHomeAsDefaultHome: " + param.args[0]);
                    param.setResult(null);
                }
            });

            XposedHelpers.findAndHookMethod("com.miui.home.recents.BaseRecentsImpl", classLoader, "updateFsgWindowState", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    boolean mIsUseMiuiHomeAsDefaultHome = XposedHelpers.getBooleanField(param.thisObject, "mIsUseMiuiHomeAsDefaultHome");
                    log("mIsUseMiuiHomeAsDefaultHome: " + mIsUseMiuiHomeAsDefaultHome);
                    XposedHelpers.setBooleanField(param.thisObject, "mIsUseMiuiHomeAsDefaultHome", true);
                }
            });

            XposedHelpers.findAndHookMethod("com.miui.home.recents.BaseRecentsImpl", classLoader, "updateUseLauncherRecentsAndFsGesture", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedHelpers.setBooleanField(param.thisObject, "mIsUseMiuiHomeAsDefaultHome", true);
                }
            });

            AtomicBoolean isRecent = new AtomicBoolean(false);
            XposedHelpers.findAndHookMethod("com.miui.home.recents.NavStubView", classLoader, "performAppToHome", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    log("performAppToHome");
                    View view = (View) param.thisObject;
                    Context context = view.getContext();

                    log("context: " + context);

                    // TODO: Add animation!
                    Runnable runnable = () -> {
                        if (isRecent.get()) {
                            isRecent.set(false);
                            Intent intent = new Intent();
                            ComponentName componentName = ComponentName.unflattenFromString("com.miui.home/.recents.RecentsActivity");
                            intent.setComponent(componentName);

                            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } else {
                            Intent startMain = new Intent(Intent.ACTION_MAIN);
                            startMain.addCategory(Intent.CATEGORY_HOME);
                            startMain.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(startMain);
                        }
                    };

                    view.postDelayed(runnable, 100);
                    XposedHelpers.callMethod(param.thisObject, "startAppToHomeAnim");
                }
            });

            XposedHelpers.findAndHookMethod("com.miui.home.recents.NavStubView", classLoader, "performAppToRecents", boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    log("performAppToRecents");
                    isRecent.set(true);
                }
            });

        } catch (Throwable e) {
            log("hookMiuiHome: ", e);
        }
    }

    private static final String TAG = "AnyLauncher";

    public static void log(CharSequence msg) {
        if (msg == null) {
            return;
        }
        Log.i(TAG, msg.toString());
    }

    public static void log(CharSequence msg, Throwable th) {
        if (msg == null) {
            return;
        }
        Log.i(TAG, msg.toString(), th);
    }
}
