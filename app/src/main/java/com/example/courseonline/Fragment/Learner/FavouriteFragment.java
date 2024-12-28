package com.example.courseonline.Fragment.Learner;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Activity.Learner.DashboardActivity;
import com.example.courseonline.Adapter.Learner.FavouriteAdapter;
import com.example.courseonline.Domain.CourseDisplayClass;
import com.example.courseonline.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavouriteFragment extends Fragment{
    private RecyclerView recyclerView;
    private AppCompatButton buttonStart;
    private TextView txtNone1;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<CourseDisplayClass> arrayFavourite = new ArrayList<>();
    private List<String> arrayID = new ArrayList<>();
    private FavouriteAdapter adapterRecycleView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping();
        if (savedInstanceState == null) {
            // Activity is being created for the first time
            loadData();
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setAdapter(adapterRecycleView);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DashboardActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
    private void loadData(){
        recyclerView.setVisibility(View.VISIBLE);
        adapterRecycleView = new FavouriteAdapter(arrayFavourite, getContext());
        if(mAuth.getCurrentUser() != null) {
            db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Favourites").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    txtNone1.setVisibility(View.GONE);
                    buttonStart.setVisibility(View.GONE);
                    arrayID.clear();
                    if(error !=null)
                    {
                        return;
                    }
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("course_id") != null) {
                            arrayID.add(doc.get("course_id").toString());
                        }
                    }
                    if(!arrayID.isEmpty())
                    {
                        recyclerView.setVisibility(View.VISIBLE);
                        db.collection("Courses").whereIn("course_id",arrayID).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error!=null)
                                {
                                    return;
                                }
                                arrayFavourite.clear();
                                for (QueryDocumentSnapshot doc : value) {
                                    if (doc.toObject(CourseDisplayClass.class) != null) {
                                        arrayFavourite.add(doc.toObject(CourseDisplayClass.class));
                                    }

                                }
                                adapterRecycleView.notifyDataSetChanged();

                            }
                        });
                    }
                    else{
                        recyclerView.setVisibility(View.GONE);
                        txtNone1.setVisibility(View.VISIBLE);
                        buttonStart.setVisibility(View.VISIBLE);
                        arrayFavourite.clear();
                        adapterRecycleView.notifyDataSetChanged();
                    }
                }
            });

        }

    }
    private void mapping(){
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerFavourite);
        txtNone1 = (TextView) getView().findViewById(R.id.txtNone1);
        buttonStart = (AppCompatButton) getView().findViewById(R.id.buttonStartLearn1);
    }
    /*@Override
    public void onClick(String str) {
        Intent intent = new Intent(getActivity(), CourseActivity.class);
        intent.putExtra("course_key", str);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }*/

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        int currentOrientation = getResources().getConfiguration().orientation;

        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE){
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setClipToPadding(false);
            recyclerView.setPadding(150,0,0,0);
            recyclerView.setAdapter(adapterRecycleView);
        }
        else {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setClipToPadding(false);
            recyclerView.setPadding(10,0,0,0);
            recyclerView.setAdapter(adapterRecycleView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(adapterRecycleView != null) adapterRecycleView.release();
    }
}