package com.yolastudio.bilog.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.yolastudio.bilog.Adapters.LocaleHelper;
import com.yolastudio.bilog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText userMail, userPassword;
    private TextView title, desc;
    private ImageButton btnLogin;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth;
    private Intent HomeActivity;
    private TextView createAccount;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        backButton = findViewById(R.id.backtoIntro1);
        userMail = findViewById(R.id.login_mail);
        title = findViewById(R.id.title1);
        desc = findViewById(R.id.desc1);
        userPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.loginBtn);
        loginProgress = findViewById(R.id.login_progress);
        mAuth = FirebaseAuth.getInstance();

        HomeActivity = new Intent(this, com.yolastudio.bilog.Activities.HomeActivity.class);
        createAccount = findViewById(R.id.goto_register);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(LoginActivity.this, "Sign up using Email and Password is no longer available. Please continue with Sign in with Google", Toast.LENGTH_LONG).show();

                /*Intent registerActivity = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerActivity);
                finish();*/
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toIntroIntent = new Intent(LoginActivity.this, IntroActivity.class);
                startActivity(toIntroIntent);
                finish();
            }
        });

        loginProgress.setVisibility(View.GONE);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginProgress.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                final String mail = userMail.getText().toString();
                final String password = userPassword.getText().toString();

                if(mail.isEmpty() || password.isEmpty()){
                    showMessage("Please Verify All Field");
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                }else{
                    signIn(mail, password);
                }
            }
        });

        Paper.init(this);
        updateView((String) Paper.book().read("language"));
    }

    private void signIn(String mail, String password) {

        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    loginProgress.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);

                    if(mAuth.getCurrentUser().isEmailVerified()){
                        updateUI();
                    }else {
                        startActivity(new Intent(LoginActivity.this, EmailVerificationActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                }else
                    showMessage(task.getException().getMessage());
                btnLogin.setVisibility(View.VISIBLE);
                loginProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void updateUI() {
        startActivity(HomeActivity);
        finish();
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            //user is already connected so we need to redirect the user to home page
            updateUI();
        }
    }

    private void updateView(String lang){
        Context context = LocaleHelper.setLocale(this, lang);
        Resources resources = context.getResources();

        userMail.setHint(resources.getString(R.string.login_hint_email));
        userPassword.setHint(resources.getString(R.string.login_hint_password));
        title.setText(resources.getString(R.string.login_title));
        desc.setText(resources.getString(R.string.login_desc));
        createAccount.setText(resources.getString(R.string.gotoSignUp));
    }
}