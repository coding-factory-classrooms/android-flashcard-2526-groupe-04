package com.example.flashcard;

import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.text.BoringLayout;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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


        Button validButton = findViewById(R.id.validationButton);
        validButton.setOnClickListener(view ->{
            checkResult(quiz.questions.get(0));
        });


    }

    public void checkResult(Quiz.Question question){
        RadioGroup radioGroup = findViewById(R.id.answerRadioGroup);

            int idChecked = radioGroup.getCheckedRadioButtonId();
            RadioButton answer = findViewById(radioGroup.getChildAt(idChecked).getId());
            String text = answer.getText().toString();
            TextView textView =findViewById(R.id.resultText);
            if(text == question.answer){
                textView.setText("Bien joué");
                textView.setVisibility(VISIBLE);
            }
            else {
                textView.setText("t'as perdu");
                textView.setVisibility(VISIBLE);
            }

    };

}