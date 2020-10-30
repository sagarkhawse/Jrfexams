package com.skteam.jrfexams.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skteam.jrfexams.R;
import com.skteam.jrfexams.activities.MCQActivity;
import com.skteam.jrfexams.models.Date;
import com.skteam.jrfexams.utils.Animations;
import com.skteam.jrfexams.utils.Functions;
import com.skteam.jrfexams.utils.Variables;

import java.util.List;

public class DatesAdapter extends RecyclerView.Adapter<DatesAdapter.DataViewHolder> {

    private Context context;
    private List<Date> list;

    public DatesAdapter(Context context, List<Date> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_date, parent, false);
        return new DataViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        Date data = list.get(position);
        holder.date_title.setText(data.date);
        holder.question_count.setText(data.question_count + " " + context.getString(R.string.questions));
        int position_i = position+1;
        holder.numbering.setText(String.valueOf(position_i));

        if (position_i >= 10) {
            holder.numbering.setPadding(10, 10, 10, 10);
        } else {
            holder.numbering.setPadding(20, 5, 20, 5);
        }

        if (position_i % 3 ==  0) {
            holder.rLyt.setBackgroundColor(context.getResources().getColor(R.color.colorPink));
        } else if (position_i % 2 == 0) {
            holder.rLyt.setBackgroundColor(context.getResources().getColor(R.color.colorBlue));
        } else {
            holder.rLyt.setBackgroundColor(context.getResources().getColor(R.color.colorAccentDark));
        }



        holder.itemView.setOnClickListener(view -> {
            if (Variables.isUserSubscribed){
                Intent intent = new Intent(context, MCQActivity.class);
                intent.putExtra("date_id",data.date_id);
                context.startActivity(intent);
            }else {
                Functions.ShowToast(context,"Please activate your Subscription");
            }


        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {
        private TextView date_title, question_count, numbering;
        private RelativeLayout rLyt;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            date_title = itemView.findViewById(R.id.date_title);
            question_count = itemView.findViewById(R.id.question_count);
            numbering = itemView.findViewById(R.id.numbering);
            rLyt = itemView.findViewById(R.id.rLyt);

        }
    }
}
