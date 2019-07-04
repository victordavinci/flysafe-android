package ar.gob.jiaac.flysafe.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ar.gob.jiaac.flysafe.R;

public class TermsAndConditionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        TextView terms = findViewById(R.id.termsLabel);
        terms.setMovementMethod(new ScrollingMovementMethod());

        Button accept = findViewById(R.id.btnAccept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("ar.gob.jiaac.flysafe", 0);
                sp.edit().putBoolean("TermsAndConditions", true).apply();
                Intent home = new Intent(TermsAndConditionsActivity.this, HomeActivity.class);
                TermsAndConditionsActivity.this.startActivity(home);
                TermsAndConditionsActivity.this.finish();
            }
        });

        Button reject = findViewById(R.id.btnReject);
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
