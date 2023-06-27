package com.yolastudio.bilog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.yolastudio.bilog.R;

import java.util.Random;

public class SplashScreen extends AppCompatActivity {

    ImageView splashImgView;
    FirebaseRemoteConfig firebaseRemoteConfig;

    String customSplashImgUrl;

    int[] images = {R.drawable.chaewon,R.drawable.yuri,R.drawable.yena,R.drawable.hitomi
            ,R.drawable.hyewon,R.drawable.chaeyeon,R.drawable.eunbi,R.drawable.nako
            ,R.drawable.minju,R.drawable.sakura,R.drawable.wonyoung,R.drawable.yujin};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        getSupportActionBar().hide();
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Black));

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        splashImgView = findViewById(R.id.splashImgView);

        iniSplash();
    }

    private void iniSplash() {
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            firebaseRemoteConfig.activate();
                            if(firebaseRemoteConfig.getBoolean("custom_splash_screen") == true ){
                                LoadCustomSplash();
                            }else if(firebaseRemoteConfig.getBoolean("custom_splash_screen") == false){
                                Random rand = new Random();
                                splashImgView.setImageResource(images[rand.nextInt(images.length)]);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent afterSplashScreen = new Intent(SplashScreen.this, IntroActivity.class);
                                        startActivity(afterSplashScreen);
                                        finish();
                                    }
                                }, 1000);
                            }
                        }else {
                            //Firebase Remote Config is not responding
                        }
                    }
                });
    }

    private void LoadCustomSplash() {
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            firebaseRemoteConfig.activate();
                            customSplashImgUrl = firebaseRemoteConfig.getString("splash_screen_img_url");
                            Glide.with(SplashScreen.this).load(customSplashImgUrl).into(splashImgView);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent afterSplashScreen = new Intent(SplashScreen.this, IntroActivity.class);
                                    startActivity(afterSplashScreen);
                                    finish();
                                }
                            }, 1350);
                        }else {
                            //Firebase Remote Config is not responding
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent afterSplashScreen = new Intent(SplashScreen.this, IntroActivity.class);
                                    startActivity(afterSplashScreen);
                                    finish();
                                }
                            }, 1350);
                        }
                    }
                });
    }
}

