package com.example.courseonline.Activity.Learner;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Learner.CourseDisplayVerticalAdapter;
import com.example.courseonline.Domain.CourseDisplayClass;
import com.example.courseonline.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CategoryChildActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerItems;
    private MaterialToolbar toolbar;
    private ShapeableImageView back;
    private Toast toast;
    private String categoryId, category_title;
    private int user_grade;
    private TextView txtNone14;
    private ArrayList<String> array = new ArrayList<String>();
    ArrayList<CourseDisplayClass> arrayCourse = new ArrayList<>();
    CourseDisplayVerticalAdapter courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_child);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    return;
                }
                if(value !=null)
                {
                    if(value.getString("user_grade").compareTo("Lớp 10") == 0) user_grade = 10;
                    else if(value.getString("user_grade").compareTo("Lớp 11") == 0) user_grade = 11;
                    else user_grade = 12;
                }
            }
        });
        mapping();
        recyclerItems.setLayoutManager(new LinearLayoutManager(CategoryChildActivity.this,LinearLayoutManager.VERTICAL,false));
        recyclerItems.setClipToPadding(false);
        //courseAdapter.setHasStableIds(true);
        recyclerItems.setPadding(10,0,0,0);
        categoryId = getIntent().getStringExtra("category_child_id");
        category_title = getIntent().getStringExtra("category_title");
        toolbar.setTitle(category_title);
        loadData();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void loadData(){
        courseAdapter = new CourseDisplayVerticalAdapter(arrayCourse, this);
        recyclerItems.setAdapter(courseAdapter);
        if(mAuth.getCurrentUser() != null && categoryId!=null)
        {
            if(!categoryId.isEmpty()) {
                db.collection("Courses").whereEqualTo("course_type","course").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                           
                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("course_id") != null) {
                                db.collection("Courses").document(doc.get("course_id").toString()).collection("Type").whereEqualTo("category_child_id", categoryId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (error != null) {
                                           
                                            return;
                                        }
                                        for (QueryDocumentSnapshot doc : value) {
                                            if (doc.get("course_id") != null) {
                                                array.add(doc.getString("course_id"));
                                            }
                                        }
                                        if (array.size() != 0) {
                                            loadDataByGrade(array, user_grade);
//                                            db.collection("Courses").whereEqualTo("course_state", true).whereIn("course_id", array).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                                @Override
//                                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                                                    if (error != null) {
//                                                        return;
//                                                    }
//                                                    arrayCourse.clear();
//                                                    for (QueryDocumentSnapshot doc : value) {
//                                                        if (doc.toObject(CourseDisplayClass.class) != null) {
//                                                            arrayCourse.add(doc.toObject(CourseDisplayClass.class));
//                                                        }
//                                                    }
//                                                    courseAdapter.notifyDataSetChanged();
//                                                }
//                                            });
                                        }
                                    }
                                });
                            }
                        }
                    }
                });

            }
        }
    }
    public void loadDataByGrade(ArrayList<String> array, int user_grade){
        if(user_grade == 10){
            db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).whereIn("course_id", array).orderBy("course_grade", Query.Direction.ASCENDING).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                       
                        return;
                    }
                    arrayCourse.clear();
                    if(value.size() != 0)
                    {
                        txtNone14.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot doc : value) {
                            arrayCourse.add(doc.toObject(CourseDisplayClass.class));

                        }
                    }else{
                        txtNone14.setVisibility(View.VISIBLE);
                    }
                    courseAdapter.notifyDataSetChanged();
                }
            });
        }else if(user_grade == 12){
            db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).whereIn("course_id", array).orderBy("course_grade", Query.Direction.DESCENDING).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {   
                        return;
                    }
                    arrayCourse.clear();
                    if(value.size() != 0)
                    {
                        txtNone14.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot doc : value) {
                            arrayCourse.add(doc.toObject(CourseDisplayClass.class));

                        }
                    }else{
                        txtNone14.setVisibility(View.VISIBLE);
                    }
                    courseAdapter.notifyDataSetChanged();
                }
            });
        }else{
            db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).whereIn("course_id", array).orderBy("course_grade", Query.Direction.DESCENDING).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {   
                        return;
                    }
                    arrayCourse.clear();
                    if(value.size() != 0)
                    {
                        txtNone14.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot doc : value) {
                            arrayCourse.add(doc.toObject(CourseDisplayClass.class));

                        }
                    }else{
                        txtNone14.setVisibility(View.VISIBLE);
                    }
                    Collections.sort(arrayCourse, new Comparator<CourseDisplayClass>() {
                        @Override
                        public int compare(CourseDisplayClass o1, CourseDisplayClass o2) {
                            if (o1.getCourse_grade() == 11) return -1;
                            if (o2.getCourse_grade() == 11) return 1;
                            return Integer.compare(o1.getCourse_grade(), o2.getCourse_grade());
                        }
                    });
                    courseAdapter.notifyDataSetChanged();
                }
            });
        }
    }
    private void mapping(){
        recyclerItems = (RecyclerView) findViewById(R.id.recyclerItemChild);
        toolbar = (MaterialToolbar) findViewById(R.id.toolbar2);
        back = (ShapeableImageView) findViewById(R.id.backAction1);
        txtNone14 = (TextView) findViewById(R.id.txtNone14);

    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(CategoryChildActivity.this,mes,Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(courseAdapter != null) courseAdapter.release();
    }
}