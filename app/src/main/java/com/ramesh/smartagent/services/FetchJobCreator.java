package com.ramesh.smartagent.services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class FetchJobCreator implements JobCreator {

    @Override
    @Nullable
    public Job create(@NonNull String tag) {
        switch (tag) {
            case FetchConfigSyncJob.TAG:
                return new FetchConfigSyncJob();
            default:
                return null;
        }
    }
}
