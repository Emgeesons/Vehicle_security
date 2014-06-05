package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class ProfileScreen extends SherlockActivity {
	TextView name, age, number, email, profile_comp, pts, samaritan;
	ImageView edit, gender;
	Button adddetails, addvehicle;
	CustomImageView profilepic;
	RelativeLayout pointsrel;
	Data info;
	File sdRoot;
	String dir, imgPath;
	private String imagepath = null;
	ProgressBar progress;
	int oldprogress = 0;
	static int newprogress;
	SharedPreferences atPrefs;
	private String uploadPhoto_url = Data.url + "uploadProfilePic.php";
	private AsyncTask<Void, Void, Void> uploadPhoto;
	String names;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profilescreen);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'> My Profile </font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.ic_app);
		atPrefs = PreferenceManager
				.getDefaultSharedPreferences(ProfileScreen.this);
		name = (TextView) findViewById(R.id.name);
		age = (TextView) findViewById(R.id.age);
		number = (TextView) findViewById(R.id.number);
		email = (TextView) findViewById(R.id.email);
		pts = (TextView) findViewById(R.id.pts);
		profile_comp = (TextView) findViewById(R.id.profile_comp);
		profilepic = (CustomImageView) findViewById(R.id.profile);
		edit = (ImageView) findViewById(R.id.edit);
		gender = (ImageView) findViewById(R.id.male_female);
		adddetails = (Button) findViewById(R.id.adddetails);
		addvehicle = (Button) findViewById(R.id.addvehicle);
		pointsrel = (RelativeLayout) findViewById(R.id.points);
		progress = (ProgressBar) findViewById(R.id.progress);
		samaritan = (TextView) findViewById(R.id.textView2);
		info = new Data();
		info.showInfo(getApplicationContext());
		name.setText(info.fName + "" + info.lName);
		number.setText(info.mobileNumber);
		email.setText(info.email);
		if (info.year == 0 || info.month == 0 || info.date == 0) {
			age.setVisibility(View.GONE);
		} else {
			int AGe = info.getAge(info.year, info.month, info.date);
			age.setText("," + AGe + " " + "Yrs");
		}

		if (info.gender.equalsIgnoreCase("male")) {
			gender.setImageResource(R.drawable.ic_male);
		} else {
			gender.setImageResource(R.drawable.ic_female);
		}
		if (!info.licenseNo.isEmpty()) {
			adddetails.setVisibility(View.GONE);
			// newprogress = 50;
			// atPrefs.edit()
			// .putInt(SplashscreenActivity.progress,
			// ProfileScreen.newprogress).commit();

		} else {
			adddetails.setVisibility(View.VISIBLE);
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
		names = atPrefs.getString(SplashscreenActivity.profile_pic,
				"profilePic.png");
		File f = new File(sdRoot, dir + names);
		if (f.exists()) {
			imagepath = f.getAbsolutePath();
			Bitmap photo = (Bitmap) decodeFile(imagepath);
			profilepic.setImageBitmap(photo);
		} else {
			profilepic.setImageResource(R.drawable.default_profile);
		}

		// change points bg color
		if (!pts.getText().toString().equalsIgnoreCase("0")) {
			pointsrel.setBackgroundColor(getResources().getColor(R.color.blue));
			pts.setTextColor(getResources().getColor(R.color.white));
			samaritan.setTextColor(getResources().getColor(R.color.white));
		} else {
			pointsrel.setBackgroundColor(getResources().getColor(R.color.gry));
			pts.setTextColor(getResources().getColor(R.color.gry));
			samaritan.setTextColor(getResources().getColor(R.color.gry));

		}
		samaritan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final AlertDialog Dialog = new AlertDialog.Builder(
						ProfileScreen.this).create();
				Dialog.setTitle("Samaritan Points");
				Dialog.setMessage("Earn Samaritan Points against your activites like Report Sighting and Parking Feedback to gain priority listing when you loose your vehicle");
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
				Intent next = new Intent(getApplicationContext(),
						AddDetails.class);
				startActivity(next);

			}
		});
		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent next = new Intent(getApplicationContext(),
						EditInfo.class);
				startActivity(next);

			}
		});

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
		newprogress = atPrefs.getInt(SplashscreenActivity.progress, 0);

		if (newprogress <= 30) {
			progress.setProgressDrawable(getResources().getDrawable(
					R.drawable.startbar));
			ProgressBarAnimation anim = new ProgressBarAnimation(progress, 0,
					newprogress);
			anim.setDuration(1000);
			progress.setAnimation(anim);
		} else if (newprogress <= 50) {
			progress.setProgressDrawable(getResources().getDrawable(
					R.drawable.middlebar));
			profile_comp.setText("Your profile is 50% Complete");
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		case 2:
			if (resultCode == RESULT_OK) {

				imagepath = getImagePath();
				Bitmap photo = (Bitmap) decodeFile(imagepath);
				photo = Bitmap.createScaledBitmap(photo, 100, 100, false);
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.PNG, 100, bytes);
				File f = new File(sdRoot, dir + "profilePic.png");
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
				photo = Bitmap.createScaledBitmap(photo, 100, 100, false);
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.PNG, 100, bytes);
				File f = new File(sdRoot, dir + "profilePic.png");
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

			String photoName = "profilePic.png";
			// change image name
			File photo = new File(sdRoot, dir + "profilePic.png");
			while (photo.exists()) {
				photoName = names;
				photo.renameTo(new File(sdRoot, dir + photoName));
				atPrefs.edit()
						.putString(SplashscreenActivity.profile_pic, photoName)
						.commit();
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
}
