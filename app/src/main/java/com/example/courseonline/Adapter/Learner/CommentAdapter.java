package com.example.courseonline.Adapter.Learner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Dialog.Learner.ProfileDialog;
import com.example.courseonline.Dialog.Learner.RateDialog;
import com.example.courseonline.Domain.CommentClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateField;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    ArrayList<CommentClass> items;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Context context;
    CommentAdapter.onItemClickListener onItemClickListener;
    private ColorStateList colorStateList, colorStateList2;
    private static final String KEY_CHECK_LIKE_COURSE_ID = "course_id";
    private static final String KEY_CHECK_LIKE_COMMENT_ID = "comment_id";
    private static final String KEY_COMMENT_LIKE = "comment_like";
    private static final String KEY_COURSE_RATE = "course_rate";
    public CommentAdapter(ArrayList<CommentClass> items, Context context) {
        this.items = items;
        this.context = context;

    }
    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_comment,parent,false);
            context = (Activity) parent.getContext();
            return new CommentAdapter.ViewHolder(inflate);

    }
    public interface onItemClickListener {
        void onClick(String str);
    }
    public void setClickItemListener(CommentAdapter.onItemClickListener onItem) {
        this.onItemClickListener = onItem;
    }
    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int color = ContextCompat.getColor(context, R.color.app_theme_light);
        int color2 = ContextCompat.getColor(context, R.color.blue);
        colorStateList2 = ColorStateList.valueOf(color2);
        colorStateList = ColorStateList.valueOf(color);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String courseId = items.get(position).getCourse_id();
        String commentId = items.get(position).getComment_id();

        if(mAuth.getCurrentUser().getUid().equals(items.get(position).getUser_id())){
            holder.btnOption.setVisibility(View.VISIBLE);
        }else{
            holder.btnOption.setVisibility(View.GONE);
        }
        holder.btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, holder.btnOption);

                popupMenu.getMenuInflater().inflate(R.menu.comment_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.editComment)
                        {
                            RateDialog rateDialog = new RateDialog(context,commentId);
                            rateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));
                            rateDialog.setCancelable(false);
                            rateDialog.show();
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Bạn có chắc chắn muốn xoá bình luận này ?")
                                    .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener(){

                                        public void onClick(DialogInterface dialog, int which) {
                                            db.collection("Courses").document(courseId).collection("Comment").document(commentId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Query query = db.collection("Courses").document(courseId).collection("Comment");
                                                    AggregateQuery aggregateQuery = query.aggregate(AggregateField.average("comment_rate"));
                                                    aggregateQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                AggregateQuerySnapshot snapshot = task.getResult();
                                                                DocumentReference reference3 = db.collection("Courses").document(courseId);
                                                                Map map = new HashMap();
                                                                if(snapshot.get(AggregateField.average("comment_rate")) != null){
                                                                    String a = String.format("%.1f",snapshot.get(AggregateField.average("comment_rate"))).replace(',','.');
                                                                    map.put(KEY_COURSE_RATE, Double.parseDouble(a));
                                                                    reference3.update(map).addOnCompleteListener(new OnCompleteListener() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task task) {
                                                                        }
                                                                    });
                                                                }else{
                                                                    map.put(KEY_COURSE_RATE, 0.0);
                                                                    reference3.update(map).addOnCompleteListener(new OnCompleteListener() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task task) {

                                                                        }
                                                                    });
                                                                }

                                                            }
                                                        }
                                                    });
                                                }
                                            });

                                        }
                                    }).setNegativeButton("Huỷ", null);

                            AlertDialog alert = builder.create();
                            alert.show();
                        }

                        return true;
                    }
                });

                popupMenu.show();
            }
        });
