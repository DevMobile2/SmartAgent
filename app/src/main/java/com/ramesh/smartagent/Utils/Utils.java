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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Utils {
    static Context app_context;
    private static SQLiteDatabaseHandler db;
    private static String TAG = "DOWNLOADING";
    private static ArrayList<String> fileNames = new ArrayList<>();
    private static ArrayList<Long> fileSizes = new ArrayList<>();

    public static void showErrorAlert(Activity mActivity, String message) {
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Make a api call to server to fetch configs.
     */
    public static void fetchConfig(Context context) {
        db = new SQLiteDatabaseHandler(context);
        app_context = context;
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
     *
     * @param configPojo
     */
    public static void saveConfigsInDatabase(ConfigPojo configPojo) {
        for (Dependency dependency : configPojo.getDependencies()) {
            if (db != null) {
                long flag = db.addConfig(dependency);
                if (flag > 0) {
                    readFilesFromLocalStorage();
                    downloadFilesFromConfig(configPojo);
                }
            }
        }
    }

    /**
     * Download file from server
     *
     * @param configPojo
     */
    private static void downloadFilesFromConfig(ConfigPojo configPojo) {
        for (final Dependency dependency : configPojo.getDependencies()) {
            if (!fileNames.contains(dependency.getName()) && !fileSizes.contains(dependency.getSizeInBytes())) {
                Call<ResponseBody> call = MyApplication.getSerivce().downloadFileWithDynamicUrlSync(dependency.getCdn_path());

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {

                        if (response.isSuccess()) {
                            Log.d(TAG, "server contacted and has file");

                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(), dependency);

                            Log.d(TAG, "file download was a success? " + writtenToDisk);
                        } else {
                            Log.d(TAG, "server contact failed");
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });
            } else {
                Log.i(TAG, "File already exists.");
            }
        }
    }

    /**
     * save the downloaded file into local storage
     *
     * @param body
     * @param dependency
     * @return
     */
    private static boolean writeResponseBodyToDisk(ResponseBody body, Dependency dependency) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(app_context.getExternalFilesDir("resources"), dependency.getName());

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * returns cuttent timestamp
     *
     * @return
     */
    public static String getTimeStamp() {
        return String.valueOf(Calendar.getInstance().getTimeInMillis() + "_");
    }


    /**
     * Read files from device storage
     */
    public static void readFilesFromLocalStorage() {
        String path = app_context.getExternalFilesDir("resources").toString();
        File directory = new File(path);
        if (directory != null) {
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {
                fileNames.add(files[i].getName());
                fileSizes.add(files[i].length());

            }
        }

    }
}
