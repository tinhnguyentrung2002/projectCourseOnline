package com.example.courseonline.Adapter.Learner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Activity.Learner.CategoryActivity;
import com.example.courseonline.Activity.Learner.CategoryChildActivity;
import com.example.courseonline.Domain.CategoryDisplayClass;
import com.example.courseonline.R;

import java.util.ArrayList;

public class CategoryDisplayAdapter extends RecyclerView.Adapter<CategoryDisplayAdapter.ViewHolder> {

    ArrayList<CategoryDisplayClass> items;
    Activity context;
    private final int limit = 5;
    public CategoryDisplayAdapter(ArrayList<CategoryDisplayClass> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CategoryDisplayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_categorydisplay,parent,false);
        context = (Activity) parent.getContext();
        return new CategoryDisplayAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryDisplayAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.btnTitle.setText(items.get(position).getCategory_title());
        if(items.get(position).getCategory_layer() == 0){
            holder.btnTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, CategoryActivity.class);
                    intent.putExtra("category_title", holder.btnTitle.getText());
                    intent.putExtra("category_id", items.get(position).getCategory_id());
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }
            });
        }else{
            holder.btnTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, CategoryChildActivity.class);
                    intent.putExtra("category_title", holder.btnTitle.getText());
                    intent.putExtra("category_child_id", items.get(position).getCategory_id());
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }
            });
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
        Button btnTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnTitle = itemView.findViewById(R.id.btnCategory);
        }
    }
}
