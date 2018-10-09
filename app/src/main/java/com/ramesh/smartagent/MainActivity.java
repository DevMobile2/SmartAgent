package com.ramesh.smartagent;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import com.ramesh.smartagent.Utils.Utils;
import com.ramesh.smartagent.services.FetchConfigSyncJob;
import com.ramesh.smartagent.sqlite.SQLiteDatabaseHandler;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabaseHandler db;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new SQLiteDatabaseHandler(this);
        Utils.fetchConfig(this);
        //fetch configs from server periodically in the background.
        FetchConfigSyncJob.scheduleJob();
    }

}
