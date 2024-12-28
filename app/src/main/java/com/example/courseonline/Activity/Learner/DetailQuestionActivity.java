package com.example.courseonline.Activity.Learner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Learner.AttachImageAdapter;
import com.example.courseonline.Adapter.Learner.MessageAdapter;
import com.example.courseonline.Domain.MessageClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class DetailQuestionActivity extends AppCompatActivity implements AttachImageAdapter.OnImageRemoveListener{
    private static final int PICK_IMAGES = 1;
    private static final int PICK_FILE_REQUEST = 2;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();
    private EmojIconActions emojIconActions;
    private RelativeLayout mainLayout;
    private Toast toast;
    private LinearLayout searchBox;
    private ImageButton buttonSmile, buttonAttach, buttonImage, buttonSend;
    private EmojiconEditText editTextMessage;
    private RecyclerView recyclerViewMessages, recyclerAttachImage;
    private MessageAdapter messageAdapter;
    private FloatingActionButton fabScrollToBottom;
    private SearchView editTextSearch;
    private TextView btnCloseSearch, txtSubject;
    private ImageView btnBackMessage, btnSearch;
    private ArrayList<String> dateHeaders = new ArrayList<>();
    private ArrayList<MessageClass> messages = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private TextView textViewEmpty;
    private Date lastLoadDate;
    private String discussion_id, discussion_title, topic_id;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private AttachImageAdapter adapter;
    private final static String KEY_MESSAGE_ID = "message_id";
    private final static String KEY_MESSAGE_SENDER_ID = "message_sender_id";
    private final static String KEY_MESSAGE_CONTENT = "message_content";
    private final static String KEY_MESSAGE_ATTACH = "message_attach";
    private final static String KEY_MESSAGE_UPLOAD = "message_upload";
    private final static String KEY_MESSAGE_STATE = "message_state";
    private final static String KEY_TOPIC_ID = "topic_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        setContentView(R.layout.activity_detail_question);
        discussion_id = getIntent().getStringExtra("discussion_id");
        discussion_title = getIntent().getStringExtra("topic_title");
        topic_id = getIntent().getStringExtra("topic_id");

        mapping();

        emojIconActions = new EmojIconActions(getApplicationContext(),mainLayout, editTextMessage, buttonSmile);
        emojIconActions.ShowEmojIcon();
        messageAdapter = new MessageAdapter(DetailQuestionActivity.this, discussion_id);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewMessages.setLayoutManager(layoutManager);
        recyclerViewMessages.setAdapter(messageAdapter);

        db = FirebaseFirestore.getInstance();
        loadMessages();

        fabScrollToBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewMessages.smoothScrollToPosition(messages.size() + dateHeaders.size());
            }
        });
        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    hideTools();
                } else {
                    showTools();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnBackMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCloseSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSearch();
            }
        });

        recyclerViewMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0) {
                    fabScrollToBottom.show();
                } else   if (dy >= 0)  {
                    fabScrollToBottom.hide();
                }
                if (recyclerView.canScrollVertically(1)) {
                    fabScrollToBottom.show();
                }
//                if (!recyclerView.canScrollVertically(-1)) {
//                    loadMoreMessages(new Handler(Looper.getMainLooper()));
//                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              showSearch();
          }
      });
        btnCloseSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSearch();
            }
        });
        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker(DetailQuestionActivity.this);
                if(selectedImageUris.size() < 4){}
                else{
                    toastMes("Chỉ chọn tối đa 3 ảnh");
                }

            }
        });
