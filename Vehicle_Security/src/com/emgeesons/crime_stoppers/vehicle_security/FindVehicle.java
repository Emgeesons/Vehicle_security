package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class FindVehicle extends BaseActivity implements LocationListener {
	private GoogleMap googleMap;
	String vid, lat, lon, address, comments;
	MarkerOptions markerOptions;
	LatLng position, curpos;
	TextView comm;
	static double curLATITUDE;
	static double curLONGITUDE;
	GPSTracker gps;
	LocationManager locationManager;
	RelativeLayout findrel;
	Data info;
	SharedPreferences atPrefs;
	String ratepts;
	EditText feedbacks;
	Connection_Detector cd = new Connection_Detector(this);
	Boolean IsInternetPresent;
	String feed_url = Data.url + "parkingFeedback.php";
	private AsyncTask<Void, Void, Void> feebback;
	ProgressDialog pDialog;
	DatabaseHandler db;
	SQLiteDatabase dbb;
	List<ParkingData> vehicles;
	static String latitude = "latitude";
	static String longitude = "longitude";
	MarkerOptions markerOptionss;
	int i = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.find_vehicle);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'>Find My Car</font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.app_icon);
		findrel = (RelativeLayout) findViewById(R.id.findrel);
		info = new Data();

		db = new DatabaseHandler(FindVehicle.this);
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();

		vehicles = db.getparkingData();
		atPrefs = PreferenceManager
				.getDefaultSharedPreferences(FindVehicle.this);

		Intent intent = getIntent();
		vid = intent.getStringExtra("id");
		lat = intent.getStringExtra("lat");
		lon = intent.getStringExtra("lon");
		comments = intent.getStringExtra("comm");
		// address = getaddress();

		googleMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();
		comm = (TextView) findViewById(R.id.comm);
		if (comments.isEmpty()) {
			comm.setVisibility(View.GONE);
		} else {
			comm.setText(comments);
		}
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				30000, 100, this);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 30000, 100, this);
		position = new LatLng(Double.valueOf(lat), Double.valueOf(lon));

		gpscheck();

		findrel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (!atPrefs.getBoolean(info.checkllogin, true)) {
					final Dialog dialog = new Dialog(FindVehicle.this);
					dialog.setContentView(R.layout.feedbackdialog);
					dialog.setTitle("Safely Tracked your way back!");
					TextView post, skip;
					final RatingBar ratebar;

					ratepts = "0.0";

					skip = (TextView) dialog.findViewById(R.id.skip);
					post = (TextView) dialog.findViewById(R.id.post);
					ratebar = (RatingBar) dialog.findViewById(R.id.ratebar);
					feedbacks = (EditText) dialog.findViewById(R.id.feedback);
					ratebar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

						@Override
						public void onRatingChanged(RatingBar ratingBar,
								float rating, boolean fromUser) {
							ratepts = String.valueOf(rating);
						}
					});
					skip.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							SQLiteDatabase dbbb = db.getReadableDatabase();
							dbbb.execSQL("UPDATE Vehicle_park SET lat = '" + ""
									+ "',lon = '" + "" + "',mark = '" + ""
									+ "'WHERE vid='" + vid + "'");
							atPrefs.edit()
									.putString(info.glatitude,
											String.valueOf("not")).commit();
							atPrefs.edit()
									.putString(info.glongitude,
											String.valueOf("not")).commit();
							Intent next = new Intent(getApplicationContext(),
									MainActivity.class);
							startActivity(next);
							finish();

						}
					});
					post.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							IsInternetPresent = cd.isConnectingToInternet();
							if (IsInternetPresent == false) {
								cd.showNoInternetPopup();
							} else {
								if (ratepts.isEmpty()
										|| ratepts.equalsIgnoreCase("0.0")) {
									AlertDialog.Builder alertDialog = new AlertDialog.Builder(
											FindVehicle.this);

									alertDialog
											.setMessage("Please provide rating for the parking spot");

									alertDialog
											.setPositiveButton(
													"Ok",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {
															dialog.cancel();
														}
													});

									alertDialog.show();

								} else {
									dialog.dismiss();
									feebback = new feed().execute();

								}

							}
						}
					});
					dialog.setCancelable(false);
					dialog.show();

				} else {
					atPrefs.edit()
							.putString(info.glatitude, String.valueOf("not"))
							.commit();
					atPrefs.edit()
							.putString(info.glongitude, String.valueOf("not"))
							.commit();
					Intent next = new Intent(getApplicationContext(),
							MainActivity.class);
					startActivity(next);
					finish();
				}

			}
		});

	}

	private void addmarker() {
		// runOnUiThread(new Runnable() {
		// public void run() {
		// // select pos
		markerOptions = new MarkerOptions();
		// markerOptions.position(new LatLng(18.970585,72.836266));
		markerOptions.position(curpos);
		markerOptions.title(getaddress());
		googleMap.addMarker(markerOptions);

		// googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
		// 20));
		// googleMap
		// .animateCamera(CameraUpdateFactory.newLatLng(position));
		// googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
		// position, 15));
		// Log.d("marker", "asd");
		// }
		// });
		if (i == 0) {
			googleMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {

				@Override
				public void onMapLoaded() {
					// TODO Auto-generated method stub
					googleMap.getUiSettings().setZoomControlsEnabled(false);
					LatLngBounds.Builder builder = new LatLngBounds.Builder();

					builder.include(markerOptions.getPosition());
					builder.include(markerOptionss.getPosition());
					LatLngBounds bounds = builder.build();
					int padding = 50; // offset from edges of the map in pixels

					// remove one zoom level to ensure no marker is on the edge.
					CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(
							adjustBoundsForMaxZoomLevel(bounds), padding);

					googleMap.moveCamera(cu);

					i++;
				}
			});
		}

	}

	// for zooming the map
	private LatLngBounds adjustBoundsForMaxZoomLevel(LatLngBounds bounds) {
		LatLng sw = bounds.southwest;
		LatLng ne = bounds.northeast;
		double deltaLat = Math.abs(sw.latitude - ne.latitude);
		double deltaLon = Math.abs(sw.longitude - ne.longitude);

		final double zoomN = 0.005; // minimum zoom coefficient
		if (deltaLat < zoomN) {
			sw = new LatLng(sw.latitude - (zoomN - deltaLat / 2), sw.longitude);
			ne = new LatLng(ne.latitude + (zoomN - deltaLat / 2), ne.longitude);
			bounds = new LatLngBounds(sw, ne);
		} else if (deltaLon < zoomN) {
			sw = new LatLng(sw.latitude, sw.longitude - (zoomN - deltaLon / 2));
			ne = new LatLng(ne.latitude, ne.longitude + (zoomN - deltaLon / 2));
			bounds = new LatLngBounds(sw, ne);
		}

		return bounds;
	}

	private class feed extends AsyncTask<Void, Void, Void> {
		String success, mess, response, points;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(FindVehicle.this);
			pDialog.setMessage("Feedback");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(feed_url);
			System.out.println(feed_url);
			JSONArray jsonMainArr;
			JSONObject json = new JSONObject();
			try {

				info.device();
				info.showInfo(getApplicationContext());
				json.put("userId", info.user_id);
				json.put("pin", info.pin);
				if (vehicles.size() == 0) {
					json.put("vehicleId", "0");
				} else {
					json.put("vehicleId", vid);
				}

				json.put("latitude", curLATITUDE);
				json.put("longitude", curLONGITUDE);
				json.put("feedback", feedbacks.getText().toString());
				json.put("rating", ratepts);
				json.put("make", info.manufacturer);
				json.put("os", "Android" + " " + info.Version);
				json.put("model", info.model);
				System.out.println("Elements-->" + json);
				postMethod.setHeader("Content-Type", "application/json");
				postMethod.setEntity(new ByteArrayEntity(json.toString()
						.getBytes("UTF8")));
				String response = httpClient
						.execute(postMethod, resonseHandler);
				Log.e("response :", response);
				JSONObject profile = new JSONObject(response);
				jsonMainArr = profile.getJSONArray("response");
				success = profile.getString("status");
				mess = profile.getString("message");
				points = jsonMainArr.getJSONObject(0).getString(
						"samaritan_points");
			} catch (JSONException e) {
				System.out.println("JSONException");
			} catch (ClientProtocolException e) {
				System.out.println("ClientProtocolException");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("IOException");
				e.printStackTrace();
			}

			if (success.equals("success")) {

				runOnUiThread(new Runnable() {

					public void run() {

						final Dialog dialog = new Dialog(FindVehicle.this);
						dialog.setContentView(R.layout.feedbackpoint);
						dialog.setTitle("Sweet!");
						TextView point;
						RelativeLayout close, profile;

						point = (TextView) dialog.findViewById(R.id.points);
						profile = (RelativeLayout) dialog
								.findViewById(R.id.profile);
						close = (RelativeLayout) dialog
								.findViewById(R.id.close);
						close.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								Intent next = new Intent(
										getApplicationContext(),
										MainActivity.class);
								startActivity(next);
								finish();

							}
						});
						profile.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								Intent next = new Intent(
										getApplicationContext(),
										ProfileScreen.class);
								startActivity(next);
								finish();

							}
						});
						point.setText("Total Samaritan Points :" + " " + points);
						dialog.setCancelable(false);
						dialog.show();
						SQLiteDatabase dbbb = db.getReadableDatabase();
						if (vehicles.size() == 0) {
							atPrefs.edit()
									.putString(info.glatitude,
											String.valueOf("not")).commit();
							atPrefs.edit()
									.putString(info.glongitude,
											String.valueOf("not")).commit();
						} else {

							dbbb.execSQL("UPDATE Vehicle_park SET lat = '" + ""
									+ "',lon = '" + "" + "',mark = '" + ""
									+ "'WHERE vid='" + vid + "'");
						}

						dbbb.execSQL("UPDATE profile SET spoints = '" + points
								+ "'");

					}
				});
			}

			// response failure
			else if (success.equals("failure")) {

				runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								FindVehicle.this).create();
						Dialog.setTitle("Error");
						Dialog.setIcon(R.drawable.ic_action_error);
						Dialog.setMessage(mess);
						Dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										Dialog.dismiss();
									}
								});
						Dialog.setCancelable(true);
						Dialog.show();
					}
				});

			} else if (success.equals("error")) {

				runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								FindVehicle.this).create();
						Dialog.setTitle("Error");
						Dialog.setIcon(R.drawable.ic_action_error);
						Dialog.setMessage(mess);
						Dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										pDialog.dismiss();
									}
								});
						Dialog.setCancelable(true);
						Dialog.show();
					}
				});
			}

			return null;

		}

		@Override
		protected void onPostExecute(Void notUsed) {
			pDialog.dismiss();

		}
	}

	public void gpscheck() {
		gps = new GPSTracker(FindVehicle.this);

		if (gps.canGetLocation()) {
			Log.i("gpscheck on", "calld and on");
			curLATITUDE = gps.getLatitude();
			curLONGITUDE = gps.getLongitude();

			curpos = new LatLng(curLATITUDE, curLONGITUDE);
			atPrefs.edit().putString(latitude, String.valueOf(curLATITUDE))
					.commit();
			atPrefs.edit().putString(longitude, String.valueOf(curLONGITUDE))
					.commit();
			onchange();
		} else {
			googleMap.animateCamera(CameraUpdateFactory.newLatLng(position));
			googleMap.moveCamera(CameraUpdateFactory
					.newLatLngZoom(position, 15));
			Toast.makeText(
					getApplicationContext(),
					"Please allow My Wheels to access Your location . Turn it ON from Location Services",
					Toast.LENGTH_LONG).show();
			addmarker();
		}

	}

	private void onchange() {
		// runOnUiThread(new Runnable() {
		// public void run() {
		// // add your marker here
		BitmapDescriptor icon = BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker_green);
		markerOptionss = new MarkerOptions();
		markerOptionss.position(curpos);
		markerOptionss.icon(icon);
		markerOptionss.title(curgetaddress());
		googleMap.clear();
		googleMap.addMarker(markerOptionss);

		// CameraPosition cameraPosition = new CameraPosition.Builder()
		// .target(midPoint(curLATITUDE, curLONGITUDE,
		// Double.valueOf(lat), Double.valueOf(lon))).zoom(5)
		// .build();
		// googleMap.animateCamera(CameraUpdateFactory
		// .newCameraPosition(cameraPosition));

		// googleMap.animateCamera(CameraUpdateFactory.newLatLng(curpos));
		// googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curpos, 15));
		// }
		// });
		addmarker();

	}

	// private double angleBteweenCoordinate(double lat1, double long1,
	// double lat2, double long2) {
	//
	// double dLon = (long2 - long1);
	//
	// double y = Math.sin(dLon) * Math.cos(lat2);
	// double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
	// * Math.cos(lat2) * Math.cos(dLon);
	//
	// double brng = Math.atan2(y, x);
	//
	// brng = Math.toDegrees(brng);
	// brng = (brng + 360) % 360;
	// brng = 360 - brng;
	//
	// return brng;
	// }

	private LatLng midPoint(double lat1, double long1, double lat2, double long2) {

		return new LatLng((lat1 + lat2) / 2, (long1 + long2) / 2);

	}

	String getaddress() {
		Geocoder geocoder = new Geocoder(FindVehicle.this, Locale.ENGLISH);

		try {
			List<Address> addresses = geocoder.getFromLocation(
					Double.valueOf(lat), Double.valueOf(lon), 1);

			if (addresses != null && addresses.size() > 0) {
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder("");
				for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
					strReturnedAddress
							.append(returnedAddress.getAddressLine(i)).append(
									"\n");
				}

				address = strReturnedAddress.toString();

			}

		} catch (IOException e) {
			e.printStackTrace();
			address = "";
			runOnUiThread(new Runnable() {

				public void run() {

				}
			});

		}
		return address;
	}

	String curgetaddress() {
		Geocoder geocoder = new Geocoder(FindVehicle.this, Locale.ENGLISH);

		try {
			List<Address> addresses = geocoder.getFromLocation(
					Double.valueOf(curLATITUDE), Double.valueOf(curLONGITUDE),
					1);

			if (addresses != null && addresses.size() > 0) {
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder("");
				for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
					strReturnedAddress
							.append(returnedAddress.getAddressLine(i)).append(
									"\n");
				}

				address = strReturnedAddress.toString();

			}

		} catch (IOException e) {
			e.printStackTrace();
			address = "";
			runOnUiThread(new Runnable() {

				public void run() {
				}
			});

		}
		return address;
	}

	// @Override
	// protected void onStop() {
	// // TODO Auto-generated method stub
	// atPrefs.edit().putString(check, "True").commit();
	// Log.i("find", "true");
	// super.onStop();
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.find_vehicle, menu);
		menu.add("Directions").setTitle("Directions")
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Intent intent = new Intent(

						android.content.Intent.ACTION_VIEW, Uri
								.parse("http://maps.google.com/maps?saddr="
										+ "&daddr=" + lat + "," + lon));
						atPrefs.edit().putString(callcheck, "True").commit();
						startActivity(intent);

						return false;
					}
				}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent back = new Intent(getApplicationContext(),
					MainActivity.class);
			startActivity(back);
			finish();
			break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		Intent back = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(back);
		finish();
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.i("change", location.getLatitude() + "" + location.getLongitude());
		curLATITUDE = location.getLatitude();
		curLONGITUDE = location.getLongitude();
		curpos = new LatLng(curLATITUDE, curLONGITUDE);
		atPrefs.edit().putString(latitude, String.valueOf(curLATITUDE))
				.commit();
		atPrefs.edit().putString(longitude, String.valueOf(curLONGITUDE))
				.commit();
		onchange();
	}

	@Override
	public void onProviderDisabled(String arg0) {

	}

	@Override
	public void onProviderEnabled(String arg0) {

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.find_vehicle;
	}

}
