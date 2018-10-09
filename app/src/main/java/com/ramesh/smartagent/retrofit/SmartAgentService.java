package com.ramesh.smartagent.retrofit;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Url;

public interface SmartAgentService {
    @GET("/fetch_config")
    Call<ResponseBody> fetchConfig();

    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);
}
