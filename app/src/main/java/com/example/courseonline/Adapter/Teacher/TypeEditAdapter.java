package com.example.courseonline.Adapter.Teacher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Domain.TypeClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

public class TypeEditAdapter extends RecyclerView.Adapter<TypeEditAdapter.ViewHolder>{
    ArrayList<TypeClass> items;
    Activity context;
    private int layer;
    private final int limit = 3;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public TypeEditAdapter(ArrayList<TypeClass> items, int layer) {
        this.items = items;
        this.layer = layer;
    }

    @NonNull
    @Override
    public TypeEditAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_type_edit,parent,false);
        context =(Activity) parent.getContext();
        return new TypeEditAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeEditAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if(items.get(position).getCategory_id().equals("freeCategory"))
        {
            holder.imgDelete.setVisibility(View.GONE);
        }
        else{
            holder.imgDelete.setVisibility(View.VISIBLE);
            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(items.get(position).getCategory_child_id().equals(""))
                    {
                        for(TypeClass typeClass : items)
                        {
                            if(!typeClass.getCategory_id().equals("freeCategory"))
                            {
                                db.collection("Courses").document(items.get(position).getCourse_id()).collection("Type").document(typeClass.getType_id()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                            }
                        }

                    }else {
                        db.collection("Courses").document(items.get(position).getCourse_id()).collection("Type").document(items.get(position).getType_id()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                    }

                }
            });

        }

        if(this.layer == 0)
        {
            db.collection("Categories").document(items.get(position).getCategory_id()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                        return;
                    }
                    holder.textType.setText(value.getString("category_title"));
                }
            });

        }else if(layer == 1)
        {
            db.collection("Categories").document(items.get(position).getCategory_id()).collection("CategoriesChild").document(items.get(position).getCategory_child_id()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                        return;
                    }
                    holder.textType.setText(value.getString("category_title"));

                }
            });
        }else{
            if(items.get(position).getCategory_child_id().equals(""))
            {
                db.collection("Categories").document(items.get(position).getCategory_id()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null)
                        {
                            return;
                        }
                        holder.textType.setText(value.getString("category_title"));

                    }
                });
            }
            else{
                db.collection("Categories").document(items.get(position).getCategory_id()).collection("CategoriesChild").document(items.get(position).getCategory_child_id()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null)
                        {
                            return;
                        }
                        holder.textType.setText(value.getString("category_title"));
                    }
                });
            }

        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textType;
        ImageView imgDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textType = itemView.findViewById(R.id.textView29);
            imgDelete = itemView.findViewById(R.id.btnDeleteType);
        }
    }
}
