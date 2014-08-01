package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class EditInfo extends BaseActivity implements TextWatcher,
		OnKeyListener {
	Connection_Detector cd = new Connection_Detector(this);
	Boolean IsInternetPresent;
	EditText fname, lname, email, number, otherqus, answer, lnumber, address,
			postcode, pin1, pin2, pin3, pin4;
	CheckBox showpin;
	ImageView male, female;
	TextView pint, qus, dob;
	boolean bfname, blname, bemail, bnumber, bdob, bgender, bpin1, bpin2,
			bpin3, bpin4, bans, bqus, blnumber, baddress, bpostcode;
	static int buffKey = 0;
	static CharSequence[] secqus;
	int tqus;
	String title, id;
	boolean isDialogOpen = false;
	static final int DATE_DIALOG_ID = 999;
	int years, months, date, day;
	String input_date;
	Data info;
	SharedPreferences atPrefs;
	String update_url = Data.url + "updateProfile.php";
	ProgressDialog pDialog;
	private AsyncTask<Void, Void, Void> update;
	int qusvalue;
	DatabaseHandler db;
	SQLiteDatabase dbb;
	String oldpin;
	GPSTracker gps;
	double LATITUDE, LONGITUDE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.edit_info);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'>Edit Info </font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.app_icon);
		atPrefs = PreferenceManager.getDefaultSharedPreferences(EditInfo.this);
		db = new DatabaseHandler(EditInfo.this);
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();
		fname = (EditText) findViewById(R.id.fname);
		lname = (EditText) findViewById(R.id.lname);
		email = (EditText) findViewById(R.id.email);
		number = (EditText) findViewById(R.id.number);
		dob = (TextView) findViewById(R.id.age);
		pin1 = (EditText) findViewById(R.id.pin1);
		pin2 = (EditText) findViewById(R.id.pin2);
		pin3 = (EditText) findViewById(R.id.pin3);
		pin4 = (EditText) findViewById(R.id.pin4);
		male = (ImageView) findViewById(R.id.male);
		female = (ImageView) findViewById(R.id.female);
		showpin = (CheckBox) findViewById(R.id.pin);
		pint = (TextView) findViewById(R.id.textView1);
		qus = (TextView) findViewById(R.id.sec_qus);
		otherqus = (EditText) findViewById(R.id.sec_other_qus);
		answer = (EditText) findViewById(R.id.sec_ans);
		lnumber = (EditText) findViewById(R.id.Licence);
		address = (EditText) findViewById(R.id.address);
		postcode = (EditText) findViewById(R.id.postcode);
		fname.addTextChangedListener(this);
		lname.addTextChangedListener(this);
		number.addTextChangedListener(this);
		email.addTextChangedListener(this);
		dob.addTextChangedListener(this);
		lnumber.addTextChangedListener(this);
		address.addTextChangedListener(this);
		postcode.addTextChangedListener(this);
		pin1.addTextChangedListener(this);
		pin2.addTextChangedListener(this);
		pin3.addTextChangedListener(this);
		pin4.addTextChangedListener(this);
		pin1.setOnKeyListener(this);
		pin2.setOnKeyListener(this);
		pin3.setOnKeyListener(this);
		pin4.setOnKeyListener(this);
		gps = new GPSTracker(EditInfo.this);
		if (gps.canGetLocation()) {
			LATITUDE = gps.getLatitude();
			LONGITUDE = gps.getLongitude();

		} else {

			LATITUDE = 0.0;
			LONGITUDE = 0.0;

		}
		info = new Data();
		secqus = getResources().getStringArray(R.array.sec_qus);
		if (!atPrefs.getBoolean(info.checkllogin, true)) {
			info.showInfo(getApplicationContext());
			id = info.user_id;
			fname.setText(info.fName);
			lname.setText(info.lName);
			email.setText(info.email);
			number.setText(info.mobileNumber);
			if (info.year == 0 || info.month == 0 || info.date == 0) {
				dob.setHint("dob");
			} else {
				// int AGe = info.getAge(info.year, info.month, info.date);
				String AGe = String.valueOf(info.date + "-" + info.month + "-"
						+ info.year);
				dob.setText(AGe);
			}
			oldpin = info.pin;
			String[] pinnumber = info.pin.split("(?!^)");
			pin1.setText(pinnumber[0]);
			pin2.setText(pinnumber[1]);
			pin3.setText(pinnumber[2]);
			pin4.setText(pinnumber[3]);

			if (info.gender.equalsIgnoreCase("male")) {
				male.setImageResource(R.drawable.male_active);
				female.setImageResource(R.drawable.female_inactive);
				title = "Male";

			} else {
				male.setImageResource(R.drawable.male_inactive);
				female.setImageResource(R.drawable.female_active);
				title = "Female";
			}
			lnumber.setText(info.licenseNo);
			address.setText(info.street);
			postcode.setText(info.postcode);
			tqus = info.qusvalues(info.squs);
			

			if (tqus == 9) {

				otherqus.setVisibility(View.VISIBLE);
				otherqus.setText(info.squs);
				qus.setText(secqus[tqus]);
			} else {
				qus.setText(secqus[tqus]);
			}
			answer.setText(info.sans);
		}

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
				isDialogOpen = true;
				Calendar c = Calendar.getInstance();
				years = c.get(Calendar.YEAR);
				months = c.get(Calendar.MONTH);
				date = c.get(Calendar.DAY_OF_MONTH);
				showDialog(DATE_DIALOG_ID);
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

		qus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						EditInfo.this);
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
						}).setCancelable(false)

				// .setPositiveButton("Ok", new
				// DialogInterface.OnClickListener() {
				// @Override
				// public void onClick(DialogInterface dialog, int which) {
				// qus.setText(secqus[buffKey]);
				// qus.setTextColor(getResources().getColor(R.color.black));
				// int selectedPosition = ((AlertDialog) dialog)
				// .getListView().getCheckedItemPosition();
				//
				// tqus = buffKey;
				// if (qus.getText().toString().equalsIgnoreCase("Other")) {
				// otherqus.setVisibility(View.VISIBLE);
				// } else {
				// otherqus.setVisibility(View.GONE);
				// }

				// }
				// })
				;

				AlertDialog alert = builder.create();
				alert.show();
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
			Calendar calendars = Calendar.getInstance();
			calendars.set(Calendar.YEAR, years-13);
			calendars.set(Calendar.MONTH, months);
			calendars.set(Calendar.MINUTE, date);
			dialog.getDatePicker().setMaxDate(calendars.getTimeInMillis());
			// 13 year
