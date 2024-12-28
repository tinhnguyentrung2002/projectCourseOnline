package com.example.courseonline.Dialog.Learner;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Learner.CommentAdapter;
import com.example.courseonline.Domain.CommentClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateField;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommentDialog extends BottomSheetDialogFragment {
    public DocumentSnapshot lastVisible;
    private ProgressBar progressLoad;
    private String course_id;
    private TextView txtAvgRate, txtTotalComment, txtNoneComment;
    private RatingBar ratingAvgRate;
    private ProgressBar progress, progress1, progress2, progress3, progress4;
    private AppCompatButton btn1, btn2, btn3, btn4, btn5, btnAll;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView  recyclerMyComment;
    private ArrayList<CommentClass> userComment, otherComments, arrayComment;
    private CommentAdapter commentAdapter;
    private LinearLayout layoutComment;
    public CommentDialog(String course_id) {
        this.course_id = course_id;
    }
    private int clickedID;
    public ExecutorService executor;
    public Handler handler;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_display_comment, null);

        bottomSheetDialog.setContentView(view);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        layoutComment = (LinearLayout) bottomSheetDialog.findViewById(R.id.LayoutComment);
        layoutComment.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
        txtAvgRate = (TextView) bottomSheetDialog.findViewById(R.id.textAVGRate);
        txtTotalComment = (TextView) bottomSheetDialog.findViewById(R.id.txtTotalComment);
        txtNoneComment = (TextView) bottomSheetDialog.findViewById(R.id.txtNoneComment);
        recyclerMyComment = (RecyclerView) bottomSheetDialog.findViewById(R.id.recyclerMyComment);
        ratingAvgRate = (RatingBar) bottomSheetDialog.findViewById(R.id.ratingAVGRate);
//        progressLoad = (ProgressBar) bottomSheetDialog.findViewById(R.id.loadMore);
        progress = (ProgressBar) bottomSheetDialog.findViewById(R.id.progress);
        progress1 = (ProgressBar) bottomSheetDialog.findViewById(R.id.progress1);
        progress2 = (ProgressBar) bottomSheetDialog.findViewById(R.id.progress2);
        progress3 = (ProgressBar) bottomSheetDialog.findViewById(R.id.progress3);
        progress4 = (ProgressBar) bottomSheetDialog.findViewById(R.id.progress4);
        btnAll = (AppCompatButton) bottomSheetDialog.findViewById(R.id.btnAll);
        btn1 = (AppCompatButton) bottomSheetDialog.findViewById(R.id.btn1Star);
        btn2 = (AppCompatButton) bottomSheetDialog.findViewById(R.id.btn2Star);
        btn3 = (AppCompatButton) bottomSheetDialog.findViewById(R.id.btn3Star);
        btn4 = (AppCompatButton) bottomSheetDialog.findViewById(R.id.btn4Star);
        btn5 = (AppCompatButton) bottomSheetDialog.findViewById(R.id.btn5Star);

        recyclerMyComment.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerMyComment.setHasFixedSize(true);
        recyclerMyComment.setItemViewCacheSize(20);

        arrayComment = new ArrayList<>();
        userComment = new ArrayList<>();
        otherComments = new ArrayList<>();
        commentAdapter = new CommentAdapter(arrayComment,getContext());

        recyclerMyComment.setAdapter(commentAdapter);

        loadData();
//        btnAll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                loadData();
//                btn1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn3.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn4.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn5.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//
//            }
//        });
//
//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                btn1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_enable, null));
//                btn2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn3.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn4.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn5.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                filter(1);
//            }
//        });
//        btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                btn2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_enable, null));
//                btn1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn3.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn4.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn5.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                filter(2);
//            }
//        });
//        btn3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                btn3.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_enable, null));
//                btn2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn4.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn5.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                filter(3);
//            }
//        });
//        btn4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                btn4.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_enable, null));
//                btn2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn3.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn5.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                filter(4);
//            }
//        });
//        btn5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                btn5.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_enable, null));
//                btn2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn3.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn4.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                btn1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
//                filter(5);
//            }
//        });
        btnAll.setOnClickListener(this::onClick);
        btn1.setOnClickListener(this::onClick);
        btn2.setOnClickListener(this::onClick);
        btn3.setOnClickListener(this::onClick);
        btn4.setOnClickListener(this::onClick);
        btn5.setOnClickListener(this::onClick);


        return bottomSheetDialog;
    }
    public void onClick(View view){
        btn1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
        btn2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
        btn3.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
        btn4.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
        btn5.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));
        btnAll.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_disable, null));

        AppCompatButton clickedBtn = (AppCompatButton) view;
        clickedBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_enable, null));
        if(clickedBtn.getId() == btn1.getId()) filter(1);
        if(clickedBtn.getId() == btn2.getId()) filter(2);
        if(clickedBtn.getId() == btn3.getId()) filter(3);
        if(clickedBtn.getId() == btn4.getId()) filter(4);
        if(clickedBtn.getId() == btn5.getId()) filter(5);
        if(clickedBtn.getId() == btnAll.getId()) loadData();
    }
    private void loadData(){
//        recyclerMyComment.clearOnScrollListeners();
        if(mAuth.getCurrentUser() != null)
        {
            db.collection("Courses").document(course_id).collection("Comment").orderBy("comment_upload_time", Query.Direction.DESCENDING).whereEqualTo("user_id", mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        return;
                    }
                    userComment.clear();
                    if(value.size() != 0)
                    {
                        for (QueryDocumentSnapshot doc : value)
                        {
                            userComment.add(doc.toObject(CommentClass.class));

                        }
                    }
                    loadComment();
                }
            });
            db.collection("Courses").document(course_id).collection("Comment").orderBy("comment_upload_time", Query.Direction.DESCENDING).whereNotEqualTo("user_id", mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        return;
                    }

                    otherComments.clear();
                    if (value.size() != 0) {
                        for (QueryDocumentSnapshot doc : value) {
                            otherComments.add(doc.toObject(CommentClass.class));
                        }
                            lastVisible = value.getDocuments().get(value.size()-1);
                    }
                    loadComment();

