package com.example.gpttest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class Language extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        LocaleUtils.setLocale(this, LocaleUtils.getLocale(this));
        setContentView(R.layout.activity_language);

        Button buttonToRussian = findViewById(R.id.Rus_Btn);
        Button buttonToEng = findViewById(R.id.EngBtn);
        Button buttonToChinese = findViewById(R.id.Chinese_Btn);

        buttonToRussian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocaleUtils.setLocale(Language.this, "ru");
                recreate();
            }
        });

        buttonToEng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocaleUtils.setLocale(Language.this, "en");
                recreate();
            }
        });
        buttonToChinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocaleUtils.setLocale(Language.this, "zh");
                recreate();
            }      
        });
    }

}