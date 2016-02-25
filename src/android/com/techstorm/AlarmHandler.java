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

import java.util.Calendar;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmHandler extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Calendar now = Calendar.getInstance();
		if (afterTime(now.getTime(), DataStorage.getTimeFrom(context))
				&& afterTime(DataStorage.getTimeTo(context), now.getTime())) {
			
		} else {
			
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
