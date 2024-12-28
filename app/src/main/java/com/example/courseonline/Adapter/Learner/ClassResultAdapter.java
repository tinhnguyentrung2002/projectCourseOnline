package com.example.courseonline.Adapter.Learner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClassResultAdapter extends RecyclerView.Adapter<ClassResultAdapter.ViewHolder> {

    private List<String> classResults;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String course_id;
    private Context context;

    public ClassResultAdapter(List<String> classResults, String course_id, Context context) {
        this.classResults = classResults;
        this.course_id = course_id;
        this.context =context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_result_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        List<String> videoID = new ArrayList<>();
        List<String> quizID = new ArrayList<>();
        String result = classResults.get(position);
        if(mAuth.getCurrentUser() != null){
            db.collection("Courses").document(course_id).collection("Heading").document(result).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isComplete()){
                        holder.txtChapterName.setText(task.getResult().getString("heading_title"));
                        db.collection("Courses").document(course_id).collection("Heading").document(result).collection("video").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @SuppressLint("SuspiciousIndentation")
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isComplete()){
                                    for(QueryDocumentSnapshot doc : task.getResult()){
                                        if(!videoID.contains(doc.getString("video_id")))
                                        videoID.add(doc.getString("video_id"));
                                    }
                                    if(videoID.size() != 0 ){
                                        Query query = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckVideo").whereIn("video_id", videoID);
                                        AggregateQuery countQuery = query.count();
                                        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    if(task.getResult().getCount() == videoID.size())
                                                    {
                                                        holder.txtVideoProgress.setTextColor(ContextCompat.getColor(context, R.color.mint));
                                                    }else{
                                                        holder.txtVideoProgress.setTextColor(ContextCompat.getColor(context, R.color.red));
                                                    }
                                                    holder.txtVideoProgress.setText("Video bài giảng: " + task.getResult().getCount() + "/" + videoID.size());
                                                } else {
                                                    holder.txtVideoProgress.setText("Video bài giảng: Lỗi");
                                                }
                                            }
                                        });
                                    }else{
                                        holder.txtVideoProgress.setText("Video bài giảng: Trống");
                                    }


                                }
                            }
                        });
                        db.collection("Courses").document(course_id).collection("Heading").document(result).collection("quiz").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @SuppressLint("SuspiciousIndentation")
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isComplete()){
                                    for(QueryDocumentSnapshot doc : task.getResult()){
                                        if(!quizID.contains(doc.getString("quiz_id")))
                                        quizID.add(doc.getString("quiz_id"));
                                    }
                                    QuizProgressAdapter quizAdapter = new QuizProgressAdapter(quizID, course_id, result, context);
                                    holder.recyclerQuizList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                                    holder.recyclerQuizList.setHasFixedSize(true);
                                    holder.recyclerQuizList.setItemViewCacheSize(20);
                                    holder.recyclerQuizList.setAdapter(quizAdapter);
                                    quizAdapter.notifyDataSetChanged();
                                    if(quizID.size() != 0 ){
                                        Query query = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckQuiz").whereIn("quiz_id", quizID);
                                        AggregateQuery countQuery = query.count();
                                        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    if(task.getResult().getCount() == quizID.size())
                                                    {
                                                        holder.txtQuizProgress.setTextColor(ContextCompat.getColor(context, R.color.mint));
                                                    }else{
                                                        holder.txtQuizProgress.setTextColor(ContextCompat.getColor(context, R.color.red));
                                                    }
                                                    holder.txtQuizProgress.setText("Bài tập: " + task.getResult().getCount() + "/" + quizID.size());

                                                } else {
                                                    holder.txtQuizProgress.setText("Bài tập: Lỗi");
                                                }
                                            }
                                        });
                                    }else{
                                        holder.txtQuizProgress.setText("Video bài giảng: Trống");
                                    }


                                }
                            }
                        });

                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return classResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtChapterName;
        TextView txtVideoProgress;
        TextView txtQuizProgress;
        RecyclerView recyclerQuizList;

        public ViewHolder(View itemView) {
            super(itemView);
            txtChapterName = itemView.findViewById(R.id.txtChapterName);
            txtVideoProgress = itemView.findViewById(R.id.txtVideoProgress_result);
            txtQuizProgress = itemView.findViewById(R.id.txtQuizProgress_result);
            recyclerQuizList = itemView.findViewById(R.id.recyclerQuizList_result);
        }
    }
}
