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

import com.facebook.Session;
import com.urbanairship.push.PushManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PinLock extends Activity implements TextWatcher, OnKeyListener {
	Button check;
	TextView enterpin, forgot, Sign;
	EditText pin1, pin2, pin3, pin4;
	boolean bpin1, bpin2, bpin3, bpin4;
	Connection_Detector cd = new Connection_Detector(this);
	Boolean IsInternetPresent;
	private AsyncTask<Void, Void, Void> checkpass;
	String pass_url = Data.url + "verifyPin.php";
	ProgressDialog pDialog;
	Data info;
	SharedPreferences atPrefs;
	DatabaseHandler db;
	SQLiteDatabase dbb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pin_lock);
		db = new DatabaseHandler(getApplicationContext());
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();
		check = (Button) findViewById(R.id.button1);
		enterpin = (TextView) findViewById(R.id.enter_pin);
		forgot = (TextView) findViewById(R.id.forgot);
		pin1 = (EditText) findViewById(R.id.pin1);
		pin2 = (EditText) findViewById(R.id.pin2);
		pin3 = (EditText) findViewById(R.id.pin3);
		pin4 = (EditText) findViewById(R.id.pin4);
		Sign = (TextView) findViewById(R.id.Sign);
		pin1.addTextChangedListener(this);
		pin2.addTextChangedListener(this);
		pin3.addTextChangedListener(this);
		pin4.addTextChangedListener(this);
		pin1.setOnKeyListener(this);
		pin2.setOnKeyListener(this);
		pin3.setOnKeyListener(this);
		pin4.setOnKeyListener(this);
		info = new Data();
		atPrefs = PreferenceManager.getDefaultSharedPreferences(PinLock.this);
		Sign.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				logout();
			}
		});
		forgot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				logout();
			}

		});

		check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				IsInternetPresent = cd.isConnectingToInternet();
				if (IsInternetPresent == false) {
					cd.showNoInternetPopup();
				} else {

					if (pin1.getText().toString().isEmpty()
							|| pin2.getText().toString().isEmpty()
							|| pin3.getText().toString().isEmpty()
							|| pin4.getText().toString().isEmpty()) {
						enterpin.setTextColor(getResources().getColor(
								R.color.red));
						bpin1 = false;
						bpin2 = false;
						bpin3 = false;
						bpin4 = false;

					}

					if (bpin1 == true && bpin2 == true && bpin3 == true
							&& bpin4 == true) {

						checkpass = new checkp().execute();

					}

				}
			}
		});

	}

	private void logout() {
		PushManager.disablePush();
		SplashscreenActivity.fblogin = true;
		atPrefs.edit().putBoolean(info.checkllogin, true).commit();
		atPrefs.edit().putString(info.glatitude, String.valueOf("not"))
				.commit();
		atPrefs.edit().putString(info.glongitude, String.valueOf("not"))

		.commit();
		atPrefs.edit()
				.putString(SplashscreenActivity.profile_pic, "profilePic.png")
				.commit();
		Session session = Session.getActiveSession();
		if (session != null) {

			if (!session.isClosed()) {
				session.closeAndClearTokenInformation();
				// clear your preferences if saved
			}
		} else {

			session = new Session(PinLock.this);
			Session.setActiveSession(session);

			session.closeAndClearTokenInformation();
			// clear your preferences if saved

		}
		dbb.execSQL("delete from Vehicle_info");
		dbb.execSQL("delete from Vehicle_park");
		dbb.execSQL("UPDATE profile SET user_id ='',fName='',lName='',email='',mobileNumber='',dob='',gender='',licenseNo='',street='',address='',postcode='',dtModified='',fbId='',fbToken='',contact_name='',contact_number='',pin='',squs='',sans='',spoints=''");

		Intent next = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(next);
	}

	private class checkp extends AsyncTask<Void, Void, Void> {
		String success, mess, response, id, pin, qus;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(PinLock.this);
			pDialog.setMessage("Verifying Pin");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(pass_url);
			System.out.println(pass_url);
			JSONArray jsonMainArr;
			JSONObject json = new JSONObject();
			try {
				info.device();
				info.showInfo(getApplicationContext());
				json.put("userId", info.user_id);
				pin = pin1.getText().toString() + pin2.getText().toString()
						+ pin3.getText().toString() + pin4.getText().toString();
				json.put("pin", pin);
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
						atPrefs.edit().putString(BaseActivity.check, "true")
								.commit();
						finish();

					}
				});
			}
			// response failure
			else if (success.equals("failure")) {

				runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								PinLock.this).create();
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
								PinLock.this).create();
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
	public void afterTextChanged(Editable s) {
		// go next
		if (pin1.hasFocus() && pin1.getText().toString().length() == 1) {
			pin2.requestFocus();

		}
		if (pin2.hasFocus() && pin2.getText().toString().length() == 1) {
			pin3.requestFocus();
		}
		if (pin3.hasFocus() && pin3.getText().toString().length() == 1) {
			pin4.requestFocus();
		}

		if (!(pin1.getText().toString().isEmpty()
				|| pin2.getText().toString().isEmpty()
				|| pin3.getText().toString().isEmpty() || pin4.getText()
				.toString().isEmpty())) {
			enterpin.setTextColor(getResources().getColor(R.color.white));
			bpin1 = true;
			bpin2 = true;
			bpin3 = true;
			bpin4 = true;

		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// go back

		if (keyCode == KeyEvent.KEYCODE_DEL) {
			if (pin4.hasFocus()) {
				pin4.setText("");
				pin3.requestFocus();
			} else if (pin3.hasFocus()) {
				pin3.setText("");
				pin2.requestFocus();
			} else if (pin2.hasFocus()) {
				pin2.setText("");
				pin1.requestFocus();

			}

		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.pin_lock, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		super.onStop();
		moveTaskToBack(true);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
