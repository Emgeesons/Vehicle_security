package com.emgeesons.crime_stoppers.vehicle_security;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.actionbarsherlock.view.MenuItem;

public class ReportSubmit extends BaseActivity {

	SharedPreferences atPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.report_submit);
		atPrefs = PreferenceManager
				.getDefaultSharedPreferences(ReportSubmit.this);
		atPrefs.edit().remove(FilenewReport.fphoto).commit();
		atPrefs.edit().remove(FilenewReport.tphoto).commit();

	}

	@Override
	public void onBackPressed() {

		Intent next = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(next);
		finish();
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			// app icon @ action bar clicked; go home
			Intent next = new Intent(getApplicationContext(),
					MainActivity.class);

			startActivity(next);
			finish();

			break;
		}
		return true;
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.report_submit;
	}

}
