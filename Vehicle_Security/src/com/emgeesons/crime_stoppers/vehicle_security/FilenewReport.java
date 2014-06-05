package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.TimePicker.OnTimeChangedListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.internal.el;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class FilenewReport extends SherlockActivity {
	double LATITUDE;
	double LONGITUDE;
	GPSTracker gps;
	static LatLng currlocation;
	private GoogleMap map;
	int height;
	String pos;
	RelativeLayout mapview, marker;
	TextView marker_label;
	LocationManager locationManager;
	LatLng position;
	EditText comments;
	TextView date, type;
	ImageView expand;
	boolean exp_col;
	int map_height;
	final static CharSequence[] typeReport = { "Theft", "Vandalism", "Other" };
	int tReport;
	static int buffKey = 0;
	// fordate and time
	String datevalue, timevalue;
	int years, months, dates, Hrs, min;
	String ctimevalue, cdatevalue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filenewreport_activity);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'>File New Report </font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.ic_app);
		marker = (RelativeLayout) findViewById(R.id.marker);
		mapview = (RelativeLayout) findViewById(R.id.mapview);
		marker_label = (TextView) findViewById(R.id.textView1);
		comments = (EditText) findViewById(R.id.comments);
		date = (TextView) findViewById(R.id.date);
		type = (TextView) findViewById(R.id.type);
		expand = (ImageView) findViewById(R.id.expand);
		Calendar c = Calendar.getInstance();
		years = c.get(Calendar.YEAR);
		months = c.get(Calendar.MONTH) + 1;
		dates = c.get(Calendar.DAY_OF_MONTH);

		Hrs = c.get(Calendar.HOUR_OF_DAY);
		min = c.get(Calendar.MINUTE);
		String ampm = "AM";
		if (Hrs >= 12) {
			Hrs -= 12;
			ampm = "PM";
		}
		timevalue = Hrs + ":" + min + ampm;
		datevalue = years + "-" + months + "-" + dates;
		ctimevalue = timevalue;
		cdatevalue = datevalue;
		System.out.println(cdatevalue + "," + ctimevalue);
		date.setText(datevalue + "," + timevalue);
		height = getWindowManager().getDefaultDisplay().getHeight();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		this.registerReceiver(this.mlocation, new IntentFilter(
				android.location.LocationManager.PROVIDERS_CHANGED_ACTION));
		map_height = height * 30 / 100;
		RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, map_height);
		mapview.setLayoutParams(parms);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		// check gps is on\off and set location
		gpscheck();
		expand.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (exp_col) {
					RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.MATCH_PARENT);
					expand.setImageResource(R.drawable.ic_close_map);
					mapview.setLayoutParams(parms);
					exp_col = false;
				} else {
					exp_col = true;
					expand.setImageResource(R.drawable.ic_expand_map);
					RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT, map_height);

					mapview.setLayoutParams(parms);
				}
			}
		});
		type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(
						FilenewReport.this);
				builder.setTitle(" Type Of Sighting ");

				builder.setSingleChoiceItems(typeReport, tReport,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// set to buffKey instead of selected
								// (when cancel not save to selected)
								// int sel = atPrefs.getInt(blood_value, 0);
								buffKey = which;
							}
						})
						.setCancelable(false)
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										type.setText(typeReport[buffKey]);
										int selectedPosition = ((AlertDialog) dialog)
												.getListView()
												.getCheckedItemPosition();
										// set buff to selected
										tReport = buffKey;
										// atPrefs.edit()
										// .putInt(Activity_EButton_data.blood_value,
										// selectedPosition)
										// .commit();
										// set blood grp value in number

									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								});

				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final StringBuilder sb = new StringBuilder();

				final Dialog dialog = new Dialog(FilenewReport.this);

				dialog.setContentView(R.layout.date_time_layout);

				dialog.setTitle("Select Date & Time");
				Button ok = (Button) dialog.findViewById(R.id.ok);
				TimePicker timePicker = (TimePicker) dialog
						.findViewById(R.id.timePicker1);
				DatePicker datePicker = (DatePicker) dialog
						.findViewById(R.id.datePicker1);

				datePicker.init(years, months, dates, datePickerListener);

				timePicker
						.setOnTimeChangedListener(new OnTimeChangedListener() {

							@Override
							public void onTimeChanged(TimePicker view,
									int hourOfDay, int minute) {

								Hrs = hourOfDay;
								min = minute;
								String ampm = "AM";
								if (Hrs >= 12) {
									Hrs -= 12;
									ampm = "PM";
								}
								timevalue = Hrs + ":" + min + ampm;

							}
						});
				ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						date.setText(datevalue + "," + timevalue);
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});

	}

	public void gpscheck() {
		gps = new GPSTracker(FilenewReport.this);
		if (gps.canGetLocation()) {
			LATITUDE = gps.getLatitude();
			LONGITUDE = gps.getLongitude();

			position = new LatLng(LATITUDE, LONGITUDE);
			Toast.makeText(getApplicationContext(), String.valueOf(position),
					Toast.LENGTH_LONG).show();
			map.getUiSettings().setZoomControlsEnabled(false);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
			new update().execute();

			// when map is move
			map.setOnCameraChangeListener(new OnCameraChangeListener() {
				public void onCameraChange(CameraPosition arg0) {

					map.clear();
					LATITUDE = arg0.target.latitude;
					LONGITUDE = arg0.target.longitude;
					new update().execute();
					onchange();

					// pos = String.valueOf(arg0.target.latitude) + " "
					// + String.valueOf(arg0.target.longitude);

				}
			});

			// map.setOnMapClickListener(new OnMapClickListener() {
			//
			// @Override
			// public void onMapClick(LatLng latLng) {
			//
			// // Creating a marker
			// MarkerOptions markerOptions = new MarkerOptions();
			//
			// // Setting the position for the marker
			// markerOptions.position(latLng);
			//
			// // Setting the title for the marker.
			// // This will be displayed on taping the marker
			// markerOptions.title(latLng.latitude + " : "
			// + latLng.longitude);
			//
			// // Clears the previously touched position
			// map.clear();
			//
			// // Animating to the touched position
			// map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
			//
			// // Placing a marker on the touched position
			// map.addMarker(markerOptions);
			// }
			// });
			// map.addMarker(new MarkerOptions().position(currlocation).title(
			// "Current location"));
			// map.moveCamera(CameraUpdateFactory.newLatLngZoom(currlocation,
			// 12));
			// // Zoom in, animating the camera.
			// map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

		} else {

			// Ask user to enable GPS/network in settings

			// give popup for 1st time
			// if (!IsAlertDialogShown) {
			// return;
			// }
			gps.showSettingsAlert();
			// IsAlertDialogShown = false;
		}
	}

	private void onchange() {
		marker_label.setText(pos);
	}

	private DatePicker.OnDateChangedListener datePickerListener = new DatePicker.OnDateChangedListener() {

		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			dates = dayOfMonth;
			years = year;
			months = monthOfYear + 1;
			datevalue = years + "-" + months + "-" + dates;
		}
	};

	private class update extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			pos = "searching";
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (FilenewReport.this == null) {
				return null;
			}
			Geocoder geocoder = new Geocoder(FilenewReport.this, Locale.ENGLISH);

			try {
				List<Address> addresses = geocoder.getFromLocation(LATITUDE,
						LONGITUDE, 1);

				if (addresses != null && addresses.size() > 0) {
					Address returnedAddress = addresses.get(0);
					StringBuilder strReturnedAddress = new StringBuilder("");
					for (int i = 0; i < returnedAddress
							.getMaxAddressLineIndex(); i++) {
						strReturnedAddress.append(
								returnedAddress.getAddressLine(i)).append("\n");
					}
					pos = strReturnedAddress.toString();
					runOnUiThread(new Runnable() {

						public void run() {

							onchange();

						}
					});

				} else {
					pos = String.valueOf(LATITUDE + LONGITUDE);
					runOnUiThread(new Runnable() {

						public void run() {

							onchange();

						}
					});

				}
			} catch (IOException e) {
				e.printStackTrace();
				pos = "";
				runOnUiThread(new Runnable() {

					public void run() {
						// checking response and display resp screen
						onchange();

					}
				});

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void notUsed) {

		}
	}

	private BroadcastReceiver mlocation = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (this != null) {
				gpscheck();
			}
		}

	};

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mlocation);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon @ action bar clicked; go home
			super.onBackPressed();
			break;
		}
		return true;
	}
}
