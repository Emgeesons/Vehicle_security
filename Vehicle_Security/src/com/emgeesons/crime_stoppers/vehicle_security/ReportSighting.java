package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class ReportSighting extends SherlockActivity {
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
	EditText vmake, vmodel, color, reg, comments;
	Button send;
	TextView date, type;
	ImageView expand, addpic;
	boolean exp_col;
	int map_height;
	final static CharSequence[] typeSighting = { "Theft", "Vandalism",
			"Suspicious Activity", "Other" };
	int tSighting;
	Data info;
	static int buffKey = 0;
	String datevalue, timevalue;
	int years, months, dates, Hrs, min;
	String ctimevalue, cdatevalue;
	// camera
	String dir, imgPath;
	String[] names;
	private String imagepath = null;
	File sdRoot;
	// LinearLayout takenpic;
	// int nopic = 1;
	CircularImageView pic1, pic2, pic3;
	List<String> ch = new ArrayList<String>();
	int nopic;
	Bitmap photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reportsighting_activity);
		getSupportActionBar()
				.setTitle(
						Html.fromHtml("<font color='#FFFFFF'> Report Sighting </font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.app_icon);
		marker = (RelativeLayout) findViewById(R.id.marker);
		mapview = (RelativeLayout) findViewById(R.id.mapview);
		marker_label = (TextView) findViewById(R.id.textView1);
		comments = (EditText) findViewById(R.id.comments);
		vmake = (EditText) findViewById(R.id.vmake);
		vmodel = (EditText) findViewById(R.id.vmodel);
		color = (EditText) findViewById(R.id.color);
		reg = (EditText) findViewById(R.id.reg);
		date = (TextView) findViewById(R.id.date);
		type = (TextView) findViewById(R.id.type);
		expand = (ImageView) findViewById(R.id.expand);
		addpic = (ImageView) findViewById(R.id.addpic);
		pic1 = (CircularImageView) findViewById(R.id.pic1);
		pic2 = (CircularImageView) findViewById(R.id.pic2);
		pic3 = (CircularImageView) findViewById(R.id.pic3);
		info = new Data();
		// takenpic = (LinearLayout) findViewById(R.id.pic);
		send = (Button) findViewById(R.id.send);
		sdRoot = Environment.getExternalStorageDirectory();
		// cr8 folder
		File folder = new File(sdRoot + "/Vehicle Security");
		boolean success = true;

		if (!folder.exists()) {
			success = folder.mkdir();
		}
		if (success) {
			// Do something on success
		} else {

		}
		dir = "Vehicle Security/";

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
		// current date and time
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
		date.setText((info.getdateformate(datevalue + "" + timevalue)) + ampm);
		checkpic();
		picclick();
		expand.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (exp_col) {
					RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.MATCH_PARENT);
					mapview.setLayoutParams(parms);
					expand.setImageResource(R.drawable.ic_close_map);
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

		// fields
		type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ReportSighting.this);
				builder.setTitle(" Type Of Sighting ");

				builder.setSingleChoiceItems(typeSighting, tSighting,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								buffKey = which;
								type.setText(typeSighting[buffKey]);
								int selectedPosition = ((AlertDialog) dialog)
										.getListView().getCheckedItemPosition();
								// set buff to selected
								tSighting = buffKey;
								dialog.dismiss();
							}
						}).setCancelable(false);
				// .setPositiveButton("Ok",
				// new DialogInterface.OnClickListener() {
				// @Override
				// public void onClick(DialogInterface dialog,
				// int which) {
				//
				// // atPrefs.edit()
				// // .putInt(Activity_EButton_data.blood_value,
				// // selectedPosition)
				// // .commit();
				// // set blood grp value in number
				//
				// }
				// })
				// .setNegativeButton("Cancel",
				// new DialogInterface.OnClickListener() {
				// @Override
				// public void onClick(DialogInterface dialog,
				// int which) {
				//
				// }
				// });

				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final StringBuilder sb = new StringBuilder();

				final Dialog dialog = new Dialog(ReportSighting.this);

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
						date.setText((info.getdateformate(datevalue + ""
								+ timevalue)));
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});
		addpic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final CharSequence[] items = { "Take Photo", "From Gallery",
						"Cancel" };
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ReportSighting.this);
				builder.setTitle("Add Photo!");
				builder.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {
						if (items[item].equals("Take Photo")) {
							final Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(MediaStore.EXTRA_OUTPUT,
									setImageUri());
							startActivityForResult(intent, 2);
						} else if (items[item].equals("From Gallery")) {
							// Choose from Library
							Intent pickPhoto = new Intent(
									Intent.ACTION_PICK,
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							startActivityForResult(pickPhoto, 3);
						} else if (items[item].equals("Cancel")) {
							dialog.dismiss();
						}
					}
				});
				builder.show();
			}
		});
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new sendd().execute();
			}
		});
	}

	private void picclick() {
		pic1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				nopic = 1;
				clickd();

			}
		});
		pic2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				nopic = 2;
				clickd();

			}
		});
		pic3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				nopic = 3;
				clickd();

			}
		});

	}

	private void setpic() {
		photo = (Bitmap) decodeFile(imagepath);
		if (nopic == 1) {
			pic1.setVisibility(View.VISIBLE);
			pic1.setImageBitmap(photo);
			// upload = new sendd().execute();
			checkpic();
		} else if (nopic == 2) {
			pic2.setVisibility(View.VISIBLE);
			pic2.setImageBitmap(photo);
			// upload = new sendd().execute();
			checkpic();
		} else if (nopic == 3) {
			pic3.setVisibility(View.VISIBLE);
			pic3.setImageBitmap(photo);
			addpic.setVisibility(View.GONE);
			// upload = new sendd().execute();
			checkpic();
		}

	}

	private void clickd() {
		final CharSequence[] items = { "Take Photo", "From Gallery", "Cancel" };
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ReportSighting.this);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {

					final Intent intent = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
					startActivityForResult(intent, 2);
				} else if (items[item].equals("From Gallery")) {
					// Choose from Library
					Intent pickPhoto = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(pickPhoto, 3);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();

	}

	private void checkpic() {
		if (pic1.getVisibility() == View.VISIBLE
				&& pic2.getVisibility() == View.GONE
				&& pic3.getVisibility() == View.GONE) {

			nopic = 2;
		} else if (pic1.getVisibility() == View.VISIBLE
				&& pic2.getVisibility() == View.VISIBLE
				&& pic3.getVisibility() == View.GONE) {
			nopic = 3;
		} else if (pic1.getVisibility() == View.VISIBLE
				&& pic2.getVisibility() == View.VISIBLE
				&& pic3.getVisibility() == View.VISIBLE) {
			addpic.setVisibility(View.GONE);

			nopic = 4;
		} else {
			nopic = 1;
		}

	}

	// update profile pic
	private class sendd extends AsyncTask<Void, Void, Void> {
		String success, mess, response, user;
		Button next;
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ReportSighting.this);
			pDialog.setMessage("Updating ");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@SuppressLint("NewApi")
		@Override
		protected Void doInBackground(Void... params) {
			try {
				sendPost(
						"http://emgeesonsdevelopment.in/HH/mobile1.0/patients/uploadPhoto.php",
						imagepath);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;

		}

		@Override
		protected void onPostExecute(Void notUsed) {

			pDialog.dismiss();

		}

	}

	public void sendPost(String url, String imagePath) throws IOException,
			ClientProtocolException {
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost httppost = new HttpPost("http://192.168.1.112/test/test.php");
		File file = new File(imagePath);

		MultipartEntity mpEntity = new MultipartEntity();
		ContentBody cbFile = new FileBody(file);

		mpEntity.addPart("uploaded_file", cbFile);

		httppost.setEntity(mpEntity);
		System.out.println(httppost.getRequestLine());
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity resEntity = response.getEntity();
		System.out.println(response.getStatusLine());
		if (resEntity != null) {
			System.out.println(EntityUtils.toString(resEntity));
		}
		if (resEntity != null) {
			resEntity.consumeContent();
		}
		httpclient.getConnectionManager().shutdown();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		case 2:
			if (resultCode == RESULT_OK) {

				imagepath = getImagePath();
				photo = (Bitmap) decodeFile(imagepath);
				// photo = Bitmap.createScaledBitmap(photo, 74, 74, false);
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.PNG, 100, bytes);
				File f = new File(sdRoot, dir + "profilePic" + nopic + ".png");
				try {

					f.createNewFile();
					FileOutputStream fo = new FileOutputStream(f);
					fo.write(bytes.toByteArray());
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				imagepath = f.getAbsolutePath();
				setpic();
				// ch.add(imagepath);
				// photo = (Bitmap) decodeFile(imagepath);
				// CustomImageView image = new CustomImageView(
				// getApplicationContext());
				// image.setImageBitmap(photo);
				// takenpic.addView(image);
				// check();

			}

			break;
		case 3:
			if (resultCode == RESULT_OK) {

				Uri selectedImageUri = data.getData();
				imagepath = getPath(selectedImageUri);
				// Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
				photo = (Bitmap) decodeFile(imagepath);
				// photo = Bitmap.createScaledBitmap(photo, 74, 74, false);
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.PNG, 100, bytes);
				File f = new File(sdRoot, dir + "profilePic" + nopic + ".png");
				try {

					f.createNewFile();
					FileOutputStream fo = new FileOutputStream(f);
					fo.write(bytes.toByteArray());
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				imagepath = f.getAbsolutePath();
				setpic();
				// photo = (Bitmap) decodeFile(imagepath);
				// CustomImageView image = new CustomImageView(
				// getApplicationContext());
				// image.setImageBitmap(photo);
				// takenpic.addView(image);
				// check();

			}
			break;
		}

	}

	// private void check() {
	// nopic++;
	// if (takenpic.getChildCount() == 4) {
	//
	// takepic.setVisibility(View.INVISIBLE);
	// }
	// }

	public Bitmap decodeFile(String path) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, o);
			// The new size we want to scale to
			final int REQUIRED_SIZE = 280;

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

	public Uri setImageUri() {
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "profile.jpg");

		Uri imgUri = Uri.fromFile(file);
		imgPath = file.getAbsolutePath();
		return imgUri;
	}

	public String getImagePath() {
		return imgPath;
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private DatePicker.OnDateChangedListener datePickerListener = new DatePicker.OnDateChangedListener() {

		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			dates = dayOfMonth;
			years = year;
			months = monthOfYear + 1;
			datevalue = years + "-" + months + "-" + dates;
		}
	};

	public void gpscheck() {
		gps = new GPSTracker(ReportSighting.this);
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

	private class update extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			pos = "searching";
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (ReportSighting.this == null) {
				return null;
			}
			Geocoder geocoder = new Geocoder(ReportSighting.this,
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
}
