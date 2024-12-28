package com.example.courseonline.Adapter.Learner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Activity.Learner.CheckoutSuccessActivity;
import com.example.courseonline.Domain.PaymentHistoryClass;
import com.example.courseonline.R;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder>{
    ArrayList<PaymentHistoryClass> items;
    Context context;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    public PaymentHistoryAdapter(ArrayList<PaymentHistoryClass> items, Context context) {
        this.items = items;
        this.context = context;
    }
    @NonNull
    @Override
    public PaymentHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_payment_history,parent,false);
        context = (Activity) parent.getContext();
        return new PaymentHistoryAdapter.ViewHolder(inflate);
    }
    @Override
    public void onBindViewHolder(@NonNull PaymentHistoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        String orderID = items.get(position).getOrderID();
//        String orderItemID = items.get(position).getOrderItemID();
//        String orderPayerID = items.get(position).getOrderPayerID();
//        String orderStatus = items.get(position).getOrderStatus();
        holder.txtID.setText("OrderID: "+items.get(position).getOrder_id());
        if(items.get(position).getOrder_date() != null) holder.txtDate.setText(sdf.format(items.get(position).getOrder_date()));
        else holder.txtDate.setText("Unknown");
        if(items.get(position).getOrder_status() == 0)
        {
            holder.txtStatus.setTextColor(Color.parseColor("#4CAF50"));
            holder.txtStatus.setText("Thành công");
        }
        else {
            holder.txtStatus.setTextColor(Color.parseColor("#FF0000"));
            holder.txtStatus.setText("Thất bại");
        }
        if(items.get(position).getOrder_pay_method().equals("Points")){
            holder.txtAmount.setText((int)((items.get(position).getOrder_amount() *2)/1000) + " ⭐");
        }else{
            holder.txtAmount.setText(changeCurrency(items.get(position).getOrder_amount()));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CheckoutSuccessActivity.class);
                intent.putExtra("order_id",items.get(position).getOrder_id());
                context.startActivity(intent);
            }
        });
    }
    public void release(){
        context =null;
    }
    @Override
    public int getItemCount() {
        return items.size();
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
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtAmount, txtID, txtStatus;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.orderDate);
            txtAmount = itemView.findViewById(R.id.orderAmount);
            txtID = itemView.findViewById(R.id.orderID);
            txtStatus = itemView.findViewById(R.id.orderStatus);
            cardView = itemView.findViewById(R.id.card_view_HP);

        }
    }
}
