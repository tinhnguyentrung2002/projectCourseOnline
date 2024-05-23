package com.example.courseonline.Adapter.Teacher;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.courseonline.Activity.Teacher.DetailCourseTeacherActivity;
import com.example.courseonline.Domain.CourseDisplayClass;
import com.example.courseonline.R;

import java.util.ArrayList;

public class RankCourseAdapter extends RecyclerView.Adapter<RankCourseAdapter.ViewHolder>{
    ArrayList<CourseDisplayClass> items;
    Activity context;
    private String type;
    private Toast toast;
    public RankCourseAdapter(ArrayList<CourseDisplayClass> items, String type) {
            this.items = items;
            this.type = type;
    }

    @NonNull
    @Override
    public RankCourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_rank,parent,false);
            context = (Activity) parent.getContext();
            return new RankCourseAdapter.ViewHolder(inflate);
    }
    @Override
    public void onBindViewHolder(@NonNull RankCourseAdapter.ViewHolder holder, int position) {
        if(type.equals("rate"))
        {
            holder.txtTitleRank.setText(items.get(position).getCourse_title());
            holder.txtDetailInfo.setText(items.get(position).getCourse_rate()+"/5");
            Glide.with(holder.itemView.getContext()).load(items.get(position).getCourse_img()).centerInside().into(holder.imgPicRank);
            holder.imgTypeRank.setImageDrawable(context.getResources().getDrawable(R.drawable.star, null));
            switch (position){
                case 0:
                    holder.imgRank.setImageDrawable(context.getResources().getDrawable(R.drawable.rank1, null));
                    break;
                case 1:
                    holder.imgRank.setImageDrawable(context.getResources().getDrawable(R.drawable.rank2, null));
                    break;
                case 2:
                    holder.imgRank.setImageDrawable(context.getResources().getDrawable(R.drawable.rank3, null));
                    break;
            }
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailCourseTeacherActivity.class);
                    intent.putExtra("key_id", items.get(position).getCourse_id());
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        }else{
            holder.txtTitleRank.setText(items.get(position).getCourse_title());
            holder.txtDetailInfo.setText(String.valueOf(items.get(position).getCourse_member()));
            Glide.with(holder.itemView.getContext()).load(items.get(position).getCourse_img()).centerInside().into(holder.imgPicRank);
            holder.imgTypeRank.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_person_2_24, null));
            switch (position){
                case 0:
                    holder.imgRank.setImageDrawable(context.getResources().getDrawable(R.drawable.rank1, null));
                    break;
                case 1:
                    holder.imgRank.setImageDrawable(context.getResources().getDrawable(R.drawable.rank2, null));
                    break;
                case 2:
                    holder.imgRank.setImageDrawable(context.getResources().getDrawable(R.drawable.rank3, null));
                    break;
            }
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailCourseTeacherActivity.class);
                    intent.putExtra("key_id", items.get(position).getCourse_id());
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(context, mes,Toast.LENGTH_SHORT);
        toast.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitleRank, txtDetailInfo;
        ImageView imgRank, imgPicRank,imgTypeRank;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitleRank = itemView.findViewById(R.id.txtTitleRank);
            txtDetailInfo = itemView.findViewById(R.id.detailInfo);
            imgRank = itemView.findViewById(R.id.rankImg);
            imgPicRank = itemView.findViewById(R.id.imgPicRank);
            imgTypeRank = itemView.findViewById(R.id.imgTypeRank);
            cardView = itemView.findViewById(R.id.main_containerRank);
        }
    }
}

