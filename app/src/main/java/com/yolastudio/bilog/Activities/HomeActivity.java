package com.yolastudio.bilog.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.squareup.picasso.Picasso;
import com.yolastudio.bilog.Adapters.LocaleHelper;
import com.yolastudio.bilog.Adapters.PostAdapter;
import com.yolastudio.bilog.Adapters.RecommendationAdapter;
import com.yolastudio.bilog.Adapters.VideoAdapter;
import com.yolastudio.bilog.Models.Video;
import com.yolastudio.bilog.R;
import com.yolastudio.bilog.Models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity{

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseRemoteConfig firebaseRemoteConfig;

    public static Boolean isConnected;
    private Uri pickedImgUri = null;
    BottomNavigationView bottomNavigationView;

    AppUpdateManager appUpdateManager;
    int RequestUpdate = 1;
    ReviewManager reviewManager;
    ReviewInfo reviewInfo;

    String topRectClickStr = "", topADImgClickLink = "";

    RecyclerView postRecyclerView, recommendRecyclerview;
    NestedScrollView nestedScrollView;
    PostAdapter postAdapter;
    RecommendationAdapter rPostAdapter;
    VideoAdapter videoAdapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, videoRef, topItemRef;
    FirebaseStorage mStorage;

    List postList, recommendationList, vidList;
    ProgressBar progressBar;
    ConstraintLayout serverOverlay, internetOverlay, caughtUpCons, recommendCons, youtubeCons, latestPostsCons, topRecCons;
    TextView serverMsg, joinOCTxt, joinOCBtn, caughtUpTitle, caughtUpDesc, seePrevPostsBtn, recommendConTitle, videoConTitle, postConTitle, topRecTitle, topRecDesc;
    ImageView topADPanelImg, topRecImg;
    ViewPager vidPager;
    CircleImageView userProfileImg;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int nightModeFlags =
                HomeActivity.this.getResources().getConfiguration().uiMode &
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
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        nestedScrollView.smoothScrollTo(0, 0);
                        return true;
                    case R.id.nav_explore:
                        startActivity(new Intent(HomeActivity.this, ExploreActivity.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_wallpaper:
                        startActivity(new Intent(HomeActivity.this, WallpaperActivity.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_openchat:
                        startActivity(new Intent(HomeActivity.this, OpenChatActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return false;
                }
                return false;
            }
        });

        postRecyclerView = findViewById(R.id.postRV);
        recommendRecyclerview = findViewById(R.id.recommendationRV);
        nestedScrollView = findViewById(R.id.mainActivityNestedScroll);
        progressBar = findViewById(R.id.progressbar_home);
        serverOverlay = findViewById(R.id.cons6);
        caughtUpCons = findViewById(R.id.caughtUpCons1);
        serverMsg = findViewById(R.id.fHome_server_msg);
        internetOverlay = findViewById(R.id.noInternetFHome);
        joinOCTxt = findViewById(R.id.fHome_join_kakao_oc_txt);
        joinOCBtn = findViewById(R.id.fHome_join_kakao_oc_btn);
        caughtUpTitle = findViewById(R.id.allCaughtUpTitleTxt);
        caughtUpDesc = findViewById(R.id.allCaughtUpDescTxt);
        seePrevPostsBtn = findViewById(R.id.seePrevPostsBtn);
        vidPager = findViewById(R.id.vidViewPager);
        recommendCons = findViewById(R.id.recommendCons1);
        youtubeCons = findViewById(R.id.youtubeCons1);
        latestPostsCons = findViewById(R.id.postCons1);
        topRecCons = findViewById(R.id.topRecCons);
        recommendConTitle = findViewById(R.id.recommendationTitle);
        videoConTitle = findViewById(R.id.videoHomeTitle);
        postConTitle = findViewById(R.id.latestContentsTitle);
        topADPanelImg = findViewById(R.id.topADPanelImg);
        topRecImg = findViewById(R.id.topRecPostImg);
        topRecDesc = findViewById(R.id.topRecPostDesc);
        topRecTitle = findViewById(R.id.todaysTitleTxt);
        userProfileImg = findViewById(R.id.userProfileImg);
        swipeRefreshLayout = findViewById(R.id.homeSwipeRefreshLayout);
        postRecyclerView.setLayoutManager(new GridLayoutManager(HomeActivity.this, 1, LinearLayoutManager.VERTICAL, false));
        recommendRecyclerview.setLayoutManager(new GridLayoutManager(HomeActivity.this, 2, LinearLayoutManager.VERTICAL, false));

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getInstance().getCurrentUser();
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseDatabase.getInstance().getReference("Posts");
        videoRef = firebaseDatabase.getInstance().getReference("Video");
        topItemRef = firebaseDatabase.getInstance().getReference("TopItems");

        Paper.init(this);
        updateView((String) Paper.book().read("language"));

        progressBar.setVisibility(View.VISIBLE);
        internetOverlay.setVisibility(View.GONE);
        serverOverlay.setVisibility(View.GONE);

        Glide.with(this)
                .load(mAuth.getCurrentUser().getPhotoUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(userProfileImg);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetServerStatus();
            }
        });

        userProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        joinOCBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://open.kakao.com/o/gitPSrNc");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        seePrevPostsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ExploreActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }
        });

        topRecImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topRectClickStr.isEmpty()){
                    // do nothing
                }else{
                    Uri uri = Uri.parse(topRectClickStr);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });

        topADPanelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topADImgClickLink.isEmpty()){
                    // do nothing
                }else{
                    Uri uri = Uri.parse(topADImgClickLink);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });

        //Init default value for Firebase Remote Config
        Map<String, Object> defaultData = new HashMap<>();
        defaultData.put("server_under_maintenance", false);
        firebaseRemoteConfig.setDefaultsAsync(defaultData);

        // ini popup
        countAppOpen();
        checkConnection();
        checkforUpdate();
        GetServerStatus();
    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            firebaseRemoteConfig.activate();
                            if(firebaseRemoteConfig.getBoolean("server_under_maintenance") == true && mAuth.getCurrentUser().getEmail().equals("yolastudio05@gmail.com")){
                                // Allow access

                            }else if(firebaseRemoteConfig.getBoolean("server_under_maintenance") == true && !mAuth.getCurrentUser().getEmail().equals("yolastudio05@gmail.com")){
                                startActivity(new Intent(HomeActivity.this, ServerMaintenanceActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();
                            }else if(firebaseRemoteConfig.getBoolean("server_under_maintenance") == false){
                                //
                            }
                        }else {
                            //Firebase Remote Config is not responding
                        }
                    }
                });
        checkUserStatus();
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void countAppOpen() {
        //Count App Open
        SharedPreferences appOpen = getSharedPreferences("APPOPEN", 0);
        int appOpened = appOpen.getInt("appOpenedInt", 0);

        if(appOpened < 15){
            SharedPreferences.Editor editor = appOpen.edit();
            editor.putInt("appOpenedInt", appOpened += 1);
            editor.apply();

            if(appOpened == 4 || appOpened == 15){
                showReviewPref();
            }
        }else{
            // stop counting app opened
        }
    }

    private void showReviewPref() {
        //Show In App Review
        reviewManager = ReviewManagerFactory.create(this);

        reviewManager.requestReviewFlow().addOnCompleteListener(new com.google.android.play.core.tasks.OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(com.google.android.play.core.tasks.Task<ReviewInfo> task) {
                if(task.isSuccessful()){
                    reviewInfo = task.getResult();

                    com.google.android.play.core.tasks.Task<Void> flow = reviewManager.launchReviewFlow(HomeActivity.this, reviewInfo);
                    flow.addOnCompleteListener(new com.google.android.play.core.tasks.OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(com.google.android.play.core.tasks.Task<Void> task) {
                        }
                    });
                }
            }
        });
    }

    private void checkforUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(HomeActivity.this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new com.google.android.play.core.tasks.OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(final AppUpdateInfo result) {
                if((result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE)
                        && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){

                    //Check if update is mandatory
                    firebaseRemoteConfig.fetch(0)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        firebaseRemoteConfig.activate();
                                        if(firebaseRemoteConfig.getBoolean("update_immediate") == true ){
                                            try {
                                                appUpdateManager.startUpdateFlowForResult(result,
                                                        AppUpdateType.IMMEDIATE,
                                                        HomeActivity.this,
                                                        RequestUpdate);
                                            }catch (IntentSender.SendIntentException e){
                                                e.printStackTrace();
                                            }
                                        }else if(firebaseRemoteConfig.getBoolean("update_immediate") == false){
                                            //Do not show update immediate function
                                        }
                                    }else {
                                        //Firebase Remote Config is not responding
                                    }
                                }
                            });
                }
            }
        });
    }

    private void checkUserStatus() {

        checkConnection();

        if(isConnected){
            currentUser.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(currentUser == null) {
                        showMessage("User Disabled");
                        startActivity(new Intent(HomeActivity.this, IntroActivity.class));
                        finish();
                    }else{
                        // User is not null
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showLongMessage(e.getMessage());
                    startActivity(new Intent(HomeActivity.this, IntroActivity.class));
                    finish();
                }
            });
        }else{
            // Do not check user status if not connected
        }
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
        }
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
                            }else if(firebaseRemoteConfig.getBoolean("server_quota_exceeded") == false){
                                serverOverlay.setVisibility(View.GONE);

                                GetTopRectPanel();
                                GetRecommendationPosts();
                                GetTopADPanel();
                                GetVideos();
                                GetLatestPosts();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }else {
                            //Firebase Remote Config is not responding
                        }
                    }
                });
    }

    private void GetTopADPanel() {
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            firebaseRemoteConfig.activate();

                            Picasso.get()
                                    .load(firebaseRemoteConfig.getString("top_panel_img"))
                                    .config(Bitmap.Config.RGB_565)
                                    .into(topADPanelImg);
                            topADImgClickLink = firebaseRemoteConfig.getString("top_panel_onclick_link");

                            if(firebaseRemoteConfig.getBoolean("show_top_panel") == true){
                                topADPanelImg.setVisibility(View.VISIBLE);
                            }else if(firebaseRemoteConfig.getBoolean("show_top_panel") == false){
                                topADPanelImg.setVisibility(View.GONE);
                            }else{
                                topADPanelImg.setVisibility(View.GONE);
                            }
                        }else {
                            //Firebase Remote Config is not responding
                            topADPanelImg.setVisibility(View.GONE);
                        }
                    }
                });
    }


    private void GetVideos() {
        vidList = new ArrayList<>();
        videoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot storysnap: snapshot.getChildren()){
                    youtubeCons.setVisibility(View.VISIBLE);
                    Video video = storysnap.getValue(Video.class);
                    video.setPostKey(storysnap.getKey());
                    vidList.add(video);
                }

                videoAdapter = new VideoAdapter(HomeActivity.this, vidList);
                vidPager.setAdapter(videoAdapter);
                vidPager.setPadding(30, 5, 30, 5);
                vidPager.setNestedScrollingEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetTopRectPanel() {
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            firebaseRemoteConfig.activate();

                            topRecDesc.setText(firebaseRemoteConfig.getString("top_rec_desc"));
                            Glide.with(getApplicationContext()).load(firebaseRemoteConfig.getString("top_rec_img")).into(topRecImg);
                            topRectClickStr = firebaseRemoteConfig.getString("top_rec_onclick_link");

                            if(firebaseRemoteConfig.getBoolean("show_top_rec") == true){
                                topRecCons.setVisibility(View.VISIBLE);
                            }else if(firebaseRemoteConfig.getBoolean("show_top_rec") == false){
                                topRecCons.setVisibility(View.GONE);
                            }else{
                                topRecCons.setVisibility(View.GONE);
                            }
                        }else {
                            //Firebase Remote Config is not responding
                            topRecCons.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void GetRecommendationPosts() {
        recommendationList = new ArrayList<>();
        Query query;

        query = databaseReference
                .orderByKey()
                .limitToLast(30);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for (DataSnapshot postsnap : snapshot.getChildren()){
                        recommendCons.setVisibility(View.VISIBLE);
                        Post post = postsnap.getValue(Post.class);
                        post.setPostKey(postsnap.getKey());
                        recommendationList.add(post);
                    }

                    rPostAdapter = new RecommendationAdapter(HomeActivity.this, recommendationList);
                    recommendRecyclerview.setAdapter(rPostAdapter);
                    recommendRecyclerview.setNestedScrollingEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetLatestPosts() {
        //Get List Posts from the database
        postList = new ArrayList<>();
        Query query;

        query = databaseReference
                .orderByKey()
                .limitToLast(12);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot postsnap : snapshot.getChildren()) {
                        progressBar.setVisibility(View.GONE);
                        latestPostsCons.setVisibility(View.VISIBLE);
                        caughtUpCons.setVisibility(View.VISIBLE);

                        Post post = postsnap.getValue(Post.class);
                        post.setPostKey(postsnap.getKey());
                        postList.add(post);
                    }


                    postAdapter = new PostAdapter(HomeActivity.this, postList);
                    postRecyclerView.setAdapter(postAdapter);
                    postRecyclerView.setNestedScrollingEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(this, lang);
        Resources resources = context.getResources();

        topRecTitle.setText(resources.getString(R.string.topRecConTitle));
        recommendConTitle.setText(getResources().getString(R.string.recommendConTitle));
        videoConTitle.setText(getResources().getString(R.string.vidConTitle));
        postConTitle.setText(getResources().getString(R.string.postConTitle));

        caughtUpTitle.setText(getResources().getString(R.string.caughtUpTitle));
        caughtUpDesc.setText(getResources().getString(R.string.caughtUpDesc));
        seePrevPostsBtn.setText(getResources().getString(R.string.caughtUpSeeOldPosts));

        serverMsg.setText(resources.getString(R.string.serverDownMsg));
        joinOCBtn.setText(getResources().getString(R.string.joinOpenChatTxt));
        joinOCTxt.setText(getResources().getString(R.string.joinOCServerDownMsg));
    }

    private void showMessage(String message) {
        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLongMessage(String message) {
        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG).show();
    }
}