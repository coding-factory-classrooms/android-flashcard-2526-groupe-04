package com.example.flashcard;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity {

    private static final String TAG = "ItemListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Quiz.Question> questions = loadQuestionsFromJson();

        recyclerView.setAdapter(new ItemAdapter(questions));
    }

    private List<Quiz.Question> loadQuestionsFromJson() {
        List<Quiz.Question> questionList = new ArrayList<>();

        try {
            InputStream is = getAssets().open("questions.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONObject root = new JSONObject(json);

            if (!root.has("questions")) {
                Log.e(TAG, "Le JSON ne contient pas le champ 'questions' !");
                return questionList;
            }

            JSONArray questionsArray = root.getJSONArray("questions");

            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject q = questionsArray.getJSONObject(i);

                String answer = q.optString("answer", "Inconnu");
                String filePath = q.optString("filePath", "");
                int numberOfAnswers = q.optInt("numberOfAnswers", 0);
                String picture = q.optString("picture", "");

                questionList.add(new Quiz.Question(answer, filePath, numberOfAnswers, 0));
            }

        } catch (Exception e) {
            Log.e(TAG, "caca", e);
        }

        return questionList;
    }
}
