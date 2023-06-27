package com.yolastudio.bilog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;
import com.yolastudio.bilog.Adapters.CommentAdapter;
import com.yolastudio.bilog.Adapters.LocaleHelper;
import com.yolastudio.bilog.Models.Comment;
import com.yolastudio.bilog.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yolastudio.bilog.Adapters.SaveImageHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class PostDetailActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1000;
    ImageView imgCurrentUser, backBtn;
    CircleImageView imgUploaderImg;
    TextView txtPostDesc, txtPostTitle;
    EditText editTextComment;
    ImageButton btnAddComment, shareImgBtn;
    Button downloadBtn;
    String PostKey;
    RecyclerView RvComment;
    CommentAdapter commentAdapter;
    List<Comment> listComment;
    static String COMMENT_KEY = "Comment";

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    Boolean isConnected;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int nightModeFlags =
                PostDetailActivity.this.getResources().getConfiguration().uiMode &
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
        setContentView(R.layout.activity_post_detail);
        getSupportActionBar().hide();

        //ini Views
        RvComment = findViewById(R.id.rv_comment);
        RvComment.setLayoutManager(new GridLayoutManager(PostDetailActivity.this, 1, LinearLayoutManager.VERTICAL, false));
        imgCurrentUser = findViewById(R.id.cafe_current_user_img);
        txtPostDesc = findViewById(R.id.row_post_desc);
        txtPostTitle = findViewById(R.id.toolbar_title1);
        downloadBtn = findViewById(R.id.downloadBtn);
        shareImgBtn = findViewById(R.id.shareImgBtn);
        imgUploaderImg = findViewById(R.id.uploaderImg);

        editTextComment = findViewById(R.id.cafe_comment_edittext);
        btnAddComment = findViewById(R.id.cafe_addComment_btn);
        backBtn = findViewById(R.id.backBtn1);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        // add comment button click listener
        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String commentTxt = editTextComment.getText().toString();

                checkConnection();

                if(isConnected){
                    if(TextUtils.isEmpty(commentTxt)){
                        //do nothing
                    }else{
                        btnAddComment.setVisibility(View.INVISIBLE);
                        DatabaseReference commentReference = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey).push();
                        String comment_content = editTextComment.getText().toString();
                        String uid = firebaseUser.getUid();
                        String uname = firebaseUser.getDisplayName();
                        String uimg = firebaseUser.getPhotoUrl().toString();
                        Comment comment = new Comment(comment_content, uid, uimg, uname);

                        commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showMessage("Comment Added");
                                editTextComment.setText("");
                                btnAddComment.setVisibility(View.VISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessage("Failed to add comment: "+e.getMessage());
                            }
                        });
                    }
                }else if(isConnected = false){
                    showMessage("No Internet Connection");
                }
            }
        });

        final String postTitle = getIntent().getExtras().getString("title");
        txtPostTitle.setText(postTitle);
        final String postDescription = getIntent().getExtras().getString("description");
        txtPostDesc.setText(postDescription);
        Glide.with(this)
                .load(firebaseUser.getPhotoUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imgCurrentUser);
        PostKey = getIntent().getExtras().getString("postKey");
        String date = timestampToString(getIntent().getExtras().getLong("postData"));
        Glide.with(this).load(R.drawable.one_reeler_logo).into(imgUploaderImg);
        final Boolean downloadImg = getIntent().getExtras().getBoolean("downloadImg");
        final Boolean commenting = getIntent().getExtras().getBoolean("commenting");

        if(downloadImg == true){
            downloadImage();
        }

        if(commenting == true){
            editTextComment.requestFocus();
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Service.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editTextComment, 0);
        }

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadImage();
            }
        });

        final Handler handler = new Handler();
        final int delay = 100; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                if(SaveImageHelper.imgDownloaded == true){
                    showMessage("Image Saved");
                    SaveImageHelper.imgDownloaded = false;
                }else{
                    //showLongMessage("Error: Please Try Again");
                }
                handler.postDelayed(this, delay);
            }
        }, delay);


        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        shareImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = "Please download IZONE Gallery*IZ to explore the posts.\n\nGoogle Play:\nhttps://play.google.com/store/apps/details?id=com.yolastudio.bilog";
                String shareSubject = "IZONE GALLERY*IZ " + postTitle;
                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(myIntent, "Share " + postTitle));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        txtPostTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        //ini Recyclerview Comment
        iniRvComment();

        Paper.init(this);
        updateView((String) Paper.book().read("language"));
    }

    private void downloadImage() {

        final String postTitle = getIntent().getExtras().getString("title");
        final String postImage = getIntent().getExtras().getString("postImage");

        if(ActivityCompat.checkSelfPermission(PostDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{

                        Manifest.permission.WRITE_EXTERNAL_STORAGE

                }, PERMISSION_REQUEST_CODE);
            }

        if(ActivityCompat.checkSelfPermission(PostDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            if(SaveImageHelper.imgDownloaded == false){
                AlertDialog dialog = new SpotsDialog(PostDetailActivity.this);

                dialog.show();
                dialog.setMessage("Downloading: " + postTitle);

                String fileName = UUID.randomUUID().toString()+".jpg";
                Picasso.get()
                        .load(postImage)
                        .into(new SaveImageHelper(getBaseContext(),
                                dialog,
                                getApplicationContext().getContentResolver(),
                                fileName,
                                "Image description"));
            }
        }
    }

    private void iniRvComment() {

        RvComment.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listComment = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    listComment.add(comment);
                }

                commentAdapter = new CommentAdapter(PostDetailActivity.this, listComment);
                RvComment.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private String timestampToString(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("MM-dd-yyyy", calendar).toString();
        return date;
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
            showMessage("No Internet Connection");
            isConnected = false;
        }
    }

    private void updateView(String lang){
        Context context = LocaleHelper.setLocale(this, lang);
        Resources resources = context.getResources();

        downloadBtn.setText(resources.getString(R.string.postDetail_downloadImg));
        editTextComment.setHint(resources.getString(R.string.postDetail_hint_comment));
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLongMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
