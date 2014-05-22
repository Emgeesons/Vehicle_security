package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class HomescreenActivity extends Activity
// implements LocationListener
{
	double LATITUDE;
	double LONGITUDE;
	GPSTracker gps;
	static LatLng currlocation;
	private GoogleMap map;
	RelativeLayout mapview, marker;
	int height;
	ListView list;
	ArrayAdapter<String> titleadapter;
	private LocationManager locationManager;
	String[] title, subtitle;
	int[] images = { R.drawable.ic_report_sighting,
			R.drawable.ic_file_new_report, R.drawable.ic_about_us };
	SharedPreferences atPrefs;
	LatLng position;
	TextView as;
	String pos;
	boolean IsAlertDialogShown;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homescreen_activity);

		atPrefs = PreferenceManager
				.getDefaultSharedPreferences(HomescreenActivity.this);
		marker = (RelativeLayout) findViewById(R.id.marker);
		mapview = (RelativeLayout) findViewById(R.id.mapview);
		as = (TextView) findViewById(R.id.textView1);
		height = getWindowManager().getDefaultDisplay().getHeight();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		this.registerReceiver(this.mlocation, new IntentFilter(
				android.location.LocationManager.PROVIDERS_CHANGED_ACTION));
		// locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		// 3000, // 3 sec
		// 10, this);

		// 30% of phone
		int image_h = height * 30 / 100;
		RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, image_h);
		mapview.setLayoutParams(parms);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		IsAlertDialogShown = true;
		// check gps is on\off and set location
		gpscheck();
		list = (ListView) findViewById(R.id.listView1);
		title = getResources().getStringArray(R.array.title);
		subtitle = getResources().getStringArray(R.array.sub_title);
		titleadapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, title);
		View footer = getLayoutInflater().inflate(R.layout.homescreen_footer,
				null);
		list.addFooterView(footer);
		list.setAdapter(new listAdapter());

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

	private void onchange() {

		as.setText(pos);
	}

	// get address
	private class update extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			pos = "searching";
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (HomescreenActivity.this == null) {
				return null;
			}
			Geocoder geocoder = new Geocoder(HomescreenActivity.this,
					Locale.ENGLISH);

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

	// gps on or off
	public void gpscheck() {
		gps = new GPSTracker(HomescreenActivity.this);
		if (gps.canGetLocation()) {
			LATITUDE = gps.getLatitude();
			LONGITUDE = gps.getLongitude();

			position = new LatLng(LATITUDE, LONGITUDE);
			Toast.makeText(getApplicationContext(), String.valueOf(position),
					Toast.LENGTH_LONG).show();
			map.getUiSettings().setZoomControlsEnabled(false);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
			new update().execute();

			// pos = String.valueOf(position.latitude)
			// + String.valueOf(position.longitude);

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
			if (!IsAlertDialogShown) {
				return;
			}
			gps.showSettingsAlert();
			IsAlertDialogShown = false;
		}
	}

	private class listAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return titleadapter.getCount();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			if (convertView == null) {
				v = getLayoutInflater()
						.inflate(R.layout.homescreen_items, null);
			} else {
				v = convertView;
			}
			ImageView image = (ImageView) v.findViewById(R.id.imageView1);
			for (int i = 0; i < images.length; i++) {
				if (position == i) {
					image.setImageResource(images[i]);
				}

			}
			TextView title = (TextView) v.findViewById(R.id.title);
			TextView sub_title = (TextView) v.findViewById(R.id.sub_title);
			title.setText(titleadapter.getItem(position));
			sub_title.setText(subtitle[position]);

			return v;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.homescreen, menu);
		return true;
	}

	 @Override
//	 public void onLocationChanged(Location location) {
//	
//	 // Toast.makeText(getApplicationContext(),
//	 // "change" + LATITUDE + LONGITUDE, Toast.LENGTH_LONG).show();
//	
//	 }
//	
//	 @Override
//	 public void onProviderDisabled(String provider) {
//	 // Toast.makeText(getApplicationContext(), "off" + LATITUDE + LONGITUDE,
//	 // Toast.LENGTH_LONG).show();
//	 }
//	
//	 @Override
//	 public void onProviderEnabled(String provider) {
//	 gpscheck();
//	 // Toast.makeText(getApplicationContext(), "on" + LATITUDE + LONGITUDE,
//	 // Toast.LENGTH_LONG).show();
//	 }
//	
//	 @Override
//	 public void onStatusChanged(String provider, int status, Bundle extras) {
//	 // gpscheck();
//	 }

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			SplashscreenActivity.fblogin = true;
			atPrefs.edit().putBoolean(SplashscreenActivity.checkllogin, true)
					.commit();
			Session session = Session.getActiveSession();
			if (session != null) {

				if (!session.isClosed()) {
					session.closeAndClearTokenInformation();
					// clear your preferences if saved
				}
			} else {

				session = new Session(this);
				Session.setActiveSession(session);

				session.closeAndClearTokenInformation();
				// clear your preferences if saved

			}
			Intent nextscreen = new Intent(HomescreenActivity.this,
					LoginActivity.class);
			startActivity(nextscreen);
			finish();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

}
