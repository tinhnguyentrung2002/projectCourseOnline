package com.example.courseonline.Fragment.Learner;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Learner.CategoryDisplayAdapter;
import com.example.courseonline.Adapter.Learner.SearchAdapter;
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

public class DiscoverFragment extends Fragment {

    SearchView searchView;
    RecyclerView recyclerSearch, recyclerFilter;
    SearchAdapter searchAdapter;
    TextView txtNone5;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    ConstraintLayout headingFilter;
    LinearLayout headingChildFilter;
    ImageView arrowFilter;
    ArrayList<CategoryDisplayClass> arrayType = new ArrayList<>();
    CategoryDisplayAdapter typeAdapter;
    ArrayList<CourseDisplayClass> arrayList = new ArrayList<>();
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String itemName = intent.getStringExtra("item");
            searchView.setQuery(itemName, false);
        }

    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        searchAdapter = new SearchAdapter(arrayList, 1, getContext());
        typeAdapter = new CategoryDisplayAdapter(arrayType, true,getContext());
        SearchManager searchManager =(SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAdapter.getFilter().filter(newText);
                return false;
            }

        });
        headingFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(headingChildFilter.getVisibility() == View.GONE) {
                    AutoTransition autoTransition = new AutoTransition();
                    autoTransition.setDuration(30);
                    TransitionManager.beginDelayedTransition(headingFilter, autoTransition);
                    headingChildFilter.setVisibility(View.VISIBLE);
                    arrowFilter.setImageResource(R.drawable.arrow_down_blue);
                }
                else {
                    AutoTransition autoTransition = new AutoTransition();
                    autoTransition.setDuration(30);
                    TransitionManager.beginDelayedTransition(headingFilter, autoTransition);
                    headingChildFilter.setVisibility(View.GONE);
                    arrowFilter.setImageResource(R.drawable.arrow_up);
                }
            }
        });

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerSearch.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        recyclerSearch.setHasFixedSize(true);
        recyclerSearch.setItemViewCacheSize(20);
        recyclerSearch.addItemDecoration(itemDecoration);
        recyclerSearch.setAdapter(searchAdapter);

        recyclerFilter.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        recyclerFilter.setHasFixedSize(true);
        recyclerFilter.setItemViewCacheSize(20);
        recyclerFilter.setAdapter(typeAdapter);
        addTouchListener(recyclerFilter);


        txtNone5.setVisibility(View.GONE);

        loadData();
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
    private void loadData(){
        db.collection("Categories").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    return;
                }
                arrayType.clear();
                if(value != null){
                    for(QueryDocumentSnapshot doc : value){
                        if(doc.getId().compareTo("grade10") != 0 && doc.getId().compareTo("grade11") != 0 && doc.getId().compareTo("grade12") != 0)
                        arrayType.add(doc.toObject(CategoryDisplayClass.class));

                    }
                    typeAdapter.notifyDataSetChanged();
                }
            }
        });

        db.collection("Courses").whereEqualTo("course_type","course").whereEqualTo("course_state", true).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Log.d("testt", error.toString());
                    return;
                }
                arrayList.clear();
                if(value.size() != 0)
                {
                    txtNone5.setVisibility(View.GONE);
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.toObject(CourseDisplayClass.class) != null) {
                            arrayList.add(doc.toObject(CourseDisplayClass.class));
                        }
                    }

                }else{
                    txtNone5.setVisibility(View.VISIBLE);
                }
                searchAdapter.notifyDataSetChanged();
            }
        });
    }
    private void mapping(){
        searchView = (SearchView) getView().findViewById(R.id.search);
        recyclerSearch = (RecyclerView) getView().findViewById(R.id.recyclerSearch);
        recyclerFilter = (RecyclerView) getView().findViewById(R.id.recyclerFilter);
        txtNone5 = (TextView) getView().findViewById(R.id.txtNone5);
        headingFilter = (ConstraintLayout) getView().findViewById(R.id.headingFilter);
        headingChildFilter = (LinearLayout) getView().findViewById(R.id.heading2_child_filter);
        arrowFilter = (ImageView) getView().findViewById(R.id.arrowFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(searchAdapter != null) searchAdapter.release();
        if(typeAdapter != null) typeAdapter.release();
     }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}