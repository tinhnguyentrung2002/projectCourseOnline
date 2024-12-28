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
import androidx.constraintlayout.widget.ConstraintLayout;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CourseDisplayVerticalAdapter extends  RecyclerView.Adapter<CourseDisplayVerticalAdapter.ViewHolder> {

    ArrayList<CourseDisplayClass> items;
    Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CourseDisplayAdapter.onItemClickListener onItemClickListener;
    RecyclerView.RecycledViewPool viewPool;
    public CourseDisplayVerticalAdapter(ArrayList<CourseDisplayClass> items, Context context) {
        this.items = items;
        this.context = context;
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
    public void onBindViewHolder(@NonNull CourseDisplayVerticalAdapter.ViewHolder holder, int position) {
        String id = items.get(position).getCourse_id();
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .centerInside();

        Glide.with(holder.itemView.getContext())
                .load(items.get(position).getCourse_img())
                .apply(options)
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
        holder.txtView.setText(changeValue(items.get(position).getCourse_member()));
        holder.txtRate.setText(String.valueOf(items.get(position).getCourse_rate()));
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
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
