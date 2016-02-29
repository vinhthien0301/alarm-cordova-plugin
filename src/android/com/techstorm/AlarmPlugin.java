package com.techstorm;

import android.app.Activity;
import android.content.Context;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.appplant.cordova.plugin.localnotification.TriggerReceiver;
import de.appplant.cordova.plugin.notification.Manager;
import de.appplant.cordova.plugin.notification.Notification;

/**
 * This class echoes a string called from JavaScript.
 */
public class AlarmPlugin extends CordovaPlugin {
	public static final int ID_ONETIME_OFFSET = 10000;

	// Reference to the web view for static access
	private static CordovaWebView webView = null;

	// Indicates if the device is ready (to receive events)
	private static Boolean deviceready = false;

	// To inform the user about the state of the app in callbacks
	protected static Boolean isInBackground = true;

	// Queues all events before deviceready
	private static ArrayList<String> eventQueue = new ArrayList<String>();

	/**
	 * Called after plugin construction and fields have been initialized.
	 * Prefer to use pluginInitialize instead since there is no value in
	 * having parameters on the initialize() function.
	 *
	 * pluginInitialize is not available for cordova 3.0-3.5 !
	 */
	@Override
	public void initialize (CordovaInterface cordova, CordovaWebView webView) {
		AlarmPlugin.webView = super.webView;
	}

	/**
	 * Called when the system is about to start resuming a previous activity.
	 *
	 * @param multitasking
	 *      Flag indicating if multitasking is turned on for app
	 */
	@Override
	public void onPause(boolean multitasking) {
		super.onPause(multitasking);
		isInBackground = true;
	}

	/**
	 * Called when the activity will start interacting with the user.
	 *
	 * @param multitasking
	 *      Flag indicating if multitasking is turned on for app
	 */
	@Override
	public void onResume(boolean multitasking) {
		super.onResume(multitasking);
		isInBackground = false;
		deviceready();
	}

	/**
	 * The final call you receive before your activity is destroyed.
	 */
	@Override
	public void onDestroy() {
		deviceready = false;
		isInBackground = true;
	}

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
						DataStorage.setTextMessage(context, args.get(4).toString());

						long interval = DataStorage.getInterval(context);

//						Intent alarmIntent = new Intent(context, AlarmHandler.class);
//						long interval = DataStorage.getInterval(context);
////						long interval = 2;
//						Calendar alarmDate = Calendar.getInstance();
//					    alarmIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//						PendingIntent sender = PendingIntent.getBroadcast(context, ID_ONETIME_OFFSET, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//						AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//						alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmDate.getTimeInMillis(), interval * 1000, sender);

						JSONObject options = new JSONObject();
						options.put("text", DataStorage.getTextMessage(context));
						options.put("every", String.valueOf(interval));
						options.put("sound", "file://sounds/"+DataStorage.getMp3(context));
						options.put("icon", "res://icon");
						options.put("smallIcon", "res://ic_popup");
						JSONArray arrayInput = new JSONArray();
						arrayInput.put(options);
						schedule(arrayInput);

