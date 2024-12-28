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
import android.widget.EditText;
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
import com.example.courseonline.Adapter.Learner.ClassAdapter;
import com.example.courseonline.Domain.ClassesClass;
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
import java.util.Map;

public class ClassFragment extends Fragment {
    private RecyclerView recyclerClass;
    private AppCompatEditText editCodeText;
    private TextView txtNone, txtNameClass;
    private Toast toast;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private AppCompatButton buttonJoin;
    private ArrayList<ClassesClass> arrayClass;
    private ArrayList<String> arrayID;
    private ClassAdapter adapterRecycleView;
    private String uid;
    private static final String KEY_OWN_CLASS_DATE = "own_course_date";
    private static final String KEY_OWN_CLASS_ID = "own_course_id";
    private static final String KEY_OWN_COURSE_ITEM_TYPE = "own_course_item_type";
    private static final String KEY_OWN_CLASS_ITEM_ID = "own_course_item_id";
    private static final String KEY_OWN_CLASS_ITEM_COMPLETE = "own_course_complete";
    private static final String KEY_OWN_CLASS_ITEM_PROGRESS = "own_course_progress";
    private static final String KEY_OWN_CLASS_ITEM_VIDEO_PROGRESS = "own_course_video_progress";
    private static final String KEY_OWN_CLASS_ITEM_EX_PROGRESS = "own_course_ex_progress";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_class, container, false);
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
                showDialog(txtNameClass.getText().toString(), editCodeText.getText().toString().trim());
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
                }else{
                    getClassById(editCodeText.getText().toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
    private void showDialog(String className, String classID){
        LayoutInflater inflater = getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.dialog_join_class, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        TextView txtID = dialogView.findViewById(R.id.headerClassName);
        txtID.setText(className);

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
                                            toastMes("Join vào");

                                            dialog.dismiss();
                                        }else{
                                            toastMes("Mã ghi danh không đúng, vui lòng thử lại");
                                        }
                                    }
                                }
                            });

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
            Drawable drawable = getResources().getDrawable(R.drawable.rounded_corner);
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
                    classRef.update("class_member", FieldValue.arrayUnion(uid))
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

            DocumentReference userClassRef = db.collection("Users").document(uid).collection("OwnClasses").document(reference.getId());
            Map<String, Object> userClassData = new HashMap<>();
            userClassData.put(KEY_OWN_CLASS_ITEM_COMPLETE, false);
            userClassData.put(KEY_OWN_CLASS_ITEM_PROGRESS, "0.00");
            userClassData.put(KEY_OWN_CLASS_ITEM_VIDEO_PROGRESS, "0.00");
            userClassData.put(KEY_OWN_CLASS_ITEM_EX_PROGRESS, "0.00");
            userClassData.put(KEY_OWN_CLASS_DATE, FieldValue.serverTimestamp());

            userClassRef.update(userClassData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                   Intent intent = new Intent(getContext(), ClassActivity.class);

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
                        txtNameClass.setText(doc.getString("class_id"));
                        txtNameClass.setTextColor(ContextCompat.getColor(getContext(), R.color.mint));
                    }

                }
            }
        });
    }
    private void loadData(){
        arrayID = new ArrayList<>();
        arrayClass = new ArrayList<>();
        adapterRecycleView = new ClassAdapter(arrayClass,getContext(), 1);
        recyclerClass.setAdapter(adapterRecycleView);
        if(mAuth.getCurrentUser() != null) {
            db.collection("Users").document(uid).collection("OwnCourses").whereEqualTo("own_course_item_type", "class").whereEqualTo("own_course_complete", false).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        recyclerClass.setVisibility(View.VISIBLE);
                        buttonJoin.setVisibility(View.GONE);
                        db.collection("Courses").whereIn("course_id",arrayID).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                                adapterRecycleView.notifyDataSetChanged();

                            }
                        });
                    }
                    else{
                        arrayClass.clear();
                        adapterRecycleView.notifyDataSetChanged();
                        txtNone.setVisibility(View.VISIBLE);
                        recyclerClass.setVisibility(View.GONE);
                        buttonJoin.setVisibility(View.VISIBLE);
                    }

                }
            });

        }


    }
    private void mapping(){
        recyclerClass = (RecyclerView) getView().findViewById(R.id.recyclerClass);
        txtNone = (TextView) getView().findViewById(R.id.txtEmptyClass);
        txtNameClass = (TextView) getView().findViewById(R.id.className);
        buttonJoin   = (AppCompatButton) getView().findViewById(R.id.buttonJoinClass);
        editCodeText = (AppCompatEditText) getView().findViewById(R.id.editCodeClass);
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
            recyclerClass.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
            recyclerClass.setHasFixedSize(true);
            recyclerClass.setItemViewCacheSize(20);
            recyclerClass.setAdapter(adapterRecycleView);
        }
        else {
            recyclerClass.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
            recyclerClass.setHasFixedSize(true);
            recyclerClass.setItemViewCacheSize(20);
            recyclerClass.setAdapter(adapterRecycleView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(adapterRecycleView != null) adapterRecycleView.release();
    }

    @Override
    public void onResume() {
        super.onResume();
//        loadData();
    }
}