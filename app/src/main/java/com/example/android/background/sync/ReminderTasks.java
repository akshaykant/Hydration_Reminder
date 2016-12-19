package com.example.android.background.sync;

import android.content.Context;

import com.example.android.background.utilities.NotificationUtils;
import com.example.android.background.utilities.PreferenceUtilities;

// TASK (1) Create a class called ReminderTasks
public class ReminderTasks {

    //  TASK (3.2) Add a public static constant called ACTION_DISMISS_NOTIFICATION
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
    // TASK(2) Create a public static constant String called ACTION_INCREMENT_WATER_COUNT
    public static String ACTION_INCREMENT_WATER_COUNT = "increment-water-count";

    // TASK (6) Create a public static void method called executeTask
    public static void executeTask(Context context, String action) {


        // TASK(7) Add a Context called context and String parameter called action to the parameter list
        // TASK (8) If the action equals ACTION_INCREMENT_WATER_COUNT, call this class's incrementWaterCount
        if (ACTION_INCREMENT_WATER_COUNT.equals(action)) {
            incrementWaterCount(context);
        }
        //      TASK (3.3) If the user ignored the reminder, clear the notification
        else if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            NotificationUtils.clearAllNotifications(context);
        }
    }

    // TASK (3) Create a private static void method called incrementWaterCount
    private static void incrementWaterCount(Context context) {

        // TASK (4) Add a Context called context to the argument list
        // TASK (5) From incrementWaterCount, call the PreferenceUtility method that will ultimately update the water count
        PreferenceUtilities.incrementWaterCount(context);

        //      TASK (3.4) If the water count was incremented, clear any notifications
        NotificationUtils.clearAllNotifications(context);

    }
}