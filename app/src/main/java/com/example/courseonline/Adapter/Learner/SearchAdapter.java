package com.example.courseonline.Adapter.Learner;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.courseonline.Activity.Learner.CourseActivity;
import com.example.courseonline.Activity.Teacher.DetailCourseTeacherActivity;
import com.example.courseonline.Domain.CourseDisplayClass;
import com.example.courseonline.Domain.TypeClass;
import com.example.courseonline.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements Filterable {
    ArrayList<CourseDisplayClass> items;
    ArrayList<CourseDisplayClass> itemsOld;
    Activity context;
    private int permission;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    SearchAdapter.onItemClickListener onItemClickListener;
    RecyclerView.RecycledViewPool viewPool;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    public SearchAdapter(ArrayList<CourseDisplayClass> items, int permission){
        this.items = items;
        this.itemsOld = items;
        viewPool = new RecyclerView.RecycledViewPool();
        this.permission = permission;
    }
    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate;
        if(permission == 1)
        {
            inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_search, parent, false);
        }else{
            inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_search_teacher, parent, false);
        }
        context =(Activity) parent.getContext();
        return new ViewHolder(inflate);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String id = items.get(position).getCourse_id();
        if(permission == 1){
            db = FirebaseFirestore.getInstance();
            ArrayList<TypeClass> arrayTypeClass = new ArrayList<>();
            TypeAdapter typeAdapter = new TypeAdapter(arrayTypeClass, 0);

            db.collection("Courses").document(id).collection("Type").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {

                        return;
                    }
                    arrayTypeClass.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.toObject(TypeClass.class) != null) {
                            if(doc.getString("category_child_id") == null)
                            {
                                arrayTypeClass.add(doc.toObject(TypeClass.class));
                            }
                        }
                        typeAdapter.notifyDataSetChanged();
                    }
                }
            });
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
            holder.recyclerView.setAdapter(typeAdapter);
            holder.recyclerView.setHasFixedSize(true);
            holder.recyclerView.setItemViewCacheSize(20);
            holder.recyclerView.setRecycledViewPool(viewPool);

            Glide.with(holder.itemView.getContext()).load(items.get(position).getCourse_img()).centerInside().into(holder.imgPic);
            holder.txtTitle.setText(items.get(position).getCourse_title());
            holder.txtOwner.setText(items.get(position).getCourse_owner());
            holder.txtView.setText(changeValue(items.get(position).getCourse_member()));
            holder.txtRate.setText(String.valueOf(items.get(position).getCourse_rate()));
            holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CourseActivity.class);
                    intent.putExtra("course_key", id);
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        }else{
            Glide.with(holder.itemView.getContext()).load(items.get(position).getCourse_img()).centerInside().into(holder.imgPicTeacher);
            holder.txtTitleTeacher.setText(items.get(position).getCourse_title());
            holder.txtDateUpload.setText(sdf.format(items.get(position).getCourse_upload()));
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailCourseTeacherActivity.class);
                    intent.putExtra("key_id", id);
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        }

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()){
                    items = itemsOld;
                }else{
                    ArrayList<CourseDisplayClass> arrayList = new ArrayList<>();
                    for (CourseDisplayClass courseDisplayClass : itemsOld)
                    {
                        if(permission == 1)
                        {
                            if(courseDisplayClass.getCourse_title().toLowerCase().contains(strSearch.toLowerCase()) ||
                                    courseDisplayClass.getCourse_owner().toLowerCase().contains(strSearch.toLowerCase())){
                                arrayList.add(courseDisplayClass);
                            }
                        }else{
                            if(courseDisplayClass.getCourse_title().toLowerCase().contains(strSearch.toLowerCase())) {
                                arrayList.add(courseDisplayClass);
                            }
                        }

                    }
                    items = arrayList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = items;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                items = (ArrayList<CourseDisplayClass>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface onItemClickListener {
        void onClick(String str);
    }
    public void setClickItemListener(SearchAdapter.onItemClickListener onItem) {
        this.onItemClickListener = onItem;
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
    public int getItemCount() {
        return items.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitle, txtOwner, txtView, txtRate, txtTitleTeacher, txtDateUpload;
        ImageView imgPic, imgPicTeacher;
        ConstraintLayout constraintLayout;
        RecyclerView recyclerView;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.main_container_course_teacher);
            txtTitle = itemView.findViewById(R.id.txtTitles);
            txtOwner = itemView.findViewById(R.id.txtOwners);
            txtView = itemView.findViewById(R.id.txtPeoples);
            txtRate = itemView.findViewById(R.id.txtRates);
            imgPic = itemView.findViewById(R.id.imgPics);
            imgPicTeacher = itemView.findViewById(R.id.imgSearchTeacher);
            txtDateUpload = itemView.findViewById(R.id.txtDateUpload);
            txtTitleTeacher = itemView.findViewById(R.id.txtTitleSearchTeacher);
            constraintLayout = itemView.findViewById(R.id.constraintVertical);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerTypes);
        }
    }
}
