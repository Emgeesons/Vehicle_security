package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;

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
	Data info;
	ProgressDialog pDialog;
	boolean blnumber, baddress, bpostcode;
	DatabaseHandler db;
	SQLiteDatabase dbb;
	SharedPreferences atPrefs;

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
			pDialog.setMessage("Adding Details");
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
				info.showInfo(getApplicationContext());
				json.put("userId", info.user_id);
				json.put("licenceNo", lnumber.getText().toString());
				json.put("address", address.getText().toString());
				json.put("pincode", postcode.getText().toString());
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
						dbbb.execSQL("UPDATE profile SET licenseNo = '"
								+ lnumber.getText().toString()
								+ "',address = '"
								+ address.getText().toString()
								+ "',postcode = '"
								+ postcode.getText().toString() + "'");
						ProfileScreen.newprogress = 50;
						atPrefs.edit()
								.putInt(SplashscreenActivity.progress,
										ProfileScreen.newprogress).commit();
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
								AddDetails.this).create();
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
					if (lnumber.getText().toString().length() < 6) {
						lnumber.setHintTextColor(getResources().getColor(
								R.color.red));
						lnumber.setTextColor(getResources().getColor(
								R.color.red));
						blnumber = false;

					}
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

					if (blnumber == true && baddress == true
							&& bpostcode == true) {
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
		lnumber.setTextColor(getResources().getColor(R.color.black));
		address.setTextColor(getResources().getColor(R.color.black));
		postcode.setTextColor(getResources().getColor(R.color.black));

		blnumber = true;
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
		// TODO Auto-generated method stub
		Intent back = new Intent(getApplicationContext(), ProfileScreen.class);
		startActivity(back);
		finish();
		super.onBackPressed();
	}

	@Override
	protected int getLayoutResourceId() {
		// TODO Auto-generated method stub
		return R.layout.add_details;
	}
}
