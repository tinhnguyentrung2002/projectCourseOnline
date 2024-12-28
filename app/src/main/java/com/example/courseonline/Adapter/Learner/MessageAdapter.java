package com.example.courseonline.Adapter.Learner;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.courseonline.Dialog.Learner.ProfileDialog;
import com.example.courseonline.Domain.MessageClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private static Context context;
    private String filterPattern = "";
    private List<Object> combinedList = new ArrayList<>();
    private List<Object> combinedListFull = new ArrayList<>();
    private static FirebaseFirestore db;
    private static FirebaseAuth mAuth;
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final int VIEW_TYPE_DATE_HEADER = 0;
    private static final int VIEW_TYPE_MESSAGE = 1;
    private String discussionId;

    public MessageAdapter(Context context, String discussionId) {
        this.context = context;
        this.discussionId = discussionId;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void updateData(ArrayList<String> dateHeaders, ArrayList<MessageClass> messages) {
        List<Object> newCombinedList = new ArrayList<>();
        for (String dateHeader : dateHeaders) {
            newCombinedList.add(dateHeader);
            for (MessageClass message : messages) {
                if (isSameDate(message.getMessage_upload(), dateHeader)) {
                    newCombinedList.add(message);
                }
            }
        }

        MessageDiffCallback diffCallback = new MessageDiffCallback(combinedList, newCombinedList);
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
            View view = LayoutInflater.from(context).inflate(R.layout.viewholder_item_message, parent, false);
            return new MessageViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DateHeaderViewHolder) {
            String dateHeader = (String) combinedList.get(position);
            ((DateHeaderViewHolder) holder).bind(dateHeader);
        } else if (holder instanceof MessageViewHolder) {
            MessageClass message = (MessageClass) combinedList.get(position);
            ((MessageViewHolder) holder).bind(message, filterPattern, this.discussionId);
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
        } else if (combinedList.get(position) instanceof MessageClass) {
            return VIEW_TYPE_MESSAGE;
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

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageContent;
        TextView uploadTime;
        GridLayout imageGrid;
        ShapeableImageView imgUserMessages;
        LinearLayout userInfoMessages;
        TextView txtUserNameMessage;
        ImageView messageUserLevel;
        LinearLayout heartIcon, messageLayout;
        LinearLayout bubbleLayout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageLayout = itemView.findViewById(R.id.messageLayout);
            messageContent = itemView.findViewById(R.id.messageBubble);
            uploadTime = itemView.findViewById(R.id.messageTime);
            imageGrid = itemView.findViewById(R.id.imageGridMessage);
            imgUserMessages = itemView.findViewById(R.id.imgUserMessage);
            userInfoMessages = itemView.findViewById(R.id.userInfoMessage);
            txtUserNameMessage = itemView.findViewById(R.id.txtUserNameMessage);
            messageUserLevel = itemView.findViewById(R.id.message_user_level);
            bubbleLayout = itemView.findViewById(R.id.bubbleLayout);
        }

        public void bind(MessageClass message, String keyword, String discussionId) {
            messageContent.setText(highlightText(message.getMessage_content(), keyword));
            if (message.getMessage_upload() != null) {
                uploadTime.setText(timeFormat.format(message.getMessage_upload()));
            }

            DocumentReference documentReference = db.collection("Users").document(message.getMessage_sender_id());
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        return;
                    }
                    if (value != null && value.exists()) {
                        if(value.getString("user_permission").equals("2")){
                            txtUserNameMessage.setText(value.getString("user_name") + " (Giáo viên)");
                            txtUserNameMessage.setTextColor(ContextCompat.getColor(context, R.color.dark_red));
                            String userAvatar = value.getString("user_avatar");
                            Picasso.get()
                                    .load(userAvatar)
                                    .placeholder(R.drawable.default_image).fit()
                                    .into(imgUserMessages);
                        }else{
                            txtUserNameMessage.setText(value.getString("user_name"));
                            String userAvatar = value.getString("user_avatar");
                            String currentLevel = value.getString("user_level") != null ? value.getString("user_level") : "1";


                            Picasso.get()
                                    .load(userAvatar)
                                    .placeholder(R.drawable.default_image).fit()
                                    .into(imgUserMessages);
                            switch (currentLevel) {
                                case "1":
                                    messageUserLevel.setImageResource(R.drawable.level_1);
                                    imgUserMessages.setStrokeColorResource(R.color.grey);
                                    break;
                                case "2":
                                    messageUserLevel.setImageResource(R.drawable.level_2);
                                    imgUserMessages.setStrokeColorResource(R.color.mint);
                                    break;
                                case "3":
                                    messageUserLevel.setImageResource(R.drawable.level_3);
                                    imgUserMessages.setStrokeColorResource(R.color.blue);
                                    break;
                                case "4":
                                    messageUserLevel.setImageResource(R.drawable.level_4);
                                    imgUserMessages.setStrokeColorResource(R.color.purple);
                                    break;
                                case "5":
                                    messageUserLevel.setImageResource(R.drawable.level_5);
                                    imgUserMessages.setStrokeColorResource(R.color.red);
                                    break;
                                default:
                                    messageUserLevel.setImageResource(R.drawable.error);
                                    imgUserMessages.setStrokeColorResource(R.color.grey);
                                    break;
                            }
                        }



                    }
                }
            });
            if(message.getMessage_content() == null){
                messageContent.setVisibility(View.GONE);
            }else {
                messageContent.setVisibility(View.VISIBLE);
            }
            if(message.getMessage_sender_id().equals(mAuth.getCurrentUser().getUid())){
                bubbleLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.round_back_blue_10));
                messageLayout.setGravity(Gravity.END);
                imgUserMessages.setVisibility(View.GONE);
                txtUserNameMessage.setVisibility(View.GONE);
                messageUserLevel.setVisibility(View.GONE);
            }else{
                bubbleLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.round_back_white_10));
                messageLayout.setGravity(Gravity.START);
                imgUserMessages.setVisibility(View.VISIBLE);
                txtUserNameMessage.setVisibility(View.VISIBLE);
                messageUserLevel.setVisibility(View.VISIBLE);
            }
            loadImages(imageGrid, message.getMessage_attach());
            bubbleLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(message.getMessage_sender_id().equals(mAuth.getCurrentUser().getUid())){
                        showDeleteDialog( context,discussionId, message.getTopic_id(), message.getMessage_id(), v);
                    }
                    return true;
                }
            });
            imgUserMessages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileDialog profileDialog = new ProfileDialog(context, message.getMessage_sender_id());
                    profileDialog.show();
                }
            });
            userInfoMessages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileDialog profileDialog = new ProfileDialog(context, message.getMessage_sender_id());
                    profileDialog.show();
                }
            });
        }

        private static void showDeleteDialog(Context context, String discussionId, String topicId, String messageId, View v) {
            PopupMenu popupMenu = new PopupMenu(context, v);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.menu_item_message_topic, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.discussion_delete_option) {
                        deleteMessage(discussionId, topicId, messageId);
                        return true;
                    }
                    return false;
                }
            });

            popupMenu.show();
        }

        private static void deleteMessage(String discussionId, String topicId, String messageId) {
            DocumentReference topicRef = db.collection("Discussions").document(discussionId)
                    .collection("Topics").document(topicId).collection("Messages").document(messageId);
            topicRef.update("message_state", false).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                boolean messageFound = false;

                if (filterPattern.isEmpty()) {
                    messageFound = true;
                    filteredList.addAll(combinedListFull);
                } else {
                    for (Object item : combinedListFull) {
                        if (item instanceof MessageClass) {
                            MessageClass message = (MessageClass) item;
                            if (message.getMessage_content().toLowerCase().contains(filterPattern)) {
                                filteredList.add(message);
                                messageFound = true;
                            }
                        } else {
                            filteredList.add(item);
                        }
                    }
                }

                if (!messageFound) {
                    filteredList.removeIf(item -> !(item instanceof MessageClass));
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

        // Căn chỉnh số lượng cột tùy vào số lượng hình ảnh
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
                int sizeInDp = 120;
                float density = context.getResources().getDisplayMetrics().density;
                params.width = (int) (sizeInDp * density * 2);
                params.height = (int) (sizeInDp * density * 1.2);
                params.setMargins(50, 12, 5, 0);
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            }
            else if (imageUrls.size() <= 4) {
                int sizeInDp = 100;
                float density = context.getResources().getDisplayMetrics().density;
                params.width = (int) (sizeInDp * density * 1.1);
                params.height = (int) (sizeInDp * density * 1.1);
                params.setMargins(50, 12, 8, 8);
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            }
            // Căn chỉnh kích thước cho các trường hợp còn lại (nếu có)

            // Cấu hình hình ảnh
            imageWithStroke.setLayoutParams(params);
            imageWithStroke.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageWithStroke.setStrokeColorResource(R.color.black);
            imageWithStroke.setStrokeWidth(1);

            // Tải hình ảnh từ URL
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(imageWithStroke);

            // Xử lý sự kiện click cho hình ảnh
            imageWithStroke.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImageDialog(context, Uri.parse(imageUrl));
                }
            });

            // Thêm hình ảnh vào FrameLayout và sau đó thêm vào GridLayout
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

    private static class MessageDiffCallback extends DiffUtil.Callback {
        private final List<Object> oldList;
        private final List<Object> newList;

        public MessageDiffCallback(List<Object> oldList, List<Object> newList) {
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
            if (oldItem instanceof MessageClass && newItem instanceof MessageClass) {
                return ((MessageClass) oldItem).getTopic_id().equals(((MessageClass) newItem).getTopic_id());
            } else if (oldItem instanceof String && newItem instanceof String) {
                return oldItem.equals(newItem);
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Object oldItem = oldList.get(oldItemPosition);
            Object newItem = newList.get(newItemPosition);
            if (oldItem instanceof MessageClass && newItem instanceof MessageClass) {
                return oldItem.equals(newItem);
            } else if (oldItem instanceof String && newItem instanceof String) {
                return oldItem.equals(newItem);
            }
            return false;
        }
    }
}
