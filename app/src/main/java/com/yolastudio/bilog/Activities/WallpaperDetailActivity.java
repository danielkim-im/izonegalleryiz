package com.yolastudio.bilog.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.yolastudio.bilog.Adapters.SaveImageHelper;
import com.yolastudio.bilog.R;

import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class WallpaperDetailActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1000;
    ImageView wallpaperImgView, downloadBtn;
    TextView titleTxt, descTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_detail);
        getSupportActionBar().hide();
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Black));

        wallpaperImgView = findViewById(R.id.wallpaperImgView);
        downloadBtn = findViewById(R.id.downloadBtnWallpaper);
        titleTxt = findViewById(R.id.wallpaperTitle);
        descTxt = findViewById(R.id.wallpaperDesc);

        final String userId = getIntent().getExtras().getString("userId");
        final String description = getIntent().getExtras().getString("description");
        final String title = getIntent().getExtras().getString("title");
        final String postImage = getIntent().getExtras().getString("postImage");
        final String postKey = getIntent().getExtras().getString("postKey");

        //Glide.with(this).load(postImage).into(wallpaperImgView);
        Picasso.get()
                .load(postImage)
                .config(Bitmap.Config.RGB_565)
                .into(wallpaperImgView);
        titleTxt.setText(title);
        descTxt.setText(description);

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage();
            }
        });

        final Handler handler = new Handler();
        final int delay = 100; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                if(SaveImageHelper.imgDownloaded == true){
                    Toast.makeText(WallpaperDetailActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
                    SaveImageHelper.imgDownloaded = false;
                }else{
                    //showLongMessage("Error: Please Try Again");
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void downloadImage() {

        final String postTitle = getIntent().getExtras().getString("title");
        final String postImage = getIntent().getExtras().getString("postImage");

        if(ActivityCompat.checkSelfPermission(WallpaperDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{

                        Manifest.permission.WRITE_EXTERNAL_STORAGE

                }, PERMISSION_REQUEST_CODE);
            }

        if(ActivityCompat.checkSelfPermission(WallpaperDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            if(SaveImageHelper.imgDownloaded == false){
                AlertDialog dialog = new SpotsDialog(WallpaperDetailActivity.this);

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
}