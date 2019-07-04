package ar.gob.jiaac.flysafe.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import ar.gob.jiaac.flysafe.R;

public class SplashActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent activity;
                SharedPreferences sp = getSharedPreferences("ar.gob.jiaac.flysafe", 0);
                if (sp.getBoolean("TermsAndConditions", false)) {
                    activity = new Intent(SplashActivity.this, HomeActivity.class);
                } else {
                    activity = new Intent(SplashActivity.this, TermsAndConditionsActivity.class);
                }
                SplashActivity.this.startActivity(activity);
                SplashActivity.this.finish();
            }
        }, 3000);
    }

}
