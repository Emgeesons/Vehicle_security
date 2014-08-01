package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
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
import android.database.Cursor;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class AddInsurance extends BaseActivity implements TextWatcher {
	TextView cname, expiry;
	EditText other_name, policy, number;
	String type, id, call;
	double LATITUDE, LONGITUDE;
	static int buffKey = -1;
	static CharSequence[] cnametype;
	static CharSequence[] cnotype;

	int tname;
	Connection_Detector cd = new Connection_Detector(this);
	Boolean IsInternetPresent;
	boolean isDialogOpen = false;
	static final int DATE_DIALOG_ID = 999;
	int years, months, date, day;
	SQLiteDatabase dbb;
	DatabaseHandler db;
	boolean bother_name, bpolicy, bcname;
	String ins_url = Data.url + "addVehicleInsurance.php";
	ProgressDialog pDialog;
	private AsyncTask<Void, Void, Void> addins;
	Data info;
	long time;
	JSONArray jsonVehicleArr;
	String profile_url = Data.url + "getProfile.php";
	SharedPreferences atPrefs;
	String expdate = null;
	GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.add_insurance);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'> Add Insurance</font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.app_icon);
		atPrefs = PreferenceManager
				.getDefaultSharedPreferences(AddInsurance.this);
		cname = (TextView) findViewById(R.id.cname);
		expiry = (TextView) findViewById(R.id.exp);
		other_name = (EditText) findViewById(R.id.other_cname);
		policy = (EditText) findViewById(R.id.policyno);
		number = (EditText) findViewById(R.id.number);
		other_name.addTextChangedListener(this);
		policy.addTextChangedListener(this);
		info = new Data();
		db = new DatabaseHandler(getApplicationContext());
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();
		Intent intent = getIntent();
		type = intent.getStringExtra("type");
		id = intent.getStringExtra("id");
		call = intent.getStringExtra("call");
		info.vehicleInfo(getApplicationContext(), id);
		if (type.equalsIgnoreCase("Bicycle")) {
			cnametype = getResources().getStringArray(R.array.bicycleins_comp);
			cnotype = getResources().getStringArray(R.array.bicycleins_no);
		} else if (type.equalsIgnoreCase("car")) {
			cnametype = getResources().getStringArray(R.array.car_comp);
			cnotype = getResources().getStringArray(R.array.car_no);
		} else if (type.equalsIgnoreCase("MotorCycle")) {
			cnametype = getResources().getStringArray(R.array.motor_comp);
			cnotype = getResources().getStringArray(R.array.motor_no);
		} else {
			cnametype = getResources().getStringArray(R.array.car_comp);
			cnotype = getResources().getStringArray(R.array.car_no);
		}

		if (!call.equalsIgnoreCase("first")) {
			// tname = true;
			if (info.iname.equalsIgnoreCase("1Cover")) {
				tname = 0;
			} else if (info.iname.equalsIgnoreCase("AAMI")) {
				tname = 1;
			} else if (info.iname.equalsIgnoreCase("Allianz")) {
				tname = 2;
			} else if (info.iname.equalsIgnoreCase("APIA")) {
				tname = 3;

			} else if (info.iname.equalsIgnoreCase("Budget Direct")) {
				tname = 4;

			} else if (info.iname.equalsIgnoreCase("Bupa/HBA")) {
				tname = 5;

			} else if (info.iname.equalsIgnoreCase("CGU")) {
				tname = 6;

			} else if (info.iname.equalsIgnoreCase("Coles")) {
				tname = 7;

			} else if (info.iname.equalsIgnoreCase("CommInsure")) {
				tname = 8;

			} else if (info.iname.equalsIgnoreCase("COTA")) {
				tname = 9;
			} else if (info.iname.equalsIgnoreCase("Elders")) {
				tname = 10;
			} else if (info.iname.equalsIgnoreCase("GIO")) {
				tname = 11;

			} else if (info.iname.equalsIgnoreCase("NAB")) {
				tname = 12;

			} else if (info.iname.equalsIgnoreCase("NRMA")) {
				tname = 13;

			} else if (info.iname.equalsIgnoreCase("People’s Choice ")) {
				tname = 14;

			} else if (info.iname.equalsIgnoreCase("QBE")) {
				tname = 15;

			} else if (info.iname.equalsIgnoreCase("RAA")) {
				tname = 16;

			} else if (info.iname.equalsIgnoreCase("RAC")) {
				tname = 17;
			} else if (info.iname.equalsIgnoreCase("RACQ")) {
				tname = 18;

			} else if (info.iname.equalsIgnoreCase("RACT")) {
				tname = 19;

			} else if (info.iname.equalsIgnoreCase("RACV")) {
				tname = 20;

			} else if (info.iname.equalsIgnoreCase("SGIC")) {
				tname = 21;

			} else if (info.iname.equalsIgnoreCase("SGIO")) {
				tname = 22;

			} else if (info.iname.equalsIgnoreCase("Shannons")) {
				tname = 23;

			} else if (info.iname.equalsIgnoreCase("St George")) {
				tname = 24;
			} else if (info.iname.equalsIgnoreCase("Suncorp")) {
				tname = 25;

			} else if (info.iname.equalsIgnoreCase("TIO")) {
				tname = 26;

			} else if (info.iname.equalsIgnoreCase("Westpac")) {
				tname = 27;

			} else if (info.iname.equalsIgnoreCase("Woolworths")) {
				tname = 28;

			} else if (info.iname.equalsIgnoreCase("Youi")) {
				tname = 29;

			} else if (info.iname.equalsIgnoreCase("BikeSure")) {
				tname = 30;

			} else if (info.iname.equalsIgnoreCase("CycleCover")) {
				tname = 31;

			} else if (info.iname.equalsIgnoreCase("RealBike")) {
				tname = 32;

			} else if (info.iname.equalsIgnoreCase("RealBike")) {
				tname = 33;

			} else {
				tname = 34;
				other_name.setVisibility(View.VISIBLE);

			}

			if (!type.equalsIgnoreCase("Bicycle") && tname == 34) {
				tname = 30;

			}
			if (tname == 34 || tname == 30) {
				other_name.setVisibility(View.VISIBLE);
				cname.setText(cnametype[tname]);
				other_name.setText(info.iname);
			} else {
				other_name.setVisibility(View.GONE);
				cname.setText(cnametype[tname]);
			}

			number.setText(cnotype[tname]);
			policy.setText(info.ipolicy);
			if (info.exp.contains("0000-00-00") || info.exp.equalsIgnoreCase("null")) {
				expiry.setHint("Expiry");
			} else {
				expiry.setText(getdateformate(info.exp));

				String dates[] = info.exp.split("-");
				int year = Integer.valueOf(dates[0]);
				int month = Integer.valueOf(dates[1]);
				int day = Integer.valueOf(dates[2]);
				expdate = year + "-" + month + "-" + day;

			}

		}
		gps = new GPSTracker(AddInsurance.this);
		if (gps.canGetLocation()) {
			LATITUDE = gps.getLatitude();
			LONGITUDE = gps.getLongitude();

		} else {
			LATITUDE = 0.0;
			LONGITUDE = 0.0;
		}
		cname.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						AddInsurance.this);
				builder.setTitle("Insurance Company Name");

				builder.setSingleChoiceItems(cnametype, tname,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								buffKey = which;
								cname.setText(cnametype[buffKey]);
								cname.setTextColor(getResources().getColor(
										R.color.black));
								int selectedPosition = ((AlertDialog) dialog)
										.getListView().getCheckedItemPosition();

								tname = buffKey;
								bcname = true;
								if (cname.getText().toString()
										.equalsIgnoreCase("Other")) {
									other_name.setVisibility(View.VISIBLE);
								} else {
									other_name.setVisibility(View.GONE);
								}
								number.setText(cnotype[buffKey]);
								dialog.dismiss();
							}
						}).setCancelable(false);

				AlertDialog alert = builder.create();
				alert.setCancelable(true);
				alert.show();
			}
		});

		expiry.setOnClickListener(new OnClickListener() {

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

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.add_insurance, menu);
		MenuItem add = menu.findItem(R.id.save);
		add.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {

				IsInternetPresent = cd.isConnectingToInternet();
				if (IsInternetPresent == false) {
					cd.showNoInternetPopup();
				} else {
					if (policy.getText().toString().trim().length() < 3) {

						policy.setTextColor(getResources()
								.getColor(R.color.red));
						policy.setHintTextColor(getResources().getColor(
								R.color.red));
						bpolicy = false;
					}
					if (other_name.getVisibility() == View.VISIBLE
							&& other_name.getText().toString().trim().length() < 3) {

						other_name.setTextColor(getResources().getColor(
								R.color.red));
						other_name.setHintTextColor(getResources().getColor(
								R.color.red));
						bother_name = false;
					}
					if (cname.getText().toString().isEmpty()) {

						cname.setTextColor(getResources().getColor(R.color.red));
						cname.setHintTextColor(getResources().getColor(
								R.color.red));
						bcname = false;
					}
					if (bpolicy == true && bother_name == true
							&& bother_name == true) {
						if (!(expdate == null)) {

							String date[] = expdate.split("-");
							int year = Integer.valueOf(date[0]);
							int month = Integer.valueOf(date[1]);
							int day = Integer.valueOf(date[2]);

							Calendar gc = Calendar.getInstance();
							gc.set(year, month - 1, day, 8, 0);
							time = gc.getTimeInMillis() - 604800000;
							SQLiteDatabase dbbb = db.getReadableDatabase();
							dbbb.execSQL("UPDATE vehicle_info SET vehicle_expmil = '"
									+ time + "'WHERE vehicle_id ='" + id + "'");
							NotificationAlarm
									.CancelAlarm(getApplicationContext());
							NotificationAlarm.SetAlarm(getApplicationContext());
						}
						addins = new ins().execute();

					}
				}

				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	private class ins extends AsyncTask<Void, Void, Void> {
		String success, mess, response, pin, qus, name;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AddInsurance.this);
			pDialog.setMessage("Adding Insurance");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(ins_url);
			System.out.println(ins_url);
			JSONArray jsonMainArr;
			JSONObject json = new JSONObject();
			try {

				info.device();
				info.showInfo(getApplicationContext());

				json.put("userId", info.user_id);
				json.put("vehicleId", id);
				if (cname.getText().toString().equalsIgnoreCase("other")) {

					name = other_name.getText().toString();

				} else {
					name = cname.getText().toString();

				}
				json.put("insuranceCompanyName", name);
				json.put("insuranceCompanyNumber", number.getText().toString());

				json.put("insurancePolicyNumber", policy.getText().toString());
				if (expiry.getText().toString().isEmpty()) {
					json.put("insuranceExpiryDate", "000-00-00");
				} else {
					json.put("insuranceExpiryDate", expdate);
				}

				json.put("make", info.manufacturer);
				json.put("os", "Android" + " " + info.Version);
				json.put("model", info.model);

				json.put("latitude", LATITUDE);
				json.put("longitude", LONGITUDE);

				json.put("pin", info.pin);
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
						dbbb.execSQL("UPDATE vehicle_info SET vehicle_insname = '"
								+ name
								+ "',vehicle_insno = '"
								+ policy.getText().toString()
								+ "',vehicle_insexp = '"
								+ expdate
								+ "',vehicle_insnum = '"
								+ number.getText().toString()
								+ "'WHERE vehicle_id='" + id + "'");

						Intent next = new Intent(AddInsurance.this,
								VehicleProfile.class);
						next.putExtra("id", id);
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
								AddInsurance.this).create();
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
								AddInsurance.this).create();
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
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			// set time picker as current date
			DatePickerDialog dialog = new DatePickerDialog(this,
					datePickerListener, years, months, date);

			// dialog.getDatePicker().setMinDate(new Date().getTime());
			return dialog;

		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		String month, dates;
		String title;
		String input_date;

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
			// add zero
			if (months < 10 || date < 10) {
				month = StringUtils.leftPad(String.valueOf(months), 2, "0");
				dates = StringUtils.leftPad(String.valueOf(date), 2, "0");
				input_date = years + "-" + month + "-" + dates;
			}
			input_date = years + "-" + months + "-" + date;
			System.out.println(input_date);
			expdate = input_date;
			expiry.setText(getdateformate(input_date));

		}

	};

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

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		other_name.setTextColor(getResources().getColor(R.color.black));
		policy.setTextColor(getResources().getColor(R.color.black));

		bother_name = true;
		bpolicy = true;
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {

		Intent next = new Intent(getApplicationContext(), VehicleProfile.class);
		next.putExtra("id", id);
		startActivity(next);
		finish();
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			// app icon @ action bar clicked; go home
			Intent next = new Intent(getApplicationContext(),
					VehicleProfile.class);

			next.putExtra("id", id);
			startActivity(next);
			finish();

			break;
		}
		return true;
	}

	@Override
	protected int getLayoutResourceId() {
		// TODO Auto-generated method stub
		return R.layout.add_insurance;
	}

}
