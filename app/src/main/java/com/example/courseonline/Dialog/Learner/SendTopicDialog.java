package com.example.courseonline.Dialog.Learner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Learner.AttachImageAdapter;
import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.R;
import com.example.courseonline.util.FCMService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendTopicDialog extends Dialog implements AttachImageAdapter.OnImageRemoveListener {
    private static final int PICK_IMAGES = 1;
    Toast toast;
    ImageView closeImage;
    AppCompatButton sendButton, selectImageBtn;
    TextInputLayout layoutSubject, layoutMessage;
    TextInputEditText editSubject, editMessage;
    RecyclerView recyclerAttachImage;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private AttachImageAdapter adapter;
    private String disID;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private LoadingAlert loading;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();
    private final static String KEY_TOPIC_ID = "topic_id";
    private final static String KEY_TOPIC_SENDER_ID = "topic_sender_id";
    private final static String KEY_TOPIC_SUBJECT = "topic_subject";
    private final static String KEY_TOPIC_CONTENT = "topic_content";
    private final static String KEY_TOPIC_ATTACH = "topic_attach";
    private final static String KEY_TOPIC_VIEWER = "topic_viewer";
    private final static String KEY_TOPIC_LIKE = "topic_like";
    private final static String KEY_TOPIC_UPLOAD = "topic_upload";
    private final static String KEY_TOPIC_STATE = "topic_state";
    private final static String KEY_TOPIC_SEEN = "topic_seen";
    private final static String KEY_DISCUSSION_ID = "discussion_id";

    public SendTopicDialog(@NonNull Context context, String disId) {
        super(context);
        setOwnerActivity((Activity) context);
        this.disID = disId;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_send_question);

        mapping();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
                dismiss();
            }
        });

        editSubject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editSubject.length() != 0) {
                    layoutSubject.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editMessage.length() != 0) {
                    layoutMessage.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editSubject.length() == 0) {
                    layoutSubject.setError("Vui lòng nhập nội dung");
                } else if (editMessage.length() == 0) {
                    layoutMessage.setError("Vui lòng nhập nội dung");
                } else {
                    sendTopic();
                }
            }
        });

        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker(getOwnerActivity());
                if(selectedImageUris.size() < 4){}
              else{
                    toastMes("Chỉ chọn tối đa 3 ảnh");
                }
            }
        });

        adapter = new AttachImageAdapter(selectedImageUris, new AttachImageAdapter.OnImageRemoveListener() {
            @Override
            public void onImageRemove(int position) {
                SendTopicDialog.this.onImageRemove(position);
            }
        });

        recyclerAttachImage.setLayoutManager(new LinearLayoutManager(getOwnerActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerAttachImage.setAdapter(adapter);


    }

    private void init() {
        editMessage.setText("");
        editMessage.clearFocus();
        editSubject.setText("");
        editSubject.clearFocus();
        selectedImageUris.clear();
        adapter.notifyDataSetChanged();
    }

    private void mapping() {
        closeImage = findViewById(R.id.closeIcon);
        sendButton = findViewById(R.id.sendButton);
        editSubject = findViewById(R.id.editTextSubject);
        editMessage = findViewById(R.id.editTextMessage);
        layoutMessage = findViewById(R.id.messageTextLayout);
        layoutSubject = findViewById(R.id.subjectTextLayout);
        selectImageBtn = findViewById(R.id.buttonAttachImage);
        recyclerAttachImage = findViewById(R.id.recyclerImages);
    }

    public void openImagePicker(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activity.startActivityForResult(intent, PICK_IMAGES);
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
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGES && resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                if (count > 4) {
                    Toast.makeText(getContext(), "Bạn chỉ được chọn tối đa 4 ảnh.", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    addImageUri(imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                addImageUri(imageUri);
            }
        }
    }

    public static String generateRandomString(int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            result.append(CHARACTERS.charAt(index));
        }
        return result.toString();
    }

    private void sendTopic() {
        if (selectedImageUris.size() != 0) {
            uploadDataWithAttach(selectedImageUris, mAuth.getCurrentUser().getUid(), editSubject.getText().toString(), editMessage.getText().toString());
        } else {
            uploadDataNotAttach(mAuth.getCurrentUser().getUid(), editSubject.getText().toString(), editMessage.getText().toString());
        }
    }

    public void sendTopicAndNotify(String topicContent, String discussion_id, String firstImageUrl) {
        db.collection("Discussions")
                .document(discussion_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String discussionTitle = documentSnapshot.getString("discussion_title");

                            // Truy vấn người tham gia
                            db.collection("Discussions")
                                    .document(discussion_id)
                                    .collection("DiscussionParticipants")
                                    .whereEqualTo("allowNotification", true)
                                    .whereEqualTo("userState", "out-topics")
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                                    String fcmToken = document.getString("userMessagingToken");

                                                    if (fcmToken != null && !fcmToken.isEmpty()) {

                                                        if (firstImageUrl != null && !firstImageUrl.isEmpty()) {
                                                            FCMService.sendNotification(getContext(),fcmToken,discussionTitle, topicContent, firstImageUrl);
                                                        } else {
                                                            FCMService.sendNotification(getContext(),fcmToken,discussionTitle, topicContent, null);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("SendTopic", "Error getting participants: ", e);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("SendTopic", "Error getting discussion title: ", e);
                    }
                });

    }


    private void uploadDataNotAttach(String senderId, String subject, String content) {
        loading = new LoadingAlert(getOwnerActivity());
        loading.startLoading();
        DocumentReference documentReference = db.collection("Discussions").document(this.disID).collection("Topics").document();
        Map<String, Object> questionData = new HashMap<>();

        questionData.put(KEY_TOPIC_ID, documentReference.getId());
        questionData.put(KEY_DISCUSSION_ID, disID);
        questionData.put(KEY_TOPIC_SENDER_ID, senderId);
        questionData.put(KEY_TOPIC_SUBJECT, subject);
        questionData.put(KEY_TOPIC_CONTENT, content);
        questionData.put(KEY_TOPIC_VIEWER, new ArrayList<>());
        questionData.put(KEY_TOPIC_LIKE, new ArrayList<>());
        questionData.put(KEY_TOPIC_SEEN, new ArrayList<>());
        questionData.put(KEY_TOPIC_STATE, true);
        questionData.put(KEY_TOPIC_UPLOAD, FieldValue.serverTimestamp());

        documentReference.set(questionData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                init();
                toastMes("Gửi thành công");
                sendTopicAndNotify(subject, disID, null);
                loading.closeLoading();
                dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMes(e.getMessage().toString());
            }
        });
    }

    private void uploadDataWithAttach(List<Uri> imageUris,String senderId, String subject, String content) {
        loading = new LoadingAlert(getOwnerActivity());
        loading.startLoading();
        final ArrayList<String> downloadUrls = new ArrayList<>();
        StorageReference storageRef = storage.getReference();

        final int totalImages = imageUris.size();
        for (Uri uri : imageUris) {
            StorageReference fileRef = storageRef.child("Topic/" + "topic_img_" + generateRandomString(12) + System.currentTimeMillis() + ".jpg");

            fileRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUrl) {
                                    downloadUrls.add(downloadUrl.toString());
                                    if (downloadUrls.size() == totalImages) {
                                        DocumentReference documentReference = db.collection("Discussions").document(disID).collection("Topics").document();
                                        Map<String, Object> questionData = new HashMap<>();
                                        questionData.put(KEY_TOPIC_ID, documentReference.getId());
                                        questionData.put(KEY_DISCUSSION_ID, disID);
                                        questionData.put(KEY_TOPIC_SENDER_ID, senderId);
                                        questionData.put(KEY_TOPIC_SUBJECT, subject);
                                        questionData.put(KEY_TOPIC_ATTACH, downloadUrls);
                                        questionData.put(KEY_TOPIC_CONTENT, content);
                                        questionData.put(KEY_TOPIC_VIEWER, new ArrayList<>());
                                        questionData.put(KEY_TOPIC_LIKE, new ArrayList<>());
                                        questionData.put(KEY_TOPIC_SEEN, new ArrayList<>());
                                        questionData.put(KEY_TOPIC_STATE, true);
                                        questionData.put(KEY_TOPIC_UPLOAD, FieldValue.serverTimestamp());
                                        documentReference.set(questionData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                init();
                                                toastMes("Gửi thành công");
                                                sendTopicAndNotify(subject, disID, downloadUrls.get(0).toString());
                                                loading.closeLoading();
                                                dismiss();
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

    private void toastMes(String mes) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getOwnerActivity(), mes, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getWindow() != null) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            getWindow().setAttributes(params);
            getWindow().getAttributes().windowAnimations = vn.zalopay.sdk.R.style.Animation_AppCompat_Dialog;
        }
    }

    @Override
    public void show() {
        super.show();
        View view = getWindow().getDecorView();
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(fadeInAnimation);
    }
}
