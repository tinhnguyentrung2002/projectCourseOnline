package com.example.courseonline.Adapter.Learner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Activity.Learner.WebActivity;
import com.example.courseonline.Domain.DocumentClass;
import com.example.courseonline.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {
    ArrayList<DocumentClass> items;
    Context context;
    private Toast toast;
    private static final String KEY_UID = "user_id";
    private static final String KEY_VID = "video_id";

    public DocumentAdapter(ArrayList<DocumentClass> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public DocumentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_layout_document,parent,false);
        context = parent.getContext();
        return new ViewHolder(inflate);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull DocumentAdapter.ViewHolder holder, int position) {
//        try {
//            URL url = new URL(items.get(position).getDocument_url().toString());
//            String path = url.getPath();
//            String fileName = path.substring(path.lastIndexOf('/') + 1);
//            holder.txtDocument.setText(fileName.replaceAll("%20","").replaceAll("%2F","").replaceAll("Coursesdocument",""));
//        } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
//        }
        holder.txtDocument.setText(items.get(position).getDocument_title());
        holder.document_constraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebActivity.class);
                try {
                    intent.putExtra("document_url",URLEncoder.encode(items.get(position).getDocument_url().toString(),"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                context.startActivity(intent);
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
        TextView txtDocument;
        ConstraintLayout document_constraint;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDocument = (TextView) itemView.findViewById(R.id.txtDocument);
            document_constraint = (ConstraintLayout) itemView.findViewById(R.id.constraintDocument);
        }
    }
}
