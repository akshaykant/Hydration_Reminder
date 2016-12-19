package com.example.android.background.sync;

import android.app.IntentService;
import android.content.Intent;

// TASK (9) Create WaterReminderIntentService and extend it from IntentService
public class WaterReminderIntentService extends IntentService {


    //  TASK (10) Create a default constructor that calls super with the name of this class
    public WaterReminderIntentService() {
        super("WaterReminderIntentService");
    }

    //  TASK (11) Override onHandleIntent
    //  TASK (12) Get the action from the Intent that started this Service
    //  TASK (13) Call ReminderTasks.executeTaskForTag and pass in the action to be performed
    @Override
    protected void onHandleIntent(Intent intent) {

        String actions = intent.getAction();
        ReminderTasks.executeTask(this, actions);
    }

}