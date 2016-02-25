package com.techstorm;

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
	protected static final int ID_ONETIME_OFFSET = 10000;
	
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
//						PendingIntent alarmHandler = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//					    AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
					    
//					    manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis(), interval, alarmHandler);
					    
						int interval = 2000;
					    alarmIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						PendingIntent alarmHandler = PendingIntent.getBroadcast(context, ID_ONETIME_OFFSET, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
						AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
						alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, alarmHandler);
					    
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
