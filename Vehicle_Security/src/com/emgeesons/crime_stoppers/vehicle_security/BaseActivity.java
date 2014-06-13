package com.emgeesons.crime_stoppers.vehicle_security;

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
	boolean check = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResourceId());
		info = new Data();
		atPrefs = PreferenceManager
				.getDefaultSharedPreferences(BaseActivity.this);

		atPrefs.edit().putBoolean(info.lockcheck, true).commit();

	}

	

	protected abstract int getLayoutResourceId();


	
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