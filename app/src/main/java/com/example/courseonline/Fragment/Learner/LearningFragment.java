package com.example.courseonline.Fragment.Learner;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Activity.Learner.DashboardActivity;
import com.example.courseonline.Adapter.Learner.LearningAdapter;
import com.example.courseonline.Domain.CourseDisplayClass;
import com.example.courseonline.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LearningFragment extends Fragment {
    private RecyclerView recyclerLearning;
    private TextView txtNone;
    private Toast toast;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private AppCompatButton buttonStart;
    private ArrayList<CourseDisplayClass> arrayLearning;
    private ArrayList<String> arrayID;
    private LearningAdapter adapterRecycleView;
    private String uid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_learning, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null)
        {
            uid = mAuth.getCurrentUser().getUid();
        }
        db = FirebaseFirestore.getInstance();
        recyclerLearning.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        recyclerLearning.setHasFixedSize(true);
        recyclerLearning.setItemViewCacheSize(20);
        loadData();
        recyclerLearning.setAdapter(adapterRecycleView);
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
        arrayID = new ArrayList<>();
        arrayLearning = new ArrayList<>();
        adapterRecycleView = new LearningAdapter(arrayLearning);
        if(mAuth.getCurrentUser() != null) {
            db.collection("Users").document(uid).collection("cart").whereEqualTo("cart_complete", false).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                        return;
                    }
                    arrayID.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("cart_item_id") != null) {
                            arrayID.add(doc.get("cart_item_id").toString());
                        }
                    }
                    if(arrayID.size() != 0)
                    {
                        txtNone.setVisibility(View.GONE);
                        recyclerLearning.setVisibility(View.VISIBLE);
                        buttonStart.setVisibility(View.GONE);
                        db.collection("Courses").whereIn("course_id",arrayID).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error!=null)
                                {
                                    return;
                                }
                                arrayLearning.clear();
                                for (QueryDocumentSnapshot doc : value) {
                                    if (doc.toObject(CourseDisplayClass.class) != null) {
                                        arrayLearning.add(doc.toObject(CourseDisplayClass.class));
                                    }
                                    adapterRecycleView.notifyDataSetChanged();
                                }

                            }
                        });
                    }
                    else{
                        arrayLearning.clear();
                        adapterRecycleView.notifyDataSetChanged();
                        txtNone.setVisibility(View.VISIBLE);
                        recyclerLearning.setVisibility(View.GONE);
                        buttonStart.setVisibility(View.VISIBLE);
                    }

                }
            });

        }

    }
    private void mapping(){
        recyclerLearning = (RecyclerView) getView().findViewById(R.id.recyclerLearning);
        txtNone = (TextView) getView().findViewById(R.id.txtNone);
        buttonStart = (AppCompatButton) getView().findViewById(R.id.buttonStartLearn);
    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(getActivity(), mes,Toast.LENGTH_SHORT);
        toast.show();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        int currentOrientation = getResources().getConfiguration().orientation;

        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE){
            recyclerLearning.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
            recyclerLearning.setHasFixedSize(true);
            recyclerLearning.setItemViewCacheSize(20);
            recyclerLearning.setAdapter(adapterRecycleView);
        }
        else {
            recyclerLearning.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
            recyclerLearning.setHasFixedSize(true);
            recyclerLearning.setItemViewCacheSize(20);
            recyclerLearning.setAdapter(adapterRecycleView);
        }
    }
}