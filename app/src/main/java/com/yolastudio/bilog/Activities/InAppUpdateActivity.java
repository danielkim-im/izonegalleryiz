package com.yolastudio.bilog.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.yolastudio.bilog.Adapters.LocaleHelper;
import com.yolastudio.bilog.BuildConfig;
import com.yolastudio.bilog.R;

import io.paperdb.Paper;

public class InAppUpdateActivity extends AppCompatActivity {

    TextView appVersionTxt, usingLatestVersionTxt, toolbarTitle;
    ImageView backBtn;
    AppUpdateManager appUpdateManager;
    ProgressBar progressBar;
    int RequestUpdate = 1;
    String versionName = BuildConfig.VERSION_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int nightModeFlags =
                InAppUpdateActivity.this.getResources().getConfiguration().uiMode &
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
        setContentView(R.layout.activity_in_app_update);
        getSupportActionBar().hide();

        appVersionTxt = findViewById(R.id.appVersionTxt);
        usingLatestVersionTxt = findViewById(R.id.usingLatestVersionTxt);
        backBtn = findViewById(R.id.backBtn4);
        toolbarTitle = findViewById(R.id.iauToolbarTitle);
        progressBar = findViewById(R.id.iauProgressbar);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        Paper.init(this);
        updateView((String) Paper.book().read("language"));

        checkforUpdate();
    }

    private void checkforUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(InAppUpdateActivity.this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new com.google.android.play.core.tasks.OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(final AppUpdateInfo result) {
                progressBar.setVisibility(View.GONE);
                if ((result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE)
                        && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(result,
                                AppUpdateType.IMMEDIATE,
                                InAppUpdateActivity.this,
                                RequestUpdate);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }else{
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(this, lang);
        Resources resources = context.getResources();

        toolbarTitle.setText(resources.getString(R.string.iauTitle));
        appVersionTxt.setText(resources.getString(R.string.currentVersion)+ " " +versionName);
        usingLatestVersionTxt.setText(resources.getString(R.string.latestVersion));
    }
}