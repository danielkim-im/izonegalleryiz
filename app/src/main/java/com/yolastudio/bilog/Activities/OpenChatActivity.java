package com.yolastudio.bilog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.yolastudio.bilog.Adapters.LocaleHelper;
import com.yolastudio.bilog.Adapters.OpenChatAdapter;
import com.yolastudio.bilog.Models.CafeComment;
import com.yolastudio.bilog.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class OpenChatActivity extends AppCompatActivity {

    ImageView internetOverlay, backBtn, ryanImg, toolbarOCBtn;
    EditText editTextComment;
    ImageButton btnAddComment;
    TextView topPanelTxt, serverMsg, joinOCTxtPopup, closeOCDialogTxt, ocTitleTxt, ocDescTxt;
    RecyclerView cafeCommentRecyclerview;
    ProgressBar progressBar;
    Dialog popjoinOC;
    OpenChatAdapter cafeCommentAdapter;
    List<CafeComment> listCafeComment;
    static String COMMENT_KEY = "CafeComment";

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    DatabaseReference commentRef;
    FirebaseRemoteConfig firebaseRemoteConfig;

    ConstraintLayout serverOverlay;
    Boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int nightModeFlags =
                OpenChatActivity.this.getResources().getConfiguration().uiMode &
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
        setContentView(R.layout.activity_open_chat);
        getSupportActionBar().hide();

        cafeCommentRecyclerview = findViewById(R.id.rv_cafe_comment);
        editTextComment = findViewById(R.id.cafe_comment_edittext);
        btnAddComment = findViewById(R.id.cafe_addComment_btn);
        progressBar = findViewById(R.id.progressbar_cafe);
        internetOverlay = findViewById(R.id.noInternetFCafe);
        topPanelTxt = findViewById(R.id.topPanelTitle1);
        backBtn = findViewById(R.id.backBtn2);
        serverOverlay = findViewById(R.id.cons9);
        serverMsg = findViewById(R.id.OC_server_msg);
        toolbarOCBtn = findViewById(R.id.toolbarOCBtn);

        cafeCommentRecyclerview.setLayoutManager(new GridLayoutManager(OpenChatActivity.this, 1, LinearLayoutManager.VERTICAL, true));
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        commentRef= firebaseDatabase.getReference(COMMENT_KEY);
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        popjoinOC = new Dialog(this);
        popjoinOC.setContentView(R.layout.popup_join_openchat);
        popjoinOC.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popjoinOC.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popjoinOC.getWindow().getAttributes().gravity = Gravity.CENTER;

        // ini popup widgets
        ryanImg = popjoinOC.findViewById(R.id.ryanImg);
        joinOCTxtPopup = popjoinOC.findViewById(R.id.joinOPTxtPopup);
        closeOCDialogTxt = popjoinOC.findViewById(R.id.closeOCdialogTxt);
        ocTitleTxt = popjoinOC.findViewById(R.id.ocTitleTxt);
        ocDescTxt = popjoinOC.findViewById(R.id.ocDescTxt);

        // add comment button click listener
        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String commentTxt = editTextComment.getText().toString();

                checkConnection();

                if(isConnected){
                    if(TextUtils.isEmpty(commentTxt)){
                        //do nothing
                    }else{
                        btnAddComment.setVisibility(View.INVISIBLE);
                        DatabaseReference commentReference = firebaseDatabase.getReference(COMMENT_KEY).push();
                        String content = editTextComment.getText().toString();
                        String uid = firebaseUser.getUid();
                        String uname = firebaseUser.getDisplayName();
                        String uimg = firebaseUser.getPhotoUrl().toString();
                        CafeComment cafeComment = new CafeComment(content, uid, uimg, uname);

                        commentReference.setValue(cafeComment).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //showMessage("Comment Added");
                                editTextComment.setText("");
                                btnAddComment.setVisibility(View.VISIBLE);
                                checkUserStatus();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessage("Failed to add comment: "+e.getMessage());
                                checkUserStatus();
                            }
                        });
                    }
                }else if(isConnected = false){
                    showMessage("No Internet Connection");
                }
            }
        });

        toolbarOCBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://open.kakao.com/o/gitPSrNc");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        internetOverlay.setVisibility(View.GONE);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        //ini Recyclerview Comment
        GetServerStatus();
        checkConnection();
        ocPopup();

        Paper.init(OpenChatActivity.this);
        updateView((String) Paper.book().read("language"));
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkUserStatus();
    }

    public void checkConnection(){
        ConnectivityManager manager = (ConnectivityManager)
                OpenChatActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    private void GetComment() {

        listCafeComment = new ArrayList<>();
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    progressBar.setVisibility(View.GONE);
                    CafeComment cafeComment = dataSnapshot.getValue(CafeComment.class);
                    listCafeComment.add(cafeComment);
                }

                cafeCommentAdapter = new OpenChatAdapter(OpenChatActivity.this, listCafeComment);
                cafeCommentRecyclerview.setAdapter(cafeCommentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Count App Open
        SharedPreferences joinOC = getSharedPreferences("JOINOCPOPUP", 0);
        int joinocShowed = joinOC.getInt("joinOCShownInt", 0);

        if(joinocShowed < 10){
            SharedPreferences.Editor editor = joinOC.edit();
            editor.putInt("joinOCShownInt", joinocShowed += 1);
            editor.apply();

            if(joinocShowed == 1 || joinocShowed == 10){
                if(Paper.book().read("language").equals("ko")){
                    popjoinOC.show();
                }else{
                    // Do not show Join KakaoTalk OC
                }
            }
        }else{
            // stop counting
        }
    }

    private void checkUserStatus() {
        checkConnection();

        if(isConnected){
            firebaseUser.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(firebaseUser == null) {
                        showMessage("User Disabled");
                        startActivity(new Intent(OpenChatActivity.this, IntroActivity.class));
                        finish();
                    }else{
                        // User is not null
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showLongMessage(e.getMessage());
                    startActivity(new Intent(OpenChatActivity.this, IntroActivity.class));
                    finish();
                }
            });
        }else{
            // Do not check user status if not connected
        }
    }

    private void GetServerStatus() {
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            firebaseRemoteConfig.activate();
                            if(firebaseRemoteConfig.getBoolean("open_chat_shutdown") == true ){
                                serverOverlay.setVisibility(View.VISIBLE);
                            }else if(firebaseRemoteConfig.getBoolean("open_chat_shutdown") == false){
                                serverOverlay.setVisibility(View.GONE);
                                GetComment();
                            }
                        }else {
                            //Firebase Remote Config is not responding
                        }
                    }
                });
    }

    private void ocPopup(){
        Glide.with(this).load(R.drawable.ryan_kakao).into(ryanImg);

        joinOCTxtPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popjoinOC.dismiss();
                Uri uri = Uri.parse("https://open.kakao.com/o/gitPSrNc");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        closeOCDialogTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popjoinOC.dismiss();
            }
        });
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void updateView(String lang){
        Context context = LocaleHelper.setLocale(OpenChatActivity.this, lang);
        Resources resources = context.getResources();

        editTextComment.setHint(resources.getString(R.string.postDetail_hint_comment));
        topPanelTxt.setText(resources.getString(R.string.openChatTitle));
        serverMsg.setText(resources.getString(R.string.openchatDownMsg));
        joinOCTxtPopup.setText(resources.getString(R.string.joinOpenChatTxt));
        closeOCDialogTxt.setText(resources.getString(R.string.closeOpenChatDialogTxt));
        ocDescTxt.setText(resources.getString(R.string.openChatDesc));
    }

    private String timestampToString(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        return date;
    }

    private void showMessage(String message) {
        Toast.makeText(OpenChatActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLongMessage(String message) {
        Toast.makeText(OpenChatActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}