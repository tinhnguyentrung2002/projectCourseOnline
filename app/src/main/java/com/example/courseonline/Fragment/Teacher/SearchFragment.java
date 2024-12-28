package com.example.courseonline.Fragment.Teacher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Learner.ClassAdapter;
import com.example.courseonline.Adapter.Learner.SearchAdapter;
import com.example.courseonline.Domain.ClassesClass;
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

public class SearchFragment extends Fragment {

    SearchView searchView;
    RecyclerView recyclerCourse, recyclerClass;
    SearchAdapter courseAdapter;
    ClassAdapter classAdapter;
    TextView txtNone6;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    ArrayList<CourseDisplayClass> arrayList = new ArrayList<>();
    ArrayList<ClassesClass> arrayList1 = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        courseAdapter = new SearchAdapter(arrayList, 2, getContext());
        classAdapter = new ClassAdapter(arrayList1,  getContext(),2);
//        SearchManager searchManager =(SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                searchAdapter.getFilter().filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                searchAdapter.getFilter().filter(newText);
//                return false;
//            }
//
//        });
        recyclerCourse.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerCourse.setHasFixedSize(true);
        recyclerCourse.setItemViewCacheSize(20);
        recyclerCourse.setClipToPadding(false);
        recyclerCourse.setPadding(10,0,0,0);
        recyclerCourse.setAdapter(courseAdapter);
//        txtNone6.setVisibility(View.GONE);
        db.collection("Courses").whereEqualTo("course_owner_id", mAuth.getCurrentUser().getUid()).whereEqualTo("course_type", "course").orderBy("course_upload", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    return;
                }
                if(value.size()!=0)
                {
                    arrayList.clear();
//                    txtNone6.setVisibility(View.GONE);
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.toObject(CourseDisplayClass.class) != null) {
                            arrayList.add(doc.toObject(CourseDisplayClass.class));
                        }
                        courseAdapter.notifyDataSetChanged();
                    }
                }else{
//                    txtNone6.setVisibility(View.VISIBLE);
                }

            }
        });
        recyclerClass.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerClass.setHasFixedSize(true);
        recyclerClass.setItemViewCacheSize(20);
        recyclerClass.setClipToPadding(false);
        recyclerClass.setPadding(10,0,0,0);
        recyclerClass.setAdapter(classAdapter);
        addTouchListener(recyclerClass);
//        txtNone6.setVisibility(View.GONE);
        db.collection("Courses").whereEqualTo("course_owner_id", mAuth.getCurrentUser().getUid()).whereEqualTo("course_type", "class").orderBy("course_upload", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    return;
                }
                if(value.size()!=0)
                {
                    arrayList1.clear();
//                    txtNone6.setVisibility(View.GONE);
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.toObject(ClassesClass.class) != null) {
                            arrayList1.add(doc.toObject(ClassesClass.class));
                        }
                        classAdapter.notifyDataSetChanged();
                    }
                }else{
//                    txtNone6.setVisibility(View.VISIBLE);
                }

            }
        });
    }
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
    private void mapping(){
//        searchView = (SearchView) getView().findViewById(R.id.searchTeacher);
        recyclerCourse = (RecyclerView) getView().findViewById(R.id.recyclerCourseTeacher);
        recyclerClass = (RecyclerView) getView().findViewById(R.id.recyclerClassTeacher);
//        txtNone6 = (TextView) getView().findViewById(R.id.txtNone6);
    }
}