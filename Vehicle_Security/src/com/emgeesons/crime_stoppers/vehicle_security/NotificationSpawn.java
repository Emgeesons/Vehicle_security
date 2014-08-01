package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;

public class NotificationSpawn extends BroadcastReceiver {
	String name;
	static DatabaseHandler db;
	static SQLiteDatabase dbb;

	@Override
	public void onReceive(Context context, Intent intent) {

		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
		wl.acquire();

		Intent in = new Intent(context, MainActivity.class);

		in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, in,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Uri alarmSound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		String time = intent.getStringExtra("time");
		db = new DatabaseHandler(context);
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();
		SQLiteDatabase dbbb = this.db.getReadableDatabase();
		String selectQuery = "SELECT vehicle_model FROM Vehicle_info WHERE vehicle_expmil = '"
				+ time + "'";
		Cursor cursor = dbbb.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				name = cursor.getString(0);
			} while (cursor.moveToNext());
		}

		cursor.getCount();

		Notification noti = new NotificationCompat.Builder(context)
				.setContentTitle("My Wheel")
				.setSmallIcon(R.drawable.app_icon)
				.setContentIntent(pIntent)
				.setContentText(
						"Your " + name + " is due for service in a week")
				.setSound(alarmSound).build();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, noti);

		wl.release();
	}

	public static void SetAlarm(Context context, long time) {

		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, NotificationSpawn.class);
		i.putExtra("time", String.valueOf(time));
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// time check .
		if (System.currentTimeMillis() <= time) {
			System.out.println(System.currentTimeMillis() - time + "" + time);
			am.set(AlarmManager.RTC_WAKEUP, time, pi);
		}
	}

	public static void CancelAlarm(Context context) {
		Intent intent = new Intent(context, NotificationSpawn.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}
}