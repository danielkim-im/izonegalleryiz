package com.yolastudio.bilog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.yolastudio.bilog.Adapters.ExplorePostAdapter;
import com.yolastudio.bilog.Adapters.LocaleHelper;
import com.yolastudio.bilog.Adapters.RecommendationAdapter;
import com.yolastudio.bilog.Models.Post;
import com.yolastudio.bilog.R;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class ExploreActivity extends AppCompatActivity {

    RecyclerView postRecyclerView;
    ExplorePostAdapter postAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage mStorage;
    FirebaseRemoteConfig firebaseRemoteConfig;
    List postList;

    public static Boolean isConnected;
    ProgressBar progressBar;
    TextView galleryiz, groupphoto, eunbi, sakura, hyewon, yena, chaeyeon, chaewon, minju, nako, hitomi, yuri, yujin, wonyoung;
    TextView serverMsg, joinOCTxt, joinOCBtn, toolbarTitle;
    ConstraintLayout serverOverlay, internetOverlay;
    StaggeredGridLayoutManager manager;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int nightModeFlags =
                ExploreActivity.this.getResources().getConfiguration().uiMode &
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
        setContentView(R.layout.activity_explore);
        getSupportActionBar().hide();

        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.nav_explore);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        startActivity(new Intent(ExploreActivity.this, HomeActivity.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_explore:
                        postRecyclerView.smoothScrollToPosition(0);
                        return true;
                    case R.id.nav_wallpaper:
                        startActivity(new Intent(ExploreActivity.this, WallpaperActivity.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_openchat:
                        startActivity(new Intent(ExploreActivity.this, OpenChatActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return false;
                }
                return false;
            }
        });

        postRecyclerView = findViewById(R.id.searchRV);
        postRecyclerView.setHasFixedSize(true);
        progressBar = findViewById(R.id.progressbar_explore);
        serverOverlay = findViewById(R.id.cons7);
        internetOverlay = findViewById(R.id.noInternetFExplore);

        manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        postRecyclerView.setLayoutManager(manager);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseDatabase.getInstance().getReference("Posts");

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        serverMsg = findViewById(R.id.fExplore_server_msg);
        joinOCTxt = findViewById(R.id.fExplore_join_kakao_oc_txt);
        joinOCBtn = findViewById(R.id.fExplore_join_kakao_oc_btn);
        toolbarTitle = findViewById(R.id.toolbarDiscoverTitleTxt);

        //ScrollBtn
        galleryiz = findViewById(R.id.galleryiz);
        groupphoto = findViewById(R.id.group_photo);
        eunbi = findViewById(R.id.eunbi);
        sakura = findViewById(R.id.sakura);
        hyewon = findViewById(R.id.hyewon);
        yena = findViewById(R.id.yena);
        chaeyeon = findViewById(R.id.chaeyeon);
        chaewon = findViewById(R.id.chaewon);
        minju = findViewById(R.id.minju);
        nako = findViewById(R.id.nako);
        hitomi = findViewById(R.id.hitomi);
        yuri = findViewById(R.id.yuri);
        yujin = findViewById(R.id.yujin);
        wonyoung = findViewById(R.id.wonyoung);


        galleryiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadPosts();
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

        progressBar.setVisibility(View.VISIBLE);
        internetOverlay.setVisibility(View.GONE);

        joinOCBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://open.kakao.com/o/gitPSrNc");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        Paper.init(this);
        updateView((String) Paper.book().read("language"));

        checkConnection();
        GetServerStatus();
    }

    private void GetServerStatus() {
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            firebaseRemoteConfig.activate();
                            if(firebaseRemoteConfig.getBoolean("server_quota_exceeded") == true ){
                                serverOverlay.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }else if(firebaseRemoteConfig.getBoolean("server_quota_exceeded") == false){
                                serverOverlay.setVisibility(View.GONE);
                                LoadPosts();
                            }
                        }else {
                            //Firebase Remote Config is not responding
                        }
                    }
                });
    }

    private void LoadPosts(){
        postList = new ArrayList<>();
        Query query;

        query = databaseReference
                .orderByKey()
                .limitToLast(700);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for (DataSnapshot postsnap : snapshot.getChildren()){
                        progressBar.setVisibility(View.GONE);
                        Post post = postsnap.getValue(Post.class);
                        post.setPostKey(postsnap.getKey());
                        postList.add(post);
                    }

                    postAdapter = new ExplorePostAdapter(ExploreActivity.this, postList);
                    postRecyclerView.setAdapter(postAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SearchPosts(final String query) {
        //Get Searched List Posts from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();
                for (DataSnapshot postsnap: dataSnapshot.getChildren()){
                    Post post = postsnap.getValue(Post.class);

                    if(post.getTitle().toLowerCase().contains(query.toLowerCase())){
                        postList.add(post);
                    }
                }

                postAdapter = new ExplorePostAdapter(ExploreActivity.this, postList);
                postAdapter.notifyDataSetChanged();
                postRecyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateView(String lang){
        Context context = LocaleHelper.setLocale(ExploreActivity.this, lang);
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

        toolbarTitle.setText(resources.getString(R.string.discoverTBTitle));
        serverMsg.setText(resources.getString(R.string.serverDownMsg));
        joinOCBtn.setText(getResources().getString(R.string.joinOpenChatTxt));
        joinOCTxt.setText(getResources().getString(R.string.joinOCServerDownMsg));
    }

    public void checkConnection(){
        ConnectivityManager manager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if(null != activeNetwork){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                isConnected = true;
            }

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                isConnected = true;
            }
        }
        else{
            isConnected = false;
            progressBar.setVisibility(View.GONE);
            internetOverlay.setVisibility(View.VISIBLE);
        }
    }

    private void showMessage(String message) {
        Toast.makeText(ExploreActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}