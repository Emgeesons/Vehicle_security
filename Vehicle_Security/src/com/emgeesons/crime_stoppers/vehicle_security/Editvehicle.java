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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.google.android.gms.internal.el;

public class Editvehicle extends BaseActivity implements TextWatcher {
	TextView type, body;
	EditText type_other, make, model, reg, engine, chassis, colour, acc, state;
	boolean btype, bbody, btype_other, bmake, bmodel, breg, beng, bchassis,
			bcolour, bacc, bstate;
	static int buffKey = 0;
	static CharSequence[] vechtype, bodytype;
	int tvech, tbody;
	Connection_Detector cd = new Connection_Detector(this);
	Boolean IsInternetPresent;
	String editVehicle_url = Data.url + "editVehicle.php";
	private AsyncTask<Void, Void, Void> editVehicle;
	ProgressDialog pDialog;
	Data info;
	DatabaseHandler db;
	SQLiteDatabase dbb;
	String id;
	GPSTracker gps;
	double LATITUDE, LONGITUDE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.addvehicle);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'> Edit Details</font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.app_icon);
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
		state = (EditText) findViewById(R.id.state);
		info = new Data();
		db = new DatabaseHandler(getApplicationContext());
		gps = new GPSTracker(Editvehicle.this);
		if (gps.canGetLocation()) {
			LATITUDE = gps.getLatitude();
			LONGITUDE = gps.getLongitude();

		} else {
			LATITUDE = 0.0;
			LONGITUDE = 0.0;
		}
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();

		Intent intent = getIntent();
		id = intent.getStringExtra("id");
		// id = "24";

		info = new Data();
		info.vehicleInfo(getApplicationContext(), id);
		// type.setText(info.vmodel);
		// type.setText(info.type);
		make.setText(info.make);
		model.setText(info.vmodel);
		engine.setText(info.eng);
		body.setText(info.body);
		chassis.setText(info.vin);
		colour.setText(info.color);
		acc.setText(info.acc);
		reg.setText(info.reg);
		state.setText(info.state);
		bmake = true;
		bmodel = true;
		breg = true;
		beng = true;
		bcolour = true;
		bchassis = true;
		btype_other = true;
		bbody = true;
		bstate = true;
		vechtype = getResources().getStringArray(R.array.vech_type);
		bodytype = getResources().getStringArray(R.array.body_type);
		if (info.type.equalsIgnoreCase("Bicycle")) {
			type_other.setVisibility(View.GONE);
			body.setVisibility(View.GONE);
			tvech = 0;
		} else if (info.type.equalsIgnoreCase("Car")) {
			type_other.setVisibility(View.GONE);
			tvech = 1;
		} else if (info.type.equalsIgnoreCase("MotorCycle")) {
			type_other.setVisibility(View.GONE);
			body.setVisibility(View.GONE);
			tvech = 2;
		} else {
			type_other.setVisibility(View.VISIBLE);
			type_other.setText(info.type);
			body.setVisibility(View.GONE);
			tvech = 3;

		}
		type.setText(vechtype[tvech]);
		// else {
		// type_other.setVisibility(View.VISIBLE);
		// body.setVisibility(View.GONE);
		// tvech = 4;
		// }
		if (body.getVisibility() == View.VISIBLE) {
			btype = true;
			if (info.body.equalsIgnoreCase("Micro/Compact")) {
				tbody = 0;
			} else if (info.type.equalsIgnoreCase("Convertible/Coupe")) {
				tbody = 1;
			} else if (info.type.equalsIgnoreCase("Hatchback")) {
				tbody = 2;
			} else if (info.type.equalsIgnoreCase("Seden")) {
				tbody = 3;

			} else if (info.type.equalsIgnoreCase("Station Wagon")) {
				tbody = 4;

			} else if (info.type.equalsIgnoreCase("SUV")) {
				tbody = 5;

			} else if (info.type.equalsIgnoreCase("People Mover")) {
				tbody = 6;

			} else if (info.type.equalsIgnoreCase("Utility/Cab Chassis")) {
				tbody = 7;

			} else if (info.type.equalsIgnoreCase("Van")) {
				tbody = 8;

			} else {
				tbody = 9;
			}

		} else {
			btype = true;
		}

		type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						Editvehicle.this);
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
									make.setHint("make");
									model.setHint("model");
									state.setVisibility(View.VISIBLE);
									state.setHint("  state of registration");
									reg.setHint("registration no");
								} else {
									type_other.setVisibility(View.GONE);
								}

								if (type.getText().toString()
										.equalsIgnoreCase("Bicycle")) {
									reg.setHint("serial no");
									make.setHint("make");
									model.setHint("model");
									state.setVisibility(View.GONE);
								}

								if (type.getText().toString()
										.equalsIgnoreCase("Car")
										|| type.getText().toString()
												.equalsIgnoreCase("MotorCycle")) {
									reg.setHint("registration no*(max 10 chars)");
									state.setVisibility(View.VISIBLE);
									state.setHint("  state of registration*(max 3 chars) ");
								}
								if (type.getText().toString()
										.equalsIgnoreCase("Car")) {
									body.setVisibility(View.VISIBLE);
								} else {
									body.setText("");
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
						Editvehicle.this);
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
		state.addTextChangedListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.editvehicle, menu);
		MenuItem add = menu.findItem(R.id.save);
		add.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				IsInternetPresent = cd.isConnectingToInternet();
				if (IsInternetPresent == false) {
					cd.showNoInternetPopup();
				} else {
					if ((type.getText().toString().equalsIgnoreCase("Car") || type
							.getText().toString()
							.equalsIgnoreCase("MotorCycle"))
							&& make.getText().toString().length() < 2) {

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
					if ((type.getText().toString().equalsIgnoreCase("Car") || type
							.getText().toString()
							.equalsIgnoreCase("MotorCycle"))

							&& (reg.getText().toString().trim().length() > 10)) {

						reg.setTextColor(getResources().getColor(R.color.red));
						reg.setHintTextColor(getResources().getColor(
								R.color.red));
						breg = false;
					}
					if ((type.getText().toString().equalsIgnoreCase("Car") || type
							.getText().toString()
							.equalsIgnoreCase("MotorCycle"))

							&& (reg.getText().toString().trim().isEmpty())) {

						reg.setTextColor(getResources().getColor(R.color.red));
						reg.setHintTextColor(getResources().getColor(
								R.color.red));
						breg = false;
					}

					if ((type.getText().toString().equalsIgnoreCase("Other") && !reg
							.getText().toString().isEmpty())
							&& (reg.getText().toString().length() > 13)) {

						reg.setTextColor(getResources().getColor(R.color.red));
						reg.setHintTextColor(getResources().getColor(
								R.color.red));
						breg = false;
					}
					if ((type.getText().toString().equalsIgnoreCase("Bicycle") && !reg
							.getText().toString().isEmpty())
							&& (reg.getText().toString().length() > 13)) {

						reg.setTextColor(getResources().getColor(R.color.red));
						reg.setHintTextColor(getResources().getColor(
								R.color.red));
						breg = false;
					}

					if ((type.getText().toString().equalsIgnoreCase("Car") || type
							.getText().toString()
							.equalsIgnoreCase("MotorCycle"))
							&& (state.getText().toString().length() > 4)) {

						state.setTextColor(getResources().getColor(R.color.red));
						state.setHintTextColor(getResources().getColor(
								R.color.red));
						bstate = false;
					}
					if ((type.getText().toString().equalsIgnoreCase("Car") || type
							.getText().toString()
							.equalsIgnoreCase("MotorCycle"))
							&& (state.getText().toString().isEmpty())) {

						state.setTextColor(getResources().getColor(R.color.red));
						state.setHintTextColor(getResources().getColor(
								R.color.red));
						bstate = false;
					}
					if ((type.getText().toString().equalsIgnoreCase("Other"))
							&& !state.getText().toString().trim().isEmpty()
							&& (state.getText().toString().length() > 4)) {

						state.setTextColor(getResources().getColor(R.color.red));
						state.setHintTextColor(getResources().getColor(
								R.color.red));
						bstate = false;
					}
					if ((type.getText().toString().equalsIgnoreCase("Other") && !state
							.getText().toString().isEmpty())
							&& (state.getText().toString().length() > 13)) {

						state.setTextColor(getResources().getColor(R.color.red));
						state.setHintTextColor(getResources().getColor(
								R.color.red));
						bstate = false;
					}

					if (colour.getText().toString().length() < 3) {

						colour.setTextColor(getResources()
								.getColor(R.color.red));
						colour.setHintTextColor(getResources().getColor(
								R.color.red));
						bcolour = false;
					}
					if (!engine.getText().toString().isEmpty()

					&& engine.getText().toString().length() < 13) {

						engine.setTextColor(getResources()
								.getColor(R.color.red));
						engine.setHintTextColor(getResources().getColor(
								R.color.red));
						beng = false;
					}

					if (!chassis.getText().toString().isEmpty()

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

						body.setTextColor(getResources().getColor(R.color.red));
						body.setHintTextColor(getResources().getColor(
								R.color.red));
						bbody = false;
					}

					if (btype == true && bbody == true && btype_other == true
							&& bmake == true && bmodel == true && breg == true
							&& bcolour == true && beng == true
							&& bchassis == true && bstate == true) {
						editVehicle = new edit().execute();

					}

				}

				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	private class edit extends AsyncTask<Void, Void, Void> {
		String success, mess, response, types;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Editvehicle.this);
			pDialog.setMessage("Updating Vehicle Info...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(editVehicle_url);
			System.out.println(editVehicle_url);
			JSONArray jsonMainArr;
			JSONObject json = new JSONObject();
			try {

				info.device();
				info.showInfo(getApplicationContext());
				json.put("userId", info.user_id);
				json.put("vehicleId", id);
				json.put("pin", info.pin);
				if (type.getText().toString().equalsIgnoreCase("other")) {

					types = type_other.getText().toString();
					// json.put("vehicleType",);
				} else {
					types = type.getText().toString();

				}
				json.put("vehicleType", types);
				json.put("vehicleMake", make.getText().toString());
				json.put("vehicleModel", model.getText().toString());
				json.put("vehicleBodyType", body.getText().toString());
				json.put("registrationSerialNo", reg.getText().toString());
				json.put("engineNo", engine.getText().toString());
				json.put("vinChassisNo", chassis.getText().toString());
				json.put("state", state.getText().toString());
				json.put("colour", colour.getText().toString());
				json.put("uniqueFeatures", acc.getText().toString());
				json.put("make", info.manufacturer);
				json.put("os", "Android" + " " + info.Version);
				json.put("model", info.model);

				json.put("latitude", LATITUDE);
				json.put("longitude", LONGITUDE);

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
						dbbb.execSQL("UPDATE vehicle_info SET vehicle_id = '"
								+ id + "',vehicle_type = '" + types
								+ "',vehicle_make = '"
								+ make.getText().toString()
								+ "',vehicle_model = '"
								+ model.getText().toString()
								+ "',vehicle_body = '"
								+ body.getText().toString()
								+ "',vehicle_eng = '"
								+ engine.getText().toString()
								+ "',vehicle_ch = '"
								+ chassis.getText().toString()
								+ "',vehicle_colour = '"
								+ colour.getText().toString()
								+ "',vehicle_acc = '"
								+ acc.getText().toString()
								+ "',vehicle_reg = '"
								+ reg.getText().toString()
								+ "',vehicle_state = '"
								+ state.getText().toString()
								+ "'WHERE vehicle_id='" + id + "'");

						dbbb.execSQL("UPDATE Vehicle_park SET type = '" + types
								+ "',model = '" + model.getText().toString()
								+ "'WHERE vid='" + id + "'");

						Intent next = new Intent(getApplicationContext(),
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
								Editvehicle.this).create();
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
								Editvehicle.this).create();
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
		state.setTextColor(getResources().getColor(R.color.black));
		bmake = true;
		bmodel = true;
		breg = true;
		beng = true;
		bcolour = true;
		bchassis = true;
		btype_other = true;
		bbody = true;
		bstate = true;

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon @ action bar clicked; go home
			Intent back = new Intent(getApplicationContext(),
					VehicleProfile.class);
			back.putExtra("id", id);
			startActivity(back);
			finish();
			break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		Intent back = new Intent(getApplicationContext(), VehicleProfile.class);
		back.putExtra("id", id);
		startActivity(back);
		finish();
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.addvehicle;
	}

}
