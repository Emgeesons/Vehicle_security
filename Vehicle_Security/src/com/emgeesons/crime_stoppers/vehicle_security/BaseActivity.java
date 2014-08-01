package com.emgeesons.crime_stoppers.vehicle_security;

import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;

public abstract class BaseActivity extends SherlockActivity {
	// static String lock = "lock";
	SharedPreferences atPrefs;
	// static String checkllogin = "checkllogin";
	Data info;
	static String check = "check";
	static String callcheck = "callcheck";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResourceId());
		info = new Data();
		atPrefs = PreferenceManager
				.getDefaultSharedPreferences(BaseActivity.this);

		// String ch = atPrefs.getString(info.lockcheck, "true");
		// Log.i("con", ch);
		// if (ch.equalsIgnoreCase("false")) {
		// Log.i("check", String.valueOf(ApplicationState.isActivityVisible()));
		// Intent ne = new Intent(getApplicationContext(), PinLock.class);
		// startActivity(ne);
		// finish();
		// }

	}

	protected abstract int getLayoutResourceId();

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		try {

			boolean foregroud = new ForegroundCheckTask().execute(
					BaseActivity.this).get();
			atPrefs.edit().putString(check, String.valueOf(foregroud)).commit();
			Log.i("onStop", String.valueOf(foregroud));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onStop();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// pin check 
		// time @30min
		String time = atPrefs.getString("time", "");
		String times = String.valueOf(System.currentTimeMillis());
		double t = Double.valueOf(time);
		double tw = Double.valueOf(times);
		String ch = atPrefs.getString(check, "true");
		String chh = atPrefs.getString(callcheck, "true");
		Log.i("con", ch);
		Log.i("cons", chh);
		Log.i("call form", getClass().getName());
		if (ch.equalsIgnoreCase("false")) {

			if (chh.equalsIgnoreCase("true")) {
				// when we open maps,pic dont show pin
				atPrefs.edit().putString(callcheck, "false").commit();
				return;
			} else {

				if (!atPrefs.getBoolean(info.checkllogin, true) && tw > t) {
					Intent ne = new Intent(getApplicationContext(),
							PinLock.class);
					startActivity(ne);
				}

			}

		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {
			boolean foregroud = new ForegroundCheckTask().execute(
					BaseActivity.this).get();
			Log.i("onPause", String.valueOf(foregroud));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// @Override
	// protected void onStart() {
	// // TODO Auto-generated method stub
	// // atPrefs.edit().putBoolean(info.lock, true).commit();
	// atPrefs.edit().putBoolean(info.lockcheck, true).commit();
	// Log.i("Base", "onStart");
	// super.onStart();
	// }
	// @Override
	// protected void onResume() {
	// Log.i("Base", "onResume");
	// if (!atPrefs.getBoolean(info.lock, true)
	// && !atPrefs.getBoolean(info.checkllogin, true)
	// && atPrefs.getBoolean(info.lockcheck, true)
	// ) {
	//
	// Intent n = new Intent(getApplicationContext(), PinLock.class);
	// startActivity(n);
	// Log.i("Base", "on");
	//
	// }
	// super.onResume();
	//
	// };
	//
	// @Override
	// protected void onPause() {
	//
	// if (!atPrefs.getBoolean(info.checkllogin, true)) {
	// Log.i("Base", "onPause");
	// atPrefs.edit().putBoolean(info.lock, false).commit();
	//
	// atPrefs.edit().putBoolean(info.lockcheck, false).commit();
	// super.onPause();
	// }
	// };
	//
	// @Override
	// protected void onStop() {
	// // TODO Auto-generated method stub
	// if (!atPrefs.getBoolean(info.checkllogin, true)) {
	// atPrefs.edit().putBoolean(info.lock, false).commit();
	// Log.i("Base", "onStop");
	// // atPrefs.edit().putBoolean(info.lockcheck, false).commit();
	//
	// super.onStop();
	// }
	// }
	//
	// @Override
	// protected void onDestroy() {
	// if (!atPrefs.getBoolean(info.checkllogin, true)) {
	// atPrefs.edit().putBoolean(info.lock, false).commit();
	//
	// super.onPause();
	// }
	//
	// };
}