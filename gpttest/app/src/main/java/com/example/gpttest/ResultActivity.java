package com.example.gpttest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.mobile.ads.banner.BannerAdEventListener;
import com.yandex.mobile.ads.banner.BannerAdSize;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.MobileAds;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultActivity extends AppCompatActivity {

    private TextView resultTextView1, resultTextView2, resultTextView3;

    @Nullable
    private BannerAdView InlineAd1,InlineAd2,InlineAd3,InlineAd4;

    private static final String PREFS_NAME = "myPrefs"; // Define PREFS_NAME here
    private TextView resultTextView;
    private TextView titleResultTextView;
    private Button shareButton;

    private Button saveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.setLocale(this, LocaleUtils.getLocale(this));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_result);



        InlineAd1 = findViewById(R.id.ad_container_view1);
        View InlineAdView1 = findViewById(R.id.ad_container_view1);

        InlineAd2 = findViewById(R.id.ad_container_view2);
        View InlineAdView2 = findViewById(R.id.ad_container_view2);

        InlineAd3 = findViewById(R.id.ad_container_view3);
        View InlineAdView3 = findViewById(R.id.ad_container_view3);

        InlineAd4 = findViewById(R.id.ad_container_view4);
        View InlineAdView4 = findViewById(R.id.ad_container_view4);

        InlineAdView1.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        InlineAdView1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        InlineAd1 = loadBannerAd(InlineAd1, getAdSize(InlineAd1));
                        Log.d("TESSST", ">>>>>TREE_DONE1 "+InlineAd1);
                    }
                }
        );
        InlineAdView2.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        InlineAdView2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        InlineAd2 = loadBannerAd(InlineAd2, getAdSize(InlineAd2));
                        Log.d("TESSST", ">>>>>TREE_DONE2 "+ InlineAd2);
                    }
                }
        );
        InlineAdView3.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        InlineAdView3.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        InlineAd3 = loadBannerAd(InlineAd3, getAdSize(InlineAd3));
                        Log.d("TESSST", ">>>>>TREE_DONE3 "+InlineAd3);
                    }
                }
        );
        InlineAdView4.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        InlineAdView4.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        InlineAd4 = loadBannerAd(InlineAd4, getAdSize(InlineAd4));
                        Log.d("TESSST", ">>>>>TREE_DONE1 "+InlineAd4);
                    }
                }
        );


        MobileAds.initialize(this, () -> {
            Log.d("TESSST", "ВСЁ ОК ИНИЦИАЛИЗОВАНО");
        });


        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String typeHoroFromMain = preferences.getString("typeHoro", ""); // Default value if not found
        resultTextView = findViewById(R.id.resultTextView);
        titleResultTextView = findViewById(R.id.titleResultTextView);

        // Находим TextView по их идентификаторам
        resultTextView1 = findViewById(R.id.resultTextView1);
        resultTextView2 = findViewById(R.id.resultTextView2);
        resultTextView3 = findViewById(R.id.resultTextView3);

        // Получаем результат из Intent
        String response = getIntent().getStringExtra("response");

        // Получаем значение email
        String email = preferences.getString("email", "");

