package com.example.courseonline.Fragment.Learner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.example.courseonline.Adapter.Learner.CourseDisplayVeriticalAdapter;
import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.Domain.CategoryDisplayClass;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment{
    LoadingAlert alert;

    private TextView txtCategories, txtPopular, txtForYou, txtFree, txtRate;
    private Toast toast;
    private SwipeRefreshLayout refresh;
    private ArrayList<String> arrayCategories = new ArrayList<String>();
    private ArrayList<String> arrayCategoriesId = new ArrayList<String>();
    //private ArrayList<String> arrayTracker = new ArrayList<String>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CourseDisplayAdapter adapterRecycleView, adapterRecycleView1, adapterRecycleView2;
    private CourseDisplayVeriticalAdapter adapterRecycleViewVertical;
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
        alert = new LoadingAlert(getActivity());
        alert.startLoading();
        loadData();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                recyclerPopular.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
                recyclerPopular.setHasFixedSize(true);
                recyclerPopular.setItemViewCacheSize(20);
                recyclerTopRated.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
                recyclerTopRated.setHasFixedSize(true);
                recyclerTopRated.setItemViewCacheSize(20);
                recyclerCategories.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
                recyclerCategories.setHasFixedSize(true);
                recyclerCategories.setItemViewCacheSize(20);
                recyclerSpecial.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
                recyclerSpecial.setHasFixedSize(true);
                recyclerSpecial.setItemViewCacheSize(20);
                recyclerFree.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
                recyclerFree.setHasFixedSize(true);
                recyclerFree.setItemViewCacheSize(20);


                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        recyclerCategories.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
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
                        recyclerSpecial.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
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
                        recyclerPopular.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
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
                        recyclerFree.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
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
                        refresh.setOnRefreshListener(() -> {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadData();
                                            refresh.setRefreshing(false);
                                        }
                                    }, 1000);
                                }
                        );
                        txtCategories.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), See_All_Activity.class);
                                intent.putExtra("key", 0);
                                intent.putExtra("category_title", arrayCategories);
                                intent.putExtra("category_id", arrayCategoriesId);
                                startActivity(intent);
                            }
                        });
                        txtPopular.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), See_All_Activity.class);
                                intent.putExtra("key", 1);
                                startActivity(intent);
                            }
                        });
                        txtForYou.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), See_All_Activity.class);
                                intent.putExtra("key", 2);
                                startActivity(intent);
                            }
                        });
                        txtRate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), See_All_Activity.class);
                                intent.putExtra("key", 3);
                                startActivity(intent);
                            }
                        });
                        txtFree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), See_All_Activity.class);
                                intent.putExtra("key", 4);
                                startActivity(intent);
                            }
                        });

                    }
                });
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alert.closeLoading();
                    }
                },600);
            }
        });
    }
    private void loadData(){
        adapterRecycleView = new CourseDisplayAdapter(arrayCourse);
        adapterRecycleView1 = new CourseDisplayAdapter(arrayCourseFree);
        adapterRecycleView2 = new CourseDisplayAdapter(arrayCourseForYou);
        adapterRecycleViewVertical = new CourseDisplayVeriticalAdapter(arrayCourseRated);
        adapterCategory = new CategoryDisplayAdapter(arrayCategory);

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
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.toObject(CategoryDisplayClass.class) != null) {
                            if(!doc.getString("category_id").equals("freeCategory"))
                            {
                                arrayCategory.add(doc.toObject(CategoryDisplayClass.class));
                            }

                        }
                        adapterCategory.notifyDataSetChanged();
                    }
                    for(int i = 0; i < arrayCategory.size();i++)
                    {
                        arrayCategories.add(arrayCategory.get(i).getCategory_title());
                        arrayCategoriesId.add(arrayCategory.get(i).getCategory_id());
                    }
                }
            });
            db.collection("Courses").whereEqualTo("course_state", true).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        adapterRecycleView.notifyDataSetChanged();
                    }
                }
            }); // Khoá học
            db.collection("Courses").whereEqualTo("course_state", true).orderBy("course_upload", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                        Log.d("aaaa",error.toString());
                        return;
                    }
                    arrayCourseForYou.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.toObject(CourseDisplayClass.class) != null) {
                            arrayCourseForYou.add(doc.toObject(CourseDisplayClass.class));
                        }
                        adapterRecycleView2.notifyDataSetChanged();
                    }
                }
            });
            db.collection("Courses").whereEqualTo("course_state", true).orderBy("course_rate", Query.Direction.DESCENDING).limit(5).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                        return;
                    }
                    arrayCourseRated.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.toObject(CourseDisplayClass.class) != null) {
                            arrayCourseRated.add(doc.toObject(CourseDisplayClass.class));
                        }
                        adapterRecycleViewVertical.notifyDataSetChanged();
                    }
                }
            });
            db.collection("Courses").whereEqualTo("course_state", true).whereEqualTo("course_price", 0).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                        return;
                    }
                    arrayCourseFree.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.toObject(CourseDisplayClass.class) != null) {
                            arrayCourseFree.add(doc.toObject(CourseDisplayClass.class));
                        }
                        adapterRecycleView1.notifyDataSetChanged();
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
        alert.closeLoading();
    }
}