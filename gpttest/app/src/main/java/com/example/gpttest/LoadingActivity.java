package com.example.gpttest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.yandex.mobile.ads.banner.BannerAdEventListener;
import com.yandex.mobile.ads.banner.BannerAdSize;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.MobileAds;

public class LoadingActivity extends AppCompatActivity {

    @Nullable
    private BannerAdView mBannerAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.setLocale(this, LocaleUtils.getLocale(this));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_loading_screen);

        // Initialize the banner ad variable
        mBannerAd = findViewById(R.id.ad_container_view1);
        View adContainerView = findViewById(R.id.ad_container_view1);

        // Initialize the banner ad variable for ad_container_view2
        BannerAdView bannerAd2 = findViewById(R.id.ad_container_view2);
        bannerAd2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bannerAd2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Now that the view has been laid out, load the banner ad
                loadBannerAd(bannerAd2, getAdSize());
            }
        });


        adContainerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                adContainerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Now that the view has been laid out, load the banner ad
               loadBannerAd(mBannerAd,getAdSize());
                Log.d("TESSST", ">>>>>TREE_DONE1 "+mBannerAd);
            }
        });

        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);




//        // Получаем значение email
 String email = preferences.getString("email", "");
//
//        // Находим TextView в макете activity_title.xml
//        TextView UserAuthTag = findViewById(R.id.UserAuthTag);
//
//        // Устанавливаем значение email в TextView
//        UserAuthTag.setText("Текущий пользователь: " + email);
        // Вызываем метод, который начнет загрузку данных
        loadData();
    }

    private void loadData() {
        // В этом методе загружаем данные с сервера или выполняем другие длительные операции

        // Запускаем AsyncTask для загрузки данных
        new LoadDataTask().execute();
    }

    // Внутренний класс AsyncTask для загрузки данных в фоновом потоке
    private class LoadDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            // В этом методе загружаем данные с сервера или выполняем другие длительные операции
            // Возвращаем загруженные данные
            return "Загруженные данные";
        }

        @Override
        protected void onPostExecute(String result) {
            // После завершения загрузки данных переходим к ResultActivity

            Intent intent = new Intent(LoadingActivity.this, ResultActivity.class);
            intent.putExtra("result", result);
            // Используем флаги для предотвращения возврата к LoadingActivity при нажатии кнопки "назад"
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // Закрываем LoadingActivity
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

    @NonNull
    private void loadBannerAd(@NonNull BannerAdView bannerAdView, @NonNull final BannerAdSize adSize) {
        bannerAdView.setAdSize(adSize);
        bannerAdView.setAdUnitId("demo-banner-yandex");
        bannerAdView.setBannerAdEventListener(new BannerAdEventListener() {
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
                    Log.d("TESSST", ">>>>>YandexADSONIMPRESSION_LOADING =  " + impressionData.getRawData());
                }
            }
        });
        final AdRequest adRequest = new AdRequest.Builder()
                // Methods in the AdRequest.Builder class can be used here to specify individual options settings.
                .build();
        bannerAdView.loadAd(adRequest);
        Log.d("TESSST", ">>>>>OPAAAA " + bannerAdView);
    }


}
