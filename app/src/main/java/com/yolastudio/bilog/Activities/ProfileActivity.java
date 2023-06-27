package com.yolastudio.bilog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yolastudio.bilog.Adapters.LocaleHelper;
import com.yolastudio.bilog.R;

import io.paperdb.Paper;

public class ProfileActivity extends AppCompatActivity {

    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    Button openInsta, openGooglePlay, kakaoBtn;
    TextView aboutDesc, toolbarTitle;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int nightModeFlags =
                ProfileActivity.this.getResources().getConfiguration().uiMode &
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
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        TextView userName = findViewById(R.id.profile_username);
        TextView userEmail = findViewById(R.id.profile_user_email);
        TextView userUid = findViewById(R.id.userIDTxt);
        ImageView userProfileImg = findViewById(R.id.profile_profileimg);
        CardView settingsBtn = findViewById(R.id.cardview5);
        toolbarTitle = findViewById(R.id.profileToolbarTitle);
        backBtn = findViewById(R.id.backBtn7);
        aboutDesc = findViewById(R.id.aboutDesc);
        kakaoBtn = findViewById(R.id.openKakaoOCBtn);
        openInsta = findViewById(R.id.openInsta);
        openGooglePlay = findViewById(R.id.openGooglePlay);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        openInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.instagram.com/yolastudio_official/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        openGooglePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.yolastudio.bilog");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        kakaoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://open.kakao.com/o/gitPSrNc");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        userUid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("UID", mAuth.getCurrentUser().getUid());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ProfileActivity.this, "UID Copied to Clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(ProfileActivity.this);
        if(acct != null){
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            Uri personPhoto = acct.getPhotoUrl();
        }

        userName.setText(mAuth.getCurrentUser().getDisplayName());
        userEmail.setText(mAuth.getCurrentUser().getEmail());
        userUid.setText(mAuth.getCurrentUser().getUid());
        Glide.with(this)
                .load(mAuth.getCurrentUser().getPhotoUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(userProfileImg);

        Paper.init(ProfileActivity.this);
        updateView((String) Paper.book().read("language"));
    }

    private void updateView(String lang){
        Context context = LocaleHelper.setLocale(ProfileActivity.this, lang);
        Resources resources = context.getResources();

        toolbarTitle.setText(resources.getString(R.string.profileToolbarTitle));
        aboutDesc.setText(resources.getString(R.string.aboutDesc));
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}