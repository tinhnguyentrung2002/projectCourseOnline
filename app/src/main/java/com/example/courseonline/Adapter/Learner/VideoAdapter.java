package com.example.courseonline.Adapter.Learner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Activity.Learner.VideoActivity;
import com.example.courseonline.Domain.VideoClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>{
    ArrayList<VideoClass> items;
    Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Toast toast;
    private String key;
    public static int totalVideo;
    private int checkedVideo;
    public int i;

    private ArrayList<String> arrID = new ArrayList<>();
    private static final String KEY_UID = "user_id";
    private static final String KEY_VID = "video_id";
    private static final String KEY_CID = "course_id";
    private static final String KEY_PROGRESS_COURSE = "own_course_progress";
    private static final String KEY_PROGRESS_VIDEO = "own_course_video_progress";
    private static final String KEY_COMPLETE_COURSE = "own_course_complete";
    private static final String KEY_USER_POINTS = "user_points";
    public VideoAdapter(ArrayList<VideoClass> items, String key, Context context) {
        this.items = items;
        this.key = key;
        this.context = context;
        arrID.clear();
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_layout_video,parent,false);
        context = parent.getContext();
        for(VideoClass video: items)
        {
            arrID.add(video.getVideo_id());
        }

        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        String url_id = items.get(position).getVideo_url();
        String course_id = key;
        String id = items.get(position).getVideo_id();
        holder.txtVideoTitle.setText(items.get(position).getVideo_title());
       // holder.txtDuration.setText(items.get(position).getVideo_duration());
        db.collection("Users").document(uid).collection("CheckVideo").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().getData() != null){
                    holder.imgCheck.setImageResource(R.drawable.checked);
                }else{
                    holder.imgCheck.setImageResource(R.drawable.unchecked);
                }
            }
        });
        holder.video_constraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.video_constraint.setEnabled(false);
                db.collection("Users").document(uid).collection("CheckVideo").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().getData() != null){
//                            Query query = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckVideo").whereEqualTo("course_id", key);
//                            AggregateQuery aggregateQuery = query.count();
//                            aggregateQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
//                                    if (task.isSuccessful()) {
//                                        totalVideo = 0;
//                                        checkedVideo = 0;
//                                        AggregateQuerySnapshot snapshot = task.getResult();
//                                        checkedVideo = (int)snapshot.getCount();
//
//                                        db.collection("Courses").document(key).collection("Heading").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                            @Override
//                                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                                                if(error != null)
//                                                {
//                                                    return;
//                                                }
//                                                if(value.size() != 0)
//                                                {
//                                                    i=0;
//                                                    for(QueryDocumentSnapshot doc : value)
//                                                    {
//
//                                                        Query query1 = db.collection("Courses").document(key).collection("Heading").document(doc.getString("heading_id").toString()).collection("video");
//                                                        AggregateQuery aggregateQuery1 = query1.count();
//                                                        Query query2 = db.collection("Courses").document(key).collection("Heading").document(doc.getString("heading_id").toString()).collection("quiz");
//                                                        AggregateQuery aggregateQuery2 = query2.count();
//                                                        aggregateQuery1.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
//                                                                if (task.isSuccessful()) {
//                                                                    AggregateQuerySnapshot snapshot = task.getResult();
//                                                                    totalVideo = totalVideo + (int)snapshot.getCount();
//                                                                    i++;
//                                                                    if(i == value.size())
//                                                                    {
//                                                                        if(totalVideo != 0)
//                                                                        {
//                                                                            db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                                                @Override
//                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                                                    if(task.isComplete())
//                                                                                    {
//                                                                                        for (QueryDocumentSnapshot documentt : task.getResult()) {
//
//                                                                                            aggregateQuery2.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
//                                                                                                @Override
//                                                                                                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
//                                                                                                    DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(documentt.getString("own_course_id"));
//                                                                                                    Map map = new HashMap();
//                                                                                                    float temp = (((float)checkedVideo/totalVideo)*100);
//                                                                                                    String exProgressStr = documentt.getString("own_course_ex_progress");
//                                                                                                    double exProgress = (exProgressStr != null) ? Double.parseDouble(exProgressStr) : 0.0;
//                                                                                                    double temp1 = (temp / 2) + (exProgress / 2);
//                                                                                                    map.put(KEY_PROGRESS_VIDEO, String.format("%.2f",temp).replaceAll(",","."));
//                                                                                                    if(task.getResult().getCount() > 0 )
//                                                                                                    {
//                                                                                                        map.put(KEY_PROGRESS_COURSE, String.format("%.2f",temp1).replaceAll(",","."));
//                                                                                                    }else{
//                                                                                                        map.put(KEY_PROGRESS_COURSE, String.format("%.2f",temp1*2).replaceAll(",","."));
//                                                                                                    }
//                                                                                                    documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
//                                                                                                        @Override
//                                                                                                        public void onComplete(@NonNull Task task) {
//                                                                                                        }
//                                                                                                    });
//                                                                                                    db.collection("Users")
//                                                                                                            .document(mAuth.getCurrentUser().getUid())
//                                                                                                            .collection("OwnCourses")
//                                                                                                            .document(documentt.getString("own_course_id")).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                                                                                                                @Override
//                                                                                                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                                                                                                                    if(error != null) return;
//                                                                                                                    if(value.exists() && value != null){
//                                                                                                                        if(value.getString("own_course_progress").equals("100.00"))
//                                                                                                                        {
//                                                                                                                            DocumentReference documentReference = db.collection("Users")
//                                                                                                                                    .document(mAuth.getCurrentUser().getUid())
//                                                                                                                                    .collection("OwnCourses")
//                                                                                                                                    .document(value.getId());
//                                                                                                                            Map map = new HashMap();
//                                                                                                                            map.put(KEY_COMPLETE_COURSE, true);
//                                                                                                                            documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                                                                @Override
//                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
//
//                                                                                                                                }
//                                                                                                                            });
//                                                                                                                            db.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                                                                                                @Override
//                                                                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                                                                                                    if(task.isComplete()){
//                                                                                                                                        long currentPoints = task.getResult().getLong("user_points");
//                                                                                                                                        long newPoints = currentPoints + 20;
//                                                                                                                                        DocumentReference documentReference1 = db.collection("Users")
//                                                                                                                                                .document(mAuth.getCurrentUser().getUid());
//                                                                                                                                        Map map = new HashMap();
//                                                                                                                                        map.put(KEY_USER_POINTS, newPoints);
//                                                                                                                                        documentReference1.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                                                                            @Override
//                                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                                                                                            }
//                                                                                                                                        });
//                                                                                                                                    }
//                                                                                                                                }
//                                                                                                                            });
//                                                                                                                        }
//                                                                                                                    }
//                                                                                                                }
//                                                                                                            });
//
//
//                                                                                                }
//                                                                                            });
//
//                                                                                        }
//
//                                                                                    }
//                                                                                }
//                                                                            });
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }
//                                                        });
//                                                    }
//
//                                                }
//                                            }
//                                        });
//                                    }
//                                }
//                            });
                        }else{
                            holder.imgCheck.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.checked));
                            Map<Object, String> hashMap = new HashMap<>();
                            hashMap.put(KEY_UID, uid);
                            hashMap.put(KEY_VID, id);
                            hashMap.put(KEY_CID, course_id);
                            DocumentReference reference = db.collection("Users").document(uid).collection("CheckVideo").document(id);
                            reference.set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
