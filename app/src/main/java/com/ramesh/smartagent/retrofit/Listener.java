package com.ramesh.smartagent.retrofit;

import android.app.Activity;
import android.app.ProgressDialog;

import com.ramesh.smartagent.R;
import com.ramesh.smartagent.Utils.BaseActivity;
import com.ramesh.smartagent.Utils.Utils;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by RameshK on 25-11-2015.
 *
 */
public class Listener implements Callback<ResponseBody> {

    Type tt;
    RetrofitService listner;
    ProgressDialog dialog;
    Activity mActivity;
    WeakReference<Activity> activity;

    public Listener(RetrofitService listner, String title, boolean showProgress, Activity activity) {
        //tt=t;
        this.listner = listner;
        if(activity != null){
            dialog =((BaseActivity) activity).dialog;
            if(dialog!=null)
                dialog.setCancelable(false);
            this.activity =  new WeakReference<>(activity);
            mActivity = (BaseActivity)this.activity.get();
            if (dialog!=null && title != null && title.length() > 0 ) {
                dialog.setTitle(title);
            }
            else
            {
                dialog.setTitle(null);
            }
            if (showProgress && !mActivity.isFinishing() && dialog!=null && !dialog.isShowing()) {
                dialog.show();
            }
        }
    }

    @Override
    public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
        try {
            /*if(mActivity != null && !mActivity.isFinishing() && dialog!=null && dialog.isShowing())
                dialog.dismiss();*/
            if (!response.isSuccess()) {
                Utils.showErrorAlert(mActivity,mActivity.getString(R.string.error_something_went_wrong));
                listner.onSuccess(response.message(), 2, null);
            } else {
                String res = response.body().string();
                JSONObject obj = new JSONObject(res);
                if (obj.has("statusCode")){
                    if (obj.getInt("statusCode") == 401) {
                        if(obj.has("errors") &&
                                obj.get("errors").toString().toLowerCase().contains("session expired")){
                        //    mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                            mActivity.finishAffinity();
                        }else{
                            listner.onSuccess(res, 3, null);
                        }

                    } else {
                     //   showError( obj);
                    }

                }else if (obj.has("code")){

                    if (obj.getInt("code") == 1) {
                        listner.onSuccess(res, 1, null);
                    } else {
                      //  showError( obj);
                    }

                }else {
               //     showError( obj);
                }
            }

            if(mActivity != null && !mActivity.isFinishing() && dialog!=null && dialog.isShowing())
                dialog.dismiss();
        } catch (IOException | JSONException | IllegalArgumentException e) {
            e.printStackTrace();
            String msg = "";
            if(e instanceof SocketTimeoutException)
                msg = "Connection time out. Please try again";
            else
                msg = "Please check your internet connection";
            listner.onSuccess(msg, 2, null);
        }
    }



/*    public void showError(JSONObject obj) {
        if (!obj.isNull("message")) {
            try {
//                obj.getJSONArray("message").getString(0)
                Utils.showErrorAlert( mActivity, obj.getString("message") );
            } catch (JSONException e) {
                e.printStackTrace();
                Utils.showErrorAlert(mActivity, Utils.ERROR_SOMETHING);
            } finally {
                listner.onSuccess(obj.toString(), 2, null);
            }
        } else {

            Utils.showErrorAlert(mActivity, Utils.ERROR_SOMETHING);
            listner.onSuccess("", 2, null);
        }
    }*/


    @Override
    public void onFailure(Throwable t) {
        String msg;
        if(dialog!=null && dialog.isShowing())
        dialog.dismiss();
        if(t instanceof SocketTimeoutException)
            msg = "Connection time out. Please try again";
        else
            msg = "An internal server error occurred";
        listner.onSuccess(msg, 2, t);
        if(t!=null)
            t.printStackTrace();
    }



}
