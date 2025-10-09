package com.example.flashcard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;

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
            showListView();
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

    private void showListView() {
        String[] items = {
                "Facile",
                "Moyen",
                "Difficile",
                "Hardcore"
        };

        // Création du dictionnaire "traduction"
        HashMap<String, Integer> traduction = new HashMap<>();

        // Dictionnaire permettant de traduire le String en int pour le QuizActivity
        traduction.put("Facile", 0);
        traduction.put("Moyen", 1);
        traduction.put("Difficile", 2);
        traduction.put("Hardcore", 3);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select diff");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Récupère la difficulté choisie
                String choix = items[which];

                // Traduction en entier
                Integer difficulty = traduction.get(choix);

                android.util.Log.d(TAG, "Difficulty: " + difficulty);

                Intent intent = new Intent(MenuActivity.this, QuizActivity.class);
                intent.putExtra("difficulty", difficulty);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
