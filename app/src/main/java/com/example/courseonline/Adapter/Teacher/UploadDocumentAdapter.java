package com.example.courseonline.Adapter.Teacher;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Domain.DocumentClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UploadDocumentAdapter extends RecyclerView.Adapter<UploadDocumentAdapter.ViewHolder> {
    ArrayList<DocumentClass> items;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    Context context;
    private Toast toast;
    private static final String KEY_UID = "user_id";
    private static final String KEY_VID = "video_id";
    private String cid, headingId;

    public UploadDocumentAdapter(ArrayList<DocumentClass> items, String cid, String headingId) {
        this.items = items;
        this.cid = cid;
        this.headingId = headingId;
    }

    @NonNull
    @Override
    public UploadDocumentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_content,parent,false);
        context = parent.getContext();
        return new UploadDocumentAdapter.ViewHolder(inflate);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull UploadDocumentAdapter.ViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        holder.txtDocument.setText(items.get(position).getDocument_title());
        holder.btnDeleteVideo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("Bạn có chắc chắn muốn xoá tài liệu này ?")
                        .setPositiveButton("Xoá", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                if(mAuth.getCurrentUser() != null)
                                {
                                    db.collection("Courses").document(cid).collection("Heading").document(headingId).collection("document").document(items.get(position).getDocument_id()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            toastMes("Xoá thành công");
                                        }
                                    });
                                }
                            }
                        }).setNegativeButton("Huỷ", null);

                AlertDialog alert1 = alert.create();
                alert1.show();

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
        ImageView btnDeleteVideo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDocument= (TextView) itemView.findViewById(R.id.txtContent);
            btnDeleteVideo = (ImageView) itemView.findViewById(R.id.btnDeleteContent);
        }
    }
}
