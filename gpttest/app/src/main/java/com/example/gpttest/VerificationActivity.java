package com.example.gpttest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class VerificationActivity extends AppCompatActivity {
    private String email;
    EditText editTextVerificationCode;
    Button buttonSubmit;
    private class SendVerificationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String verificationCode = params[0];
            try {
                URL url = new URL("https://testmobileapl.extecom.com//registration.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Формируем параметры запроса, включая адрес электронной почты
                String postData = "email=" + URLEncoder.encode(email, "UTF-8") +
                        "&verification_code=" + URLEncoder.encode(verificationCode, "UTF-8") +
                        "&verify=1"; // Параметр, указывающий на проверку кода подтверждения

                // Отправляем данные на сервер
                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                // Читаем ответ от сервера
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Возвращаем ответ от сервера
                return response.toString();
            } catch (Exception e) {
                Log.e("Send Verification Error", "Ошибка отправки данных на сервер", e);
                return null;
            }
        }

        @Override

        protected void onPostExecute(String result) {
            if (result != null) {
                // Проверяем ответ от сервера
                if (result.equals("Код подтверждения верный. Аккаунт успешно подтвержден.")) {
                    // Переходим к MainActivity
                    SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("email", email.toString());
                    editor.apply();
                    redirectToMainActivity();
                } else {
                    // Показываем сообщение об ошибке
                    Toast.makeText(VerificationActivity.this, result, Toast.LENGTH_SHORT).show();
                }
            } else {
                // Ошибка отправки данных на сервер
                Toast.makeText(VerificationActivity.this, "Ошибка отправки данных на сервер", Toast.LENGTH_SHORT).show();
                Log.e("Send Verification Error", "Ошибка отправки данных на сервер ");
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_verification);
        // Получаем значение email из Intent
        email = getIntent().getStringExtra("email");
        editTextVerificationCode = findViewById(R.id.input_verification_code);
        buttonSubmit = findViewById(R.id.buttonSendVerification);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verificationCode = editTextVerificationCode.getText().toString().trim();

                // Проверка, что код не пустой
                if (!verificationCode.isEmpty()) {
                    // Отправить код на сервер для проверки
                    sendVerificationCodeToServer(verificationCode);
                } else {
                    // Сообщить пользователю, что код пустой
                    Toast.makeText(VerificationActivity.this, "Введите проверочный код", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Метод для отправки кода на сервер для проверки
    private void sendVerificationCodeToServer(String verificationCode) {
        new SendVerificationTask().execute(verificationCode);
    }



    // Метод для перехода к MainActivity
    private void redirectToMainActivity() {

        Intent intent = new Intent(VerificationActivity.this, TitleMenu.class);
        startActivity(intent);
        finish(); // Опционально: закрыть текущую активность, чтобы пользователь не мог вернуться по кнопке "назад"
    }

}
