package com.example.courseonline.Adapter.Learner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Activity.Learner.CategoryActivity;
import com.example.courseonline.Activity.Learner.CategoryChildActivity;
import com.example.courseonline.Domain.TypeClass;
import com.example.courseonline.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;


public class TypeAdapter  extends RecyclerView.Adapter<TypeAdapter.ViewHolder>{
    ArrayList<TypeClass> items;
    Activity context;
    private int layer;
    private final int limit = 3;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public TypeAdapter(ArrayList<TypeClass> items, int layer) {
        this.items = items;
        this.layer = layer;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_layout_type,parent,false);
        context =(Activity) parent.getContext();
        return new TypeAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
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
            if(!items.get(position).getCategory_id().equals("freeCategory")) {
                holder.textType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, CategoryActivity.class);
                        intent.putExtra("course_id", items.get(position).getCourse_id());
                        intent.putExtra("category_id", items.get(position).getCategory_id());
                        intent.putExtra("category_title", holder.textType.getText());
                        context.startActivity(intent);
                        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
            }

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

                holder.textType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, CategoryChildActivity.class);
                        intent.putExtra("course_id", items.get(position).getCourse_id());
                        intent.putExtra("category_id", items.get(position).getCategory_child_id());
                        intent.putExtra("category_title", holder.textType.getText());
                        context.startActivity(intent);
                        context.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
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
                if(!items.get(position).getCategory_id().equals("freeCategory"))
                {
                    holder.textType.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, CategoryActivity.class);
                            intent.putExtra("course_id", items.get(position).getCourse_id());
                            intent.putExtra("category_id", items.get(position).getCategory_id());
                            intent.putExtra("category_title", holder.textType.getText());
                            context.startActivity(intent);
                            context.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        }
                    });
                }
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
                holder.textType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, CategoryChildActivity.class);
                        intent.putExtra("course_id", items.get(position).getCourse_id());
                        intent.putExtra("category_child_id", items.get(position).getCategory_child_id());
                        intent.putExtra("category_title", holder.textType.getText());
                        context.startActivity(intent);
                        context.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    }
                });
            }

        }
    }

    @Override
    public int getItemCount() {
        if(items.size() > limit){
            return limit;
        }
        else
        {
            return items.size();
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textType = itemView.findViewById(R.id.textView29);
        }
    }
}
