package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
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
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomescreenActivity extends SherlockFragment implements
		LocationListener {
	double LATITUDE, LONGITUDE;
	GPSTracker gps;
	static LatLng currlocation;
	private GoogleMap map;
	RelativeLayout mapview, marker;
	int height;
	private LocationManager locationManager;
	static SharedPreferences atPrefs;
	SharedPreferences sharedpreferences;
	LatLng position;
	TextView marker_label;
	String pos;
	boolean IsAlertDialogShown;
	TextView park, find;
	static View view;
	Fragment fragment;
	RelativeLayout report, file, about, update;
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
	String vid = "0", typename;
	String lat, lon, comm;
	CircularImageView profilepic;
	String names;
	private String imagepath = null;
	AlertDialog alert;
	String reponse;
	String as;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.homescreen_activity, container, false);
		cd = new Connection_Detector(getActivity());
		atPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		sharedpreferences = getActivity().getSharedPreferences(
				Data.MyPREFERENCES, Context.MODE_PRIVATE);
		db = new DatabaseHandler(getActivity());
		String time = atPrefs.getString("time", "");
		double curtime = System.currentTimeMillis();
		// for everytime pin ,app 1st time open
		if (time.isEmpty()) {
			atPrefs.edit()
					.putString("time",
							String.valueOf(System.currentTimeMillis()))
					.commit();
		}
		// else {
		//
		// double prevtime = Double.valueOf(time);
		// if (curtime > (prevtime + 30 * 60 * 1000)) {
		// atPrefs.edit()
		// .putString("time",
		// String.valueOf(System.currentTimeMillis()))
		// .commit();
		// }
		// }

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
		// marker_labeol = (TextView) view.findViewById(R.id.textView1);
		park = (TextView) view.findViewById(R.id.park);
		find = (TextView) view.findViewById(R.id.find);
		report = (RelativeLayout) view.findViewById(R.id.report);
		update = (RelativeLayout) view.findViewById(R.id.update);
		file = (RelativeLayout) view.findViewById(R.id.file);
		about = (RelativeLayout) view.findViewById(R.id.about);
		height = getActivity().getWindowManager().getDefaultDisplay()
				.getHeight();
		d = new downlaod();
		setHasOptionsMenu(true);
		gps = new GPSTracker(getActivity());
		// checkupdate cno

		checkupdate(getActivity());

		// COACHMARK
		if (!atPrefs.getBoolean(info.checkllogin, true)
				&& atPrefs.getBoolean(Data.coach, true)) {
			Intent coach = new Intent(getActivity(), Coachmark.class);
			startActivityForResult(coach, 0);
		}

		if (!(vehicles.size() == 0)) {

			for (int i = 0; i < vehicles.size(); i++) {
				// check = make
				name.add(vehicles.get(i).getcheck() + " "
						+ vehicles.get(i).getvehicle_model());
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
				atPrefs.edit().putInt("selected", itemPosition).commit();
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
		// Get selected pos for spinner
		if (!atPrefs.getBoolean(info.checkllogin, true) && type.size() > 1) {
			getSherlockActivity().getSupportActionBar()
					.setSelectedNavigationItem(atPrefs.getInt("selected", 0));
		}

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
		// profile pic image
		File f = new File(sdRoot, dir + names);
		if (names.isEmpty()) {
			profilepic.setImageResource(R.drawable.default_profile);
		} else {
			if (f.exists()) {
				imagepath = f.getAbsolutePath();
				Bitmap photo = (Bitmap) decodeFile(imagepath);
				profilepic.setImageBitmap(photo);
			} else {
				profilepic.setImageResource(R.drawable.default_profile);
			}
		}

		profilepic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				if (atPrefs.getBoolean(info.checkllogin, true)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setMessage("Please log in to use this feature")
							.setCancelable(false)
							// .setMessage("Are you sure?")
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									})
							.setPositiveButton("Sign in",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											Intent next = new Intent(
													getActivity(),
													LoginActivity.class);
											startActivity(next);
											getActivity().finish();

										}

									});
					builder.show();

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

		map.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {

				marker.setTitle(getaddress());
				return false;

			}
		});
		report.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				IsInternetPresent = cd.isConnectingToInternet();
				if (IsInternetPresent == false) {
					cd.showNoInternetPopup();
				} else {
					Intent next = new Intent(getActivity(),
							ReportSighting.class);
					startActivity(next);
					// getActivity().finish();
				}
			}
		});
		file.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!atPrefs.getBoolean(info.checkllogin, true)) {
					IsInternetPresent = cd.isConnectingToInternet();
					if (IsInternetPresent == false) {
						cd.showNoInternetPopup();
					} else {
						Intent next = new Intent(getActivity(),
								FilenewReport.class);
						startActivity(next);
					}
					// getActivity().finish();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setMessage("Please log in to use this feature")
							.setCancelable(false)
							// .setMessage("Are you sure?")
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									})
							.setPositiveButton("Sign in",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											Intent next = new Intent(
													getActivity(),
													LoginActivity.class);
											startActivity(next);
											getActivity().finish();

										}

									});
					builder.show();
				}
				// Intent next = new Intent(getActivity(), AboutUs.class);
				// startActivity(next);
				// getActivity().finish();

			}
		});
		about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent next = new Intent(getActivity(), AboutUs.class);
				startActivity(next);
				// getActivity().finish();
			}
		});
		update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				IsInternetPresent = cd.isConnectingToInternet();
				if (IsInternetPresent == false) {
					cd.showNoInternetPopup();
				} else {
					Intent next = new Intent(getActivity(), Updates.class);

					startActivity(next);
					// getActivity().finish();
				}

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
						// location is off
						Toast.makeText(
								getActivity(),
								"Please allow MyWheels to access Your location . Turn it ON from Location Services",
								Toast.LENGTH_LONG).show();
					}
				}

			}
		});
		return view;

	}

	// for no of update ( red round)
	static void checkupdate(Context con) {
		if (con != null) {
			RelativeLayout updates;
			updates = (RelativeLayout) view.findViewById(R.id.updatesc);
			int no = atPrefs.getInt("updates", 0);
			if (no != 0) {

				updates.setVisibility(View.VISIBLE);
				TextView nos = (TextView) view.findViewById(R.id.no);
				nos.setText(String.valueOf(no));
			} else {
				updates.setVisibility(View.GONE);
			}
		}

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
			pDialog.setMessage("Parking Details...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@SuppressWarnings("deprecation")
		@Override
		protected Void doInBackground(Void... params) {

			JSONArray jsonMainArr;
			HttpEntity resEntity;
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(
					CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

			HttpPost httppost = new HttpPost(details_url);
			@SuppressWarnings("deprecation")
			MultipartEntity mpEntity = null;
			mpEntity = new MultipartEntity();
			try {

				info.device();
				info.showInfo(getActivity());
				if (!atPrefs.getBoolean(info.checkllogin, true)) {
					mpEntity.addPart("userId", new StringBody(info.user_id));
					mpEntity.addPart("vehicleId", new StringBody(vid));
					mpEntity.addPart("pin", new StringBody(info.pin));

				} else {
					mpEntity.addPart("userId", new StringBody("0"));
					mpEntity.addPart("vehicleId", new StringBody("0"));
					mpEntity.addPart("pin", new StringBody("0" + "0" + "0"
							+ "0"));
				}

				mpEntity.addPart("latitude",
						new StringBody(String.valueOf(LATITUDE)));
				mpEntity.addPart("longitude",
						new StringBody(String.valueOf(LONGITUDE)));
				mpEntity.addPart("os", new StringBody(info.manufacturer));
				mpEntity.addPart("make", new StringBody("Android" + " "
						+ info.Version));
				mpEntity.addPart("model", new StringBody(info.model));

			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			httppost.setEntity(mpEntity);
			System.out.println(httppost.getRequestLine());
			HttpResponse response = null;
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				resEntity = response.getEntity();
			} catch (Exception e) {
				getActivity().runOnUiThread(new Runnable() {

					public void run() {
						cd.showNoInternetPopup();
					}
				});
				return null;
			}
			System.out.println(response.getStatusLine());
			if (resEntity != null) {
				try {
					reponse = EntityUtils.toString(resEntity);
					System.out.println(reponse);
					JSONObject profile = new JSONObject(reponse);

					jsonMainArr = profile.getJSONArray("response");

					success = profile.getString("status");
					mess = profile.getString("message");
					rate = jsonMainArr.getJSONObject(0).getString("rating");
					notip = jsonMainArr.getJSONObject(0).getString("noTips");
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
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
									+ "',type = '" + typename + "'WHERE vid='"
									+ vid + "'");
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

	// when gps service turn on or off
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
		// remove reg location manger
		getActivity().unregisterReceiver(mlocation);
		locationManager.removeUpdates(this);

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
		// locationManager.removeUpdates(this);
	};

	public void onStop() {
		super.onStop();
		// locationManager.removeUpdates(this);
	};

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
				// Log.i("LOGTAG", "Removing map fragment");
				a.getSupportFragmentManager().beginTransaction()
						.remove(fragment).commit();
				fragment = null;
			} catch (IllegalStateException e) {
				// Log.i("LOGTAG", "IllegalStateException on exit");
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
		// getActivity().runOnUiThread(new Runnable() {
		// public void run() {
		// markerOptions.title(getaddress());
		// }
		// });

		// markerOptions.icon(icon);
		map.clear();
		map.addMarker(markerOptions);
		map.getUiSettings().setZoomControlsEnabled(false);
		map.animateCamera(CameraUpdateFactory.newLatLng(position));
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

	}

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
			// Toast.makeText(getActivity(), String.valueOf(position),
			// Toast.LENGTH_LONG).show();
			map.getUiSettings().setZoomControlsEnabled(false);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					onchange();
				}
			}

			);

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
			// Toast.makeText(getActivity(), String.valueOf(position),
			// Toast.LENGTH_LONG).show();
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

	public void showAlert() {
		HomescreenActivity n = new HomescreenActivity();
		if (n != null) {
			try {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Location Access Required")
						.setMessage(
								"Please allow MyWheels access to your location to correctly identify your location. Turn it on from Location Services")
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
										String callcheck = "callcheck";
										atPrefs.edit()
												.putString(callcheck, "True")
												.commit();
										Intent intent = new Intent(
												Settings.ACTION_LOCATION_SOURCE_SETTINGS);
										startActivity(intent);
									}
								});
				alert = builder.create();
				alert.show();
			} catch (Exception e) {
			}

		}
	}

	// for vehicle park
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
		// vehicle is parked
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
			find.setTextColor(getResources().getColor(R.color.gry_text));
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
									"Please allow MyWheels access to your location to correctly identify your location. Turn it on from Location Services",
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
			find.setTextColor(getResources().getColor(R.color.gry_text));
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
									"Please allow MyWheels to access Your location . Turn it ON from Location Services",
									Toast.LENGTH_LONG).show();
						}
					}

				}
			});

		}

	}

	// check app comes from background or not
	class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Context... params) {
			final Context context = params[0].getApplicationContext();
			return isAppOnForeground(context);
		}

		private boolean isAppOnForeground(Context context) {
			ActivityManager activityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> appProcesses = activityManager
					.getRunningAppProcesses();
			if (appProcesses == null) {
				return false;
			}
			final String packageName = context.getPackageName();
			for (RunningAppProcessInfo appProcess : appProcesses) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
						&& appProcess.processName.equals(packageName)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == getActivity().RESULT_OK) {
			getActivity().setResult(getActivity().RESULT_OK);
			getActivity().finish();
		}
	}
}