//                                    Query query = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckVideo").whereEqualTo("course_id", key);
//                                    AggregateQuery aggregateQuery = query.count();
//                                    aggregateQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
//                                            if (task.isSuccessful()) {
//                                                totalVideo = 0;
//                                                checkedVideo = 0;
//                                                AggregateQuerySnapshot snapshot = task.getResult();
//                                                checkedVideo = (int)snapshot.getCount();
//
//                                                db.collection("Courses").document(key).collection("Heading").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                                    @Override
//                                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                                                        if(error != null)
//                                                        {
//                                                            return;
//                                                        }
//                                                        if(value.size() != 0)
//                                                        {
//                                                            i=0;
//                                                            for(QueryDocumentSnapshot doc : value)
//                                                            {
//
//                                                                Query query1 = db.collection("Courses").document(key).collection("Heading").document(doc.getString("heading_id").toString()).collection("video");
//                                                                AggregateQuery aggregateQuery1 = query1.count();
//                                                                Query query2 = db.collection("Courses").document(key).collection("Heading").document(doc.getString("heading_id").toString()).collection("quiz");
//                                                                AggregateQuery aggregateQuery2 = query2.count();
//                                                                aggregateQuery1.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
//                                                                    @Override
//                                                                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
//                                                                        if (task.isSuccessful()) {
//                                                                            AggregateQuerySnapshot snapshot = task.getResult();
//                                                                            totalVideo = totalVideo + (int)snapshot.getCount();
//                                                                            i++;
//                                                                            if(i == value.size())
//                                                                            {
//                                                                                if(totalVideo != 0)
//                                                                                {
//                                                                                    db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                                                        @Override
//                                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                                                            if(task.isComplete())
//                                                                                            {
//                                                                                                for (QueryDocumentSnapshot documentt : task.getResult()) {
//
//                                                                                                    aggregateQuery2.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
//                                                                                                        @Override
//                                                                                                        public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
//                                                                                                            DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(documentt.getString("own_course_id"));
//                                                                                                            Map map = new HashMap();
//                                                                                                            float temp = (((float)checkedVideo/totalVideo)*100);
//                                                                                                            String exProgressStr = documentt.getString("own_course_ex_progress");
//                                                                                                            double exProgress = (exProgressStr != null) ? Double.parseDouble(exProgressStr) : 0.0;
//                                                                                                            double temp1 = (temp / 2) + (exProgress / 2);
//                                                                                                            map.put(KEY_PROGRESS_VIDEO, String.format("%.2f",temp).replaceAll(",","."));
//                                                                                                            if(task.getResult().getCount() > 0 )
//                                                                                                            {
//                                                                                                                map.put(KEY_PROGRESS_COURSE, String.format("%.2f",temp1).replaceAll(",","."));
//                                                                                                            }else{
//                                                                                                                map.put(KEY_PROGRESS_COURSE, String.format("%.2f",temp1 * 2).replaceAll(",","."));
//                                                                                                            }
//                                                                                                            documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
//                                                                                                                @Override
//                                                                                                                public void onComplete(@NonNull Task task) {
//                                                                                                                    db.collection("Users")
//                                                                                                                            .document(mAuth.getCurrentUser().getUid())
//                                                                                                                            .collection("OwnCourses")
//                                                                                                                            .document(documentt.getString("own_course_id")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                                                                                                @Override
//                                                                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                                                                                                                                    if(task.isComplete()){
//                                                                                                                                        if(temp1 == 100)
//                                                                                                                                        {
//                                                                                                                                            DocumentReference documentReference = db.collection("Users")
//                                                                                                                                                    .document(mAuth.getCurrentUser().getUid())
//                                                                                                                                                    .collection("OwnCourses")
//                                                                                                                                                    .document(task.getResult().getId());
//                                                                                                                                            Map map = new HashMap();
//                                                                                                                                            map.put(KEY_COMPLETE_COURSE, true);
//                                                                                                                                            documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                                                                                @Override
//                                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
//
//                                                                                                                                                }
//                                                                                                                                            });
//                                                                                                                                        }
//                                                                                                                                        else{
//                                                                                                                                            DocumentReference documentReference = db.collection("Users")
//                                                                                                                                                    .document(mAuth.getCurrentUser().getUid())
//                                                                                                                                                    .collection("OwnCourses")
//                                                                                                                                                    .document(task.getResult().getId());
//                                                                                                                                            Map map = new HashMap();
//                                                                                                                                            map.put(KEY_COMPLETE_COURSE, false);
//                                                                                                                                            documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                                                                                @Override
//                                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
//
//                                                                                                                                                }
//                                                                                                                                            });
//                                                                                                                                        }
//                                                                                                                                    }
//                                                                                                                                }
//                                                                                                                            });
//                                                                                                                }
//                                                                                                            });
////                                                                                                            db.collection("Users")
////                                                                                                                    .document(mAuth.getCurrentUser().getUid())
////                                                                                                                    .collection("OwnCourses")
////                                                                                                                    .document(documentt.getString("own_course_id")).addSnapshotListener(new EventListener<DocumentSnapshot>() {
////                                                                                                                        @Override
////                                                                                                                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
////                                                                                                                            if(error != null) return;
////                                                                                                                            if(value.exists() && value != null){
////                                                                                                                                if(value.getString("own_course_progress").equals("100.00"))
////                                                                                                                                {
////                                                                                                                                    DocumentReference documentReference = db.collection("Users")
////                                                                                                                                            .document(mAuth.getCurrentUser().getUid())
////                                                                                                                                            .collection("OwnCourses")
////                                                                                                                                            .document(value.getId());
////                                                                                                                                    Map map = new HashMap();
////                                                                                                                                    map.put(KEY_COMPLETE_COURSE, true);
////                                                                                                                                    documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                                                                                                        @Override
////                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
////
////                                                                                                                                        }
////                                                                                                                                    });
//////                                                                                                                                    db.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//////                                                                                                                                        @Override
//////                                                                                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//////                                                                                                                                            if(task.isComplete()){
//////                                                                                                                                                long currentPoints = task.getResult().getLong("user_points");
//////                                                                                                                                                long newPoints = currentPoints + 20;
//////                                                                                                                                                DocumentReference documentReference1 = db.collection("Users")
//////                                                                                                                                                        .document(mAuth.getCurrentUser().getUid());
//////                                                                                                                                                Map map = new HashMap();
//////                                                                                                                                                map.put(KEY_USER_POINTS, newPoints);
//////                                                                                                                                                documentReference1.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//////                                                                                                                                                    @Override
//////                                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
//////                                                                                                                                                    }
//////                                                                                                                                                });
//////                                                                                                                                            }
//////                                                                                                                                        }
//////                                                                                                                                    });
////                                                                                                                                }
////                                                                                                                            }
////                                                                                                                        }
////                                                                                                                    });
//
//                                                                                                        }
//                                                                                                    });
//
//                                                                                                }
//
//                                                                                            }
//                                                                                        }
//                                                                                    });
//                                                                                }
//                                                                            }
//                                                                        }
//                                                                    }
//                                                                });
//                                                            }
//
//                                                        }
//                                                    }
//                                                });
//                                            }
//                                        }
//                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    toastMes("Có lỗi xảy ra !");
                                }
                            });
                        }
                    }
                });
                holder.video_constraint.setEnabled(true);
                Intent intent = new Intent(context, VideoActivity.class);
                intent.putExtra("arrayID", arrID);
                intent.putExtra("url_id", url_id);
                context.startActivity(intent);
            }
        });
