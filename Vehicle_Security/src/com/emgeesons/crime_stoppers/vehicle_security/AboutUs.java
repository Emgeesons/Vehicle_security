package com.emgeesons.crime_stoppers.vehicle_security;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class AboutUs extends BaseActivity {
	View view;
	TextView call, website;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.about_us);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'>About Us</font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.app_icon);
		call = (TextView) findViewById(R.id.call);
		call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent call = new Intent(Intent.ACTION_CALL);
				call.setData(Uri.parse("tel:" + "1800333000"));
				startActivity(call);
			}
		});
		website = (TextView) findViewById(R.id.website);
		website.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent next = new Intent(getApplicationContext(), Website.class);
				startActivity(next);
			}
		});

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent next = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(next);
		finish();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon @ action bar clicked; go home
			Intent back = new Intent(AboutUs.this, MainActivity.class);
			startActivity(back);
			finish();
			break;
		}
		return true;
	}

	@Override
	protected int getLayoutResourceId() {
		// TODO Auto-generated method stub
		return R.layout.about_us;
	}

}