//        buttonAttach.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openFilePicker();
//            }
//        });
        adapter = new AttachImageAdapter(selectedImageUris, new AttachImageAdapter.OnImageRemoveListener() {
            @Override
            public void onImageRemove(int position) {
                DetailQuestionActivity.this.onImageRemove(position);
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(editTextMessage.getText().toString());
            }
        });

        recyclerAttachImage.setLayoutManager(new LinearLayoutManager(DetailQuestionActivity.this, LinearLayoutManager.HORIZONTAL, false));
        recyclerAttachImage.setAdapter(adapter);
    }

    private void mapping() {
        txtSubject = findViewById(R.id.textViewSubjectMessage);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        recyclerAttachImage = findViewById(R.id.recyclerViewAttachImageMessage);
        textViewEmpty = findViewById(R.id.textViewEmptyMessage);
        fabScrollToBottom = findViewById(R.id.fabScrollToBottomMessage);
        btnBackMessage = findViewById(R.id.btnBackMessage);
        editTextSearch = findViewById(R.id.editTextSearchMessage);
        btnCloseSearch = findViewById(R.id.btnCloseSearchMessage);
        searchBox = findViewById(R.id.searchBoxMessage);
        btnSearch = findViewById(R.id.messageSearchIcon);
        buttonSend = findViewById(R.id.buttonSend);
        buttonImage = findViewById(R.id.buttonImage);
//        buttonAttach = findViewById(R.id.buttonAttachFile);
        buttonSmile = findViewById(R.id.buttonSmile);
        editTextMessage = findViewById(R.id.editTextMessage);
        mainLayout = findViewById(R.id.mainMessageLayout);

    }
    public void openImagePicker(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activity.startActivityForResult(intent, PICK_IMAGES);
    }
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    public void addImageUri(Uri uri) {
        selectedImageUris.add(uri);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onImageRemove(int position) {
        selectedImageUris.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, selectedImageUris.size());
        if(selectedImageUris.size() != 0)
        {
            hideTools();
        }else{
            showTools();
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGES && resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                if (count > 4) {
                    toastMes("Bạn chỉ được chọn tối đa 4 ảnh");
                    showTools();
                    return;
                }
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    addImageUri(imageUri);
                }
                hideTools();
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                addImageUri(imageUri);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleActivityResult(requestCode, resultCode, data);
    }
    private void showTools(){
        buttonImage.setVisibility(View.VISIBLE);
//        buttonAttach.setVisibility(View.VISIBLE);
        buttonSend.setVisibility(View.GONE);
    }
    private void hideTools(){
        buttonImage.setVisibility(View.GONE);
//        buttonAttach.setVisibility(View.GONE);
        buttonSend.setVisibility(View.VISIBLE);
    }
    private void sendMessage(String messageText) {
        if (selectedImageUris.size() == 0){
            sendMessageWithoutImage(messageText,topic_id,discussion_id);
            editTextMessage.setText("");
        } else {
            sendMessageWithImage(messageText,topic_id,discussion_id, selectedImageUris);
            editTextMessage.setText("");
            selectedImageUris.clear();
            adapter.notifyDataSetChanged();
        }
    }

    private void sendMessageWithImage(String content, String topic_id, String discussion_id, List<Uri> imageUris) {
        final ArrayList<String> downloadUrls = new ArrayList<>();
        StorageReference storageRef = storage.getReference();

        final int totalImages = imageUris.size();
        for (Uri uri : imageUris) {
            StorageReference fileRef = storageRef.child("Message/" + "message_img_" + generateRandomString(12) + System.currentTimeMillis() + ".jpg");

            fileRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUrl) {
                                    downloadUrls.add(downloadUrl.toString());
                                    if (downloadUrls.size() == totalImages) {
                                        DocumentReference documentReference = db.collection("Discussions").document(discussion_id).collection("Topics").document(topic_id).collection("Messages").document();
                                        Map<String, Object> map = new HashMap<>();
                                        map.put(KEY_MESSAGE_ID, documentReference.getId());
                                        map.put(KEY_MESSAGE_SENDER_ID, mAuth.getCurrentUser().getUid());
                                        map.put(KEY_MESSAGE_CONTENT, content);
                                        map.put(KEY_MESSAGE_ATTACH, downloadUrls);
                                        map.put(KEY_MESSAGE_UPLOAD, FieldValue.serverTimestamp());
                                        map.put(KEY_MESSAGE_STATE, true);
                                        map.put(KEY_TOPIC_ID, topic_id);
                                        documentReference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                toastMes(e.getMessage().toString());
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("UploadImageError", e.getMessage());
                        }
                    });
        }
    }

    private void sendMessageWithoutImage(String content, String topic_id, String discussion_id) {
        DocumentReference documentReference = db.collection("Discussions").document(discussion_id).collection("Topics").document(topic_id).collection("Messages").document();
        Map map = new HashMap();
        map.put(KEY_MESSAGE_ID, documentReference.getId());
        map.put(KEY_MESSAGE_SENDER_ID, mAuth.getCurrentUser().getUid());
        map.put(KEY_MESSAGE_CONTENT, content);
        map.put(KEY_MESSAGE_UPLOAD, FieldValue.serverTimestamp());
        map.put(KEY_MESSAGE_STATE, true);
        map.put(KEY_TOPIC_ID, topic_id);
        documentReference.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){

                }
            }
        });
    }
    public String generateRandomString(int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            result.append(CHARACTERS.charAt(index));
        }
        return result.toString();
    }
    private void showSearch() {
        searchBox.setVisibility(View.VISIBLE);
        Animation slideDown = AnimationUtils.loadAnimation(DetailQuestionActivity.this, R.anim.slide_down);
        searchBox.startAnimation(slideDown);
        editTextSearch.requestFocus();
        editTextSearch.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                messageAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                messageAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }
    private void hideSearch() {
        Animation slideUp = AnimationUtils.loadAnimation(DetailQuestionActivity.this, R.anim.slide_up);
        searchBox.startAnimation(slideUp);

        slideUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                searchBox.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void toastMes(String mes) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(DetailQuestionActivity.this, mes, Toast.LENGTH_SHORT);
        toast.show();
    }
    private void loadMessages() {
//        lastLoadDate = new Date();
        txtSubject.setText(discussion_title);
        db.collection("Discussions")
                .document(discussion_id)
                .collection("Topics")
                .document(topic_id)
                .collection("Messages")
                .whereEqualTo("message_state", true)
                .orderBy("message_upload", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        if (value != null && !value.isEmpty()) {
                            textViewEmpty.setVisibility(View.GONE);

                            for (DocumentChange dc : value.getDocumentChanges()) {
                                MessageClass topic = dc.getDocument().toObject(MessageClass.class);
                                if (topic.getMessage_upload()!= null) {
                                    Date timestamp = topic.getMessage_upload();
                                    String dateStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(timestamp);

                                    if (!dateHeaders.contains(dateStr)) {
                                        dateHeaders.add(dateStr);
                                    }
                                    if (!messages.contains(topic)) {
                                        insertMessageInOrder(topic);
                                        messageAdapter.notifyItemRangeChanged(0, messages.size());
                                        recyclerViewMessages.smoothScrollToPosition(messages.size() + dateHeaders.size());
                                    }
                                }

                                switch (dc.getType()) {
                                    case ADDED:

                                        break;

                                    case REMOVED:
                                        MessageClass removedTopic = dc.getDocument().toObject(MessageClass.class);
                                        Date timestamp1 = removedTopic.getMessage_upload();
                                        String dateStr1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(timestamp1);
                                        messages.remove(removedTopic);

                                        boolean stillHasTopicsForDate = false;
                                        for (MessageClass m : messages) {
                                            String topicDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(m.getMessage_upload());
                                            if (topicDate.equals(dateStr1)) {
                                                stillHasTopicsForDate = true;
                                                break;
                                            }
                                        }
                                        if (!stillHasTopicsForDate) {
                                            dateHeaders.remove(dateStr1);
                                        }

                                        messageAdapter.notifyDataSetChanged();
                                        break;

                                    case MODIFIED:
                                        break;
                                }
                            }
                        } else {
                            textViewEmpty.setVisibility(View.VISIBLE);
                            fabScrollToBottom.hide();
                            messages.clear();
                            dateHeaders.clear();
                            messageAdapter.notifyDataSetChanged();
                        }

                        messageAdapter.updateData(dateHeaders, messages);
                    }
                });

    }
    private void insertMessageInOrder(MessageClass newTopic) {
        int index = 0;
        while (index < messages.size() &&  messages.get(index).getMessage_upload().before(newTopic.getMessage_upload())) {
            index++;
        }
        messages.add(index, newTopic);
    }
