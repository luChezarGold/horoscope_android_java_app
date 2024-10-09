package com.example.gpttest;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;

public class AuthActivity extends AppCompatActivity {

    EditText editTextEmail;
    EditText editTextPassword;
    Button buttonRegister, buttonForgotPassword;
    TextView textViewEmailError;
    TextView text_passwordError;
    Animation pulseAnimation;
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable runnable;
    private static final String SERVER_URL = "https://testmobileapl.extecom.com//forgotPassword.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_auth);


        editTextEmail = findViewById(R.id.input_email);
        editTextPassword = findViewById(R.id.input_password);
        buttonRegister = findViewById(R.id.buttonAuth);
        text_passwordError = findViewById(R.id.text_passwordError);
        textViewEmailError = findViewById(R.id.text_email_error);
        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_anim);
        buttonForgotPassword = findViewById(R.id.buttonForgotPassword);

        ImageView AnimatedBack = findViewById(R.id.Back_rotation_Img);
        Animation anim_alpha = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        AnimatedBack.startAnimation(anim_alpha);

        // Проверяем сохраненные учетные данные при создании активности
        checkSavedCredentials();
        // Настройка таймера для проверки формата электронной почты каждые 2 секунды
        runnable = new Runnable() {
            @Override
            public void run() {
                checkEmailFormat();
                handler.postDelayed(this, 5000); // Повторяем через 2 секунды
            }
        };
        handler.postDelayed(runnable, 2000); // Запускаем сразу при создании активности

        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateAuthCode();
            }
        });


        // Обработчик нажатия кнопки "Регистрация"
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                if (isValidEmail(email)) {
                    // Если формат правильный, отправляем данные на сервер
                    sendDataToServer(email, password);
                } else {
                    // Если формат неправильный, запускаем анимацию пульсации TextView
                    textViewEmailError.startAnimation(pulseAnimation);
                }
            }
        });
    }
    private void checkSavedCredentials() {
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String savedEmail = preferences.getString("email", "");
        String savedPassword = preferences.getString("password", "");

        if (!TextUtils.isEmpty(savedEmail) && !TextUtils.isEmpty(savedPassword)) {
            // Делаем автоматический вход, используя сохраненные email и пароль
            loginUser(savedEmail, savedPassword);
        }
    }

    private void generateAuthCode() {
        // Отправка запроса на сервер для генерации проверочного кода
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // После успешного получения кода, откройте новое окно (Activity) для ввода этого кода
                        Intent intent = new Intent(AuthActivity.this, ForgotVerification.class);
                        intent.putExtra("auth_code", response);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Обработка ошибки при отправке запроса
                        Toast.makeText(AuthActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                });

        // Добавление запроса в очередь
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void loginUser(String email, String password) {
        // Создаем объект AsyncTask для выполнения запроса на сервер в фоновом потоке
        class LoginUserTask extends AsyncTask<String, Void, String> {
            // Метод doInBackground будет выполняться в фоновом потоке
            @Override
            protected String doInBackground(String... params) {
                String email = params[0];
                String password = params[1];
                try {
                    // Создаем URL для отправки запроса на сервер
                    URL url = new URL("https://testmobileapl.extecom.com//registration.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    // Формируем параметры запроса
                    String postData = "email=" + URLEncoder.encode(email, "UTF-8") +
                            "&password=" + URLEncoder.encode(password, "UTF-8") +
                            "&register=1"; // Параметр, указывающий на регистрацию

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

                    conn.disconnect();

                    return response.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            // Метод onPostExecute будет вызван после завершения выполнения doInBackground
            @Override
            protected void onPostExecute(String result) {
                // Здесь обрабатываем ответ от сервера
                if (result != null) {
                    if (result.equals("Вход успешен.")) {
                        // Если ответ успешный, переходим на MainActivity или какую-то другую активность
                        Intent intent = new Intent(AuthActivity.this, TitleMenu.class);
                        startActivity(intent);
                        finish(); // Закрываем текущую активность, чтобы пользователь не мог вернуться на нее кнопкой "Назад"
                    } else if (result.equals("Неверный пароль.")) {
                        // Если ответ "Неверный пароль.", показываем сообщение об ошибке
                        Toast.makeText(AuthActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                    } else {
                        // В случае другого ответа от сервера, также показываем сообщение об ошибке
                        Toast.makeText(AuthActivity.this, "Ошибка при входе", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Если произошла ошибка во время выполнения запроса, показываем сообщение об ошибке
                    Toast.makeText(AuthActivity.this, "Ошибка при входе", Toast.LENGTH_SHORT).show();
                }
            }
        }

        // Запускаем AsyncTask для выполнения запроса на сервер с передачей email и пароля
        new LoginUserTask().execute(email, password);
    }

    // Метод для проверки формата электронной почты
    private boolean isValidEmail(String email) {
        // Регулярное выражение для проверки формата электронной почты
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    // Метод для проверки формата электронной почты
    private void checkEmailFormat() {
        String email = editTextEmail.getText().toString();
        // Регулярное выражение для проверки формата электронной почты
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            // Если формат неправильный, устанавливаем текст и запускаем анимацию пульсации в textViewEmailError
            textViewEmailError.setText("Неправильный формат электронной почты");
            textViewEmailError.startAnimation(pulseAnimation);
        } else {
            // Если формат правильный, очищаем текст и останавливаем анимацию пульсации в textViewEmailError
            textViewEmailError.setText("");
            textViewEmailError.clearAnimation();
        }
    }

    // Метод для отправки данных на сервер
    private class SendDataToServerTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];
            try {
                URL url = new URL("https://testmobileapl.extecom.com//registration.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Формируем параметры запроса
                String postData = "email=" + URLEncoder.encode(email, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8") +
                        "&register=1"; // Параметр, указывающий на регистрацию

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

                // Выводим ответ от сервера
                Log.d("Server Response", response.toString());

                conn.disconnect();

                return response.toString();
            } catch (Exception e) {
                Log.d("Server Error", e.toString());
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if (result.equals("Пользователь зарегистрирован. Код подтверждения отправлен на указанный email.")) {
                    SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("email", editTextEmail.getText().toString());
                    editor.putString("password", editTextPassword.getText().toString());
                    editor.apply();

                    // Если пользователь успешно зарегистрирован, переходим на VerificationActivity
                    Intent intent = new Intent(AuthActivity.this, VerificationActivity.class);
                    intent.putExtra("email", editTextEmail.getText().toString());
                    startActivity(intent);
                } else if (result.equals("Вход успешен.")) {
                    SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("email", editTextEmail.getText().toString());
                    editor.putString("password", editTextPassword.getText().toString());
                    editor.apply();
                    // Если вход успешен, переходим на MainActivity
                    Intent intent = new Intent(AuthActivity.this, TitleMenu.class);
                    startActivity(intent);
                } else if (result.equals("Неверный пароль.")) {
                    // Если пароль неверный, запускаем анимацию пульсации в TextView
                    text_passwordError.setText("Неверный пароль!");
                    text_passwordError.startAnimation(pulseAnimation);
                } else if (result.equals("Account not verified.")) {
                    // Если аккаунт не подтвержден, выводим сообщение об ошибке
                    Log.d("Server Response", result.toString());
                    Toast.makeText(AuthActivity.this, result, Toast.LENGTH_SHORT).show();
                    SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("email", editTextEmail.getText().toString());
                    editor.putString("password", editTextPassword.getText().toString());
                    editor.apply();
                    Intent intent = new Intent(AuthActivity.this, VerificationActivity.class);
                    intent.putExtra("email", editTextEmail.getText().toString());
                    startActivity(intent);
                } else {
                    // Выводим сообщение об ошибке
                    Toast.makeText(AuthActivity.this, "Ошибка при регистрации или входе", Toast.LENGTH_SHORT).show();
                }
            } else {

                // Выводим сообщение об ошибке
                Toast.makeText(AuthActivity.this, "Ошибка при обработке ответа от сервера", Toast.LENGTH_SHORT).show();
            }
        }


    }

    // В методе sendDataToServer измените вызов на AsyncTask
    private void sendDataToServer(String email, String password) {
        new SendDataToServerTask().execute(email, password);
    }



}
