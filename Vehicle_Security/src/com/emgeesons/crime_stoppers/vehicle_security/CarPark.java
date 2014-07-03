package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class CarPark extends BaseActivity {
	String Rate, notip, Address, type, vid;
	TextView address, rates, tip, done;
	RatingBar ratebar;
	EditText comm;
	ListView data;
	String[] checkdata;
	ArrayAdapter<String> checkadapter;
	DatabaseHandler db;
	SQLiteDatabase dbb;
	List<ParkingData> vehicles;
	SharedPreferences atPrefs;
	Data info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_car_park);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'>Parking Details</font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.app_icon);
		info = new Data();
		atPrefs = PreferenceManager.getDefaultSharedPreferences(CarPark.this);
		Intent intent = getIntent();
		Rate = intent.getStringExtra("Rate");
		notip = intent.getStringExtra("tips");
		type = intent.getStringExtra("type");
		Address = intent.getStringExtra("Address");
		vid = intent.getStringExtra("id");
		db = new DatabaseHandler(CarPark.this);
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();
		vehicles = db.getparkingData();
		address = (TextView) findViewById(R.id.address);
		rates = (TextView) findViewById(R.id.rate);
		ratebar = (RatingBar) findViewById(R.id.ratebar);
		comm = (EditText) findViewById(R.id.editText1);
		done = (TextView) findViewById(R.id.done);
		tip = (TextView) findViewById(R.id.tip);
		data = (ListView) findViewById(R.id.listView1);
		address.setText(Address);
		rates.setText(Rate);
		tip.setText(notip + "   " + "tips for this location");
		ratebar.setRating(Float.valueOf(Rate));

		if (type.equalsIgnoreCase("Bicycle")) {
			checkdata = getResources().getStringArray(R.array.bicycle_tip);
		} else if (type.equalsIgnoreCase("car")) {
			checkdata = getResources().getStringArray(R.array.car_tip);
		} else if (type.equalsIgnoreCase("MotorCycle")) {
			checkdata = getResources().getStringArray(R.array.motor_tip);
		} else {
			checkdata = getResources().getStringArray(R.array.car_tip);
		}

		checkadapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, checkdata);

		data.setAdapter(new listAdapter());
		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				db = new DatabaseHandler(CarPark.this);
				try {

					db.createDataBase();
				} catch (IOException e) {
					e.printStackTrace();
				}
				dbb = db.openDataBase();
				dbb = db.getReadableDatabase();
				SQLiteDatabase dbbb = db.getReadableDatabase();
				if (vehicles.size() == 0) {
					atPrefs.edit()
							.putString(info.gcomm, comm.getText().toString())
							.commit();
				} else {
					dbbb.execSQL("UPDATE Vehicle_park SET comm = '"
							+ comm.getText().toString() + "'WHERE vid='" + vid
							+ "'");
				}

				Intent next = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(next);
				finish();

			}
		});

	}

	private class listAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			if (convertView == null) {
				v = getLayoutInflater().inflate(R.layout.checkboxlist, null);
			} else {
				v = convertView;
			}
			Random random = new Random();

			int maxIndex = checkdata.length;
			int generatedIndex = random.nextInt(maxIndex);
			TextView name = (TextView) v.findViewById(R.id.textView1);
			TextView check = (TextView) v.findViewById(R.id.checkBox1);
			name.setText(checkdata[generatedIndex]);

			return v;

		}
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
		// TODO Auto-generated method stub
		return R.layout.activity_car_park;
	}
}
