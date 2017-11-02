package com.iwillow.app.android.interview;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;

/**
 * Created by Administrator on 2017/11/2.
 */

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Log.d(activity.getClass().getSimpleName(), "onActivityCreated");

        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.d(activity.getClass().getSimpleName(), "onActivityStarted");
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.d(activity.getClass().getSimpleName(), "onActivityResumed");
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.d(activity.getClass().getSimpleName(), "onActivityPaused");
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.d(activity.getClass().getSimpleName(), "onActivityStopped");
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Log.d(activity.getClass().getSimpleName(), "onActivitySaveInstanceState");
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.d(activity.getClass().getSimpleName(), "onActivityDestroyed");
        }
    }
}
