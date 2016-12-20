/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.background.sync.ReminderTasks;
import com.example.android.background.sync.ReminderUtilities;
import com.example.android.background.sync.WaterReminderIntentService;
import com.example.android.background.utilities.PreferenceUtilities;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    ChargingBroadcastReceiver mChargingReceiver;
    IntentFilter mChargingIntentFilter;
    private TextView mWaterCountDisplay;
    private TextView mChargingCountDisplay;
    private ImageView mChargingImageView;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Get the views **/
        mWaterCountDisplay = (TextView) findViewById(R.id.tv_water_count);
        mChargingCountDisplay = (TextView) findViewById(R.id.tv_charging_reminder_count);
        mChargingImageView = (ImageView) findViewById(R.id.iv_power_increment);

        /** Set the original values in the UI **/
        updateWaterCount();
        updateChargingReminderCount();

        // TASK (4.23) Schedule the charging reminder
        ReminderUtilities.scheduleChargingReminder(this);

        /** Setup the shared preference listener **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        // TASK (5.5) Create and instantiate a new instance variable for your ChargingBroadcastReceiver
        // and an IntentFilter
        /*
         * Setup and register the broadcast receiver
         */
        mChargingIntentFilter = new IntentFilter();
        mChargingReceiver = new ChargingBroadcastReceiver();

        // TASK (5.6) Call the addAction method on your intent filter and add Intent.ACTION_POWER_CONNECTED
        // and Intent.ACTION_POWER_DISCONNECTED. This sets up an intent filter which will trigger
        // when the charging state changes.
        mChargingIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        mChargingIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);

    }

    // TASK (5.7) Override onResume and setup your broadcast receiver. Do this by calling
    // registerReceiver with the ChargingBroadcastReceiver and IntentFilter.
    @Override
    protected void onResume() {
        super.onResume();
        // The developer documentation shows how to get battery information pre Android M:
        // https://developer.android.com/training/monitoring-device-state/battery-monitoring.html
        // In Android M and beyond you can simply get a reference to the BatteryManager and call
        // isCharging.

        // TASK (6.1) Check if you are on Android M or later, if so...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // TASK (6.2) Get a BatteryManager instance using getSystemService()
            BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
            // TASK (6.3) Call isCharging on the battery manager and pass the result on to your show
            // charging method
            showCharging(batteryManager.isCharging());
        }
        // TASK (6.4) If your user is not on M+, then...
        else {
            // TASK (6.5) Create a new intent filter with the action ACTION_BATTERY_CHANGED. This is a
            // sticky broadcast that contains a lot of information about the battery state.
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

            // TASK (6.6) Set a new Intent object equal to what is returned by registerReceiver, passing in null
            // for the receiver. Pass in your intent filter as well. Passing in null means that you're
            // getting the current state of a sticky broadcast - the intent returned will contain the
            // battery information you need.
            Intent currentBatteryStatusIntent = registerReceiver(null, ifilter);

            // TASK (6.7) Get the integer extra BatteryManager.EXTRA_STATUS. Check if it matches
            // BatteryManager.BATTERY_STATUS_CHARGING or BatteryManager.BATTERY_STATUS_FULL. This means
            // the battery is currently charging.
            int batteryStatus = currentBatteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                    batteryStatus == BatteryManager.BATTERY_STATUS_FULL;

            // TASK (6.8) Update the UI using your showCharging method
            showCharging(isCharging);
        }

        /** Register the receiver for future state changes **/
        registerReceiver(mChargingReceiver, mChargingIntentFilter);
    }

    // TASK (5.8) Override onPause and unregister your receiver using the unregisterReceiver method
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mChargingReceiver);
    }



    /**
     * Updates the TextView to display the new water count from SharedPreferences
     */
    private void updateWaterCount() {
        int waterCount = PreferenceUtilities.getWaterCount(this);
        mWaterCountDisplay.setText(waterCount + "");
    }

    /**
     * Updates the TextView to display the new charging reminder count from SharedPreferences
     */
    private void updateChargingReminderCount() {
        int chargingReminders = PreferenceUtilities.getChargingReminderCount(this);
        String formattedChargingReminders = getResources().getQuantityString(
                R.plurals.charge_notification_count, chargingReminders, chargingReminders);
        mChargingCountDisplay.setText(formattedChargingReminders);

    }

    // TASK (5.1) Create a new method called showCharging which takes a boolean. This method should
    // either change the image of mChargingImageView to ic_power_pink_80px if the boolean is true
    // or R.drawable.ic_power_grey_80px it it's not. This method will eventually update the UI
    // when our broadcast receiver is triggered when the charging state changes.
    private void showCharging(boolean isCharging) {
        if (isCharging) {
            mChargingImageView.setImageResource(R.drawable.ic_power_pink_80px);

        } else {
            mChargingImageView.setImageResource(R.drawable.ic_power_grey_80px);
        }
    }


    /**
     * Adds one to the water count and shows a toast
     */
    public void incrementWater(View view) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(this, R.string.water_chug_toast, Toast.LENGTH_SHORT);
        mToast.show();

        // TASK (15) Create an explicit intent for WaterReminderIntentService
        Intent incrementWaterCount = new Intent(this, WaterReminderIntentService.class);

        // TASK (16) Set the action of the intent to ACTION_INCREMENT_WATER_COUNT
        incrementWaterCount.setAction(ReminderTasks.ACTION_INCREMENT_WATER_COUNT);

        // TASK (17) Call startService and pass the explicit intent you just created
        startService(incrementWaterCount);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /** Cleanup the shared preference listener **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * This is a listener that will update the UI when the water count or charging reminder counts
     * change
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceUtilities.KEY_WATER_COUNT.equals(key)) {
            updateWaterCount();
        } else if (PreferenceUtilities.KEY_CHARGING_REMINDER_COUNT.equals(key)) {
            updateChargingReminderCount();
        }
    }

    // TASK (5.2) Create an inner class called ChargingBroadcastReceiver that extends BroadcastReceiver
    private class ChargingBroadcastReceiver extends BroadcastReceiver {
        // TASK (5.3) Override onReceive to get the action from the intent and see if it matches the
        // Intent.ACTION_POWER_CONNECTED. If it matches, it's charging. If it doesn't match, it's not
        // charging.
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean isCharging = (action.equals(Intent.ACTION_POWER_CONNECTED));
            // TASK (5.4) Update the UI using the showCharging method you wrote
            showCharging(isCharging);
        }
    }
}