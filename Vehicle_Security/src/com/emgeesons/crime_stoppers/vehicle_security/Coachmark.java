package com.emgeesons.crime_stoppers.vehicle_security;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Coachmark extends Activity {
	Button button1;
	RelativeLayout rel;
	SharedPreferences atPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coachmark);
		atPrefs = PreferenceManager.getDefaultSharedPreferences(Coachmark.this);
		button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent next = new Intent(getApplicationContext(),
						ProfileScreen.class);
				startActivity(next);
				setResult(RESULT_OK);
				finish();
			}
		});
		rel = (RelativeLayout) findViewById(R.id.rel);
		rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		atPrefs.edit().putBoolean(Data.coach, false).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.coachmark, menu);
		return true;
	}

}
