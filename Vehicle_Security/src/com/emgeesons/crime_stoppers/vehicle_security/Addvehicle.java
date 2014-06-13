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
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class Addvehicle extends SherlockActivity implements TextWatcher {
	TextView type, body;
	EditText type_other, make, model, reg, engine, chassis, colour, acc;
	boolean btype, bbody, btype_other, bmake, bmodel, breg, beng, bchassis,
			bcolour, bacc;
	static int buffKey = 0;
	static CharSequence[] vechtype, bodytype;
	int tvech, tbody;
	Connection_Detector cd = new Connection_Detector(this);
	Boolean IsInternetPresent;
	String addVehicle_url = Data.url + "addVehicle.php";
	private AsyncTask<Void, Void, Void> addVehicle;
	ProgressDialog pDialog;
	Data info;
	DatabaseHandler db;
	SQLiteDatabase dbb;
	int typevalue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addvehicle);
		type = (TextView) findViewById(R.id.type);
		body = (TextView) findViewById(R.id.body);
		type_other = (EditText) findViewById(R.id.type_other);
		make = (EditText) findViewById(R.id.make);
		model = (EditText) findViewById(R.id.model);
		reg = (EditText) findViewById(R.id.registration);
		engine = (EditText) findViewById(R.id.engine);
		chassis = (EditText) findViewById(R.id.chassis);
		colour = (EditText) findViewById(R.id.Colour);
		acc = (EditText) findViewById(R.id.acc);
		info = new Data();
		db = new DatabaseHandler(getApplicationContext());
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();
		vechtype = getResources().getStringArray(R.array.vech_type);
		bodytype = getResources().getStringArray(R.array.body_type);
		type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						Addvehicle.this);
				builder.setTitle("Vehicle Type");

				builder.setSingleChoiceItems(vechtype, tvech,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								buffKey = which;
								type.setText(vechtype[buffKey]);
								type.setTextColor(getResources().getColor(
										R.color.black));
								int selectedPosition = ((AlertDialog) dialog)
										.getListView().getCheckedItemPosition();

								tvech = buffKey;
								if (type.getText().toString()
										.equalsIgnoreCase("Other")) {
									type_other.setVisibility(View.VISIBLE);
								} else {
									type_other.setVisibility(View.GONE);
								}

								if (type.getText().toString()
										.equalsIgnoreCase("Car")) {
									body.setVisibility(View.VISIBLE);
								} else {
									body.setVisibility(View.GONE);
								}
								btype = true;
								dialog.dismiss();
							}
						}).setCancelable(false);

				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		body.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						Addvehicle.this);
				builder.setTitle("Body Type");

				builder.setSingleChoiceItems(bodytype, tbody,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								buffKey = which;
								body.setText(bodytype[buffKey]);
								body.setTextColor(getResources().getColor(
										R.color.black));
								int selectedPosition = ((AlertDialog) dialog)
										.getListView().getCheckedItemPosition();
								bbody = true;
								tbody = buffKey;
								dialog.dismiss();

							}
						}).setCancelable(false);

				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		make.addTextChangedListener(this);
		model.addTextChangedListener(this);
		reg.addTextChangedListener(this);
		chassis.addTextChangedListener(this);
		engine.addTextChangedListener(this);
		colour.addTextChangedListener(this);
		type_other.addTextChangedListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.addvehicle, menu);
		MenuItem add = menu.findItem(R.id.add);
		add.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				IsInternetPresent = cd.isConnectingToInternet();
				if (IsInternetPresent == false) {
					cd.showNoInternetPopup();
				} else {
					if (make.getText().toString().length() < 2) {

						make.setTextColor(getResources().getColor(R.color.red));
						make.setHintTextColor(getResources().getColor(
								R.color.red));
						bmake = false;
					}
					if (model.getText().toString().length() < 2) {

						model.setTextColor(getResources().getColor(R.color.red));
						model.setHintTextColor(getResources().getColor(
								R.color.red));
						bmodel = false;
					}
					if (!(reg.getText().toString().length() == 10)) {

						reg.setTextColor(getResources().getColor(R.color.red));
						reg.setHintTextColor(getResources().getColor(
								R.color.red));
						breg = false;
					}
					if (colour.getText().toString().length() < 3) {

						colour.setTextColor(getResources()
								.getColor(R.color.red));
						colour.setHintTextColor(getResources().getColor(
								R.color.red));
						bcolour = false;
					}
					if (type.getText().toString().equalsIgnoreCase("Car")

					&& engine.getText().toString().length() < 13) {

						engine.setTextColor(getResources()
								.getColor(R.color.red));
						engine.setHintTextColor(getResources().getColor(
								R.color.red));
						beng = false;
					}
					if (type.getText().toString()
							.equalsIgnoreCase("MotorCycle")
							&& engine.getText().toString().length() < 13) {

						engine.setTextColor(getResources()
								.getColor(R.color.red));
						engine.setHintTextColor(getResources().getColor(
								R.color.red));
						beng = false;
					}
					if (type.getText().toString().equalsIgnoreCase("Car")

					&& chassis.getText().toString().length() < 17) {

						chassis.setTextColor(getResources().getColor(
								R.color.red));
						chassis.setHintTextColor(getResources().getColor(
								R.color.red));
						bchassis = false;
					}
					if (type.getText().toString()
							.equalsIgnoreCase("MotorCycle")
							&& chassis.getText().toString().length() < 17) {

						chassis.setTextColor(getResources().getColor(
								R.color.red));
						chassis.setHintTextColor(getResources().getColor(
								R.color.red));
						bchassis = false;
					}
					if (type_other.getVisibility() == View.VISIBLE
							&& type_other.getText().toString().length() < 3) {

						type_other.setTextColor(getResources().getColor(
								R.color.red));
						type_other.setHintTextColor(getResources().getColor(
								R.color.red));
						btype_other = false;
					}
					if (type.getText().toString().isEmpty()) {

						type.setTextColor(getResources().getColor(R.color.red));
						type.setHintTextColor(getResources().getColor(
								R.color.red));
						btype = false;
					}
					if (body.getText().toString().isEmpty()
							&& body.getVisibility() == View.VISIBLE) {

						type.setTextColor(getResources().getColor(R.color.red));
						type.setHintTextColor(getResources().getColor(
								R.color.red));
						btype = false;
					}

					if (btype == true && bbody == true && btype_other == true
							&& bmake == true && bmodel == true && breg == true
							&& bchassis == true && bcolour == true
							&& beng == true) {
						Log.i("right", "enter");
						addVehicle = new add().execute();

					}

				}

				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	private class add extends AsyncTask<Void, Void, Void> {
		String success, mess, response;
		String vid, typevalue;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Addvehicle.this);
			pDialog.setMessage("Register");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(addVehicle_url);
			System.out.println(addVehicle_url);
			JSONArray jsonMainArr;
			JSONObject json = new JSONObject();
			try {
				info.device();
				info.showInfo(getApplicationContext());
				json.put("userId", info.user_id);
				if (type.getText().toString().equalsIgnoreCase("other")) {
					typevalue = type_other.getText().toString();

				} else {
					typevalue = type.getText().toString();

				}
				json.put("vehicleType", typevalue);
				json.put("vehicleMake", make.getText().toString());
				json.put("vehicleModel", model.getText().toString());
				json.put("vehicleBodyType", body.getText().toString());
				json.put("registrationSerialNo", reg.getText().toString());
				json.put("engineNo", engine.getText().toString());
				json.put("vinChassisNo", chassis.getText().toString());
				json.put("colour", colour.getText().toString());
				json.put("uniqueFeatures", acc.getText().toString());
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
				vid = jsonMainArr.getJSONObject(0).getString("vehicle_id");

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

						VehicleData data = new VehicleData(
								Integer.valueOf(vid), typevalue, make.getText()
										.toString(),
								model.getText().toString(), body.getText()
										.toString(), engine.getText()
										.toString(), chassis.getText()
										.toString(), colour.getText()
										.toString(), acc.getText().toString(),
								reg.getText().toString(), "", "", "", "", "");
						db.insertvehicleData(data);
						Intent next = new Intent(getApplicationContext(),
								VehicleProfile.class);
						next.putExtra("id", vid);
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
								Addvehicle.this).create();
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
								Addvehicle.this).create();
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

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		make.setTextColor(getResources().getColor(R.color.black));
		model.setTextColor(getResources().getColor(R.color.black));
		reg.setTextColor(getResources().getColor(R.color.black));
		engine.setTextColor(getResources().getColor(R.color.black));
		colour.setTextColor(getResources().getColor(R.color.black));
		chassis.setTextColor(getResources().getColor(R.color.black));
		type_other.setTextColor(getResources().getColor(R.color.black));
		bmake = true;
		bmodel = true;
		breg = true;
		beng = true;
		bcolour = true;
		bchassis = true;
		btype_other = true;
		bbody = true;

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void onBackPressed() {
		Intent next = new Intent(getApplicationContext(), ProfileScreen.class);
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
					ProfileScreen.class);

			startActivity(next);
			finish();
			break;
		}
		return true;
	}

}