						callbackContext.success();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}
        
        return true;
    }

	/**
	 * Schedule multiple local notifications.
	 *
	 * @param notifications
	 *      Properties for each local notification
	 */
	private void schedule (JSONArray notifications) {
		for (int i = 0; i < notifications.length(); i++) {
			JSONObject options = notifications.optJSONObject(i);

			Notification notification =
					getNotificationMgr().schedule(options, TriggerReceiver.class);

			fireEvent("schedule", notification);
		}
	}

	/**
	 * Update multiple local notifications.
	 *
	 * @param updates
	 *      Notification properties including their IDs
	 */
	private void update (JSONArray updates) {
		for (int i = 0; i < updates.length(); i++) {
			JSONObject update = updates.optJSONObject(i);
			int id = update.optInt("id", 0);

			Notification notification =
					getNotificationMgr().update(id, update, TriggerReceiver.class);

			if (notification == null)
				continue;

			fireEvent("update", notification);
		}
	}

	/**
	 * Cancel multiple local notifications.
	 *
	 * @param ids
	 *      Set of local notification IDs
	 */
	private void cancel (JSONArray ids) {
		for (int i = 0; i < ids.length(); i++) {
			int id = ids.optInt(i, 0);

			Notification notification =
					getNotificationMgr().cancel(id);

			if (notification == null)
				continue;

			fireEvent("cancel", notification);
		}
	}

	/**
	 * Cancel all scheduled notifications.
	 */
	private void cancelAll() {
		getNotificationMgr().cancelAll();
		fireEvent("cancelall");
	}

	/**
	 * Clear multiple local notifications without canceling them.
	 *
	 * @param ids
	 *      Set of local notification IDs
	 */
	private void clear(JSONArray ids){
		for (int i = 0; i < ids.length(); i++) {
			int id = ids.optInt(i, 0);

			Notification notification =
					getNotificationMgr().clear(id);

			if (notification == null)
				continue;

			fireEvent("clear", notification);
		}
	}

	/**
	 * Clear all triggered notifications without canceling them.
	 */
	private void clearAll() {
		getNotificationMgr().clearAll();
		fireEvent("clearall");
	}

	/**
	 * If a notification with an ID is present.
	 *
	 * @param id
	 *      Notification ID
	 * @param command
	 *      The callback context used when calling back into JavaScript.
	 */
	private void isPresent (int id, CallbackContext command) {
		boolean exist = getNotificationMgr().exist(id);

		PluginResult result = new PluginResult(
				PluginResult.Status.OK, exist);

		command.sendPluginResult(result);
	}

	/**
	 * If a notification with an ID is scheduled.
	 *
	 * @param id
	 *      Notification ID
	 * @param command
	 *      The callback context used when calling back into JavaScript.
	 */
	private void isScheduled (int id, CallbackContext command) {
		boolean exist = getNotificationMgr().exist(
				id, Notification.Type.SCHEDULED);

		PluginResult result = new PluginResult(
				PluginResult.Status.OK, exist);

		command.sendPluginResult(result);
	}

	/**
	 * If a notification with an ID is triggered.
	 *
	 * @param id
	 *      Notification ID
	 * @param command
	 *      The callback context used when calling back into JavaScript.
	 */
	private void isTriggered (int id, CallbackContext command) {
		boolean exist = getNotificationMgr().exist(
				id, Notification.Type.TRIGGERED);

		PluginResult result = new PluginResult(
				PluginResult.Status.OK, exist);

		command.sendPluginResult(result);
	}

	/**
	 * Set of IDs from all existent notifications.
	 *
	 * @param command
	 *      The callback context used when calling back into JavaScript.
	 */
	private void getAllIds (CallbackContext command) {
		List<Integer> ids = getNotificationMgr().getIds();

		command.success(new JSONArray(ids));
	}

	/**
	 * Set of IDs from all scheduled notifications.
	 *
	 * @param command
	 *      The callback context used when calling back into JavaScript.
	 */
	private void getScheduledIds (CallbackContext command) {
		List<Integer> ids = getNotificationMgr().getIdsByType(
				Notification.Type.SCHEDULED);

		command.success(new JSONArray(ids));
	}

	/**
	 * Set of IDs from all triggered notifications.
	 *
	 * @param command
	 *      The callback context used when calling back into JavaScript.
	 */
	private void getTriggeredIds (CallbackContext command) {
		List<Integer> ids = getNotificationMgr().getIdsByType(
				Notification.Type.TRIGGERED);

		command.success(new JSONArray(ids));
	}

	/**
	 * Options from local notification.
	 *
	 * @param ids
	 *      Set of local notification IDs
	 * @param command
	 *      The callback context used when calling back into JavaScript.
	 */
	private void getSingle (JSONArray ids, CallbackContext command) {
		getOptions(ids.optString(0), Notification.Type.ALL, command);
	}

	/**
	 * Options from scheduled notification.
	 *
	 * @param ids
	 *      Set of local notification IDs
	 * @param command
	 *      The callback context used when calling back into JavaScript.
	 */
	private void getSingleScheduled (JSONArray ids, CallbackContext command) {
		getOptions(ids.optString(0), Notification.Type.SCHEDULED, command);
	}

	/**
	 * Options from triggered notification.
	 *
	 * @param ids
	 *      Set of local notification IDs
	 * @param command
	 *      The callback context used when calling back into JavaScript.
	 */
	private void getSingleTriggered (JSONArray ids, CallbackContext command) {
		getOptions(ids.optString(0), Notification.Type.TRIGGERED, command);
	}

	/**
	 * Set of options from local notification.
	 *
	 * @param ids
	 *      Set of local notification IDs
	 * @param command
	 *      The callback context used when calling back into JavaScript.
	 */
	private void getAll (JSONArray ids, CallbackContext command) {
		getOptions(ids, Notification.Type.ALL, command);
	}

	/**
	 * Set of options from scheduled notifications.
	 *
	 * @param ids
	 *      Set of local notification IDs
	 * @param command
	 *      The callback context used when calling back into JavaScript.
	 */
	private void getScheduled (JSONArray ids, CallbackContext command) {
		getOptions(ids, Notification.Type.SCHEDULED, command);
	}

	/**
	 * Set of options from triggered notifications.
	 *
	 * @param ids
	 *      Set of local notification IDs
	 * @param command
	 *      The callback context used when calling back into JavaScript.
	 */
	private void getTriggered (JSONArray ids, CallbackContext command) {
		getOptions(ids, Notification.Type.TRIGGERED, command);
	}

	/**
	 * Options from local notification.
	 *
	 * @param id
	 *      Set of local notification IDs
	 * @param type
	 *      The local notification life cycle type
	 * @param command
	 *      The callback context used when calling back into JavaScript.
	 */
	private void getOptions (String id, Notification.Type type,
							 CallbackContext command) {

		JSONArray ids = new JSONArray().put(id);
		PluginResult result;

		List<JSONObject> options =
				getNotificationMgr().getOptionsBy(type, toList(ids));

		if (options.isEmpty()) {
			// Status.NO_RESULT led to no callback invocation :(
			// Status.OK        led to no NPE and crash
			result = new PluginResult(PluginResult.Status.NO_RESULT);
		} else {
			result = new PluginResult(PluginResult.Status.OK, options.get(0));
		}

		command.sendPluginResult(result);
	}

	/**
	 * Set of options from local notifications.
	 *
	 * @param ids
	 *      Set of local notification IDs
	 * @param type
	 *      The local notification life cycle type
	 * @param command
	 *      The callback context used when calling back into JavaScript.
	 */
	private void getOptions (JSONArray ids, Notification.Type type,
							 CallbackContext command) {

		List<JSONObject> options;

		if (ids.length() == 0) {
			options = getNotificationMgr().getOptionsByType(type);
		} else {
			options = getNotificationMgr().getOptionsBy(type, toList(ids));
		}

		command.success(new JSONArray(options));
	}

	/**
	 * Call all pending callbacks after the deviceready event has been fired.
	 */
	private static synchronized void deviceready () {
		isInBackground = false;
		deviceready = true;

		for (String js : eventQueue) {
			sendJavascript(js);
		}

		eventQueue.clear();
	}

	/**
	 * Fire given event on JS side. Does inform all event listeners.
	 *
	 * @param event
	 *      The event name
	 */
	private void fireEvent (String event) {
		fireEvent(event, null);
	}

	/**
	 * Fire given event on JS side. Does inform all event listeners.
	 *
	 * @param event
	 *      The event name
	 * @param notification
	 *      Optional local notification to pass the id and properties.
	 */
	static void fireEvent (String event, Notification notification) {
		String state = getApplicationState();
		String params = "\"" + state + "\"";

		if (notification != null) {
			params = notification.toString() + "," + params;
		}

		String js = "cordova.plugins.notification.local.core.fireEvent(" +
				"\"" + event + "\"," + params + ")";

		sendJavascript(js);
	}

	/**
	 * Use this instead of deprecated sendJavascript
	 *
	 * @param js
	 *       JS code snippet as string
	 */
	private static synchronized void sendJavascript(final String js) {

		if (!deviceready) {
			eventQueue.add(js);
			return;
		}
		Runnable jsLoader = new Runnable() {
			public void run() {
				webView.loadUrl("javascript:" + js);
			}
		};
		try {
			Method post = webView.getClass().getMethod("post",Runnable.class);
			post.invoke(webView,jsLoader);
		} catch(Exception e) {

			((Activity)(webView.getContext())).runOnUiThread(jsLoader);
		}
	}

	/**
	 * Convert JSON array of integers to List.
	 *
	 * @param ary
	 *      Array of integers
	 */
	private List<Integer> toList (JSONArray ary) {
		ArrayList<Integer> list = new ArrayList<Integer>();

		for (int i = 0; i < ary.length(); i++) {
			list.add(ary.optInt(i));
		}

		return list;
	}

	/**
	 * Current application state.
	 *
	 * @return
	 *      "background" or "foreground"
	 */
	static String getApplicationState () {
		return isInBackground ? "background" : "foreground";
	}

	/**
	 * Notification manager instance.
	 */
	private Manager getNotificationMgr() {
		return Manager.getInstance(cordova.getActivity());
	}

}
