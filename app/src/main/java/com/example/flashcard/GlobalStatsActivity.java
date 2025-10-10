package com.example.flashcard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GlobalStatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_global_stats);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences("GlobalStats", MODE_PRIVATE);
        int totalQuestions = prefs.getInt("totalQuestions", 0);
        int correctAnswers = prefs.getInt("correctAnswers", 0);

        Button backToMenu = findViewById(R.id.buttonBackToMenu);
        TextView totalText = findViewById(R.id.textViewTotalQuestions);
        TextView correctText = findViewById(R.id.textViewCorrectAnswers);
        TextView percentText = findViewById(R.id.textViewPercentage);

        double percentage = (totalQuestions > 0)
                ? ((double) correctAnswers / totalQuestions) * 100
                : 0;

        totalText.setText(getString(R.string.global_questions_label, totalQuestions));
        correctText.setText(getString(R.string.global_correct_lable, correctAnswers));
        percentText.setText(getString(R.string.global_success_label, percentage));

        backToMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}