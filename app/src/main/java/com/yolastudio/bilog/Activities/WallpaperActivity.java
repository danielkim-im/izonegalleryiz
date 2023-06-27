package com.yolastudio.bilog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.yolastudio.bilog.Adapters.LocaleHelper;
import com.yolastudio.bilog.Adapters.WallpaperAdapter;
import com.yolastudio.bilog.Models.Wallpaper;
import com.yolastudio.bilog.R;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class WallpaperActivity extends AppCompatActivity {

    TextView galleryiz, groupphoto, eunbi, sakura, hyewon, yena, chaeyeon, chaewon, minju, nako, hitomi, yuri, yujin, wonyoung;
    TextView toolbarTitle, serverMsg;
    ProgressBar progressBar;
    RecyclerView wallpaperRecyclerview;
    WallpaperAdapter wallpaperAdapter;
    ConstraintLayout serverOverlay;
    BottomNavigationView bottomNavigationView;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseRemoteConfig firebaseRemoteConfig;
    List wallpaperList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int nightModeFlags =
                WallpaperActivity.this.getResources().getConfiguration().uiMode &
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
        setContentView(R.layout.activity_wallpaper);
        getSupportActionBar().hide();

        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.nav_wallpaper);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        startActivity(new Intent(WallpaperActivity.this, HomeActivity.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_explore:
                        startActivity(new Intent(WallpaperActivity.this, ExploreActivity.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_wallpaper:
                        wallpaperRecyclerview.smoothScrollToPosition(0);
                        return true;
                    case R.id.nav_openchat:
                        startActivity(new Intent(WallpaperActivity.this, OpenChatActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return false;
                }
                return false;
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getInstance().getReference("Wallpapers");
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        //ScrollBtn
        galleryiz = findViewById(R.id.galleryizw);
        groupphoto = findViewById(R.id.group_photow);
        eunbi = findViewById(R.id.eunbiw);
        sakura = findViewById(R.id.sakuraw);
        hyewon = findViewById(R.id.hyewonw);
        yena = findViewById(R.id.yenaw);
        chaeyeon = findViewById(R.id.chaeyeonw);
        chaewon = findViewById(R.id.chaewonw);
        minju = findViewById(R.id.minjuw);
        nako = findViewById(R.id.nakow);
        hitomi = findViewById(R.id.hitomiw);
        yuri = findViewById(R.id.yuriw);
        yujin = findViewById(R.id.yujinw);
        wonyoung = findViewById(R.id.wonyoungw);

        toolbarTitle = findViewById(R.id.toolbarWallpaperTitle);
        progressBar = findViewById(R.id.progressbar_wallpaper);
        serverOverlay = findViewById(R.id.cons12);
        serverMsg = findViewById(R.id.fWallpaper_server_msg);
        wallpaperRecyclerview = findViewById(R.id.rv_wallpaper);
        wallpaperRecyclerview.setHasFixedSize(true);
        wallpaperRecyclerview.setLayoutManager(new GridLayoutManager(WallpaperActivity.this, 3, LinearLayoutManager.VERTICAL, false));

        galleryiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetWallpapers();
            }
        });
        groupphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPosts("Group Photo");
            }
        });
        eunbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPosts("Eunbi");
            }
        });
        sakura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPosts("Sakura");
            }
        });
        hyewon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPosts("Hyewon");
            }
        });
        yena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPosts("Yena");
            }
        });
        chaeyeon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPosts("Chaeyeon");
            }
        });
        chaewon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPosts("Chaewon");
            }
        });
        minju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPosts("Minju");
            }
        });
        nako.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPosts("Nako");
            }
        });
        hitomi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPosts("Hitomi");
            }
        });
        yuri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPosts("Yuri");
            }
        });
        yujin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPosts("Yujin");
            }
        });
        wonyoung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPosts("Wonyoung");
            }
        });

        Paper.init(WallpaperActivity.this);
        updateView((String) Paper.book().read("language"));

        GetServerStatus();
    }

    private void GetServerStatus() {
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            firebaseRemoteConfig.activate();
                            if(firebaseRemoteConfig.getBoolean("wallpaper_shut_down") == true ){
                                serverOverlay.setVisibility(View.VISIBLE);
                            }else if(firebaseRemoteConfig.getBoolean("server_quota_exceeded") == false){
                                serverOverlay.setVisibility(View.GONE);
                                GetWallpapers();
                            }
                        }else {
                            //Firebase Remote Config is not responding
                        }
                    }
                });
    }

    private void GetWallpapers() {
        //Get List Posts from the database
        wallpaperList = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot wallpaperSnap: dataSnapshot.getChildren()){
                    progressBar.setVisibility(View.GONE);
                    Wallpaper wallpaper = wallpaperSnap.getValue(Wallpaper.class);
                    wallpaper.setPostKey(wallpaperSnap.getKey());
                    wallpaperList.add(wallpaper);
                }

                wallpaperAdapter = new WallpaperAdapter(WallpaperActivity.this, wallpaperList);
                wallpaperRecyclerview.setAdapter(wallpaperAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void SearchPosts(final String query) {
        //Get Searched List Posts from the database
        wallpaperList = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot wallpapersnap: dataSnapshot.getChildren()){
                    Wallpaper wallpaper = wallpapersnap.getValue(Wallpaper.class);

                    if(wallpaper.getTitle().toLowerCase().contains(query.toLowerCase())){
                        wallpaperList.add(wallpaper);
                    }
                }

                wallpaperAdapter = new WallpaperAdapter(WallpaperActivity.this, wallpaperList);
                wallpaperAdapter.notifyDataSetChanged();
                wallpaperRecyclerview.setAdapter(wallpaperAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateView(String lang){
        Context context = LocaleHelper.setLocale(WallpaperActivity.this, lang);
        Resources resources = context.getResources();

        //searchTxt.setHint(resources.getString(R.string.searchTxt));
        galleryiz.setText(resources.getString(R.string.galleryiz));
        groupphoto.setText(resources.getString(R.string.groupphoto));
        eunbi.setText(resources.getString(R.string.eunbi));
        sakura.setText(resources.getString(R.string.sakura));
        hyewon.setText(resources.getString(R.string.hyewon));
        yena.setText(resources.getString(R.string.yena));
        chaeyeon.setText(resources.getString(R.string.chaeyeon));
        chaewon.setText(resources.getString(R.string.chaewon));
        minju.setText(resources.getString(R.string.minju));
        nako.setText(resources.getString(R.string.nako));
        hitomi.setText(resources.getString(R.string.hitomi));
        yuri.setText(resources.getString(R.string.yuri));
        yujin.setText(resources.getString(R.string.yujin));
        wonyoung.setText(resources.getString(R.string.wonyoung));

        toolbarTitle.setText(resources.getString(R.string.wallpaper_toolbar_title));
        serverMsg.setText(resources.getString(R.string.serverDownMsg));
    }
}