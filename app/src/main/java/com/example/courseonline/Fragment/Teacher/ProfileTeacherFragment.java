package com.example.courseonline.Fragment.Teacher;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.courseonline.Activity.IntroActivity;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateField;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class ProfileTeacherFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private String img = null;
    private Toast toast;

    // Views from the XML layout
    private ShapeableImageView imgAvatar;
    private TextView txtProfileName, txtProfileEmail, txtProfileUID, txtFacebookLink, txtYoutubeLink, txtAboutMe;
    private TextView txtCourseCount, txtClassCount, txtRating, txtTotalRating, txtStudentCount;
    private LinearLayout layoutFacebookContact, layoutYoutubeContact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        return inflater.inflate(R.layout.fragment_profile_teacher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapping();


        user = mAuth.getCurrentUser();

//        getView().setVisibility(View.GONE);

        if (user != null) {
            fetchData();
        }
    }

    private void fetchData() {
        DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null || value == null || !value.exists()) {
                    return;
                }

                String userName = value.getString("user_name");
                String userEmail = value.getString("user_email");
                String userUID = value.getString("user_uid");
                String avatarUrl = value.getString("user_avatar");

                txtProfileName.setText(userName != null ? userName : "N/A");
                txtProfileEmail.setText(userEmail != null ? userEmail : "N/A");
                txtProfileUID.setText("UID: " + (userUID != null ? userUID : "N/A"));

                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Picasso.get().load(avatarUrl).resize(100, 100).centerCrop().into(imgAvatar);
                } else {
                    imgAvatar.setImageResource(R.drawable.profile); // Default avatar
                }

                if (value.getString("user_facebook") != null && !value.getString("user_facebook").isEmpty()) {
                    txtFacebookLink.setText(value.getString("user_facebook"));
                } else {
                    txtFacebookLink.setText("Không có");
                }

                if (value.getString("user_youtube") != null && !value.getString("user_youtube").isEmpty()) {
                    txtYoutubeLink.setText(value.getString("user_youtube"));
                } else {
                    txtYoutubeLink.setText("Không có");
                }

                if (value.getString("user_about_me") != null && !value.getString("user_about_me").isEmpty()) {
                    txtAboutMe.setText(value.getString("user_about_me"));
                } else {
                    txtAboutMe.setText("Không có");
                }

//                updateCourseStats();
            }
        });
    }

    private void updateCourseStats() {
        Query courseQuery = db.collection("Courses").whereEqualTo("course_owner_id", mAuth.getCurrentUser().getUid());

        AggregateQuery aggregateQuery = courseQuery.aggregate(AggregateField.average("course_rate"));
        aggregateQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    AggregateQuerySnapshot snapshot = task.getResult();
                    txtRating.setText("Đánh giá: " + String.format("%.1f", snapshot.get(AggregateField.average("course_rate"))) + "/5");
                }
            }
        });

        AggregateQuery memberCountQuery = courseQuery.aggregate(AggregateField.sum("course_member"));
        memberCountQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    AggregateQuerySnapshot snapshot = task.getResult();
                    txtStudentCount.setText("Số lượng học viên: " + snapshot.get(AggregateField.sum("course_member")));
                }
            }
        });

        courseQuery.count().get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    AggregateQuerySnapshot snapshot = task.getResult();
                    txtCourseCount.setText("Số lượng khóa học: " + snapshot.getCount());
                }
            }
        });

        Query classQuery = db.collection("Courses").whereEqualTo("course_owner_id", mAuth.getCurrentUser().getUid()).whereEqualTo("course_type", "class");
        classQuery.count().get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    AggregateQuerySnapshot snapshot = task.getResult();
                    txtClassCount.setText("Số lượng lớp học: " + snapshot.getCount());
                }
            }
        });

        courseQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    final int[] totalRatings = {0};
                    for (DocumentSnapshot course : task.getResult()) {
                        CollectionReference commentsRef = course.getReference().collection("Comment");
                        commentsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    totalRatings[0] += task.getResult().size();
                                    txtTotalRating.setText("Tổng số đánh giá: " + totalRatings[0]);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void mapping() {
        imgAvatar = getView().findViewById(R.id.imgAvatarProfileTeacher);
        txtProfileName = getView().findViewById(R.id.txtProfileTeacherName);
        txtProfileEmail = getView().findViewById(R.id.txtProfileTeacherEmail);
        txtProfileUID = getView().findViewById(R.id.txtProfileTeacherUID);
        txtFacebookLink = getView().findViewById(R.id.txtTeacherFacebookLink);
        txtYoutubeLink = getView().findViewById(R.id.txtTeacherYoutubeLink);
//        txtCourseCount = getView().findViewById(R.id.txtTeacherCourseCount);
//        txtClassCount = getView().findViewById(R.id.txtTeacherClassCount);
//        txtRating = getView().findViewById(R.id.txtTeacherRating);
//        txtTotalRating = getView().findViewById(R.id.txtTeacherTotalRating);
//        txtStudentCount = getView().findViewById(R.id.txtTeacherStudentCount);
        txtAboutMe = getView().findViewById(R.id.txtTeacherAboutMe);
        layoutFacebookContact = getView().findViewById(R.id.layoutTeacherFacebookContact);
        layoutYoutubeContact = getView().findViewById(R.id.layoutTeacherYoutubeContact);
    }

    private void toastMes(String mes) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getActivity(), mes, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        userStatus();
    }

    private void userStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(getActivity(), IntroActivity.class));
            getActivity().finish();
        }
    }
}
