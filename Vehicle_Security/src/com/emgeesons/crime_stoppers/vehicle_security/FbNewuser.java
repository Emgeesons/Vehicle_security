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
	int tqus;
	Data info;
	boolean bnumber, bpin1, bpin2, bpin3, bpin4, bans, bqus;
	private AsyncTask<Void, Void, Void> regcheck;
	ProgressDialog pDialog;
	String fbregister_url = Data.url + "fbCompleteRegistration.php";
	SharedPreferences atPrefs;
	DatabaseHandler db;
	SQLiteDatabase dbb;
	String pin;
	int qusvalue;

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
		getSupportActionBar().setIcon(R.drawable.ic_app);
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
							}
						}).setCancelable(false)

				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						qus.setText(secqus[buffKey]);
						qus.setTextColor(getResources().getColor(R.color.black));
						int selectedPosition = ((AlertDialog) dialog)
								.getListView().getCheckedItemPosition();

						tqus = buffKey;
						if (qus.getText().toString().equalsIgnoreCase("Other")) {
							otherqus.setVisibility(View.VISIBLE);
						} else {
							otherqus.setVisibility(View.GONE);
						}

					}
				});

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
			pDialog.setMessage("Register");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(fbregister_url);
			System.out.println(fbregister_url);
			JSONArray jsonMainArr;
			JSONObject json = new JSONObject();
			try {
				info.device();
				
				json.put("mobileNumber", number.getText().toString());
				json.put("oldPin", oldpin);
				json.put("userId", userid);
				json.put("email", email);
				pin = pin1.getText().toString() + pin2.getText().toString()
						+ pin3.getText().toString() + pin4.getText().toString();
				json.put("pin", pin);
				json.put("make", info.manufacturer);
				json.put("os", "Android" + " " + info.Version);
				json.put("model", info.model);
				// if (otherqus.getVisibility() == View.GONE) {
				// json.put("securityQuestion", qus);
				// } else {
				// json.put("securityQuestion", otherqus);
				// }
				// switch (buffKey) {
				// case 0:
				// json.put("securityQuestion",
				// "What’s your Passport Number ?");
				// break;
				// case 1:
				// json.put("securityQuestion", "What’s your License Number ?");
				// break;
				//
				// case 2:
				// json.put("securityQuestion",
				// "What’s your Mothers Maiden Name ?");
				// break;
				//
				// case 3:
				// json.put("securityQuestion",
				// "What’s your First Pets Name ?");
				// break;
				//
				// case 4:
				// json.put("securityQuestion",
				// "Who was your First Childhood Friend ?");
				// break;
				// case 5:
				// json.put("securityQuestion",
				// "What Primary School did you First Attend ?");
				// break;
				// case 6:
				// json.put("securityQuestion",
				// "What was the Colour of your First Car ?");
				// break;
				// case 7:
				// json.put("securityQuestion",
				// "What is your All Time Favourite Movie ?");
				// break;
				// case 8:
				// json.put("securityQuestion",
				// "What was your First Paid Job ?");
				// break;
				// case 9:
				// json.put("securityQuestion", otherqus.getText().toString());
				// break;
				//
				// default:
				// break;
				// }

				switch (buffKey) {
				case 0:
					quss = "What’s your Passport Number ?";
					// json.put("securityQuestion",
					// "What’s your Passport Number ?");
					break;
				case 1:
					quss = "What’s your License Number ?";
					// json.put("securityQuestion",
					// "What’s your License Number ?");
					break;

				case 2:
					quss = "What’s your Mothers Maiden Name ?";
					// json.put("securityQuestion",
					// "What’s your Mothers Maiden Name ?");
					break;

				case 3:
					quss = "What’s your First Pets Name ?";
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
				json.put("securityQuestion", qus);

				json.put("securityAnswer", answer.getText().toString());

				System.out.println("Elements-->" + json);
				postMethod.setHeader("Content-Type", "application/json");
				postMethod.setEntity(new ByteArrayEntity(json.toString()
						.getBytes("UTF8")));
				String response = httpClient
						.execute(postMethod, resonseHandler);
				Log.e("response :", response);
				JSONObject profile = new JSONObject(response);
				// jsonMainArr = profile.getJSONArray("response");
				success = profile.getString("status");
				mess = profile.getString("message");
				// id = jsonMainArr.getJSONObject(0).getString("user_id");

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
						qusvalue = info.qusvalues(quss);
						db = new DatabaseHandler(FbNewuser.this);
						try {

							db.createDataBase();
						} catch (IOException e) {
							e.printStackTrace();
						}
						dbb = db.openDataBase();
						dbb = db.getReadableDatabase();

						atPrefs.edit()
								.putBoolean(SplashscreenActivity.checkllogin,
										false).commit();
						SQLiteDatabase dbbb = db.getReadableDatabase();
						dbbb.execSQL("UPDATE profile SET mobileNumber = '"
								+ number.getText().toString() + "'");

						dbbb.execSQL("UPDATE profile SET pin = '" + pin + "'");
						dbbb.execSQL("UPDATE profile SET squs = '"
								+ String.valueOf(qusvalue) + "'");
						dbbb.execSQL("UPDATE profile SET sans = '"
								+ answer.getText().toString() + "'");

						Intent next = new Intent(FbNewuser.this,
								MainActivity.class);
						startActivity(next);

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
								FbNewuser.this).create();
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
