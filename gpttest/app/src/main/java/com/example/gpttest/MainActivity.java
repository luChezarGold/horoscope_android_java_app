package com.example.gpttest;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.yandex.mobile.ads.banner.BannerAdEventListener;
import com.yandex.mobile.ads.banner.BannerAdSize;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.MobileAds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private String[] horoData;
    private DatePicker datePicker;
    private Handler handler;
    private Runnable runnable;
    private Button LeftBtn,RightBtn;
    private TextView signName;
    private Button sendButton;
    private TextView typeHoroTextView;
    private GestureDetectorCompat gestureDetector;
    private String currentZodiacSign = "";
    private int currentIndex = 0;
    @Nullable
    private BannerAdView mBannerAd;

    private static final String PREFS_NAME = "myPrefs";
    private static final String KEY_SELECTED_DATE = "selectedDate";

    @Override
    protected void onResume() {
        super.onResume();

        // Restore selected date from SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedDate = preferences.getString(KEY_SELECTED_DATE, "");

        if (!savedDate.isEmpty()) {
            try {
                String[] dateParts = savedDate.split("\\.");
                int day = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]) - 1; // Months in DatePicker start from 0
                int year = Integer.parseInt(dateParts[2]);
                datePicker.updateDate(year, month, day);
            } catch (NumberFormatException e) {
                Log.e("PARSE_ERROR", "Failed to parse saved date: " + savedDate);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.setLocale(this, LocaleUtils.getLocale(this));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        signName = findViewById(R.id.signName);
        datePicker = findViewById(R.id.datePicker);
        sendButton = findViewById(R.id.sendButton);
        View swipeView = findViewById(R.id.SwipeView);
        horoData = new String[]{getString(R.string.toJob), getString(R.string.toToday), getString(R.string.toLove), getString(R.string.toHealth)};
        // Initialize the banner ad variable
        mBannerAd = findViewById(R.id.ad_container_view1);
        View adContainerView = findViewById(R.id.ad_container_view1);
        Log.d("BUBUBU ", String.valueOf(Locale.getDefault()));
        MobileAds.initialize(this, () -> {
            Log.d("TESSST", "ВСЁ ОК ИНИЦИАЛИЗОВАНО");
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

        //SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        typeHoroTextView = findViewById(R.id.TypeHoro);

        LeftBtn = findViewById(R.id.leftBtn);
        RightBtn = findViewById(R.id.rightBtn);

        LeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = (currentIndex - 1 + horoData.length) % horoData.length;
                updateTypeHoroText();
            }
        });

        RightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = (currentIndex + 1) % horoData.length;
                updateTypeHoroText();
            }
        });

        //String email = preferences.getString("email", ""); // Получаем email пользователя из SharedPreferences

        // Отправляем запрос на сервер для получения lastDate пользователя
//        new GetLastDateTask().execute(email);

        gestureDetector = new GestureDetectorCompat(this, new MyGestureListener());
        swipeView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        // Получаем значение email

//
//        // Находим TextView в макете activity_title.xml
//        TextView UserAuthTag = findViewById(R.id.UserAuthTag);
//
//        // Устанавливаем значение email в TextView
//        UserAuthTag.setText("Текущий пользователь: " + email);
        handler = new Handler();
        int delayMillis = 1000;
        //String typeHoro = "";
       // Intent intent = getIntent();
            //String data = intent.getStringExtra("key");
            /*
            TextView textView = findViewById(R.id.textView2);
            textView.setText(data);
             */
            //typeHoro = data;

        ImageView AnimatedBack = findViewById(R.id.Back_rotation_Img);
        Animation anim_Rotate_Pulse = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
        AnimatedBack.startAnimation(anim_Rotate_Pulse);

        runnable = new Runnable() {
            @Override
            public void run() {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                String zodiacSign = getZodiacSign(day, month);
                shiftMainLayoutContent(zodiacSign);
                signName.setText(zodiacSign);
                handler.postDelayed(this, delayMillis);
            }
        };

        handler.postDelayed(runnable, delayMillis);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save selected date to SharedPreferences

                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                String selectedDate = day + "." + month + "." + year;

                SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(KEY_SELECTED_DATE, selectedDate);
                editor.apply(); // or editor.commit();

                // Открываем интент с данными о типе гороскопа и присваиваем
                String typeHoroOnClick = horoData[currentIndex];

                // Отправляем запрос на сервер для обновления даты
               // String email = preferences.getString("email", ""); // Получаем email пользователя из SharedPreferences

                editor.putString("typeHoro", typeHoroOnClick);
                editor.apply(); // or editor.commit();

                String zodiacSign = getZodiacSign(day, month);
                String params = Locale.getDefault() + ";" + zodiacSign + ";" + typeHoroOnClick + ";"+day+"."+month+"."+year;
                Log.d("opanegr = ",params);
                if (zodiacSign != null) {
                    // Показываем активити с сообщением ожидания
                    Intent intent = new Intent(MainActivity.this, LoadingActivity.class);
                    startActivity(intent);

                    // Отправляем запрос на сервер
                    new SendRequestTask().execute(params);
                }
            }
        });
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
    public void onLeftButtonClick(View view) {
        currentIndex = (currentIndex - 1 + horoData.length) % horoData.length;
        updateTypeHoroText();
    }

    public void onRightButtonClick(View view) {
        currentIndex = (currentIndex + 1) % horoData.length;
        updateTypeHoroText();
    }
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(e2.getY() - e1.getY()) &&
                    Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    // Swipe right
                    currentIndex = (currentIndex - 1 + horoData.length) % horoData.length;
                    updateTypeHoroText();
                } else {
                    // Swipe left
                    currentIndex = (currentIndex + 1) % horoData.length;
                    updateTypeHoroText();
                }
                return true;
            }
            return false;
        }
    }


