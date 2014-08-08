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

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.google.android.gms.drive.internal.e;

public class Addvehicle extends BaseActivity implements TextWatcher {
	TextView type, body;
	EditText type_other, make, model, reg, engine, chassis, colour, acc, state;
	boolean btype, bbody, btype_other, bmake, bmodel, bcolour, bacc, breg,
			beng, bchassis, bstate;
	static int buffKey = 0;
	static CharSequence[] vechtype, bodytype;
	int tvech = -1, tbody = -1;
	Connection_Detector cd = new Connection_Detector(this);
	Boolean IsInternetPresent;
	String addVehicle_url = Data.url + "addVehicle.php";
	private AsyncTask<Void, Void, Void> addVehicle;
	ProgressDialog pDialog;
	Data info;
	DatabaseHandler db;
	SQLiteDatabase dbb;
	int typevalue;
	GPSTracker gps;
	String reponse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.addvehicle);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'> Add Vehicle</font>"));
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
									make.setHint("make");
									model.setHint("model*");
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
									body.setVisibility(View.GONE);
								}
								btype = true;
								dialog.dismiss();
							}
						}).setCancelable(false);

				AlertDialog alert = builder.create();
				alert.setCancelable(false);
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
				alert.setCancelable(false);
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
		inflater.inflate(R.menu.addvehicle, menu);
		MenuItem add = menu.findItem(R.id.add);
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
					if ((type.getText().toString().equalsIgnoreCase("Car") || type
							.getText().toString()
							.equalsIgnoreCase("MotorCycle"))
							|| type.getText().toString()
									.equalsIgnoreCase("Other")
							&& model.getText().toString().length() < 2) {

						model.setTextColor(getResources().getColor(R.color.red));
						model.setHintTextColor(getResources().getColor(
								R.color.red));

						bmodel = false;
					}

					if ((type.getText().toString().equalsIgnoreCase("Bicycle"))
							&& (model.getText().toString().trim().isEmpty() && make
									.getText().toString().trim().isEmpty())) {

						model.setTextColor(getResources().getColor(R.color.red));
						model.setHintTextColor(getResources().getColor(
								R.color.red));
						make.setTextColor(getResources().getColor(R.color.red));
						make.setHintTextColor(getResources().getColor(
								R.color.red));
						bmake = false;
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
			pDialog.setMessage("Adding Vehicle");
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

			HttpPost httppost = new HttpPost(addVehicle_url);
			@SuppressWarnings("deprecation")
			MultipartEntity mpEntity = null;
			mpEntity = new MultipartEntity();
			try {
				gps = new GPSTracker(Addvehicle.this);
				info.device();
				info.showInfo(getApplicationContext());
				if (type.getText().toString().equalsIgnoreCase("other")) {
					typevalue = type_other.getText().toString();

				} else {
					typevalue = type.getText().toString();

				}
				mpEntity.addPart("vehicleBodyType", new StringBody(body
						.getText().toString()));
				mpEntity.addPart("registrationSerialNo", new StringBody(reg
						.getText().toString()));
				mpEntity.addPart("engineNo", new StringBody(engine.getText()
						.toString()));

				mpEntity.addPart("vinChassisNo", new StringBody(chassis
						.getText().toString()));
				mpEntity.addPart("colour", new StringBody(colour.getText()
						.toString()));
				mpEntity.addPart("uniqueFeatures", new StringBody(acc.getText()
						.toString()));
				mpEntity.addPart("state", new StringBody(state.getText()
						.toString()));
				mpEntity.addPart("vehicleType", new StringBody(typevalue));
				mpEntity.addPart("vehicleMake", new StringBody(make.getText()
						.toString()));
				mpEntity.addPart("vehicleModel", new StringBody(model.getText()
						.toString()));
				mpEntity.addPart("pin", new StringBody(info.pin));
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
					vid = jsonMainArr.getJSONObject(0).getString("vehicle_id");

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

							VehicleData data = new VehicleData(
									Integer.valueOf(vid), typevalue, make
											.getText().toString(), model
											.getText().toString(), body
											.getText().toString(), engine
											.getText().toString(), chassis
											.getText().toString(), colour
											.getText().toString(), acc
											.getText().toString(), reg
											.getText().toString(), "", "", "",
									"", "", "", state.getText().toString());
							// check = make
							ParkingData datas = new ParkingData(vid, model
									.getText().toString(), "", "", "", make
									.getText().toString(), typevalue);
							db.inserparkData(datas);
							db.insertvehicleData(data);
							// SQLiteDatabase dbbb = db.getReadableDatabase();
							// dbbb.execSQL("UPDATE vehicle_info SET vehicle_model = '"
							// + model.getText().toString()
							//
							// + "'WHERE vehicle_id='" + "5" + "'");

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
							Dialog.setButton(DialogInterface.BUTTON_NEUTRAL,
									"OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
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

	@Override
	protected int getLayoutResourceId() {
		return R.layout.addvehicle;
	}

}
