package com.emgeesons.crime_stoppers.vehicle_security;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;

public class ReportSubmit extends BaseActivity {

	SharedPreferences atPrefs;
	SharedPreferences sharedpreferences;
	Button report;
	TextView call;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.report_submit);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'>Report Submitted</font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.app_icon);
		atPrefs = PreferenceManager
				.getDefaultSharedPreferences(ReportSubmit.this);
		sharedpreferences = getSharedPreferences(Data.MyPREFERENCES,
				Context.MODE_PRIVATE);
		atPrefs.edit().remove(FilenewReport.fphoto).commit();
		atPrefs.edit().remove(FilenewReport.tphoto).commit();
		report = (Button) findViewById(R.id.rreport);
		call = (TextView) findViewById(R.id.textView6);
		call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent call = new Intent(Intent.ACTION_CALL);
				call.setData(Uri.parse("tel:" + "1800333000"));
				startActivity(call);
			}
		});
		report.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent next = new Intent(getApplicationContext(),
						Reportsummary.class);
				startActivity(next);
				// finish();
			}
		});

	}

	@Override
	public void onBackPressed() {

		Intent next = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(next);
		Editor editor = sharedpreferences.edit();

		editor.putString(Data.vid, "notcall");
		editor.putString(Data.p1, "");
		editor.putString(Data.p2, "");
		editor.putString(Data.p3, "");
		editor.putString(Data.comm, "");

		editor.commit();
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
			Editor editor = sharedpreferences.edit();

			editor.putString(Data.vid, "notcall");
			editor.putString(Data.p1, "");
			editor.putString(Data.p2, "");
			editor.putString(Data.p3, "");
			editor.putString(Data.comm, "");

			editor.commit();
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
