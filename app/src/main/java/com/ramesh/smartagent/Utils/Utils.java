package com.ramesh.smartagent.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ramesh.smartagent.MyApplication;
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

public class Utils {
    private static SQLiteDatabaseHandler db;

    public static void showErrorAlert(Activity mActivity, String message) {
        Toast.makeText(mActivity,message , Toast.LENGTH_SHORT).show();
    }

    /**
     * Make a api call to server to fetch configs.
     */
    public static void fetchConfig(Context context) {
        db = new SQLiteDatabaseHandler(context);
        Call<ResponseBody> call = MyApplication.getSerivce().fetchConfig();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    try {
                        Gson gson = new Gson();
                        String res = response.body().string();
                        JSONObject obj = new JSONObject(res);
                        ConfigPojo configPojo = gson.fromJson(res, ConfigPojo.class);
                        if (configPojo != null && configPojo.getDependencies().size() > 0) {
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
                //  Toast.makeText(MainActivity.this, getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * save config data in local database
     * @param configPojo
     */
    public static void saveConfigsInDatabase(ConfigPojo configPojo) {
        for (Dependency dependency : configPojo.getDependencies()) {
            if (db != null) {
                db.addConfig(dependency);
            }
        }
    }
}
