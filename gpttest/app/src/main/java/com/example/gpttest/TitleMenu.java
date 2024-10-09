package com.example.gpttest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yandex.mobile.ads.banner.BannerAdEventListener;
import com.yandex.mobile.ads.banner.BannerAdSize;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.MobileAds;

import java.util.Locale;

public class TitleMenu extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    @Nullable
    private BannerAdView mBannerAd;
    Locale locale = Locale.getDefault();
    private ImageButton langChangeBtn;
    private static final String SHARED_PREFS_NAME = "MyPrefs";
    public static void clearSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.setLocale(this, LocaleUtils.getLocale(this));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_title_menu);
        TitleMenu.clearSharedPreferences(getApplicationContext());


        Log.d("EBAL", "стоит язык = "+ locale);
        locale = Locale.getDefault();
        langChangeBtn = findViewById(R.id.language_button);
        ImageButton changeLanguageBtn = findViewById(R.id.language_button);
        changeLangImg();
        mBannerAd = findViewById(R.id.ad_container_view);
        View adContainerView = findViewById(R.id.ad_container_view);

        adContainerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        adContainerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mBannerAd = loadBannerAd(getAdSize());
                        Log.d("TESSST", ">>>>>TREE_DONE1 "+mBannerAd);
                    }
                }
        );


//        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
////        Button logoutButton = findViewById(R.id.buttonLogout);


        changeLanguageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TitleMenu.this, Language.class);
                startActivity(intent);
            }
        });


        Button watchSaveButton = findViewById(R.id.watchSaveButton);
        watchSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSavesActivity();
            }
        });
        MobileAds.initialize(this, () -> {
            Log.d("TESSST", "ВСЁ ОК");
        });


//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Удаляем данные из SharedPreferences
//                SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.remove("email");
//                editor.remove("password");
//                editor.apply();
//
//                // Перенаправляем пользователя на AuthActivity
//                Intent intent = new Intent(TitleMenu.this, AuthActivity.class);
//                startActivity(intent);
//                finish(); // Закрываем текущую активность
//            }
//        });
        // Получаем значение email
//        String email = preferences.getString("email", "");

//        // Находим TextView в макете activity_title.xml
//        TextView UserAuthTag = findViewById(R.id.UserAuthTag);
//
//        // Устанавливаем значение email в TextView
//        UserAuthTag.setText("Текущий пользователь: " + email);


        Button HoroButton = findViewById(R.id.Horo_Btn);
        HoroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TitleMenu.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Устанавливаем желаемую локаль (например, русский язык)
        // Здесь можете поменять на нужную локаль

        Configuration config = new Configuration();
        config.locale = Locale.getDefault();;

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        if(!String.valueOf(locale).equals(String.valueOf(Locale.getDefault()))) {
            Log.d("EBAL", "стоит язык = "+ locale + " а по дефолту = " + Locale.getDefault());
            locale = Locale.getDefault();

            recreate();

        }
    }



    @NonNull
    private BannerAdSize getAdSize() {
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        // Calculate the width of the ad, taking into account the padding in the ad container.
        int adWidthPixels = mBannerAd.getWidth();
        if (adWidthPixels == 0) {
            // If the ad hasn't been laid out, default to the full screen width
            adWidthPixels = displayMetrics.widthPixels;
        }
        final int adWidth = Math.round(adWidthPixels / displayMetrics.density);
        Log.d("TESSST", ">>>>>ADSIZE_ = "+ BannerAdSize.stickySize(this, adWidth));
        return BannerAdSize.stickySize(this, adWidth);
    }




    void changeLangImg() {
        Locale currentLocale = getResources().getConfiguration().locale;
        String currentLanguage = currentLocale.getLanguage();

        switch (currentLanguage) {
            case "ru":
                langChangeBtn.setImageResource(R.drawable.rus);
                break;
            case "zh":
                langChangeBtn.setImageResource(R.drawable.ch);
                break;
            default: // По умолчанию используем английское изображение
                langChangeBtn.setImageResource(R.drawable.eng);
                break;
        }
        Log.d("SUKA = ", currentLanguage);
    }


    @NonNull
    private BannerAdView loadBannerAd(@NonNull final BannerAdSize adSize) {
        final BannerAdView bannerAd = findViewById(R.id.ad_container_view);
        bannerAd.setAdSize(adSize);
        bannerAd.setAdUnitId("demo-banner-yandex");
        bannerAd.setBannerAdEventListener(new BannerAdEventListener() {
            @Override
            public void onAdLoaded() {
                // If this callback occurs after the activity is destroyed, you
                // must call destroy and return or you may get a memory leak.
                // Note `isDestroyed` is a method on Activity.
                if (isDestroyed() && mBannerAd != null) {
                    mBannerAd.destroy();
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull final AdRequestError adRequestError) {
                // Ad failed to load with AdRequestError.
                // Attempting to load a new ad from the onAdFailedToLoad() method is strongly discouraged.
            }

            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
            }

            @Override
            public void onLeftApplication() {
                // Called when user is about to leave application (e.g., to go to the browser), as a result of clicking on the ad.
            }

            @Override
            public void onReturnedToApplication() {
                // Called when user returned to application after click.
            }

            @Override
            public void onImpression(@Nullable ImpressionData impressionData) {
                // Called when an impression is recorded for an ad.

                if(impressionData!= null){
                    Log.d("TESSST", ">>>>>YandexADSONIMPRESSION_TITLE = " + impressionData.getRawData());
                }
            }
        });
        final AdRequest adRequest = new AdRequest.Builder()
                // Methods in the AdRequest.Builder class can be used here to specify individual options settings.
                .build();
        bannerAd.loadAd(adRequest);
        Log.d("TESSST", ">>>>>OPAAAA " + bannerAd);
        return bannerAd;
    }

    private void openSavesActivity() {
        Intent intent = new Intent(this, SavesActivity.class);
        startActivity(intent);
    }
}
