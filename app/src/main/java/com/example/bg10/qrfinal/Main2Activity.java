package com.example.bg10.qrfinal;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {
    //AutoCompleteTextView acUsername;
    //EditText etPassword;
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEdit;
    private static final int JOB_ID =101;
    private JobScheduler jobScheduler;
    private JobInfo jobInfo;
    ComponentName componentName;
    String acUsername, etStaffId, strStaffRole, strStaffHub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        componentName = new ComponentName(this,NetworkSchedulerService.class);

        try{
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            acUsername = (String) b.get("staff_name");
            etStaffId = (String) b.get("staff_id");
            strStaffRole = (String) b.get("staff_role");
            strStaffHub = (String) b.get("Staff_hub");

            /*strStaffHub = "Hub";
            strStaffRole = "N/A";*/

        }catch(Exception e){

        }


        final DBHandler db = new DBHandler(this);

        //using shared preferences to persist the user's name
        prefs = getSharedPreferences("Preferences1", MODE_PRIVATE);
        prefsEdit = prefs.edit();
        prefsEdit.putString("username", acUsername);
        prefsEdit.putString("staff_id", etStaffId);
        prefsEdit.putString("hub", strStaffHub);


            if (strStaffRole.equals("SD") || strStaffRole.equals("CCO") || strStaffRole.equals("SCCO")) {
                prefsEdit.putString("department", "SD");
            } else if (strStaffRole.equals("FOR") || strStaffRole.equals("FOD") || strStaffRole.equals("LFO")) {
                prefsEdit.putString("department", "FO");
            } else if (strStaffRole.equals("SFO")) {
                prefsEdit.putString("department", "SFO");
            }

            prefsEdit.putString("appVersion", BuildConfig.VERSION_NAME);
            prefsEdit.commit();

            if (!db.logged(acUsername, etStaffId)) {
                db.updateLogged(acUsername, etStaffId, strStaffRole, strStaffHub); //
            }

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID,componentName);
            builder.setMinimumLatency(2000);

            //builder.setTriggerContentMaxDelay(100000);
            builder.setPersisted(true);
            jobInfo = builder.build();
            jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(jobInfo);
            //Toast.makeText(this, "Job Scheduled", Toast.LENGTH_SHORT).show();

        }else{

            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID,componentName);
            builder.setPeriodic(2000);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            builder.setPersisted(true);
            jobInfo = builder.build();
            jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(jobInfo);
            //Toast.makeText(this, "Job Scheduled < N", Toast.LENGTH_SHORT).show();
        }*/

        startActivity(new Intent(getApplicationContext(),Operations.class));
    }

   /* public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            componentName = new ComponentName(this,NetworkSchedulerService.class);
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID,componentName);
            builder.setMinimumLatency(2000);

            //builder.setTriggerContentMaxDelay(100000);
            builder.setPersisted(true);
            jobInfo = builder.build();
            jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(jobInfo);
            //Toast.makeText(this, "Job Scheduled", Toast.LENGTH_SHORT).show();

        }else{

            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID,componentName);
            builder.setPeriodic(2000);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            builder.setPersisted(true);
            jobInfo = builder.build();
            jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(jobInfo);
            //Toast.makeText(this, "" + "Scheduled", Toast.LENGTH_SHORT).show();
        }
    }*/

}

