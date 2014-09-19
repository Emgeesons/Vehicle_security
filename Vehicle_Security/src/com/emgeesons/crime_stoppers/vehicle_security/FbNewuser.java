package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;

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
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Session;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.Logger;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

public class FbNewuser extends SherlockActivity implements TextWatcher,
		OnKeyListener {
	String email, userid, oldpin;
	Button submit;
	CheckBox showpin;
	Connection_Detector cd = new Connection_Detector(this);
	Boolean IsInternetPresent;
	EditText number, pin1, pin2, pin3, pin4, otherqus, answer;
	TextView pint, qus;
	static int buffKey = 0;
	static CharSequence[] secqus;
	int tqus = -1;
	Data info;
	boolean bnumber, bpin1, bpin2, bpin3, bpin4, bans, bqus;
	private AsyncTask<Void, Void, Void> regcheck;
	ProgressDialog pDialog;
	String fbregister_url = Data.url + "fbCompleteRegistration.php";
	SharedPreferences atPrefs;
	DatabaseHandler db;
	SQLiteDatabase dbb;
	String pin;
	// int qusvalue;
	String fname, lname, reponse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fb_newuser);
		getSupportActionBar()
				.setTitle(
						Html.fromHtml("<font color='#FFFFFF'>Login With Facebook</font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.app_icon);
		Intent intent = getIntent();
		email = intent.getStringExtra("email");
		userid = intent.getStringExtra("userid");
		oldpin = intent.getStringExtra("oldpin");
		Log.i("data", email + userid + oldpin);
		atPrefs = PreferenceManager.getDefaultSharedPreferences(FbNewuser.this);
		info = new Data();

		number = (EditText) findViewById(R.id.number);
		pin1 = (EditText) findViewById(R.id.pin1);
		pin2 = (EditText) findViewById(R.id.pin2);
		pin3 = (EditText) findViewById(R.id.pin3);
		pin4 = (EditText) findViewById(R.id.pin4);
		showpin = (CheckBox) findViewById(R.id.pin);
		pint = (TextView) findViewById(R.id.textView1);
		submit = (Button) findViewById(R.id.submit);
		qus = (TextView) findViewById(R.id.sec_qus);
		otherqus = (EditText) findViewById(R.id.sec_other_qus);
		answer = (EditText) findViewById(R.id.sec_ans);
		number.addTextChangedListener(this);
		pin1.addTextChangedListener(this);
		pin2.addTextChangedListener(this);
		pin3.addTextChangedListener(this);
		pin4.addTextChangedListener(this);
		pin1.setOnKeyListener(this);
		pin2.setOnKeyListener(this);
		pin3.setOnKeyListener(this);
		pin4.setOnKeyListener(this);
		secqus = getResources().getStringArray(R.array.sec_qus);

		showpin.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// checkbox status is changed from uncheck to checked.
				if (!isChecked) {
					// show password
					pin1.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
					pin2.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
					pin3.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
					pin4.setTransformationMethod(PasswordTransformationMethod
							.getInstance());

				} else {
					// hide password
					pin1.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
					pin2.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
					pin3.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
					pin4.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());

				}
			}
		});

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				IsInternetPresent = cd.isConnectingToInternet();
				if (IsInternetPresent == false) {
					cd.showNoInternetPopup();
				} else {

					if (number.getText().toString().trim().length() < 6) {
						number.setTextColor(getResources()
								.getColor(R.color.red));
						number.setHintTextColor(getResources().getColor(
								R.color.red));
						bnumber = false;
					}

					if (answer.getText().toString().isEmpty()) {
						answer.setTextColor(getResources()
								.getColor(R.color.red));
						answer.setHintTextColor(getResources().getColor(
								R.color.red));
						bans = false;
					}
					if (otherqus.getVisibility() == View.VISIBLE
							&& otherqus.getText().toString().isEmpty()) {

						otherqus.setTextColor(getResources().getColor(
								R.color.red));
						otherqus.setHintTextColor(getResources().getColor(
								R.color.red));
						bqus = false;
					}
					if (qus.getText().toString().isEmpty()) {

						qus.setTextColor(getResources().getColor(R.color.red));
						qus.setHintTextColor(getResources().getColor(
								R.color.red));
						bqus = false;
					}

					if (pin1.getText().toString().isEmpty()
							|| pin2.getText().toString().isEmpty()
							|| pin3.getText().toString().isEmpty()
							|| pin4.getText().toString().isEmpty()) {
						pint.setTextColor(getResources().getColor(R.color.red));
						bpin1 = false;
						bpin2 = false;
						bpin3 = false;
						bpin4 = false;

					}

					if (bnumber == true & bpin1 == true && bpin2 == true
							&& bpin3 == true && bpin4 == true && bans == true
							&& bqus == true) {

						regcheck = new regloginid().execute();

					}

				}
			}
		});

		qus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						FbNewuser.this);
				builder.setTitle("Security Questions");

				builder.setSingleChoiceItems(secqus, tqus,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								buffKey = which;
								qus.setText(secqus[buffKey]);
								qus.setTextColor(getResources().getColor(
										R.color.black));
								int selectedPosition = ((AlertDialog) dialog)
										.getListView().getCheckedItemPosition();

								tqus = buffKey;
								if (qus.getText().toString()
										.equalsIgnoreCase("Other")) {
									otherqus.setVisibility(View.VISIBLE);
								} else {
									otherqus.setVisibility(View.GONE);
								}
								qus.setText(secqus[buffKey]);
								qus.setTextColor(getResources().getColor(
										R.color.black));
								// int selectedPosition = ((AlertDialog) dialog)
								// .getListView().getCheckedItemPosition();

								tqus = buffKey;
								if (qus.getText().toString()
										.equalsIgnoreCase("Other")) {
									otherqus.setVisibility(View.VISIBLE);
								} else {
									otherqus.setVisibility(View.GONE);
								}
								dialog.dismiss();

							}
						}).setCancelable(false);

				AlertDialog alert = builder.create();
				alert.show();
			}
		});

	}

	private class regloginid extends AsyncTask<Void, Void, Void> {
		String success, mess, response, quss;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(FbNewuser.this);
			pDialog.setMessage("Registering …");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@SuppressWarnings("deprecation")
		@Override
		protected Void doInBackground(Void... params) {
			HttpEntity resEntity;
			JSONArray jsonMainArr;
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(
					CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

			HttpPost httppost = new HttpPost(fbregister_url);
			MultipartEntity mpEntity = null;
			mpEntity = new MultipartEntity();
			try {
				info.device();
				info.showInfo(getApplicationContext());
				mpEntity.addPart("mobileNumber", new StringBody(number
						.getText().toString()));
				mpEntity.addPart("oldPin", new StringBody(oldpin));
				mpEntity.addPart("userId", new StringBody(userid));
				mpEntity.addPart("email", new StringBody(email));
				pin = pin1.getText().toString() + pin2.getText().toString()
						+ pin3.getText().toString() + pin4.getText().toString();
				mpEntity.addPart("pin", new StringBody(pin));
				mpEntity.addPart("os", new StringBody(info.manufacturer));
				mpEntity.addPart("make", new StringBody("Android" + " "
						+ info.Version));
				mpEntity.addPart("model", new StringBody(info.model));
				switch (buffKey) {
				case 0:
					quss = "What's your Passport Number ?";
					// json.put("securityQuestion",
					// "What’s your Passport Number ?");
					break;
				case 1:
					quss = "What's your License Number ?";
					// json.put("securityQuestion",
					// "What’s your License Number ?");
					break;

				case 2:
					quss = "What's your Mothers Maiden Name ?";
					// json.put("securityQuestion",
					// "What’s your Mothers Maiden Name ?");
					break;

				case 3:
					quss = "What's your First Pets Name ?";
					// json.put("securityQuestion",
					// "What’s your First Pets Name ?");
					break;

				case 4:
					quss = "Who was your First Childhood Friend ?";
					// json.put("securityQuestion",
					// "Who was your First Childhood Friend ?");
					break;
				case 5:
					quss = "What Primary School did you First Attend ?";
					// json.put("securityQuestion",
					// "What Primary School did you First Attend ?");
					break;
				case 6:
					quss = "What was the Colour of your First Car ?";
					// json.put("securityQuestion",
					// "What was the Colour of your First Car ?");
					break;
				case 7:
					quss = "What is your All Time Favourite Movie ?";
					// json.put("securityQuestion",
					// "What is your All Time Favourite Movie ?");
					break;
				case 8:
					quss = "What was your First Paid Job ?";
					// json.put("securityQuestion",
					// "What was your First Paid Job ?");
					break;
				case 9:
					quss = otherqus.getText().toString();
					// json.put("securityQuestion",
					// otherqus.getText().toString());
					break;

				default:
					break;
				}
				mpEntity.addPart("securityQuestion", new StringBody(quss));
				mpEntity.addPart("userId", new StringBody(userid));
				mpEntity.addPart("securityAnswer", new StringBody(answer
						.getText().toString()));

			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			httppost.setEntity(mpEntity);
			System.out.println(httppost.getRequestLine());
			HttpResponse response = null;
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				resEntity = response.getEntity();
			} catch (Exception e) {
				runOnUiThread(new Runnable() {

					public void run() {
						cd.showNoInternetPopup();
					}
				});
				return null;
			}
			System.out.println(response.getStatusLine());
			if (resEntity != null) {

				try {
					reponse = EntityUtils.toString(resEntity);
					System.out.println(reponse);
					JSONObject profile = new JSONObject(reponse);
					success = profile.getString("status");
					mess = profile.getString("message");
				} catch (ParseException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}

				if (success.equals("success")) {

					runOnUiThread(new Runnable() {

						public void run() {
							// for escape char
							quss = DatabaseUtils.sqlEscapeString(quss);

							db = new DatabaseHandler(FbNewuser.this);
							try {

								db.createDataBase();
							} catch (IOException e) {
								e.printStackTrace();
							}
							dbb = db.openDataBase();
							dbb = db.getReadableDatabase();

							atPrefs.edit().putBoolean(info.checkllogin, false)
									.commit();
							SQLiteDatabase dbbb = db.getReadableDatabase();
							dbbb.execSQL("UPDATE profile SET mobileNumber = '"
									+ number.getText().toString() + "'");

							dbbb.execSQL("UPDATE profile SET pin = '" + pin
									+ "'");
							dbbb.execSQL("UPDATE profile SET squs = " + quss
									+ "");
							dbbb.execSQL("UPDATE profile SET sans = '"
									+ answer.getText().toString() + "'");

							String selectQuery = "SELECT * FROM profile";

							Cursor cursor = dbb.rawQuery(selectQuery, null);
							if (cursor.moveToFirst()) {
								do {
									fname = cursor.getString(cursor
											.getColumnIndex("fName"));
									lname = cursor.getString(cursor
											.getColumnIndex("lName"));
								} while (cursor.moveToNext());
							}
							atPrefs.edit()
									.putInt(SplashscreenActivity.progress, 30)
									.commit();
							// register with urban airship
							AirshipConfigOptions options = AirshipConfigOptions
									.loadDefaultOptions(FbNewuser.this);
							UAirship.takeOff(getApplication(), options);
							PushManager.shared().setAlias(
									String.valueOf(userid));
							// set Tags @urban airship
							HashSet<String> tags = new HashSet<String>();
							tags.add(fname);
							tags.add(lname);
							PushManager.shared().setTags(tags);
							PushManager.enablePush();
							PushManager.shared().setIntentReceiver(
									IntentReceiver.class);
							String apid = PushManager.shared().getAPID();
							Logger.info("My Application onCreate - App APID: "
									+ apid);
							Intent next = new Intent(FbNewuser.this,
									MainActivity.class);
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
									FbNewuser.this).create();
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
									FbNewuser.this).create();
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
	protected void onDestroy() {
		// if app is close before register success ,del seesion of fb
		Session session = Session.getActiveSession();
		if (session != null) {

			if (!session.isClosed()) {
				session.closeAndClearTokenInformation();
				// clear your preferences if saved
			}
		} else {

			session = new Session(this);
			Session.setActiveSession(session);

			session.closeAndClearTokenInformation();
			// clear your preferences if saved

		}
		// Intent back = new Intent(getApplicationContext(),
		// LoginActivity.class);
		// startActivity(back);
		// finish();
		super.onDestroy();

	};

	@Override
	protected void onStop() {
		// if app is close before register ,del seesion of fb

		Session session = Session.getActiveSession();
		if (session != null) {

			if (!session.isClosed()) {
				session.closeAndClearTokenInformation();
				// clear your preferences if saved
			}
		} else {

			session = new Session(this);
			Session.setActiveSession(session);

			session.closeAndClearTokenInformation();
			// clear your preferences if saved

		}
		// Intent back = new Intent(getApplicationContext(),
		// LoginActivity.class);
		// startActivity(back);
		// finish();
		super.onDestroy();
	};

	@Override
	public void onBackPressed() {
		// if app is close before register ,del seesion of fb

		Session session = Session.getActiveSession();
		if (session != null) {

			if (!session.isClosed()) {
				session.closeAndClearTokenInformation();
				// clear your preferences if saved
			}
		} else {

			session = new Session(this);
			Session.setActiveSession(session);

			session.closeAndClearTokenInformation();
			// clear your preferences if saved

		}
		Intent back = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(back);
		finish();

	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon @ action bar clicked; go home
			Intent back = new Intent(this, LoginActivity.class);
			startActivity(back);
			finish();
			break;
		}
		return true;
	}

	@Override
	public void afterTextChanged(Editable s) {
		// go next
		if (pin1.getText().toString().length() == 1) {
			pin2.requestFocus();

		}
		if (pin2.getText().toString().length() == 1) {
			pin3.requestFocus();
		}
		if (pin3.getText().toString().length() == 1) {
			pin4.requestFocus();
		}

		// // hide keyboard
		// if (pin4.getText().toString().length() == 1) {
		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(pin4.getWindowToken(), 0);
		// }
		if (!(pin1.getText().toString().isEmpty()
				|| pin2.getText().toString().isEmpty()
				|| pin3.getText().toString().isEmpty() || pin4.getText()
				.toString().isEmpty())) {
			pint.setTextColor(getResources().getColor(R.color.black));
			bpin1 = true;
			bpin2 = true;
			bpin3 = true;
			bpin4 = true;

		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// change text color
		number.setTextColor(getResources().getColor(R.color.black));
		answer.setTextColor(getResources().getColor(R.color.black));
		otherqus.setTextColor(getResources().getColor(R.color.black));
		bnumber = true;
		bans = true;
		bqus = true;

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}
}
