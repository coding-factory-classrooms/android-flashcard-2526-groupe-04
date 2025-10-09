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
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.prout);
        Button play = findViewById(R.id.audioButton);
        play.setOnClickListener(view -> {
            mediaPlayer.start();
        });
        Quiz.Question question1 = new Quiz.Question("vrai","fff",3,R.drawable.drapeau);
        ArrayList<Quiz.Question> liste = new ArrayList<Quiz.Question>();
        liste.add(question1);
        Quiz quiz = new Quiz(liste,1);
        setupAnswers(quiz.questions.get(0));


        Button validButton = findViewById(R.id.validationButton);
        validButton.setOnClickListener(view ->{
            Log.d("QuizActivity","test ");
            checkResult(quiz.questions.get(0));
        });


    }

    public void scoreSharing(){
        // we retrieve the player's score with Intent
        Intent percentage = getIntent();
        // we initialize it in a variable of the same type
        double sendpercentage = percentage.getDoubleExtra("percentage" , 0);
        // we write the message that will be shared
        String message = "Mon score était de " + percentage +"% sur Classe Royal !!";
        // we create a new Intent which will be shared by the user
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        // createChooser allows to open the Android share menu
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
    public void setupAnswers(Quiz.Question question){
        RadioGroup answerRadioGroup = findViewById(R.id.answerRadioGroup);
        int id = answerRadioGroup.getChildAt((int) (Math.random()*3)).getId();
        TextView view = findViewById(id);
        view.setText(question.answer);
    }

    public void checkResult(Quiz.Question question){
        RadioGroup radioGroup = findViewById(R.id.answerRadioGroup);

            int idChecked = radioGroup.getCheckedRadioButtonId();
            Log.d("QuizActivity","caca");
            RadioButton answer = findViewById(radioGroup.getChildAt(idChecked).getId());
            String userAnswer = answer.getText().toString();
            TextView textView = findViewById(R.id.resultText);
            if(userAnswer.equals(question.answer)){
                textView.setText("Bien joué");
                textView.setVisibility(VISIBLE);
            }
            else {
                textView.setText("t'as perdu");
                textView.setVisibility(VISIBLE);
            }

    };

}