//    private class GetLastDateTask extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String email = strings[0];
//            String response = "";
//            try {
//                URL url = new URL("https://testmobileapl.extecom.com//registration.php");
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("POST");
//                connection.setDoOutput(true);
//
//                // Формируем тело запроса
//                String postData = "email=" + URLEncoder.encode(email, "UTF-8") +
//                        "&update_date=1"; // Этот параметр указывает на тип запроса
//
//                // Отправляем данные запроса на сервер
//                OutputStream outputStream = connection.getOutputStream();
//                outputStream.write(postData.getBytes());
//                outputStream.flush();
//                outputStream.close();
//
//                // Получаем ответ от сервера
//                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    response += line;
//                }
//                reader.close();
//                connection.disconnect();
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e("HTTP_REQUEST", "Error occurred while sending request: " + e.getMessage());
//            }
//            return response;
//        }
//
//        @Override
//        protected void onPostExecute(String lastDate) {
//            super.onPostExecute(lastDate);
//            try {
//                int date = Integer.parseInt(lastDate);
//                // Обновляем значение DatePicker с полученной датой
//                if (!lastDate.isEmpty()) {
//                    String[] dateParts = lastDate.split("\\."); // Разбиваем строку с датой на составляющие
//                    int day = Integer.parseInt(dateParts[0]);
//                    int month = Integer.parseInt(dateParts[1]) - 1; // Месяцы в DatePicker начинаются с 0
//                    int year = Integer.parseInt(dateParts[2]);
//
//                    // Устанавливаем полученную дату в DatePicker
//                    datePicker.updateDate(year, month, day);
//                }
//            }
//            catch (NumberFormatException e) {
//                    // Если строка не является числом, выводим сообщение об ошибке
//                    Log.e("PARSE_ERROR", "Failed to parse last date: " + lastDate);
//                }
//        }
//    }

    private void updateTypeHoroText() {
        typeHoroTextView.setText(getString(R.string.horoscopeText) + " " + horoData[currentIndex]);
        // Определяем картинку в зависимости от значения typeHoro
        // Находим ImageView
        ImageView imageView = findViewById(R.id.Back_rotation_Img);
        switch (horoData[currentIndex]) {
            case "на Работу":
            case "for Work":
            case "工作":
                imageView.setImageResource(R.drawable.job);
                break;
            case "на Сегодня":
            case "for Today":
            case "今天":
                imageView.setImageResource(R.drawable.future);
                break;
            case "на Любовь":
            case "for Love":
            case "爱情":
                imageView.setImageResource(R.drawable.love);
                break;
            case "на Здоровье":
            case "for Health":
            case "为了健康":
                imageView.setImageResource(R.drawable.healthh);
                break;
            default:
                // Установите изображение по умолчанию или обработайте другие случаи, если необходимо
                break;
        }
    }
    private void shiftMainLayoutContent(String zodiacSign) {
        if (!zodiacSign.equals(currentZodiacSign)) {
            // Определите ImageView для анимации
            ImageView zodiacImageView = findViewById(R.id.zodiacImg);

            // Создайте анимацию затухания
            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setDuration(500); // Продолжительность анимации в миллисекундах

            // Создайте слушатель анимации затухания
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // Выполняем какие-то действия при старте анимации
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // После завершения анимации затухания, меняем изображение в ImageView
                    switch (zodiacSign) {
                        case "Aries":
                        case "Овен":
                        case "白羊座":
                            zodiacImageView.setImageResource(R.drawable.aries);
                            break;
                        case "Taurus":
                        case "Телец":
                        case "金牛座":
                            zodiacImageView.setImageResource(R.drawable.taurus);
                            break;
                        case "Gemini":
                        case "Близнецы":
                        case "双子座":
                            zodiacImageView.setImageResource(R.drawable.gemini);
                            break;
                        case "Cancer":
                        case "Рак":
                        case "巨蟹座":
                            zodiacImageView.setImageResource(R.drawable.cancer);
                            break;
                        case "Leo":
                        case "Лев":
                        case "狮子座":
                            zodiacImageView.setImageResource(R.drawable.leo);
                            break;
                        case "Virgo":
                        case "Дева":
                        case "处女座":
                            zodiacImageView.setImageResource(R.drawable.virgo);
                            break;
                        case "Libra":
                        case "Весы":
                        case "天秤座":
                            zodiacImageView.setImageResource(R.drawable.libra);
                            break;
                        case "Scorpio":
                        case "Скорпион":
                        case "天蝎座":
                            zodiacImageView.setImageResource(R.drawable.scorpio);
                            break;
                        case "Sagittarius":
                        case "Стрелец":
                        case "射手座":
                            zodiacImageView.setImageResource(R.drawable.saget);
                            break;
                        case "Capricorn":
                        case "Козерог":
                        case "摩羯座":
                            zodiacImageView.setImageResource(R.drawable.capric);
                            break;
                        case "Aquarius":
                        case "Водолей":
                        case "水瓶座":
                            zodiacImageView.setImageResource(R.drawable.accquarius);
                            break;
                        case "Pisces":
                        case "Рыбы":
                        case "双鱼座":
                            zodiacImageView.setImageResource(R.drawable.pisces);
                            break;
                        default:
                            // Если знак зодиака не распознан, установите изображение по умолчанию
                            break;
                    }

                // Создайте анимацию появления
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setDuration(500); // Продолжительность анимации в миллисекундах
                zodiacImageView.startAnimation(fadeIn); // Запускаем анимацию появления
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Выполняем какие-то действия при повторении анимации
            }
        });

        // Запускаем анимацию затухания
        zodiacImageView.startAnimation(fadeOut);

        // Обновляем текущий знак зодиака
        currentZodiacSign = zodiacSign;
    }
}




    private class SendRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String zodiacSign = strings[0];

            String response = "";
            try {
                URL url = new URL("https://testmobileapl.extecom.com//GPTrequest.php?horo=" + zodiacSign);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
                reader.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("HTTP_REQUEST", "Error occurred while sending request: " + e.getMessage());
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            // Переходим на активити с результатом
            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("response", response);
            startActivity(intent);

            // Закрываем текущую активити
            finish();
        }
    }
    private String getZodiacSign(int day, int month) {
        String zodiacSign;

        switch (month) {
            case 1: // Январь
                zodiacSign = (day <= 19) ? getString(R.string.capricorn) : getString(R.string.aquarius);
                break;
            case 2: // Февраль
                zodiacSign = (day <= 18) ? getString(R.string.aquarius) : getString(R.string.pisces);
                break;
            case 3: // Март
                zodiacSign = (day <= 20) ? getString(R.string.pisces) : getString(R.string.aries);
                break;
            case 4: // Апрель
                zodiacSign = (day <= 19) ? getString(R.string.aries) : getString(R.string.taurus);
                break;
            case 5: // Май
                zodiacSign = (day <= 20) ? getString(R.string.taurus) : getString(R.string.gemini);
                break;
            case 6: // Июнь
                zodiacSign = (day <= 20) ? getString(R.string.gemini) : getString(R.string.cancer);
                break;
            case 7: // Июль
                zodiacSign = (day <= 22) ? getString(R.string.cancer) : getString(R.string.leo);
                break;
            case 8: // Август
                zodiacSign = (day <= 22) ? getString(R.string.leo) : getString(R.string.virgo);
                break;
            case 9: // Сентябрь
                zodiacSign = (day <= 22) ? getString(R.string.virgo) : getString(R.string.libra);
                break;
            case 10: // Октябрь
                zodiacSign = (day <= 22) ? getString(R.string.libra) : getString(R.string.scorpio);
                break;
            case 11: // Ноябрь
                zodiacSign = (day <= 21) ? getString(R.string.scorpio) : getString(R.string.sagittarius);
                break;
            case 12: // Декабрь
                zodiacSign = (day <= 21) ? getString(R.string.sagittarius) : getString(R.string.capricorn);
                break;
            default:
                zodiacSign = null; // Неизвестный месяц
        }

        return zodiacSign;

    }

}

