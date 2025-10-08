package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button "Jouer"
        Button quiz = findViewById(R.id.quizButton);
        quiz.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuizActivity.class);
            startActivity(intent);
        });

        // Button "Statistiques"
        Button stats = findViewById(R.id.statsButton);
        stats.setOnClickListener(v -> {
            Intent intent = new Intent(this, GlobalStatsActivity.class);
            startActivity(intent);
        });

        // Button "Liste des questions"
        Button list = findViewById(R.id.itemListButton);
        list.setOnClickListener(v -> {
            Intent intent = new Intent(this, ItemListActivity.class);
            startActivity(intent);
        });

        // Button "À propos"
        Button about = findViewById(R.id.aboutButton);
        about.setOnClickListener(v -> {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        });

    }
}