package com.example.courseonline.Fragment.Learner;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Learner.SearchAdapter;
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
    RecyclerView recyclerSearch;
    SearchAdapter searchAdapter;
    TextView txtNone5;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    ArrayList<CourseDisplayClass> arrayList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        searchAdapter = new SearchAdapter(arrayList, 1);
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
        recyclerSearch.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        recyclerSearch.setHasFixedSize(true);
        recyclerSearch.setItemViewCacheSize(20);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerSearch.addItemDecoration(itemDecoration);
        recyclerSearch.setAdapter(searchAdapter);
        txtNone5.setVisibility(View.GONE);

        db.collection("Courses").whereEqualTo("course_state", true).orderBy("course_member", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    return;
                }
                if(value.size() != 0)
                {
                    txtNone5.setVisibility(View.GONE);
                    arrayList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.toObject(CourseDisplayClass.class) != null) {
                            arrayList.add(doc.toObject(CourseDisplayClass.class));
                        }
                        searchAdapter.notifyDataSetChanged();
                    }
                }else{
                    txtNone5.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void mapping(){
        searchView = (SearchView) getView().findViewById(R.id.search);
        recyclerSearch = (RecyclerView) getView().findViewById(R.id.recyclerSearch);
        txtNone5 = (TextView) getView().findViewById(R.id.txtNone5);
    }

}