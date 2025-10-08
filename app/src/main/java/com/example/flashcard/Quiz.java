package com.example.flashcard;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import java.util.ArrayList;

public class Quiz implements Parcelable {
    public ArrayList<Question> questions;
    public int numberOfQuestions;
    public int difficulty;

    public Quiz(ArrayList<Question> questions,int difficulty) {
        this.questions = questions;
        this.numberOfQuestions = questions.size();
        this.difficulty = difficulty;
    }


    public static class Question implements Parcelable{
        public String answer;
        public String filePath;
        public int numberOfAnswers;
        public int picture;


        public Question(String answer, String filePath, int numberOfAnswers, int picture) {
            this.answer = answer;
            this.filePath = filePath;
            this.numberOfAnswers = numberOfAnswers;
            this.picture = picture;
        }


        protected Question(Parcel in) {
            answer = in.readString();
            filePath = in.readString();
            numberOfAnswers = in.readInt();
            picture = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(answer);
            dest.writeString(filePath);
            dest.writeInt(numberOfAnswers);
            dest.writeInt(picture);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Question> CREATOR = new Creator<Question>() {
            @Override
            public Question createFromParcel(Parcel in) {
                return new Question(in);
            }

            @Override
            public Question[] newArray(int size) {
                return new Question[size];
            }
        };
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(questions);
        dest.writeInt(numberOfQuestions);
        dest.writeInt(difficulty);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    protected Quiz(Parcel in) {
        questions = in.createTypedArrayList(Question.CREATOR);

        numberOfQuestions = in.readInt();
        difficulty = in.readInt();
    }

    public static final Creator<Quiz> CREATOR = new Creator<Quiz>() {
        @Override
        public Quiz createFromParcel(Parcel in) {
            return new Quiz(in);
        }

        @Override
        public Quiz[] newArray(int size) {
            return new Quiz[size];
        }
    };

}
