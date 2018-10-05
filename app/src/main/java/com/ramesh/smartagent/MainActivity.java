package com.ramesh.smartagent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ramesh.smartagent.pojos.ConfigPojo;
import com.ramesh.smartagent.pojos.Dependency;
import com.ramesh.smartagent.sqlite.SQLiteDatabaseHandler;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new SQLiteDatabaseHandler(this);
        fetchConfig();
    }

    private void fetchConfig() {
        Call<ResponseBody> call = MyApplication.getSerivce().fetchConfig();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if(response.isSuccess()){
                    try {
                        Gson gson=new Gson();
                        String res = response.body().string();
                        JSONObject obj = new JSONObject(res);
                        ConfigPojo configPojo= gson.fromJson(res, ConfigPojo.class);
                        if(configPojo!=null && configPojo.getDependencies().size()>0){
                            saveConfigsInDatabase(configPojo);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("error", t.getMessage());
                Toast.makeText(MainActivity.this, getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveConfigsInDatabase(ConfigPojo configPojo) {
        for(Dependency dependency:configPojo.getDependencies()) {
            if (db != null) {
                db.addConfig(dependency);
            }
        }
    }
}
