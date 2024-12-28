package com.example.courseonline.Activity.Learner;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Learner.DiscussionsAdapter;
import com.example.courseonline.Domain.DiscussionClass;
import com.example.courseonline.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DiscussionBoxActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    private TextView txtNoneDiscussionBox;
    private AppCompatImageButton btnBack;
    private RecyclerView recyclerDiscussionBox;
    private DiscussionsAdapter adapter;
    private ArrayList<DiscussionClass> discussions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_discussion_box);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mapping();

        recyclerDiscussionBox.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        discussions = new ArrayList<>();
        adapter = new DiscussionsAdapter(discussions, DiscussionBoxActivity.this);
        recyclerDiscussionBox.setAdapter(adapter);

        loadData();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void loadData(){
        if(mAuth.getCurrentUser() != null){
            db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("DiscussionGroups").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                        return;
                    }
                    List<String> discussionIDArray = new ArrayList<>();

                    for(QueryDocumentSnapshot doc : value){
                        discussionIDArray.add(doc.getString("discussion_id"));
                    }

                    if(discussionIDArray.size() != 0)
                    {
                        db.collection("Discussions").whereIn("discussion_id", discussionIDArray).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error != null)
                                {
                                    return;
                                }
                                discussions.clear();
                                if(value.size() != 0)
                                {
                                    txtNoneDiscussionBox.setVisibility(View.GONE);
                                    for(QueryDocumentSnapshot doc : value){
                                        discussions.add(doc.toObject(DiscussionClass.class));
                                    }
                                }else{
                                    txtNoneDiscussionBox.setVisibility(View.VISIBLE);
                                }

                                adapter.notifyDataSetChanged();
                            }
                        });
                    }else {
                        discussions.clear();
                        txtNoneDiscussionBox.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }


                }
            });

        }
    }
    private void mapping() {
        txtNoneDiscussionBox = findViewById(R.id.txtNoneDiscussionBox);
        recyclerDiscussionBox = findViewById(R.id.recyclerDiscussionBox);
        btnBack = findViewById(R.id.btnBackDiscussionBox);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adapter != null) adapter.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}