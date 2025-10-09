package com.example.flashcard;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.Random;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

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

        boolean answered = false;
        //int currentQuestionIndex = getIntent().getParcelableExtra("currentQuestionIndex");
        int currentQuestionIndex = 0;//a set dans le menu et a transferer a chaque itération de questions
        Quiz.Question question1 = new Quiz.Question("vrai", "prout", 3, R.drawable.drapeau);
        ArrayList<Quiz.Question> liste = new ArrayList<Quiz.Question>();
        liste.add(question1);
        //Quiz quiz = getIntent().getParcelableExtra("Quiz"); <- quand tout sera relié
        Quiz quiz = new Quiz(liste, 1);
        Quiz.Question currentQuestion = quiz.questions.remove(getRandom(1,quiz.numberOfQuestions)-1);
        dynamicAnswers(currentQuestion);
        MediaPlayer mediaPlayer = setup(currentQuestion);
        Button play = findViewById(R.id.audioButton);
        play.setOnClickListener(view -> {
            mediaPlayer.start();
        });
        Button validButton = findViewById(R.id.validationButton);
        validButton.setOnClickListener(view -> {
            if (!answered) {
                checkResult(currentQuestion);
                validButton.setText("Continuer"); //<-le button pour passer a la question suivante marche pas mais tout y est il manque juste la condition du if
            } else {
                Intent intent = new Intent(this, QuizActivity.class);
                intent.putExtra("currentQuestionIndex", currentQuestionIndex + 1);
                intent.putExtra("Quiz", quiz);

                startActivity(intent);
            }
            });
    }
    public static int getRandom(int min, int max) {

        int range = (max - min) + 1;
        int random = (int) ((range * Math.random()) + min);
        return random;
    }
    public MediaPlayer setup(Quiz.Question currentQuestion){
        final int resourceId = getResources().getIdentifier(currentQuestion.filePath,"raw",this.getPackageName());
        MediaPlayer mediaPlayer = MediaPlayer.create(this,resourceId);
        setupAnswers(currentQuestion);
        return mediaPlayer;
    }
    public void dynamicAnswers(Quiz.Question question){
        // pour chaque reponses que je veux, je créer un radioButton
        RadioGroup radioGroup = findViewById(R.id.answerRadioGroup);
        for(int i = 0; i < question.numberOfAnswers; i++ ){
            // instancie nouveau radioButton
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(question.answer);
            radioGroup.addView(radioButton);
        }
    }
    public void setupAnswers(Quiz.Question question){
        ArrayList<String> currentAnswers = new ArrayList<String>();
        RadioGroup answerRadioGroup = findViewById(R.id.answerRadioGroup);
        int indexGoodAnswer = (int) getRandom(0,question.numberOfAnswers-1);
        for(int i = 0 ; i < question.numberOfAnswers;i++){
            if(i == indexGoodAnswer){
                int id = answerRadioGroup.getChildAt(indexGoodAnswer).getId();
                TextView view = findViewById(id);
                currentAnswers.add(question.answer);
                view.setText(question.answer);
            }else{
                int id = answerRadioGroup.getChildAt(i).getId();
                TextView view = findViewById(id);
                currentAnswers.add("Mauvaise Réponse");//<-
                view.setText("Mauvaise Réponse"); //<- A REMPLACER PAR UNE MAUVAISE REPONSE ALEATOIRE qui n'est pas deja dans currentAnswers
            }
        }
        //for(int i;i <
    }

    public void checkResult(Quiz.Question question){
        RadioGroup radioGroup = findViewById(R.id.answerRadioGroup);

            int idChecked = radioGroup.getCheckedRadioButtonId();
            RadioButton answer = findViewById(idChecked);
            String userAnswer = answer.getText().toString();
            TextView textView = findViewById(R.id.resultText);
            if(userAnswer.equals(question.answer)){
                textView.setText("Bonne Réponse");
                textView.setVisibility(VISIBLE);
            }
            else {
                textView.setText("Mauvaise Réponse");
                textView.setVisibility(VISIBLE);
            }


    };

}
