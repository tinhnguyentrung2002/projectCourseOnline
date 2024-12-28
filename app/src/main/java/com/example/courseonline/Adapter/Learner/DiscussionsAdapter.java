package com.example.courseonline.Adapter.Learner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.courseonline.Activity.Learner.TopicActivity;
import com.example.courseonline.Domain.DiscussionClass;
import com.example.courseonline.Domain.TopicClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiscussionsAdapter extends RecyclerView.Adapter<DiscussionsAdapter.MessageViewHolder> {

    private List<DiscussionClass> discussions;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Context context;

    public DiscussionsAdapter(List<DiscussionClass> discussions, Context context) {
        this.discussions = discussions;
        this.context = context;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_mail_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DiscussionClass discussion = discussions.get(position);
        holder.txtQuestion.setText(discussion.getDiscussion_title());
        String currentUserId = mAuth.getCurrentUser().getUid();
        db.collection("Discussions")
                .document(discussion.getDiscussion_id())
                .collection("Topics").whereEqualTo("topic_state", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        if (value != null) {
                            int unreadCount = 0;

                            for (DocumentSnapshot topicSnapshot : value.getDocuments()) {
                                TopicClass topic = topicSnapshot.toObject(TopicClass.class);
                                List<String> topicSeen = topic.getTopic_seen();
                                if (topicSeen == null || !topicSeen.contains(currentUserId)) {
                                    unreadCount++;
                                }
                            }

                            if (unreadCount > 0) {
                                holder.txtUnread.setVisibility(View.VISIBLE);
                                holder.txtUnread.setText(String.valueOf(unreadCount));
                            } else {
                                holder.txtUnread.setVisibility(View.GONE);
                            }
                        }
                    }
                });
        db.collection("Discussions")
                .document(discussion.getDiscussion_id())
                .collection("Topics")
                .whereEqualTo("topic_state", true)
                .orderBy("topic_upload", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("testt", String.valueOf(task.getException()));
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            DocumentSnapshot topicSnapshot = task.getResult().getDocuments().get(0);
                            String topicSubject = topicSnapshot.getString("topic_subject");
                            Date topicUpload = topicSnapshot.getDate("topic_upload");
                            List<String> topicAttach = (List<String>) topicSnapshot.get("topic_attach");

                            holder.txtDescription.setText(topicSubject);
                            holder.txtTime.setText(formatTime(topicUpload));

                            if (topicAttach != null && !topicAttach.isEmpty()) {
                                Glide.with(holder.itemView.getContext())
                                        .load(topicAttach.get(0))
                                        .apply(RequestOptions.placeholderOf(R.drawable.default_image))
                                        .into(holder.topicImage);
                            } else {
                                holder.topicImage.setVisibility(View.GONE);
                            }
                        } else {
                            holder.txtDescription.setText("Không có chủ đề");
                            holder.txtTime.setText("");
                            holder.topicImage.setVisibility(View.GONE);
                        }
                    }
                });

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .centerInside();

        Glide.with(holder.itemView.getContext())
                .load(discussion.getDiscussion_thumbnail())
                .apply(options)
                .into(holder.discussionAvatar);
        holder.mailLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                leave(discussion.getDiscussion_id());
                return true;
            }
        });
        db.collection("Discussions").document(discussion.getDiscussion_id()).collection("DiscussionParticipants")
                .document(mAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.exists()) {
                            boolean allowNotification = false;
                            allowNotification = value.getBoolean("allowNotification");
                            if (allowNotification) {
                              holder.notificationImage.setVisibility(View.GONE);
                            } else {
                                holder.notificationImage.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
        holder.mailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Discussions")
                        .document(discussion.getDiscussion_id())
                        .collection("Topics")
                        .whereEqualTo("topic_state", true)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for (DocumentSnapshot topicSnapshot : task.getResult()) {
                                        List<String> topicSeen = (List<String>) topicSnapshot.get("topic_seen");
                                        if (topicSeen != null && !topicSeen.contains(currentUserId)) {
                                            topicSeen.add(currentUserId);
                                        } else if (topicSeen == null) {
                                            topicSeen = new ArrayList<>();
                                            topicSeen.add(currentUserId);
                                        }
                                        topicSnapshot.getReference().update("topic_seen", topicSeen);
                                    }
                                }
                            }});
                Intent intent = new Intent(context, TopicActivity.class);
                intent.putExtra("discussion_id", discussions.get(position).getDiscussion_id());
                intent.putExtra("course_id", discussions.get(position).getCourse_id());
                intent.putExtra("discussion_title", discussions.get(position).getDiscussion_title());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return discussions.size();
    }
    public void release(){
        context = null;
    }
    private void leave(String discussion_id){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.dialog_leave_group);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LinearLayout btnLeave = bottomSheetDialog.findViewById(R.id.btnLeaveGroup);
        if (btnLeave != null) {
            btnLeave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("Rời nhóm thảo luận")
                            .setMessage("Bạn có chắc chắn muốn rời khỏi nhóm thảo luận này không?")
                            .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.collection("Discussions")
                                            .document(discussion_id)
                                            .collection("DiscussionParticipants")
                                            .document(mAuth.getCurrentUser().getUid())
                                            .delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        db.collection("Users")
                                                                .document(mAuth.getCurrentUser().getUid())
                                                                .collection("DiscussionGroups")
                                                                .document(discussion_id)
                                                                .delete()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(context, "Bạn đã rời nhóm thảo luận",Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        Toast.makeText(context, "Có lỗi xảy ra, không thể rời nhóm thảo luận",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    bottomSheetDialog.dismiss();
                }
            });
        }

        bottomSheetDialog.setCancelable(true);

        bottomSheetDialog.show();
    }

    public  String formatTime(Date timestamp) {
        Calendar current = Calendar.getInstance();
        Calendar messageTime = Calendar.getInstance();
        messageTime.setTime(timestamp);

        if (current.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
                current.get(Calendar.DAY_OF_YEAR) == messageTime.get(Calendar.DAY_OF_YEAR)) {
            SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return hourFormat.format(timestamp);
        } else if (current.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
                current.get(Calendar.DAY_OF_YEAR) == messageTime.get(Calendar.DAY_OF_YEAR) - 1) {
            return "Hôm qua";
        } else if (current.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
                current.get(Calendar.DAY_OF_YEAR) >= messageTime.get(Calendar.DAY_OF_YEAR) - 7) {
            SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.getDefault());
            return dayFormat.format(timestamp);
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());
            return dateFormat.format(timestamp);
        }
    }
    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtQuestion, txtDescription, txtTime, txtUnread;
        LinearLayout mailLayout;
        ShapeableImageView discussionAvatar;
        AppCompatImageView topicImage, notificationImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtQuestion = itemView.findViewById(R.id.chat_name);
            txtTime = itemView.findViewById(R.id.time_chat);
            txtDescription = itemView.findViewById(R.id.chat_last_message);
            mailLayout = itemView.findViewById(R.id.mail_layout);
            discussionAvatar = itemView.findViewById(R.id.chat_avatar);
            topicImage = itemView.findViewById(R.id.chat_image);
            txtUnread = itemView.findViewById(R.id.unreadCount);
            notificationImage = itemView.findViewById(R.id.notification_icon);
        }
    }
}
