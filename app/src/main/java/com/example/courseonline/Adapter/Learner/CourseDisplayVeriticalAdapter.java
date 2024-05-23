package com.example.courseonline.Adapter.Learner;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.courseonline.Activity.Learner.CourseActivity;
import com.example.courseonline.Domain.CourseDisplayClass;
import com.example.courseonline.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CourseDisplayVeriticalAdapter extends RecyclerView.Adapter<CourseDisplayVeriticalAdapter.ViewHolder> {

    ArrayList<CourseDisplayClass> items;
    Activity context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CourseDisplayAdapter.onItemClickListener onItemClickListener;
    RecyclerView.RecycledViewPool viewPool;
    public CourseDisplayVeriticalAdapter(ArrayList<CourseDisplayClass> items) {
        this.items = items;
        viewPool = new RecyclerView.RecycledViewPool();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_coursedisplay_vertical,parent,false);
        context =(Activity) parent.getContext();
        return new ViewHolder(inflate);
    }
    public interface onItemClickListener {
        void onClick(String str);
    }
    public void setClickItemListener(CourseDisplayAdapter.onItemClickListener onItem) {
        this.onItemClickListener = onItem;
    }
    @Override
    public void onBindViewHolder(@NonNull CourseDisplayVeriticalAdapter.ViewHolder holder, int position) {
        String id = items.get(position).getCourse_id();
        try {
            Glide.with(holder.itemView.getContext()).load(items.get(position).getCourse_img()).centerInside().into(holder.imgPic);

        }catch (Exception e)
        {
            Glide.with(holder.itemView.getContext()).load(R.mipmap.logo2_new_foreground).centerInside().into(holder.imgPic);

        }
        holder.txtTitle.setText(items.get(position).getCourse_title());
        holder.txtOwner.setText(items.get(position).getCourse_owner());
        holder.txtView.setText(changeValue(items.get(position).getCourse_member()));
        holder.txtRate.setText(String.valueOf(items.get(position).getCourse_rate()));
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CourseActivity.class);
                intent.putExtra("course_key", id);
                context.startActivity(intent);
                context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
    public String changeValue(Number number) {
        char[] suffix = {' ', 'K', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitle, txtOwner, txtView, txtRate;
        ImageView imgPic;
        ConstraintLayout constraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitlev);
            txtOwner = itemView.findViewById(R.id.txtOwnerv);
            txtView = itemView.findViewById(R.id.txtPeoplev);
            txtRate = itemView.findViewById(R.id.txtRatev);
            imgPic = itemView.findViewById(R.id.imgPicv);
            constraintLayout = itemView.findViewById(R.id.constraintVertical);
        }
    }
}
