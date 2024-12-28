package com.example.courseonline.Fragment.Learner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Learner.RankAdapter;
import com.example.courseonline.Domain.RankClass;
import com.example.courseonline.Domain.UserClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankingFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView txtMyRank, txtMyPoints, txtMyReward;
    private AppCompatButton claimButton;
    private RankAdapter rankAdapter;
    private RecyclerView recyclerRank;
    private ArrayList<RankClass> rankArray = new ArrayList<>();
    private int mintColor;
    private int greyColor;
    private int pointsToClaim = 0;
    private final static String KEY_UID = "rank_user_id";
    private final static String KEY_USER_POINTS = "rank_user_points";
    private final static String KEY_USER_RANK_POSITION= "rank_position";
    private static final String KEY_LAST_CLAIMED_DATE = "last_claimed";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.fragment_ranking, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapping();
        mintColor = ContextCompat.getColor(requireContext(), R.color.mint);
        greyColor = ContextCompat.getColor(requireContext(), R.color.grey);
        recyclerRank.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerRank.setHasFixedSize(true);
        recyclerRank.setItemViewCacheSize(20);
        if(mAuth.getCurrentUser() != null)
        {
            loadData();
        }
        claimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                claimPoints();
            }
        });


    }
    private void loadData(){
        updateRankings();
        checkClaimStatus();
        rankAdapter = new RankAdapter(rankArray, getContext());
        recyclerRank.setAdapter(rankAdapter);
        db.collection("Rank").limit(10).orderBy("rank_position", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    return;
                }
                rankArray.clear();
                if(value != null && value.size() != 0){
                    for(QueryDocumentSnapshot doc : value)
                    {
                        rankArray.add(doc.toObject(RankClass.class));
                    }
                }else{
                    for (int i = 0 ;i<10;i++)
                    {
                        rankArray.add(new RankClass("null", 0, i+1, null));
                    }
                }
                rankAdapter.notifyDataSetChanged();
            }
        });
        db.collection("Rank").document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    return;
                }
                if(value.exists()){
                    txtMyPoints.setText(" "+value.getLong("rank_user_points"));
                    Long old_position = value.getLong("rank_old_position");
                    txtMyRank.setText(" "+value.getLong("rank_position"));
                    if(value.getLong("rank_position") > 10){
                        txtMyRank.setText("10+ ");
                    }
                    if (old_position != null) {
                        if (old_position > 10) {
                            pointsToClaim = 1;
                            txtMyReward.setText(" x " + pointsToClaim);
                        } else {
                            switch (old_position.intValue()) {
                                case 1: pointsToClaim = 20;    txtMyReward.setText(" x " + pointsToClaim); break;
                                case 2: pointsToClaim = 15;    txtMyReward.setText(" x " + pointsToClaim);break;
                                case 3: pointsToClaim = 10;    txtMyReward.setText(" x " + pointsToClaim);; break;
                                case 4:
                                case 5:
                                case 6:
                                case 7:
                                case 8:
                                case 9:
                                case 10: pointsToClaim = 5;    txtMyReward.setText(" x " + pointsToClaim); break;
                                default: pointsToClaim = 1;   txtMyReward.setText(" x " + pointsToClaim); break;
                            }
                        }
                    }

                }else{
                    txtMyPoints.setText(" N/A");
                    txtMyRank.setText(" N/A");
                    txtMyReward.setText(" N/A");
                }
            }
        });
    }
    private void checkClaimStatus() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("Rank").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    return;
                }
                if(value.exists()){
                    Timestamp lastClaimedTime = value.getTimestamp("last_claimed");
                    LocalDate lastClaimedDate;
                    LocalDate today = LocalDate.now();
                    if (lastClaimedTime != null) {
                        lastClaimedDate = lastClaimedTime.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        if (today.isEqual(lastClaimedDate)) {
                            disableClaimButton();
                        } else {
                            enableClaimButton();
                        }
                    } else {
                        enableClaimButton();
                    }


                }else{
                    disableClaimButton();
                }
            }
        });
    }
    private void claimPoints() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("Users").document(userId).update("user_points", FieldValue.increment(pointsToClaim))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            checkClaimStatus();
                            loadData();
                        }
                })
                .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                });
        db.collection("Rank").document(userId).update(KEY_LAST_CLAIMED_DATE,FieldValue.serverTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Nhận thưởng xếp hạng thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Lỗi khi nhận thưởng: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void updateRankings() {
        db.collection("Users").whereEqualTo("user_permission", "1").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    return;
                }
                if(value.size() != 0 && value != null)
                {
                    List<UserClass> userList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : value) {
                        UserClass user = document.toObject(UserClass.class);
                        userList.add(user);
                    }

                    Collections.sort(userList, new Comparator<UserClass>() {
                        @Override
                        public int compare(UserClass u1, UserClass u2) {
                            return Long.compare(u2.getUser_best_points(), u1.getUser_best_points());
                        }
                    });

                    for (int i = 0; i < userList.size(); i++) {
                        UserClass user = userList.get(i);
                        int rank = i + 1;
                        DocumentReference rankingRef = db.collection("Rank").document(user.getUser_uid());
                        Map map = new HashMap();
                        map.put(KEY_UID, user.getUser_uid());
                        map.put(KEY_USER_POINTS, user.getUser_best_points());
                        map.put(KEY_USER_RANK_POSITION, rank);
                        rankingRef.set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
//                                Log.d("RankingsUpdate", "Updated ranking for user: " + user.getUser_uid());
                            }
                        });
                    }
                }
            }
        });

    }

    public void enableClaimButton() {
        claimButton.setEnabled(true);
        claimButton.setBackgroundTintList(getResources().getColorStateList(R.color.mint, null));
        claimButton.setText("Nhận");
    }

    public void disableClaimButton() {
        claimButton.setEnabled(false);
        claimButton.setBackgroundTintList(getResources().getColorStateList(R.color.grey, null));
        claimButton.setText("Đã nhận");
    }
    private void mapping(){
        txtMyRank = (TextView) getView().findViewById(R.id.my_rank);
        txtMyPoints = (TextView) getView().findViewById(R.id.my_points);
        txtMyReward = (TextView) getView().findViewById(R.id.claimPoints);
        claimButton = (AppCompatButton) getView().findViewById(R.id.buttonClaimReward);
        recyclerRank = (RecyclerView) getView().findViewById(R.id.recycler_ranking);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(rankAdapter != null) rankAdapter.release();

    }

    @Override
    public void onResume() {
        super.onResume();

    }
}