//    private void loadMoreMessages(Handler handler) {
//        if (lastLoadDate == null) return;
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                db.collection("Discussions")
//                        .document(discussion_id)
//                        .collection("Topics")
//                        .document(topic_id)
//                        .collection("Messages")
//                        .whereEqualTo("message_state", true)
//                        .whereLessThan("message_upload", lastLoadDate)
//                        .orderBy("message_upload", Query.Direction.ASCENDING)
//                        .limit(100)
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//
//                                if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
//                                    boolean newDateHeader = false;
//                                    ArrayList<MessageClass> newTopics = new ArrayList<>();
//
//                                    for (DocumentChange dc : task.getResult().getDocumentChanges()) {
//                                        MessageClass message = dc.getDocument().toObject(MessageClass.class);
//                                        Date timestamp = message.getMessage_upload();
//                                        String dateStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(timestamp);
//
//                                        if (!dateHeaders.contains(dateStr)) {
//                                            dateHeaders.add(0, dateStr);
//                                            newDateHeader = true;
//                                        }
//
//                                        if (!messages.contains(message)) {
//                                            newTopics.add(0, message);
//                                            messageAdapter.notifyItemRangeChanged(0, messages.size());
//                                            recyclerViewMessages.smoothScrollToPosition(0);
//                                        }
//                                        lastLoadDate = timestamp;
//                                    }
//
//                                    if (!newTopics.isEmpty()) {
//                                        messages.addAll(0, newTopics);
//                                        messageAdapter.updateData(dateHeaders, messages);
////                                        recyclerViewTopics.smoothScrollToPosition(0);
//                                    }
//
//                                }
//                            }
//                        });
//            }
//        }, 500);
//    }

    private Date getDateNDaysAgo(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -n);
        return calendar.getTime();
    }
}
