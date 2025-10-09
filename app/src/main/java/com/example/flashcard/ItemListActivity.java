package com.example.flashcard;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity {

    private static final String TAG = "ItemListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        QuizRepo.loadQuiz(this, 0, new QuizRepo.QuizCallback() {
            @Override
            public void onSuccess(Quiz quiz) {
                runOnUiThread(() -> {
                    List<Quiz.Question> data = quiz != null && quiz.questions != null
                            ? new ArrayList<>(quiz.questions)
                            : new ArrayList<>();
                    recyclerView.setAdapter(new ItemAdapter(data));
                });
            }

            @Override
            public void onError(Exception exception) {
                Log.e(TAG, "Failed to load questions", exception);
                runOnUiThread(() -> recyclerView.setAdapter(new ItemAdapter(new ArrayList<>())));
            }
        });
    }
}
