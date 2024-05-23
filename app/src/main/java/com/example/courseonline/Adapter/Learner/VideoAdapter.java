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
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Activity.Learner.VideoActivity;
import com.example.courseonline.Domain.VideoClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    public int i;
    private int checkedVideo;
    private static final String KEY_UID = "user_id";
    private static final String KEY_VID = "video_id";
    private static final String KEY_CID = "course_id";
    private static final String KEY_PROGRESS_COURSE = "cart_progress";
    private static final String KEY_COMPLETE_COURSE = "cart_complete";
    public VideoAdapter(ArrayList<VideoClass> items, String key) {
        this.items = items;
        this.key = key;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_layout_video,parent,false);
        context = parent.getContext();
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
        holder.txtDuration.setText(items.get(position).getVideo_duration());
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
                        }else{
                            holder.imgCheck.setImageResource(R.drawable.checked);
                            Map<Object, String> hashMap = new HashMap<>();
                            hashMap.put(KEY_UID, uid);
                            hashMap.put(KEY_VID, id);
                            hashMap.put(KEY_CID, course_id);
                            DocumentReference reference = db.collection("Users").document(uid).collection("CheckVideo").document(id);
                            reference.set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Query query = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckVideo").whereEqualTo("course_id", key);
                                    AggregateQuery aggregateQuery = query.count();
                                    aggregateQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                totalVideo = 0;
                                                checkedVideo = 0;
                                                AggregateQuerySnapshot snapshot = task.getResult();
                                                checkedVideo = (int)snapshot.getCount();

                                                db.collection("Courses").document(key).collection("Heading").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                        if(error != null)
                                                        {
                                                            return;
                                                        }
                                                        if(value.size() != 0)
                                                        {
                                                            i=0;
                                                            for(QueryDocumentSnapshot doc : value)
                                                            {
                                                                Query query1 = db.collection("Courses").document(key).collection("Heading").document(doc.getString("heading_id").toString()).collection("video");
                                                                AggregateQuery aggregateQuery1 = query1.count();
                                                                aggregateQuery1.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            AggregateQuerySnapshot snapshot = task.getResult();
                                                                            totalVideo = totalVideo + (int)snapshot.getCount();
                                                                            i++;
                                                                            if(i == value.size())
                                                                            {
                                                                                if(totalVideo != 0)
                                                                                {
                                                                                    db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("cart").whereEqualTo("cart_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                            if(task.isComplete())
                                                                                            {
                                                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                                    DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("cart").document(document.getString("cart_id"));
                                                                                                    Map map = new HashMap();
                                                                                                    float temp = (((float)checkedVideo/totalVideo)*100);
                                                                                                    map.put(KEY_PROGRESS_COURSE, String.format("%.2f",temp).replaceAll(",","."));
                                                                                                    documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task task) {
                                                                                                            DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("cart").document(document.getString("cart_id"));
                                                                                                            Map map = new HashMap();
                                                                                                            if((checkedVideo/totalVideo)*100 >= 100)
                                                                                                            {
                                                                                                                map.put(KEY_COMPLETE_COURSE, true);
                                                                                                            }
                                                                                                            else{
                                                                                                                map.put(KEY_COMPLETE_COURSE, false);
                                                                                                            }
                                                                                                            documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task task) {

                                                                                                                }
                                                                                                            });
                                                                                                        }
                                                                                                    });
                                                                                                }

                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                            }

                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
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
                intent.putExtra("url_id", url_id);
                context.startActivity(intent);
            }
        });
        holder.imgCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.imgCheck.setEnabled(false);
                holder.imgCheck.setImageResource(R.drawable.unchecked);
                db.collection("Users").document(uid).collection("CheckVideo").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().getData() != null){
                            db.collection("Users").document(uid).collection("CheckVideo").document(id)
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Query query = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckVideo").whereEqualTo("course_id", key);
                                            AggregateQuery aggregateQuery = query.count();
                                            aggregateQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        totalVideo = 0;
                                                        checkedVideo = 0;
                                                        AggregateQuerySnapshot snapshot = task.getResult();
                                                        checkedVideo = (int)snapshot.getCount();
                                                        db.collection("Courses").document(key).collection("Heading").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                                if(error != null)
                                                                {
                                                                    return;
                                                                }
                                                                if(value.size() != 0)
                                                                {
                                                                    i=0;
                                                                    for(QueryDocumentSnapshot doc : value)
                                                                    {
                                                                        Query query1 = db.collection("Courses").document(key).collection("Heading").document(doc.getString("heading_id").toString()).collection("video");
                                                                        AggregateQuery aggregateQuery1 = query1.count();
                                                                        aggregateQuery1.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    AggregateQuerySnapshot snapshot = task.getResult();
                                                                                    totalVideo = totalVideo + (int)snapshot.getCount();
                                                                                    i++;
                                                                                    if(i == value.size())
                                                                                    {
                                                                                        if(totalVideo != 0)
                                                                                        {
                                                                                            db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("cart").whereEqualTo("cart_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                    if(task.isComplete())
                                                                                                    {
                                                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                                            DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("cart").document(document.getString("cart_id"));
                                                                                                            Map map = new HashMap();
                                                                                                            float temp =  (((float)checkedVideo/totalVideo)*100);
                                                                                                            map.put(KEY_PROGRESS_COURSE, String.format("%.2f",temp).replaceAll(",","."));
                                                                                                            documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task task) {
                                                                                                                    DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("cart").document(document.getString("cart_id"));
                                                                                                                    Map map = new HashMap();
                                                                                                                    if((checkedVideo/totalVideo)*100 >= 100)
                                                                                                                    {
                                                                                                                        map.put(KEY_COMPLETE_COURSE, true);
                                                                                                                    }
                                                                                                                    else{
                                                                                                                        map.put(KEY_COMPLETE_COURSE, false);
                                                                                                                    }
                                                                                                                    documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task task) {
                                                                                                                            holder.imgCheck.setEnabled(true);
                                                                                                                        }
                                                                                                                    });
                                                                                                                }
                                                                                                            });
                                                                                                        }

                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        });
                                                                    }

                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            toastMes("Có lỗi xảy ra !");
                                        }
                                    });


                        }else{
                            holder.imgCheck.setEnabled(false);
                            holder.imgCheck.setImageResource(R.drawable.checked);
                            Map<Object, String> hashMap = new HashMap<>();
                            hashMap.put(KEY_UID, uid);
                            hashMap.put(KEY_VID, id);
                            hashMap.put(KEY_CID, course_id);
                            DocumentReference reference1 = db.collection("Users").document(uid).collection("CheckVideo").document(id);
                            reference1.set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Query query = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckVideo").whereEqualTo("course_id", key);
                                    AggregateQuery aggregateQuery = query.count();
                                    aggregateQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                totalVideo = 0;
                                                checkedVideo = 0;
                                                AggregateQuerySnapshot snapshot = task.getResult();
                                                checkedVideo = (int)snapshot.getCount();

                                                db.collection("Courses").document(key).collection("Heading").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                        if(error != null)
                                                        {
                                                            return;
                                                        }
                                                        if(value.size() != 0)
                                                        {
                                                            i=0;
                                                            for(QueryDocumentSnapshot doc : value)
                                                            {
                                                                Query query1 = db.collection("Courses").document(key).collection("Heading").document(doc.getString("heading_id").toString()).collection("video");
                                                                AggregateQuery aggregateQuery1 = query1.count();
                                                                aggregateQuery1.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            AggregateQuerySnapshot snapshot = task.getResult();
                                                                            totalVideo = totalVideo + (int)snapshot.getCount();
                                                                            i++;
                                                                            if(i == value.size())
                                                                            {
                                                                                if(totalVideo != 0)
                                                                                {
                                                                                    db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("cart").whereEqualTo("cart_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                            if(task.isComplete())
                                                                                            {
                                                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                                    DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("cart").document(document.getString("cart_id"));
                                                                                                    Map map = new HashMap();
                                                                                                    float temp = (((float)checkedVideo/totalVideo)*100);
                                                                                                    map.put(KEY_PROGRESS_COURSE, String.format("%.2f",temp).replaceAll(",","."));
                                                                                                    documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task task) {
                                                                                                            DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("cart").document(document.getString("cart_id"));
                                                                                                            Map map = new HashMap();
                                                                                                            if((checkedVideo/totalVideo)*100 >= 100)
                                                                                                            {
                                                                                                                map.put(KEY_COMPLETE_COURSE, true);
                                                                                                            }
                                                                                                            else{
                                                                                                                map.put(KEY_COMPLETE_COURSE, false);
                                                                                                            }
                                                                                                            documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task task) {
                                                                                                                    holder.imgCheck.setEnabled(true);
                                                                                                                }
                                                                                                            });
                                                                                                        }
                                                                                                    });
                                                                                                }

                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                            }

                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    toastMes("Có lỗi xảy ra !");
                                }
                            });;

                        }
                    }
                });
            }
        });

    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(context, mes,Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDuration, txtVideoTitle;
        ConstraintLayout video_constraint;
        ImageView imgCheck;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDuration = (TextView) itemView.findViewById(R.id.txtDuration);
            txtVideoTitle = (TextView) itemView.findViewById(R.id.txtVideoTitle);
            video_constraint = (ConstraintLayout) itemView.findViewById(R.id.video_constraint);
            imgCheck = (ImageView) itemView.findViewById(R.id.imgCheck);
        }
    }
}
