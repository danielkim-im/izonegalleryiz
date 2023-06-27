package com.yolastudio.bilog.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yolastudio.bilog.Adapters.LocaleHelper;
import com.yolastudio.bilog.R;

import io.paperdb.Paper;

public class LanguageActivity extends AppCompatActivity {

    ConstraintLayout engBtn, krBtn, jpBtn;
    TextView toolbarTitle;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int nightModeFlags =
                LanguageActivity.this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                setTheme(R.style.darkTheme);
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                setTheme(R.style.AppTheme);
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                setTheme(R.style.AppTheme);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        getSupportActionBar().hide();

        engBtn = findViewById(R.id.engBtnCons);
        krBtn = findViewById(R.id.krBtnCons);
        jpBtn = findViewById(R.id.jpBtnCons);
        toolbarTitle = findViewById(R.id.languageToolbarTitle);
        backBtn = findViewById(R.id.backBtn6);

        engBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //English
                Paper.book().write("language", "en");
                updateView((String)Paper.book().read("language"));
                startActivity(new Intent(LanguageActivity.this, IntroActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        krBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Korean
                Paper.book().write("language", "ko");
                updateView((String)Paper.book().read("language"));
                startActivity(new Intent(LanguageActivity.this, IntroActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        jpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Japanese
                Paper.book().write("language", "ja");
                updateView((String)Paper.book().read("language"));
                startActivity(new Intent(LanguageActivity.this, IntroActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        Paper.init(LanguageActivity.this);
        updateView((String) Paper.book().read("language"));
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void updateView(String lang){
        Context context = LocaleHelper.setLocale(this, lang);
        Resources resources = context.getResources();

        toolbarTitle.setText(resources.getString(R.string.languagePrefTitle));
    }
}