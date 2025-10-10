package com.example.flashcard;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private final List<Quiz.Question> questions;

    public ItemAdapter(List<Quiz.Question> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(questions, position);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;

        public ViewHolder(@NonNull View itemView) {
            // Connect XML with java
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textView);
        }

        void bind(List<Quiz.Question> questions, int position) {
            textViewTitle.setText(String.format(Locale.getDefault(), "Question %d :", position + 1));

            itemView.setOnClickListener(v -> {
                int adapterPosition = getBindingAdapterPosition();
                // Verif error
                if (adapterPosition == RecyclerView.NO_POSITION) {
                    return;
                }
                // Send question to quiz activity
                Quiz.Question selectedQuestion = questions.get(adapterPosition);
                Intent intent = new Intent(v.getContext(), QuizActivity.class);
                intent.putExtra(QuizActivity.EXTRA_SINGLE_QUESTION, selectedQuestion);
                v.getContext().startActivity(intent);
            });
        }
    }
}