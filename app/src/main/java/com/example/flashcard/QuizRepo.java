package com.example.flashcard;

import android.content.Context;

import androidx.annotation.VisibleForTesting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class QuizRepo {

    private static final String QUIZ_ENDPOINT = "https://students.gryt.tech/api/L2/classeroyale/";
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final Random RANDOM = new Random();
    private static final int DEFAULT_CHOICE_COUNT = 4;

    private QuizRepo() {
    }

    public interface QuizCallback {
        void onSuccess(Quiz quiz);

        void onError(Exception exception);
    }

    static void loadQuiz(Context context, int difficulty, QuizCallback callback) {
        loadQuiz(context, difficulty, DEFAULT_CHOICE_COUNT, callback);
    }

    static void loadQuiz(Context context, int difficulty, int choiceCount, QuizCallback callback) {
        Request request = new Request.Builder()
                .url(QUIZ_ENDPOINT)
                .get()
                .build();

        CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    callback.onError(new IOException("Unexpected HTTP " + response.code()));
                    return;
                }

                try (ResponseBody body = response.body()) {
                    if (body == null) {
                        callback.onError(new IOException("Empty response body"));
                        return;
                    }

                    String payload = body.string();
                    Quiz quiz = parseQuiz(context, payload, difficulty, Math.max(2, choiceCount));
                    callback.onSuccess(quiz);
                } catch (IOException | JSONException parseException) {
                    callback.onError(parseException instanceof IOException
                            ? (IOException) parseException
                            : new IOException(parseException));
                }
            }
        });
    }

    @VisibleForTesting
    static Quiz parseQuiz(Context context, String payload, int difficulty, int choiceCount) throws JSONException {
        JSONObject root = new JSONObject(payload);

        JSONArray questionsArray = root.optJSONArray("questions");
        if (questionsArray == null || questionsArray.length() == 0) {
            throw new JSONException("Missing or empty questions array");
        }

        List<String> answerPool = extractAnswerPool(root);

        ArrayList<Quiz.Question> questions = new ArrayList<>();
        for (int i = 0; i < questionsArray.length(); i++) {
            JSONObject questionNode = questionsArray.getJSONObject(i);
            String answer = questionNode.optString("answer", "").trim();
            String filePath = questionNode.optString("filePath", "").trim();
            String pictureName = questionNode.optString("picture", "").trim();

            int drawableId = resolveDrawableId(context, pictureName);
            ArrayList<String> choices = buildChoices(answer, answerPool, choiceCount);

            questions.add(new Quiz.Question(answer, filePath, choices, drawableId));
        }

        return new Quiz(questions, difficulty);
    }

    private static List<String> extractAnswerPool(JSONObject root) {
        JSONArray poolArray = root.optJSONArray("awserPossible");
        if (poolArray == null) {
            poolArray = root.optJSONArray("answersPossible");
        }

        if (poolArray == null) {
            return Collections.emptyList();
        }

        ArrayList<String> pool = new ArrayList<>();
        for (int i = 0; i < poolArray.length(); i++) {
            String value = poolArray.optString(i, "").trim();
            if (!value.isEmpty()) {
                pool.add(value);
            }
        }
        return pool;
    }

    private static int resolveDrawableId(Context context, String drawableName) {
        if (drawableName == null || drawableName.isEmpty()) {
            return 0;
        }
        return context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
    }

    private static ArrayList<String> buildChoices(String answer, List<String> answerPool, int desiredCount) {
        ArrayList<String> choices = new ArrayList<>();
        if (answer != null && !answer.isEmpty()) {
            choices.add(answer);
        }

        if (answerPool != null && !answerPool.isEmpty()) {
            ArrayList<String> pool = new ArrayList<>(answerPool);
            Collections.shuffle(pool, RANDOM);

            Set<String> existing = new HashSet<>();
            for (String current : choices) {
                existing.add(current.toLowerCase(Locale.ROOT));
            }

            for (String candidate : pool) {
                if (candidate == null || candidate.isEmpty()) {
                    continue;
                }
                String lowered = candidate.toLowerCase(Locale.ROOT);
                if (existing.contains(lowered)) {
                    continue;
                }
                choices.add(candidate);
                existing.add(lowered);
                if (choices.size() >= desiredCount) {
                    break;
                }
            }
        }

        Collections.shuffle(choices, RANDOM);
        if (choices.isEmpty()) {
            choices.add("?");
        }
        return choices;
    }
}
