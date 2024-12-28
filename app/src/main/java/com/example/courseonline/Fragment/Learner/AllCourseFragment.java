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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Activity.Learner.DashboardActivity;
import com.example.courseonline.Adapter.Learner.AllCourseAdapter;
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

public class AllCourseFragment extends Fragment {
    private RecyclerView recyclerInventory;
    private Toast toast;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView txtNone2;
    private AppCompatButton buttonStart;
    private ArrayList<CourseDisplayClass> arrayInventory = new ArrayList<>();
    private List<String> arrayID = new ArrayList<>();
    private AllCourseAdapter adapterRecycleView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        return inflater.inflate(R.layout.fragment_all_course, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping();
        recyclerInventory.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerInventory.setHasFixedSize(true);
        recyclerInventory.setItemViewCacheSize(20);
        if (savedInstanceState == null) {
            loadData();
        }
        recyclerInventory.setAdapter(adapterRecycleView);
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
        recyclerInventory.setVisibility(View.VISIBLE);
        adapterRecycleView = new AllCourseAdapter(arrayInventory, getContext());
        if(mAuth.getCurrentUser() != null) {
            db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_type", "course").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    arrayID.clear();
                    if(error !=null)
                    {
                        return;
                    }
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("own_course_item_id") != null) {
                            arrayID.add(doc.get("own_course_item_id").toString());
                        }
                    }

                    if(!arrayID.isEmpty())
                    {
                        txtNone2.setVisibility(View.GONE);
                        buttonStart.setVisibility(View.GONE);
                        db.collection("Courses").whereIn("course_id", arrayID).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error!=null)
                                {
                                    return;
                                }

                                arrayInventory.clear();
                                for (QueryDocumentSnapshot doc : value) {
                                    if (doc.toObject(CourseDisplayClass.class) != null) {
                                        arrayInventory.add(doc.toObject(CourseDisplayClass.class));
                                    }

                                }
                                adapterRecycleView.notifyDataSetChanged();

                            }
                        });
                    }
                    else{
                        txtNone2.setVisibility(View.VISIBLE);
                        buttonStart.setVisibility(View.VISIBLE);
                        arrayInventory.clear();
                        adapterRecycleView.notifyDataSetChanged();
                    }
                }
            });

        }

    }
    private void mapping(){
        recyclerInventory = (RecyclerView) getView().findViewById(R.id.recyclerinventory);
        txtNone2 = (TextView) getView().findViewById(R.id.txtNone2);
        buttonStart = (AppCompatButton) getView().findViewById(R.id.buttonStartLearn2);
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
            recyclerInventory.setLayoutManager(new GridLayoutManager(getActivity(),3));
            recyclerInventory.setHasFixedSize(true);
            recyclerInventory.setItemViewCacheSize(20);
            recyclerInventory.setClipToPadding(false);
            recyclerInventory.setPadding(150,0,0,0);
            recyclerInventory.setAdapter(adapterRecycleView);
        }
        else {
            recyclerInventory.setLayoutManager(new GridLayoutManager(getActivity(),2));
            recyclerInventory.setHasFixedSize(true);
            recyclerInventory.setItemViewCacheSize(20);
            recyclerInventory.setClipToPadding(false);
            recyclerInventory.setPadding(10,0,0,0);
            recyclerInventory.setAdapter(adapterRecycleView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(adapterRecycleView != null) adapterRecycleView.release();
    }
}