package com.example.courseonline.Activity.Teacher;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Teacher.TypeEditAdapter;
import com.example.courseonline.Domain.TypeClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UploadTypeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView btnBack;
    private RecyclerView recyclerCourse;
    private AppCompatButton btnParent, btnChild;
    private Spinner spinnerParent, spinnerChildren;
    private TypeEditAdapter adapter;
    private ArrayAdapter<String> adapter1, adapter2;
    private ArrayList<TypeClass> arrayList = new ArrayList<>();
    private ArrayList<String> arrayList1 = new ArrayList<>();
    private ArrayList<String> arrayTemp1 = new ArrayList<>();
    private ArrayList<String> arrayList2 = new ArrayList<>();
    private ArrayList<String> arrayTemp2 = new ArrayList<>();
    private static final String KEY_TYPE_ID = "type_id";
    private static final String KEY_COURSE_ID = "course_id";
    private static final String KEY_CATEGORY_ID = "category_id";
    private static final String KEY_CATEGORY_CHILD_ID = "category_child_id";
    private Toast toast;
    private String course_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_type);
        course_key = getIntent().getStringExtra("key_id");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mapping();
        recyclerCourse.setLayoutManager(new LinearLayoutManager(UploadTypeActivity.this,LinearLayoutManager.HORIZONTAL,false));
        recyclerCourse.setHasFixedSize(true);
        recyclerCourse.setItemViewCacheSize(20);
        loadData();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Courses").document(course_key).collection("Type").whereNotEqualTo("category_id", "freeCategory").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().size() > 0)
                        {
                            toastMes("Bạn chỉ có thể thêm 1 thể loại cha");
                        }else{
                            DocumentReference ref = db.collection("Courses").document(course_key).collection("Type").document();
                            Map map = new HashMap();
                            map.put(KEY_TYPE_ID, ref.getId());
                            map.put(KEY_COURSE_ID, course_key);
                            map.put(KEY_CATEGORY_ID, arrayTemp1.get(spinnerParent.getSelectedItemPosition()).toString());
                            map.put(KEY_CATEGORY_CHILD_ID, "");
                            ref.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    toastMes("Đã thêm");
                                }
                            });
                        }
                    }
                });
            }
        });
        btnChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!spinnerChildren.getSelectedItem().toString().equals("Trống")) {
                    db.collection("Courses").document(course_key).collection("Type")
                            .whereEqualTo("category_child_id", "")
                            .whereNotEqualTo("category_id", "freeCategory")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isComplete()) {
                                        if (task.getResult().size() != 0) {
                                            if (task.getResult().size() >= 5) {
                                                toastMes("Bạn chỉ có thể thêm tối đa 3 thể loại cho 1 khoá học");
                                            } else {
                                                db.collection("Courses").document(course_key).collection("Type")
                                                        .whereEqualTo("category_child_id", arrayTemp2.get(spinnerChildren.getSelectedItemPosition()).toString())
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isComplete()) {
                                                                    if (task.getResult().size() > 0) {
                                                                        toastMes("Tồn tại thể loại");
                                                                    } else {
                                                                        DocumentReference ref = db.collection("Courses").document(course_key).collection("Type").document();
                                                                        Map map = new HashMap();
                                                                        map.put(KEY_TYPE_ID, ref.getId());
                                                                        map.put(KEY_COURSE_ID, course_key);
                                                                        map.put(KEY_CATEGORY_ID, arrayTemp1.get(spinnerParent.getSelectedItemPosition()).toString());
                                                                        map.put(KEY_CATEGORY_CHILD_ID, arrayTemp2.get(spinnerChildren.getSelectedItemPosition()).toString());
                                                                        ref.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                toastMes("Đã thêm");
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }
                                                        });
                                            }
                                        } else {
                                            toastMes("Vui lòng thêm thể loại cha trước");
                                        }
                                    }
                                }
                            });
                }
            }
        });
        spinnerParent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                db.collection("Categories").document(arrayTemp1.get(i).toString()).collection("CategoriesChild").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null)
                        {
                            return;
                        }

                        if(value.size() != 0)
                        {
                            arrayList2.clear();
                            arrayTemp2.clear();
                            for(QueryDocumentSnapshot doc : value)
                            {
                                arrayList2.add(doc.getString("category_title"));
                                arrayTemp2.add(doc.getString("category_id"));
                            }
                        }else{
                            arrayList2.clear();
                            arrayTemp2.clear();
                            arrayList2.add("Trống");
                        }
                        adapter2.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerParent.setSelection(0);
    }
    private void loadData(){
       adapter = new TypeEditAdapter(arrayList, 2);
       adapter1 = new ArrayAdapter<String>(UploadTypeActivity.this, android.R.layout.simple_spinner_item, arrayList1);
       adapter2 = new ArrayAdapter<String>(UploadTypeActivity.this, android.R.layout.simple_spinner_item, arrayList2);
       adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       spinnerParent.setAdapter(adapter1);
       spinnerChildren.setAdapter(adapter2);
       recyclerCourse.setAdapter(adapter);

       if(mAuth.getCurrentUser() != null)
       {
           db.collection("Courses").document(course_key).collection("Type").whereNotEqualTo("category_id", "freeCategory").addSnapshotListener(new EventListener<QuerySnapshot>() {
               @Override
               public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                   if(error != null)
                   {
                       return;
                   }
                   if(value.size() > 0)
                   {
                       spinnerParent.setEnabled(false);
                   }else{
                       spinnerParent.setEnabled(true);
                   }
               }
           });
           db.collection("Courses").document(course_key).collection("Type").addSnapshotListener(new EventListener<QuerySnapshot>() {
               @Override
               public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                   if(error != null)
                   {
                       return;
                   }
                   if(value.size() != 0)
                   {
                       arrayList.clear();
                       for(QueryDocumentSnapshot doc : value)
                       {
                           arrayList.add(doc.toObject(TypeClass.class));
                       }
                       adapter.notifyDataSetChanged();
                   }
               }
           });
           db.collection("Categories").addSnapshotListener(new EventListener<QuerySnapshot>() {
               @Override
               public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                   if(error != null)
                   {
                       return;
                   }

                   if(value.size() != 0)
                   {
                       arrayList1.clear();
                       arrayTemp1.clear();
                       for(QueryDocumentSnapshot doc : value)
                       {
                           if(!doc.getString("category_id").equals("freeCategory"))
                           {
                               arrayList1.add(doc.getString("category_title"));
                               arrayTemp1.add(doc.getString("category_id"));

                           }

                       }
                   }
                   adapter1.notifyDataSetChanged();
               }
           });

       }
    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(UploadTypeActivity.this, mes,Toast.LENGTH_SHORT);
        toast.show();
    }
    private void mapping(){
        btnBack = (ImageView) findViewById(R.id.btnBackType);
        btnParent = (AppCompatButton) findViewById(R.id.buttonParent);
        btnChild = (AppCompatButton) findViewById(R.id.buttonChild);
        spinnerParent = (Spinner) findViewById(R.id.typeParent);
        spinnerChildren = (Spinner) findViewById(R.id.typeChild);
        recyclerCourse = (RecyclerView) findViewById(R.id.recyclerTypeDetail);
    }
}