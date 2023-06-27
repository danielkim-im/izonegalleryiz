package com.yolastudio.bilog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.shobhitpuri.custombuttons.GoogleSignInButton;
import com.yolastudio.bilog.Adapters.LocaleHelper;
import com.facebook.login.LoginManager;
import com.yolastudio.bilog.R;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Locale;

import io.paperdb.Paper;

public class IntroActivity extends AppCompatActivity {

    ImageView introImg;
    ConstraintLayout googleBtn, facebookBtn, signinEmailBtn;
    TextView desc1, googleSignInTxt, facebookSignInTxt, signinEmailTxt;

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    private GoogleSignInClient mGoogleSignInClient;
    private final static  int RC_SIGN_IN = 123;

    CallbackManager mCallbackManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        getSupportActionBar().hide();
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Black));

        FacebookSdk.sdkInitialize(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        introImg = findViewById(R.id.introImg);
        googleSignInTxt = findViewById(R.id.signinGoogleTxt);
        facebookSignInTxt = findViewById(R.id.signinFacebookTxt);
        facebookBtn = findViewById(R.id.signinFacebook);
        googleBtn = findViewById(R.id.signinGoogle);
        signinEmailBtn = findViewById(R.id.signinEmail);
        signinEmailTxt = findViewById(R.id.signinEmailTxt);
        desc1 = findViewById(R.id.desc1);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(IntroActivity.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        //Log.d(TAG, "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        //Log.d(TAG, "facebook:onError", error);
                        // ...
                    }
                });
            }
        });

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        signinEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signinIntent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(signinIntent);
                sendBroadcast(signinIntent);
            }
        });

        Paper.init(this);

        //Default language is English
        String language = Paper.book().read("language");
        if(language == null){
            Paper.book().write("language", "en");
            showChangeLanguageDialog();
        }

        updateView((String) Paper.book().read("language"));

        createRequest();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        String language = Paper.book().read("language");

        if(user != null && mAuth.getCurrentUser().isEmailVerified() && language != null){
            //user is already connected so we need to redirect the user to home page
            startActivity(new Intent(this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        } /*else if(user != null && mAuth.getCurrentUser().isEmailVerified() == false){
            //Toast.makeText(IntroActivity.this, "VERIFY EMAIL", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(IntroActivity.this, EmailVerificationActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }*/else if(user != null && language != null){
            startActivity(new Intent(this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }else{
            //new user
        }
    }

    private void updateView(String lang){
        Context context = LocaleHelper.setLocale(this, lang);
        Resources resources = context.getResources();

        signinEmailTxt.setText(resources.getString(R.string.signInEmail));
        googleSignInTxt.setText(resources.getString(R.string.signInGoogle));
        facebookSignInTxt.setText(resources.getString(R.string.signInFacebook));
        desc1.setText(resources.getString(R.string.intro_desc));
    }

    private void handleFacebookAccessToken(AccessToken token) {
        //Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(IntroActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user != null){
            startActivity(new Intent(IntroActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }else{
            Toast.makeText(this, "Please sign in to continue", Toast.LENGTH_SHORT).show();
        }
    }

    private void showChangeLanguageDialog(){
        final String[] listItems = {"English", "한국어", "日本人"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(IntroActivity.this);
        mBuilder.setTitle("Language");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){
                    //English
                    Paper.book().write("language", "en");
                    updateView((String)Paper.book().read("language"));

                    dialogInterface.dismiss();
                    recreate();
                }else if(i == 1){
                    //Korean
                    Paper.book().write("language", "ko");
                    updateView((String)Paper.book().read("language"));

                    dialogInterface.dismiss();
                    recreate();
                }else if(i == 2){
                    //Japanese
                    Paper.book().write("language", "ja");
                    updateView((String)Paper.book().read("language"));

                    dialogInterface.dismiss();
                    recreate();
                }
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(IntroActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(IntroActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}