package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StatsActivity extends AppCompatActivity {

    private static final String EXTRA_STATS_DIFFICULTY = "QUIZ_DIFFICULTY";
    private static final String EXTRA_STATS_CORRECT = "CORRECT_ANSWERS";
    private static final String EXTRA_STATS_TOTAL = "TOTAL_QUESTIONS";
    private static final String EXTRA_FAILED_ANSWERS = "EXTRA_FAILED_ANSWERS";

    ArrayList<Quiz.Question> failedQuestions = new ArrayList<Quiz.Question>();

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
        failedQuestions = getIntent().getParcelableArrayListExtra(EXTRA_FAILED_ANSWERS);
        if (failedQuestions == null) {
            failedQuestions = new ArrayList<>();
        }
        showFailedAnswer(failedQuestions);
        TextView textViewDifficulty = findViewById(R.id.textViewDifficulty);
        TextView textViewScore = findViewById(R.id.textViewScore);
        TextView textViewPercentage = findViewById(R.id.textViewPercentage);
        Button backToMenu = findViewById(R.id.buttonBackToMenu);
        Button Share = findViewById(R.id.buttonShare);

        String difficulty = getIntent().getStringExtra(EXTRA_STATS_DIFFICULTY);
        if (difficulty == null || difficulty.isEmpty()) {
            difficulty = getString(R.string.quiz_difficulty_unknown);
        }
        int correctAnswers = getIntent().getIntExtra(EXTRA_STATS_CORRECT, 0);
        int totalQuestions = getIntent().getIntExtra(EXTRA_STATS_TOTAL, 0);


        double percentage = (totalQuestions > 0)
                ? ((double) correctAnswers / totalQuestions) * 100
                : 0;

        textViewDifficulty.setText(getString(R.string.difficulty_label, difficulty));
        textViewScore.setText(getString(R.string.score_label, correctAnswers, totalQuestions));
        textViewPercentage.setText(getString(R.string.success_label, percentage));

        backToMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        Share.setOnClickListener(v -> {
            scoreSharing(percentage);
        });
    }

    public void scoreSharing(double percentageSend){
        String message = getString(R.string.share_message, percentageSend);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);

    }
    private void showFailedAnswer(ArrayList<Quiz.Question> choices) {
        LinearLayout showFailedAnswerLinearLayout = findViewById(R.id.showFailedAnswerLinearLayout);
        if (choices == null || choices.isEmpty()) {
            return;
        }
        for (Quiz.Question choice : choices) {
            Button button = new Button(this);
            button.setText(choice.answer);
            showFailedAnswerLinearLayout.addView(button);
            button.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), QuizActivity.class);
                intent.putExtra(QuizActivity.EXTRA_SINGLE_QUESTION, choice);
                v.getContext().startActivity(intent);
            });
        }
    }
}