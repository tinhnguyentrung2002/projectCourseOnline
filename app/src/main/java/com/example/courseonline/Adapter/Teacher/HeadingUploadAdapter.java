package com.example.courseonline.Adapter.Teacher;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Activity.Teacher.UploadStep3Activity;
import com.example.courseonline.Domain.HeadingClass;
import com.example.courseonline.R;

import java.util.ArrayList;

public class HeadingUploadAdapter extends RecyclerView.Adapter<HeadingUploadAdapter.ViewHolder>{
    ArrayList<HeadingClass> items;
    Activity context;
    private Toast toast;
    HeadingUploadAdapter.onItemClickListener onItemClickListener;

    public HeadingUploadAdapter(ArrayList<HeadingClass> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public HeadingUploadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_teacher_heading_upload,parent,false);
        context = (Activity) parent.getContext();
        return new HeadingUploadAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull HeadingUploadAdapter.ViewHolder holder, int position) {
        String headingID = items.get(position).getHeading_id();
        String courseID = items.get(position).getCourse_id();
        holder.constraintHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UploadStep3Activity.class);
                intent.putExtra("heading_key", headingID);
                intent.putExtra("course_key", courseID);
                context.startActivity(intent);
                context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        holder.txtHeading.setText(items.get(position).getHeading_title());

    }
    public interface onItemClickListener {
        void onClick(String str);
    }
    public void setClickItemListener(HeadingUploadAdapter.onItemClickListener onItem) {
        this.onItemClickListener = onItem;
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
        ConstraintLayout constraintHeading;
        TextView txtHeading;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintHeading = (ConstraintLayout) itemView.findViewById(R.id.constraintHeadingUpload);
            txtHeading = (TextView) itemView.findViewById(R.id.txtHeadingUpload);
        }
    }
}
