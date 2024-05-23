package com.example.courseonline.Adapter.Learner;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Activity.Learner.LearnActivity;
import com.example.courseonline.Domain.HeadingClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HeadingAdapter extends RecyclerView.Adapter<HeadingAdapter.ViewHolder> {
    ArrayList<HeadingClass> items;
    Activity context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Toast toast;
    HeadingAdapter.onItemClickListener onItemClickListener;

    public HeadingAdapter(ArrayList<HeadingClass> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public HeadingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_layout_heading,parent,false);
        context = (Activity) parent.getContext();
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull HeadingAdapter.ViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        String headingID = items.get(position).getHeading_id();
        String courseID = items.get(position).getCourse_id();
        db.collection("Users").document(uid).collection("cart").whereEqualTo("cart_item_id", courseID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                        if(task.getResult().size() != 0)
                        {
                            holder.imgLock.setImageResource(R.drawable.baseline_keyboard_arrow_right_24);
                            holder.constraintHeading.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, LearnActivity.class);
                                    intent.putExtra("heading_key", headingID);
                                    intent.putExtra("course_key", courseID);
                                    context.startActivity(intent);
                                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            });

                        }
                        else{
                            holder.imgLock.setImageResource(R.drawable.baseline_lock_24);
                            holder.constraintHeading.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    toastMes("Vui lòng mua khoá học trước !");
                                }
                            });}


                }
            }
        });

        holder.txtHeading.setText(items.get(position).getHeading_title());

    }
    public interface onItemClickListener {
        void onClick(String str);
    }
    public void setClickItemListener(HeadingAdapter.onItemClickListener onItem) {
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
        ImageView imgLock;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintHeading = (ConstraintLayout) itemView.findViewById(R.id.constraintHeading);
            txtHeading = (TextView) itemView.findViewById(R.id.Heading);
            imgLock = (ImageView) itemView.findViewById(R.id.imgLock);
        }
    }
}
