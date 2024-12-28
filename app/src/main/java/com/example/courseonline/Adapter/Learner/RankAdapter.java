package com.example.courseonline.Adapter.Learner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Dialog.Learner.ProfileDialog;
import com.example.courseonline.Domain.RankClass;
import com.example.courseonline.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder>{
    private ArrayList<RankClass> items;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Context context;
    public RankAdapter(ArrayList<RankClass> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public RankAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_rank_item,parent,false);
        context = (Activity) parent.getContext();
        return new RankAdapter.ViewHolder(inflate);
    }
    @Override
    public void onBindViewHolder(@NonNull RankAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        int medalDrawable = 0;
        int rankText = 0;
        if (items.get(position).getRank_position() == 1) {
            medalDrawable = R.drawable.gold_medal;
        } else if (items.get(position).getRank_position() == 2) {
            medalDrawable = R.drawable.silver_medal;
        } else if (items.get(position).getRank_position() == 3) {
            medalDrawable = R.drawable.bronze_medal;
        } else {
          rankText = items.get(position).getRank_position();
        }


        if (medalDrawable != 0) {
            Drawable drawable = ContextCompat.getDrawable(context, medalDrawable);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth()/4, drawable.getIntrinsicHeight()/4);
            holder.txtRankPosition.setCompoundDrawablesWithIntrinsicBounds(medalDrawable, 0, 0, 0);
            holder.txtRankPosition.setPadding(0, 0, 0, 0);
            holder.txtName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.txtEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.txtName.setPadding(0, 0, 0, 0);
            holder.txtEmail.setPadding(0, 0, 0, 0);

        } else {
            holder.txtRankPosition.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.txtName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.txtEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.txtRankPosition.setPadding(50, 20, 0, 0);
            holder.txtName.setPadding(58, 0, 0, 0);
            holder.txtEmail.setPadding(58, 0, 0, 0);
        }
        if(items.get(position).getRank_position() == 10)
        {
            holder.txtRankPosition.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.txtName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.txtEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.txtRankPosition.setPadding(40, 20, 0, 0);
            holder.txtName.setPadding(40, 0, 0, 0);
            holder.txtEmail.setPadding(40, 0, 0, 0);
        }
        if(rankText == 0)
        {
            holder.txtRankPosition.setText("");
        }else{
            holder.txtRankPosition.setText(String.valueOf(rankText));
        }

        String userID = items.get(position).getRank_user_id();
        holder.txtPoints.setText(String.valueOf(items.get(position).getRank_user_points()));
        if(userID.equals(mAuth.getUid()) && items.get(position).getRank_position() != 0)
        {
            holder.linearRank.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue));
        }else{
            holder.linearRank.setBackgroundColor(Color.TRANSPARENT);

        }
        db.collection("Users").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    return;
                }
                if(value.exists()){
                    holder.txtName.setText(value.getString("user_name"));
                    holder.txtEmail.setText(value.getString("user_email"));
                }else{
                    holder.txtName.setText("Chưa có");
                    holder.txtEmail.setText("Chưa có");
                }
            }
        });
        holder.linearRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileDialog profileDialog = new ProfileDialog(context, items.get(position).getRank_user_id());
                profileDialog.show();
            }
        });
        DocumentReference documentReference = db.collection("Users").document(items.get(position).getRank_user_id());
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
                    if(value.getString("user_avatar") != "")
                    {
                        Picasso.get()
                                .load(value.getString("user_avatar"))
                                .placeholder(R.drawable.user)
                                .error(R.drawable.user)
                                .fit()
                                .centerInside()
                                .into(holder.imgUser);
                    }
                    switch (currentLevel)
                    {
                        case "1":
                            holder.imgUserLevel.setImageResource(R.drawable.level_1);
                            holder.imgUser.setStrokeColorResource(R.color.grey);
                            break;
                        case "2":
                            holder.imgUserLevel.setImageResource(R.drawable.level_2);
                            holder.imgUser.setStrokeColorResource(R.color.mint);
                            break;
                        case "3":
                            holder.imgUserLevel.setImageResource(R.drawable.level_3);
                            holder.imgUser.setStrokeColorResource(R.color.blue);
                            break;
                        case "4":
                            holder.imgUserLevel.setImageResource(R.drawable.level_4);
                            holder.imgUser.setStrokeColorResource(R.color.purple);
                            break;
                        case "5":
                            holder.imgUserLevel.setImageResource(R.drawable.level_5);
                            holder.imgUser.setStrokeColorResource(R.color.red);
                            break;
                        default:
                            holder.imgUserLevel.setImageResource(R.drawable.error);
                            holder.imgUser.setStrokeColorResource(R.color.grey);
                            break;
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public void release(){
        context = null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtEmail, txtRankPosition, txtPoints;
        ShapeableImageView imgUser;
        ImageView imgUserLevel;
        LinearLayout linearRank;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName= itemView.findViewById(R.id.rank_name);
            txtEmail = itemView.findViewById(R.id.rank_email);
            txtRankPosition = itemView.findViewById(R.id.rank_position);
            txtPoints = itemView.findViewById(R.id.rank_points);
            linearRank = itemView.findViewById(R.id.card_view_rank);
            imgUser = itemView.findViewById(R.id.imgUser_rank);
            imgUserLevel = itemView.findViewById(R.id.rank_user_level);

        }
    }
}
