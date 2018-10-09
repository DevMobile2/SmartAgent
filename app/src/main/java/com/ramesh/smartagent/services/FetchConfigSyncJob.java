package com.ramesh.smartagent.services;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.ramesh.smartagent.Utils.Utils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class FetchConfigSyncJob extends Job {

    public static final String TAG = "job_note_sync";

    @Override
    @NonNull
    protected Result onRunJob(@NonNull Params params) {
        Utils.fetchConfig(getContext());
        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        Set<JobRequest> jobRequests = JobManager.instance().getAllJobRequestsForTag(FetchConfigSyncJob.TAG);
        if (!jobRequests.isEmpty()) {
            return;
        }
        new JobRequest.Builder(FetchConfigSyncJob.TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(7))
                .setUpdateCurrent(true) // calls cancelAllForTag(NoteSyncJob.TAG) for you
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
                .build()
                .schedule();
    }
}
