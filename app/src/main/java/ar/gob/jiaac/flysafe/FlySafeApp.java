package ar.gob.jiaac.flysafe;

import android.app.Application;

import com.evernote.android.state.StateSaver;

public class FlySafeApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        StateSaver.setEnabledForAllActivitiesAndSupportFragments(this, true);
    }
}
