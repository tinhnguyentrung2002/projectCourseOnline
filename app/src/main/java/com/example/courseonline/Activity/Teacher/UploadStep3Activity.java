package com.example.courseonline.Activity.Teacher;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Teacher.UploadDocumentAdapter;
import com.example.courseonline.Adapter.Teacher.UploadVideoAdapter;
import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.Domain.DocumentClass;
import com.example.courseonline.Domain.VideoClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadStep3Activity extends AppCompatActivity {
    private final static String expression = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
    EditText editTitle, editDes, editUrl;
    private TextView txtNone8, txtNone9;
    private RecyclerView listUrl, listDocument;
    AppCompatButton btnURL, btnDocument, btnFinish;
    private String courseKey, headingKey;
    private ImageView btnBackP3;
    private FirebaseAuth mAuth;
    private String storagePath = "Courses/Documents/";
    private Toast toast;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    ArrayList<VideoClass>  arrayList1 = new ArrayList<>();
    ArrayList<DocumentClass>  arrayList2 = new ArrayList<>();
    UploadVideoAdapter adapter1;
    UploadDocumentAdapter adapter2;
    private static final String KEY_VIDEO_ID = "video_id";
    private static final String KEY_VIDEO_URL = "video_url";
    private static final String KEY_VIDEO_TITLE = "video_title";
    private static final String KEY_DOCUMENT_TITLE= "document_title";
    private static final String KEY_DOCUMENT_URL= "document_url";
    private static final String KEY_DOCUMENT_ID= "document_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_step3);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        courseKey = getIntent().getStringExtra("course_key");
        headingKey = getIntent().getStringExtra("heading_key");

        mapping();
        listUrl.setLayoutManager(new LinearLayoutManager(UploadStep3Activity.this,LinearLayoutManager.VERTICAL,false));
        listUrl.setHasFixedSize(true);
        listUrl.setItemViewCacheSize(20);

        listDocument.setLayoutManager(new LinearLayoutManager(UploadStep3Activity.this,LinearLayoutManager.VERTICAL,false));
        listDocument.setHasFixedSize(true);
        listDocument.setItemViewCacheSize(20);

        adapter1 = new UploadVideoAdapter(arrayList1, courseKey, headingKey);
        adapter2 = new UploadDocumentAdapter(arrayList2, courseKey, headingKey);

        listUrl.setAdapter(adapter1);
        listDocument.setAdapter(adapter2);

        load();

        btnBackP3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTitle.getText().length() != 0 && editDes.getText().length() != 0)
                {
                    DocumentReference documentReference = db.collection("Courses").document(courseKey).collection("Heading").document(headingKey);
                    Map map = new HashMap();
                    map.put("heading_title", editTitle.getText().toString());
                    map.put("heading_description", editDes.getText().toString());
                    documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                        }
                    });
                }else{
                    toastMes("Vui lòng điền tên chương và mô tả chương");
                }
            }
        });

        btnURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editUrl.length() != 0)
                {
                    DocumentReference reference =  db.collection("Courses").document(courseKey).collection("Heading").document(headingKey).collection("video").document();
                    Map map = new HashMap<>();
                    map.put(KEY_VIDEO_ID,reference.getId());
                    int tmp = arrayList1.size() + 1;
                    map.put(KEY_VIDEO_TITLE,"Video " + tmp);
                    if(getVideoId(editUrl.getText().toString())!= null)
                    {
                        map.put(KEY_VIDEO_URL,getVideoId(editUrl.getText().toString()));
                    }else{
                        map.put(KEY_VIDEO_URL,"");
                    }
                    reference.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            enterTitleVideo(reference.getId());
                            editUrl.setText("");
                            load();
                        }
                    });
                }
                adapter1.notifyDataSetChanged();
            }
        });

        btnDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePDF();
                adapter2.notifyDataSetChanged();
            }
        });

    }
    private void load(){

        if(mAuth.getCurrentUser() != null){
            db.collection("Courses").document(courseKey).collection("Heading").document(headingKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        editTitle.setText(task.getResult().getString("heading_title"));
                        editDes.setText(task.getResult().getString("heading_description"));
                    }

                }
            });
            db.collection("Courses").document(courseKey).collection("Heading").document(headingKey).collection("video").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {

                        return;
                    }
                    arrayList1.clear();
                    if(value.size() != 0)
                    {
                        txtNone8.setVisibility(View.GONE);

                        for(QueryDocumentSnapshot snapshot : value)
                        {
                            arrayList1.add(snapshot.toObject(VideoClass.class));
                        }

                    }else{
                        txtNone8.setVisibility(View.VISIBLE);
                    }
                    adapter1.notifyDataSetChanged();
                }
            });

            db.collection("Courses").document(courseKey).collection("Heading").document(headingKey).collection("document").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                        return;
                    }
                    arrayList2.clear();
                    if(value.size() != 0)
                    {
                        txtNone9.setVisibility(View.GONE);

                        for(QueryDocumentSnapshot snapshot : value)
                        {
                            arrayList2.add(snapshot.toObject(DocumentClass.class));
                        }

                    }else{
                        txtNone9.setVisibility(View.VISIBLE);
                    }
                    adapter2.notifyDataSetChanged();
                }
            });

        }
    }
    private void choosePDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Pdf files"),101);
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri uri =data.getData();
            String uriString  = uri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            String displayName = null;
            if (uriString.startsWith("content://")){
                Cursor cursor = null;
                try {
                    cursor = this.getContentResolver().query(uri,null,null,null,null);
                    if (cursor != null && cursor.moveToFirst()){
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            } else  if (uriString.startsWith("file://")){
                displayName = myFile.getName();
            }
            upload(data.getData());
        }

    }
    private void upload(Uri data){
        LoadingAlert loadingAlert = new LoadingAlert(UploadStep3Activity.this);
        loadingAlert.startLoading();
        String fileName = storagePath +" PDF_" + headingKey + String.valueOf(arrayList2.size());
        StorageReference storageReference1 = storageReference.child(fileName);
        storageReference1.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri download = uriTask.getResult();
                if(uriTask.isSuccessful()){
                    DocumentReference documentReference =  db.collection("Courses").document(courseKey).collection("Heading").document(headingKey).collection("document").document();
                    Map<String, Object> results = new HashMap<>();
                    results.put(KEY_DOCUMENT_ID, documentReference.getId());
                    int tmp = arrayList2.size() + 1;
                    results.put(KEY_DOCUMENT_TITLE, "Document " + tmp);
                    results.put(KEY_DOCUMENT_URL, download.toString());
                    documentReference.set(results).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            load();
                            loadingAlert.closeLoading();
                            enterTitleDocument(documentReference.getId());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingAlert.closeLoading();
                            toastMes("Thất bại!");
                        }
                    });
                }else{
                    loadingAlert.closeLoading();
                    toastMes("Có lỗi xảy ra");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingAlert.closeLoading();
                toastMes(e.getMessage());
            }
        });

    }
    public static String getVideoId(String videoUrl) {
        if (videoUrl == null || videoUrl.trim().length() <= 0){
            return null;
        }
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(videoUrl);
        try {
            if (matcher.find())
                return matcher.group();
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(UploadStep3Activity.this, mes,Toast.LENGTH_SHORT);
        toast.show();
    }
    private void enterTitleVideo(String id){
            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(UploadStep3Activity.this);
            builder.setTitle("Nhập tiêu đề");
            LinearLayout layout = new LinearLayout(UploadStep3Activity.this);
            EditText editText = new EditText(UploadStep3Activity.this);
            editText.setHint("Nhập tiêu đề");
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            editText.setBackgroundResource(R.drawable.edit_login);
            editText.setMinEms(16);
            editText.setPadding(25,20,20,20);
            layout.addView(editText);
            layout.setPadding(15,15,15,15);
            builder.setView(layout);
            builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(editText.getText().length() != 0)
                    {
                        DocumentReference reference =  db.collection("Courses").document(courseKey).collection("Heading").document(headingKey).collection("video").document(id);
                        Map map = new HashMap<>();
                        map.put(KEY_VIDEO_TITLE,editText.getText().toString());
                        reference.update(map).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                toastMes("Thành công");
                                load();
                            }
                        });
                    }else{
                        DocumentReference reference =  db.collection("Courses").document(courseKey).collection("Heading").document(headingKey).collection("document").document(id);
                        Map map = new HashMap<>();
                        map.put(KEY_DOCUMENT_TITLE,"Video Title 1");
                        reference.update(map).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                toastMes("Thành công");
                                load();
                            }
                        });
                    }


                }
            });
            builder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();


                }
            });
            builder.create().show();
    }
    private void enterTitleDocument(String id){
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(UploadStep3Activity.this);
        builder.setTitle("Nhập tiêu đề");
        LinearLayout layout = new LinearLayout(UploadStep3Activity.this);
        EditText editText = new EditText(UploadStep3Activity.this);
        editText.setHint("Nhập tiêu đề");
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        editText.setBackgroundResource(R.drawable.edit_login);
        editText.setMinEms(16);
        editText.setPadding(25,20,20,20);
        layout.addView(editText);
        layout.setPadding(15,15,15,15);
        builder.setView(layout);
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (editText.getText().length() != 0)
                {
                    DocumentReference reference =  db.collection("Courses").document(courseKey).collection("Heading").document(headingKey).collection("document").document(id);
                    Map map = new HashMap<>();
                    map.put(KEY_DOCUMENT_TITLE,editText.getText().toString());
                    reference.update(map).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            toastMes("Thành công");
                            load();
                        }
                    });
                }
                else{
                    DocumentReference reference =  db.collection("Courses").document(courseKey).collection("Heading").document(headingKey).collection("document").document(id);
                    Map map = new HashMap<>();
                    map.put(KEY_DOCUMENT_TITLE,"Document Title 1");
                    reference.update(map).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            toastMes("Thành công");
                            load();
                        }
                    });
                }
            }
        });
        builder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(editText.getText().length() == 0)
                {
                    toastMes("Vui lòng nhập tên tài liệu !!");
                }
                else{
                    dialogInterface.dismiss();
                }
            }
        });
        builder.create().show();
    }
    private void mapping(){
        txtNone8 = (TextView) findViewById(R.id.txtNone8);
        txtNone9 = (TextView) findViewById(R.id.txtNone9);

        editTitle = (EditText) findViewById(R.id.editTextTitle);
        editDes = (EditText) findViewById(R.id.editTextDescription);
        editUrl = (EditText) findViewById(R.id.editTextURL);

        btnURL = (AppCompatButton) findViewById(R.id.buttonLoadUrl);
        btnDocument = (AppCompatButton) findViewById(R.id.buttonDocument);
        btnFinish = (AppCompatButton) findViewById(R.id.buttonFinish);
        btnBackP3 = (ImageView) findViewById(R.id.btnBackP3);

        listUrl = (RecyclerView) findViewById(R.id.listUrl);
        listDocument = (RecyclerView) findViewById(R.id.listDocument);
    }
}