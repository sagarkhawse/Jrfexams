package com.virtualmind.jrfexams.common;


import com.virtualmind.jrfexams.retrofit.RetrofitApi;
import com.virtualmind.jrfexams.retrofit.RetrofitClient;
import com.virtualmind.jrfexams.utils.Variables;

public class Common {

    public static RetrofitApi getAPI(){
        return RetrofitClient.getClient(Variables.BASE_URL).create(RetrofitApi.class);
    }
}

