package com.emgeesons.crime_stoppers.vehicle_security;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationAutoStart extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {		
		NotificationAlarm.SetAlarm(context);
	}
}