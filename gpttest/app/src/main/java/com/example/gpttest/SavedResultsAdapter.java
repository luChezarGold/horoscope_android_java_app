package com.example.gpttest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SavedResultsAdapter extends RecyclerView.Adapter<SavedResultsAdapter.ViewHolder> {

    private List<String> savedResults;
    private Context context;
    private Button deleteButton;
    public SavedResultsAdapter(Context context, List<String> savedResults) {
        this.context = context;
        this.savedResults = savedResults;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_result_item, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String resultText = savedResults.get(holder.getAdapterPosition());
        holder.titleResultTextView.setText(resultText);

        // Обработчик нажатия на кнопку Delete
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    // Удаляем элемент из списка
                    savedResults.remove(clickedPosition);
                    notifyItemRemoved(clickedPosition);

                    // Удаляем данные из SharedPreferences
                    SharedPreferences preferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove("saved_result_" + clickedPosition);
                    editor.apply();

                    // Обновляем ключи в SharedPreferences для остальных сохраненных результатов
                    for (int i = clickedPosition + 1; i < savedResults.size() + 1; i++) {
                        editor.putString("saved_result_" + (i - 1), preferences.getString("saved_result_" + i, ""));
                    }
                    // Уменьшаем количество сохраненных результатов на 1
                    editor.putInt("number_of_saved_results", preferences.getInt("number_of_saved_results", 0) - 1);
                    editor.apply();
                }
            }
        });
    }




    @Override
    public int getItemCount() {
        return savedResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleResultTextView;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleResultTextView = itemView.findViewById(R.id.titleResultTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

}

