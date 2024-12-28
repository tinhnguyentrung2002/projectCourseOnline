package com.example.courseonline.Adapter.Learner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.courseonline.Activity.Learner.CourseActivity;
import com.example.courseonline.Domain.CourseDisplayClass;
import com.example.courseonline.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

public class AllCourseAdapter extends RecyclerView.Adapter<AllCourseAdapter.ViewHolder> {
    ArrayList<CourseDisplayClass> items;
    Context context;
    private FirebaseFirestore db;
    AllCourseAdapter.onItemClickListener onItemClickListener;
    public AllCourseAdapter(ArrayList<CourseDisplayClass> items, Context context) {
        this.items = items;
        this.context = context;
    }
    @NonNull
    @Override
    public AllCourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_all_course,parent,false);
        context = (Activity) parent.getContext();
        return new ViewHolder(inflate);
    }
    public interface onItemClickListener {
        void onClick(String str);
    }
    public void setClickItemListener(AllCourseAdapter.onItemClickListener onItem) {
        this.onItemClickListener = onItem;
    }
    @Override
    public void onBindViewHolder(@NonNull AllCourseAdapter.ViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();
        String id = items.get(position).getCourse_id();
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .fitCenter();

        Glide.with(holder.itemView.getContext())
                .load(items.get(position).getCourse_img())
                .apply(requestOptions)
                .into(holder.imgPic);
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
        context = null;
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtOwner;
        ImageView imgPic;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitlea);
            txtOwner = itemView.findViewById(R.id.txtOwnera);
            imgPic = itemView.findViewById(R.id.imgPica);
            cardView = itemView.findViewById(R.id.main_containera);
        }
    }
}
