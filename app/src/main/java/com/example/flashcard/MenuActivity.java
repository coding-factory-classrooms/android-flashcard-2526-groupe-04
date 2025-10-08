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

        Log.d(TAG, "Hello Flashcard");

        Button btnTestStats = findViewById(R.id.btnTestStats);
        btnTestStats.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, StatsActivity.class);
            intent.putExtra("QUIZ_DIFFICULTY", "Facile");  // Dummy data
            intent.putExtra("CORRECT_ANSWERS", 5);
            intent.putExtra("TOTAL_QUESTIONS", 10);
            startActivity(intent);
        });
    }
}