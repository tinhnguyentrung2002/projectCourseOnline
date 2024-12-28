package com.example.courseonline.Fragment.Learner;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Activity.Learner.ClassActivity;
import com.example.courseonline.Activity.Learner.DashboardActivity;
import com.example.courseonline.Adapter.Learner.ClassAdapter;
import com.example.courseonline.Adapter.Learner.LearningAdapter;
import com.example.courseonline.Domain.ClassesClass;
import com.example.courseonline.Domain.CourseDisplayClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LearningFragment extends Fragment {
    private RecyclerView recyclerLearning, recyclerClass;
    private AppCompatEditText editCodeText;
    private TextView txtNone1, txtNameClass;
    private TextView txtNone;
    private Toast toast;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private AppCompatButton buttonStart, buttonJoin;
    private ArrayList<ClassesClass> arrayClass;
    private ArrayList<String> arrayID1;
    private ClassAdapter adapterRecycleView1;
    private ArrayList<CourseDisplayClass> arrayLearning;
    private ArrayList<String> arrayID;
    private LearningAdapter adapterRecycleView;
    private String uid;
    private ImageView imgArrowCourse, imgArrowClass;
    private boolean isCourseExpanded = true, isClassExpanded = true;
    private static final String KEY_OWN_CLASS_DATE = "own_course_date";
    private static final String KEY_OWN_CLASS_ID = "own_course_id";
    private static final String KEY_OWN_CLASS_ITEM_TYPE = "own_course_item_type";
    private static final String KEY_OWN_CLASS_ITEM_ID = "own_course_item_id";
    private static final String KEY_OWN_CLASS_ITEM_COMPLETE = "own_course_complete";
    private static final String KEY_OWN_CLASS_ITEM_PROGRESS = "own_course_progress";
    private static final String KEY_OWN_CLASS_ITEM_VIDEO_PROGRESS = "own_course_video_progress";
    private static final String KEY_OWN_CLASS_ITEM_EX_PROGRESS = "own_course_ex_progress";
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
        recyclerClass.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        recyclerClass.setHasFixedSize(true);
        recyclerClass.setItemViewCacheSize(20);


        if (savedInstanceState == null) {

        }
        loadData();

        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtNameClass.length() != 0 && !txtNameClass.getText().equals("Không tồn tại lớp này"))
                {
                    if(mAuth.getCurrentUser() != null)
                    {
                        db.collection("Courses").document(editCodeText.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isComplete()){
                                    List<String> member = (List<String>) task.getResult().get("course_member");
                                    boolean state = task.getResult().getBoolean("course_state");
                                    if(!state) {
                                        toastMes("Khóa học này đã kết thúc, không thể tham gia nữa");
                                    }else if(member.contains(mAuth.getCurrentUser().getUid())){
                                        toastMes("Bạn đã tham gia lớp này");
                                    }else{
                                        editCodeText.clearFocus();
                                        showDialog(txtNameClass.getText().toString(), editCodeText.getText().toString().trim());
                                    }
                                }
                            }
                        });
                    }

                }
                else {
                    toastMes("Mã lớp không hợp lệ");
                }


            }
        });
        editCodeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0)
                {
                    txtNameClass.setText("");
                    txtNameClass.setVisibility(View.GONE);
                }
                else{
                    txtNameClass.setVisibility(View.VISIBLE);
                    getClassById(editCodeText.getText().toString().trim());
                }



            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0)
                {
                    txtNameClass.setText("");
                    txtNameClass.setVisibility(View.GONE);
                }
            }
        });
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DashboardActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        imgArrowCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecyclerView(recyclerLearning, imgArrowCourse, isCourseExpanded);
                isCourseExpanded = !isCourseExpanded;
            }
        });

        imgArrowClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecyclerView(recyclerClass, imgArrowClass, isClassExpanded);
                isClassExpanded = !isClassExpanded;
            }
        });

    }
    private void toggleRecyclerView(RecyclerView recyclerView, ImageView arrowImageView, boolean isExpanded) {
        if (isExpanded) {
            animateRecyclerView(recyclerView, 0);
            arrowImageView.setImageResource(R.drawable.arrow_down_blue);
        } else {
            animateRecyclerView(recyclerView, 1);
            arrowImageView.setImageResource(R.drawable.arrow_up);
        }
    }

    private void animateRecyclerView(RecyclerView recyclerView, int action) {
        int height = recyclerView.getHeight();
        TranslateAnimation animation;
        if (action == 0) {
//            animation = new TranslateAnimation(0, 0, 0, -height);
//            animation.setDuration(300);
//            recyclerView.startAnimation(animation);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            animation = new TranslateAnimation(0, 0, height, 0);
            animation.setDuration(300);
            recyclerView.startAnimation(animation);
        }
    }
    private void loadData(){
        arrayID = new ArrayList<>();
        arrayLearning = new ArrayList<>();
        adapterRecycleView = new LearningAdapter(arrayLearning, getContext());
        recyclerLearning.setAdapter(adapterRecycleView);
        arrayID1 = new ArrayList<>();
        arrayClass = new ArrayList<>();
        adapterRecycleView1 = new ClassAdapter(arrayClass, getContext(), 1);
        recyclerClass.setAdapter(adapterRecycleView1);
        if(mAuth.getCurrentUser() != null) {
            db.collection("Users").document(uid).collection("OwnCourses").whereEqualTo("own_course_item_type", "course").whereEqualTo("own_course_complete", false).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                        return;
                    }
                    arrayID.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("own_course_item_id") != null) {
                            arrayID.add(doc.get("own_course_item_id").toString());

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

                                }

                                adapterRecycleView.notifyDataSetChanged();

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
            db.collection("Users").document(uid).collection("OwnCourses").whereEqualTo("own_course_item_type", "class").whereEqualTo("own_course_complete", false).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                        return;
                    }
                    arrayID1.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("own_course_item_id") != null) {
                            arrayID1.add(doc.get("own_course_item_id").toString());

                        }
                    }

                    if(arrayID1.size() != 0)
                    {
                        txtNone1.setVisibility(View.GONE);
                        recyclerClass.setVisibility(View.VISIBLE);
                        db.collection("Courses").whereIn("course_id",arrayID1).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error!=null)
                                {
                                    return;
                                }
                                arrayClass.clear();
                                for (QueryDocumentSnapshot doc : value) {
                                    if (doc.toObject(ClassesClass.class) != null) {
                                        arrayClass.add(doc.toObject(ClassesClass.class));
                                    }

                                }

                                adapterRecycleView1.notifyDataSetChanged();

                            }
                        });
                    }
                    else{
                        arrayClass.clear();
                        adapterRecycleView1.notifyDataSetChanged();
                        txtNone1.setVisibility(View.VISIBLE);
                        recyclerClass.setVisibility(View.GONE);
                    }

                }
            });
        }


    }
    private void showDialog(String className, String classID){
        LayoutInflater inflater = getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.dialog_join_class, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        TextView txtID = dialogView.findViewById(R.id.headerClassName);
        txtID.setText("Khóa học: " + className);

        EditText editTextCode = dialogView.findViewById(R.id.editClassCode);

        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String enteredCode = editTextCode.getText().toString();
                        if (!enteredCode.isEmpty()) {
                            db.collection("Courses").document(classID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isComplete())
                                    {
                                        if(task.getResult().exists()){
                                            if(task.getResult().getString("course_private_key").equals(editTextCode.getText().toString().trim())){
                                                confirmJoinClass(uid, classID);
                                            }else{
                                                toastMes("Mã ghi danh không đúng");
                                            }



                                        }else{
                                            toastMes("Có lỗi xảy ra, vui lòng thử lại");
                                        }
                                    }
                                }
                            });

                        }else {
                            toastMes("Bạn chưa nhập mã ghi danh");
                        }
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();

        Window window = dialog.getWindow();
        if (window != null) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.bg_state);
            window.setBackgroundDrawable(drawable);
        }
        dialog.show();

    }
    private void confirmJoinClass(String uid, String classID){
        if (mAuth.getCurrentUser() != null) {
            DocumentReference reference = db.collection("Users").document(uid).collection("OwnCourses").document();
            Map<Object, String> hashMap = new HashMap<>();
            hashMap.put(KEY_OWN_CLASS_ITEM_ID, classID);
            hashMap.put(KEY_OWN_CLASS_ID, reference.getId());

            reference.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    DocumentReference classRef = db.collection("Courses").document(classID);
                    classRef.update("course_member", FieldValue.arrayUnion(uid))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });

                    toastMes("Ghi danh thành công! Chúc bạn học thật tốt <3");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    toastMes("Có lỗi xảy ra !");
                }
            });

            DocumentReference userClassRef = db.collection("Users").document(uid).collection("OwnCourses").document(reference.getId());
            Map<String, Object> userClassData = new HashMap<>();
            userClassData.put(KEY_OWN_CLASS_ITEM_COMPLETE, false);
            userClassData.put(KEY_OWN_CLASS_ITEM_PROGRESS, "0.00");
            userClassData.put(KEY_OWN_CLASS_ITEM_TYPE, "class");
            userClassData.put(KEY_OWN_CLASS_ITEM_VIDEO_PROGRESS, "0.00");
            userClassData.put(KEY_OWN_CLASS_ITEM_EX_PROGRESS, "0.00");
            userClassData.put(KEY_OWN_CLASS_DATE, FieldValue.serverTimestamp());

            userClassRef.update(userClassData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    editCodeText.setText(null);
                    Intent intent = new Intent(getContext(), ClassActivity.class);
                    intent.putExtra("course_key", classID);
                    startActivity(intent);

                }
            });
        }

    }
    private void getClassById(String Id){
        db.collection("Courses").whereEqualTo("course_type", "class").whereEqualTo("course_id", Id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete()){
                    if(task.getResult().isEmpty()) {
                        txtNameClass.setText("Không tồn tại lớp này");
                        txtNameClass.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                        return;
                    }
                    for(DocumentSnapshot doc : task.getResult()){
                        txtNameClass.setText(doc.getString("course_title"));
                        txtNameClass.setTextColor(ContextCompat.getColor(getContext(), R.color.mint));
                    }

                }
            }
        });
    }
    private void mapping(){
        recyclerLearning = (RecyclerView) getView().findViewById(R.id.recyclerLearning);
        txtNone = (TextView) getView().findViewById(R.id.txtNone);
        buttonStart = (AppCompatButton) getView().findViewById(R.id.buttonStartLearn);

        recyclerClass = (RecyclerView) getView().findViewById(R.id.recyclerClass);
        txtNone1 = (TextView) getView().findViewById(R.id.txtEmptyClass);
        txtNameClass = (TextView) getView().findViewById(R.id.className);
        buttonJoin   = (AppCompatButton) getView().findViewById(R.id.buttonJoinClass);
        editCodeText = (AppCompatEditText) getView().findViewById(R.id.editCodeClass);
        imgArrowCourse = (ImageView) getView().findViewById(R.id.imgArrowCourse);
        imgArrowClass =(ImageView) getView().findViewById(R.id.imgArrowClass);
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

            recyclerClass.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
            recyclerClass.setHasFixedSize(true);
            recyclerClass.setItemViewCacheSize(20);
            recyclerClass.setAdapter(adapterRecycleView1);
        }
        else {
            recyclerLearning.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
            recyclerLearning.setHasFixedSize(true);
            recyclerLearning.setItemViewCacheSize(20);
            recyclerLearning.setAdapter(adapterRecycleView);

            recyclerClass.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
            recyclerClass.setHasFixedSize(true);
            recyclerClass.setItemViewCacheSize(20);
            recyclerClass.setAdapter(adapterRecycleView1);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(adapterRecycleView != null) adapterRecycleView.release();
        if(adapterRecycleView1 != null) adapterRecycleView1.release();
    }

    @Override
    public void onResume() {
        super.onResume();
//        loadData();
    }
}