//			dialog.getDatePicker().setMaxDate(
//					new Date().getTime() - 1L * 13 * 365 * 24 * 60 * 60 * 1000);
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
			int AGE = info.getAge(years, months, date);
			// Log.d("My", "age is :" + AGE);
			String age = date + "-" + months + "-" + years;
			dob.setText(age);

		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.edit_info, menu);
		MenuItem add = menu.findItem(R.id.save);
		add.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {

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
					if (!email
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
//					if (lnumber.getText().toString().length() < 6) {
//						lnumber.setHintTextColor(getResources().getColor(
//								R.color.red));
//						lnumber.setTextColor(getResources().getColor(
//								R.color.red));
//						blnumber = false;
//
//					}
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
					if (bfname == true && blname == true && bemail == true
							&& bnumber == true && bdob == true && bpin1 == true
							&& bpin2 == true && bpin3 == true && bpin4 == true
							&& bans == true && bqus == true && blnumber == true
							&& baddress == true && bpostcode == true) {

						update = new updates().execute();

					}

				}

				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	private class updates extends AsyncTask<Void, Void, Void> {
		String success, mess, response, pin, qus;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditInfo.this);
			pDialog.setMessage("Updating Information...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(update_url);
			System.out.println(update_url);
			JSONArray jsonMainArr;
			JSONObject json = new JSONObject();
			try {
				
				info.device();
				info.showInfo(getApplicationContext());

				json.put("userId", info.user_id);
				json.put("firstName", fname.getText().toString());
				json.put("lastName", lname.getText().toString());
				json.put("mobileNumber", number.getText().toString());
				json.put("dob", input_date);
				json.put("email", email.getText().toString());
				json.put("gender", title);
				pin = pin1.getText().toString() + pin2.getText().toString()
						+ pin3.getText().toString() + pin4.getText().toString();
				json.put("pin", pin);
				json.put("oldPin", oldpin);
				json.put("make", info.manufacturer);
				json.put("os", "Android" + " " + info.Version);
				json.put("model", info.model);
				json.put("licenceNo", lnumber.getText().toString());
				json.put("address", address.getText().toString());
				json.put("pincode", postcode.getText().toString());
				json.put("latitude", LATITUDE);
				json.put("longitude", LONGITUDE);
				switch (buffKey) {
				case 0:
					qus = "What's your Passport Number ?";
					break;
				case 1:
					qus = "What's your License Number ?";
					break;

				case 2:
					qus = "What's your Mothers Maiden Name ?";
					break;

				case 3:
					qus = "What's your First Pets Name ?";
					break;

				case 4:
					qus = "Who was your First Childhood Friend ?";
					break;
				case 5:
					qus = "What Primary School did you First Attend ?";
					break;
				case 6:
					qus = "What was the Colour of your First Car ?";
					break;
				case 7:
					qus = "What is your All Time Favourite Movie ?";
					break;
				case 8:
					qus = "What was your First Paid Job ?";
					break;
				case 9:
					qus = otherqus.getText().toString();
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
						// qusvalue = info.qusvalues(qus);

						qus = DatabaseUtils.sqlEscapeString(qus);
						SQLiteDatabase dbbb = db.getReadableDatabase();
						dbbb.execSQL("UPDATE profile SET user_id = '" + id
								+ "',fName = '" + fname.getText().toString()
								+ "',lName = '" + lname.getText().toString()
								+ "',email = '" + email.getText().toString()
								+ "',mobileNumber = '"
								+ number.getText().toString() + "',dob = '"
								+ input_date + "',gender = '" + title
								+ "',licenseNo = '"
								+ lnumber.getText().toString()
								+ "',street = '"
								+ address.getText().toString()
								+ "',postcode = '"
								+ postcode.getText().toString() + "',pin = '"
								+ pin + "',squs = " + qus + ",sans = '"
								+ answer.getText().toString() + "'");

						atPrefs.edit().putBoolean(info.checkllogin, false)
								.commit();
						Intent next = new Intent(EditInfo.this,
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
								EditInfo.this).create();
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
								EditInfo.this).create();
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
		answer.setTextColor(getResources().getColor(R.color.black));
		otherqus.setTextColor(getResources().getColor(R.color.black));
		lnumber.setTextColor(getResources().getColor(R.color.black));
		address.setTextColor(getResources().getColor(R.color.black));
		postcode.setTextColor(getResources().getColor(R.color.black));

		bfname = true;
		blname = true;
		bnumber = true;
		bemail = true;
		bdob = true;
		bans = true;
		bqus = true;
		blnumber = true;
		baddress = true;
		bpostcode = true;

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// go back

		if (keyCode == KeyEvent.KEYCODE_DEL) {
			if (pin4.hasFocus()) {
				// pin4.setText("");
				pin3.requestFocus();
			} else if (pin3.hasFocus()) {
				// pin3.setText("");
				pin2.requestFocus();
			} else if (pin2.hasFocus()) {
				// pin2.setText("");
				pin1.requestFocus();

			}

		}
		return false;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon @ action bar clicked; go home
			super.onBackPressed();
			break;
		}
		return true;
	}

	@Override
	protected int getLayoutResourceId() {
		// TODO Auto-generated method stub
		return R.layout.edit_info;
	}

}
