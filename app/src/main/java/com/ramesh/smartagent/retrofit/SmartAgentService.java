package com.ramesh.smartagent.retrofit;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.GET;

public interface SmartAgentService {
    @GET("/fetch_config")
    Call<ResponseBody> fetchConfig();
}
