package com.yolastudio.bilog.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

import com.yolastudio.bilog.Adapters.LocaleHelper;
import com.yolastudio.bilog.R;

import io.paperdb.Paper;

public class ErrorActivity extends AppCompatActivity {

    TextView errorTitle, errorDesc1, errorDesc2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        errorTitle = findViewById(R.id.error_title);
        errorDesc1 = findViewById(R.id.error_desc1);
        errorDesc2 = findViewById(R.id.error_desc2);

        Paper.init(this);
        updateView((String) Paper.book().read("language"));
    }

    private void updateView(String lang){
        Context context = LocaleHelper.setLocale(this, lang);
        Resources resources = context.getResources();

        errorTitle.setText(resources.getString(R.string.error_title));
        errorDesc1.setText(resources.getString(R.string.error_desc1));
        errorDesc2.setText(resources.getString(R.string.error_desc2));
    }
}