//                            recyclerMyComment.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                                @Override
//                                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                                    super.onScrollStateChanged(recyclerView, newState);
////                                    if(recyclerView.canScrollVertically(1))
////                                    {
////                                        txtNone4.setVisibility(View.VISIBLE);
////                                    }
//                                    if(!recyclerView.canScrollVertically(1))
//                                    {
//                                        executor.execute(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                handler.post(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        progressLoad.setVisibility(View.VISIBLE);
//
//                                                    }
//                                                });
//                                                handler.postDelayed(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        progressLoad.setVisibility(View.GONE);
//                                                        db.collection("Courses").document(course_id).collection("Comment").orderBy("comment_upload_time", Query.Direction.DESCENDING).whereNotEqualTo("user_id", mAuth.getCurrentUser().getUid()).startAfter(lastVisible).limit(5).addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                                            @Override
//                                                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                                                                if (error != null) {
//                                                                    return;
//                                                                }
//                                                                //arrayComment.clear();
//                                                                if (value.size() != 0) {
//                                                                    for (QueryDocumentSnapshot doc : value) {
//                                                                        if(!doc.getString("user_id").equals(lastVisible.getString("user_id")))
//                                                                            arrayComment.add(doc.toObject(CommentClass.class));
//
//                                                                    }
//                                                                    lastVisible = value.getDocuments().get(value.size()-1);
//                                                                    commentAdapter.notifyDataSetChanged();
//
//                                                                }
//                                                            }
//                                                        });
//                                                    }
//                                                },1000);
//                                            }
//                                        });
//
//
//                                    }
//                                }
//
//                                @Override
//                                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                                    super.onScrolled(recyclerView, dx, dy);
//                                }
//                            });
                }
            });

            Query query = db.collection("Courses").document(course_id).collection("Comment");
            AggregateQuery aggregateQuery = query.aggregate(AggregateField.average("comment_rate"));
            aggregateQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        AggregateQuerySnapshot snapshot = task.getResult();
                        txtAvgRate.setText(String.format("%.1f",snapshot.get(AggregateField.average("comment_rate"))));
                        ratingAvgRate.setRating(Float.parseFloat(String.valueOf(snapshot.get(AggregateField.average("comment_rate")))));
                    }
                }
            });
            Query query1 = db.collection("Courses").document(course_id).collection("Comment").whereEqualTo("comment_rate", 5);
            AggregateQuery aggregateQuery1 = query1.count();
            aggregateQuery1.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        AggregateQuerySnapshot snapshot = task.getResult();
                        progress.setProgress(Integer.parseInt(String.valueOf(snapshot.getCount())));
                        progress.setTooltipText(String.valueOf(snapshot.getCount()));
                    }
                }
            });
            Query query2 = db.collection("Courses").document(course_id).collection("Comment").whereEqualTo("comment_rate", 4);
            AggregateQuery aggregateQuery2 = query2.count();
            aggregateQuery2.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        AggregateQuerySnapshot snapshot = task.getResult();
                        progress1.setProgress(Integer.parseInt(String.valueOf(snapshot.getCount())));
                        progress1.setTooltipText(String.valueOf(snapshot.getCount()));
                    }
                }
            });
            Query query3 = db.collection("Courses").document(course_id).collection("Comment").whereEqualTo("comment_rate", 3);
            AggregateQuery aggregateQuery3 = query3.count();
            aggregateQuery3.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        AggregateQuerySnapshot snapshot = task.getResult();
                        progress2.setProgress(Integer.parseInt(String.valueOf(snapshot.getCount())));
                        progress2.setTooltipText(String.valueOf(snapshot.getCount()));
                    }
                }
            });
            Query query4 = db.collection("Courses").document(course_id).collection("Comment").whereEqualTo("comment_rate", 2);
            AggregateQuery aggregateQuery4 = query4.count();
            aggregateQuery4.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        AggregateQuerySnapshot snapshot = task.getResult();
                        progress3.setProgress(Integer.parseInt(String.valueOf(snapshot.getCount())));
                        progress3.setTooltipText(String.valueOf(snapshot.getCount()));
                    }
                }
            });
            Query query5 = db.collection("Courses").document(course_id).collection("Comment").whereEqualTo("comment_rate", 1);
            AggregateQuery aggregateQuery5 = query5.count();
            aggregateQuery5.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        AggregateQuerySnapshot snapshot = task.getResult();
                        progress4.setProgress(Integer.parseInt(String.valueOf(snapshot.getCount())));
                        progress4.setTooltipText(String.valueOf(snapshot.getCount()));
                    }
                }
            });
            Query query6 = db.collection("Courses").document(course_id).collection("Comment");
            AggregateQuery aggregateQuery6 = query6.count();
            aggregateQuery6.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        AggregateQuerySnapshot snapshot = task.getResult();
                        txtTotalComment.setText(String.valueOf(snapshot.getCount()));
                    }
                }
            });
        }
    }

    private void filter(int star)
    {
//        txtNone4.setVisibility(View.GONE);

//        recyclerMyComment.clearOnScrollListeners();
        db.collection("Courses").document(course_id).collection("Comment").orderBy("comment_upload_time", Query.Direction.DESCENDING).whereEqualTo("user_id", mAuth.getCurrentUser().getUid()).whereEqualTo("comment_rate", star).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                userComment.clear();
                if(value != null)
                {
                    for (QueryDocumentSnapshot doc : value)
                    {
                        userComment.add(doc.toObject(CommentClass.class));
                    }
                }
                loadComment();

            }
        });
        db.collection("Courses").document(course_id).collection("Comment").orderBy("comment_upload_time", Query.Direction.DESCENDING).whereNotEqualTo("user_id", mAuth.getCurrentUser().getUid()).whereEqualTo("comment_rate", star).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                otherComments.clear();
                if (value.size() != 0) {
                    for (QueryDocumentSnapshot doc : value) {
                        otherComments.add(doc.toObject(CommentClass.class));
                    }
//                            lastVisible = value.getDocuments().get(value.size()-1);
                }
                loadComment();

