package com.example.courseonline.Dialog.Learner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.courseonline.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class ProfileDialog extends BottomSheetDialog {

    private final Context context;
    private final FirebaseFirestore db;
    private final String userId;
    private TextView txtProfileName;
    private TextView txtProfileEmail;
    private TextView txtProfileUID;
    private ImageView imgAvatar;
    private ImageView iconLevel;
    private ImageView imgClose;

    public ProfileDialog(@NonNull Context context, String userId) {
        super(context);
        this.context = context;
        this.userId = userId;
        db = FirebaseFirestore.getInstance();
        setupDialog();
    }

    private void setupDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_user_profile, null);
        setContentView(view);

        mapping(view);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        DocumentReference documentReference = db.collection("Users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null || value == null || !value.exists()) {
                    return;
                }

                String userName = value.getString("user_name");
                String userEmail = value.getString("user_email");
                String userUID = value.getString("user_uid");
                String userLevelString = value.getString("user_level");

                txtProfileName.setText(userName != null ? userName : "N/A");
                txtProfileEmail.setText(userEmail != null ? userEmail : "N/A");
                txtProfileUID.setText("UID: " + (userUID != null ? userUID : "N/A"));

                int currentLevel = userLevelString != null ? Integer.parseInt(userLevelString) : 1;
                updateProfileLevel(iconLevel, currentLevel);

                String img = value.getString("user_avatar");
                if (img != null && !img.isEmpty()) {
                    Picasso.get().load(img).resize(100, 100).centerCrop().into(imgAvatar);
                } else {
                    imgAvatar.setImageResource(R.drawable.profile);
                }

                updateCoursesCount(userId);
            }
        });
    }
    private void mapping(View view) {
        txtProfileName = view.findViewById(R.id.txtProfileNameDialog);
        txtProfileEmail = view.findViewById(R.id.txtProfileEmailDialog);
        txtProfileUID = view.findViewById(R.id.txtProfileUIDDialog);
        imgAvatar = view.findViewById(R.id.imgAvatarDialog);
        imgClose = view.findViewById(R.id.closeUserProfileDialog);
        iconLevel = view.findViewById(R.id.imageLevelDialog);
    }

    private void updateProfileLevel(ImageView iconLevel, int currentLevel) {
        switch (currentLevel) {
            case 1:
                iconLevel.setImageResource(R.drawable.level_1);
                break;
            case 2:
                iconLevel.setImageResource(R.drawable.level_2);
                break;
            case 3:
                iconLevel.setImageResource(R.drawable.level_3);
                break;
            case 4:
                iconLevel.setImageResource(R.drawable.level_4);
                break;
            case 5:
                iconLevel.setImageResource(R.drawable.level_5);
                break;
            default:
                iconLevel.setImageResource(R.drawable.error);
                break;
        }
    }

    private void updateCoursesCount(String userId) {
        Query query = db.collection("Users").document(userId).collection("OwnCourses");

        query.count().get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AggregateQuerySnapshot snapshot = task.getResult();
                TextView txtAll = findViewById(R.id.profileTotal);
                txtAll.setText("Tổng: " + snapshot.getCount());
            }
        });

        query.whereEqualTo("own_course_complete", true).count().get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AggregateQuerySnapshot snapshot = task.getResult();
                TextView txtComplete = findViewById(R.id.profileComplete);
                txtComplete.setText("Hoàn thành: " + snapshot.getCount());
            }
        });
        query.whereEqualTo("own_course_complete", false).count().get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AggregateQuerySnapshot snapshot = task.getResult();
                TextView txtLearn = findViewById(R.id.profileLearning);
                txtLearn.setText("Đang học: " + snapshot.getCount());
            }
        });
    }
}
