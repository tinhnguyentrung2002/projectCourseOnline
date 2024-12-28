package com.example.courseonline.Adapter.Learner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Activity.Learner.ClassActivity;
import com.example.courseonline.Activity.Teacher.DetailClassTeacherActivity;
import com.example.courseonline.Domain.ClassesClass;
import com.example.courseonline.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private List<ClassesClass> classesList;
    private Context context;
    private FirebaseFirestore db;
    private int permission;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public ClassAdapter(List<ClassesClass> classesList, Context context, int permission) {
        this.classesList = classesList;
        this.context = context;
        this.permission = permission;
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {

        public ShapeableImageView imgPic;
        public TextView txtTitleClass;
        public TextView txtOwnerClass;
        public TextView txtTime;
        public ConstraintLayout mainContainer;

        public ClassViewHolder(View itemView) {
            super(itemView);

            imgPic = itemView.findViewById(R.id.imgPic_class);
            txtTitleClass = itemView.findViewById(R.id.txtTitle_class);
            txtOwnerClass = itemView.findViewById(R.id.txtOwner_class);
            txtTime = itemView.findViewById(R.id.txtTime_class);
            mainContainer = itemView.findViewById(R.id.main_container_class);
        }
    }

    @Override
    public ClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_class, parent, false);

        return new ClassViewHolder(itemView);
    }
    public void release(){
        context =null;
    }

    @Override
    public void onBindViewHolder(ClassViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();
        ClassesClass classesClass = classesList.get(position);
        String id = classesClass.getCourse_id();
        holder.txtTitleClass.setText(classesClass.getCourse_title());
        db.collection("Users").document(classesClass.getCourse_owner_id()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    return;
                }
                if(value.exists())
                {
                    holder.txtOwnerClass.setText((value.getString("user_name")));

                }
            }
        });


        Picasso.get().load(classesClass.getCourse_img()).into(holder.imgPic);
        if(permission == 2)
        {
            holder.mainContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailClassTeacherActivity.class);
                    intent.putExtra("key_id", id);
                    context.startActivity(intent);
                }
            });
            holder.imgPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailClassTeacherActivity.class);
                    intent.putExtra("key_id", id);
                    context.startActivity(intent);
                }
            });
        }else{
            holder.mainContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ClassActivity.class);
                    intent.putExtra("course_key", id);
                    context.startActivity(intent);
                }
            });
            holder.imgPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ClassActivity.class);
                    intent.putExtra("course_key", id);
                    context.startActivity(intent);
                }
            });
            holder.txtTime.setText("Thời gian: " + sdf.format(classesClass.getCourse_start_time())  + " - " + sdf.format(classesClass.getCourse_end_time()) );
        }


    }

    @Override
    public int getItemCount() {
        return classesList.size();
    }
}
