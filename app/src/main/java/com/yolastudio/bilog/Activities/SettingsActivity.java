package com.yolastudio.bilog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.yolastudio.bilog.Adapters.LocaleHelper;
import com.yolastudio.bilog.R;

import io.paperdb.Paper;

public class SettingsActivity extends AppCompatActivity {

    TextView toolbarTitle, langPrefTitle, checkForUpdateTitle, signOutTitle, privacyPolictTitle;
    ImageView backBtn;
    ConstraintLayout signOutCons, langPrefCons, checkUpdateCons, ppCons;
    String signoutquestion, signout, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int nightModeFlags =
                SettingsActivity.this.getResources().getConfiguration().uiMode &
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
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();

        signOutCons = findViewById(R.id.signOutCons);
        langPrefCons = findViewById(R.id.langPrefCons);
        checkUpdateCons = findViewById(R.id.checkUpdateCons);
        ppCons = findViewById(R.id.privacyPolicyCons);
        langPrefTitle = findViewById(R.id.languagePrefTitle);
        backBtn = findViewById(R.id.backBtn3);
        toolbarTitle = findViewById(R.id.settingsToolbarTitle);
        signOutTitle = findViewById(R.id.signOutTitle);
        checkForUpdateTitle = findViewById(R.id.updateTitle);
        privacyPolictTitle = findViewById(R.id.privacyPolicyTxt);

        signOutCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setTitle(signoutquestion)
                        .setPositiveButton(signout, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                logout();
                            }
                        }).setNegativeButton(cancel, null);

                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        langPrefCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, LanguageActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        checkUpdateCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, InAppUpdateActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        ppCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, PrivacyPolicyActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        Paper.init(SettingsActivity.this);
        updateView((String) Paper.book().read("language"));
    }

    public void logout(){

        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                build();

        GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(this,gso);
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(SettingsActivity.this, IntroActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                }
            }
        });
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void updateView(String lang){
        Context context = LocaleHelper.setLocale(this, lang);
        Resources resources = context.getResources();

        checkForUpdateTitle.setText(resources.getString(R.string.checkUpdateTitle));
        langPrefTitle.setText(resources.getString(R.string.languagePrefTitle));
        privacyPolictTitle.setText(resources.getString(R.string.privacyPolicyTitle));
        toolbarTitle.setText(resources.getString(R.string.settingsTitle));
        signOutTitle.setText(resources.getString(R.string.signOutTitle));
        signoutquestion = resources.getString(R.string.dialog_signout_title);
        signout = resources.getString(R.string.dialog_signout_signout);
        cancel = resources.getString(R.string.dialog_signout_cancel);
    }
}