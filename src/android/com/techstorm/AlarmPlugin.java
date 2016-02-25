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
    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    	final Context context = this.cordova.getActivity();
    	if (action.equals("startService")) {
    		this.cordova.getActivity().runOnUiThread(new Runnable() {
				public void run() {
		    		Intent i = new Intent(context, AlarmService.class);
		            context.startService(i);
				}
			});
    	} else if (action.equals("stopService")) {
    		this.cordova.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					//TODO
		    		Intent i = new Intent(context, AlarmService.class);
		            context.stopService(i);
				}
			});
    	} else if (action.equals("schedule")) {
			this.cordova.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					try {
						String timeInterval = (String) (args.get(0).equals("null") ? "" : args.get(0));
						String timeFrom = (String) (args.get(1).equals("null") ? "" : args.get(1));
						String timeTo = (String) (args.get(2).equals("null") ? "" : args.get(2));
						
						Intent alarmIntent = new Intent(context, AlarmHandler.class);
						PendingIntent alarmHandler = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
					    AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
					    int interval = 2000;
					    manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis(), interval, alarmHandler);
					    
//						cordova.getActivity().getWindow().addFlags(
//                                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
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
