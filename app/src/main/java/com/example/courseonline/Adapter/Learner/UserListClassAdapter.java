package com.example.courseonline.Adapter.Learner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Dialog.Learner.ProfileDialog;
import com.example.courseonline.Domain.UserClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

public class UserListClassAdapter extends RecyclerView.Adapter<UserListClassAdapter.UserViewHolder> {
    private List<UserClass> userList;
    private Context context;
    private String course_id;
    private FirebaseFirestore db;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public UserListClassAdapter(Context context, List<UserClass> userList, String course_id) {
        this.context = context;
        this.userList = userList;
        this.course_id = course_id;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_user_list, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();
        UserClass user = userList.get(position);
        holder.txtUserName.setText(user.getUser_name());
        Picasso.get()
                .load(user.getUser_avatar())
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .fit()
                .centerInside()
                .into(holder.imgUser);
        DocumentReference documentReference = db.collection("Users").document(user.getUser_uid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    return;
                }
                if(value != null && value.exists())
                {
                    String currentLevel = value.getString("user_level") != null ? value.getString("user_level"): "1" ;
                    switch (currentLevel)
                    {
                        case "1":
                            holder.userLevel.setImageResource(R.drawable.level_1);
                            holder.imgUser.setStrokeColorResource(R.color.grey);
                            break;
                        case "2":
                            holder.userLevel.setImageResource(R.drawable.level_2);
                            holder.imgUser.setStrokeColorResource(R.color.mint);
                            break;
                        case "3":
                            holder.userLevel.setImageResource(R.drawable.level_3);
                            holder.imgUser.setStrokeColorResource(R.color.blue);
                            break;
                        case "4":
                            holder.userLevel.setImageResource(R.drawable.level_4);
                            holder.imgUser.setStrokeColorResource(R.color.purple);
                            break;
                        case "5":
                            holder.userLevel.setImageResource(R.drawable.level_5);
                            holder.imgUser.setStrokeColorResource(R.color.red);
                            break;
                        default:
                            holder.userLevel.setImageResource(R.drawable.error);
                            holder.imgUser.setStrokeColorResource(R.color.grey);
                            break;
                    }
                }
            }
        });
        holder.linearUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileDialog profileDialog = new ProfileDialog(context, user.getUser_uid());
                profileDialog.show();
            }
        });
        db.collection("Users").document(user.getUser_uid()).collection("OwnCourses").whereEqualTo("own_course_item_id", course_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete()){
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        holder.joinDateTextView.setText(sdf.format(doc.getDate("own_course_date")));
                    }

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imgUser;
        TextView txtUserName;
        ImageView userLevel;
        TextView joinDateTextView;
        LinearLayout linearUser;

        public UserViewHolder(View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser_class);
            txtUserName = itemView.findViewById(R.id.txtUserName_class);
            userLevel = itemView.findViewById(R.id.class_user_level);
            joinDateTextView = itemView.findViewById(R.id.userListJoinDate);
            linearUser = itemView.findViewById(R.id.linearUser);
        }
    }
}