//        // Находим TextView в макете activity_title.xml
//        TextView UserAuthTag = findViewById(R.id.UserAuthTag);
//
//        // Устанавливаем значение email в TextView
//        UserAuthTag.setText("Текущий пользователь: " + email);

        // Разделяем текст на title и result и устанавливаем их в соответствующие TextView
        separateText(response);

        shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareText("Сейчас Я получил свой Гороскоп " + typeHoroFromMain  + "! Попробуй и ты: ..ССЫЛКА НА ПРИЛОЖЕНИЕ..");
            }
        });
        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveActivityState();
            }
        });
    }
    private void saveActivityState() {
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Получаем текст из resultTextView и titleResultTextView
        String resultText = resultTextView.getText().toString();
        String titleText = titleResultTextView.getText().toString();

        // Получаем текущее количество сохраненных результатов
        int numberOfSavedResults = preferences.getInt("number_of_saved_results", 0);

        // Сохраняем заголовок и текст вместе, разделяя их, например, символом "|"
        String combinedText = titleText + "|" + resultText;

        // Сохраняем заголовок и текст
        editor.putString("saved_result_" + numberOfSavedResults, combinedText);

        // Увеличиваем количество сохраненных результатов
        editor.putInt("number_of_saved_results", numberOfSavedResults + 1);

        editor.apply();
        Toast.makeText(ResultActivity.this, "Activity saved successfully", Toast.LENGTH_SHORT).show();
    }


    private void separateText(String response) {
        // Если используется китайский язык, устанавливаем текст напрямую без HTML-тегов

            // Если используется другой язык, обрабатываем текст с использованием HTML.fromHtml()
        resultTextView.setText(Html.fromHtml(response.replace("。", ". "), Html.FROM_HTML_MODE_LEGACY));

        String originalText = resultTextView.getText().toString();
        Log.d("opanegr = ", resultTextView.getText().toString());
        // Разбиваем текст на предложения
        List<String> sentences = splitIntoSentences(originalText);

        // Вычисляем количество предложений и длину каждой части
        int totalSentences = sentences.size();
        int sentencesPerPart = totalSentences / 3;

        // Находим индексы для разделения текста
        int index1 = sentencesPerPart;
        int index2 = sentencesPerPart * 2;

        // Собираем три части текста
        String divider = "------------\n";
        String part1 = divider + joinSentences(sentences.subList(0, index1));
        String part2 = divider + joinSentences(sentences.subList(index1, index2));
        String part3 = divider + joinSentences(sentences.subList(index2, totalSentences));

        // Устанавливаем текст в соответствующие TextView
        resultTextView1.setText(part1);
        resultTextView2.setText(part2);
        resultTextView3.setText(part3);
        resultTextView.setText(" ");

    }

    private String[] splitResponse(String response) {
        return response.split("/end_title/");
    }

    private void shareText(String textToShare) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        startActivity(Intent.createChooser(shareIntent, "Поделиться через"));
    }
    private String removeTitleFromResponse(String response) {
        try {
            Document document = Jsoup.parse(response);
            Elements titles = document.select(".title");
            if (!titles.isEmpty()) {
                Element titleElement = titles.first();
                titleElement.remove(); // Удаляем элемент с классом "title"
            }
            return Jsoup.parse(document.body().html()).text(); // Очищаем текст от HTML-тегов
        } catch (Exception e) {
            e.printStackTrace();
            return response; // Возвращаем исходный response в случае ошибки
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Создаем новый Intent для перехода к MainActivity
        Intent intent = new Intent(this, TitleMenu.class);
        // Добавляем флаги для очистки всех активностей на вершине стека и предотвращения их повторного создания
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // Запускаем активность MainActivity
        startActivity(intent);
        // Завершаем текущую активность ResultActivity
        finish();
    }

    private List<String> splitIntoSentences(String text) {
        List<String> sentences = new ArrayList<>();
        Pattern pattern = Pattern.compile("[^.!?]+[.!?]");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            sentences.add(matcher.group().trim());
        }
        return sentences;
    }

    // Метод для объединения списка предложений в одну строку
    private String joinSentences(List<String> sentences) {
        StringBuilder builder = new StringBuilder();
        for (String sentence : sentences) {
            builder.append(sentence).append(" ");
        }
        return builder.toString();
    }


    private BannerAdSize getAdSize(@NonNull View adView) {
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        final int screenHeight = Math.round(displayMetrics.heightPixels / displayMetrics.density);

        // Get the parent view of the ad view
        ViewParent parent = adView.getParent();

        View parentView = (View) parent;
        // Calculate the width of the ad based on the width of the parent view
        int adWidthPixels = parentView.getMeasuredWidth();
        final int adWidth = Math.round(adWidthPixels / displayMetrics.density);
        // Determine the maximum allowable ad height. The current value is given as an example.
        final int maxAdHeight = screenHeight / 5;



        // If the parent view has not been measured yet, return a default ad size
        return BannerAdSize.inlineSize(this, adWidth, maxAdHeight);
    }




    @NonNull
    private BannerAdView loadBannerAd(@NonNull final BannerAdView bannerAd, @NonNull final BannerAdSize adSize) {
        bannerAd.setAdSize(adSize);
        bannerAd.setAdUnitId("demo-banner-yandex");
        bannerAd.setBannerAdEventListener(new BannerAdEventListener() {
            @Override
            public void onAdLoaded() {
                // If this callback occurs after the activity is destroyed, you
                // must call destroy and return or you may get a memory leak.
                Log.d("TESSST", ">>>>>LOADED");
                // Note `isDestroyed` is a method on Activity.
                if (isDestroyed() && bannerAd != null) {
                    bannerAd.destroy();
                    Log.d("TESSST", ">>>>>DESTROY");
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull final AdRequestError adRequestError) {
                // Ad failed to load with AdRequestError.
                // Attempting to load a new ad from the onAdFailedToLoad() method is strongly discouraged.
                // Called when ad is shown.
                Log.d("TESSST", ">>>>>ОПА PIZDEC");
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
                if(impressionData!= null){
                    Log.d("TESSST", ">>>>>YandexADSONIMPRESSION_RESULT = " + impressionData.getRawData());
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

}
