package com.ramesh.smartagent;

import android.app.Activity;
import android.app.Application;

import com.evernote.android.job.JobManager;
import com.ramesh.smartagent.Utils.UtilsServer;
import com.ramesh.smartagent.retrofit.GsonStringConverterFactory;
import com.ramesh.smartagent.retrofit.SmartAgentService;
import com.ramesh.smartagent.services.FetchJobCreator;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class MyApplication extends Application{


    private static Retrofit mRetrofit;
    private static OkHttpClient httpClient= new OkHttpClient();;
    private static Activity mCurrentActivity = null;
    private static SmartAgentService service;


    @Override
    public void onCreate() {
        super.onCreate();
        JobManager.create(this).addJobCreator(new FetchJobCreator());
    }

    /**
     * returns Retrofit instance.
     * @return
     */
    public static synchronized Retrofit getRetrofit()
    {
        if(mRetrofit==null)
        {
            httpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                }
            });
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.interceptors().add(interceptor);
            httpClient.setReadTimeout(1, TimeUnit.MINUTES);
            httpClient.setWriteTimeout(1, TimeUnit.MINUTES);
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(UtilsServer.SERVER_URL)
                    .addConverterFactory(new GsonStringConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build();


        }
        return mRetrofit;
    }
    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        MyApplication.mCurrentActivity = mCurrentActivity;
    }

    public static synchronized SmartAgentService getSerivce()
    {
        if (service == null) {
            service = getRetrofit().create(SmartAgentService.class);
        }
        return service;
    }

}
