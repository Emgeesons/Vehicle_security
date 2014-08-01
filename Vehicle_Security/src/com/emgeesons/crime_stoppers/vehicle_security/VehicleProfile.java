package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.internal.el;

public class VehicleProfile extends BaseActivity {
	TextView title, reg, type, make, model, body, eng, vin, color, acc, cname,
			policy, expiry, status, number, state;
	ImageView vpic, addpic;
	String id;
	Data info;
	RelativeLayout ins;
	Button addins, edit, Delete;
	GPSTracker gps;
	int ttype;
	String dir, imgPath;
	private String imagepath = null;
	File sdRoot;
	String call;
	RelativeLayout imagesre;
	CircularImageView pic1, pic2, pic3;
	int nopic;
	File folder;
	Bitmap photo;
	Boolean IsInternetPresent;
	private AsyncTask<Void, Void, Void> upload, getprofile;
	String uploadPhoto_url = Data.url + "uploadVehiclePic.php";
	private AsyncTask<Void, Void, Void> del;
	String del_url = Data.url + "deleteVehicle.php";
	ProgressDialog pDialog;
	String names;
	List<VehicleData> vehicles;
	DatabaseHandler db;
	SQLiteDatabase dbb;
	File[] no;
	JSONArray jsonVehicleArr;
	String profile_url = Data.url + "getProfile.php";
	SharedPreferences atPrefs;
	double LATITUDE;
	double LONGITUDE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.vehicle_profile);

		title = (TextView) findViewById(R.id.model);
		reg = (TextView) findViewById(R.id.reg);
		type = (TextView) findViewById(R.id.vtype);
		make = (TextView) findViewById(R.id.vmake);
		model = (TextView) findViewById(R.id.vmodel);
		body = (TextView) findViewById(R.id.vbody);
		eng = (TextView) findViewById(R.id.vengine);
		vin = (TextView) findViewById(R.id.vvin);
		color = (TextView) findViewById(R.id.vcolor);
		acc = (TextView) findViewById(R.id.vacc);
		cname = (TextView) findViewById(R.id.vname);
		policy = (TextView) findViewById(R.id.vpolicy);
		expiry = (TextView) findViewById(R.id.vexpiry);
		state = (TextView) findViewById(R.id.vstate);
		vpic = (ImageView) findViewById(R.id.imagetype);
		ins = (RelativeLayout) findViewById(R.id.ins);
		addins = (Button) findViewById(R.id.addins);
		edit = (Button) findViewById(R.id.edit);
		status = (TextView) findViewById(R.id.status);
		number = (TextView) findViewById(R.id.vnumber);
		imagesre = (RelativeLayout) findViewById(R.id.pic);
		addpic = (ImageView) findViewById(R.id.addpic);
		pic1 = (CircularImageView) findViewById(R.id.pic1);
		pic2 = (CircularImageView) findViewById(R.id.pic2);
		pic3 = (CircularImageView) findViewById(R.id.pic3);
		Delete = (Button) findViewById(R.id.Delete);
		Intent intent = getIntent();
		id = intent.getStringExtra("id");
		info = new Data();
		info.vehicleInfo(getApplicationContext(), id);
		title.setText(info.make + " " + info.vmodel);
		type.setText(info.type);
		make.setText(info.make);
		model.setText(info.vmodel);
		eng.setText(info.eng);
		body.setText(info.body);
		vin.setText(info.vin);
		color.setText(info.color);
		acc.setText(info.acc);
		number.setText(info.inum);
		state.setText(info.state);
		atPrefs = PreferenceManager
				.getDefaultSharedPreferences(VehicleProfile.this);
		db = new DatabaseHandler(getApplicationContext());
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();
		vehicles = db.getVehicleData();

		sdRoot = Environment.getExternalStorageDirectory();
		folder = new File(sdRoot + "/My Wheel/" + id);

		boolean success = true;

		if (!folder.exists()) {
			success = folder.mkdir();
		}
		if (success) {
			// Do something on success
		} else {

		}

		dir = "My Wheel/" + id + "/";

		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'>" + info.vmodel
						+ "</font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.app_icon);
		if (type.getText().toString().equalsIgnoreCase("Bicycle")) {
			reg.setText("Serial No:" + info.reg);
		} else {
			reg.setText("Registration no:" + info.reg);
		}

		if (type.getText().toString().equalsIgnoreCase("Car")) {
			vpic.setImageResource(R.drawable.ic_car);
		} else if (type.getText().toString().equalsIgnoreCase("Bicycle")) {
			vpic.setImageResource(R.drawable.ic_cycle);

		} else if (type.getText().toString().equalsIgnoreCase("MotorCycle")) {
			vpic.setImageResource(R.drawable.ic_bike);
		} else {

			vpic.setImageResource(R.drawable.ic_other);
		}

		if (!info.status.isEmpty()) {
			ImageView s = (ImageView) findViewById(R.id.imageView1);
			s.setVisibility(View.VISIBLE);
			status.setVisibility(View.VISIBLE);
			status.setText(info.status);
			title.setTextColor(getResources().getColor(R.color.yellow));

		} else {
			ImageView s = (ImageView) findViewById(R.id.imageView1);
			s.setVisibility(View.GONE);
			status.setVisibility(View.GONE);
		}

		if (info.body.isEmpty()
				&& type.getText().toString().equalsIgnoreCase("Car")) {
			TextView b = (TextView) findViewById(R.id.body);
			b.setVisibility(View.VISIBLE);
			body.setVisibility(View.VISIBLE);

		} else {
			TextView b = (TextView) findViewById(R.id.body);
			b.setVisibility(View.GONE);
			body.setVisibility(View.GONE);
		}
		// if (info.eng.isEmpty()) {
		// TextView e = (TextView) findViewById(R.id.engine);
		// e.setVisibility(View.GONE);
		// eng.setVisibility(View.GONE);
		// }
		// if (info.acc.isEmpty()) {
		// TextView accc = (TextView) findViewById(R.id.accessories);
		// accc.setVisibility(View.GONE);
		// acc.setVisibility(View.GONE);
		// }
		// if (info.vin.isEmpty()) {
		// TextView v = (TextView) findViewById(R.id.vin);
		// v.setVisibility(View.GONE);
		// vin.setVisibility(View.GONE);
		// }

		if (info.iname.isEmpty()) {
			// ins.setVisibility(View.GONE);
			addins.setBackgroundResource(R.drawable.blue_button);
			call = "first";
			addins.setTextColor(getResources().getColor(R.color.white));

		} else {
			ins.setVisibility(View.VISIBLE);
			// change status text
			if (status.getText().toString().contains("Add Insurance")) {
				status.setText("Add Vehicle Photos");
			}
			addins.setBackgroundResource(R.drawable.box_whiteb);
			addins.setText("Edit Insurance");
			call = "second";
			addins.setTextColor(getResources().getColor(R.color.black));
			cname.setText(info.iname);
			policy.setText(info.ipolicy);
			if (info.exp.contains("0000-00-00") || info.exp.isEmpty()) {
				// TextView ex = (TextView) findViewById(R.id.expiry);
				// ex.setVisibility(View.GONE);
				// expiry.setVisibility(View.GONE);
				expiry.setVisibility(View.GONE);
			} else {
				expiry.setText(getdateformate(info.exp));
			}

		}

		// check image

		ImageView[] IMGS = { pic1, pic2, pic3 };
		if (folder.exists()) {

			no = folder.listFiles();
			if (no.length == 3) {

				addpic.setVisibility(View.GONE);

			}
			for (int i = 0; i < no.length; i++) {
				if (no[i].isFile()) {
					System.out.println("File " + no[i]);
					imagepath = no[i].getAbsolutePath();
					Bitmap photo = (Bitmap) decodeFile(imagepath);
					IMGS[i].setVisibility(View.VISIBLE);
					IMGS[i].setImageBitmap(photo);
					checkpic();
				} else if (no[i].isDirectory()) {
					System.out.println("Directory " + no[i].getName());
				}
			}

		}
		// refresh ui
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {

				Log.i("call", "call");
				ImageView[] IMGS = { pic1, pic2, pic3 };
				if (folder.exists()) {
					File[] no = folder.listFiles();
					if (no.length == 3) {

						addpic.setVisibility(View.GONE);

					}

					for (int i = 0; i < no.length; i++) {

						if (no[i].isFile()) {
							System.out.println("File " + no[i]);
							imagepath = no[i].getAbsolutePath();
							Bitmap photo = (Bitmap) decodeFile(imagepath);
							IMGS[i].setVisibility(View.VISIBLE);
							IMGS[i].setImageBitmap(photo);
							checkpic();
						} else if (no[i].isDirectory()) {
							System.out.println("Directory " + no[i].getName());
						}
					}

				}

			}
		}, 3000);

		// cal no of pic
		checkpic();
		// click on pic
		picclick();

		gps = new GPSTracker(VehicleProfile.this);
		if (gps.canGetLocation()) {
			LATITUDE = gps.getLatitude();
			LONGITUDE = gps.getLongitude();

		} else {
			LATITUDE = 0;
			LONGITUDE = 0;

		}
		addpic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final CharSequence[] items = { "Take Photo", "From Gallery",
						"Cancel" };
				AlertDialog.Builder builder = new AlertDialog.Builder(
						VehicleProfile.this);
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

		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent next = new Intent(getApplicationContext(),
						Editvehicle.class);
				next.putExtra("id", id);
				startActivity(next);

				finish();

			}
		});
		addins.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent next = new Intent(getApplicationContext(),
						AddInsurance.class);
				String type = info.type.toString();
				next.putExtra("type", type);
				next.putExtra("call", call);
				next.putExtra("id", id);
				startActivity(next);
				finish();

			}
		});

		Delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						VehicleProfile.this);
				builder.setMessage("Are you sure you want to delete ?")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										del = new delete().execute();

									}
								})

						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				builder.show();
			}
		});

	}

	public String getdateformate(String _date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date datef;
		String dateformat = "";
		try {
			datef = sdf.parse(_date);
			sdf.applyPattern("dd-MMMM-yyyy");
			dateformat = sdf.format(datef);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateformat;
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

	private void clickd() {
		final CharSequence[] items = { "Take Photo", "From Gallery", "Cancel" };
		AlertDialog.Builder builder = new AlertDialog.Builder(
				VehicleProfile.this);
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
			if (status.getText().toString().contains("Add Vehicle")) {
				status.setVisibility(View.GONE);
				title.setTextColor(getResources().getColor(R.color.black));
				ImageView s = (ImageView) findViewById(R.id.imageView1);
				s.setVisibility(View.GONE);
			}
			nopic = 2;
		} else if (pic1.getVisibility() == View.VISIBLE
				&& pic2.getVisibility() == View.VISIBLE
				&& pic3.getVisibility() == View.GONE) {
			nopic = 3;
		} else if (pic1.getVisibility() == View.VISIBLE
				&& pic2.getVisibility() == View.VISIBLE
				&& pic3.getVisibility() == View.VISIBLE) {

			nopic = 4;
		} else {
			nopic = 1;
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon @ action bar clicked; go home
			getprofile = new getprofiles().execute();
			break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		// Intent back = new Intent(getApplicationContext(), VehicleInfo.class);
		getprofile = new getprofiles().execute();
		// startActivity(back);
		// finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		case 2:
			if (resultCode == RESULT_OK) {

				imagepath = getImagePath();
				photo = (Bitmap) decodeFile(imagepath);
				// photo = Bitmap.createScaledBitmap(photo, 100, 100, false);
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.PNG, 100, bytes);
				File f = new File(sdRoot, dir + "VehiclePic" + nopic + ".png");
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

			}

			break;
		case 3:
			if (resultCode == RESULT_OK) {

				Uri selectedImageUri = data.getData();
				imagepath = getPath(selectedImageUri);
				photo = BitmapFactory.decodeFile(imagepath);
				photo = (Bitmap) decodeFile(imagepath);
				// photo = Bitmap.createScaledBitmap(photo, 100, 100, false);
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.PNG, 100, bytes);
				File f = new File(sdRoot, dir + "VehiclePic" + nopic + ".png");
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
				// uploadPhoto = new sendd().execute();
			}
			break;
		}

	}

	private void setpic() {
		photo = (Bitmap) decodeFile(imagepath);
		if (nopic == 1) {
			pic1.setVisibility(View.VISIBLE);
			pic1.setImageBitmap(photo);
			upload = new sendd().execute();
			// checkpic();
		} else if (nopic == 2) {
			pic2.setVisibility(View.VISIBLE);
			pic2.setImageBitmap(photo);
			upload = new sendd().execute();
			// checkpic();
		} else if (nopic == 3) {
			pic3.setVisibility(View.VISIBLE);
			pic3.setImageBitmap(photo);
			addpic.setVisibility(View.GONE);
			upload = new sendd().execute();
			// checkpic();
		}

	}

	private class delete extends AsyncTask<Void, Void, Void> {
		String success, mess, response;
		int pts;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(VehicleProfile.this);
			pDialog.setMessage("Deleting ");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(del_url);
			System.out.println(del_url);
			JSONArray jsonMainArr;
			JSONObject json = new JSONObject();
			final SharedPreferences atPrefs;
			atPrefs = PreferenceManager
					.getDefaultSharedPreferences(VehicleProfile.this);

			try {
				info.device();
				info.showInfo(getApplicationContext());
				info.vehicleInfo(getApplicationContext(), id);

				json.put("userId", info.user_id);
				json.put("pin", info.pin);
				json.put("vehicleId", id);
				if (no.length == 0) {
					json.put("photosExist", 0);
				} else {
					json.put("photosExist", 1);
				}

				if (info.iname.isEmpty()) {
					json.put("insuranceDetailsExist", 0);
				} else {
					json.put("insuranceDetailsExist", 1);
				}
				if (info.licenseNo.isEmpty()) {
					json.put("additionalDetailsExist", 0);
				} else {
					json.put("additionalDetailsExist", 1);
				}

				json.put("noVehicles", vehicles.size());
				int newprogress = atPrefs.getInt(SplashscreenActivity.progress,
						0);
				json.put("profileCompleteness", newprogress);
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
				pts = jsonMainArr.getJSONObject(0).getInt(
						"profile_completeness");
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

						SQLiteDatabase dbbb = db.getReadableDatabase();
						dbbb.execSQL("delete from Vehicle_info WHERE Vehicle_id ='"
								+ id + "'");
						dbbb.execSQL("delete from Vehicle_park WHERE vid ='"
								+ id + "'");
						NotificationAlarm.CancelAlarm(getApplicationContext());
						NotificationAlarm.SetAlarm(getApplicationContext());
						deleteDirectory(folder);
						atPrefs.edit()
								.putInt(SplashscreenActivity.progress, pts)
								.commit();
						if (vehicles.size() == 1) {
							Intent next = new Intent(getApplicationContext(),
									ProfileScreen.class);
							startActivity(next);
							finish();
						} else {
							Intent next = new Intent(getApplicationContext(),
									VehicleInfo.class);
							startActivity(next);
							finish();
						}

					}
				});
			}
			// response failure
			else if (success.equals("failure")) {

				runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								VehicleProfile.this).create();
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
								VehicleProfile.this).create();
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

	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null) {
				return true;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	private class sendd extends AsyncTask<Void, Void, Void> {
		String success, mess, response, user;
		Button next;
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(VehicleProfile.this);
			pDialog.setMessage("Updating ");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@SuppressLint("NewApi")
		@Override
		protected Void doInBackground(Void... params) {
			try {
				sendPost(uploadPhoto_url, imagepath);
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

			try {
				JSONObject profile = new JSONObject(names);
				String res = profile.getString("response");
				int pos = res.lastIndexOf("/");
				names = res.substring(pos + 1);
				Log.i("name", names);
			} catch (Exception e) {
			}

			String photoName = "VehiclePic" + nopic + ".png";
			// change image name
			File photo = new File(sdRoot, dir + "VehiclePic" + nopic + ".png");
			while (photo.exists()) {
				photoName = names;
				photo.renameTo(new File(sdRoot, dir + photoName));

			}
			checkpic();

		}

	}

	// sending profile pic
	@SuppressWarnings("deprecation")
	public void sendPost(String url, String imagePath) throws IOException,
			ClientProtocolException {
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost httppost = new HttpPost(url);
		File file = new File(imagePath);

		MultipartEntity mpEntity = new MultipartEntity();
		ContentBody cbFile = new FileBody(file);
		info.device();
		info.showInfo(getApplicationContext());
		mpEntity.addPart("image", cbFile);
		mpEntity.addPart("make", new StringBody(info.manufacturer));
		mpEntity.addPart("os", new StringBody("Android" + " " + info.Version));
		mpEntity.addPart("model", new StringBody(info.model));
		mpEntity.addPart("userId", new StringBody(info.user_id));
		mpEntity.addPart("pin", new StringBody(info.pin));
		mpEntity.addPart("vehicleId", new StringBody(id));
		File[] no = folder.listFiles();
		mpEntity.addPart("noPhotos", new StringBody(String.valueOf(no.length)));
		mpEntity.addPart("noVehicles",
				new StringBody(String.valueOf(vehicles.size())));
		mpEntity.addPart("position", new StringBody(String.valueOf(nopic)));
		mpEntity.addPart("latitude", new StringBody(String.valueOf(LATITUDE)));
		mpEntity.addPart("longitude", new StringBody(String.valueOf(LONGITUDE)));

		httppost.setEntity(mpEntity);
		System.out.println(httppost.getRequestLine());
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity resEntity = response.getEntity();
		System.out.println(response.getStatusLine());

		if (resEntity != null) {
			names = EntityUtils.toString(resEntity);
			System.out.println(names);

		}
		if (resEntity != null) {
			resEntity.consumeContent();
		}

		httpclient.getConnectionManager().shutdown();
	}

	public Bitmap decodeFile(String path) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, o);
			// The new size we want to scale to
			final int REQUIRED_SIZE = 400;

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

	private class getprofiles extends AsyncTask<Void, Void, Void> {
		String success, mess, response;
		String user_id, fName, lName, email, mobileNumber, dob, gender,
				licenseNo, street, suburb, postcode, dtModified, fbId, fbToken,
				cname, cnumber, sques, sans, photourl, photoname, pin, points,
				address;
		int profilecom;

		// vehicle
		int vid;
		String vtype, vmake, vmodel, reg, vstatus;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(VehicleProfile.this);
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
			atPrefs = PreferenceManager
					.getDefaultSharedPreferences(VehicleProfile.this);

			JSONObject json = new JSONObject();
			try {

				info.device();
				info.showInfo(VehicleProfile.this);
				json.put("userId", info.user_id);
				json.put("pin", info.pin);
				json.put("make", info.manufacturer);
				json.put("os", "Android" + " " + info.Version);
				json.put("model", info.model);

				json.put("latitude", LATITUDE);
				json.put("longitude", LONGITUDE);

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
				address = jsonMainArr.getJSONObject(0).getString("address");
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

				runOnUiThread(new Runnable() {

					public void run() {
						db = new DatabaseHandler(VehicleProfile.this);
						PersonalData data = new PersonalData(user_id, fName,
								lName, email, mobileNumber, dob, gender,
								licenseNo, street, suburb, postcode,
								dtModified, fbId, fbToken, cname, cnumber, pin,
								sques, sans, points);

						db.updateprofileData(data);
						atPrefs.edit()
								.putInt(SplashscreenActivity.progress,
										profilecom).commit();
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

								db = new DatabaseHandler(VehicleProfile.this);

								SQLiteDatabase dbbb = db.getReadableDatabase();
								dbbb.execSQL("UPDATE Vehicle_info SET vehicle_type = '"
										+ vtype
										+ "',vehicle_make = '"
										+ vmake
										+ "',vehicle_model = '"
										+ vmodel
										+ "',vehicle_reg = '"
										+ reg
										+ "',vehicle_status = '"
										+ vstatus
										+ "' WHERE vehicle_id= '" + vid + "'");

							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						Intent nextscreen = new Intent(VehicleProfile.this,
								VehicleInfo.class);
						startActivity(nextscreen);
						finish();

					}
				});
			}
			// response failure
			else if (success.equals("failure")) {

				runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								VehicleProfile.this).create();
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
				runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								VehicleProfile.this).create();
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

	@Override
	protected int getLayoutResourceId() {
		return R.layout.vehicle_profile;
	}

	@Override
	protected void onPause() {
		// db.close();
		// dbb.close();
		super.onPause();
	}
}
