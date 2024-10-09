package com.example.gpttest;

import androidx.appcompat.app.AppCompatActivity;

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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ForgotVerification extends AppCompatActivity {

    private Button buttonSubmit;
    private String email;
    private EditText editTextVerificationCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_forgot_verification);
        editTextVerificationCode = findViewById(R.id.input_verification_code);
        buttonSubmit = findViewById(R.id.buttonSendVerification);
        email = getIntent().getStringExtra("email");
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
                    Toast.makeText(ForgotVerification.this, "Введите проверочный код", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void sendVerificationCodeToServer(String verificationCode) {
        new ForgotVerification.SendVerificationTask().execute(verificationCode);
    }
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
                    Toast.makeText(ForgotVerification.this, result, Toast.LENGTH_SHORT).show();
                }
            } else {
                // Ошибка отправки данных на сервер
                Toast.makeText(ForgotVerification.this, "Ошибка отправки данных на сервер", Toast.LENGTH_SHORT).show();
                Log.e("Send Verification Error", "Ошибка отправки данных на сервер ");
            }
        }
        private void redirectToMainActivity() {
            // Define what you want to do when redirecting to the MainActivity
            // For example, you can create an Intent and start the MainActivity
            Intent intent = new Intent(ForgotVerification.this, MainActivity.class);
            startActivity(intent);
            finish(); // Optional, if you want to finish the current activity
        }

    }

}