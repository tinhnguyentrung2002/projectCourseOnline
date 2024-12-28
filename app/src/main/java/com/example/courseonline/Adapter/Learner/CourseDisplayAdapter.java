package com.example.courseonline.Adapter.Learner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.courseonline.Activity.Learner.CourseActivity;
import com.example.courseonline.Domain.CourseDisplayClass;
import com.example.courseonline.Domain.TypeClass;
import com.example.courseonline.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class CourseDisplayAdapter extends RecyclerView.Adapter<CourseDisplayAdapter.ViewHolder> {

    ArrayList<CourseDisplayClass> items;
    Context context;
    private final int limit = 10;
    private FirebaseFirestore db;
    RecyclerView.RecycledViewPool viewPool;
    onItemClickListener onItemClickListener;
    public CourseDisplayAdapter(ArrayList<CourseDisplayClass> items, Context context) {
        this.items = items;
        this.context = context;
        viewPool = new RecyclerView.RecycledViewPool();

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_coursedisplay,parent,false);
        context =(Activity) parent.getContext();
        return new CourseDisplayAdapter.ViewHolder(inflate);
    }

    public interface onItemClickListener {
        void onClick(String str);
    }
    @Override
    public void onBindViewHolder(@NonNull CourseDisplayAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        db = FirebaseFirestore.getInstance();
        String id = items.get(position).getCourse_id();
        ArrayList<TypeClass> arrayTypeClass = new ArrayList<>();
        TypeAdapter typeAdapter = new TypeAdapter(arrayTypeClass, context);

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
                        arrayTypeClass.add(doc.toObject(TypeClass.class));
                    }
                }
                typeAdapter.notifyDataSetChanged();
            }
        });
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        holder.recyclerView.setAdapter(typeAdapter);
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setItemViewCacheSize(20);
        holder.recyclerView.setRecycledViewPool(viewPool);
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .centerInside();

        Glide.with(holder.itemView.getContext())
                .load(items.get(position).getCourse_img())
                .apply(options)
                .into(holder.imgPic);
        holder.txtTitle.setText(items.get(position).getCourse_title());
        holder.txtTitle.setSelected(true);
        db.collection("Users").document(items.get(position).getCourse_owner_id()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    return;
                }
                if(value.exists())
                {
                    holder.txtOwner.setText((value.getString("user_name")));

                }
            }
        });


        holder.txtPeople.setText(changeValue(items.get(position).getCourse_member()));
        holder.txtRate.setText(String.valueOf(items.get(position).getCourse_rate()));
        if(items.get(position).getCourse_price() != 0)
        {
            holder.txtPrice.setText(changeCurrency(items.get(position).getCourse_price()));
        }else{
            holder.txtPrice.setText("Miễn phí");
        }
        holder.txtTime.setText(items.get(position).getCourse_total_time() + " giờ");
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CourseActivity.class);
                intent.putExtra("course_key", id);
                context.startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

        });

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
        if(items.size() > limit){
            return limit;
        }
        else
        {
            return items.size();
        }
    }
    public void release(){
        context = null;
    }

    public void setClickItemListener(CourseDisplayAdapter.onItemClickListener onItem) {
        this.onItemClickListener = onItem;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitle, txtOwner, txtPeople, txtRate, txtTime, txtPrice;
        ImageView imgPic;
        CardView cardView;
        RecyclerView recyclerView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerType);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtOwner = itemView.findViewById(R.id.txtOwner);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtPeople = itemView.findViewById(R.id.txtPeople);
            txtRate = itemView.findViewById(R.id.txtRate);
            txtPrice = itemView.findViewById(R.id.txtCoursePrice);
            imgPic = itemView.findViewById(R.id.imgPic);
            cardView = itemView.findViewById(R.id.main_container);
        }
    }
    private String changeCurrency(double value){
        Locale locale = new Locale("vi", "VN");
        Currency currency = Currency.getInstance("VND");
        DecimalFormatSymbols df = DecimalFormatSymbols.getInstance(locale);
        df.setCurrency(currency);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        numberFormat.setCurrency(currency);
        return numberFormat.format(value);
    }
}