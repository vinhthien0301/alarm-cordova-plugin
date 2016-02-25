package com.techstorm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;


@SuppressLint("SimpleDateFormat")
public class DataStorage {

	private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
	
	private static final String PREFS_NAME = "com_techstorm_alarm";
	private static final String DATA_INTERVAL = "INTERVAL";
	private static final String DATA_TIME_FROM = "TIME_FROM";
	private static final String DATA_TIME_TO = "TIME_TO";
	private static final String DATA_MP3 = "MP3";
	
	
	public static long getInterval(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getLong(DATA_INTERVAL, 0);
	}
	
	public static void setInterval(Context context, String interval) { //HH:mm:ss
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		Date date;
		try {
			date = formatter.parse(interval);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			long intervalLong = (calendar.get(Calendar.HOUR) * 60 + calendar.get(Calendar.HOUR)) * 60; 
			editor.putLong(DATA_INTERVAL, intervalLong);
			editor.commit();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static Date getTimeFrom(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		String dateString = settings.getString(DATA_TIME_FROM, "");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(formatter.parse(dateString));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return calendar.getTime();
	}
	
	public static void setTimeFrom(Context context, String timeFrom) { //HH:mm:ss
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(DATA_TIME_FROM, timeFrom);
		editor.commit();
	}
	
	public static Date getTimeTo(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		String dateString = settings.getString(DATA_TIME_TO, "");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(formatter.parse(dateString));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return calendar.getTime();
	}
	
	public static void setTimeTo(Context context, String timeTo) { //HH:mm:ss
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(DATA_TIME_TO, timeTo);
		editor.commit();
	}
	
	public static String getMp3(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString(DATA_MP3, "");
	}
	
	public static void setMp3(Context context, String mp3) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(DATA_MP3, mp3);
		editor.commit();
	}
	
}