//        Glide.with(holder.itemView.getContext()).load(items.get(position).getUser_avatar()).centerInside().into(holder.imgUser);
        Picasso.get()
                .load(items.get(position).getUser_avatar())
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .fit()
                .centerInside()
                .into(holder.imgUser);
        holder.txtUserName.setText(items.get(position).getUser_name());
        holder.txtContent.setText(items.get(position).getComment_content());
        holder.ratingBar.setRating(items.get(position).getComment_rate());
        holder.txtLike.setText("Hữu ích (" + changeValue(items.get(position).getComment_like()) +")");
        holder.userNameAndLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileDialog profileDialog = new ProfileDialog(context, items.get(position).getUser_id());
                profileDialog.show();
            }
        });
        holder.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileDialog profileDialog = new ProfileDialog(context, items.get(position).getUser_id());
                profileDialog.show();
            }
        });
        if(items.get(position).getComment_upload_time() != null)
        {
            holder.txtCommentUpload.setText(sdf.format(items.get(position).getComment_upload_time()));
        }

        DocumentReference documentReference = db.collection("Users").document(items.get(position).getUser_id());
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
        db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckLike").whereEqualTo("comment_id", commentId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    return;
                }
                if(value.size() !=0)
                {
                    holder.btnLike.setBackgroundTintList(colorStateList2);
                    holder.txtLike.setTextColor(colorStateList);
                    holder.imgLike.setImageTintList(colorStateList);
                    holder.btnLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            holder.btnLike.setImageDrawable(context.getResources().getDrawable(R.drawable.like, null));
                            DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckLike").document(commentId);
                            documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });
                            HashMap map2 = new HashMap<>();
                            if(items.get(position).getComment_like() - 1 < 0)
                            {
                                map2.put(KEY_COMMENT_LIKE, 0);
                            }else map2.put(KEY_COMMENT_LIKE, items.get(position).getComment_like() - 1);
                            DocumentReference documentReference1 = db.collection("Courses").document(courseId).collection("Comment").document(commentId);
                            documentReference1.update(map2).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                }
                            });
                        }
                    });
                }
                else{
                    holder.btnLike.setBackgroundTintList(colorStateList);
                    holder.txtLike.setTextColor(colorStateList2);
                    holder.imgLike.setImageTintList(colorStateList2);
                    holder.btnLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            holder.btnLike.setImageDrawable(context.getResources().getDrawable(R.drawable.liked, null));
                            HashMap map = new HashMap<>();
                            map.put(KEY_CHECK_LIKE_COURSE_ID, courseId);
                            map.put(KEY_CHECK_LIKE_COMMENT_ID, commentId);
                            DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckLike").document(commentId);
                            documentReference.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });
                            HashMap map2 = new HashMap<>();
                            map2.put(KEY_COMMENT_LIKE, items.get(position).getComment_like() + 1);
                            DocumentReference documentReference1 = db.collection("Courses").document(courseId).collection("Comment").document(commentId);
                            documentReference1.update(map2).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                }
                            });
                        }
                    });
                }

            }
        });
    }
    public void release(){
        context = null;
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
    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout userNameAndLevel;
        ConstraintLayout constraintComment;
        TextView txtUserName, txtCommentUpload, txtLike;
        TextView txtContent;
        AppCompatRatingBar ratingBar;
        ShapeableImageView imgUser;
        LinearLayout btnLike;
        ImageView imgLike, imgUserLevel, btnOption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintComment = itemView.findViewById(R.id.constraintComment);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtCommentUpload = itemView.findViewById(R.id.txtUpload);
            txtContent = itemView.findViewById(R.id.txtCommentContent);
            ratingBar = itemView.findViewById(R.id.ratingUser);
            imgUser = itemView.findViewById(R.id.imgUser);
            btnOption = itemView.findViewById(R.id.btnOption);
            btnLike = itemView.findViewById(R.id.btnLike);
            txtLike = itemView.findViewById(R.id.txtLike);
            imgLike = itemView.findViewById(R.id.imgLike);
            imgUserLevel = itemView.findViewById(R.id.comment_user_level);
            userNameAndLevel = itemView.findViewById(R.id.userNameAndLevel);
        }

    }

}
