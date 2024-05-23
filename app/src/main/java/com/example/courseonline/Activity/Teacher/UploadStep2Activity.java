package com.example.courseonline.Activity.Teacher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Teacher.HeadingUploadAdapter;
import com.example.courseonline.Domain.HeadingClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UploadStep2Activity extends AppCompatActivity {

    ConstraintLayout constraintLayoutDefault;
    RecyclerView recyclerHeading;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView btnBackP2;
    private ArrayList<HeadingClass> arrayHeading = new ArrayList<>();
    private HeadingUploadAdapter adapter;
    private String key;
    private Toast toast;
    private static final String KEY_CID = "course_id";
    private static final String KEY_DESCRIPTION = "heading_description";
    private static final String KEY_ID = "heading_id";
    private static final String KEY_ORDER = "heading_order";
    private static final String KEY_TITLE = "heading_title";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_step2);
        mapping();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        key = getIntent().getStringExtra("key_id");
        adapter = new HeadingUploadAdapter(arrayHeading);
        recyclerHeading.setAdapter(adapter);
        recyclerHeading.setLayoutManager(new LinearLayoutManager(UploadStep2Activity.this,LinearLayoutManager.VERTICAL,false));
        recyclerHeading.setHasFixedSize(true);
        recyclerHeading.setItemViewCacheSize(20);
        loadData();
        btnBackP2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        constraintLayoutDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mAuth.getCurrentUser() != null)
                {

                    DocumentReference reference =  db.collection("Courses").document(key).collection("Heading").document();
                    Map hashMap = new HashMap<>();
                    hashMap.put(KEY_ID, reference.getId());
                    hashMap.put(KEY_CID, key);
                    hashMap.put(KEY_DESCRIPTION, "Description Example");
                    hashMap.put(KEY_TITLE, "Heading Example");
                    hashMap.put(KEY_ORDER, arrayHeading.size());
                    reference.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent intent = new Intent(UploadStep2Activity.this, UploadStep3Activity.class);
                            intent.putExtra("heading_key", reference.getId());
                            intent.putExtra("course_key", key);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toastMes("Có lỗi xảy ra !");
                        }
                    });

                }
            }
        });
    }
    private void loadData(){
        adapter = new HeadingUploadAdapter(arrayHeading);
        recyclerHeading.setAdapter(adapter);
        if(mAuth.getCurrentUser() != null)
        {
            db.collection("Courses").document(key).collection("Heading").orderBy("heading_order", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                        return;
                    }
                    arrayHeading.clear();
                    for(QueryDocumentSnapshot snapshot : value)
                    {
                        if(snapshot.toObject(HeadingClass.class) != null)
                        {
                            arrayHeading.add(snapshot.toObject(HeadingClass.class));
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerHeading);
    }
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.START | ItemTouchHelper.END) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(arrayHeading, fromPosition, toPosition);
            adapter.notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            for(int i = 0 ;i<arrayHeading.size();i++)
            {
                DocumentReference documentReference =  db.collection("Courses").document(key).collection("Heading").document(arrayHeading.get(i).getHeading_id());
                Map map = new HashMap<>();
                map.put(KEY_ORDER, i);
                documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
            }
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //Toast.makeText(UploadStep2Activity.this,String.valueOf(viewHolder.getAdapterPosition()),Toast.LENGTH_SHORT).show();
            DocumentReference reference1 =  db.collection("Courses").document(key).collection("Heading").document(arrayHeading.get(viewHolder.getAdapterPosition()).getHeading_id());
            reference1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    adapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    toastMes("Có lỗi xảy ra !");
                }
            });
        }
    };
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(UploadStep2Activity.this, mes,Toast.LENGTH_SHORT);
        toast.show();
    }
    private void mapping(){
        constraintLayoutDefault = (ConstraintLayout) findViewById(R.id.constraintHeadingDefault);
        recyclerHeading = (RecyclerView) findViewById(R.id.recyclerUploadHeading);
        btnBackP2 = (ImageView) findViewById(R.id.btnBackP2);
    }
}