package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomescreenActivity extends SherlockFragment implements
		LocationListener {
	double LATITUDE;
	double LONGITUDE;
	GPSTracker gps;
	static LatLng currlocation;
	private GoogleMap map;
	RelativeLayout mapview, marker;
	int height;
	private LocationManager locationManager;
	SharedPreferences atPrefs;
	LatLng position;
	TextView marker_label;
	String pos;
	boolean IsAlertDialogShown;
	TextView park, find;
	View view;
	Fragment fragment;
	RelativeLayout report, file, about;
	MarkerOptions markerOptions;
	private AsyncTask<Void, Void, Void> profile;
	ProgressDialog pDialog;
	String profile_url = Data.url + "getProfile.php";
	DatabaseHandler db;
	SQLiteDatabase dbb;
	Data info;
	downlaod d;
	File sdRoot;
	String dir;
	JSONArray jsonVehicleArr;
	List<ParkingData> vehicles;
	ArrayList<String> name = new ArrayList<String>();
	ArrayList<String> no = new ArrayList<String>();
	ArrayList<String> type = new ArrayList<String>();
	Connection_Detector cd;
	Boolean IsInternetPresent;
	private AsyncTask<Void, Void, Void> details;
	String details_url = Data.url + "parkingHere.php";
	String vid, typename;
	String lat, lon, comm;
	CircularImageView profilepic;
	String names;
	private String imagepath = null;
	AlertDialog alert;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.homescreen_activity, container, false);
		cd = new Connection_Detector(getActivity());
		atPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		db = new DatabaseHandler(getActivity());
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();
		vehicles = db.getparkingData();
		alert = new AlertDialog.Builder(getActivity()).create();
		info = new Data();
		marker = (RelativeLayout) view.findViewById(R.id.marker);
		mapview = (RelativeLayout) view.findViewById(R.id.mapview);
		// marker_label = (TextView) view.findViewById(R.id.textView1);
		park = (TextView) view.findViewById(R.id.park);
		find = (TextView) view.findViewById(R.id.find);
		report = (RelativeLayout) view.findViewById(R.id.report);
		file = (RelativeLayout) view.findViewById(R.id.file);
		about = (RelativeLayout) view.findViewById(R.id.about);
		height = getActivity().getWindowManager().getDefaultDisplay()
				.getHeight();
		d = new downlaod();
		setHasOptionsMenu(true);
		if (!(vehicles.size() == 0)) {
			for (int i = 0; i < vehicles.size(); i++) {
				name.add(vehicles.get(i).getvehicle_model());
				no.add(vehicles.get(i).getvehicle_id());
				type.add(vehicles.get(i).gettype());
			}
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.nav_item, name);
		// hide if no vehicle added
		if (name.size() == 0) {
			getSherlockActivity()
					.getSupportActionBar()
					.setNavigationMode(
							com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_STANDARD);
			typename = "other";
			check();
		} else {
			getSherlockActivity().getSupportActionBar().setNavigationMode(
					com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_LIST);
		}

		ActionBar.OnNavigationListener navigationListener = new OnNavigationListener() {

			@Override
			public boolean onNavigationItemSelected(int itemPosition,
					long itemId) {

				typename = type.get(itemPosition);
				vid = no.get(itemPosition);
				// Toast.makeText(getActivity(),
				// typename + "" + vid + "" + name.get(itemPosition),
				// Toast.LENGTH_SHORT).show();
				checkinfo();
				return false;
			}

		};

		getSherlockActivity().getSupportActionBar().setListNavigationCallbacks(
				adapter, navigationListener);
		adapter.setDropDownViewResource(R.layout.nav_item);

		// add images at right side
		getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(
				true);
		getSherlockActivity().getSupportActionBar().setDisplayUseLogoEnabled(
				true);
		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(
				true);
		getSherlockActivity().getSupportActionBar()
				.setDisplayShowCustomEnabled(true);
		getSherlockActivity().getSupportActionBar().setDisplayShowHomeEnabled(
				true);

		View customNav = LayoutInflater.from(getActivity()).inflate(
				R.layout.test, null);
		profilepic = (CircularImageView) customNav
				.findViewById(R.id.actionBarLogo);
		names = atPrefs.getString(SplashscreenActivity.profile_pic,
				"profilePic.png");
		sdRoot = Environment.getExternalStorageDirectory();
		dir = "My Wheel/";
		File f = new File(sdRoot, dir + names);
		if (f.exists()) {
			imagepath = f.getAbsolutePath();
			Bitmap photo = (Bitmap) decodeFile(imagepath);
			profilepic.setImageBitmap(photo);
		} else {
			profilepic.setImageResource(R.drawable.default_profile);
		}
		profilepic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				if (atPrefs.getBoolean(info.checkllogin, true)) {
					Intent next = new Intent(getActivity(), LoginActivity.class);
					startActivity(next);
					getActivity().finish();
				} else {

					// profile = new getprofile().execute();
					Intent nextscreen = new Intent(getActivity(),
							ProfileScreen.class);
					startActivity(nextscreen);

					getActivity().finish();

				}

			}
		});

		getSherlockActivity().getSupportActionBar().setCustomView(customNav);

		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		getActivity()
				.registerReceiver(
						this.mlocation,
						new IntentFilter(
								android.location.LocationManager.PROVIDERS_CHANGED_ACTION));

		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				30000, 100, this);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 30000, 100, this);

		// 30% of phone
		int image_h = height * 30 / 100;
		RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, image_h);
		mapview.setLayoutParams(parms);

		FragmentManager fmanager = getActivity().getSupportFragmentManager();
		fragment = fmanager.findFragmentById(R.id.map);
		SupportMapFragment supportmapfragment = (SupportMapFragment) fragment;
		map = supportmapfragment.getMap();

		// map = ((MapFragment) getActivity().getFragmentManager()
		// .findFragmentById(R.id.map)).getMap();

		IsAlertDialogShown = true;
		// check gps is on\off and set location
		gpscheck();

		report.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent next = new Intent(getActivity(), ReportSighting.class);
				startActivity(next);
				getActivity().finish();
			}
		});
		file.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				 Intent next = new Intent(getActivity(), FilenewReport.class);
				 startActivity(next);
				 getActivity().finish();
			}
		});
		about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent next = new Intent(getActivity(), AboutUs.class);
				startActivity(next);
				getActivity().finish();
			}
		});

		park.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IsInternetPresent = cd.isConnectingToInternet();
				if (IsInternetPresent == false) {
					cd.showNoInternetPopup();
				} else {
					if (gps.canGetLocation() || !(LATITUDE == 0.0)
							|| !(LONGITUDE == 0.0)) {

						details = new detail().execute();

					} else {
						Toast.makeText(
								getActivity(),
								"Please allow My Wheels to access Your location . Turn it ON from Location Services",
								Toast.LENGTH_LONG).show();
					}
				}

			}
		});
		return view;

	}

	public Bitmap decodeFile(String path) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, o);
			// The new size we want to scale to
			final int REQUIRED_SIZE = 80;

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeFile(path, o2);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;

	}

	private class detail extends AsyncTask<Void, Void, Void> {
		String success, mess, response, id, rate, notip;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Parking Details");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(details_url);
			System.out.println(details_url);
			JSONArray jsonMainArr;
			JSONObject json = new JSONObject();
			try {
				info.device();
				info.showInfo(getActivity());
				if (!atPrefs.getBoolean(info.checkllogin, true)) {
					json.put("userId", info.user_id);
					json.put("vehicleId", vid);
					json.put("pin", info.pin);
				} else {

					json.put("userId", 0);
					json.put("vehicleId", 0);
					//
					json.put("pin", "0" + "0" + "0" + "0");
				}

				json.put("latitude", LATITUDE);
				json.put("longitude", LONGITUDE);
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
				rate = jsonMainArr.getJSONObject(0).getString("rating");
				notip = jsonMainArr.getJSONObject(0).getString("noTips");

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

				getActivity().runOnUiThread(new Runnable() {

					public void run() {
						if (name.size() == 0) {

							atPrefs.edit()
									.putString(info.glatitude,
											String.valueOf(LATITUDE)).commit();
							atPrefs.edit()
									.putString(info.glongitude,
											String.valueOf(LONGITUDE)).commit();
							// atPrefs.edit()
							// .putString(gcomm,
							// String.valueOf(LONGITUDE)).commit();

						} else {

							SQLiteDatabase dbbb = db.getReadableDatabase();
							dbbb.execSQL("UPDATE Vehicle_park SET lat = '"
									+ LATITUDE + "',lon = '" + LONGITUDE
									+ "',mark = '" + "true" + "',type = '"
									+ typename + "'WHERE vid='" + vid + "'");
						}
						Intent next = new Intent(getActivity(), CarPark.class);
						next.putExtra("Rate", rate);
						next.putExtra("tips", notip);
						next.putExtra("type", typename);
						next.putExtra("id", vid);
						next.putExtra("Address", getaddress());
						startActivity(next);
						getActivity().finish();

					}
				});
			}
			// response failure
			else if (success.equals("failure")) {

				getActivity().runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								getActivity()).create();
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

			} else if (success.equals("error")) {

				getActivity().runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								getActivity()).create();
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

	private BroadcastReceiver mlocation = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (this != null) {
				gpscheck();
			}
		}

	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mlocation);
		locationManager.removeUpdates(this);

	}

	// @Override
	// public void onDestroyView() {
	// super.onDestroyView();
	// Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
	// FragmentTransaction ft = getActivity().getSupportFragmentManager()
	// .beginTransaction();
	// ft.remove(fragment);
	// ft.commitAllowingStateLoss();
	// }
	@Override
	public void onDestroyView() {
		SherlockFragmentActivity a = getSherlockActivity();
		if (a != null && map != null) {
			try {
				Log.i("LOGTAG", "Removing map fragment");
				a.getSupportFragmentManager().beginTransaction()
						.remove(fragment).commit();
				fragment = null;
			} catch (IllegalStateException e) {
				Log.i("LOGTAG", "IllegalStateException on exit");
			}
		}
		super.onDestroyView();
	}

	// private void onchange() {
	// // marker_label.setText(pos);
	// }
	void onchange() {

		markerOptions = new MarkerOptions();
		// BitmapDescriptor icon = BitmapDescriptorFactory
		// .fromResource(R.drawable.ic_location_marker);
		markerOptions.position(position);
		markerOptions.title(getaddress());
		// markerOptions.icon(icon);
		map.clear();
		map.addMarker(markerOptions);
		map.getUiSettings().setZoomControlsEnabled(false);
		map.animateCamera(CameraUpdateFactory.newLatLng(position));
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

	}

	// get address
	// private class update extends AsyncTask<Void, Void, Void> {
	//
	// @Override
	// protected void onPreExecute() {
	// pos = "searching";
	// }
	//
	// @Override
	// protected Void doInBackground(Void... params) {
	// if (HomescreenActivity.this == null) {
	// return null;
	// }
	// Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
	//
	// try {
	// List<Address> addresses = geocoder.getFromLocation(LATITUDE,
	// LONGITUDE, 1);
	//
	// if (addresses != null && addresses.size() > 0) {
	// Address returnedAddress = addresses.get(0);
	// StringBuilder strReturnedAddress = new StringBuilder("");
	// for (int i = 0; i < returnedAddress
	// .getMaxAddressLineIndex(); i++) {
	// strReturnedAddress.append(
	// returnedAddress.getAddressLine(i)).append("\n");
	// }
	// pos = strReturnedAddress.toString();
	// getActivity().runOnUiThread(new Runnable() {
	//
	// public void run() {
	//
	// onchange();
	//
	// }
	// });
	//
	// } else {
	// pos = String.valueOf(LATITUDE + LONGITUDE);
	// getActivity().runOnUiThread(new Runnable() {
	//
	// public void run() {
	//
	// onchange();
	//
	// }
	// });
	//
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// pos = "";
	// getActivity().runOnUiThread(new Runnable() {
	//
	// public void run() {
	// // checking response and display resp screen
	// onchange();
	//
	// }
	// });
	//
	// }
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Void notUsed) {
	//
	// }
	// }
	String getaddress() {
		Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);

		try {
			List<Address> addresses = geocoder.getFromLocation(LATITUDE,
					LONGITUDE, 1);

			if (addresses != null && addresses.size() > 0) {
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder("");
				for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
					strReturnedAddress
							.append(returnedAddress.getAddressLine(i)).append(
									"\n");
				}

				pos = strReturnedAddress.toString();

			}

		} catch (IOException e) {
			e.printStackTrace();
			pos = "";
			getActivity().runOnUiThread(new Runnable() {

				public void run() {
					// checking response and display resp screen

					// onchange();

				}
			});

		}
		return pos;
	}

	// gps on or off
	public void gpscheck() {
		gps = new GPSTracker(getActivity());
		if (gps.canGetLocation()) {
			LATITUDE = gps.getLatitude();
			LONGITUDE = gps.getLongitude();

			position = new LatLng(LATITUDE, LONGITUDE);
			Toast.makeText(getActivity(), String.valueOf(position),
					Toast.LENGTH_LONG).show();
			map.getUiSettings().setZoomControlsEnabled(false);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
			onchange();

		} else {

			// give popup for 1st time
			if (!alert.isShowing()) {
				showAlert();
			}

			// IsAlertDialogShown = false;
		}
	}

	public void gpschecks(double lat, double lon) {
		gps = new GPSTracker(getActivity());
		if (gps.canGetLocation()) {
			LATITUDE = gps.getLatitude();
			LONGITUDE = gps.getLongitude();

			position = new LatLng(LATITUDE, LONGITUDE);
			Toast.makeText(getActivity(), String.valueOf(position),
					Toast.LENGTH_LONG).show();
			map.getUiSettings().setZoomControlsEnabled(false);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
			onchange();

		} else {

			if (!alert.isShowing()) {
				showAlert();
			}

		}
	}

	@Override
	public void onLocationChanged(Location location) {
		LATITUDE = location.getLatitude();
		LONGITUDE = location.getLongitude();
		gpscheck();
		// gpschecks(LATITUDE, LONGITUDE);
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	//
	public void onPause() {
		super.onPause();
		// locationManager.removeUpdates(this);
	};

	public void onStop() {
		super.onStop();
		// locationManager.removeUpdates(this);
	};

	private class getprofile extends AsyncTask<Void, Void, Void> {
		String success, mess, response;
		String user_id, fName, lName, email, mobileNumber, dob, gender,
				licenseNo, street, suburb, postcode, dtModified, fbId, fbToken,
				cname, cnumber, sques, sans, photourl, photoname, pin, points,
				exp;
		int profilecom;

		// vehicle
		int vid;
		String vtype, vmake, vmodel, reg, vstatus;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Updating Info");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(profile_url);
			JSONArray jsonMainArr;

			JSONObject json = new JSONObject();
			try {
				info.device();
				info.showInfo(getActivity());
				json.put("userId", info.user_id);
				json.put("make", info.manufacturer);
				json.put("os", "Android" + " " + info.Version);
				json.put("model", info.model);
				System.out.println("Element1-->" + json);
				postMethod.setHeader("Content-Type", "application/json");
				postMethod.setEntity(new ByteArrayEntity(json.toString()
						.getBytes("UTF8")));
				String response = httpClient
						.execute(postMethod, resonseHandler);
				Log.e("response :", response);

				JSONObject profile = new JSONObject(response);
				jsonMainArr = profile.getJSONArray("response");
				jsonVehicleArr = profile.getJSONArray("vehicles");
				success = profile.getString("status");

				mess = profile.getString("message");
				user_id = jsonMainArr.getJSONObject(0).getString("user_id");
				fName = jsonMainArr.getJSONObject(0).getString("first_name");
				lName = jsonMainArr.getJSONObject(0).getString("last_name");
				email = jsonMainArr.getJSONObject(0).getString("email");
				mobileNumber = jsonMainArr.getJSONObject(0).getString(
						"mobile_number");
				String[] datespilt = jsonMainArr.getJSONObject(0)
						.getString("dob").split("\\s+");
				dob = String.valueOf(datespilt[0]);
				gender = jsonMainArr.getJSONObject(0).getString("gender");
				licenseNo = jsonMainArr.getJSONObject(0)
						.getString("license_no");
				// street = jsonMainArr.getJSONObject(0).getString("street");
				// suburb = jsonMainArr.getJSONObject(0).getString("suburb");
				postcode = jsonMainArr.getJSONObject(0).getString("postcode");
				dtModified = jsonMainArr.getJSONObject(0).getString(
						"modified_at");
				fbId = jsonMainArr.getJSONObject(0).getString("fb_id");
				fbToken = jsonMainArr.getJSONObject(0).getString("fb_token");
				// cname = jsonMainArr.getJSONObject(0).getString(
				// "emergency_contact");
				// cnumber = jsonMainArr.getJSONObject(0).getString(
				// "emergency_contact_number");
				sques = jsonMainArr.getJSONObject(0).getString(
						"security_question");
				sans = jsonMainArr.getJSONObject(0)
						.getString("security_answer");
				profilecom = jsonMainArr.getJSONObject(0).getInt(
						"profile_completed");
				pin = jsonMainArr.getJSONObject(0).getString("pin");
				points = jsonMainArr.getJSONObject(0).getString(
						"samaritan_points");
				photourl = jsonMainArr.getJSONObject(0).getString("photo_url");
				int pos = photourl.lastIndexOf("/");
				photoname = photourl.substring(pos + 1);

				// vehicle info

			} catch (ClientProtocolException e) {
				System.out.println("ClientProtocolException");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("IOException");
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (success.equals("success")) {

				getActivity().runOnUiThread(new Runnable() {

					public void run() {
						db = new DatabaseHandler(getActivity());
						PersonalData data = new PersonalData(user_id, fName,
								lName, email, mobileNumber, dob, gender,
								licenseNo, street, suburb, postcode,
								dtModified, fbId, fbToken, cname, cnumber, pin,
								sques, sans, points);

						db.updateprofileData(data);
						atPrefs.edit()
								.putInt(SplashscreenActivity.progress,
										profilecom).commit();
						atPrefs.edit().putBoolean(info.checkllogin, false)
								.commit();

						SQLiteDatabase dbb = db.getReadableDatabase();
						dbb.execSQL("delete from Vehicle_info");
						for (int i = 0; i < jsonVehicleArr.length(); i++) {

							try {
								vid = jsonVehicleArr.getJSONObject(i).getInt(
										"vehicle_id");
								vtype = jsonVehicleArr.getJSONObject(i)
										.getString("vehicle_type");
								vmake = jsonVehicleArr.getJSONObject(i)
										.getString("vehicle_make");
								vmodel = jsonVehicleArr.getJSONObject(i)
										.getString("vehicle_model");
								reg = jsonVehicleArr.getJSONObject(i)
										.getString("registration_serial_no");
								vstatus = jsonVehicleArr.getJSONObject(i)
										.getString("vehicle_status");
								exp = jsonVehicleArr.getJSONObject(i)
										.getString("insurance_expiry_date");
								String[] date = exp.split("\\s+");
								db = new DatabaseHandler(getActivity());
								VehicleData datas = new VehicleData(vid, vtype,
										vmake, vmodel, "", "", "", "", "", reg,
										"", "", date[0], vstatus, "");
								db.insertvehicleData(datas);
								if (date[0].equalsIgnoreCase("0000-00-00")) {
								} else {
									String toParse = date[0] + " " + 8 + ":"
											+ 00;
									SimpleDateFormat formatter = new SimpleDateFormat(
											"yyyy-MM-dd h:m");
									Date dates = formatter.parse(toParse);
									long millis = dates.getTime();
									long time = millis - 604800000;

									dbb.execSQL("UPDATE vehicle_info SET vehicle_expmil = '"
											+ time
											+ "'WHERE vehicle_id ='"
											+ vid + "'");
									NotificationAlarm
											.CancelAlarm(getActivity());
									NotificationAlarm.SetAlarm(getActivity());
								}

							} catch (JSONException e) {
								e.printStackTrace();
							} catch (java.text.ParseException e) {
								e.printStackTrace();
							}

						}
						Thread thread = new Thread(new Runnable() {
							@Override
							public void run() {
								sdRoot = Environment
										.getExternalStorageDirectory();
								dir = "My Wheel/";
								String dowlaod = "/My Wheel";
								File photo = new File(sdRoot, dir + photoname);
								if (photo.exists()) {
									atPrefs.edit()
											.putString(
													SplashscreenActivity.profile_pic,
													photoname).commit();
								} else {
									try {
										d.DownloadFromUrl(photourl, photoname,
												dowlaod, dir);
										atPrefs.edit()
												.putString(
														SplashscreenActivity.profile_pic,
														photoname).commit();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}

							}
						});
						thread.start();
						Intent nextscreen = new Intent(getActivity(),
								ProfileScreen.class);
						startActivity(nextscreen);

						getActivity().finish();

					}
				});
			}
			// response failure
			else if (success.equals("failure")) {

				getActivity().runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								getActivity()).create();
						Dialog.setTitle("Incorrect Email");
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

			} else if (success.equals("error")) {
				getActivity().runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								getActivity()).create();
						Dialog.setTitle("Error");
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

	public void showAlert() {
		HomescreenActivity n = new HomescreenActivity();
		if (!(n == null)) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("No location access")
					.setMessage(
							"Please allow User App to access your location.Turn it On From Location Services")
					.setCancelable(false)
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							})
					.setPositiveButton("Settings",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Intent intent = new Intent(
											Settings.ACTION_LOCATION_SOURCE_SETTINGS);
									startActivity(intent);
								}
							});
			alert = builder.create();
			alert.show();
		}
	}

	private void checkinfo() {

		RelativeLayout parkrel;
		parkrel = (RelativeLayout) view.findViewById(R.id.parkrel);
		ImageView tick = (ImageView) view.findViewById(R.id.tick);
		final SQLiteDatabase dbbb = this.db.getReadableDatabase();
		String selectQuery = "SELECT * FROM Vehicle_park WHERE vid = '" + vid
				+ "'";
		Cursor cursor = dbbb.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				lat = cursor.getString(3);
				lon = cursor.getString(4);
				comm = cursor.getString(5);
			} while (cursor.moveToNext());
		}

		if (!lat.isEmpty()) {

			tick.setVisibility(View.VISIBLE);
			parkrel.setBackgroundColor(getResources().getColor(
					R.color.blue_text));
			park.setTextColor(getResources().getColor(R.color.white));
			find.setEnabled(true);
			find.setTextColor(getResources().getColor(R.color.blue_text));

			find.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent next = new Intent(getActivity(), FindVehicle.class);
					next.putExtra("lat", lat);
					next.putExtra("lon", lon);
					next.putExtra("id", vid);
					next.putExtra("comm", comm);

					startActivity(next);
					getActivity().finish();
				}
			});
			park.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							getActivity());

					alertDialog.setTitle("Cancel parking");
					alertDialog
							.setMessage("Please confirm your vehicle is not parked here");

					alertDialog.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dbbb.execSQL("UPDATE Vehicle_park SET lat = '"
											+ ""
											+ "',lon = '"
											+ ""
											+ "',mark = '"
											+ ""
											+ "'WHERE vid='" + vid + "'");

									checkinfo();
								}
							});

					alertDialog.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});

					alertDialog.show();

				}
			});

		} else {
			tick.setVisibility(View.GONE);
			parkrel.setBackgroundColor(getResources().getColor(
					R.color.white_trs1));
			park.setTextColor(getResources().getColor(R.color.blue_text));
			find.setTextColor(getResources().getColor(R.color.blue_trs));
			find.setEnabled(false);
			find.setAlpha(50);
			park.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					IsInternetPresent = cd.isConnectingToInternet();
					if (IsInternetPresent == false) {
						cd.showNoInternetPopup();
					} else {
						if (gps.canGetLocation() || !(LATITUDE == 0.0)
								|| !(LONGITUDE == 0.0)) {

							details = new detail().execute();

						} else {
							Toast.makeText(
									getActivity(),
									"Please allow My Wheels to access Your location . Turn it ON from Location Services",
									Toast.LENGTH_LONG).show();
						}
					}

				}
			});

		}

	}

	private void check() {

		RelativeLayout parkrel;
		parkrel = (RelativeLayout) view.findViewById(R.id.parkrel);
		ImageView tick = (ImageView) view.findViewById(R.id.tick);
		final SQLiteDatabase dbbb = this.db.getReadableDatabase();

		lat = atPrefs.getString(info.glatitude, "not");
		lon = atPrefs.getString(info.glongitude, "not");
		comm = atPrefs.getString(info.gcomm, "not");

		if (!lat.equalsIgnoreCase("not")) {

			tick.setVisibility(View.VISIBLE);
			parkrel.setBackgroundColor(getResources().getColor(
					R.color.blue_text));
			park.setTextColor(getResources().getColor(R.color.white));
			find.setEnabled(true);
			find.setTextColor(getResources().getColor(R.color.blue_text));

			find.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent next = new Intent(getActivity(), FindVehicle.class);
					next.putExtra("lat", lat);
					next.putExtra("lon", lon);
					next.putExtra("id", vid);
					next.putExtra("comm", comm);

					startActivity(next);
					getActivity().finish();
				}
			});
			park.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							getActivity());

					alertDialog.setTitle("Cancel parking");
					alertDialog
							.setMessage("Please confirm your vehicle is not parked here");

					alertDialog.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dbbb.execSQL("UPDATE Vehicle_park SET lat = '"
											+ ""
											+ "',lon = '"
											+ ""
											+ "',mark = '"
											+ ""
											+ "'WHERE vid='" + vid + "'");

									check();
								}
							});

					alertDialog.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});

					alertDialog.show();

				}
			});

		} else {
			tick.setVisibility(View.GONE);
			parkrel.setBackgroundColor(getResources().getColor(
					R.color.white_trs1));
			park.setTextColor(getResources().getColor(R.color.blue_text));
			find.setTextColor(getResources().getColor(R.color.blue_trs));
			find.setEnabled(false);
			find.setAlpha(50);
			park.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					IsInternetPresent = cd.isConnectingToInternet();
					if (IsInternetPresent == false) {
						cd.showNoInternetPopup();
					} else {
						if (gps.canGetLocation() || !(LATITUDE == 0.0)
								|| !(LONGITUDE == 0.0)) {

							details = new detail().execute();

						} else {
							Toast.makeText(
									getActivity(),
									"Please allow My Wheels to access Your location . Turn it ON from Location Services",
									Toast.LENGTH_LONG).show();
						}
					}

				}
			});

		}

	}

}
