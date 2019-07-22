package com.example.bg10.qrfinal;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.widget.Toast;

public class NetworkSchedulerService extends JobService {
    private ConnectivityReceiver connectivityReceiver;
    private ConnectivityReceiver2 connectivityReceiver2;

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        connectivityReceiver =new ConnectivityReceiver(getApplicationContext()){
            @Override
            protected void onPostExecute(String s){
                //Toast.makeText(NetworkSchedulerService.this, s, Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    jobFinished(jobParameters,true);}

                else{
                    jobFinished(jobParameters,false);}
            }
        };
        connectivityReceiver2 =new ConnectivityReceiver2(getApplicationContext()){
            @Override
            protected void onPostExecute(String s){
                //Toast.makeText(NetworkSchedulerService.this, s, Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    jobFinished(jobParameters,true);}

                else{
                    jobFinished(jobParameters,false);}
            }
        };

        connectivityReceiver.execute();
        connectivityReceiver2.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        connectivityReceiver.cancel(true);
        connectivityReceiver2.cancel(true);
        return false;
    }
}