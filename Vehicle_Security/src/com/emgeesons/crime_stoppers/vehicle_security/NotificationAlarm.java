package com.emgeesons.crime_stoppers.vehicle_security;

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
import android.util.Log;

public class NotificationAlarm extends BroadcastReceiver {

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
	public static void SetAlarm(Context context) {
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
		List<VehicleData> notificationData = db.getVehicleData();
		int co = 1;
		for (int i = 0; i < notificationData.size(); i++) {
			VehicleData data = notificationData.get(i);
			if (data.getexpmil().isEmpty()) {

			} else {

				long times = Long.valueOf(data.getexpmil());
				// +(1000*co)
				Calendar gc = Calendar.getInstance();
				// if (gc.getTimeInMillis() < times) {
				System.out.println(String.valueOf(times) + ""
						+ data.getvehicle_model());
				NotificationSpawn.SetAlarm(context, times);
			}
			co++;
			// }

		}

	}
}
