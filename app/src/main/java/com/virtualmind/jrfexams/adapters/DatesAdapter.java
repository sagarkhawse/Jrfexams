package com.virtualmind.jrfexams.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.virtualmind.jrfexams.R;
import com.virtualmind.jrfexams.activities.MCQActivity;

import com.virtualmind.jrfexams.common.Common;
import com.virtualmind.jrfexams.models.Date;
import com.virtualmind.jrfexams.retrofit.RetrofitApi;
import com.virtualmind.jrfexams.utils.Constants;
import com.virtualmind.jrfexams.utils.Functions;
import com.virtualmind.jrfexams.utils.Variables;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        int position_i = position + 1;
        holder.numbering.setText(String.valueOf(position_i));

        if (position_i >= 10) {
            holder.numbering.setPadding(10, 10, 10, 10);
        } else {
            holder.numbering.setPadding(20, 5, 20, 5);
        }

        if (position_i % 3 == 0) {
            holder.rLyt.setBackgroundColor(context.getResources().getColor(R.color.colorPink));
        } else if (position_i % 2 == 0) {
            holder.rLyt.setBackgroundColor(context.getResources().getColor(R.color.colorBlue));
        } else {
            holder.rLyt.setBackgroundColor(context.getResources().getColor(R.color.colorAccentDark));
        }


        holder.itemView.setOnClickListener(view -> {
            if (Variables.isUserSubscribed) {
                Intent intent = new Intent(context, MCQActivity.class);
                intent.putExtra("date_id", data.date_id);
                intent.putExtra("title", data.date);
                context.startActivity(intent);
            } else {
                Functions.ShowToast(context, "Please activate your Subscription");
            }


        });


        //for admin only
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
//                        .setMessage("delete karna hai kya bhai isko?")
//                        .setPositiveButton("Ha bhai krde", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                                add_date_api(data.date_id);
//                                dialogInterface.dismiss();
//
//                            }
//                        })
//                        .setNegativeButton("Nahi yr maat kr", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        });
//
//                Dialog dialog = builder.create();
//                dialog.show();
//
//                return false;
//            }
//        });


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


    private void add_date_api(int date_id) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setTitle("Deleting Date");
        pd.setMessage("please wait...");
        pd.setCancelable(false);
        pd.show();
        RetrofitApi mService = Common.getAPI();
        mService.admin_add_date(date_id, "", "delete")
                .enqueue(new Callback<Date>() {
                    @Override
                    public void onResponse(Call<Date> call, Response<Date> response) {
                        if (response.body().code.equals(Constants.SUCCESS)) {
                            Functions.ShowToast(context, "date added successfully");
                        } else {
                            Functions.ShowToast(context, response.body().error_msg);
                        }
                        pd.dismiss();

                    }

                    @Override
                    public void onFailure(Call<Date> call, Throwable t) {
                        pd.dismiss();
                        Functions.ShowToast(context, "" + t.getMessage());
                    }
                });
    }


}
