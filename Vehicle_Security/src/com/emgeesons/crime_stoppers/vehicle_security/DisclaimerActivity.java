package com.emgeesons.crime_stoppers.vehicle_security;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DisclaimerActivity extends Activity {
	static SharedPreferences atPrefs;
	Button agree;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.disclaimer_activity);
		agree = (Button) findViewById(R.id.agree);
		agree.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				atPrefs = PreferenceManager
						.getDefaultSharedPreferences(DisclaimerActivity.this);
				atPrefs.edit().putBoolean(SplashscreenActivity.agree, false)
						.commit();
				Intent next = new Intent(DisclaimerActivity.this,
						LoginActivity.class);
				startActivity(next);
				finish();

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.disclaimer_, menu);
		return true;
	}

}
