package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
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
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;

public class ProfileScreen extends BaseActivity {
	TextView name, age, number, email, profile_comp, pts, samaritan, status;
	ImageView edit, gender, bg;
	Button adddetails, addvehicle;
	CircularImageView profilepic;
	RelativeLayout pointsrel, relbg;
	Data info;
	File sdRoot;
	String dir, imgPath;
	Connection_Detector cd = new Connection_Detector(this);
	private String imagepath = null;
	ProgressBar progress;
	int oldprogress = 0;
	static int newprogress;
	SharedPreferences atPrefs;
	private String uploadPhoto_url = Data.url + "uploadProfilePic.php";
	private AsyncTask<Void, Void, Void> uploadPhoto;
	String names;
	List<VehicleData> vehicles;
	SQLiteDatabase dbb;
	DatabaseHandler db;
	LinearLayout v;
	int count = 0;
	ArrayList<String> f = new ArrayList<String>();// list of file paths
	File[] listFile;
	int height;
	double LATITUDE, LONGITUDE;
	GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.profilescreen);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'> My Profile </font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.app_icon);
		atPrefs = PreferenceManager
				.getDefaultSharedPreferences(ProfileScreen.this);
		db = new DatabaseHandler(getApplicationContext());
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();
		bg = (ImageView) findViewById(R.id.imageView1);
		name = (TextView) findViewById(R.id.name);
		age = (TextView) findViewById(R.id.age);
		number = (TextView) findViewById(R.id.number);
		email = (TextView) findViewById(R.id.email);
		pts = (TextView) findViewById(R.id.pts);
		status = (TextView) findViewById(R.id.textView1);
		profile_comp = (TextView) findViewById(R.id.profile_comp);
		profilepic = (CircularImageView) findViewById(R.id.profile);
		// edit = (ImageView) findViewById(R.id.edit);
		gender = (ImageView) findViewById(R.id.male_female);
		adddetails = (Button) findViewById(R.id.adddetails);
		addvehicle = (Button) findViewById(R.id.addvehicle);
		pointsrel = (RelativeLayout) findViewById(R.id.points);
		progress = (ProgressBar) findViewById(R.id.progress);
		samaritan = (TextView) findViewById(R.id.textView2);
		relbg = (RelativeLayout) findViewById(R.id.relbg);
		height = getWindowManager().getDefaultDisplay().getHeight();
		int image_h = height * 30 / 100;
		RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, image_h);
		relbg.setLayoutParams(parms);
		info = new Data();
		info.showInfo(getApplicationContext());
		name.setText(info.fName + " " + info.lName);
		number.setText(info.mobileNumber);
		email.setText(info.email);
		pts.setText(info.spoints);
		// if (info.year == 0 || info.month == 0 || info.date == 0) {
		// age.setVisibility(View.GONE);
		// } else {
		// int AGe = info.getAge(info.year, info.month, info.date);
		// age.setText("," + AGe + " " + "Yrs");
		// }

		if (info.gender.equalsIgnoreCase("male")) {
			gender.setImageResource(R.drawable.ic_male);
		} else {
			gender.setImageResource(R.drawable.ic_female);
		}
		if (!info.street.isEmpty()) {
			adddetails.setText("Edit Details");

			// newprogress = 50;
			// atPrefs.edit()
			// .putInt(SplashscreenActivity.progress,
			// ProfileScreen.newprogress).commit();

		} else {
			adddetails.setVisibility(View.VISIBLE);
			adddetails.setText("Add Details");

			// newprogress = 30;
			// atPrefs.edit()
			// .putInt(SplashscreenActivity.progress,
			// ProfileScreen.newprogress).commit();
		}
		sdRoot = Environment.getExternalStorageDirectory();
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
		setimage();
		// refresh ui
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {

				Log.i("call", "call");
				setimage();
			}
		}, 3000);

		// change points bg color
		if (!pts.getText().toString().equalsIgnoreCase("0")) {
			pointsrel.setBackgroundColor(getResources().getColor(R.color.blue));
			pts.setTextColor(getResources().getColor(R.color.white));
			samaritan.setTextColor(getResources().getColor(R.color.white));
		} else {
			pointsrel.setBackgroundColor(getResources().getColor(
					R.color.gry_profile));
			pts.setTextColor(getResources().getColor(R.color.black));
			samaritan.setTextColor(getResources().getColor(R.color.black));

		}
		vehicles = db.getVehicleData();
		if (!(vehicles.size() == 0)) {
			addvehicle.setText("My Vehicles");
		}

		gps = new GPSTracker(ProfileScreen.this);
		if (gps.canGetLocation()) {
			LATITUDE = gps.getLatitude();
			LONGITUDE = gps.getLongitude();
		} else {
			LATITUDE = 0.0;
			LONGITUDE = 0.0;
		}
		addvehicle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (vehicles.size() == 0) {
					Intent next = new Intent(getApplicationContext(),
							Addvehicle.class);
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
		samaritan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final AlertDialog Dialog = new AlertDialog.Builder(
						ProfileScreen.this).create();
				Dialog.setTitle("Samaritan Points");
				Dialog.setMessage("Earn Samaritan points against your activities like Report Sighting and Parking Feedback in order to gain priority listing and other benefits");
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
		adddetails.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (adddetails.getText().toString()
						.equalsIgnoreCase("Edit Details")) {
					Intent next = new Intent(getApplicationContext(),
							EditInfo.class);
					startActivity(next);
				} else {
					Intent next = new Intent(getApplicationContext(),
							AddDetails.class);
					startActivity(next);
				}

			}
		});

		// get all images from folder
		getFromSdcard();

		// new Handler().postDelayed(new Runnable() {
		// public void run() {
		//
		// if (count < f.size()) {
		//
		// Drawable d = (Drawable) Drawable.createFromPath(f
		// .get(count));
		//
		// bg.setImageDrawable(d);
		//
		// count++; // <<< increment counter here
		// } else {
		// // reset counter here
		// count = 0;
		// }
		//
		// }
		// }, 1000);

		// change images

		// if (f.size() == 0) {
		height = getWindowManager().getDefaultDisplay().getHeight();
		int image_hs = height * 30 / 100;
		RelativeLayout.LayoutParams parmss = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, image_hs);
		bg.setLayoutParams(parmss);
		bg.setImageResource(R.drawable.default_profile_bg);
		bg.setScaleType(ScaleType.FIT_XY);

		// } else {
		//
		// final Handler handlers = new Handler();
		// Runnable runnable = new Runnable() {
		//
		// public void run() {
		// Drawable d = (Drawable) Drawable.createFromPath(f
		// .get(count));
		// // int image_hs = height * 30 / 100;
		// // RelativeLayout.LayoutParams parmss = new
		// // RelativeLayout.LayoutParams(
		// // LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// // bg.setLayoutParams(parmss);
		// bg.setImageDrawable(d);
		// count++;
		//
		// if (count > f.size() - 1) {
		//
		// count = 0;
		// }
		//
		// handlers.postDelayed(this, 3000); // for interval...
		// }
		// };
		// handlers.post(runnable);
		// }

		profilepic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final CharSequence[] items = { "Take Photo", "From Gallery",
						"Cancel" };
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ProfileScreen.this);
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
		newprogress = atPrefs.getInt(SplashscreenActivity.progress, 0);

		if (newprogress <= 30) {
			progress.setProgressDrawable(getResources().getDrawable(
					R.drawable.startbar));
			ProgressBarAnimation anim = new ProgressBarAnimation(progress, 0,
					newprogress);
			profile_comp.setText("Your Profile is 30% complete");
			status.setText("Complete your profile and add vehicles");
			anim.setDuration(1000);
			progress.setAnimation(anim);
		} else if (newprogress <= 50) {
			progress.setProgressDrawable(getResources().getDrawable(
					R.drawable.middlebar));
			status.setText("Add vehicles to your profile");
			profile_comp.setText("Your profile is " + newprogress + "%"
					+ " complete");
			ProgressBarAnimation anim = new ProgressBarAnimation(progress, 0,
					newprogress);
			anim.setDuration(1000);
			progress.setAnimation(anim);
		} else if (newprogress == 60) {
			progress.setProgressDrawable(getResources().getDrawable(
					R.drawable.middlebar));
			status.setText("Add details and Add photo/insurance of vehicle");
			profile_comp.setText("Your profile is " + newprogress + "%"
					+ " complete");
			ProgressBarAnimation anim = new ProgressBarAnimation(progress, 0,
					newprogress);
			anim.setDuration(1000);
			progress.setAnimation(anim);
		} else if (newprogress <= 80) {
			progress.setProgressDrawable(getResources().getDrawable(
					R.drawable.fourbar));
			if (info.street.isEmpty()) {
				status.setText("Add details to your profile");
			} else {
				status.setText("Add photo/insurance of vehicle");
			}

			profile_comp.setText("Your profile is " + newprogress + "%"
					+ " complete");
			ProgressBarAnimation anim = new ProgressBarAnimation(progress, 0,
					newprogress);
			anim.setDuration(1000);
			progress.setAnimation(anim);
		} else if (newprogress == 90) {
			progress.setProgressDrawable(getResources().getDrawable(
					R.drawable.fourbar));
			status.setText("Add photo/insurance of vehicle");
			profile_comp.setText("Your profile is " + newprogress + "%"
					+ " complete");
			ProgressBarAnimation anim = new ProgressBarAnimation(progress, 0,
					newprogress);
			anim.setDuration(1000);
			progress.setAnimation(anim);
		} else {
			progress.setProgressDrawable(getResources().getDrawable(
					R.drawable.endbar));
			ProgressBarAnimation anim = new ProgressBarAnimation(progress, 0,
					newprogress);
			anim.setDuration(1000);
			progress.setAnimation(anim);
		}
		if (newprogress >= 100) {
			newprogress = 100;
			profile_comp.setText("Your profile is complete");
			// progress.setVisibility(View.GONE);
			status.setVisibility(View.GONE);
			// profile_comp.setVisibility(View.GONE);

		}
		// if (newprogress > 50) {
		// if (info.licenseNo.isEmpty()) {
		// profile_comp.setText("Your profile is  " + newprogress + " % "
		// + "Complete");
		// status.setText("Complete your profile and add information about your vehicle now");
		//
		// } else {
		// profile_comp.setText("Your profile is  " + newprogress + " % "
		// + "Complete");
		// status.setText("Add information about your vehicle now");
		// }
		//
		// }

	}

	public void getFromSdcard() {
		File file = null;
		for (int i = 0; i < vehicles.size(); i++) {
			file = new File(sdRoot + "/My Wheel/"
					+ vehicles.get(i).getvehicle_id());
			if (file.isDirectory()) {
				listFile = file.listFiles();
				for (int j = 0; j < listFile.length; j++) {

					f.add(listFile[j].getAbsolutePath());

				}
			}
		}

	}

	private void setimage() {
		names = atPrefs.getString(SplashscreenActivity.profile_pic,
				"profilePic.png");
		File f = new File(sdRoot, dir + names);

		if (names.isEmpty()) {
			profilepic.setImageResource(R.drawable.add_photo_profile);
		} else {

			if (f.exists()) {
				imagepath = f.getAbsolutePath();
				Bitmap photo = (Bitmap) decodeFile(imagepath);
				profilepic.setImageBitmap(photo);
			} else {
				profilepic.setImageResource(R.drawable.add_photo_profile);
			}
		}
	}

	public class ProgressBarAnimation extends Animation {
		private ProgressBar progressBar;
		private float from;
		private float to;

		public ProgressBarAnimation(ProgressBar progressBar, float from,
				float to) {
			super();
			this.progressBar = progressBar;
			this.from = from;
			this.to = to;
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			float value = from + (to - from) * interpolatedTime;
			progressBar.setProgress((int) value);
		}

	}

	@Override
	protected void onResume() {
		// Intent n = new Intent(getApplicationContext(), PinLock.class);
		// startActivity(n);
		db.close();
		super.onResume();

	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		case 2:
			if (resultCode == RESULT_OK) {

				imagepath = getImagePath();
				Bitmap photo = (Bitmap) decodeFile(imagepath);
				int rotate = 0;
				try {
					File imageFile = new File(imagepath);
					ExifInterface exif = new ExifInterface(
							imageFile.getAbsolutePath());
					int orientation = exif.getAttributeInt(
							ExifInterface.TAG_ORIENTATION,
							ExifInterface.ORIENTATION_NORMAL);

					switch (orientation) {
					case ExifInterface.ORIENTATION_ROTATE_270:
						rotate = 270;
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						rotate = 180;
						break;
					case ExifInterface.ORIENTATION_ROTATE_90:
						rotate = 90;
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Matrix matrix = new Matrix();
				matrix.postRotate(rotate);
				photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(),
						photo.getHeight(), matrix, true);
				photo = Bitmap.createScaledBitmap(photo, 200, 200, false);
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.PNG, 100, bytes);
				File f = new File(sdRoot, dir + "profile.png");
				try {

					f.createNewFile();
					FileOutputStream fo = new FileOutputStream(f);
					fo.write(bytes.toByteArray());
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				imagepath = f.getAbsolutePath();
				photo = (Bitmap) decodeFile(imagepath);
				profilepic.setImageBitmap(photo);
				uploadPhoto = new sendd().execute();
			}

			break;
		case 3:
			if (resultCode == RESULT_OK) {

				Uri selectedImageUri = data.getData();
				imagepath = getPath(selectedImageUri);
				// Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
				Bitmap photo = (Bitmap) decodeFile(imagepath);
				photo = Bitmap.createScaledBitmap(photo, 200, 200, false);
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.PNG, 100, bytes);
				File f = new File(sdRoot, dir + "profile.png");
				try {

					f.createNewFile();
					FileOutputStream fo = new FileOutputStream(f);
					fo.write(bytes.toByteArray());
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				imagepath = f.getAbsolutePath();
				photo = (Bitmap) decodeFile(imagepath);
				profilepic.setImageBitmap(photo);
				uploadPhoto = new sendd().execute();
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

	public Uri setImageUri() {
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "profile.jpg");
		// File file = new File(sdRoot, dir + "profilePic.jpg");
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

	private class sendd extends AsyncTask<Void, Void, Void> {
		String success, mess, response, user;
		Button next;
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ProfileScreen.this);
			pDialog.setMessage("Updating...");
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
				success = profile.getString("status");
				// mess = profile.getString("message");
				if (success.equalsIgnoreCase("success")) {
					String res = profile.getString("response");
					int pos = res.lastIndexOf("/");
					names = res.substring(pos + 1);
					Log.i("name", names);
					String photoName = "profile.png";
					// change image name
					File photo = new File(sdRoot, dir + "profile.png");
					while (photo.exists()) {
						photoName = names;
						photo.renameTo(new File(sdRoot, dir + photoName));
						atPrefs.edit()
								.putString(SplashscreenActivity.profile_pic,
										photoName).commit();
					}
				} else if (!success.equalsIgnoreCase("success")) {
					runOnUiThread(new Runnable() {

						public void run() {
							final AlertDialog Dialog = new AlertDialog.Builder(
									ProfileScreen.this).create();
							Dialog.setTitle(success);
							// Dialog.setMessage(mess);
							Dialog.setButton(DialogInterface.BUTTON_NEUTRAL,
									"OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									});
							Dialog.setCancelable(true);
							Dialog.show();
						}
					});
				}

			} catch (Exception e) {
			}

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

		mpEntity.addPart("latitude", new StringBody(String.valueOf(LATITUDE)));
		mpEntity.addPart("longitude", new StringBody(String.valueOf(LONGITUDE)));

		httppost.setEntity(mpEntity);
		System.out.println(httppost.getRequestLine());
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (Exception e) {
			runOnUiThread(new Runnable() {

				public void run() {
					System.out.println("net");
					cd.showNoInternetPopup();
				}
			});
			return;
		}

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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon @ action bar clicked; go home
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
	protected int getLayoutResourceId() {
		return R.layout.profilescreen;
	}
}
