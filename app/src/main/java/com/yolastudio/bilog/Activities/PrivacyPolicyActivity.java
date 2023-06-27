package com.yolastudio.bilog.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.yolastudio.bilog.Adapters.LocaleHelper;
import com.yolastudio.bilog.R;

import io.paperdb.Paper;

public class PrivacyPolicyActivity extends AppCompatActivity {

    TextView toolbarTitle;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int nightModeFlags =
                PrivacyPolicyActivity.this.getResources().getConfiguration().uiMode &
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
        setContentView(R.layout.activity_privacy_policy);
        getSupportActionBar().hide();

        backBtn = findViewById(R.id.backBtn5);
        toolbarTitle = findViewById(R.id.privacyPolicyToolbarTitle);

        WebView myWebView = (WebView) findViewById(R.id.ppWebvieww);
        myWebView.loadUrl("https://sites.google.com/view/izonegalleryizprivacypolicy/home");


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        Paper.init(PrivacyPolicyActivity.this);
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

        toolbarTitle.setText(resources.getString(R.string.privacyPolicyTitle));
    }
}