package com.example.courseonline.Activity.Learner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Learner.CourseDisplayVerticalAdapter;
import com.example.courseonline.Domain.CourseDisplayClass;
import com.example.courseonline.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class See_All_Activity extends AppCompatActivity{

    private RecyclerView recyclerList;
    private TextView txtToolbar, txtNone15;
    private GridView gridCategory;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList, array, arrayListId;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private AppCompatImageButton btnBack;
    private CourseDisplayVerticalAdapter adapterRecycleViewVertical;
    ArrayList<CourseDisplayClass> arrayCourse = new ArrayList<>();
    private int load;
    private String type, id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all);
        mapping();
        gridCategory.setVisibility(View.GONE);
        recyclerList.setVisibility(View.GONE);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        arrayList = new ArrayList<String>();
        arrayListId = new ArrayList<String>();
        array = new ArrayList<String>();
        load = getIntent().getIntExtra("key", 0);
        type = getIntent().getStringExtra("type");
        id = getIntent().getStringExtra("id");
        switch (load){
            case 0:
                gridCategory.setVisibility(View.VISIBLE);
                arrayList = getIntent().getStringArrayListExtra("category_title");
                arrayListId =getIntent().getStringArrayListExtra("category_id");
                if(arrayList.size() !=0 &&   arrayListId.size() != 0)
                {
                    txtNone15.setVisibility(View.GONE);
                    arrayAdapter = new ArrayAdapter<String>(See_All_Activity.this, android.R.layout.simple_list_item_1, arrayList);
                    gridCategory.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                    gridCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(See_All_Activity.this, CategoryActivity.class);
                            intent.putExtra("category_title", arrayList.get(i).toString());
                            intent.putExtra("category_id", arrayListId.get(i).toString());
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        }
                    });
                }else{
                    txtNone15.setText("Không có kết quả nào");
                    txtNone15.setVisibility(View.VISIBLE);
                }
                txtToolbar.setText("Tất cả thể loại");
                break;
            case 1:
                txtToolbar.setText("Khoá học phổ biến");
                recyclerList.setVisibility(View.VISIBLE);
                recyclerList.setLayoutManager(new LinearLayoutManager(See_All_Activity.this,LinearLayoutManager.VERTICAL,false));
                adapterRecycleViewVertical = new CourseDisplayVerticalAdapter(arrayCourse, this);
                recyclerList.setAdapter(adapterRecycleViewVertical);
                if(mAuth.getCurrentUser() != null && type == null)
                {
                    db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(error != null)
                            {
                           
                                return;
                            }
                            arrayCourse.clear();
                            if(value.size() != 0)
                            {
                                txtNone15.setVisibility(View.GONE);
                                for (QueryDocumentSnapshot doc : value) {
                                    arrayCourse.add(doc.toObject(CourseDisplayClass.class));
                                }
                            }else{
                                txtNone15.setText("không có khóa học nào");
                                txtNone15.setVisibility(View.VISIBLE);
                            }
                            adapterRecycleViewVertical.notifyDataSetChanged();

                        }
                    });

                }else {
                    db.collection("Courses").whereEqualTo("course_type","course").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                           
                                return;
                            }
                            for (QueryDocumentSnapshot doc : value) {
                                if (doc.get("course_id") != null) {
                                    db.collection("Courses").document(doc.get("course_id").toString()).collection("Type").whereEqualTo("category_id", id).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                                            if(array.size() != 0)
                                            {

                                                db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).whereIn("course_id", array).orderBy("course_member").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                        if(error != null)
                                                        {
                                                       
                                                            return;
                                                        }

                                                        arrayCourse.clear();
                                                        if(value.size() != 0)
                                                        {
                                                            txtNone15.setVisibility(View.GONE);
                                                            for (QueryDocumentSnapshot doc : value) {
                                                                arrayCourse.add(doc.toObject(CourseDisplayClass.class));
                                                            }
                                                        }else{
                                                            txtNone15.setText("không có khóa học nào");
                                                            txtNone15.setVisibility(View.VISIBLE);
                                                        }
                                                        adapterRecycleViewVertical.notifyDataSetChanged();
                                                    }
                                                });
                                            }

                                        }
                                    });
                                }
                            }
                        }
                    });

                }
                break;
            case 2:
                txtToolbar.setText("Khóa học mới nhất");
                recyclerList.setVisibility(View.VISIBLE);
                recyclerList.setLayoutManager(new LinearLayoutManager(See_All_Activity.this,LinearLayoutManager.VERTICAL,false));
                adapterRecycleViewVertical = new CourseDisplayVerticalAdapter(arrayCourse, this);
//                adapterRecycleViewVertical.setClickItemListener(this::onClick);
                recyclerList.setAdapter(adapterRecycleViewVertical);
                if(mAuth.getCurrentUser() != null && type == null)
                {

                    db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).orderBy("course_upload", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(error != null)
                            {
                           
                                return;
                            }
                            arrayCourse.clear();
                            if(value.size() != 0)
                            {
                                txtNone15.setVisibility(View.GONE);
                                for (QueryDocumentSnapshot doc : value) {
                                    arrayCourse.add(doc.toObject(CourseDisplayClass.class));
                                }
                            }else{
                                txtNone15.setText("không có khóa học nào");
                                txtNone15.setVisibility(View.VISIBLE);
                            }
                            adapterRecycleViewVertical.notifyDataSetChanged();

                        }
                    });

                }
                break;
            case 3:
                txtToolbar.setText("Top đánh giá");
                recyclerList.setVisibility(View.VISIBLE);
                recyclerList.setLayoutManager(new LinearLayoutManager(See_All_Activity.this,LinearLayoutManager.VERTICAL,false));
                adapterRecycleViewVertical = new CourseDisplayVerticalAdapter(arrayCourse, this);
