package com.techstorm;

import java.util.Calendar;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * This class echoes a string called from JavaScript.
 */
public class AlarmPlugin extends CordovaPlugin {
	public static final int ID_ONETIME_OFFSET = 10000;
	
    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    	final Context context = this.cordova.getActivity();
    	if (action.equals("schedule")) {
			this.cordova.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					try {
						DataStorage.setInterval(context, args.get(0).toString());
						DataStorage.setTimeFrom(context, args.get(1).toString());
						DataStorage.setTimeTo(context, args.get(2).toString());
						DataStorage.setMp3(context, args.get(3).toString());
						
						Intent alarmIntent = new Intent(context, AlarmHandler.class);
						long interval = DataStorage.getInterval(context);
						Calendar alarmDate = Calendar.getInstance();
					    alarmIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						PendingIntent sender = PendingIntent.getBroadcast(context, ID_ONETIME_OFFSET, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
						AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
						alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmDate.getTimeInMillis(), interval*1000, sender);
						
						callbackContext.success();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}
        
        return true;
    }    
}
