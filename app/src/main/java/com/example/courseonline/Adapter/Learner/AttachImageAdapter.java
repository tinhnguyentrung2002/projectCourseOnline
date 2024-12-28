package com.example.courseonline.Adapter.Learner;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.courseonline.R;

import java.util.List;

public class AttachImageAdapter extends RecyclerView.Adapter<AttachImageAdapter.ViewHolder> {
    private List<Uri> imageUris;
    private OnImageRemoveListener onImageRemoveListener;

    public AttachImageAdapter(List<Uri> imageUris, OnImageRemoveListener onImageRemoveListener) {
        this.imageUris = imageUris;
        this.onImageRemoveListener = onImageRemoveListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_question_attach_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Uri uri = imageUris.get(position);
        Glide.with(holder.imageView.getContext()).load(uri).into(holder.imageView);

        holder.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageRemoveListener.onImageRemove(position);
            }
        });
        holder.imgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog(holder.itemView.getContext(), uri);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView imgClose;
        ConstraintLayout imgLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.attachImage);
            imgClose = itemView.findViewById(R.id.removeImage);
            imgLayout = itemView.findViewById(R.id.attachImageLayout);
        }
    }

    public interface OnImageRemoveListener {
        void onImageRemove(int position);
    }
    private void showImageDialog(Context context, Uri imageUri) {
        Dialog imageDialog = new Dialog(context);
        imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imageDialog.setContentView(R.layout.dialog_zoom_image);

        ImageView imageView = imageDialog.findViewById(R.id.dialogImageView);
        imageView.setImageURI(imageUri);
        imageDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView btnClose = imageDialog.findViewById(R.id.btnCloseZoom);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDialog.dismiss();
            }
        });

        imageDialog.setCancelable(true);
        imageDialog.show();
    }




}