//                adapterRecycleViewVertical.setClickItemListener(this::onClick);
                recyclerList.setAdapter(adapterRecycleViewVertical);
                if(mAuth.getCurrentUser() != null && type == null)
                {
                    recyclerList.setVisibility(View.VISIBLE);
                    db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).orderBy("course_rate",Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(error != null)
                            {
                           
                                return;
                            }
                            arrayCourse.clear();
                            if(value.size() != 0)
                            {
                                txtNone15.setVisibility(View.GONE);
                                for (QueryDocumentSnapshot doc : value) {
                                    arrayCourse.add(doc.toObject(CourseDisplayClass.class));
                                }
                            }else{
                                txtNone15.setText("không có khóa học nào");
                                txtNone15.setVisibility(View.VISIBLE);
                            }
                            adapterRecycleViewVertical.notifyDataSetChanged();
                        }
                    });
                }else {
                    db.collection("Courses").whereEqualTo("course_type","course").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                           
                                return;
                            }
                            for (QueryDocumentSnapshot doc : value) {
                                if (doc.get("course_id") != null) {
                                    db.collection("Courses").document(doc.get("course_id").toString()).collection("Type").whereEqualTo("category_id", id).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                            if(array.size() != 0)
                                            {
                                                db.collection("Courses").whereEqualTo("course_state", true).whereIn("course_id", array).orderBy("course_rate",Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                        if(error != null)
                                                        {
                                                       
                                                            return;
                                                        }
                                                        arrayCourse.clear();
                                                        if(value.size() != 0)
                                                        {
                                                            txtNone15.setVisibility(View.GONE);
                                                            for (QueryDocumentSnapshot doc : value) {
                                                                arrayCourse.add(doc.toObject(CourseDisplayClass.class));
                                                            }
                                                        }else{
                                                            txtNone15.setText("không có khóa học nào");
                                                            txtNone15.setVisibility(View.VISIBLE);
                                                        }
                                                        adapterRecycleViewVertical.notifyDataSetChanged();
                                                    }
                                                });
                                            }

                                        }
                                    });
                                }
                            }
                        }
                    });

                }
                break;
            case 4:
                txtToolbar.setText("Khoá học miễn phí");
                recyclerList.setVisibility(View.VISIBLE);
                recyclerList.setLayoutManager(new LinearLayoutManager(See_All_Activity.this,LinearLayoutManager.VERTICAL,false));
                adapterRecycleViewVertical = new CourseDisplayVerticalAdapter(arrayCourse, this);
                recyclerList.setAdapter(adapterRecycleViewVertical);
                if(mAuth.getCurrentUser() != null && type == null)
                {
                    recyclerList.setVisibility(View.VISIBLE);
                    db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).whereEqualTo("course_price", 0).orderBy("course_member",Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(error != null)
                            {
                           
                                return;
                            }
                            arrayCourse.clear();
                            if(value.size() != 0)
                            {
                                txtNone15.setVisibility(View.GONE);
                                for (QueryDocumentSnapshot doc : value) {
                                    arrayCourse.add(doc.toObject(CourseDisplayClass.class));
                                }
                            }else{
                                txtNone15.setText("không có khóa học nào");
                                txtNone15.setVisibility(View.VISIBLE);
                            }
                            adapterRecycleViewVertical.notifyDataSetChanged();
                        }
                    });
                }else{
                    db.collection("Courses").whereEqualTo("course_type","course").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                           
                                return;
                            }
                            for (QueryDocumentSnapshot doc : value) {
                                if (doc.get("course_id") != null) {
                                    db.collection("Courses").document(doc.get("course_id").toString()).collection("Type").whereEqualTo("category_id", id).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                            if(array.size()!=0)
                                            {
                                                db.collection("Courses").whereEqualTo("course_state", true).whereIn("course_id", array).whereEqualTo("course_price",0).orderBy("course_member",Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                        if(error != null)
                                                        {
                                                       
                                                            return;
                                                        }
                                                        arrayCourse.clear();
                                                        if(value.size() != 0)
                                                        {
                                                            txtNone15.setVisibility(View.GONE);
                                                            for (QueryDocumentSnapshot doc : value) {
                                                                arrayCourse.add(doc.toObject(CourseDisplayClass.class));
                                                            }
                                                        }else{
                                                            txtNone15.setText("không có khóa học nào");
                                                            txtNone15.setVisibility(View.VISIBLE);
                                                        }
                                                        adapterRecycleViewVertical.notifyDataSetChanged();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });

                }
                break;
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void mapping(){

        recyclerList = (RecyclerView) findViewById(R.id.recyclerList);
        gridCategory = (GridView) findViewById(R.id.gridCategory);
        txtToolbar = (TextView) findViewById(R.id.txtToolbar);
        txtNone15 = (TextView) findViewById(R.id.txtNone15);
        btnBack = (AppCompatImageButton) findViewById(R.id.btnBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adapterRecycleViewVertical!= null) adapterRecycleViewVertical.release();
    }
}