//        holder.imgCheck.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                holder.imgCheck.setEnabled(false);
//                holder.imgCheck.setImageResource(R.drawable.unchecked);
//                db.collection("Users").document(uid).collection("CheckVideo").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if(task.getResult().getData() != null){
//                            db.collection("Users").document(uid).collection("CheckVideo").document(id)
//                                    .delete()
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            Query query = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckVideo").whereEqualTo("course_id", key);
//                                            AggregateQuery aggregateQuery = query.count();
//                                            aggregateQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
//                                                    if (task.isSuccessful()) {
//                                                        totalVideo = 0;
//                                                        checkedVideo = 0;
//                                                        AggregateQuerySnapshot snapshot = task.getResult();
//                                                        checkedVideo = (int)snapshot.getCount();
//                                                        db.collection("Courses").document(key).collection("Heading").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                                            @Override
//                                                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                                                                if(error != null)
//                                                                {
//                                                                    return;
//                                                                }
//                                                                if(value.size() != 0)
//                                                                {
//                                                                    i=0;
//                                                                    for(QueryDocumentSnapshot doc : value)
//                                                                    {
//                                                                        Query query1 = db.collection("Courses").document(key).collection("Heading").document(doc.getString("heading_id").toString()).collection("video");
//                                                                        AggregateQuery aggregateQuery1 = query1.count();
//                                                                        aggregateQuery1.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
//                                                                            @Override
//                                                                            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
//                                                                                if (task.isSuccessful()) {
//                                                                                    AggregateQuerySnapshot snapshot = task.getResult();
//                                                                                    totalVideo = totalVideo + (int)snapshot.getCount();
//                                                                                    i++;
//                                                                                    if(i == value.size())
//                                                                                    {
//                                                                                        if(totalVideo != 0)
//                                                                                        {
//                                                                                            db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                                                                @Override
//                                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                                                                    if(task.isComplete())
//                                                                                                    {
//                                                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
//                                                                                                            DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(document.getString("own_course_id"));
//                                                                                                            Map map = new HashMap();
//                                                                                                            float temp =  (((float)checkedVideo/totalVideo)*100);
//                                                                                                            map.put(KEY_PROGRESS_COURSE, String.format("%.2f",temp).replaceAll(",","."));
//                                                                                                            documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
//                                                                                                                @Override
//                                                                                                                public void onComplete(@NonNull Task task) {
//                                                                                                                    DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(document.getString("own_course_id"));
//                                                                                                                    Map map = new HashMap();
//                                                                                                                    if((checkedVideo/totalVideo)*100 >= 100)
//                                                                                                                    {
//                                                                                                                        map.put(KEY_COMPLETE_COURSE, true);
//                                                                                                                    }
//                                                                                                                    else{
//                                                                                                                        map.put(KEY_COMPLETE_COURSE, false);
//                                                                                                                    }
//                                                                                                                    documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
//                                                                                                                        @Override
//                                                                                                                        public void onComplete(@NonNull Task task) {
//                                                                                                                            holder.imgCheck.setEnabled(true);
//                                                                                                                        }
//                                                                                                                    });
//                                                                                                                }
//                                                                                                            });
//                                                                                                        }
//
//                                                                                                    }
//                                                                                                }
//                                                                                            });
//                                                                                        }
//                                                                                    }
//                                                                                }
//                                                                            }
//                                                                        });
//                                                                    }
//
//                                                                }
//                                                            }
//                                                        });
//                                                    }
//                                                }
//                                            });
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            toastMes("Có lỗi xảy ra !");
//                                        }
//                                    });
//
//
//                        }else{
//                            holder.imgCheck.setEnabled(false);
//                            holder.imgCheck.setImageResource(R.drawable.checked);
//                            Map<Object, String> hashMap = new HashMap<>();
//                            hashMap.put(KEY_UID, uid);
//                            hashMap.put(KEY_VID, id);
//                            hashMap.put(KEY_CID, course_id);
//                            DocumentReference reference1 = db.collection("Users").document(uid).collection("CheckVideo").document(id);
//                            reference1.set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    Query query = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckVideo").whereEqualTo("course_id", key);
//                                    AggregateQuery aggregateQuery = query.count();
//                                    aggregateQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
//                                            if (task.isSuccessful()) {
//                                                totalVideo = 0;
//                                                checkedVideo = 0;
//                                                AggregateQuerySnapshot snapshot = task.getResult();
//                                                checkedVideo = (int)snapshot.getCount();
//
//                                                db.collection("Courses").document(key).collection("Heading").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                                    @Override
//                                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                                                        if(error != null)
//                                                        {
//                                                            return;
//                                                        }
//                                                        if(value.size() != 0)
//                                                        {
//                                                            i=0;
//                                                            for(QueryDocumentSnapshot doc : value)
//                                                            {
//                                                                Query query1 = db.collection("Courses").document(key).collection("Heading").document(doc.getString("heading_id").toString()).collection("video");
//                                                                AggregateQuery aggregateQuery1 = query1.count();
//                                                                aggregateQuery1.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
//                                                                    @Override
//                                                                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
//                                                                        if (task.isSuccessful()) {
//                                                                            AggregateQuerySnapshot snapshot = task.getResult();
//                                                                            totalVideo = totalVideo + (int)snapshot.getCount();
//                                                                            i++;
//                                                                            if(i == value.size())
//                                                                            {
//                                                                                if(totalVideo != 0)
//                                                                                {
//                                                                                    db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                                                        @Override
//                                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                                                            if(task.isComplete())
//                                                                                            {
//                                                                                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                                                                                    DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(document.getString("own_course_id"));
//                                                                                                    Map map = new HashMap();
//                                                                                                    float temp = (((float)checkedVideo/totalVideo)*100);
//                                                                                                    map.put(KEY_PROGRESS_COURSE, String.format("%.2f",temp).replaceAll(",","."));
//                                                                                                    documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
//                                                                                                        @Override
//                                                                                                        public void onComplete(@NonNull Task task) {
//                                                                                                            DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(document.getString("own_course_id"));
//                                                                                                            Map map = new HashMap();
//                                                                                                            if((checkedVideo/totalVideo)*100 >= 100)
//                                                                                                            {
//                                                                                                                map.put(KEY_COMPLETE_COURSE, true);
//                                                                                                            }
//                                                                                                            else{
//                                                                                                                map.put(KEY_COMPLETE_COURSE, false);
//                                                                                                            }
//                                                                                                            documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
//                                                                                                                @Override
//                                                                                                                public void onComplete(@NonNull Task task) {
//                                                                                                                    holder.imgCheck.setEnabled(true);
//                                                                                                                }
//                                                                                                            });
//                                                                                                        }
//                                                                                                    });
//                                                                                                }
//
//                                                                                            }
//                                                                                        }
//                                                                                    });
//                                                                                }
//                                                                            }
//                                                                        }
//                                                                    }
//                                                                });
//                                                            }
//
//                                                        }
//                                                    }
//                                                });
//                                            }
//                                        }
//                                    });
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    toastMes("Có lỗi xảy ra !");
//                                }
//                            });;
//
//                        }
//                    }
//                });
//            }
//        });

    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(context, mes,Toast.LENGTH_SHORT);
        toast.show();
    }
    public void release(){
        context =null;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView  txtVideoTitle;
        ConstraintLayout video_constraint;
        ImageView imgCheck;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            txtDuration = (TextView) itemView.findViewById(R.id.txtDuration);
            txtVideoTitle = (TextView) itemView.findViewById(R.id.txtVideoTitle);
            video_constraint = (ConstraintLayout) itemView.findViewById(R.id.video_constraint);
            imgCheck = (ImageView) itemView.findViewById(R.id.imgCheck);
        }
    }
}
