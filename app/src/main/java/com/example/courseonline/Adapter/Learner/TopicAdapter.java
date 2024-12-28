package com.example.courseonline.Adapter.Learner;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.courseonline.Activity.Learner.DetailQuestionActivity;
import com.example.courseonline.Dialog.Learner.ProfileDialog;
import com.example.courseonline.Domain.TopicClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TopicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private static Context context;
    private String filterPattern = "";
    private List<Object> combinedList = new ArrayList<>();
    private List<Object> combinedListFull = new ArrayList<>();
    private static FirebaseFirestore db;
    private static FirebaseAuth mAuth;
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final int VIEW_TYPE_DATE_HEADER = 0;
    private static final int VIEW_TYPE_TOPIC = 1;

    public TopicAdapter(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void updateData(ArrayList<String> dateHeaders, ArrayList<TopicClass> topics) {
        List<Object> newCombinedList = new ArrayList<>();

        for (String dateHeader : dateHeaders) {
            newCombinedList.add(dateHeader);
            for (TopicClass topic : topics) {
                if (isSameDate(topic.getTopic_upload(), dateHeader)) {
                    newCombinedList.add(topic);
                }
            }
        }

        TopicDiffCallback diffCallback = new TopicDiffCallback(combinedList, newCombinedList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        combinedList.clear();
        combinedList.addAll(newCombinedList);
        combinedListFull.clear();
        combinedListFull.addAll(combinedList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DATE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.viewholder_item_date_header, parent, false);
            return new DateHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.viewholder_item_topic, parent, false);
            return new TopicViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DateHeaderViewHolder) {
            String dateHeader = (String) combinedList.get(position);
            ((DateHeaderViewHolder) holder).bind(dateHeader);
        } else if (holder instanceof TopicViewHolder) {
            TopicClass topic = (TopicClass) combinedList.get(position);
            ((TopicViewHolder) holder).bind(topic, filterPattern);
        }
    }

    @Override
    public int getItemCount() {
        return combinedList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (combinedList.get(position) instanceof String) {
            return VIEW_TYPE_DATE_HEADER;
        } else if (combinedList.get(position) instanceof TopicClass) {
            return VIEW_TYPE_TOPIC;
        }
        return -1;
    }

    static class DateHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView dateHeader;

        public DateHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            dateHeader = itemView.findViewById(R.id.textViewDate);
        }

        public void bind(String date) {
            dateHeader.setText(date);
        }
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView topicContent;
        TextView viewCount;
        TextView likeCount;
        TextView uploadTime;
        TextView topicSubject;
        LinearLayout btnJoinDiscussion;
        GridLayout imageGrid;
        ShapeableImageView imgUserTopic;
        LinearLayout userInfoTopic;
        TextView txtUserNameTopic;
        ImageView topicUserLevel;
        ImageView heartIcon;
        ConstraintLayout topicDetail;
        CardView cardView;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            topicContent = itemView.findViewById(R.id.topicContent);
            viewCount = itemView.findViewById(R.id.viewCount);
            likeCount = itemView.findViewById(R.id.likeCount);
            uploadTime = itemView.findViewById(R.id.uploadTime);
            btnJoinDiscussion = itemView.findViewById(R.id.btnJoinDiscussion);
            imageGrid = itemView.findViewById(R.id.imageGrid);
            imgUserTopic = itemView.findViewById(R.id.imgUserTopic);
            userInfoTopic = itemView.findViewById(R.id.userInfoTopic);
            txtUserNameTopic = itemView.findViewById(R.id.txtUserNameTopic);
            topicUserLevel = itemView.findViewById(R.id.topic_user_level);
            heartIcon = itemView.findViewById(R.id.iconHeartTopic);
            topicDetail = itemView.findViewById(R.id.topicDetail);
            topicSubject = itemView.findViewById(R.id.topicSubject);
            cardView = itemView.findViewById(R.id.cardView_Topic);
        }

        public void bind(TopicClass topic, String keyword) {
            topicContent.setText(highlightText(topic.getTopic_content(), keyword));
            topicSubject.setText(highlightText(topic.getTopic_subject(), keyword));
            if (topic.getTopic_upload() != null) {
                uploadTime.setText(timeFormat.format(topic.getTopic_upload()));
            }

            DocumentReference documentReference = db.collection("Users").document(topic.getTopic_sender_id());
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        return;
                    }
                    if (value != null && value.exists()) {
                        if(value.getString("user_permission").equals("2")){
                            txtUserNameTopic.setText(value.getString("user_name") + " (Giáo viên)");
//                            txtUserNameTopic.setTextColor(ContextCompat.getColor(context, R.color.dark_red));
                            String userAvatar = value.getString("user_avatar");
                            Picasso.get()
                                    .load(userAvatar)
                                    .placeholder(R.drawable.default_image).fit()
                                    .into(imgUserTopic);
                            imgUserTopic.setStrokeColorResource(R.color.grey);
                        }else{
                            txtUserNameTopic.setText(value.getString("user_name"));
                            String userAvatar = value.getString("user_avatar");
                            String currentLevel = value.getString("user_level") != null ? value.getString("user_level") : "1";


                            Picasso.get()
                                    .load(userAvatar)
                                    .placeholder(R.drawable.default_image).fit()
                                    .into(imgUserTopic);
                            switch (currentLevel) {
                                case "1":
                                    topicUserLevel.setImageResource(R.drawable.level_1);
                                    imgUserTopic.setStrokeColorResource(R.color.grey);
                                    break;
                                case "2":
                                    topicUserLevel.setImageResource(R.drawable.level_2);
                                    imgUserTopic.setStrokeColorResource(R.color.mint);
                                    break;
                                case "3":
                                    topicUserLevel.setImageResource(R.drawable.level_3);
                                    imgUserTopic.setStrokeColorResource(R.color.blue);
                                    break;
                                case "4":
                                    topicUserLevel.setImageResource(R.drawable.level_4);
                                    imgUserTopic.setStrokeColorResource(R.color.purple);
                                    break;
                                case "5":
                                    topicUserLevel.setImageResource(R.drawable.level_5);
                                    imgUserTopic.setStrokeColorResource(R.color.red);
                                    break;
                                default:
                                    topicUserLevel.setImageResource(R.drawable.error);
                                    imgUserTopic.setStrokeColorResource(R.color.grey);
                                    break;
                            }
                        }



                    }
                }
            });
            if(topic.getTopic_sender_id().equals(mAuth.getCurrentUser().getUid())){
                Resources res = context.getResources();

                int pxMarginLeft = 105;
                int pxMarginTop = 10;
                int pxMarginRight = 0;
                int pxMarginBottom = 10;

                float dpMarginLeft = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,  pxMarginLeft, res.getDisplayMetrics());
                float  dpMarginTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pxMarginTop, res.getDisplayMetrics());
                float  dpMarginRight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pxMarginRight, res.getDisplayMetrics());
                float  dpMarginBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pxMarginBottom , res.getDisplayMetrics());

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) cardView.getLayoutParams();
                params.setMargins((int)dpMarginLeft, (int)dpMarginTop, (int)dpMarginRight, (int)dpMarginBottom);
                topicDetail.setBackgroundColor(ContextCompat.getColor(context,R.color.light_blue));
                cardView.setLayoutParams(params);
            }else{
                Resources res = context.getResources();

                int pxMarginLeft = 12;
                int pxMarginTop = 10;
                int pxMarginRight = 105;
                int pxMarginBottom = 10;

                float dpMarginLeft = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,  pxMarginLeft, res.getDisplayMetrics());
                float  dpMarginTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pxMarginTop, res.getDisplayMetrics());
                float  dpMarginRight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pxMarginRight, res.getDisplayMetrics());
                float  dpMarginBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pxMarginBottom , res.getDisplayMetrics());

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) cardView.getLayoutParams();
                params.setMargins((int)dpMarginLeft, (int)dpMarginTop, (int)dpMarginRight, (int)dpMarginBottom);
                topicDetail.setBackgroundColor(0);
                cardView.setLayoutParams(params);
            }
            getLikeStatus(topic.getDiscussion_id(), topic.getTopic_id());
            getViewerCount(topic.getDiscussion_id(), topic.getTopic_id());

            heartIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleLike(topic.getDiscussion_id(), topic.getTopic_id());
                }
            });

            loadImages(imageGrid, topic.getTopic_attach());
            btnJoinDiscussion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    joinDiscussion(topic.getDiscussion_id(), topic.getTopic_id(), topic.getTopic_subject());
                }
            });
            topicDetail.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(topic.getTopic_sender_id().equals(mAuth.getCurrentUser().getUid())){
                        showDeleteDialog(context,topic.getDiscussion_id(), topic.getTopic_id(), v);
                    }
                    return true;
                }
            });
            imgUserTopic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileDialog profileDialog = new ProfileDialog(context, topic.getTopic_sender_id());
                    profileDialog.show();
                }
            });
            userInfoTopic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileDialog profileDialog = new ProfileDialog(context, topic.getTopic_sender_id());
                    profileDialog.show();
                }
            });
        }

        private void getLikeStatus(String discussionId, String topicId) {
            DocumentReference topicRef = db.collection("Discussions").document(discussionId).collection("Topics").document(topicId);
            topicRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        List<String> likes = (List<String>) document.get("topic_like");
                        if (likes != null) {
                            int likeCountValue = likes.size();
                            likeCount.setText(likeCountValue + " lượt yêu thích");
                            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            heartIcon.setImageResource(likes.contains(currentUserId) ? R.drawable.favourite_fill : R.drawable.favourite_unfill);
                        } else {
                            likeCount.setText("0 yêu thích");
                            heartIcon.setImageResource(R.drawable.favourite_unfill);
                        }
                    }
                }
            });
        }
        private void getViewerCount(String discussionId, String topicId) {
            Query query = db.collection("Discussions").document(discussionId).collection("Topics").document(topicId).collection("Messages");
            AggregateQuery countQuery = query.whereEqualTo("message_state", true).count();
            countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        AggregateQuerySnapshot snapshot = task.getResult();
                        viewCount.setText("Tham gia thảo luận (" + snapshot.getCount() + ")");
                    }
                }
            });
        }
        private void toggleLike(String discussionId, String topicId) {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DocumentReference topicRef = db.collection("Discussions").document(discussionId).collection("Topics").document(topicId);

            topicRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        List<String> likes = (List<String>) document.get("topic_like");

                        if (likes == null) {
                            likes = new ArrayList<>();
                        }

                        if (likes.contains(currentUserId)) {
                            likes.remove(currentUserId);
                        } else {
                            likes.add(currentUserId);
                        }

                        topicRef.update("topic_like", likes).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    getLikeStatus(discussionId, topicId);
                                }
                            }
                        });
                    }
                }
            });
        }

        private void joinDiscussion(String discussionId, String topicId, String topicTitle) {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DocumentReference topicRef = db.collection("Discussions").document(discussionId)
                    .collection("Topics").document(topicId);

            topicRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        List<String> viewers = (List<String>) document.get("topic_viewer");
                        if (viewers == null) {
                            viewers = new ArrayList<>();
                        }

                        if (!viewers.contains(currentUserId)) {
                            viewers.add(currentUserId);
                            topicRef.update("topic_viewer", viewers).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                    }
                                }
                            });
                        }
                    }
                    Intent intent = new Intent(context, DetailQuestionActivity.class);
                    intent.putExtra("discussion_id", discussionId);
                    intent.putExtra("topic_id", topicId);
                    intent.putExtra("topic_title", topicTitle);
                    context.startActivity(intent);
                }
            });
        }

        private static void showDeleteDialog(Context context, String discussionId, String topicId,View v) {
            PopupMenu popupMenu = new PopupMenu(context, v);

            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.menu_item_message_topic, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.discussion_delete_option) {
                        deleteTopic(discussionId, topicId);
                        return true;
                    }
                    return false;
                }
            });

            popupMenu.show();
        }

        private static void deleteTopic(String discussionId, String topicId) {
            DocumentReference topicRef = db.collection("Discussions").document(discussionId)
                    .collection("Topics").document(topicId);
            topicRef.update("topic_state", false).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                    }
                }
            });
        }
    }
    private static SpannableString highlightText(String text, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return new SpannableString(text);
        }

        SpannableString spannableString = new SpannableString(text);
        int startPos = text.toLowerCase().indexOf(keyword.toLowerCase());

        while (startPos >= 0) {
            int endPos = startPos + keyword.length();
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            startPos = text.toLowerCase().indexOf(keyword.toLowerCase(), endPos);
        }

        return spannableString;
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Object> filteredList = new ArrayList<>();
                filterPattern = constraint.toString().toLowerCase().trim();
                boolean topicFound = false;

                if (filterPattern.isEmpty()) {
                    topicFound = true;
                    filteredList.addAll(combinedListFull);
                } else {
                    for (Object item : combinedListFull) {
                        if (item instanceof TopicClass) {
                            TopicClass topic = (TopicClass) item;
                            if (topic.getTopic_subject().toLowerCase().contains(filterPattern) ||
                                    topic.getTopic_content().toLowerCase().contains(filterPattern)) {
                                filteredList.add(topic);
                                topicFound = true;
                            }
                        } else {
                                filteredList.add(item);
                        }
                    }
                }

                if (!topicFound) {
                    filteredList.removeIf(item -> !(item instanceof TopicClass));
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                combinedList.clear();
                if (results.values != null) {
                    combinedList.addAll((List) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }


    private static void loadImages(GridLayout imageGrid, List<String> imageUrls) {
        imageGrid.removeAllViews();
        int columnCount = 3;

        if (imageUrls == null) {
            imageUrls = new ArrayList<>();
        }

        if (imageUrls.size() == 1) {
            columnCount = 1;
        } else if (imageUrls.size() <= 4) {
            columnCount = 2;
        }

        imageGrid.setColumnCount(columnCount);

        for (String imageUrl : imageUrls) {
            FrameLayout frameLayout = new FrameLayout(context);
            ShapeableImageView imageWithStroke = new ShapeableImageView(context);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            if (imageUrls.size() == 1) {
                int sizeInDp = 100;
                float density = context.getResources().getDisplayMetrics().density;
                params.width = (int) (sizeInDp * density* 2.5);
                params.height = (int) (sizeInDp * density * 1.5);
                params.setMargins(4, 4, 4, 4);
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

                imageWithStroke.setLayoutParams(params);
                imageWithStroke.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageWithStroke.setStrokeColorResource(R.color.black);
                imageWithStroke.setStrokeWidth(1);

                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .into(imageWithStroke);
            } else if (imageUrls.size() <= 4) {
                int sizeInDp = 100;
                float density = context.getResources().getDisplayMetrics().density;
                params.width = (int) (sizeInDp * density* 1.1);
                params.height = (int) (sizeInDp * density * 1.5);
                params.setMargins(4, 4, 4, 4);
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

                imageWithStroke.setLayoutParams(params);
                imageWithStroke.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageWithStroke.setStrokeColorResource(R.color.black);
                imageWithStroke.setStrokeWidth(1);

                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .into(imageWithStroke);
            }



            imageWithStroke.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImageDialog(context, Uri.parse(imageUrl));
                }
            });

            frameLayout.addView(imageWithStroke);
            imageGrid.addView(frameLayout);
        }
    }

    private static void showImageDialog(Context context, Uri imageUri) {
        Dialog imageDialog = new Dialog(context);
        imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imageDialog.setContentView(R.layout.dialog_zoom_image);

        ImageView imageView = imageDialog.findViewById(R.id.dialogImageView);
        Glide.with(context).load(imageUri).centerInside().into(imageView);
        imageDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView btnClose = imageDialog.findViewById(R.id.btnCloseZoom);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDialog.dismiss();
            }
        });

        imageDialog.setCancelable(true);
        imageDialog.show();
    }

    private boolean isSameDate(Date topicDate, String dateStr) {
        String topicDateStr = dateFormat.format(topicDate);
        return topicDateStr.equals(dateStr);
    }

    private static class TopicDiffCallback extends DiffUtil.Callback {
        private final List<Object> oldList;
        private final List<Object> newList;

        public TopicDiffCallback(List<Object> oldList, List<Object> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Object oldItem = oldList.get(oldItemPosition);
            Object newItem = newList.get(newItemPosition);
            if (oldItem instanceof TopicClass && newItem instanceof TopicClass) {
                return ((TopicClass) oldItem).getTopic_id().equals(((TopicClass) newItem).getTopic_id());
            } else if (oldItem instanceof String && newItem instanceof String) {
                return oldItem.equals(newItem);
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Object oldItem = oldList.get(oldItemPosition);
            Object newItem = newList.get(newItemPosition);
            if (oldItem instanceof TopicClass && newItem instanceof TopicClass) {
                return oldItem.equals(newItem);
            } else if (oldItem instanceof String && newItem instanceof String) {
                return oldItem.equals(newItem);
            }
            return false;
        }
    }
}
