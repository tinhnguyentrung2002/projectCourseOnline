package com.example.courseonline.Dialog.Learner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

public class TeacherDialog extends BottomSheetDialog {

    private final Context context;
    private final FirebaseFirestore db;
    private String teacherId;
    private View view;
    private ImageView imgAvatar, imgClose;
    private TextView txtProfileName, txtProfileEmail, txtProfileUID, txtFacebookLink, txtYoutubeLink;
    private TextView txtCourseCount, txtClassCount, txtRating, txtTotalRating, txtStudentCount, txtAboutMe;
    private LinearLayout layoutYoutubeContact, layoutFacebookContact;

    public TeacherDialog(@NonNull Context context, String teacherId) {
        super(context);
        this.context = context;
        this.teacherId = teacherId;
        db = FirebaseFirestore.getInstance();
        setupDialog();
    }

    private void setupDialog() {
        view = LayoutInflater.from(context).inflate(R.layout.dialog_teacher_profile, null);
        setContentView(view);
        mapping();
        fetchData();
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void mapping() {
        imgAvatar = view.findViewById(R.id.imgAvatarDialog);
        txtProfileName = view.findViewById(R.id.txtProfileNameDialog);
        txtProfileEmail = view.findViewById(R.id.txtProfileEmailDialog);
        txtProfileUID = view.findViewById(R.id.txtProfileUIDDialog);
        txtFacebookLink = view.findViewById(R.id.txtFacebookLink);
        txtYoutubeLink = view.findViewById(R.id.txtYoutubeLink);
        imgClose = view.findViewById(R.id.closeUserProfileDialog);
        txtCourseCount = view.findViewById(R.id.txtCourseCount);
        txtClassCount = view.findViewById(R.id.txtClassCount);
        txtRating = view.findViewById(R.id.txtRating);
        txtTotalRating = view.findViewById(R.id.txtTotalRating);
        txtStudentCount = view.findViewById(R.id.txtStudentCount);
        txtAboutMe = view.findViewById(R.id.txtAboutMe);
        layoutFacebookContact = view.findViewById(R.id.layoutFacebookContact);
        layoutYoutubeContact = view.findViewById(R.id.layoutYoutubeContact);
    }

    private void fetchData() {
        DocumentReference documentReference = db.collection("Users").document(teacherId);
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
                    imgAvatar.setImageResource(R.drawable.profile);
                }

                if (value.getString("user_facebook") != "") {
                    txtFacebookLink.setText(value.getString("user_facebook"));
                } else {
                    txtFacebookLink.setText("Không có");
                }

                if (value.getString("user_youtube") != "") {
                    txtYoutubeLink.setText(value.getString("user_youtube"));
                } else {
                    txtYoutubeLink.setText("Không có");
                }
                if (value.getString("user_about_me") != "") {
                    txtAboutMe.setText(value.getString("user_about_me"));
                }else{
                    txtAboutMe.setText("Không có");
                }

                updateCourseStats();
            }
        });
    }

    private void updateCourseStats() {
        Query courseQuery = db.collection("Courses").whereEqualTo("course_owner_id", teacherId).whereEqualTo("course_type", "course");

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

        Query classQuery = db.collection("Courses").whereEqualTo("course_owner_id", teacherId).whereEqualTo("course_type", "class");
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

}
