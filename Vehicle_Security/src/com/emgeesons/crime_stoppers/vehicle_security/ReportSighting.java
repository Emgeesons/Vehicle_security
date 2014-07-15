package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class ReportSighting extends BaseActivity implements LocationListener,
		TextWatcher, android.location.LocationListener {
	double LATITUDE, LONGITUDE, slon, slat, olat, olon;
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
	// ImageView expan;
	ImageView addpic;
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
	String dir, imgPath, reponse, points;
	String[] names;
	private String imagepath = null;
	File sdRoot;
	CircularImageView pic1, pic2, pic3;
	int nopic;
	Bitmap photo;
	boolean btype, breg, bmake, bmodel, bcolor;
	Connection_Detector cd = new Connection_Detector(this);
	Boolean IsInternetPresent;
	LocationClient mLocationClient;
	LocationRequest locationRequest;
	ArrayList<String> f = new ArrayList<String>();
	String report_url = Data.url + "reportSighting.php";
	ProgressDialog pDialog;
	DatabaseHandler db;
	SQLiteDatabase dbb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.reportsighting_activity);
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
		// expand = (ImageView) findViewById(R.id.expand);
		addpic = (ImageView) findViewById(R.id.addpic);
		pic1 = (CircularImageView) findViewById(R.id.pic1);
		pic2 = (CircularImageView) findViewById(R.id.pic2);
		pic3 = (CircularImageView) findViewById(R.id.pic3);
		info = new Data();
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();

		if (extras != null) {

			String types = extras.getString("type");
			String regs = extras.getString("reg");
			String com = extras.getString("com");
			String make = extras.getString("make");
			String model = extras.getString("model");
			vmake.setText(make);
			vmodel.setText(model);
			reg.setText(regs);
			comments.setText(com);

			if (types.equalsIgnoreCase("Theft")) {
				tSighting = 0;
			} else if (types.equalsIgnoreCase("Vandalism")) {
				tSighting = 1;
			} else if (types.equalsIgnoreCase("Suspicious Activity")) {
				tSighting = 2;
			} else if (types.equalsIgnoreCase("Other")) {
				tSighting = 3;
			}
			type.setText(typeSighting[tSighting]);
			btype = true;
			bmake = true;
			bmodel = true;
			breg = true;
			bcolor = true;
		}
		vmake.addTextChangedListener(this);
		type.addTextChangedListener(this);
		vmodel.addTextChangedListener(this);
		reg.addTextChangedListener(this);
		color.addTextChangedListener(this);
		send = (Button) findViewById(R.id.send);
		db = new DatabaseHandler(ReportSighting.this);
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();

		sdRoot = Environment.getExternalStorageDirectory();

		// cr8 folder
		File folder = new File(sdRoot + "/My Wheel");
		boolean success = true;

		if (!folder.exists()) {
			success = folder.mkdir();
		}
		if (success) {
			// Do something on success
		} else {

		}
		dir = "My Wheel/";

		height = getWindowManager().getDefaultDisplay().getHeight();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				30000, 100, this);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 30000, 100, this);
		this.registerReceiver(this.mlocation, new IntentFilter(
				android.location.LocationManager.PROVIDERS_CHANGED_ACTION));
		map_height = height * 30 / 100;
		RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, map_height);
		mapview.setLayoutParams(parms);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.setMyLocationEnabled(true);
		map.getMyLocation();
		// check gps is on\off and set location

		// current date and time
		Calendar c = Calendar.getInstance();
		years = c.get(Calendar.YEAR);
		months = c.get(Calendar.MONTH) + 1;
		dates = c.get(Calendar.DAY_OF_MONTH);

		Hrs = c.get(Calendar.HOUR_OF_DAY);
		min = c.get(Calendar.MINUTE);
		// String ampm = "AM";
		// if (Hrs >= 12) {
		// Hrs -= 12;
		// ampm = "PM";
		// }
		// mLocationClient = new LocationClient(this, this, this);
		// mLocationClient.connect();
		// locationRequest = new LocationRequest().create();
		// // Use high accuracy
		// locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// // Set the update interval to 5 seconds
		// locationRequest.setInterval(10000);
		// // Set the fastest update interval to 1 second
		// locationRequest.setFastestInterval(5000);

		gpscheck();
		timevalue = String.format("%02d:%02d", Hrs, min);
		// timevalue = Hrs + ":" + min;
		datevalue = years + "-" + months + "-" + dates;
		ctimevalue = timevalue;
		cdatevalue = datevalue;
		System.out.println(cdatevalue + "," + ctimevalue);
		date.setText((info.getdateformate(datevalue + "-" + timevalue)));
		checkpic();
		picclick();
		// expand.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// if (exp_col) {
		// RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
		// LayoutParams.WRAP_CONTENT,
		// LayoutParams.MATCH_PARENT);
		// mapview.setLayoutParams(parms);
		// expand.setImageResource(R.drawable.ic_close_map);
		// exp_col = false;
		// } else {
		// exp_col = true;
		// expand.setImageResource(R.drawable.ic_expand_map);
		// RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
		// LayoutParams.WRAP_CONTENT, map_height);
		// mapview.setLayoutParams(parms);
		// }
		// }
		// });

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

								tSighting = buffKey;
								btype = true;
								type.setTextColor(getResources().getColor(
										R.color.black));
								dialog.dismiss();
							}
						}).setCancelable(false);

				AlertDialog alert = builder.create();
				alert.setCancelable(true);
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

				datePicker.init(years, months - 1, dates, datePickerListener);

				timePicker
						.setOnTimeChangedListener(new OnTimeChangedListener() {

							@Override
							public void onTimeChanged(TimePicker view,
									int hourOfDay, int minute) {

								Hrs = hourOfDay;
								min = minute;
								// String ampm = "AM";
								// if (Hrs >= 12) {
								// Hrs -= 12;
								// ampm = "PM";
								// }
								timevalue = Hrs + ":" + min;

							}
						});
				ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						date.setText((info.getdateformate(datevalue + "-"
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
							atPrefs.edit().putString(callcheck, "True")
									.commit();
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
							atPrefs.edit().putString(callcheck, "false")
									.commit();
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
				IsInternetPresent = cd.isConnectingToInternet();
				if (IsInternetPresent == false) {
					cd.showNoInternetPopup();
				} else {
					if (vmake.getText().toString().length() < 2) {

						vmake.setTextColor(getResources().getColor(R.color.red));
						vmake.setHintTextColor(getResources().getColor(
								R.color.red));
						bmake = false;
					}
					if (vmodel.getText().toString().length() < 2) {

						vmodel.setTextColor(getResources()
								.getColor(R.color.red));
						vmodel.setHintTextColor(getResources().getColor(
								R.color.red));
						bmodel = false;
					}
					// if (!(reg.getText().toString().length() == 10)) {
					//
					// reg.setTextColor(getResources().getColor(R.color.red));
					// reg.setHintTextColor(getResources().getColor(
					// R.color.red));
					// breg = false;
					// }
					if (color.getText().toString().length() < 3) {

						color.setTextColor(getResources().getColor(R.color.red));
						color.setHintTextColor(getResources().getColor(
								R.color.red));
						bcolor = false;
					}
					if (type.getText().toString().isEmpty()) {

						type.setTextColor(getResources().getColor(R.color.red));
						type.setHintTextColor(getResources().getColor(
								R.color.red));
						btype = false;
					}
					if (btype == true && bcolor == true && bmake == true
							&& bmodel == true) {
						map.getMyLocation();
						new sendd().execute();
					}

				}
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
		f.add(imagepath);
		if (nopic == 1) {
			pic1.setVisibility(View.VISIBLE);
			pic1.setImageBitmap(photo);
			// upload = new sendd().execute();
			nopic = 2;
		} else if (nopic == 2) {
			pic2.setVisibility(View.VISIBLE);
			pic2.setImageBitmap(photo);
			// upload = new sendd().execute();
			nopic = 3;
		} else if (nopic == 3) {
			pic3.setVisibility(View.VISIBLE);
			pic3.setImageBitmap(photo);
			addpic.setVisibility(View.GONE);
			// upload = new sendd().execute();
			nopic = 4;
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
				atPrefs.edit().putString(callcheck, "True").commit();
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
					atPrefs.edit().putString(callcheck, "false").commit();
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
				sendPost(report_url, imagepath);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;

		}

		@Override
		protected void onPostExecute(Void notUsed) {
			pDialog.dismiss();

		}

	}

	@SuppressWarnings("deprecation")
	public void sendPost(String url, String imagePath) throws IOException,
			ClientProtocolException, JSONException {
		final String mess;
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost httppost = new HttpPost(url);
		MultipartEntity mpEntity = null;
		mpEntity = new MultipartEntity();
		for (int i = 0; i < f.size(); i++) {
			File file = new File(f.get(i));
			ContentBody cbFile = new FileBody(file);
			mpEntity.addPart("image" + "" + (i + 1), cbFile);
		}

		info.device();
		info.showInfo(getApplicationContext());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfs = new SimpleDateFormat("HH:mm");
		String currentTime = sdfs.format(new Date());
		String currentDate = sdf.format(new Date());
		mpEntity.addPart("originalDate", new StringBody(currentDate));
		mpEntity.addPart("orginalTime", new StringBody(currentTime));
		mpEntity.addPart("selectedDate", new StringBody(datevalue));
		mpEntity.addPart("selectedTime", new StringBody(timevalue));
		mpEntity.addPart("sightingType", new StringBody(type.getText()
				.toString()));
		mpEntity.addPart("vehicleMake", new StringBody(vmake.getText()
				.toString()));
		mpEntity.addPart("vehicleModel", new StringBody(vmodel.getText()
				.toString()));
		mpEntity.addPart("vehicleColour", new StringBody(color.getText()
				.toString()));
		mpEntity.addPart("noPhotos", new StringBody(String.valueOf(f.size())));
		mpEntity.addPart("registerationNumber", new StringBody(reg.getText()
				.toString()));
		mpEntity.addPart("comments", new StringBody(comments.getText()
				.toString()));
		mpEntity.addPart("os", new StringBody(info.manufacturer));
		mpEntity.addPart("make", new StringBody("Android" + " " + info.Version));
		mpEntity.addPart("model", new StringBody(info.model));
		mpEntity.addPart("userId", new StringBody(info.user_id));
		mpEntity.addPart("pin", new StringBody(info.pin));
		mpEntity.addPart("selectedLatitutde",
				new StringBody(String.valueOf(slat)));
		mpEntity.addPart("selectedLongitude",
				new StringBody(String.valueOf(slon)));
		mpEntity.addPart("originalLatitude",
				new StringBody(String.valueOf(olat)));
		mpEntity.addPart("originalLongitude",
				new StringBody(String.valueOf(olon)));
		httppost.setEntity(mpEntity);
		System.out.println(httppost.getRequestLine());
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity resEntity = response.getEntity();
		System.out.println(response.getStatusLine());
		if (resEntity != null) {
			reponse = EntityUtils.toString(resEntity);
			System.out.println(reponse);
			JSONObject profile = new JSONObject(reponse);
			String success = profile.getString("status");
			mess = profile.getString("message");
			JSONArray jsonMainArr = profile.getJSONArray("response");
			if (success.equals("success")) {
				points = jsonMainArr.getJSONObject(0).getString(
						"samaritan_points");
				runOnUiThread(new Runnable() {

					public void run() {
						final Dialog dialog = new Dialog(ReportSighting.this);
						dialog.setContentView(R.layout.feedbackpoint);
						dialog.setTitle("Good Job!");
						TextView line, point, totalp;
						RelativeLayout close, profile;
						totalp = (TextView) dialog.findViewById(R.id.points);
						line = (TextView) dialog.findViewById(R.id.textView1);
						point = (TextView) dialog.findViewById(R.id.textView3);
						profile = (RelativeLayout) dialog
								.findViewById(R.id.profile);
						point.setText("50");
						line.setText("You  earned yourself 50 good Samaritan points!!!");
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
						totalp.setText("Total Samaritan Points :" + " "
								+ points);
						dialog.setCancelable(false);
						dialog.show();
						SQLiteDatabase dbbb = db.getReadableDatabase();
						dbbb.execSQL("UPDATE profile SET spoints = '" + points
								+ "'");

					}
				});

			}
			if (!success.equals("success")) {
				runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								ReportSighting.this).create();
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
			olat = gps.getLatitude();
			olon = gps.getLongitude();
			position = new LatLng(LATITUDE, LONGITUDE);
//			Toast.makeText(getApplicationContext(), String.valueOf(position),
//					Toast.LENGTH_LONG).show();
			map.getUiSettings().setZoomControlsEnabled(false);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
			new update().execute();

			// when map is move
			map.setOnCameraChangeListener(new OnCameraChangeListener() {
				public void onCameraChange(CameraPosition arg0) {

					map.clear();
					LATITUDE = arg0.target.latitude;
					LONGITUDE = arg0.target.longitude;
					slat = arg0.target.latitude;
					slon = arg0.target.longitude;
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
			map.setOnCameraChangeListener(new OnCameraChangeListener() {
				public void onCameraChange(CameraPosition arg0) {

					map.clear();
					LATITUDE = arg0.target.latitude;
					LONGITUDE = arg0.target.longitude;
					slat = arg0.target.latitude;
					slon = arg0.target.longitude;
					new update().execute();
					onchange();

					// pos = String.valueOf(arg0.target.latitude) + " "
					// + String.valueOf(arg0.target.longitude);

				}
			});
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

	@Override
	public void afterTextChanged(Editable arg0) {

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {

		vmake.setTextColor(getResources().getColor(R.color.black));
		vmodel.setTextColor(getResources().getColor(R.color.black));
		reg.setTextColor(getResources().getColor(R.color.black));
		color.setTextColor(getResources().getColor(R.color.black));

		bmake = true;
		bmodel = true;
		breg = true;
		bcolor = true;

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void onLocationChanged(Location location) {

		gpscheck();

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
		return R.layout.reportsighting_activity;
	}
}
