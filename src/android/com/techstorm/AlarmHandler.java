package com.techstorm;
/*
KeepAliveHandler.java
Copyright (C) 2013  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class AlarmHandler extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
		WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
		KeyguardManager keyguardManager = (KeyguardManager) context.getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE); 
		KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
		
		Calendar now = Calendar.getInstance();
		if (afterTime(now.getTime(), DataStorage.getTimeFrom(context))
				&& afterTime(DataStorage.getTimeTo(context), now.getTime())) {
			try {
				AssetFileDescriptor afd = context.getAssets().openFd("www/sounds/"+DataStorage.getMp3(context));
				MediaPlayer player = new MediaPlayer();
				player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
				player.prepare();
				player.start();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			wakeLock.acquire();
			keyguardLock.disableKeyguard();
			wakeLock.release();
			
		} else {
			Intent alarmIntent = new Intent(context, AlarmHandler.class);
			alarmIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent alarmHandler = PendingIntent.getBroadcast(context, AlarmPlugin.ID_ONETIME_OFFSET, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(alarmHandler);
			
			wakeLock.release();
			keyguardLock.reenableKeyguard();
		}
	}
	
	private static boolean afterTime(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		if (cal1.get(Calendar.HOUR_OF_DAY) > cal2.get(Calendar.HOUR_OF_DAY)) {
			return true;
		}
		if (cal1.get(Calendar.MINUTE) > cal2.get(Calendar.MINUTE)) {
			return true;
		}
		if (cal1.get(Calendar.SECOND) > cal2.get(Calendar.SECOND)) {
			return true;
		}
		return false;
	}

}
