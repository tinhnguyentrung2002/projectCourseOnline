package com.example.courseonline.Adapter.Learner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Domain.HistoryQuizClass;
import com.example.courseonline.R;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryQuizAdapter extends RecyclerView.Adapter<HistoryQuizAdapter.ViewHolder> {

    private Context context;
    private List<HistoryQuizClass> historyQuizList;

    public HistoryQuizAdapter(Context context, List<HistoryQuizClass> historyQuizList) {
        this.context = context;
        this.historyQuizList = historyQuizList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_history_quiz, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HistoryQuizClass historyQuiz = historyQuizList.get(position);
        holder.tvDiem.setText(historyQuiz.getHistory_quiz_score()+ "%");
        holder.tvThoigian.setText(historyQuiz.getHistory_quiz_time_taken());

        if(historyQuiz.isHistory_quiz_state())
        {
            holder.tvKetqua.setText("Đạt");
            holder.tvKetqua.setTextColor(ContextCompat.getColor(context, R.color.mint));
        }else{
            holder.tvKetqua.setText("Chưa đạt");
            holder.tvKetqua.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        Timestamp timestamp = historyQuiz.getHistory_quiz_upload();

        if (timestamp != null) {
            Date date = timestamp.toDate();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault());
            String formattedDate = sdf.format(date);

            holder.tvLan.setText(formattedDate);
        }
    }

    @Override
    public int getItemCount() {
        return historyQuizList.size();
    }

    public void updateData(List<HistoryQuizClass> newData) {
        historyQuizList = newData;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLan, tvDiem, tvThoigian, tvKetqua;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDiem = itemView.findViewById(R.id.tv_diem);
            tvThoigian = itemView.findViewById(R.id.tv_thoigian);
            tvKetqua = itemView.findViewById(R.id.tv_ketqua);
            tvLan = itemView.findViewById(R.id.tv_upload_time);
        }
    }
}
