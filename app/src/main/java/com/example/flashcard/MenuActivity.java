package com.example.flashcard;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
        quiz.setOnClickListener(v -> showQuizOptionsDialog());

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
        Button aboutButton = findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, AboutActivity.class);
            startActivity(intent);
        });

    }

    private void showQuizOptionsDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_quiz_options, null);
        RadioGroup difficultyGroup = dialogView.findViewById(R.id.difficultyGroup);
        CheckBox hardcoreCheckBox = dialogView.findViewById(R.id.hardcoreModeCheckbox);
        CheckBox timeAttackCheckBox = dialogView.findViewById(R.id.timeAttackCheckbox);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.quiz_options_title);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.quiz_options_confirm, (dialog, which) -> {
            int selectedDifficultyId = difficultyGroup.getCheckedRadioButtonId();
            int difficulty = 0;

            if (selectedDifficultyId != -1) {
                RadioButton selectedButton = dialogView.findViewById(selectedDifficultyId);
                if (selectedButton != null) {
                    Object tag = selectedButton.getTag();
                    if (tag != null) {
                        difficulty = Integer.parseInt(tag.toString());
                    }
                }
            }

            Intent intent = new Intent(MenuActivity.this, QuizActivity.class);
            intent.putExtra("difficulty", difficulty);
            intent.putExtra(QuizActivity.EXTRA_HARDCORE_MODE, hardcoreCheckBox.isChecked());
            intent.putExtra(QuizActivity.EXTRA_TIME_ATTACK_MODE, timeAttackCheckBox.isChecked());
            startActivity(intent);
        });
        builder.setNegativeButton(R.string.quiz_options_cancel, (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}