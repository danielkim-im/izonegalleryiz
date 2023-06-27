package com.yolastudio.bilog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.yolastudio.bilog.Adapters.LocaleHelper;
import com.yolastudio.bilog.R;

import io.paperdb.Paper;

public class EmailVerificationActivity extends AppCompatActivity {

    ProgressBar progressBar;
    boolean updateAgain = true;

    TextView emailTextView, descTextView;
    Button signinwithDifferentAccount, emailVerifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        getSupportActionBar().hide();

        progressBar = findViewById(R.id.verify_progress_bar);
        progressBar.setVisibility(View.GONE);

        emailTextView = findViewById(R.id.timeTextView);
        descTextView = findViewById(R.id.descriptionTextView);
        signinwithDifferentAccount = findViewById(R.id.signinwithdifferentaccountBtn);
        emailVerifyBtn = findViewById(R.id.verifyEmailBtn);

        emailTextView.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        progressBar.setVisibility(View.INVISIBLE);
        emailVerifyBtn.setVisibility(View.VISIBLE);

        signinwithDifferentAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(EmailVerificationActivity.this, IntroActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });

        FirebaseAuth.getInstance().getCurrentUser()
                .sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            emailTextView.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            //showMessage("Verification Email Sent To: " + FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        }else {
                            descTextView.setText("Failed To Send Email Verification Link To:");
                            emailTextView.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            Toast.makeText(EmailVerificationActivity.this, "Please try again later.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(EmailVerificationActivity.this, ErrorActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            finish();
                        }
                    }
                });

        emailVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            if(updateAgain == true){
                                if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                                    updateAgain = false;
                                    progressBar.setVisibility(View.VISIBLE);
                                    emailVerifyBtn.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(EmailVerificationActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    finish();
                                }else {
                                    Toast.makeText(EmailVerificationActivity.this, "Please check your email inbox and verify your account", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    emailVerifyBtn.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });
            }
        });

        Paper.init(this);

        updateView((String) Paper.book().read("language"));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    if(updateAgain == true){
                        if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                            updateAgain = false;
                            progressBar.setVisibility(View.VISIBLE);
                            emailVerifyBtn.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(EmailVerificationActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            finish();
                        }
                    }
                }
            }
        });
    }

    private void updateView(String lang){
        Context context = LocaleHelper.setLocale(this, lang);
        Resources resources = context.getResources();

        emailTextView.setText(resources.getString(R.string.email_verification_title));
        descTextView.setText(resources.getString(R.string.email_verification_desc));
        signinwithDifferentAccount.setText(resources.getString(R.string.signinwithdifferentaccount_btn));
        emailVerifyBtn.setText(resources.getString(R.string.verifyEmailBtn));
    }
}