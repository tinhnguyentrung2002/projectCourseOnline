package com.example.courseonline.Adapter.Learner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class QuizProgressAdapter extends RecyclerView.Adapter<QuizProgressAdapter.QuizViewHolder> {

    private List<String> quizItems;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String course_id;
    private String header_id;
    private Context context;

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        public TextView quizTitle;
        public TextView progress;

        public QuizViewHolder(View view) {
            super(view);
            quizTitle = view.findViewById(R.id.txtQuizTitle_result);
            progress = view.findViewById(R.id.txtQuizProgress_result);
        }
    }

    public QuizProgressAdapter(List<String> quizItems, String course_id, String header_id, Context context) {
        this.quizItems = quizItems;
        this.course_id = course_id;
        this.header_id = header_id;
        this.context = context;

    }

    @Override
    public QuizViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_quiz_result_class, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuizViewHolder holder, @SuppressLint("RecyclerView") int position) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        String quizItem = quizItems.get(position);

        if(mAuth.getCurrentUser() != null)
        {
            db.collection("Courses").document(course_id).collection("Heading").document(header_id).collection("quiz").document(quizItem).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isComplete()){
                        int pos = position + 1;
                        holder.quizTitle.setText(pos+". " + task.getResult().getString("quiz_title"));
                    }
                }
            });
            db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckQuiz").document(quizItem).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isComplete()){
                        if(task.getResult().getString("quiz_best_score") != null){
                            if(task.getResult().getBoolean("quiz_state"))
                            {
                                holder.progress.setText(task.getResult().getString("quiz_best_score") + "%" +" (Đạt)");
                                holder.progress.setTextColor(ContextCompat.getColor(context, R.color.mint));
                            }else{
                                holder.progress.setText(task.getResult().getString("quiz_best_score") + "%" +" (Chưa đạt)");
                                holder.progress.setTextColor(ContextCompat.getColor(context, R.color.red));
                            }
                        }
                        else{
                            holder.progress.setText("0% (Chưa đạt)");
                            holder.progress.setTextColor(ContextCompat.getColor(context, R.color.red));
                        }
                    }
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return quizItems.size();
    }
}