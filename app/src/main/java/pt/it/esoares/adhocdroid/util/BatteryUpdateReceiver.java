package pt.it.esoares.adhocdroid.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import pt.it.esoares.adhocdroid.R;

public class BatteryUpdateReceiver extends BroadcastReceiver {
    private static final String TAG = BatteryUpdateReceiver.class.getCanonicalName();
    private static final String PREF_VAL = "val";

    @Override
    public void onReceive(Context context, Intent intent) {
        int newLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int oldVal=getVal(context);
        if(newLevel==oldVal){
            return;
        }
        saveVal(context, newLevel);
        File file = new File(Environment.getExternalStorageDirectory(), "log.txt");
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    Log.e(TAG, "error creating file in: " + file.getAbsolutePath());
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "error creating file in: " + file.getAbsolutePath());
                return;
            }
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, true);
            writer.write(String.valueOf(System.currentTimeMillis()) + ": " + String.valueOf(newLevel)+"\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveVal(Context context, int val) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sharedPref.edit().putInt(PREF_VAL, val).commit();
    }

    private int getVal(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getInt(PREF_VAL, 101);
    }
}
