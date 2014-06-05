package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.File;
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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
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
	LocationListener locationListener;
	private AsyncTask<Void, Void, Void> profile;
	ProgressDialog pDialog;
	String profile_url = Data.url + "getProfile.php";
	DatabaseHandler db;
	SQLiteDatabase dbb;
	Data info;
	downlaod d;
	File sdRoot;
	String dir;

	@SuppressLint("ResourceAsColor")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.homescreen_activity, container, false);

		atPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		db = new DatabaseHandler(getActivity());
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();
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
		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		getActivity()
				.registerReceiver(
						this.mlocation,
						new IntentFilter(
								android.location.LocationManager.PROVIDERS_CHANGED_ACTION));

		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				LATITUDE = location.getLatitude();
				LONGITUDE = location.getLongitude();

				gpschecks(LATITUDE, LONGITUDE);
			}
		};
		// locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		// 30000, 0, new LocationListener() {
		//
		// @Override
		// public void onStatusChanged(String provider, int status,
		// Bundle extras) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onProviderEnabled(String provider) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onProviderDisabled(String provider) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onLocationChanged(Location location) {
		// // TODO Auto-generated method stub
		// LATITUDE = location.getLatitude();
		// LONGITUDE = location.getLongitude();
		//
		// gpschecks(LATITUDE, LONGITUDE);
		// }
		// });
		// locationManager.requestLocationUpdates(
		// LocationManager.NETWORK_PROVIDER, 30000, 0,
		// new LocationListener() {
		//
		// @Override
		// public void onStatusChanged(String provider, int status,
		// Bundle extras) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onProviderEnabled(String provider) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onProviderDisabled(String provider) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onLocationChanged(Location location) {
		// // TODO Auto-generated method stub
		// LATITUDE = location.getLatitude();
		// LONGITUDE = location.getLongitude();
		//
		// gpschecks(LATITUDE, LONGITUDE);
		// }
		// });

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
				// getActivity().finish();
			}
		});
		file.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent next = new Intent(getActivity(), FilenewReport.class);
				startActivity(next);
				// getActivity().finish();
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
		// list = (ListView) view.findViewById(R.id.listView1);
		// title = getResources().getStringArray(R.array.title);
		// subtitle = getResources().getStringArray(R.array.sub_title);
		// titleadapter = new ArrayAdapter<String>(getActivity(),
		// android.R.layout.simple_list_item_1, title);
		// View footer = getActivity().getLayoutInflater().inflate(
		// R.layout.homescreen_footer, null);
		// list.addFooterView(footer);
		// list.setAdapter(new listAdapter());
		// list.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// Intent next;
		// switch (position) {
		//
		// case 0:
		// next = new Intent(getActivity(), ReportSighting.class);
		// startActivity(next);
		// // finish();
		// break;
		// case 1:
		// next = new Intent(getActivity(), FilenewReport.class);
		// startActivity(next);
		// // finish();
		// break;
		// case 2:
		// next = new Intent(getActivity(), AboutUs.class);
		// startActivity(next);
		// // finish();
		// break;
		//
		// default:
		// break;
		// }
		// }
		// });

		park.setOnClickListener(new OnClickListener() {

			@SuppressLint("ResourceAsColor")
			@Override
			public void onClick(View v) {
				park.setBackgroundColor(R.color.blue_text);
				park.setTextColor(R.color.white);
			}
		});
		return view;

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

			// pos = String.valueOf(position.latitude)
			// + String.valueOf(position.longitude);

			// when map is move
			// map.setOnCameraChangeListener(new OnCameraChangeListener() {
			// public void onCameraChange(CameraPosition arg0) {
			//
			// map.clear();
			// LATITUDE = arg0.target.latitude;
			// LONGITUDE = arg0.target.longitude;
			// new update().execute();
			// onchange();
			//
			// // pos = String.valueOf(arg0.target.latitude) + " "
			// // + String.valueOf(arg0.target.longitude);
			//
			// }
			// });

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
			// pos = String.valueOf(position.latitude)
			// + String.valueOf(position.longitude);

			// when map is move
			// map.setOnCameraChangeListener(new OnCameraChangeListener() {
			// public void onCameraChange(CameraPosition arg0) {
			//
			// map.clear();
			// LATITUDE = arg0.target.latitude;
			// LONGITUDE = arg0.target.longitude;
			// new update().execute();
			// onchange();
			//
			// // pos = String.valueOf(arg0.target.latitude) + " "
			// // + String.valueOf(arg0.target.longitude);
			//
			// }
			// });

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

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		LATITUDE = location.getLatitude();
		LONGITUDE = location.getLongitude();

		gpschecks(LATITUDE, LONGITUDE);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		// locationManager = (LocationManager) getActivity().getSystemService(
		// Context.LOCATION_SERVICE);
		// if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		// {
		// locationManager.requestLocationUpdates(
		// LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		// } else {
		// locationManager.requestLocationUpdates(
		// LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		// }
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		// locationManager = (LocationManager) getActivity().getSystemService(
		// Context.LOCATION_SERVICE);
		// if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		// {
		// locationManager.requestLocationUpdates(
		// LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		// } else {
		// locationManager.requestLocationUpdates(
		// LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		// }
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.homescreen, menu);
		menu.add("Notification").setIcon(R.drawable.default_profile)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// login check
						if (atPrefs.getBoolean(
								SplashscreenActivity.checkllogin, true)) {
							Intent next = new Intent(getActivity(),
									LoginActivity.class);
							startActivity(next);
							getActivity().finish();
						} else {

							profile = new getprofile().execute();

						}

						return false;
					}
				}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

	}

	private class getprofile extends AsyncTask<Void, Void, Void> {
		String success, mess, response;
		String user_id, fName, lName, email, mobileNumber, dob, gender,
				licenseNo, street, suburb, postcode, dtModified, fbId, fbToken,
				cname, cnumber, pin, sques, sans, photourl, photoname;
		int profilecom;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Sending Info");
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
				street = jsonMainArr.getJSONObject(0).getString("street");
				// suburb = jsonMainArr.getJSONObject(0).getString("suburb");
				postcode = jsonMainArr.getJSONObject(0).getString("postcode");
				dtModified = jsonMainArr.getJSONObject(0).getString("dob");
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
				photourl = jsonMainArr.getJSONObject(0).getString("photo_url");
				int pos = photourl.lastIndexOf("/");
				photoname = photourl.substring(pos + 1);

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
								sques, sans);

						db.updateprofileData(data);
						atPrefs.edit()
								.putInt(SplashscreenActivity.progress,
										profilecom).commit();
						atPrefs.edit()
								.putBoolean(SplashscreenActivity.checkllogin,
										false).commit();
						Thread thread = new Thread(new Runnable() {
							@Override
							public void run() {
								sdRoot = Environment
										.getExternalStorageDirectory();
								dir = "My Wheel/";
								File photo = new File(sdRoot, dir + photoname);
								if (photo.exists()) {

								} else {
									try {
										d.DownloadFromUrl(photourl, photoname);
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

	// private class listAdapter extends BaseAdapter {
	//
	// @Override
	// public int getCount() {
	// return titleadapter.getCount();
	// }
	//
	// @Override
	// public Object getItem(int position) {
	// return null;
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// return 0;
	// }
	//
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// View v;
	// if (convertView == null) {
	// v = getActivity().getLayoutInflater().inflate(
	// R.layout.homescreen_items, null);
	// } else {
	// v = convertView;
	// }
	// ImageView image = (ImageView) v.findViewById(R.id.imageView1);
	// for (int i = 0; i < images.length; i++) {
	// if (position == i) {
	// image.setImageResource(images[i]);
	// }
	//
	// }
	// TextView title = (TextView) v.findViewById(R.id.title);
	// TextView sub_title = (TextView) v.findViewById(R.id.sub_title);
	// title.setText(titleadapter.getItem(position));
	// sub_title.setText(subtitle[position]);
	//
	// return v;
	// }
	// }

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getActivity().getMenuInflater().inflate(R.menu.homescreen, menu);
	// return true;
	// }

	// @Override
	// public void onLocationChanged(Location location) {
	//
	// // Toast.makeText(getApplicationContext(),
	// // "change" + LATITUDE + LONGITUDE, Toast.LENGTH_LONG).show();
	//
	// }
	//
	// @Override
	// public void onProviderDisabled(String provider) {
	// // Toast.makeText(getApplicationContext(), "off" + LATITUDE + LONGITUDE,
	// // Toast.LENGTH_LONG).show();
	// }
	//
	// @Override
	// public void onProviderEnabled(String provider) {
	// gpscheck();
	// // Toast.makeText(getApplicationContext(), "on" + LATITUDE + LONGITUDE,
	// // Toast.LENGTH_LONG).show();
	// }
	//
	// @Override
	// public void onStatusChanged(String provider, int status, Bundle extras) {
	// // gpscheck();
	// }
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle item selection
	// switch (item.getItemId()) {
	// case R.id.action_settings:
	// SplashscreenActivity.fblogin = true;
	// atPrefs.edit().putBoolean(SplashscreenActivity.checkllogin, true)
	// .commit();
	// Session session = Session.getActiveSession();
	// if (session != null) {
	//
	// if (!session.isClosed()) {
	// session.closeAndClearTokenInformation();
	// // clear your preferences if saved
	// }
	// } else {
	//
	// session = new Session(this);
	// Session.setActiveSession(session);
	//
	// session.closeAndClearTokenInformation();
	// // clear your preferences if saved
	//
	// }
	// Intent nextscreen = new Intent(HomescreenActivity.this,
	// LoginActivity.class);
	// startActivity(nextscreen);
	// finish();
	//
	// return true;
	//
	// default:
	// return super.onOptionsItemSelected(item);
	// }
	//
	// }

}
