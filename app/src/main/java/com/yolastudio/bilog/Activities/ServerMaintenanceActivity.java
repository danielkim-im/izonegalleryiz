package com.yolastudio.bilog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.yolastudio.bilog.Adapters.LocaleHelper;
import com.yolastudio.bilog.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.paperdb.Paper;

public class ServerMaintenanceActivity extends AppCompatActivity {

    TextView title, desc, timestamp, developermsg;
    ProgressBar progressBar;
    Button refreshBtn, quitBtn;

    FirebaseRemoteConfig firebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_maintenance);
        getSupportActionBar().hide();

        title = findViewById(R.id.titleTextview);
        desc = findViewById(R.id.descriptionTextView);
        timestamp = findViewById(R.id.timeTextView);
        developermsg = findViewById(R.id.developerMsg);
        progressBar = findViewById(R.id.refresh_progress_bar);
        refreshBtn = findViewById(R.id.refreshBtn);
        quitBtn = findViewById(R.id.quitBtn);

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().build();
        //firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        progressBar.setVisibility(View.GONE);

        fetchRemoteConfigData();

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                firebaseRemoteConfig.fetch(0)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    firebaseRemoteConfig.activate();
                                    if(firebaseRemoteConfig.getBoolean("server_under_maintenance") == true){
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(ServerMaintenanceActivity.this, "SERVER UNDER MAINTENANCE 서버점검중", Toast.LENGTH_SHORT).show();
                                        getTimeStamp();
                                    }else if(firebaseRemoteConfig.getBoolean("server_under_maintenance") == false){
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(ServerMaintenanceActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                        finish();
                                    }
                                }else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(ServerMaintenanceActivity.this, "Server is not responding", Toast.LENGTH_SHORT).show();
                                    //Firebase Remote Config is not responding
                                }
                            }
                        });
            }
        });

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ServerMaintenanceActivity.this);
                alertDialogBuilder
                        .setCancelable(false)
                        .setTitle("Quit Application?")
                        .setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ServerMaintenanceActivity.this.finish();
                                System.exit(0);
                            }
                        })
                        .setNegativeButton("Cancel", null);

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        getTimeStamp();

        Paper.init(this);
        updateView((String) Paper.book().read("language"));
    }

    private void fetchRemoteConfigData() {
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            firebaseRemoteConfig.activate();

                            developermsg.setText(firebaseRemoteConfig.getString("server_maintenance_developer_msg"));

                        }else {
                            //Firebase Remote Config is not responding
                        }
                    }
                });
    }

    private void getTimeStamp(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        String dateTime = simpleDateFormat.format(calendar.getTime());
        timestamp.setText(dateTime);
    }

    private void updateView(String lang){
        Context context = LocaleHelper.setLocale(this, lang);
        Resources resources = context.getResources();

        title.setText(resources.getString(R.string.serverMaintenanceTitle));
        desc.setText(resources.getString(R.string.serverMaintenanceDesc));
        refreshBtn.setText(resources.getString(R.string.serverMaintenanceRefresh));
        quitBtn.setText(resources.getString(R.string.serverMaintenanceQuit));
    }
}