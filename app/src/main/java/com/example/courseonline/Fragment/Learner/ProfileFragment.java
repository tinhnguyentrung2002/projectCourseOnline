package com.example.courseonline.Fragment.Learner;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.courseonline.Activity.IntroActivity;
import com.example.courseonline.Adapter.Learner.ViewPage2AdapterProfile;
import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private String storagePath = "Users/Avatars/";
    private Toast toast;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPage2AdapterProfile viewPage2AdapterProfile;
    private String img = null;
    private TextView txtProfileName, txtProfileEmail, txtProfileUID, txtAll, txtLearn, txtComplete;
    private ShapeableImageView imgAvatar, imgEdit;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int STORAGE_REQUEST_CODE = 102;
    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 103;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 104;
    String cameraPermissions[];
    Uri img_uri;
    //final Fragment fragment1 = new AllCourseFragment();
   // final Fragment fragment2 = new FavouriteFragment();
    //Fragment active;
    private SwipeRefreshLayout refresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //tab
        mapping();
        viewPage2AdapterProfile = new ViewPage2AdapterProfile(getActivity());
        viewPager2.setAdapter(viewPage2AdapterProfile);
        viewPager2.setOffscreenPageLimit(1);
        LoadingAlert alert = new LoadingAlert(getActivity());
        alert.startLoading();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
              //  FragmentManager fm = getActivity().getSupportFragmentManager();
              //  FragmentTransaction ft = fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

               // ft.add(R.id.frameLayout, fragment1, "1").commit();
               // ft.add(R.id.frameLayout, fragment2, "2").commit();
               // ft.replace(R.id.frameLayout, fragment1, "1").commit();
                //ft.hide(fragment2).show(fragment1).commit();

                user = mAuth.getCurrentUser();
                tabLayout.getTabAt(0).select();
                getView().setVisibility(View.GONE);
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager2.setCurrentItem(tab.getPosition());
                    }
                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }
                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
                //permissions
                cameraPermissions = new String[]{android.Manifest.permission.CAMERA};
                //process
                if(user != null)
                {

                    DocumentReference documentReference = db.collection("Users").document(user.getUid());
                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(error!=null)
                            {
                                return;
                            }
                            txtProfileName.setText(value.getString("user_name"));
                            txtProfileEmail.setText(value.getString("user_email"));
                            txtProfileUID.setText("UID: " + value.getString("user_uid"));
                            img = value.getString("user_avatar");
                            try {
                                Picasso.get().load(img).resize(100,100).centerCrop().into(imgAvatar);
                            }catch (Exception e)
                            {
                                Picasso.get().load(R.drawable.profile).resize(100,100).centerInside().into(imgAvatar);
                            }
                        }
                    });
                    Query query = db.collection("Users").document(user.getUid()).collection("cart");
                    AggregateQuery countQuery = query.count();
                    countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                AggregateQuerySnapshot snapshot = task.getResult();
                                txtAll.setText(String.valueOf(snapshot.getCount()));
                            } else {
                                toastMes("Error");
                            }
                        }
                    });
                    Query query1 = db.collection("Users").document(user.getUid()).collection("cart").whereEqualTo("cart_complete", true);
                    AggregateQuery countQuery1 = query1.count();
                    countQuery1.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                AggregateQuerySnapshot snapshot = task.getResult();
                                txtComplete.setText(String.valueOf(snapshot.getCount()));
                            } else {
                                toastMes("Error");
                            }
                        }
                    });
                    Query query2 = db.collection("Users").document(user.getUid()).collection("cart").whereEqualTo("cart_complete", false);
                    AggregateQuery countQuery2 = query2.count();
                    countQuery2.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                AggregateQuerySnapshot snapshot = task.getResult();
                                txtLearn.setText(String.valueOf(snapshot.getCount()));
                            } else {
                                toastMes("Error");
                            }

                        }
                    });
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread work here
                    }
                });
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getView().setVisibility(View.VISIBLE);
                        imgAvatar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showEditAvatar();
                            }
                        });
                        imgEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showEdit();
                            }
                        });
                        refresh.setOnRefreshListener(() -> {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(user != null)
                                            {
                                                DocumentReference documentReference = db.collection("Users").document(user.getUid());
                                                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                        if(error!=null)
                                                        {
                                                            return;
                                                        }
                                                        txtProfileName.setText(value.getString("user_name"));
                                                        txtProfileEmail.setText(value.getString("user_email"));
                                                        txtProfileUID.setText("UID: " + value.getString("user_uid"));
                                                        img = value.getString("user_avatar");
                                                        try {
                                                            Picasso.get().load(img).resize(100,100).centerCrop().into(imgAvatar);
                                                        }catch (Exception e)
                                                        {
                                                            Picasso.get().load(R.drawable.profile).centerInside().into(imgAvatar);
                                                        }
                                                    }
                                                });
                                                Query query = db.collection("Users").document(user.getUid()).collection("cart");
                                                AggregateQuery countQuery = query.count();
                                                countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            AggregateQuerySnapshot snapshot = task.getResult();
                                                            txtAll.setText(String.valueOf(snapshot.getCount()));
                                                        } else {
                                                            toastMes("Error");
                                                        }
                                                    }
                                                });
                                                Query query1 = db.collection("Users").document(user.getUid()).collection("cart").whereEqualTo("cart_complete", true);
                                                AggregateQuery countQuery1 = query1.count();
                                                countQuery1.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            AggregateQuerySnapshot snapshot = task.getResult();
                                                            txtComplete.setText(String.valueOf(snapshot.getCount()));
                                                        } else {
                                                            toastMes("Error");
                                                        }
                                                    }
                                                });
                                                Query query2 = db.collection("Users").document(user.getUid()).collection("cart").whereEqualTo("cart_complete", false);
                                                AggregateQuery countQuery2 = query2.count();
                                                countQuery2.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            AggregateQuerySnapshot snapshot = task.getResult();
                                                            txtLearn.setText(String.valueOf(snapshot.getCount()));
                                                        } else {
                                                            toastMes("Error");
                                                        }

                                                    }
                                                });
                                            }

                                            refresh.setRefreshing(false);
                                        }
                                    }, 500);
                                }
                        );
                        alert.closeLoading();
                    }
                }, 500);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        userStatus();
    }
    private void userStatus(){
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
        }
        else{
            startActivity(new Intent(getActivity(), IntroActivity.class));
            getActivity().finish();
        }
    }
    /*private boolean checkStoragePermission(){
        boolean result = ContextCompat
                .checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }*/
    /*private void requestStoragePermission(){
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }*/
    private boolean checkCameraPermission(){
        boolean result = ContextCompat
                .checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestCameraPermission(){
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length > 0){
                    toastMes("Đang mở camera");
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted){
                        openCamera();
                    }else{
                        //toastMes("Lỗi");
                    }
                }
                break;
            }
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length > 0){
                    toastMes("Đang mở thư viện");
                        openGallery();
                    }else{
                        toastMes("Lỗi");
                }
                break;
            }
        }
    }
    private void openCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Img");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        img_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_OUTPUT, img_uri);
        startActivityForResult(camera, IMAGE_PICK_CAMERA_REQUEST_CODE);
    }
    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        startActivityForResult(gallery, IMAGE_PICK_GALLERY_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE){
                img_uri = data.getData();
                uploadAvatar(img_uri);
            }
            if(requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE){
                uploadAvatar(img_uri);
            }
        }
    }
    private void uploadAvatar(Uri imgUri) {
        LoadingAlert loadingAlert = new LoadingAlert(getActivity());
        loadingAlert.startLoading();
        String fileName = storagePath +" avatar_" + user.getUid();
        StorageReference storageReference1 = storageReference.child(fileName);
        storageReference1.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri download = uriTask.getResult();
                if(uriTask.isSuccessful()){
                    Map<String, Object> results = new HashMap<>();
                    results.put("user_avatar", download.toString());
                    db.collection("Users").document(user.getUid()).update(results).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loadingAlert.closeLoading();
                            toastMes("Cập nhật ảnh đại diện thành công!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingAlert.closeLoading();
                            toastMes("Cập nhật ảnh đại diện thất bại!");
                        }
                    });
                }else{
                    loadingAlert.closeLoading();
                    toastMes("Có lỗi xảy ra");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingAlert.closeLoading();
                toastMes(e.getMessage());
            }
        });
    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(getActivity(),mes,Toast.LENGTH_SHORT);
        toast.show();
    }
    private void showEditAvatar(){
        String options[] = { "Camera","Thư viện"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Đổi ảnh đại diện");
        builder.setItems(options,(dialogInterface, i) -> {
            if(i == 0)
            {
                if(!checkCameraPermission())
                {
                    requestCameraPermission();
                }else{
                    openCamera();
                }
            }else if(i == 1){
                    openGallery();

            }
        });

        builder.create().show();
    }
    private void showEdit(){
        LoadingAlert loadingAlert = new LoadingAlert(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Đổi tên người dùng");
        LinearLayout layout = new LinearLayout(getActivity());
        EditText editText = new EditText(getActivity());
        editText.setHint("Nhập tên mới");
        editText.setBackgroundResource(R.drawable.edit_login);
        editText.setMinEms(16);
        editText.setPadding(25,20,20,20);
        layout.addView(editText);
        layout.setPadding(15,15,15,15);
        builder.setView(layout);
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = editText.getText().toString().trim();
                if(!TextUtils.isEmpty(name))
                {
                    loadingAlert.startLoading();
                    Map<String, Object> result  = new HashMap<>();
                    result.put("user_name", name);
                    DocumentReference documentReference = db.collection("Users").document(user.getUid());
                    documentReference.update(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            loadingAlert.closeLoading();
                            toastMes("Cập nhật tên thành công !");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingAlert.closeLoading();
                            toastMes(e.getMessage());
                        }
                    });

                }
                else{
                    toastMes("Bạn chưa nhập tên kìa !");
                }
            }
        });
        builder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }
    private void mapping(){
        txtProfileName = (TextView) getView().findViewById(R.id.txtProfileName);
        txtProfileEmail = (TextView) getView().findViewById(R.id.txtProfileEmail);
        txtProfileUID = (TextView) getView().findViewById(R.id.txtProfileUID);
        txtAll = (TextView) getView().findViewById(R.id.txtAll);
        txtComplete = (TextView) getView().findViewById(R.id.txtComplete);
        txtLearn = (TextView) getView().findViewById(R.id.txtLearn);
        imgAvatar = (ShapeableImageView) getView().findViewById(R.id.imgAvatar);
        imgEdit = (ShapeableImageView) getView().findViewById(R.id.imageEdit);
        tabLayout = (TabLayout) getView().findViewById(R.id.tabLayout);
        refresh = (SwipeRefreshLayout) getView().findViewById(R.id.refresh1);
        viewPager2 = (ViewPager2) getView().findViewById(R.id.viewPager2Profile);
    }
}