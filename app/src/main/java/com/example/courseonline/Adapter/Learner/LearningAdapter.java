package com.example.courseonline.Adapter.Learner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Activity.Learner.CourseActivity;
import com.example.courseonline.Domain.CourseDisplayClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class LearningAdapter extends RecyclerView.Adapter<LearningAdapter.ViewHolder>  {
    ArrayList<CourseDisplayClass> items;
    Context context;
    LearningAdapter.onItemClickListener onItemClickListener;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    public LearningAdapter(ArrayList<CourseDisplayClass> items, Context context) {
        this.items = items;
        this.context = context;
    }
    @NonNull
    @Override
    public LearningAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_learning,parent,false);
        context = (Activity) parent.getContext();
        return new ViewHolder(inflate);
    }
    public interface onItemClickListener {
        void onClick(String str);
    }
    public void setClickItemListener(LearningAdapter.onItemClickListener onItem) {
        this.onItemClickListener = onItem;
    }
    @Override
    public void onBindViewHolder(@NonNull LearningAdapter.ViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String id = items.get(position).getCourse_id();
        holder.txtTitle.setText(items.get(position).getCourse_title());
        db.collection("Users").document(items.get(position).getCourse_owner_id()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    return;
                }
                if(value.exists())
                {
                    holder.txtOwner.setText((value.getString("user_name")));

                }
            }
        });


        DocumentReference documentReference = db.collection("Courses").document(id);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    return;
                }
                if(value.getDouble("course_price") != 0)
                {
                   holder.imgPic.setVisibility(View.GONE);
                }else{
                    holder.imgPic.setVisibility(View.VISIBLE);
                    holder.imgPic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setMessage("Bạn có chắc chắn muốn huỷ đăng kí khoá học(chỉ áp dụng cho khoá học miễn phí) ?")
                                    .setPositiveButton("Huỷ", new DialogInterface.OnClickListener(){
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(mAuth.getCurrentUser() != null)
                                            {
                                                db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isComplete()){
                                                            for(QueryDocumentSnapshot snapshot : task.getResult())
                                                            {
                                                                db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckVideo").whereEqualTo("course_id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if(task.isComplete())
                                                                        {
                                                                            if(task.getResult().size() != 0)
                                                                            {
                                                                                for(QueryDocumentSnapshot doc : task.getResult())
                                                                                {
                                                                                    db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckVideo").document(doc.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                                db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckQuiz").whereEqualTo("course_id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if(task.isComplete())
                                                                        {
                                                                            if(task.getResult().size() != 0)
                                                                            {
                                                                                for(QueryDocumentSnapshot doc : task.getResult())
                                                                                {
                                                                                    db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckQuiz").document(doc.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                });

                                                                db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(snapshot.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Toast.makeText(context, "Huỷ thành công", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }

                                                        }

                                                    }
                                                });
                                            }
                                        }
                                    }).setNegativeButton("Không", null);

                            AlertDialog alert1 = alert.create();
                            alert1.show();
                        }
                    });
                }

            }
        });
        db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", items.get(position).getCourse_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        holder.txtProgress.setText(String.valueOf((int)Float.parseFloat(document.getString("own_course_progress"))/2) + "%");
                        holder.progressBar.setProgress((int)Float.parseFloat(document.getString("own_course_progress"))/2);
                    }
                } else {

                }
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CourseActivity.class);
                intent.putExtra("course_key", id);
                context.startActivity(intent);
//                context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
    public void release(){
        context =null;
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtOwner, txtProgress;
        ProgressBar progressBar;
        ImageView imgPic;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitlel);
            txtOwner = itemView.findViewById(R.id.txtOwnerl);
            imgPic = itemView.findViewById(R.id.imgPicl);
            cardView = itemView.findViewById(R.id.main_containerl);
            txtProgress = itemView.findViewById(R.id.txtProgress);
            progressBar = itemView.findViewById(R.id.progressCompleteLearning);
        }
    }
}

