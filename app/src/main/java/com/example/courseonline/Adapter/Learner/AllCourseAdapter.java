package com.example.courseonline.Adapter.Learner;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.courseonline.Activity.Learner.CourseActivity;
import com.example.courseonline.Domain.CourseDisplayClass;
import com.example.courseonline.R;

import java.util.ArrayList;

public class AllCourseAdapter extends RecyclerView.Adapter<AllCourseAdapter.ViewHolder> {
    ArrayList<CourseDisplayClass> items;
    Activity context;
    AllCourseAdapter.onItemClickListener onItemClickListener;
    public AllCourseAdapter(ArrayList<CourseDisplayClass> items) {
        this.items = items;
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
        String id = items.get(position).getCourse_id();
        Glide.with(holder.itemView.getContext()).load(items.get(position).getCourse_img()).centerInside().into(holder.imgPic);
        holder.txtTitle.setText(items.get(position).getCourse_title());
        holder.txtOwner.setText(items.get(position).getCourse_owner());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CourseActivity.class);
                intent.putExtra("course_key", id);
                context.startActivity(intent);
                context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
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
