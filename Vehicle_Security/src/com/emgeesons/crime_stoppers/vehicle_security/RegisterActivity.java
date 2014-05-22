package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class RegisterActivity extends SherlockActivity implements TextWatcher {
	EditText fname, lname, email, number, dob, gender, pin1, pin2, pin3, pin4;
	ImageView male, female;
	TextView pint;
	Button submit;
	String title = "Male";
	CheckBox showpin;
	Connection_Detector cd = new Connection_Detector(this);
	Boolean IsInternetPresent;
	boolean isDialogOpen = false;
	static final int DATE_DIALOG_ID = 999;
	int years, months, date, day;
	private AsyncTask<Void, Void, Void> regcheck;
	ProgressDialog pDialog;
	boolean bfname, blname, bemail, bnumber, bdob, bgender, bpin1, bpin2,
			bpin3, bpin4;
	String input_date;
	String register_url = "http://emgeesonsdevelopment.in/crimestoppers/mobile1.0/register.php";
	Data info;
	DatabaseHandler db;
	SQLiteDatabase dbb;
	SharedPreferences atPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.register_activity);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'> Register </font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.ic_app);
		atPrefs = PreferenceManager
				.getDefaultSharedPreferences(RegisterActivity.this);
		db = new DatabaseHandler(RegisterActivity.this);
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();
		info = new Data();
		fname = (EditText) findViewById(R.id.fname);
		lname = (EditText) findViewById(R.id.lname);
		email = (EditText) findViewById(R.id.email);
		number = (EditText) findViewById(R.id.number);
		dob = (EditText) findViewById(R.id.age);
		pin1 = (EditText) findViewById(R.id.pin1);
		pin2 = (EditText) findViewById(R.id.pin2);
		pin3 = (EditText) findViewById(R.id.pin3);
		pin4 = (EditText) findViewById(R.id.pin4);
		male = (ImageView) findViewById(R.id.male);
		female = (ImageView) findViewById(R.id.female);
		submit = (Button) findViewById(R.id.submit);
		showpin = (CheckBox) findViewById(R.id.pin);
		pint = (TextView) findViewById(R.id.textView1);
		fname.addTextChangedListener(this);
		lname.addTextChangedListener(this);
		number.addTextChangedListener(this);
		email.addTextChangedListener(this);
		dob.addTextChangedListener(this);
		pin1.addTextChangedListener(this);
		pin2.addTextChangedListener(this);
		pin3.addTextChangedListener(this);
		pin4.addTextChangedListener(this);
		bgender = true;
		male.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				male.setImageResource(R.drawable.male_active);
				female.setImageResource(R.drawable.female_inactive);
				title = "Male";
			}
		});
		female.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				female.setImageResource(R.drawable.female_active);
				male.setImageResource(R.drawable.male_inactive);
				title = "Female";
			}
		});
		dob.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isDialogOpen = true;
				Calendar c = Calendar.getInstance();
				years = c.get(Calendar.YEAR);
				months = c.get(Calendar.MONTH);
				date = c.get(Calendar.DAY_OF_MONTH);
				showDialog(DATE_DIALOG_ID);
			}
		});
		dob.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {
					isDialogOpen = true;
					Calendar c = Calendar.getInstance();
					years = c.get(Calendar.YEAR);
					months = c.get(Calendar.MONTH);
					date = c.get(Calendar.DAY_OF_MONTH);
					showDialog(DATE_DIALOG_ID);
				}
			}
		});

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
					if (fname.getText().toString().trim().length() < 3) {

						fname.setTextColor(getResources().getColor(R.color.red));
						fname.setHintTextColor(getResources().getColor(
								R.color.red));
						bfname = false;
					}
					if (lname.getText().toString().trim().length() < 3) {
						lname.setTextColor(getResources().getColor(R.color.red));
						lname.setHintTextColor(getResources().getColor(
								R.color.red));
						blname = false;
					}
					if (number.getText().toString().trim().length() < 6) {
						number.setTextColor(getResources()
								.getColor(R.color.red));
						number.setHintTextColor(getResources().getColor(
								R.color.red));
						bnumber = false;
					}
					if (email.getText().toString().trim().length() < 2
							|| !email
									.getText()
									.toString()
									.matches(
											"[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")) {
						email.setTextColor(getResources().getColor(R.color.red));
						email.setHintTextColor(getResources().getColor(
								R.color.red));
						bemail = false;
					}
					if (dob.getText().toString().length() == 0) {
						dob.setTextColor(getResources().getColor(R.color.red));
						dob.setHintTextColor(getResources().getColor(
								R.color.red));
						bdob = false;
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

					if (bfname == true & blname == true & bemail == true
							& bnumber == true & bdob == true & bgender == true
							& bpin1 == true & bpin2 == true & bpin3 == true
							& bpin4 == true) {

						regcheck = new regloginid().execute();

					}

				}
			}
		});

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			// set time picker as current date

			DatePickerDialog dialog = new DatePickerDialog(this,
					datePickerListener, years, months, date);
			dialog.getDatePicker().setMaxDate(new Date().getTime());
			return dialog;

		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		String month, dates;

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			if (!isDialogOpen)
				return;
			isDialogOpen = false;
			date = dayOfMonth;
			years = year;
			months = monthOfYear + 1;
			// to get day from date

			// if (months < 10 || date < 10) {
			// month = StringUtils.leftPad(String.valueOf(months), 2, "0");
			// dates = StringUtils.leftPad(String.valueOf(date), 2, "0");
			//
			// }
			input_date = years + "-" + months + "-" + date;
			System.out.println(input_date);
			int AGE = getAge(years, months, date);
			// Log.d("My", "age is :" + AGE);
			dob.setText(String.valueOf(AGE) + "Yrs");

		}

	};

	public int getAge(int years, int months, int day) {

		GregorianCalendar cal = new GregorianCalendar();
		int y, m, d, a;

		y = cal.get(Calendar.YEAR);
		m = cal.get(Calendar.MONTH);
		d = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(years, months, day);
		a = y - cal.get(Calendar.YEAR);
		if ((m < cal.get(Calendar.MONTH) + 1)
				|| ((m == cal.get(Calendar.MONTH) + 1) && (d < cal
						.get(Calendar.DAY_OF_MONTH)))) {
			--a;
		}
		if (a == -1) {
			a = 0;
		}
		return a;
	}

	private class regloginid extends AsyncTask<Void, Void, Void> {
		String success, mess, response, id;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RegisterActivity.this);
			pDialog.setMessage("");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(register_url);
			JSONArray jsonMainArr;
			JSONObject json = new JSONObject();
			try {
				info.device();
				json.put("firstName", fname.getText().toString());
				json.put("lastName", lname.getText().toString());
				json.put("mobileNumber", number.getText().toString());
				json.put("dob", input_date);
				json.put("email", email.getText().toString());
				json.put("gender", title);
				String pin = pin1.getText().toString()
						+ pin2.getText().toString() + pin3.getText().toString()
						+ pin4.getText().toString();
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
				id = jsonMainArr.getJSONObject(0).getString("user_id");

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

						db = new DatabaseHandler(RegisterActivity.this);
						PersonalData data = new PersonalData(id, fname
								.getText().toString(), lname.getText()
								.toString(), email.getText().toString(), number
								.getText().toString(), input_date, title, "",
								"", "", "", "", "", "", "", "", "");
						db.updateprofileData(data);
						atPrefs.edit()
								.putBoolean(SplashscreenActivity.checkllogin,
										false).commit();
						Intent next = new Intent(RegisterActivity.this,
								HomescreenActivity.class);
						startActivity(next);

					}
				});
			}
			// response failure
			else if (success.equals("failure")) {

				runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								RegisterActivity.this).create();
						Dialog.setTitle("Error");
						Dialog.setIcon(R.drawable.ic_action_error);
						Dialog.setMessage("Something went wrong.please try again later");
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
								RegisterActivity.this).create();
						Dialog.setTitle("Error");
						Dialog.setIcon(R.drawable.ic_action_error);
						Dialog.setMessage("Something went wrong.please try again later");
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
		fname.setTextColor(getResources().getColor(R.color.black));
		lname.setTextColor(getResources().getColor(R.color.black));
		number.setTextColor(getResources().getColor(R.color.black));
		email.setTextColor(getResources().getColor(R.color.black));
		dob.setTextColor(getResources().getColor(R.color.black));
		bfname = true;
		blname = true;
		bnumber = true;
		bemail = true;
		bdob = true;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

}
