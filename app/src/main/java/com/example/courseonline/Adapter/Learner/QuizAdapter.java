package com.example.courseonline.Adapter.Learner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Activity.Learner.QuizActivity;
import com.example.courseonline.Activity.Learner.QuizHistoryActivity;
import com.example.courseonline.Domain.QuestionClass;
import com.example.courseonline.Domain.QuizClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder>{
    private ArrayList<QuizClass> items;
    private ArrayList<QuestionClass> arrayQuestion;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Context context;
    public int i;
    public QuizAdapter(ArrayList<QuizClass> items, Context context) {
        this.items = items;
        this.context = context;

    }

    @NonNull
    @Override
    public QuizAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_quiz,parent,false);
        context = (Activity) parent.getContext();
        return new QuizAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Intent intent = new Intent(context, QuizActivity.class);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        String id = items.get(position).getQuiz_id();
        String course_id = items.get(position).getCourse_id();
        String heading_id = items.get(position).getHeading_id();

        holder.txtTime.setText(items.get(position).getQuiz_time() + " phút");

        holder.txtTitle.setText(items.get(position).getQuiz_title());
        holder.txtSubTitle.setText(items.get(position).getQuiz_subtitle());
        holder.txtRequire.setText("Yêu cầu: " + items.get(position).getQuiz_require() + "%");
        if(mAuth.getCurrentUser() != null)
        {
            db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckQuiz").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        intent.putExtra("quiz_state", "exist");
                        if(documentSnapshot.getBoolean("quiz_state"))
                        {
                            holder.checkRequire.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.checked));
                        }else      holder.checkRequire.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.unchecked));

                        holder.txtBestScore.setText(documentSnapshot.getString("quiz_best_score")+"%");
                        int number = (int) Double.parseDouble(documentSnapshot.getString("quiz_best_score"));
                        holder.progressScore.setProgress(number);
                    }else{
                        intent.putExtra("quiz_state", "new");
                        holder.txtBestScore.setText("0%");
                        holder.progressScore.setProgress(0);
                        holder.checkRequire.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.unchecked));

                    }
                }
            });
            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, v);

                    popupMenu.getMenuInflater().inflate(R.menu.quiz_history_option, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.quiz_history) {
                                Intent intent = new Intent(context, QuizHistoryActivity.class);

                                intent.putExtra("course_id", course_id);
                                intent.putExtra("heading_id", heading_id);
                                intent.putExtra("quiz_id", id);

                                context.startActivity(intent);
                                return true;
                            }
                            return false;
                        }
                    });

                    popupMenu.show();
                    return true;
                }
            });
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.collection("Courses")
                            .document(course_id)
                            .collection("Heading")
                            .document(heading_id)
                            .collection("quiz")
                            .document(id)
                            .collection("questions")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()) {
                                        } else {
                                            arrayQuestion = new ArrayList<>();
                                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                                arrayQuestion.add(doc.toObject(QuestionClass.class));
                                            }
                                            LayoutInflater inflater = LayoutInflater.from(context);
                                            View dialogView = inflater.inflate(R.layout.quiz_confirm, null);

                                            AlertDialog.Builder builder = new AlertDialog.Builder(context, androidx.navigation.ui.R.style.Theme_MaterialComponents_Dialog_Alert);
                                            builder.setView(dialogView);

                                            AppCompatButton buttonStart = dialogView.findViewById(R.id.button_start);
                                            AppCompatButton buttonCancel = dialogView.findViewById(R.id.button_cancel);
                                            TextView txtContent = dialogView.findViewById(R.id.text_message);
                                            TextView txtTime = dialogView.findViewById(R.id.text_message2);
                                            txtContent.setText("Tên bài trắc nghiệm: " + items.get(position).getQuiz_title());
                                            txtTime.setText("Thời gian: " + items.get(position).getQuiz_time() + " phút");

                                            AlertDialog dialog = builder.create();

                                            buttonCancel.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            buttonStart.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    intent.putParcelableArrayListExtra("questionList", arrayQuestion);
                                                    intent.putExtra("quiz_time", items.get(position).getQuiz_time());
                                                    intent.putExtra("quiz_id", id);
                                                    intent.putExtra("quiz_title", items.get(position).getQuiz_title());
                                                    intent.putExtra("course_id", course_id);
                                                    intent.putExtra("heading_id", heading_id);
                                                    context.startActivity(intent);
                                                }
                                            });

                                            dialog.show();
                                        }
                                    } else {
                                        Log.e("FirestoreError", "Error getting documents: ", task.getException());
                                    }
                                }
                            });


                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public void release(){
        context =null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtTitle, txtSubTitle, txtTime, txtBestScore, txtRequire;
        LinearProgressIndicator progressScore;
        ImageView checkRequire;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRequire = (TextView) itemView.findViewById(R.id.quiz_require_text);
            txtBestScore = (TextView) itemView.findViewById(R.id.quiz_best_score_text);
            txtTitle = (TextView) itemView.findViewById(R.id.quiz_title_text);
            txtSubTitle = (TextView) itemView.findViewById(R.id.quiz_subtitle_text);
            txtTime = (TextView) itemView.findViewById(R.id.quiz_time_text);
            cardView = (CardView) itemView.findViewById(R.id.cardViewQuiz);
            progressScore = (LinearProgressIndicator) itemView.findViewById(R.id.progressScore);
            checkRequire = (ImageView) itemView.findViewById(R.id.imgCheckRequire);
        }
    }
}
