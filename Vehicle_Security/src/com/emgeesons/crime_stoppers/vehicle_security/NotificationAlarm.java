package com.emgeesons.crime_stoppers.vehicle_security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class NotificationAlarm extends BroadcastReceiver {

	static String title;

	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
		wl.acquire();

		callAlarm(context);

		wl.release();
	}

	@SuppressLint("NewApi")
	public static void SetAlarm(Context context, String titles) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, NotificationAlarm.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		Calendar gc = Calendar.getInstance();
		gc.setTimeInMillis(System.currentTimeMillis());
		gc.set(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH),
				gc.get(Calendar.DAY_OF_MONTH), 23, 59);
		am.setRepeating(AlarmManager.RTC_WAKEUP, gc.getTimeInMillis()
				+ TimeUnit.MINUTES.toMillis(2), AlarmManager.INTERVAL_DAY, pi);
		title = titles;
		callAlarm(context);
	}

	public static void CancelAlarm(Context context) {
		Intent intent = new Intent(context, NotificationAlarm.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

	public static void callAlarm(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		List<VehicleData> vehicleData = db.getVehicleData();
		for (int i = 0; i < vehicleData.size(); i++) {
			VehicleData data = vehicleData.get(i);
			if (data.getvehicle_insexp().isEmpty()) {

			} else {
				long times = Long.valueOf(data.getexpmil());
				// if (i == 1) {
				// NotificationSpawn.SetAlarm(context, times + 70000);
				// }
				NotificationSpawn.SetAlarm(context, times + 30000,title);
			}

		}
	}

}