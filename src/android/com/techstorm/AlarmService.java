package com.techstorm;

import android.app.IntentService;
import android.content.Intent;

public class AlarmService extends IntentService {
	// Must create a default constructor
    public AlarmService() {
        // Used to name the worker thread, important only for debugging.
        super("test-service");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
    }

	@Override
	protected void onHandleIntent(Intent intent) {
		
	}
}
