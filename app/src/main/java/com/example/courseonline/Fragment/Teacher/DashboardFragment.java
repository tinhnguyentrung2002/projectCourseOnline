package com.example.courseonline.Fragment.Teacher;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Teacher.RankCourseAdapter;
import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.Domain.CourseDisplayClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateField;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LoadingAlert alert;
    private TextView txtRate, txtAllCourse, txtAllMember;
    private RecyclerView recyclerRank, recyclerRankMember;
    private RankCourseAdapter rankCourseAdapter, rankCourseAdapter1;
    private ArrayList<CourseDisplayClass> rankList = new ArrayList<>();
    private ArrayList<CourseDisplayClass> rankList1 = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        db = FirebaseFirestore.getInstance();
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
                recyclerRank.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
                recyclerRank.setHasFixedSize(true);
                recyclerRank.setItemViewCacheSize(20);

                recyclerRankMember.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
                recyclerRankMember.setHasFixedSize(true);
                recyclerRankMember.setItemViewCacheSize(20);

                recyclerRank.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
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
                recyclerRankMember.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
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
        }); handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alert.closeLoading();
            }
        },600);





    }
    private void loadData(){
        rankCourseAdapter = new RankCourseAdapter(rankList, "rate");
        rankCourseAdapter1 = new RankCourseAdapter(rankList1, "member");
       // Toast.makeText(getActivity(), String.valueOf(rankCourseAdapter.getItemCount()), Toast.LENGTH_SHORT ).show();
        recyclerRank.setAdapter(rankCourseAdapter);
        recyclerRankMember.setAdapter(rankCourseAdapter1);
        if(mAuth.getCurrentUser() != null)
        {
            db.collection("Courses").whereEqualTo("course_owner_id", mAuth.getCurrentUser().getUid()).whereEqualTo("course_state", true).orderBy("course_rate", Query.Direction.DESCENDING).limit(3).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null){
                        return;
                    }


                    if(value.size() != 0)
                    {
                        rankList.clear();
                        for(QueryDocumentSnapshot doc : value)
                        {
                            rankList.add(doc.toObject(CourseDisplayClass.class));
                        }
                        rankCourseAdapter.notifyDataSetChanged();
                    }
                }
            });
            db.collection("Courses").whereEqualTo("course_owner_id", mAuth.getCurrentUser().getUid()).whereEqualTo("course_state", true).orderBy("course_member", Query.Direction.DESCENDING).limit(3).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null){
                        return;
                    }
                    if(value.size() != 0)
                    {
                        rankList1.clear();
                        for(QueryDocumentSnapshot doc : value)
                        {
                            rankList1.add(doc.toObject(CourseDisplayClass.class));
                        }
                        rankCourseAdapter1.notifyDataSetChanged();
                    }
                }
            });

            Query query = db.collection("Courses").whereEqualTo("course_owner_id", mAuth.getCurrentUser().getUid());
            AggregateQuery aggregateQuery = query.aggregate(AggregateField.average("course_rate"));
            aggregateQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        AggregateQuerySnapshot snapshot = task.getResult();
                        txtRate.setText(String.format("%.1f",snapshot.get(AggregateField.average("course_rate"))) +"/5");
                    }else{
                        Log.d("aaaa", "Aggregation failed: ", task.getException());
                    }
                }
            });

            Query query1 = db.collection("Courses").whereEqualTo("course_owner_id", mAuth.getCurrentUser().getUid());
            AggregateQuery aggregateQuery1 = query1.aggregate(AggregateField.sum("course_member"));
            aggregateQuery1.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        AggregateQuerySnapshot snapshot = task.getResult();
                        txtAllMember.setText(String.valueOf(snapshot.get(AggregateField.sum("course_member"))));
                    }else{
                        Log.d("bbbb", "Aggregation failed: ", task.getException());
                    }
                }
            });

            Query query2 = db.collection("Courses").whereEqualTo("course_owner_id", mAuth.getCurrentUser().getUid());
            AggregateQuery aggregateQuery2 = query2.count();
            aggregateQuery2.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        AggregateQuerySnapshot snapshot = task.getResult();
                        txtAllCourse.setText(String.valueOf(snapshot.getCount()));
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        alert.closeLoading();
    }
    private void mapping(){
        txtAllCourse = (TextView) getView().findViewById(R.id.txtAllCourse);
        txtAllMember = (TextView) getView().findViewById(R.id.txtAllMember);
        txtRate = (TextView) getView().findViewById(R.id.teacherRate);
        recyclerRank = (RecyclerView) getView().findViewById(R.id.recyclerRank);
        recyclerRankMember = (RecyclerView) getView().findViewById(R.id.recyclerRankMember);
    }
}