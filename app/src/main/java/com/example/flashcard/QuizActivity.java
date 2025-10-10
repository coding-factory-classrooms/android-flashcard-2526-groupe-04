package com.example.flashcard;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Process;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    private static final String EXTRA_DIFFICULTY = "difficulty";
    public static final String EXTRA_SINGLE_QUESTION = "EXTRA_SINGLE_QUESTION";
    private static final String EXTRA_STATS_DIFFICULTY = "QUIZ_DIFFICULTY";
    private static final String EXTRA_STATS_CORRECT = "CORRECT_ANSWERS";
    private static final String EXTRA_STATS_TOTAL = "TOTAL_QUESTIONS";
    private static final int SINGLE_QUESTION_DIFFICULTY = -1;
    public static final String EXTRA_HARDCORE_MODE = "extra_hardcore_mode";
    public static final String EXTRA_TIME_ATTACK_MODE = "extra_time_attack_mode";

    private Quiz quiz;
    private Quiz.Question currentQuestion;
    private int currentQuestionIndex = 0;
    private boolean answered = false;
    private MediaPlayer mediaPlayer;
    private int correctAnswers = 0;
    private String difficultyLabel;
    private DifficultyConfig difficultyConfig;
    private boolean singleQuestionMode = false;
    private boolean hardcoreMode = false;
    private boolean timeAttackMode = false;
    private int defaultResultTextColor;
    private int selectedDifficultyLevel = 0;

    private RadioGroup answerRadioGroup;
    private TextView questionTextView;
    private TextView resultTextView;
    private TextView timerTextView;
    private ImageView questionImageView;
    private Button validationButton;
    private Button playButton;
    private MediaPlayer explosionPlayer;
    private CountDownTimer questionTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();

        Quiz.Question singleQuestion = extractSingleQuestionFromIntent();
        if (singleQuestion != null) {
            startSingleQuestionMode(singleQuestion);
            return;
        }

        Intent launchIntent = getIntent();
        int difficultyLevel = launchIntent != null ? launchIntent.getIntExtra(EXTRA_DIFFICULTY, 0) : 0;
        selectedDifficultyLevel = difficultyLevel;
        hardcoreMode = launchIntent != null && launchIntent.getBooleanExtra(EXTRA_HARDCORE_MODE, false);
        timeAttackMode = launchIntent != null && launchIntent.getBooleanExtra(EXTRA_TIME_ATTACK_MODE, false);

        difficultyConfig = resolveDifficultyConfig(difficultyLevel);
        difficultyLabel = getString(difficultyConfig.labelResId);

        if (hardcoreMode) {
            difficultyConfig = new DifficultyConfig(difficultyConfig.questionCount, 6, R.string.quiz_difficulty_hardcore);
            difficultyLabel = getString(R.string.quiz_difficulty_hardcore);
        }

        fetchQuizData(this, difficultyLevel, difficultyConfig.choiceCount);
    }

    private void initViews() {
        answerRadioGroup = findViewById(R.id.answerRadioGroup);
        questionTextView = findViewById(R.id.questionTextView);
        resultTextView = findViewById(R.id.resultText);
        timerTextView = findViewById(R.id.timerTextView);
        questionImageView = findViewById(R.id.imageView);

        validationButton = findViewById(R.id.validationButton);
        playButton = findViewById(R.id.audioButton);

        defaultResultTextColor = resultTextView.getCurrentTextColor();

        questionTextView.setText(getString(R.string.quiz_loading));
        resultTextView.setVisibility(View.INVISIBLE);
        validationButton.setEnabled(false);
        playButton.setEnabled(false);

        playButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        });

        validationButton.setOnClickListener(v -> {
            if (quiz == null || currentQuestion == null) {
                return;
            }

            if (!answered) {
                if (checkCurrentAnswer()) {
                    answered = true;
                    validationButton.setText(hasNextQuestion() ? getString(R.string.quiz_next) : getString(R.string.quiz_finish));
                }
            } else {
                goToNextStep();
            }
        });
    }

    private void fetchQuizData(Context context, int difficulty, int choiceCount) {
        QuizRepo.loadQuiz(context, difficulty, choiceCount, new QuizRepo.QuizCallback() {
            @Override
            public void onSuccess(Quiz loadedQuiz) {
                runOnUiThread(() -> {
                    quiz = loadedQuiz;
                    if (quiz == null || quiz.questions == null || quiz.questions.isEmpty()) {
                        showErrorAndFinish(getString(R.string.quiz_error_no_questions));
                        return;
                    }
                    currentQuestionIndex = 0;
                    correctAnswers = 0;
                    applyDifficultyRules();
                    showQuestion();
                });
            }

            @Override
            public void onError(Exception exception) {
                runOnUiThread(() -> showErrorAndFinish(getString(R.string.quiz_error_load)));
            }
        });
    }

    private Quiz.Question extractSingleQuestionFromIntent() {
        if (getIntent() == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return getIntent().getParcelableExtra(EXTRA_SINGLE_QUESTION, Quiz.Question.class);
        }
        return getIntent().getParcelableExtra(EXTRA_SINGLE_QUESTION);
    }

    private void startSingleQuestionMode(Quiz.Question question) {
        if (question == null) {
            showErrorAndFinish(getString(R.string.quiz_error_no_questions));
            return;
        }

        ArrayList<Quiz.Question> singleQuestionList = new ArrayList<>();
        singleQuestionList.add(question);
        quiz = new Quiz(singleQuestionList, SINGLE_QUESTION_DIFFICULTY);
        currentQuestionIndex = 0;
        correctAnswers = 0;
        singleQuestionMode = true;
        showQuestion();
    }

    private void showQuestion() {
        releaseMediaPlayer();
        cancelTimeAttackTimer();

        currentQuestion = quiz.questions.get(currentQuestionIndex);
        answered = false;
        resultTextView.setVisibility(View.INVISIBLE);
        resultTextView.setTextColor(defaultResultTextColor);
        validationButton.setEnabled(true);
        validationButton.setText(getString(R.string.quiz_validate));

        questionTextView.setText(getString(R.string.quiz_progress, currentQuestionIndex + 1, quiz.numberOfQuestions));

        questionImageView.setVisibility(View.INVISIBLE);
        questionImageView.setImageDrawable(null);

        populateAnswers(currentQuestion.getChoices());
        mediaPlayer = setupMediaPlayer(currentQuestion.filePath);
        playButton.setEnabled(mediaPlayer != null);

        startTimeAttackTimer();
    }

    private void populateAnswers(List<String> choices) {
        answerRadioGroup.removeAllViews();
        answerRadioGroup.clearCheck();

        if (choices == null || choices.isEmpty()) {
            return;
        }

        for (String choice : choices) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(choice);
            answerRadioGroup.addView(radioButton);
        }

        setAnswerInputsEnabled(true);
    }

    private MediaPlayer setupMediaPlayer(String audioName) {
        if (audioName == null || audioName.isEmpty()) {
            return null;
        }
        int resourceId = getResources().getIdentifier(audioName.toLowerCase(Locale.ROOT), "raw", getPackageName());
        if (resourceId == 0) {
            return null;
        }
        return MediaPlayer.create(this, resourceId);
    }

    private boolean checkCurrentAnswer() {
        int checkedId = answerRadioGroup.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, getString(R.string.quiz_select_answer), Toast.LENGTH_SHORT).show();
            return false;
        }

        RadioButton button = findViewById(checkedId);
        String userAnswer = button.getText().toString();
        boolean isCorrect = userAnswer.equalsIgnoreCase(currentQuestion.answer);

        cancelTimeAttackTimer();

        resultTextView.setVisibility(VISIBLE);
        resultTextView.setTextColor(defaultResultTextColor);
        resultTextView.setText(isCorrect ? getString(R.string.quiz_correct) : getString(R.string.quiz_wrong));

        if (isCorrect) {
            correctAnswers += 1;
        } else if (hardcoreMode) {
            handleHardcoreFailure();
            return false;
        }

        if (currentQuestion.picture != 0) {
            questionImageView.setImageResource(currentQuestion.picture);
            questionImageView.setVisibility(View.VISIBLE);
        }

        return true;
    }

    private boolean hasNextQuestion() {
        return quiz != null && currentQuestionIndex < quiz.numberOfQuestions - 1;
    }

    private void goToNextStep() {
        cancelTimeAttackTimer();

        if (hasNextQuestion()) {
            currentQuestionIndex += 1;
            showQuestion();
        } else {
            if (singleQuestionMode) {
                finish();
                return;
            }
            Intent intent = new Intent(this, StatsActivity.class);
            intent.putExtra(EXTRA_STATS_DIFFICULTY, difficultyLabel);
            intent.putExtra(EXTRA_STATS_CORRECT, correctAnswers);
            intent.putExtra(EXTRA_STATS_TOTAL, quiz != null ? quiz.numberOfQuestions : currentQuestionIndex + 1);
            startActivity(intent);
            finish();
        }
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void startTimeAttackTimer() {
        if (!timeAttackMode || singleQuestionMode) {
            if (timerTextView != null) {
                timerTextView.setVisibility(View.GONE);
            }
            return;
        }

        long timeLimitMillis = resolveTimeAttackMillis();
        if (timeLimitMillis <= 0L) {
            timerTextView.setVisibility(View.GONE);
            return;
        }

        timerTextView.setVisibility(View.VISIBLE);
        updateTimerText(timeLimitMillis);

        questionTimer = new CountDownTimer(timeLimitMillis, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimerText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (timerTextView != null) {
                    timerTextView.setText(getString(R.string.quiz_timer_expired));
                }
                handleTimeAttackFailure();
            }
        }.start();
    }

    private void cancelTimeAttackTimer() {
        if (questionTimer != null) {
            questionTimer.cancel();
            questionTimer = null;
        }
    }

    private void handleTimeAttackFailure() {
        if (answered) {
            return;
        }

        cancelTimeAttackTimer();
        answered = true;
        setAnswerInputsEnabled(false);

        if (hardcoreMode) {
            handleHardcoreFailure();
            return;
        }

        resultTextView.setVisibility(VISIBLE);
        resultTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        resultTextView.setText(getString(R.string.quiz_timer_expired));

        validationButton.setEnabled(true);
        validationButton.setText(hasNextQuestion() ? getString(R.string.quiz_next) : getString(R.string.quiz_finish));
    }

    private void updateTimerText(long millisRemaining) {
        if (timerTextView == null) {
            return;
        }
        long totalSeconds = millisRemaining / 1000L;
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        timerTextView.setText(String.format(Locale.getDefault(), "%01d:%02d", minutes, seconds));
    }

    private long resolveTimeAttackMillis() {
        if (hardcoreMode) {
            return 10_000L;
        }
        switch (selectedDifficultyLevel) {
            case 0:
                return 60_000L;
            case 1:
                return 30_000L;
            case 2:
                return 20_000L;
            case 3:
                return 10_000L;
            default:
                return 45_000L;
        }
    }

    private void handleHardcoreFailure() {
        cancelTimeAttackTimer();
        resultTextView.setText("Explosion ! Mauvaise réponse en mode Hardcore.");
        resultTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        validationButton.setEnabled(false);
        validationButton.setText("Boom !!");
        setAnswerInputsEnabled(false);

        View root = findViewById(R.id.main);
        if (root != null) {
            root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        }

        playExplosionSound();
        validationButton.postDelayed(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                finishAffinity();
            }
            finishAffinity();
            Process.killProcess(Process.myPid());
            System.exit(0);
        }, 2500L);
    }

    private void setAnswerInputsEnabled(boolean enabled) {
        for (int i = 0; i < answerRadioGroup.getChildCount(); i++) {
            View child = answerRadioGroup.getChildAt(i);
            child.setEnabled(enabled);
        }
        answerRadioGroup.setEnabled(enabled);
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
        releaseExplosionPlayer();
        cancelTimeAttackTimer();
    }

    private DifficultyConfig resolveDifficultyConfig(int difficulty) {
        switch (difficulty) {
            case 0:
                return new DifficultyConfig(4, 3, R.string.quiz_difficulty_easy);
            case 1:
                return new DifficultyConfig(8, 4, R.string.quiz_difficulty_medium);
            case 2:
                return new DifficultyConfig(13, 5, R.string.quiz_difficulty_hard);
            case 3:
                return new DifficultyConfig(Integer.MAX_VALUE, 6, R.string.quiz_difficulty_hardcore);
            default:
                return new DifficultyConfig(4, 4, R.string.quiz_difficulty_unknown);
        }
    }

    private void applyDifficultyRules() {
        if (quiz == null || quiz.questions == null || difficultyConfig == null) {
            return;
        }

        Collections.shuffle(quiz.questions);
        int desiredCount = Math.min(difficultyConfig.questionCount, quiz.questions.size());
        if (desiredCount < quiz.questions.size()) {
            quiz.questions = new ArrayList<>(quiz.questions.subList(0, desiredCount));
        } else {
            quiz.questions = new ArrayList<>(quiz.questions);
        }
        quiz.numberOfQuestions = quiz.questions.size();

        for (Quiz.Question question : quiz.questions) {
            if (question == null || question.choices == null) {
                continue;
            }
            if (question.choices.size() > difficultyConfig.choiceCount) {
                question.choices = new ArrayList<>(question.choices.subList(0, difficultyConfig.choiceCount));
            }
            question.numberOfAnswers = question.choices.size();
        }
    }

    private static class DifficultyConfig {
        final int questionCount;
        final int choiceCount;
        final int labelResId;

        DifficultyConfig(int questionCount, int choiceCount, int labelResId) {
            this.questionCount = questionCount;
            this.choiceCount = choiceCount;
            this.labelResId = labelResId;
        }
    }


    private void playExplosionSound() {
        releaseExplosionPlayer();
        explosionPlayer = MediaPlayer.create(this, R.raw.explosion);
        if (explosionPlayer == null) {
            return;
        }
        explosionPlayer.setLooping(false);
        explosionPlayer.setVolume(1f, 1f);
        explosionPlayer.setOnCompletionListener(player -> {
            player.release();
            explosionPlayer = null;
        });
        explosionPlayer.start();
    }

    private void releaseExplosionPlayer() {
        if (explosionPlayer != null) {
            explosionPlayer.release();
            explosionPlayer = null;
        }
    }
}
