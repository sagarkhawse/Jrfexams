package com.skteam.jrfexams.common;


import com.skteam.jrfexams.retrofit.RetrofitApi;
import com.skteam.jrfexams.retrofit.RetrofitClient;
import com.skteam.jrfexams.utils.Variables;

public class Common {

    public static RetrofitApi getAPI(){
        return RetrofitClient.getClient(Variables.BASE_URL).create(RetrofitApi.class);
    }
}

