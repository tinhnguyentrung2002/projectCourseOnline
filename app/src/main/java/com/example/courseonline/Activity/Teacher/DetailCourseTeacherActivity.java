package com.example.courseonline.Activity.Teacher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.courseonline.Adapter.Learner.TypeAdapter;
import com.example.courseonline.Domain.TypeClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailCourseTeacherActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FrameLayout linearContent;
    private SwitchCompat switchState;
    private ImageView imgState2, imgState3;
    public boolean state1 ,state2, state3;
    private RecyclerView recyclerP2;
    private TypeAdapter adapter;
    private ArrayList<TypeClass> arrayList = new ArrayList<>();
    private ShapeableImageView imgDetail, imgBack;
    private TextView txtNone7, txtDetailNameP1, txtPriceNameP1, txtDetailDescriptionP1, txtDetailTimeP1, txtDetailMember, txtDetailRate, btnDetailEditP1, btnDetailEditP2,btnDetailEditP3;
    private String key_id;
    private static final String KEY_COURSE_STATE = "course_state";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_course_teacher);
        mapping();
        key_id = getIntent().getStringExtra("key_id");
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null && key_id != null)
        {
            loadData();
        }
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        switchState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(state2  && state3)
                    {
                        DocumentReference doc = db.collection("Courses").document(key_id);
                        Map map = new HashMap();
                        map.put(KEY_COURSE_STATE, b);
                        doc.update(map).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                            }
                        });
                    }else{
                        Toast.makeText(DetailCourseTeacherActivity.this, "Nội dung khoá học chưa đầy đủ", Toast.LENGTH_SHORT).show();
                        switchState.setChecked(false);
                    }

            }
        });
        btnDetailEditP1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(state1 != false)
                {
                    Toast.makeText(DetailCourseTeacherActivity.this, "Hãy chuyển khoá học sang chế độ riêng tư để chỉnh sửa", Toast.LENGTH_SHORT).show();
                }else{
                Intent intent = new Intent(DetailCourseTeacherActivity.this, UploadStep1Activity.class);
                intent.putExtra("key_id", key_id);
                intent.putExtra("key_type", "edit");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);}
            }
        });
        btnDetailEditP2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(state1 != false)
                {
                    Toast.makeText(DetailCourseTeacherActivity.this, "Hãy chuyển khoá học sang chế độ riêng tư để chỉnh sửa", Toast.LENGTH_SHORT).show();
                }else{
                Intent intent = new Intent(DetailCourseTeacherActivity.this, UploadTypeActivity.class);
                intent.putExtra("key_id", key_id);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);}
            }
        });
        btnDetailEditP3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(state1 != false)
                {
                    Toast.makeText(DetailCourseTeacherActivity.this, "Hãy chuyển khoá học sang chế độ riêng tư để chỉnh sửa", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(DetailCourseTeacherActivity.this, UploadStep2Activity.class);
                    intent.putExtra("key_id", key_id);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

            }
        });
    }
    private void loadData(){
        recyclerP2.setLayoutManager(new LinearLayoutManager(DetailCourseTeacherActivity.this,LinearLayoutManager.HORIZONTAL,false));
        recyclerP2.setHasFixedSize(true);
        recyclerP2.setItemViewCacheSize(20);
        adapter = new TypeAdapter(arrayList, 2);
        recyclerP2.setAdapter(adapter);
        db.collection("Courses").document(key_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    return;
                }

                assert value != null;
                if(Boolean.TRUE.equals(value.getBoolean("course_state")))
                {
                    state1 = true;
                    state2 = true;
                    state3 = true;
                }
                else{
                    state1 = false;
                }
                if(Boolean.TRUE.equals(value.getBoolean("course_state")))
                    switchState.setChecked(true);
                else
                    switchState.setChecked(false);


            }
        });
        db.collection("Courses").document(key_id).collection("Type").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    return;
                }
                if(value.size() != 0)
                {
                    if(value.size() >= 2)
                    {
                        imgState2.setImageDrawable(getResources().getDrawable(R.drawable.ready,null));
                        state2 = true;
                    }else {
                        imgState2.setImageDrawable(getResources().getDrawable(R.drawable.notready,null));
                        state2 = false;
                        switchState.setChecked(false);
                    }
                    arrayList.clear();
                    for(QueryDocumentSnapshot doc : value)
                    {
                        arrayList.add(doc.toObject(TypeClass.class));
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    imgState2.setImageDrawable(getResources().getDrawable(R.drawable.notready,null));
                    state2 = false;
                    switchState.setChecked(false);
                }
            }
        });
        db.collection("Courses").document(key_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    return;
                }
                txtDetailNameP1.setText(value.getString("course_title"));
                txtDetailDescriptionP1.setText("Mô tả: " +  value.getString("course_description"));
                if(value.getDouble("course_price") != null)
                txtPriceNameP1.setText("Giá: " + changeCurrency(value.getDouble("course_price")));
                if(value.getDouble("course_total_time") != null)
                txtDetailTimeP1.setText("Thời lượng: " + value.getDouble("course_total_time") + " giờ");
                if(value.getDouble("course_member") != null)
                txtDetailMember.setText(changeValue(value.getDouble("course_member")));
                if(value.getDouble("course_rate") != null)
                txtDetailRate.setText(String.valueOf(value.getDouble("course_rate")));
                String img = value.getString("course_img");
                Glide.with(getApplicationContext()).load(img).centerInside().into(imgDetail);
            }
        });
        db.collection("Courses").document(key_id).collection("Heading").orderBy("heading_order", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    return;
                }
                if(value.size() != 0)
                {
                    TreeNode root = TreeNode.root();
                    txtNone7.setVisibility(View.GONE);
                    for(QueryDocumentSnapshot doc : value)
                    {
                        MyHolder.IconTreeItem nodeParent = new MyHolder.IconTreeItem();
                        nodeParent.icon = 0;
                        nodeParent.text = doc.getString("heading_title");
                        TreeNode parent = new TreeNode(nodeParent).setViewHolder(new MyHolder(DetailCourseTeacherActivity.this));
                        MyHolder.IconTreeItem nodeChild1 = new MyHolder.IconTreeItem();
                        MyHolder.IconTreeItem nodeChild2 = new MyHolder.IconTreeItem();
                        nodeChild1.icon = 1;
                        nodeChild2.icon = 1;
                        TreeNode child1 = new TreeNode(nodeChild1).setViewHolder(new MyHolder(DetailCourseTeacherActivity.this));
                        TreeNode child2 = new TreeNode(nodeChild2).setViewHolder(new MyHolder(DetailCourseTeacherActivity.this));
                        parent.addChildren(child1);
                        parent.addChildren(child2);
                        db.collection("Courses").document(key_id).collection("Heading").document(doc.getString("heading_id")).collection("video").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error != null){
                                    return;
                                }

                                if(value.size() != 0)
                                {
                                    imgState3.setImageDrawable(getResources().getDrawable(R.drawable.ready, null));
                                    state3 = true;
                                    nodeChild1.text = "Video " + "("+value.size()+")";
                                    for (QueryDocumentSnapshot doc : value)
                                    {
                                        MyHolder.IconTreeItem nodeChild11 = new MyHolder.IconTreeItem();
                                        nodeChild11.icon = 2;
                                        nodeChild11.text = doc.getString("video_title");
                                        TreeNode child11 = new TreeNode(nodeChild11).setViewHolder(new MyHolder(DetailCourseTeacherActivity.this));
                                        child1.addChildren(child11);
                                    }
                                }
                                else{
                                    nodeChild1.text = "Video " + "(0)";
                                    state3 = false;
                                    switchState.setChecked(false);
                                    imgState3.setImageDrawable(getResources().getDrawable(R.drawable.notready,null));
                                }
                            }
                        });
                        db.collection("Courses").document(key_id).collection("Heading").document(doc.getString("heading_id")).collection("document").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error != null){
                                    return;
                                }

                                if(value.size() != 0)
                                {
                                    nodeChild2.text = "Tài liệu " + "("+value.size()+")";
                                    for (QueryDocumentSnapshot doc : value)
                                    {
                                        MyHolder.IconTreeItem nodeChild12 = new MyHolder.IconTreeItem();
                                        nodeChild12.icon = 3;
                                        nodeChild12.text = doc.getString("document_title");
                                        TreeNode child12 = new TreeNode(nodeChild12).setViewHolder(new MyHolder(DetailCourseTeacherActivity.this));
                                        child2.addChildren(child12);
                                    }
                                }
                                else{
                                    nodeChild2.text = "Tài liệu " + "(0)";
                                }
                            }
                        });
                        root.addChild(parent);


                    }
                    AndroidTreeView tView = new AndroidTreeView(DetailCourseTeacherActivity.this, root);
                    linearContent.addView(tView.getView());
                }else{
                    txtNone7.setVisibility(View.VISIBLE);
                }

            }
        });
    }
    private void mapping(){
        txtDetailNameP1 = (TextView) findViewById(R.id.txtDetailNameP1);
        txtPriceNameP1 = (TextView) findViewById(R.id.txtDetailPriceP1);
        txtDetailDescriptionP1 = (TextView) findViewById(R.id.txtDetailDescriptionP1);
        txtDetailTimeP1 = (TextView) findViewById(R.id.txtDetailTimeP1);
        txtDetailMember = (TextView) findViewById(R.id.txtDetailMember);
        txtNone7 = (TextView) findViewById(R.id.txtNone7);
        txtDetailRate = (TextView) findViewById(R.id.txtDetailRate);
        btnDetailEditP1 = (TextView) findViewById(R.id.btnDetailEditP1);
        btnDetailEditP2 = (TextView) findViewById(R.id.btnDetailEditP2);
        btnDetailEditP3 = (TextView) findViewById(R.id.btnDetailEditP3);
        imgDetail = (ShapeableImageView) findViewById(R.id.imgDetail);
        imgBack = (ShapeableImageView) findViewById(R.id.backActionDetail);
        linearContent = (FrameLayout) findViewById(R.id.linearP3);
        imgState3 = (ImageView) findViewById(R.id.imgState3);
        imgState2 = (ImageView) findViewById(R.id.imgState2);
        recyclerP2 = (RecyclerView) findViewById(R.id.recyclerP2);
        switchState = (SwitchCompat) findViewById(R.id.switchState);
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
    public static class MyHolder extends TreeNode.BaseNodeViewHolder<MyHolder.IconTreeItem> {
        public MyHolder(Context context) {
            super(context);
        }

        @Override
        public View createNodeView(TreeNode node, IconTreeItem value) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.layout_profile_node, null, false);
            TextView tvValue = (TextView) view.findViewById(R.id.node_value);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageNode);
            switch (value.icon)
            {
                case 0:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.heading, null));
                    break;
                case 1:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.content, null));
                    break;
                case 2:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.clip, null));
                    break;
                case 3:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_picture_as_pdf_24, null));
                    break;
            }
            tvValue.setText(value.text);

            return view;
        }
        public static class IconTreeItem {
            public int icon;
            public String text;
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(0,0);
        startActivity(getIntent());
        overridePendingTransition(0,0);
    }
}