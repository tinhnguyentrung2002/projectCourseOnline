package com.example.courseonline.Fragment.Learner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.courseonline.Activity.Learner.See_All_Activity;
import com.example.courseonline.Adapter.Learner.CategoryDisplayAdapter;
import com.example.courseonline.Adapter.Learner.CourseDisplayAdapter;
import com.example.courseonline.Adapter.Learner.CourseDisplayVerticalAdapter;
import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.Domain.CategoryDisplayClass;
import com.example.courseonline.Domain.CourseDisplayClass;
import com.example.courseonline.R;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment{
    LoadingAlert alert;
    private int user_grade;
    private TextView txtCategories, txtPopular, txtForYou, txtFree, txtRate;
    private Toast toast;
    private SwipeRefreshLayout refresh;
    private ArrayList<String> arrayCategories = new ArrayList<String>();
    private ArrayList<String> arrayCategoriesId = new ArrayList<String>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CourseDisplayAdapter adapterRecycleView, adapterRecycleView1, adapterRecycleView2;
    private CourseDisplayVerticalAdapter adapterRecycleViewVertical;
    private CategoryDisplayAdapter adapterCategory;
    RecyclerView recyclerPopular, recyclerTopRated, recyclerCategories, recyclerSpecial, recyclerFree;
    ArrayList<CourseDisplayClass> arrayCourse = new ArrayList<>();
    ArrayList<CourseDisplayClass> arrayCourseForYou = new ArrayList<>();
    ArrayList<CourseDisplayClass> arrayCourseRated = new ArrayList<>();
    ArrayList<CourseDisplayClass> arrayCourseFree = new ArrayList<>();
    ArrayList<CategoryDisplayClass> arrayCategory = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        alert = new LoadingAlert(getActivity());
        alert.startLoading();
        if(savedInstanceState == null)
        {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    // Cấu hình RecyclerView
                    configureRecyclerView(recyclerPopular, LinearLayoutManager.HORIZONTAL);
                    configureRecyclerView(recyclerTopRated, LinearLayoutManager.VERTICAL);
                    configureRecyclerView(recyclerCategories, LinearLayoutManager.HORIZONTAL);
                    configureRecyclerView(recyclerSpecial, LinearLayoutManager.HORIZONTAL);
                    configureRecyclerView(recyclerFree, LinearLayoutManager.HORIZONTAL);

                    // Đưa cấu hình OnItemTouchListener lên main thread
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadData();
                            addTouchListener(recyclerCategories);
                            addTouchListener(recyclerSpecial);
                            addTouchListener(recyclerPopular);
                            addTouchListener(recyclerFree);

                            // Đặt listener cho RefreshLayout
                            refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                @Override
                                public void onRefresh() {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadData();
                                            refresh.setRefreshing(false);
                                        }
                                    }, 600);
                                }
                            });

                            // Đặt listener cho các TextView
                            setOnClickListener(txtCategories, 0);
                            setOnClickListener(txtPopular, 1);
                            setOnClickListener(txtForYou, 2);
                            setOnClickListener(txtRate, 3);
                            setOnClickListener(txtFree, 4);

                            // Đóng loading sau khi xử lý xong
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
//                                    loadData();
                                    alert.closeLoading();
                                }
                            }, 1000);
                        }
                    });
                }
            });
        }else{
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    // Cấu hình RecyclerView
                    configureRecyclerView(recyclerPopular, LinearLayoutManager.HORIZONTAL);
                    configureRecyclerView(recyclerTopRated, LinearLayoutManager.VERTICAL);
                    configureRecyclerView(recyclerCategories, LinearLayoutManager.HORIZONTAL);
                    configureRecyclerView(recyclerSpecial, LinearLayoutManager.HORIZONTAL);
                    configureRecyclerView(recyclerFree, LinearLayoutManager.HORIZONTAL);

                    // Đưa cấu hình OnItemTouchListener lên main thread
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadData();
                            addTouchListener(recyclerCategories);
                            addTouchListener(recyclerSpecial);
                            addTouchListener(recyclerPopular);
                            addTouchListener(recyclerFree);

                            // Đặt listener cho RefreshLayout
                            refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                @Override
                                public void onRefresh() {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadData();
                                            refresh.setRefreshing(false);
                                        }
                                    }, 600);
                                }
                            });

                            // Đặt listener cho các TextView
                            setOnClickListener(txtCategories, 0);
                            setOnClickListener(txtPopular, 1);
                            setOnClickListener(txtForYou, 2);
                            setOnClickListener(txtRate, 3);
                            setOnClickListener(txtFree, 4);

                            // Đóng loading sau khi xử lý xong
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alert.closeLoading();
                                }
                            }, 1000);
                        }
                    });
                }
            });
        }

        ;}
    private void configureRecyclerView(RecyclerView recyclerView, int orientation) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), orientation, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
    }

    // Một phương thức để thêm OnItemTouchListener vào RecyclerView
    private void addTouchListener(RecyclerView recyclerView) {
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
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
    }
    private void setOnClickListener(TextView textView, int key) {
        textView.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), See_All_Activity.class);
            intent.putExtra("key", key);
            if (key == 0) {
                intent.putExtra("category_title", arrayCategories);
                intent.putExtra("category_id", arrayCategoriesId);
            }
            startActivity(intent);
        });
    }
    private void loadData(){
        adapterCategory = new CategoryDisplayAdapter(arrayCategory, false, getContext());
        adapterRecycleView = new CourseDisplayAdapter(arrayCourse, getContext());
        adapterRecycleView1 = new CourseDisplayAdapter(arrayCourseFree, getContext());
        adapterRecycleView2 = new CourseDisplayAdapter(arrayCourseForYou, getContext());
        adapterRecycleViewVertical = new CourseDisplayVerticalAdapter(arrayCourseRated, getContext());

        if(mAuth.getCurrentUser() != null)
        {
                db.collection("Categories").orderBy("category_order", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                        return;
                    }
                    arrayCategory.clear();
                    arrayCategories.clear();
                    if(value !=null)
                    {
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.toObject(CategoryDisplayClass.class) != null){
                                if(!doc.getString("category_id").equals("grade10") && !doc.getString("category_id").equals("grade11") && !doc.getString("category_id").equals("grade12"))
                                {
                                    arrayCategory.add(doc.toObject(CategoryDisplayClass.class));
                                }

                            }
                        }
                    }
                    adapterCategory.notifyDataSetChanged();
                    for(int i = 0; i < arrayCategory.size();i++)
                    {
                        arrayCategories.add(arrayCategory.get(i).getCategory_title());
                        arrayCategoriesId.add(arrayCategory.get(i).getCategory_id());
                    }
                }
            });
            db.collection("Users").document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                        return;
                    }
                    if(mAuth.getCurrentUser() != null){
                        if(value !=null)
                        {
                            if(value.getString("user_grade").compareTo("Lớp 10") == 0) user_grade = 10;
                            else if(value.getString("user_grade").compareTo("Lớp 11") == 0) user_grade = 11;
                            else user_grade = 12;
                            loadByGradeAndFilter(user_grade, "member");
                            loadByGradeAndFilter(user_grade, "new");
                            loadByGradeAndFilter(user_grade, "rate");
                            loadByGradeAndFilter(user_grade, "free");
                        }
                    }

                }
            });

        //adapterRecycleView.setClickItemListener(this);
        //adapterRecycleViewVertical.setClickItemListener(this);
            recyclerCategories.setAdapter(adapterCategory);
            recyclerPopular.setAdapter(adapterRecycleView);
            recyclerTopRated.setAdapter(adapterRecycleViewVertical);
            recyclerSpecial.setAdapter(adapterRecycleView2);
            recyclerFree.setAdapter(adapterRecycleView1);
        }
    }
    private void mapping(){
        recyclerPopular = getView().findViewById(R.id.recyclerPopular);
        recyclerTopRated = getView().findViewById(R.id.recyclerTopRated);
        recyclerCategories = getView().findViewById(R.id.recyclerCategories);
        recyclerSpecial = getView().findViewById(R.id.recyclerSpecial);
        recyclerFree = getView().findViewById(R.id.recyclerFree);

        txtCategories = (TextView) getView().findViewById(R.id.txtCategories);
        txtForYou = (TextView) getView().findViewById(R.id.txtForYou);
        txtPopular = (TextView) getView().findViewById(R.id.txtPopular);
        txtRate = (TextView) getView().findViewById(R.id.txtTopRate);
        txtFree = (TextView) getView().findViewById(R.id.txtFree);

        refresh = (SwipeRefreshLayout) getView().findViewById(R.id.refresh);
    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(getActivity(),mes,Toast.LENGTH_SHORT);
        toast.show();
    }
    private void loadByGradeAndFilter(int grade, String filter){
        if(mAuth.getCurrentUser() != null)
        {
            switch (filter){
                case "member" :
                    if( grade == 10 ){
                        db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).orderBy("course_grade", Query.Direction.ASCENDING).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error != null)
                                {
                                   
                                    return;
                                }
                                arrayCourse.clear();
                                for (QueryDocumentSnapshot doc : value) {
                                    if (doc.toObject(CourseDisplayClass.class) != null) {
                                        arrayCourse.add(doc.toObject(CourseDisplayClass.class));
                                    }

                                }
                                adapterRecycleView.notifyDataSetChanged();
                            }
                        }); // Khoá học
                    }else if( grade == 12 ){
                        db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).orderBy("course_grade", Query.Direction.DESCENDING).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error != null)
                                {
                                   
                                    return;
                                }
                                arrayCourse.clear();
                                for (QueryDocumentSnapshot doc : value) {
                                    if (doc.toObject(CourseDisplayClass.class) != null) {
                                        arrayCourse.add(doc.toObject(CourseDisplayClass.class));
                                    }

                                }
                                adapterRecycleView.notifyDataSetChanged();
                            }
                        }); // Khoá học
                    }else{
                        db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).orderBy("course_member", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error != null)
                                {
                                   
                                    return;
                                }
                                arrayCourse.clear();
                                if(value != null){
                                    for (QueryDocumentSnapshot doc : value) {
                                        if (doc.toObject(CourseDisplayClass.class) != null) {
                                            arrayCourse.add(doc.toObject(CourseDisplayClass.class));
                                        }

                                    }
                                    Collections.sort(arrayCourse, new Comparator<CourseDisplayClass>() {
                                        @Override
                                        public int compare(CourseDisplayClass o1, CourseDisplayClass o2) {
                                            if (o1.getCourse_grade() == 11) return -1;
                                            if (o2.getCourse_grade() == 11) return 1;
                                            return Integer.compare(o1.getCourse_grade(), o2.getCourse_grade());
                                        }
                                    });
                                }
                                adapterRecycleView.notifyDataSetChanged();
                            }
                        }); // Khoá học
                    }
                    break;
                case "new" :
                    if( grade == 10 ){
                        db.collection("Courses").whereEqualTo("course_type","course").orderBy("course_grade", Query.Direction.ASCENDING)
                                .orderBy("course_upload", Query.Direction.ASCENDING)
                                .whereEqualTo("course_state", true)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (error != null) {
                                           
                                            return;
                                        }
                                        arrayCourseForYou.clear();
                                        if (value != null) {

                                            for (QueryDocumentSnapshot doc : value) {
                                                CourseDisplayClass course = doc.toObject(CourseDisplayClass.class);
                                                if (course != null) {
                                                    arrayCourseForYou.add(course);
                                                }
                                            }

                                        }
                                        adapterRecycleView2.notifyDataSetChanged();
                                    }
                                });
                    }else if( grade == 12 ){
                        db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).orderBy("course_grade", Query.Direction.DESCENDING)
                                .orderBy("course_upload", Query.Direction.ASCENDING)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (error != null) {

                                            return;
                                        }
                                        arrayCourseForYou.clear();
                                        if (value != null) {

                                            for (QueryDocumentSnapshot doc : value) {
                                                CourseDisplayClass course = doc.toObject(CourseDisplayClass.class);
                                                if (course != null) {
                                                    arrayCourseForYou.add(course);
                                                }
                                            }

                                        }
                                        adapterRecycleView2.notifyDataSetChanged();
                                    }
                                });
                    }else{
                        db.collection("Courses").whereEqualTo("course_type","course").orderBy("course_grade", Query.Direction.DESCENDING)
                                .orderBy("course_upload", Query.Direction.ASCENDING)
                                .whereEqualTo("course_state", true)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (error != null) {
                                            return;
                                        }
                                        arrayCourseForYou.clear();
                                        if (value != null) {

                                            for (QueryDocumentSnapshot doc : value) {
                                                CourseDisplayClass course = doc.toObject(CourseDisplayClass.class);
                                                if (course != null) {
                                                    arrayCourseForYou.add(course);
                                                }
                                            }
                                            Collections.sort(arrayCourseForYou, new Comparator<CourseDisplayClass>() {
                                                @Override
                                                public int compare(CourseDisplayClass o1, CourseDisplayClass o2) {
                                                    if (o1.getCourse_grade() == 11) return -1;
                                                    if (o2.getCourse_grade() == 11) return 1;
                                                    return Integer.compare(o1.getCourse_grade(), o2.getCourse_grade());
                                                }
                                            });

                                        }
                                        adapterRecycleView2.notifyDataSetChanged();
                                    }
                                });
                    }

                    break;
                case "rate" :
                    if( grade == 10 ){
                        db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).orderBy("course_grade", Query.Direction.ASCENDING).orderBy("course_rate", Query.Direction.DESCENDING).limit(5).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error != null)
                                {
                                   
                                    return;
                                }
                                arrayCourseRated.clear();
                                if(value != null)
                                {
                                    for (QueryDocumentSnapshot doc : value) {
                                        if (doc.toObject(CourseDisplayClass.class) != null) {
                                            arrayCourseRated.add(doc.toObject(CourseDisplayClass.class));
                                        }
                                    }
                                }
                                adapterRecycleViewVertical.notifyDataSetChanged();
                            }
                        });

                    }else if( grade == 12 ){
                        db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).orderBy("course_grade", Query.Direction.DESCENDING).orderBy("course_rate", Query.Direction.DESCENDING).limit(5).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error != null)
                                {
                                   
                                    return;
                                }
                                arrayCourseRated.clear();
                                if(value != null){
                                    for (QueryDocumentSnapshot doc : value) {
                                        if (doc.toObject(CourseDisplayClass.class) != null) {
                                            arrayCourseRated.add(doc.toObject(CourseDisplayClass.class));
                                        }
                                    }
                                }
                                adapterRecycleViewVertical.notifyDataSetChanged();
                            }
                        });
                    }else{
                        db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).orderBy("course_rate", Query.Direction.DESCENDING).limit(5).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error != null)
                                {
                                    return;
                                }
                                arrayCourseRated.clear();
                                if(value != null)
                                {
                                    for (QueryDocumentSnapshot doc : value) {
                                        if (doc.toObject(CourseDisplayClass.class) != null) {
                                            arrayCourseRated.add(doc.toObject(CourseDisplayClass.class));
                                        }
                                    }
                                    Collections.sort(arrayCourseRated, new Comparator<CourseDisplayClass>() {
                                        @Override
                                        public int compare(CourseDisplayClass o1, CourseDisplayClass o2) {
                                            if (o1.getCourse_grade() == 11) return -1;
                                            if (o2.getCourse_grade() == 11) return 1;
                                            return Integer.compare(o1.getCourse_grade(), o2.getCourse_grade());
                                        }
                                    });
                                }

                                adapterRecycleViewVertical.notifyDataSetChanged();
                            }
                        });
                    }
                    break;
                case "free" :
                    if( grade == 10 ){
                        db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).whereEqualTo("course_price", 0).orderBy("course_grade", Query.Direction.ASCENDING).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error != null)
                                {
                                   
                                    return;
                                }
                                arrayCourseFree.clear();
                                if(value != null){
                                    for (QueryDocumentSnapshot doc : value) {
                                        if (doc.toObject(CourseDisplayClass.class) != null) {
                                            arrayCourseFree.add(doc.toObject(CourseDisplayClass.class));
                                        }

                                    }
                                }
                                adapterRecycleView1.notifyDataSetChanged();

                            }
                        });
                    }else if( grade == 12 ){
                        db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).whereEqualTo("course_price", 0).orderBy("course_grade", Query.Direction.DESCENDING).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error != null)
                                {
                                   
                                    return;
                                }
                                arrayCourseFree.clear();
                                if(value != null)
                                {
                                    for (QueryDocumentSnapshot doc : value) {
                                        if (doc.toObject(CourseDisplayClass.class) != null) {
                                            arrayCourseFree.add(doc.toObject(CourseDisplayClass.class));
                                        }
                                    }
                                }
                                adapterRecycleView1.notifyDataSetChanged();
                            }
                        });
                    }else{
                        db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).whereEqualTo("course_price", 0).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error != null)
                                {
                                    return;
                                }
                                arrayCourseFree.clear();
                                if(value != null)
                                {
                                    for (QueryDocumentSnapshot doc : value) {
                                        if (doc.toObject(CourseDisplayClass.class) != null) {
                                            arrayCourseFree.add(doc.toObject(CourseDisplayClass.class));
                                        }

                                    }
                                    Collections.sort(arrayCourseFree, new Comparator<CourseDisplayClass>() {
                                        @Override
                                        public int compare(CourseDisplayClass o1, CourseDisplayClass o2) {
                                            if (o1.getCourse_grade() == 11) return -1;
                                            if (o2.getCourse_grade() == 11) return 1;
                                            return Integer.compare(o1.getCourse_grade(), o2.getCourse_grade());
                                        }
                                    });
                                }
                                adapterRecycleView1.notifyDataSetChanged();
                            }
                        });
                    }
                    break;
                default :
                    toastMes("Tải dữ liệu gặp lỗi");
                    break;
            }
        }
    }

    /*@Override
    public void onClick(String str) {
        //Intent intent = new Intent(getActivity(),CourseActivity.class);
        //intent.putExtra("course_key", str);
        //startActivity(intent);
        //getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(adapterCategory != null) adapterCategory.release();
        if(adapterRecycleView != null) adapterRecycleView.release();
        if(adapterRecycleView1 != null) adapterRecycleView1.release();
        if(adapterRecycleView2 != null) adapterRecycleView2.release();
        if(adapterRecycleViewVertical != null) adapterRecycleViewVertical.release();
        alert.closeLoading();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}