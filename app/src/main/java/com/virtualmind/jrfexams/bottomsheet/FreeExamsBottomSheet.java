package com.virtualmind.jrfexams.bottomsheet;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.virtualmind.jrfexams.R;
import com.virtualmind.jrfexams.adapters.DatesAdapter;
import com.virtualmind.jrfexams.common.Common;
import com.virtualmind.jrfexams.models.Date;
import com.virtualmind.jrfexams.retrofit.RetrofitApi;
import com.virtualmind.jrfexams.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FreeExamsBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "FreeExamsBottomSheet";
    private BottomSheetListner mListener;
    private RetrofitApi mService;
    private RecyclerView rvFreeExams;
    private ProgressBar progressBar;
    private TextView tvTotalNum;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_free_exams,container,false);
        mService = Common.getAPI();
        rvFreeExams = view.findViewById(R.id.rvFreeExams);
        progressBar = view.findViewById(R.id.progress_bar);
        tvTotalNum = view.findViewById(R.id.tvTotalNum);
        getFreeExams();
        return view;

    }


    private interface BottomSheetListner {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListner) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getFreeExams() {
        //0 for free exams
        mService.show_all_test(0)
                .enqueue(new Callback<Date>() {
                    @Override
                    public void onResponse(@NonNull Call<Date> call, @NonNull Response<Date> response) {

                        if (response.body() != null) {
                            if (response.body().code.equals(Constants.SUCCESS)) {
                                DatesAdapter adapter = new DatesAdapter(getContext(), response.body().res);
                                tvTotalNum.setText(String.valueOf(response.body().res.size()));
                                rvFreeExams.setHasFixedSize(true);
                                rvFreeExams.setLayoutManager(new LinearLayoutManager(getContext()));
                                rvFreeExams.setAdapter(adapter);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Date> call, @NonNull Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

}