//                        recyclerMyComment.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                            @Override
//                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                                super.onScrollStateChanged(recyclerView, newState);
////                                if(recyclerView.canScrollVertically(1))
////                                {
////                                    txtNone4.setVisibility(View.VISIBLE);
////                                }
//                                if(!recyclerView.canScrollVertically(1))
//                                {
//                                    executor.execute(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            handler.post(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    progressLoad.setVisibility(View.VISIBLE);
//
//                                                }
//                                            });
//                                            handler.postDelayed(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    progressLoad.setVisibility(View.GONE);
//                                                    db.collection("Courses").document(course_id).collection("Comment").orderBy("comment_upload_time", Query.Direction.DESCENDING).whereNotEqualTo("user_id", mAuth.getCurrentUser().getUid()).whereEqualTo("comment_rate", star).startAfter(lastVisible).limit(5).addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                                        @Override
//                                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                                                            if (error != null) {
//                                                                return;
//                                                            }
//                                                            //arrayComment.clear();
//                                                            if (value.size() != 0) {
//                                                                for (QueryDocumentSnapshot doc : value) {
//                                                                    if(!doc.getString("user_id").equals(lastVisible.getString("user_id")))
//                                                                        arrayComment.add(doc.toObject(CommentClass.class));
//
//                                                                }
//                                                                lastVisible = value.getDocuments().get(value.size()-1);
//                                                                commentAdapter.notifyDataSetChanged();
//
//                                                            }
//                                                        }
//                                                    });
//                                                }
//                                            },1000);
//                                        }
//                                    });
//
//
//                                }
//                            }
//
//                            @Override
//                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                                super.onScrolled(recyclerView, dx, dy);
//                            }
//                        });

            }
        });
    }
    private void loadComment(){
        arrayComment.clear();
        arrayComment.addAll(userComment);
        arrayComment.addAll(otherComments);
        if(arrayComment.size() != 0 )
        {
            txtNoneComment.setVisibility(View.GONE);
        }else{
            txtNoneComment.setVisibility(View.VISIBLE);
        }
        commentAdapter.notifyDataSetChanged();
    }
    public String changeValue(Number number) {
        char[] suffix = {' ', 'K', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(commentAdapter != null ) commentAdapter.release();
    }
}