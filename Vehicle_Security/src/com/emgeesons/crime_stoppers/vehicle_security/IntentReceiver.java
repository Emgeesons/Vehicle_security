package com.emgeesons.crime_stoppers.vehicle_security;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class IntentReceiver extends BroadcastReceiver {
	static SharedPreferences atPrefs;
	private static final String logTag = "PushSample";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(logTag, "Received intent: " + intent.toString());
		String action = intent.getAction();

		if (action.equals(PushManager.ACTION_PUSH_RECEIVED)) {

			int id = intent.getIntExtra(PushManager.EXTRA_NOTIFICATION_ID, 0);
			// String mess = intent.getStringExtra(PushManager.EXTRA_ALERT);
			Log.i(logTag,
					"Received push notification. Alert: "
							+ intent.getStringExtra(PushManager.EXTRA_ALERT)
							+ " [NotificationID=" + id + "]");
			atPrefs = PreferenceManager.getDefaultSharedPreferences(context);
			// for updates @homescreen
			int no = atPrefs.getInt("updates", 0);
			atPrefs.edit().putInt("updates", no + 1).commit();
			// refresh homescreen ui for update

			HomescreenActivity.checkupdate(context);
			logPushExtras(intent);

		} else if (action.equals(PushManager.ACTION_NOTIFICATION_OPENED)) {

			Log.i(logTag,
					"User clicked notification. Message: "
							+ intent.getStringExtra(PushManager.EXTRA_ALERT));

			logPushExtras(intent);

			Intent launch = new Intent(Intent.ACTION_MAIN);
			// onclick
			launch.setClass(UAirship.shared().getApplicationContext(),
					MainActivity.class);
			launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			UAirship.shared().getApplicationContext().startActivity(launch);

		} else if (action.equals(PushManager.ACTION_REGISTRATION_FINISHED)) {
			Log.i(logTag,
					"Registration complete. APID:"
							+ intent.getStringExtra(PushManager.EXTRA_APID)
							+ ". Valid: "
							+ intent.getBooleanExtra(
									PushManager.EXTRA_REGISTRATION_VALID, false));
		}

	}

	/**
	 * Log the values sent in the payload's "extra" dictionary.
	 * 
	 * @param intent
	 *            A PushManager.ACTION_NOTIFICATION_OPENED or
	 *            ACTION_PUSH_RECEIVED intent.
	 */
	private void logPushExtras(Intent intent) {
		Set<String> keys = intent.getExtras().keySet();
		for (String key : keys) {

			// ignore standard C2DM extra keys
			List<String> ignoredKeys = (List<String>) Arrays.asList(
					"collapse_key",// c2dm collapse key
					"from",// c2dm sender
					PushManager.EXTRA_NOTIFICATION_ID,// int id of generated
														// notification
														// (ACTION_PUSH_RECEIVED
														// only)
					PushManager.EXTRA_PUSH_ID,// internal UA push id
					PushManager.EXTRA_ALERT);// ignore alert
			if (ignoredKeys.contains(key)) {
				continue;
			}
			Log.i(logTag,
					"Push Notification Extra: [" + key + " : "
							+ intent.getStringExtra(key) + "]");
		}
	}
}