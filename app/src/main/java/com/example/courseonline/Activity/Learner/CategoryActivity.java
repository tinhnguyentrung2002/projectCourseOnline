package com.example.courseonline.Activity.Learner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Learner.CategoryDisplayAdapter;
import com.example.courseonline.Adapter.Learner.CourseDisplayAdapter;
import com.example.courseonline.Adapter.Learner.CourseDisplayVerticalAdapter;
import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.Domain.CategoryDisplayClass;
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

public class CategoryActivity extends AppCompatActivity {
    private TextView  txtPopular1, txtFree3, txtRate2, txtNone11, txtNone12, txtNone13;
    private FirebaseAuth mAuth;
    private Toast toast;
    private int user_grade;
    private MaterialToolbar toolbar1;
    private ShapeableImageView back;
    private ArrayList<String> array = new ArrayList<String>();
    private FirebaseFirestore db;
    private String type, categoryId;
    private CoordinatorLayout coorType;
    private CategoryDisplayAdapter categoryAdapter;
    private CourseDisplayAdapter adapterRecycleView, adapterRecycleView1;
    private CourseDisplayVerticalAdapter adapterRecycleViewVertical;
    RecyclerView recyclerPopular1, recyclerTopRated2, recyclerFree3, recyclerTypeChild;
    ArrayList<CourseDisplayClass> arrayCourse = new ArrayList<>();
    ArrayList<CourseDisplayClass> arrayCourseRated = new ArrayList<>();
    ArrayList<CourseDisplayClass> arrayCourseFree = new ArrayList<>();
    ArrayList<CategoryDisplayClass> arrayCategory = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mapping();
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
        type = getIntent().getStringExtra("category_title");
        categoryId = getIntent().getStringExtra("category_id");
        toolbar1.setTitle(type);
        LoadingAlert alert = new LoadingAlert(CategoryActivity.this);
        alert.startLoading();
        Handler handler = new Handler();
        coorType.setVisibility(View.GONE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                coorType.setVisibility(View.VISIBLE);
                alert.closeLoading();
            }
        }, 600);
        loadData(user_grade);
        recyclerTypeChild.setLayoutManager(new LinearLayoutManager(CategoryActivity.this,LinearLayoutManager.HORIZONTAL,false));
        recyclerTypeChild.setHasFixedSize(true);
        recyclerTypeChild.setItemViewCacheSize(20);
        recyclerPopular1.setLayoutManager(new LinearLayoutManager(CategoryActivity.this,LinearLayoutManager.HORIZONTAL,false));
        recyclerPopular1.setHasFixedSize(true);
        recyclerPopular1.setItemViewCacheSize(20);
        recyclerTopRated2.setLayoutManager(new LinearLayoutManager(CategoryActivity.this,LinearLayoutManager.VERTICAL,false));
        recyclerTopRated2.setHasFixedSize(true);
        recyclerTopRated2.setItemViewCacheSize(20);
        recyclerFree3.setLayoutManager(new LinearLayoutManager(CategoryActivity.this,LinearLayoutManager.HORIZONTAL,false));
        recyclerFree3.setHasFixedSize(true);
        recyclerFree3.setItemViewCacheSize(20);
        categoryAdapter = new CategoryDisplayAdapter(arrayCategory, false ,this);
        adapterRecycleView = new CourseDisplayAdapter(arrayCourse, this);
        adapterRecycleView1 = new CourseDisplayAdapter(arrayCourseFree, this);
        adapterRecycleViewVertical = new CourseDisplayVerticalAdapter(arrayCourseRated, this);
        recyclerTypeChild.setAdapter(categoryAdapter);
        //categoryAdapter.setHasStableIds(true);
        recyclerPopular1.setAdapter(adapterRecycleView);
        //adapterRecycleView.setHasStableIds(true);
        recyclerTopRated2.setAdapter(adapterRecycleViewVertical);
        //adapterRecycleViewVertical.setHasStableIds(true);
        recyclerFree3.setAdapter(adapterRecycleView);
       //adapterRecycleView.setHasStableIds(true);
        recyclerPopular1.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            private float startX = 0f;
            private final int SCROLL_DIRECTION_RIGHT = 1;
            private final int SCROLL_DIRECTION_LEFT = -1;

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        boolean isScrollingRight = event.getX() < startX;
                        boolean scrollItemsToRight = isScrollingRight && recyclerView.canScrollHorizontally(SCROLL_DIRECTION_RIGHT);
                        boolean scrollItemsToLeft = !isScrollingRight && recyclerView.canScrollHorizontally(SCROLL_DIRECTION_LEFT);
                        boolean disallowIntercept = scrollItemsToRight || scrollItemsToLeft;
                        recyclerView.getParent().requestDisallowInterceptTouchEvent(disallowIntercept);
                        break;
                    case MotionEvent.ACTION_UP:
                        startX = 0f;
                        break;
                    default:
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
        recyclerFree3.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            private float startX = 0f;
            private final int SCROLL_DIRECTION_RIGHT = 1;
            private final int SCROLL_DIRECTION_LEFT = -1;

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        boolean isScrollingRight = event.getX() < startX;
                        boolean scrollItemsToRight = isScrollingRight && recyclerView.canScrollHorizontally(SCROLL_DIRECTION_RIGHT);
                        boolean scrollItemsToLeft = !isScrollingRight && recyclerView.canScrollHorizontally(SCROLL_DIRECTION_LEFT);
                        boolean disallowIntercept = scrollItemsToRight || scrollItemsToLeft;
                        recyclerView.getParent().requestDisallowInterceptTouchEvent(disallowIntercept);
                        break;
                    case MotionEvent.ACTION_UP:
                        startX = 0f;
                        break;
                    default:
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
        txtPopular1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this, See_All_Activity.class);
                intent.putExtra("key", 1);
                intent.putExtra("type", type);
                intent.putExtra("id", categoryId);
                startActivity(intent);
            }
        });
        txtRate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this, See_All_Activity.class);
                intent.putExtra("key", 3);
                intent.putExtra("type", type);
                intent.putExtra("id", categoryId);
                startActivity(intent);
            }
        });
        txtFree3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this, See_All_Activity.class);
                intent.putExtra("key", 4);
                intent.putExtra("type", type);
                intent.putExtra("id", categoryId);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadData(int user_grade){
        if(mAuth.getCurrentUser() != null && categoryId!=null)
        {
            if(!categoryId.isEmpty())
            {
                db.collection("Categories").document(categoryId).collection("CategoriesChild").orderBy("category_order", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null)
                        {
                            return;
                        }
                        arrayCategory.clear();
                        if(value.size() != 0)
                        {
                            for (QueryDocumentSnapshot doc : value) {
                                arrayCategory.add(doc.toObject(CategoryDisplayClass.class));
                            }
                        }


                        categoryAdapter.notifyDataSetChanged();
                    }
                });
            }
            db.collection("Courses").whereEqualTo("course_type","course").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                       
                        return;
                    }
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("course_id") != null) {
                            db.collection("Courses").document(doc.get("course_id").toString()).collection("Type").whereEqualTo("category_id", categoryId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if(error != null)
                                    {
                                        return;
                                    }
                                    for (QueryDocumentSnapshot doc : value) {
                                        if (doc.get("course_id") != null) {
                                            array.add(doc.getString("course_id"));
                                        }
                                    }
                                    if(array.size() != 0)
                                    {
                                        loadDataByGrade(array,user_grade);
                                    }


                                }
                            });
                        }
                    }
            }
        });

        }
    }
    public void loadDataByGrade(ArrayList<String> array,int user_grade){
        if(user_grade == 10)
        {
            db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).whereIn("course_id", array).orderBy("course_grade", Query.Direction.ASCENDING).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                       
                        return;
                    }
                    arrayCourse.clear();
                    if(value.size() !=0)
                    {
                        txtNone11.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot doc : value) {
                            arrayCourse.add(doc.toObject(CourseDisplayClass.class));
                        }
                    }else{
                        txtNone11.setVisibility(View.VISIBLE);
                    }

                    adapterRecycleView.notifyDataSetChanged();

                }
            });
            db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).whereIn("course_id", array).orderBy("course_grade", Query.Direction.ASCENDING).orderBy("course_rate").limit(5).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                       
                        return;
                    }
                    arrayCourseRated.clear();
                    if(value.size() != 0)
                    {
                        txtNone12.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot doc : value) {
                            arrayCourseRated.add(doc.toObject(CourseDisplayClass.class));
                        }
                    }else{
                        txtNone12.setVisibility(View.VISIBLE);
                    }
                    adapterRecycleViewVertical.notifyDataSetChanged();


                }
            });
            db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).whereIn("course_id", array).orderBy("course_grade", Query.Direction.ASCENDING).whereEqualTo("course_price", 0).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                       
                        return;
                    }
                    arrayCourseFree.clear();
                    if(value.size() != 0)
                    {
                        txtNone13.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot doc : value) {
                            arrayCourseFree.add(doc.toObject(CourseDisplayClass.class));

                        }
                    }else {
                        txtNone13.setVisibility(View.VISIBLE);
                    }

                    adapterRecycleView1.notifyDataSetChanged();

                }
            });
        }else if(user_grade == 12){
            db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).whereIn("course_id", array).orderBy("course_grade", Query.Direction.DESCENDING).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                       
                        return;
                    }
                    arrayCourse.clear();
                    if(value.size() != 0)
                    {
                        txtNone11.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot doc : value) {
                            arrayCourse.add(doc.toObject(CourseDisplayClass.class));
                        }
                    }else{
                        txtNone11.setVisibility(View.VISIBLE);
                    }

                    adapterRecycleView.notifyDataSetChanged();

                }
            });
            db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).whereIn("course_id", array).orderBy("course_grade", Query.Direction.DESCENDING).orderBy("course_rate").limit(5).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                       
                        return;
                    }
                    arrayCourseRated.clear();
                    if(value.size() != 0)
                    {
                        txtNone12.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot doc : value) {
                            arrayCourseRated.add(doc.toObject(CourseDisplayClass.class));
                        }
                    }else{
                        txtNone12.setVisibility(View.VISIBLE);
                    }
                    adapterRecycleViewVertical.notifyDataSetChanged();


                }
            });
            db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).whereIn("course_id", array).orderBy("course_grade", Query.Direction.DESCENDING).whereEqualTo("course_price", 0).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                       
                        return;
                    }
                    arrayCourseFree.clear();
                    if(value.size() != 0)
                    {
                        txtNone13.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot doc : value) {
                            arrayCourseFree.add(doc.toObject(CourseDisplayClass.class));

                        }
                    }else {
                        txtNone13.setVisibility(View.VISIBLE);
                    }
                    adapterRecycleView1.notifyDataSetChanged();

                }
            });
        }else{
            db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).whereIn("course_id", array).orderBy("course_grade", Query.Direction.DESCENDING).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                       
                        return;
                    }
                    arrayCourse.clear();
                    if(value.size() !=0)
                    {
                        txtNone11.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot doc : value) {
                            arrayCourse.add(doc.toObject(CourseDisplayClass.class));
                        }
                    }else{
                        txtNone11.setVisibility(View.VISIBLE);
                    }
                    Collections.sort(arrayCourse, new Comparator<CourseDisplayClass>() {
                        @Override
                        public int compare(CourseDisplayClass o1, CourseDisplayClass o2) {
                            if (o1.getCourse_grade() == 11) return -1;
                            if (o2.getCourse_grade() == 11) return 1;
                            return Integer.compare(o1.getCourse_grade(), o2.getCourse_grade());
                        }
                    });
                    adapterRecycleView.notifyDataSetChanged();

                }
            });
            db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).whereIn("course_id", array).orderBy("course_grade", Query.Direction.DESCENDING).orderBy("course_rate").limit(5).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                       
                        return;
                    }
                    arrayCourseRated.clear();
                    if(value.size() != 0)
                    {
                        txtNone12.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot doc : value) {
                            arrayCourseRated.add(doc.toObject(CourseDisplayClass.class));
                        }
                    }else{
                        txtNone12.setVisibility(View.VISIBLE);
                    }
                    Collections.sort(arrayCourseRated, new Comparator<CourseDisplayClass>() {
                        @Override
                        public int compare(CourseDisplayClass o1, CourseDisplayClass o2) {
                            if (o1.getCourse_grade() == 11) return -1;
                            if (o2.getCourse_grade() == 11) return 1;
                            return Integer.compare(o1.getCourse_grade(), o2.getCourse_grade());
                        }
                    });
                    adapterRecycleViewVertical.notifyDataSetChanged();


                }
            });
            db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).whereIn("course_id", array).orderBy("course_grade", Query.Direction.DESCENDING).whereEqualTo("course_price", 0).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                       
                        return;
                    }
                    arrayCourseFree.clear();
                    if(value.size() != 0)
                    {
                        txtNone13.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot doc : value) {
                            arrayCourseFree.add(doc.toObject(CourseDisplayClass.class));

                        }
                    }else {
                        txtNone13.setVisibility(View.VISIBLE);
                    }
                    Collections.sort(arrayCourseFree, new Comparator<CourseDisplayClass>() {
                        @Override
                        public int compare(CourseDisplayClass o1, CourseDisplayClass o2) {
                            if (o1.getCourse_grade() == 11) return -1;
                            if (o2.getCourse_grade() == 11) return 1;
                            return Integer.compare(o1.getCourse_grade(), o2.getCourse_grade());
                        }
                    });
                    adapterRecycleView1.notifyDataSetChanged();

                }
            });
        }
    }

    private void mapping(){
        recyclerPopular1 = (RecyclerView) findViewById(R.id.recyclerPopular1);
        recyclerTopRated2 = (RecyclerView) findViewById(R.id.recyclerTopRated2);
        recyclerFree3 = (RecyclerView) findViewById(R.id.recyclerFree3);
        recyclerTypeChild = (RecyclerView) findViewById(R.id.recyclerTypeChild);

        txtPopular1 = (TextView) findViewById(R.id.txtPopular1);
        txtRate2 = (TextView) findViewById(R.id.txtTopRate2);
        txtFree3 = (TextView) findViewById(R.id.txtFree3);
        txtNone11 = (TextView) findViewById(R.id.txtNone11);
        txtNone12 = (TextView) findViewById(R.id.txtNone12);
        txtNone13 = (TextView) findViewById(R.id.txtNone13);
        toolbar1 = (MaterialToolbar) findViewById(R.id.toolbar1);
        back = (ShapeableImageView) findViewById(R.id.backAction);

        coorType = (CoordinatorLayout) findViewById(R.id.CoorType);
    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(CategoryActivity.this,mes,Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(categoryAdapter != null){
            categoryAdapter.release();
        }
        if(adapterRecycleView != null) adapterRecycleView.release();
        if(adapterRecycleView1 != null) adapterRecycleView1.release();
        if(adapterRecycleViewVertical != null) adapterRecycleViewVertical.release();
    }
}