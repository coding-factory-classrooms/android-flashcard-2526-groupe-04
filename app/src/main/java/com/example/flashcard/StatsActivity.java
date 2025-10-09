package com.example.flashcard;

import android.os.Bundle;

import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.Locale;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stats);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView textViewDifficulty = findViewById(R.id.textViewDifficulty);
        TextView textViewScore = findViewById(R.id.textViewScore);
        TextView textViewPercentage = findViewById(R.id.textViewPercentage);

        String difficulty = getIntent().getStringExtra("QUIZ_DIFFICULTY");
        int correctAnswers = getIntent().getIntExtra("CORRECT_ANSWERS", 0);
        int totalQuestions = getIntent().getIntExtra("TOTAL_QUESTIONS", 0);

        double percentage = (totalQuestions > 0)
                ? ((double) correctAnswers / totalQuestions) * 100
                : 0;

        textViewDifficulty.setText(getString(R.string.difficulty_label, difficulty));
        textViewScore.setText(getString(R.string.score_label, correctAnswers, totalQuestions));
        textViewPercentage.setText(getString(R.string.success_label, percentage));
    }
}