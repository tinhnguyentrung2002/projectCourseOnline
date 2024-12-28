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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.courseonline.Activity.Learner.CourseActivity;
import com.example.courseonline.Domain.CourseDisplayClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;


public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder>  {
    ArrayList<CourseDisplayClass> items;
    Context context;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Toast toast;
    FavouriteAdapter.onItemClickListener onItemClickListener;
    public FavouriteAdapter(ArrayList<CourseDisplayClass> items, Context context) {
        this.items = items;
        this.context = context;
    }
    @NonNull
    @Override
    public FavouriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_course_favourite,parent,false);
            context = (Activity) parent.getContext();
            return new ViewHolder(inflate);
    }
    public interface onItemClickListener {
        void onClick(String str);
    }
    public void setClickItemListener(FavouriteAdapter.onItemClickListener onItem) {
        this.onItemClickListener = onItem;
    }
    @Override
    public void onBindViewHolder(@NonNull FavouriteAdapter.ViewHolder holder, int position) {
        db= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
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
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("Bạn có chắc chắn muốn xoá khoá học khỏi mục yêu thích ?")
                        .setPositiveButton("Xoá", new DialogInterface.OnClickListener()                 {
                            public void onClick(DialogInterface dialog, int which) {
                                if(mAuth.getCurrentUser() != null)
                                {
                                    db.collection("Users").document(uid).collection("Favourites").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.getResult().getData() != null){
                                                db.collection("Users").document(uid).collection("Favourites").document(id)
                                                        .delete()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                toastMes("Đã xoá khoá học khỏi mục yêu thích!");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                toastMes("Có lỗi xảy ra !");
                                                            }
                                                        });
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
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(context, mes,Toast.LENGTH_SHORT);
        toast.show();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtOwner;
        ImageView imgPic, imgDelete;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitlef);
            txtOwner = itemView.findViewById(R.id.txtOwnerf);
            imgPic = itemView.findViewById(R.id.imgPicf);
            cardView = itemView.findViewById(R.id.main_containerf);
            imgDelete = itemView.findViewById(R.id.imageDelete);
        }
    }
}
