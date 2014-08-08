package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class AddDetails extends BaseActivity implements TextWatcher {
	EditText lnumber, address, postcode;
	Connection_Detector cd = new Connection_Detector(this);
	Boolean IsInternetPresent;
	private AsyncTask<Void, Void, Void> details;
	String details_url = Data.url + "profileAddDetails.php";
	String reponse;
	Data info;
	ProgressDialog pDialog;
	boolean baddress, bpostcode;
	DatabaseHandler db;
	SQLiteDatabase dbb;
	SharedPreferences atPrefs;
	GPSTracker gps;
	int p;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.add_details);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'> Add Details </font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.app_icon);
		atPrefs = PreferenceManager
				.getDefaultSharedPreferences(AddDetails.this);
		lnumber = (EditText) findViewById(R.id.Licence);
		address = (EditText) findViewById(R.id.address);
		postcode = (EditText) findViewById(R.id.postcode);
		lnumber.addTextChangedListener(this);
		address.addTextChangedListener(this);
		postcode.addTextChangedListener(this);
		info = new Data();
		db = new DatabaseHandler(AddDetails.this);
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();

	}

	private class detail extends AsyncTask<Void, Void, Void> {
		String success, mess, response, id;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AddDetails.this);
			pDialog.setMessage("Adding Details...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@SuppressWarnings("deprecation")
		@Override
		protected Void doInBackground(Void... params) {

			JSONArray jsonMainArr;
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(
					CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

			HttpPost httppost = new HttpPost(details_url);
			@SuppressWarnings("deprecation")
			MultipartEntity mpEntity = null;
			mpEntity = new MultipartEntity();
			try {
				gps = new GPSTracker(AddDetails.this);
				info.device();
				info.showInfo(getApplicationContext());

				mpEntity.addPart("pin", new StringBody(info.pin));
				mpEntity.addPart("licenceNo", new StringBody(lnumber.getText()
						.toString()));

				mpEntity.addPart("address", new StringBody(address.getText()
						.toString()));
				mpEntity.addPart("pincode", new StringBody(postcode.getText()
						.toString()));

				if (gps.canGetLocation()) {
					double LATITUDE = gps.getLatitude();
					double LONGITUDE = gps.getLongitude();
					mpEntity.addPart("latitude",
							new StringBody(String.valueOf(LATITUDE)));
					mpEntity.addPart("longitude",
							new StringBody(String.valueOf(LONGITUDE)));

				} else {
					mpEntity.addPart("latitude",
							new StringBody(String.valueOf(0)));
					mpEntity.addPart("longitude",
							new StringBody(String.valueOf(0)));
				}

				mpEntity.addPart("os", new StringBody(info.manufacturer));
				mpEntity.addPart("make", new StringBody("Android" + " "
						+ info.Version));
				mpEntity.addPart("model", new StringBody(info.model));
				mpEntity.addPart("userId", new StringBody(info.user_id));

			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			httppost.setEntity(mpEntity);
			System.out.println(httppost.getRequestLine());
			HttpResponse response = null;
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			HttpEntity resEntity = response.getEntity();
			System.out.println(response.getStatusLine());
			if (resEntity != null) {
				try {
					reponse = EntityUtils.toString(resEntity);
					System.out.println(reponse);
					JSONObject profile = new JSONObject(reponse);
					jsonMainArr = profile.getJSONArray("response");
					success = profile.getString("status");
					mess = profile.getString("message");
					p = jsonMainArr.getJSONObject(0)
							.getInt("profile_completed");
				} catch (JSONException e) {
					System.out.println("JSONException");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (success.equals("success")) {

					runOnUiThread(new Runnable() {

						public void run() {
							SQLiteDatabase dbbb = db.getReadableDatabase();
							dbbb.execSQL("UPDATE profile SET licenseNo = '"
									+ lnumber.getText().toString()
									+ "',street = '"
									+ address.getText().toString()
									+ "',postcode = '"
									+ postcode.getText().toString() + "'");
							atPrefs.edit()
									.putInt(SplashscreenActivity.progress, p)
									.commit();
							Intent next = new Intent(getApplicationContext(),
									ProfileScreen.class);
							startActivity(next);
							finish();
						}
					});
				}
				// response failure
				else if (success.equals("failure")) {

					runOnUiThread(new Runnable() {

						public void run() {
							final AlertDialog Dialog = new AlertDialog.Builder(
									AddDetails.this).create();
							Dialog.setTitle("Error");
							Dialog.setIcon(R.drawable.ic_action_error);
							Dialog.setMessage(mess);
							Dialog.setButton(DialogInterface.BUTTON_NEUTRAL,
									"OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
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
									AddDetails.this).create();
							Dialog.setTitle("Error");
							Dialog.setIcon(R.drawable.ic_action_error);
							Dialog.setMessage(mess);
							Dialog.setButton(DialogInterface.BUTTON_NEUTRAL,
									"OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
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
			return null;
		}

		@Override
		protected void onPostExecute(Void notUsed) {
			pDialog.dismiss();

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.add_details, menu);
		MenuItem add = menu.findItem(R.id.Save);
		add.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {

				IsInternetPresent = cd.isConnectingToInternet();
				if (IsInternetPresent == false) {
					cd.showNoInternetPopup();
				} else {
					// if (lnumber.getText().toString().length() < 6) {
					// lnumber.setHintTextColor(getResources().getColor(
					// R.color.red));
					// lnumber.setTextColor(getResources().getColor(
					// R.color.red));
					// blnumber = false;
					//
					// }
					if (address.getText().toString().length() < 6) {
						address.setHintTextColor(getResources().getColor(
								R.color.red));
						address.setTextColor(getResources().getColor(
								R.color.red));
						baddress = false;
					}
					if (postcode.getText().toString().length() < 4) {
						postcode.setHintTextColor(getResources().getColor(
								R.color.red));
						postcode.setTextColor(getResources().getColor(
								R.color.red));
						bpostcode = false;
					}

					if (baddress == true && bpostcode == true) {
						details = new detail().execute();
					}

				}

				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void afterTextChanged(Editable arg0) {

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// lnumber.setTextColor(getResources().getColor(R.color.black));
		address.setTextColor(getResources().getColor(R.color.black));
		postcode.setTextColor(getResources().getColor(R.color.black));

		// blnumber = true;
		baddress = true;
		bpostcode = true;

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon @ action bar clicked; go home
			Intent back = new Intent(getApplicationContext(),
					ProfileScreen.class);
			startActivity(back);
			finish();
			break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		Intent back = new Intent(getApplicationContext(), ProfileScreen.class);
		startActivity(back);
		finish();
		super.onBackPressed();
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.add_details;
	}
}
