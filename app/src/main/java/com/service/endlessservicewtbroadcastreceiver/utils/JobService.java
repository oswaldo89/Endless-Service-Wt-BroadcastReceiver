package com.service.endlessservicewtbroadcastreceiver.utils;

import android.app.job.JobParameters;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobService extends android.app.job.JobService {

    private static String TAG = "JobService";
    private static RestartBroadcastReceiver restartBroadcastReceiver;
    private static JobService instance;
    private static JobParameters jobParameters;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        ServiceAdmin serviceAdmin = new ServiceAdmin();
        serviceAdmin.launchService(this);
        instance = this;
        JobService.jobParameters = jobParameters;

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(TAG, "Stopping job");
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        new Handler().postDelayed(() -> unregisterReceiver(restartBroadcastReceiver), 1000);

        return false;
    }

    public static void stopJob(Context context) {
        if (instance!=null && jobParameters!=null) {
            try{
                instance.unregisterReceiver(restartBroadcastReceiver);
            } catch (Exception e){
                // not registered
            }
            Log.i(TAG, "Finishing job");
            instance.jobFinished(jobParameters, true);
        }
    }
}