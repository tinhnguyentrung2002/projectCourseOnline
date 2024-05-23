package com.example.courseonline.Activity.Teacher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class UploadStep1Activity extends AppCompatActivity {

    EditText editTitle, editPrice, editTime;
    TextInputEditText editDescription;
    ImageView imgThumbnail, btnBackP1;
    AppCompatButton btnNext;
    private StorageReference storageReference;
    private String storagePath = "Courses/Thumbnails/";
    private FirebaseAuth mAuth;
    Uri img_uri;
    private FirebaseFirestore db;
    private static final String KEY_COURSE_ID = "course_id";
    private static final String KEY_COURSE_DESCRIPTION = "course_description";
    private static final String KEY_COURSE_THUMBNAIL = "course_img";
    private static final String KEY_COURSE_MEMBER = "course_member";
    private static final String KEY_COURSE_OWNER = "course_owner";
    private static final String KEY_COURSE_OWNER_ID = "course_owner_id";
    private static final String KEY_COURSE_RATE = "course_rate";
    private static final String KEY_COURSE_PRICE = "course_price";
    private static final String KEY_COURSE_TITLE = "course_title";
    private static final String KEY_COURSE_TIME = "course_total_time";
    private static final String KEY_COURSE_UPLOAD = "course_upload";
    private static final String KEY_COURSE_STATE = "course_state";
    private static final String KEY_COURSE_CATEGORY_ID = "category_id";
    private static final String KEY_COURSE_CATEGORY_CHILD_ID = "category_child_id";
    private static final String KEY_TYPE_ID = "type_id";
    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 103;
    private String c_id;
    private boolean check = false;
    private LoadingAlert loadingAlert;
    private String img, type, course_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_step1);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        loadingAlert = new LoadingAlert(UploadStep1Activity.this);
        mapping();
        storageReference = FirebaseStorage.getInstance().getReference();
        type = getIntent().getStringExtra("key_type");
        if(type.equals("edit"))
        {
            course_id = getIntent().getStringExtra("key_id");
            check = true;
            loadData();
        }
        if(check == false)
        {
            imgThumbnail.setImageResource(R.drawable.default_image);
        }

        btnBackP1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingAlert.startLoading();
                if(type.equals("create"))
                {
                    DocumentReference ref = db.collection("Courses").document();
                    c_id = ref.getId();
                    if(editTitle.length() != 0 && editPrice.length()!=0 && editTime.length() !=0 && check)
                    {
                        Map hashMap = new HashMap<>();
                        hashMap.put(KEY_COURSE_ID, ref.getId());

                        if(editDescription.length() != 0)
                        {
                            hashMap.put(KEY_COURSE_DESCRIPTION,editDescription.getText().toString());
                        }else{
                            hashMap.put(KEY_COURSE_DESCRIPTION,"");
                        }
                        hashMap.put(KEY_COURSE_MEMBER,0);

                        hashMap.put(KEY_COURSE_OWNER_ID,mAuth.getCurrentUser().getUid());
                        hashMap.put(KEY_COURSE_RATE,0);
                        hashMap.put(KEY_COURSE_PRICE,Integer.parseInt(editPrice.getText().toString()));
                        hashMap.put(KEY_COURSE_TITLE,editTitle.getText().toString());
                        hashMap.put(KEY_COURSE_TIME,Integer.parseInt(editTime.getText().toString()));
                        hashMap.put(KEY_COURSE_THUMBNAIL,img);
                        hashMap.put(KEY_COURSE_STATE,false);
                        hashMap.put(KEY_COURSE_UPLOAD, FieldValue.serverTimestamp());
                        ref.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                db.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isComplete()){
                                            DocumentReference ref = db.collection("Courses").document(c_id);
                                            Map map = new HashMap<>();
                                            map.put(KEY_COURSE_OWNER,task.getResult().getString("user_name"));
                                            ref.update(map).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {

                                                }
                                            });
                                            if(editPrice.getText().toString().equals("0")){
                                                DocumentReference ref1 = db.collection("Courses").document(c_id).collection("Type").document();
                                                Map map1 = new HashMap<>();
                                                map1.put(KEY_TYPE_ID, ref1.getId());
                                                map1.put(KEY_COURSE_CATEGORY_ID, "freeCategory");
                                                map1.put(KEY_COURSE_CATEGORY_CHILD_ID, "");
                                                map1.put(KEY_COURSE_ID, c_id);
                                                ref1.set(map1).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {

                                                    }
                                                });
                                            }
                                        }
                                        else{
                                            DocumentReference ref = db.collection("Courses").document(c_id);
                                            Map map = new HashMap<>();
                                            map.put(KEY_COURSE_OWNER,"Ẩn danh");
                                            ref.update(map).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {


                                                }
                                            });
                                        }
                                        try {
                                            Thread.sleep(200);
                                            loadingAlert.closeLoading();
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                        Toast.makeText(UploadStep1Activity.this, "Thành công", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });

                            }
                        });

                    }
                    else{
                        loadingAlert.closeLoading();
                        Toast.makeText(UploadStep1Activity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    DocumentReference ref1 = db.collection("Courses").document(course_id);
                    if(editTitle.length() != 0 && editPrice.length()!=0 && editTime.length() != 0 && check)
                    {
                        if(editPrice.getText().toString().equals("0")){
                            db.collection("Courses").document(course_id).collection("Type").whereEqualTo("category_id","freeCategory").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isComplete())
                                    {
                                        if(task.getResult().size() == 0)
                                        {
                                            DocumentReference ref2 = db.collection("Courses").document(course_id).collection("Type").document();
                                            Map map2 = new HashMap<>();
                                            map2.put(KEY_TYPE_ID, ref2.getId());
                                            map2.put(KEY_COURSE_CATEGORY_ID, "freeCategory");
                                            map2.put(KEY_COURSE_CATEGORY_CHILD_ID, "");
                                            map2.put(KEY_COURSE_ID, course_id);
                                            ref2.set(map2).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {

                                                }
                                            });
                                        }else{

                                        }
                                        loadingAlert.closeLoading();
                                    }
                                }
                            });

                        }else{
                            db.collection("Courses").document(course_id).collection("Type").whereEqualTo("category_id","freeCategory").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isComplete())
                                    {
                                        if(task.getResult().size() != 0)
                                        {
                                            for(QueryDocumentSnapshot doc : task.getResult())
                                            {
                                                    db.collection("Courses").document(course_id).collection("Type").document(doc.getString("type_id").toString()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {}
                                                    });
                                            }
                                        }else{}
                                        loadingAlert.closeLoading();
                                    }
                                }
                            });
                        }
                        Map hashMap = new HashMap<>();
                        if(editDescription.length() != 0)
                        {
                            hashMap.put(KEY_COURSE_DESCRIPTION,editDescription.getText().toString());
                        }else{
                            hashMap.put(KEY_COURSE_DESCRIPTION,"");
                        }
                        hashMap.put(KEY_COURSE_PRICE,Integer.parseInt(editPrice.getText().toString()));
                        hashMap.put(KEY_COURSE_TITLE,editTitle.getText().toString());
                        hashMap.put(KEY_COURSE_TIME,Integer.parseInt(editTime.getText().toString()));
                        hashMap.put(KEY_COURSE_THUMBNAIL, img);

                        ref1.update(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                try {
                                    Thread.sleep(200);

                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                Toast.makeText(UploadStep1Activity.this, "Thành công", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        });

                    }
                    else{
                        Toast.makeText(UploadStep1Activity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        imgThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

    }
    private void loadData(){
        db.collection("Courses").document(course_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    return;
                }
                editTitle.setText(value.getString("course_title"));
                editDescription.setText(value.getString("course_description"));
                editPrice.setText(String.format("%.0f",value.getDouble("course_price")));
                editTime.setText(String.format("%.0f",value.getDouble("course_total_time")));
                img = value.getString("course_img");
                Glide.with(getApplicationContext()).load(img).centerInside().into(imgThumbnail);
                check = true;

            }
        });
    }
    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        startActivityForResult(gallery, IMAGE_PICK_GALLERY_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE) {
                img_uri = data.getData();
                uploadAvatar(img_uri);
            }
        }
    }
    private void uploadAvatar(Uri imgUri) {
        LoadingAlert loadingAlert = new LoadingAlert(UploadStep1Activity.this);
        loadingAlert.startLoading();
        String fileName = storagePath +" thumbnail_" + mAuth.getCurrentUser().getUid();
        StorageReference storageReference1 = storageReference.child(fileName);
        storageReference1.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri download = uriTask.getResult();
                if(uriTask.isSuccessful()){
                    loadingAlert.closeLoading();
                    Map<String, Object> results = new HashMap<>();
                    results.put(KEY_COURSE_THUMBNAIL, download.toString());
                    img = download.toString();
                    //Toast.makeText(UploadStep1Activity.this, String.valueOf(c_id),Toast.LENGTH_SHORT).show();
                            try {

                                check = true;
                                Glide.with(getApplicationContext()).load(download.toString()).centerInside().into(imgThumbnail);
                            }catch (Exception e){
                                check = true;
                                Glide.with(getApplicationContext()).load(R.drawable.default_image).centerInside().into(imgThumbnail);
                            }

                }else{
                    loadingAlert.closeLoading();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingAlert.closeLoading();
            }
        });
    }
    private void delete(){
        db.collection("Courses").document(c_id).delete();
    }
    private void mapping(){
        btnBackP1 = (ImageView) findViewById(R.id.btnBackP1);
        editTitle = (EditText) findViewById(R.id.editTitle);
        editDescription = (TextInputEditText) findViewById(R.id.editTextContent);
        editPrice = (EditText) findViewById(R.id.editPrice);
        imgThumbnail = (ImageView) findViewById(R.id.imgThumbnail);
        btnNext = (AppCompatButton) findViewById(R.id.buttonToStep2);
        editTime = (EditText) findViewById(R.id.editTextTime);
    }
}