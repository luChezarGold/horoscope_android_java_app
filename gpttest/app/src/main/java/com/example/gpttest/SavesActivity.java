package com.example.gpttest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
public class SavesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SavedResultsAdapter adapter;
    private List<String> savedResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.setLocale(this, LocaleUtils.getLocale(this));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_saves);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        savedResults = loadSavedResultsFromSharedPreferences();
        adapter = new SavedResultsAdapter(this, savedResults);
        recyclerView.setAdapter(adapter);

        Button deleteSavesButton = findViewById(R.id.deleteSavesButton);
        deleteSavesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSavedResultsFromSharedPreferences();
                savedResults.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private List<String> loadSavedResultsFromSharedPreferences() {
        List<String> results = new ArrayList<>();
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        int numberOfSavedResults = preferences.getInt("number_of_saved_results", 0);

        for (int i = 0; i < numberOfSavedResults; i++) {
            String combinedText = preferences.getString("saved_result_" + i, "");
            String[] parts = combinedText.split("\\|"); // Разделитель, который вы использовали при сохранении

            // Здесь parts[0] будет заголовком, а parts[1] - текстом гороскопа
            String titleText = parts[0];
            String resultText = parts[1];

            // Добавляем заголовок и текст гороскопа в список для отображения в списке сохраненных результатов
            String fullText = titleText + "\n" + resultText;
            results.add(fullText);
        }

        return results;
    }




    private void deleteSavedResultsFromSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int numberOfSavedResults = preferences.getInt("number_of_saved_results", 0);
        for (int i = 0; i < numberOfSavedResults; i++) {
            editor.remove("saved_result_" + i);
        }
        editor.putInt("number_of_saved_results", 0); // Обнуляем количество сохраненных результатов
        editor.apply();
    }
}
