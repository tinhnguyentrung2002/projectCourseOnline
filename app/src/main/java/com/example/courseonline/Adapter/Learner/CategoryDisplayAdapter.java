package com.example.courseonline.Adapter.Learner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Activity.Learner.CategoryActivity;
import com.example.courseonline.Activity.Learner.CategoryChildActivity;
import com.example.courseonline.Domain.CategoryDisplayClass;
import com.example.courseonline.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryDisplayAdapter extends RecyclerView.Adapter<CategoryDisplayAdapter.ViewHolder> {

    private ArrayList<CategoryDisplayClass> items;
    private Context context;
    private boolean filter;
    private List<Integer> colorList;

    public CategoryDisplayAdapter(ArrayList<CategoryDisplayClass> items, boolean filter, Context context) {
        this.items = items;
        this.filter = filter;
        this.context = context;
        initColorList();
    }

    @NonNull
    @Override
    public CategoryDisplayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate;
        if(filter)
        {
            inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_layout_type, parent, false);
        }else {
            inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_categorydisplay, parent, false);
        }
        context = (Activity) parent.getContext();
        return new ViewHolder(inflate);
    }
    private void initColorList() {
        colorList = Arrays.asList(
                R.drawable.btn_login,
                R.drawable.btn_login_dark_green,
                R.drawable.btn_login_green,
                R.drawable.btn_login_purple,
                R.drawable.btn_login_dark_red
        );
    }
    @Override
    public void onBindViewHolder(@NonNull CategoryDisplayAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(filter)
        {
            holder.textView.setText(items.get(position).getCategory_title());
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String itemName = holder.textView.getText().toString();
                    Intent intent = new Intent("custom-message");
                    intent.putExtra("item",itemName);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });



        }else {
            holder.btnTitle.setText(items.get(position).getCategory_title());

            int colorIndex = position % colorList.size();
            holder.btnTitle.setBackgroundResource(colorList.get(colorIndex));

            if(items.get(position).getCategory_layer() == 0){

                holder.btnTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(context, CategoryActivity.class);
                        intent.putExtra("category_title", holder.btnTitle.getText());
                        intent.putExtra("category_id", items.get(position).getCategory_id());
                        context.startActivity(intent);
//                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
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
//                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    }
                });
            }
        }


    }
    public void release(){
        context = null;
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        Button btnTitle;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnTitle = itemView.findViewById(R.id.btnCategory);
            textView = itemView.findViewById(R.id.textView29);
        }
    }
}
