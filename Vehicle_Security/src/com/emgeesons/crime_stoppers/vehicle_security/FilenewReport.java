package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.android.Facebook;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class FilenewReport extends BaseActivity implements TextWatcher,
		LocationListener, android.location.LocationListener {
	double LATITUDE, LONGITUDE, slat, slon, olat, olon;
	GPSTracker gps;
	static LatLng currlocation;
	private GoogleMap map;
	int height;
	String pos, reponse, mess;
	RelativeLayout mapview, marker, vdetails;
	TextView marker_label;
	LocationManager locationManager;
	LatLng position;
	EditText comments;
	TextView date, type;
	boolean exp_col;
	int map_height;
	final static CharSequence[] typeReport = { "Theft", "Vandalism",
			"Stolen /Abandoned Vehicle?" };
	int tReport = -1;
	static int buffKey = 0;
	// fordate and time
	String datevalue, timevalue;
	int years, months, dates, Hrs, min;
	String ctimevalue, cdatevalue;
	ImageView typeimage, edit, addpic;
	// ImageView expand;
	TextView name, reg;
	List<VehicleData> vehicles;
	ArrayList<String> names = new ArrayList<String>();
	ArrayList<Integer> nos = new ArrayList<Integer>();
	ArrayList<String> types = new ArrayList<String>();
	ArrayList<String> regs = new ArrayList<String>();
	DatabaseHandler db;
	SQLiteDatabase dbb;
	int selected = 0;
	Dialog dialog;
	RelativeLayout details, adddetails;
	Button go;
	boolean btype;
	Data info;
	int nopic;
	Bitmap photo;
	ArrayList<String> f = new ArrayList<String>();
	CircularImageView pic1, pic2, pic3;
	String dir, imgPath;
	File sdRoot;
	Button send;
	Connection_Detector cd = new Connection_Detector(this);
	Boolean IsInternetPresent;
	private String imagepath = null;
	String photourl, tphotourl;
	Facebook mFacebook;
	private ProgressDialog mProgress;
	String file_url = Data.url + "fileNewReport.php";
	SharedPreferences atPrefs;
	static String fphoto = "fphoto";
	static String tphoto = "tphoto";
	SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.filenewreport_activity);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'>File New Report </font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.app_icon);

		sharedpreferences = getSharedPreferences(Data.MyPREFERENCES,
				Context.MODE_PRIVATE);
		atPrefs = PreferenceManager
				.getDefaultSharedPreferences(FilenewReport.this);
		db = new DatabaseHandler(FilenewReport.this);
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();
		info = new Data();
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
		send = (Button) findViewById(R.id.send);
		pic1 = (CircularImageView) findViewById(R.id.pic1);
		pic2 = (CircularImageView) findViewById(R.id.pic2);
		pic3 = (CircularImageView) findViewById(R.id.pic3);
		details = (RelativeLayout) findViewById(R.id.details);
		marker = (RelativeLayout) findViewById(R.id.marker);
		mapview = (RelativeLayout) findViewById(R.id.mapview);
		marker_label = (TextView) findViewById(R.id.textView1);
		comments = (EditText) findViewById(R.id.comments);

		date = (TextView) findViewById(R.id.date);
		type = (TextView) findViewById(R.id.type);
		// expand = (ImageView) findViewById(R.id.expand);
		typeimage = (ImageView) findViewById(R.id.typeimage);
		edit = (ImageView) findViewById(R.id.edit);
		vdetails = (RelativeLayout) findViewById(R.id.vdetails);
		name = (TextView) findViewById(R.id.name);
		reg = (TextView) findViewById(R.id.reg);
		addpic = (ImageView) findViewById(R.id.addpic);
		adddetails = (RelativeLayout) findViewById(R.id.adddetails);
		vehicles = db.getVehicleData();
		if (!(vehicles.size() == 0)) {

			details.setVisibility(View.VISIBLE);
			adddetails.setVisibility(View.GONE);
			for (int i = 0; i < vehicles.size(); i++) {
				names.add(vehicles.get(i).getvehicle_model());
				nos.add(vehicles.get(i).getvehicle_id());
				types.add(vehicles.get(i).getvehicle_type());
				regs.add(vehicles.get(i).getvehicle_reg());
			}
			select();

		} else {

			details.setVisibility(View.GONE);
			adddetails.setVisibility(View.VISIBLE);

		}

		go = (Button) findViewById(R.id.go);
		go.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent next = new Intent(getApplicationContext(),
						ProfileScreen.class);
				startActivity(next);
				finish();
			}
		});

		checkpic();
		picclick();
		vdetails.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog = new Dialog(FilenewReport.this);
				dialog.setContentView(R.layout.vehicle_info);
				dialog.setTitle("Select Vehicle");
				ListView list = (ListView) dialog.findViewById(R.id.listView1);
				list.setAdapter(new ffAdapter());

				dialog.show();
			}
		});
		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog = new Dialog(FilenewReport.this);
				dialog.setContentView(R.layout.vehicle_info);
				dialog.setTitle("Select Vehicle");
				ListView list = (ListView) dialog.findViewById(R.id.listView1);
				list.setAdapter(new ffAdapter());

				dialog.show();
			}
		});
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
		timevalue = String.format("%02d:%02d", Hrs, min);
		// timevalue = Hrs + ":" + min;
		datevalue = years + "-" + months + "-" + dates;
		ctimevalue = timevalue;
		cdatevalue = datevalue;
		System.out.println(cdatevalue + "," + ctimevalue);
		date.setText((info.getdateformate(datevalue + "-" + timevalue)));
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
		// check gps is on\off and set location
		gpscheck();
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				IsInternetPresent = cd.isConnectingToInternet();
				if (IsInternetPresent == false) {
					cd.showNoInternetPopup();
				} else {
					if (type.getText().toString().isEmpty()) {

						type.setTextColor(getResources().getColor(R.color.red));
						type.setHintTextColor(getResources().getColor(
								R.color.red));
						btype = false;
					}
					if (btype == true) {
						// Intent share = new Intent(getApplicationContext(),
						// Share.class);
						// startActivity(share);
						// finish();
						new sendd().execute();
					}

				}

				// publishStory();

			}
		});

		// expand.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// if (exp_col) {
		// RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
		// LayoutParams.WRAP_CONTENT,
		// LayoutParams.MATCH_PARENT);
		// expand.setImageResource(R.drawable.ic_close_map);
		// mapview.setLayoutParams(parms);
		// exp_col = false;
		// } else {
		// exp_col = true;
		// expand.setImageResource(R.drawable.ic_expand_map);
		// RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
		// LayoutParams.WRAP_CONTENT, map_height);
		//
		// mapview.setLayoutParams(parms);
		// }
		// }
		// });
		type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
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
								type.setText(typeReport[buffKey]);
								int selectedPosition = ((AlertDialog) dialog)
										.getListView().getCheckedItemPosition();
								tReport = buffKey;
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
		addpic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final CharSequence[] items = { "Take Photo", "From Gallery",
						"Cancel" };
				AlertDialog.Builder builder = new AlertDialog.Builder(
						FilenewReport.this);
				builder.setTitle("Add Photo!");
				builder.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {
						atPrefs.edit().putString(callcheck, "True").commit();
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
							atPrefs.edit().putString(callcheck, "false")
									.commit();
							dialog.dismiss();
						}
					}
				});
				builder.show();
			}
		});
		date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final StringBuilder sb = new StringBuilder();

				final Dialog dialog = new Dialog(FilenewReport.this);

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

	}

	private void publishStory() {
		Session session = Session.getActiveSession();

		if (session != null) {

			// Check for publish permissions
			// List<String> permissions = session.getPermissions();
			// if (!isSubsetOf(PERMISSIONS, permissions)) {
			// pendingPublishReauthorization = true;
			// Session.NewPermissionsRequest newPermissionsRequest = new
			// Session.NewPermissionsRequest(
			// this, PERMISSIONS);
			// session.requestNewPublishPermissions(newPermissionsRequest);
			// return;
			// }

			Bundle postParams = new Bundle();
			postParams.putString("message", "TEsttttttttttttttt");
			postParams.putString("name", "Facebook SDK for Android");
			// postParams.putString("caption",
			// "Build great social apps and get more installs.");
			// postParams
			// .putString(
			// "description",
			// "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
			// postParams.putString("link",
			// "https://developers.facebook.com/android");
			postParams
					.putString("picture",
							"https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

			Request.Callback callback = new Request.Callback() {
				public void onCompleted(Response response) {
					JSONObject graphResponse = response.getGraphObject()
							.getInnerJSONObject();
					String postId = null;
					try {
						postId = graphResponse.getString("id");
					} catch (JSONException e) {
						Log.i("TAG", "JSON error " + e.getMessage());
					}
					FacebookRequestError error = response.getError();
					if (error != null) {
						Toast.makeText(getApplicationContext(),
								error.getErrorMessage(), Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(getApplicationContext(), postId,
								Toast.LENGTH_LONG).show();
					}
				}
			};

			Request request = new Request(session, "me/feed", postParams,
					HttpMethod.POST, callback);

			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		} else {

			session = Session.openActiveSessionFromCache(FilenewReport.this);
			publishStory();
		}

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
			nopic = 2;
		} else if (nopic == 2) {
			pic2.setVisibility(View.VISIBLE);
			pic2.setImageBitmap(photo);
			nopic = 3;
		} else if (nopic == 3) {
			pic3.setVisibility(View.VISIBLE);
			pic3.setImageBitmap(photo);
			addpic.setVisibility(View.GONE);
			nopic = 4;
		}

	}

	private void clickd() {
		final CharSequence[] items = { "Take Photo", "From Gallery", "Cancel" };
		AlertDialog.Builder builder = new AlertDialog.Builder(
				FilenewReport.this);
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
					dialog.dismiss();
					atPrefs.edit().putString(callcheck, "false").commit();
				}
			}
		});
		builder.show();

	}

	// update profile pic
	private class sendd extends AsyncTask<Void, Void, Void> {
		String success, mess, response, user;
		Button next;
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(FilenewReport.this);
			pDialog.setMessage("Updating ");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@SuppressLint("NewApi")
		@Override
		protected Void doInBackground(Void... params) {
			try {
				sendPost(file_url, imagepath);
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
		final JSONArray jsonphotoArr;

		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost httppost = new HttpPost(url);
		MultipartEntity mpEntity = null;
		mpEntity = new MultipartEntity();
		String[] im = { Data.p1, Data.p2, Data.p3 };
		Editor editor = sharedpreferences.edit();

		for (int i = 0; i < f.size(); i++) {

			File file = new File(f.get(i));
			editor.putString(im[i], f.get(i));
			editor.commit();
			ContentBody cbFile = new FileBody(file);
			mpEntity.addPart("image" + "" + (i + 1), cbFile);
		}

		info.device();
		info.showInfo(getApplicationContext());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfs = new SimpleDateFormat("HH:mm");
		String currentTime = sdfs.format(new Date());
		String currentDate = sdf.format(new Date());

		mpEntity.addPart("reportType",
				new StringBody(type.getText().toString()));
		mpEntity.addPart("vehicleId",
				new StringBody(String.valueOf(nos.get(selected))));
		mpEntity.addPart("originalDate", new StringBody(currentDate));
		mpEntity.addPart("originalTime", new StringBody(currentTime));
		mpEntity.addPart("selectedDate", new StringBody(datevalue));
		mpEntity.addPart("selectedTime", new StringBody(timevalue));
		mpEntity.addPart("noPhotos", new StringBody(String.valueOf(f.size())));
		mpEntity.addPart("comments", new StringBody(comments.getText()
				.toString()));
		mpEntity.addPart("os", new StringBody(info.manufacturer));
		mpEntity.addPart("make", new StringBody("Android" + " " + info.Version));
		mpEntity.addPart("model", new StringBody(info.model));
		mpEntity.addPart("userId", new StringBody(info.user_id));
		mpEntity.addPart("pin", new StringBody(info.pin));
		mpEntity.addPart("selectedLatitude",
				new StringBody(String.valueOf(slat)));
		mpEntity.addPart("selectedLongitude",
				new StringBody(String.valueOf(slon)));
		mpEntity.addPart("originalLatitude",
				new StringBody(String.valueOf(olat)));
		mpEntity.addPart("originalLongitude",
				new StringBody(String.valueOf(olon)));
		mpEntity.addPart("location", new StringBody(marker_label.getText()
				.toString()));
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
			jsonphotoArr = profile.getJSONArray("response");

			if (success.equals("success")) {

				runOnUiThread(new Runnable() {
					public void run() {
						Intent share = new Intent(getApplicationContext(),
								Share.class);
						if (!(f.size() == 0)) {
							try {
								tphotourl = f.get(0);
								photourl = jsonphotoArr.getJSONObject(0)
										.getString("photo1");
								atPrefs.edit().putString(fphoto, photourl)
										.commit();
								atPrefs.edit().putString(tphoto, tphotourl)
										.commit();

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						Editor editor = sharedpreferences.edit();

						editor.putString(Data.vid,
								String.valueOf(nos.get(selected)));
						editor.putString(Data.rtype, type.getText().toString());
						editor.putString(Data.time, datevalue + "*" + timevalue);
						editor.putString(Data.comm, comments.getText()
								.toString());
						editor.putString(Data.location, pos);

						editor.commit();
						startActivity(share);
						finish();
					}
				});

			} else {
				runOnUiThread(new Runnable() {
					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								FilenewReport.this).create();
						Dialog.setTitle("Error");
						Dialog.setMessage(mess);
						Dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
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

	private void select() {
		name.setText(names.get(selected));
		reg.setText(regs.get(selected));
		if (vehicles.get(selected).getvehicle_type().equalsIgnoreCase("Car")) {
			typeimage.setImageResource(R.drawable.ic_car);
		} else if (vehicles.get(selected).getvehicle_type()
				.equalsIgnoreCase("Bicycle")) {
			typeimage.setImageResource(R.drawable.ic_cycle);
		} else if (vehicles.get(selected).getvehicle_type()
				.equalsIgnoreCase("MotorCycle")) {
			typeimage.setImageResource(R.drawable.ic_bike);
		} else {

			typeimage.setImageResource(R.drawable.ic_other);

		}
	}

	public void gpscheck() {
		gps = new GPSTracker(FilenewReport.this);
		if (gps.canGetLocation()) {
			LATITUDE = gps.getLatitude();
			LONGITUDE = gps.getLongitude();
			olat = gps.getLatitude();
			olon = gps.getLongitude();
			position = new LatLng(LATITUDE, LONGITUDE);
			// Toast.makeText(getApplicationContext(), String.valueOf(position),
			// Toast.LENGTH_LONG).show();
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

	class ffAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return vehicles.size();
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View vv;
			TextView name, reg;
			ImageView typei;
			RadioButton ch;

			if (convertView == null) {
				vv = getLayoutInflater().inflate(R.layout.vehicleinfo_dialog,
						null);
			} else {
				vv = convertView;
			}
			// vv.setBackgroundResource(R.drawable.box_whiteb);
			// ids = (TextView) vv.findViewById(R.id.ids);

			name = (TextView) vv.findViewById(R.id.model);
			reg = (TextView) vv.findViewById(R.id.reg);
			typei = (ImageView) vv.findViewById(R.id.imageView1);
			ch = (RadioButton) vv.findViewById(R.id.radioButton1);
			name.setText(names.get(position));
			reg.setText(regs.get(position));
			if (types.get(position).equalsIgnoreCase("Car")) {
				typei.setImageResource(R.drawable.ic_car);
			} else if (types.get(position).equalsIgnoreCase("Bicycle")) {
				typei.setImageResource(R.drawable.ic_cycle);
			} else if (types.get(position).equalsIgnoreCase("MotorCycle")) {
				typei.setImageResource(R.drawable.ic_bike);
			} else {

				typei.setImageResource(R.drawable.ic_other);

			}
			ch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					selected = position;
					dialog.dismiss();
					select();
				}
			});
			vv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					selected = position;
					dialog.dismiss();
					select();

				}
			});
			return vv;
		}
	}

	private void onchange() {
		marker_label.setText(pos);
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
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.filenewreport_activity;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		gpscheck();
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

}
