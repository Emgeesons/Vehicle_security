package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class VehicleInfo extends BaseActivity {
	ListView list;
	List<VehicleData> vehicles;
	DatabaseHandler db;
	SQLiteDatabase dbb;
	Connection_Detector cd = new Connection_Detector(this);
	Boolean IsInternetPresent;
	private AsyncTask<Void, Void, Void> getVehicle;
	String getVehicle_url = Data.url + "getVehicleProfile.php";
	ProgressDialog pDialog;
	Data info;
	String id;
	File sdRoot;
	File folder;
	String dir;
	downlaod d;
	JSONArray jsonVehicleArr;
	String profile_url = Data.url + "getProfile.php";
	SharedPreferences atPrefs;
	GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'>My Vehicle </font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.app_icon);
		// setContentView(R.layout.vehicle_info);
		list = (ListView) findViewById(R.id.listView1);
		info = new Data();
		atPrefs = PreferenceManager
				.getDefaultSharedPreferences(VehicleInfo.this);
		db = new DatabaseHandler(getApplicationContext());
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();
		vehicles = db.getVehicleData();
		d = new downlaod();
		list.setAdapter(new ffAdapter());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.vehicle_info, menu);
		MenuItem add = menu.findItem(R.id.add);
		add.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent next = new Intent(getApplicationContext(),
						Addvehicle.class);
				startActivity(next);
				finish();
				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
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
		Intent back = new Intent(getApplicationContext(), ProfileScreen.class);
		startActivity(back);
		finish();
	}

	// private class getprofile extends AsyncTask<Void, Void, Void> {
	// String success, mess, response;
	// String user_id, fName, lName, email, mobileNumber, dob, gender,
	// licenseNo, street, suburb, postcode, dtModified, fbId, fbToken,
	// cname, cnumber, sques, sans, photourl, photoname, pin, points;
	// int profilecom;
	//
	// // vehicle
	// int vid;
	// String vtype, vmake, vmodel, reg, vstatus;
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// pDialog = new ProgressDialog(VehicleInfo.this);
	// pDialog.setMessage("Updating Info");
	// pDialog.setIndeterminate(false);
	// pDialog.setCancelable(true);
	// pDialog.show();
	// }
	//
	// @Override
	// protected Void doInBackground(Void... params) {
	//
	// DefaultHttpClient httpClient = new DefaultHttpClient();
	// ResponseHandler<String> resonseHandler = new BasicResponseHandler();
	// HttpPost postMethod = new HttpPost(profile_url);
	// JSONArray jsonMainArr;
	//
	// JSONObject json = new JSONObject();
	// try {
	// info.device();
	// info.showInfo(VehicleInfo.this);
	// json.put("userId", info.user_id);
	// json.put("make", info.manufacturer);
	// json.put("os", "Android" + " " + info.Version);
	// json.put("model", info.model);
	// System.out.println("Element1-->" + json);
	// postMethod.setHeader("Content-Type", "application/json");
	// postMethod.setEntity(new ByteArrayEntity(json.toString()
	// .getBytes("UTF8")));
	// String response = httpClient
	// .execute(postMethod, resonseHandler);
	// Log.e("response :", response);
	//
	// JSONObject profile = new JSONObject(response);
	// jsonMainArr = profile.getJSONArray("response");
	// jsonVehicleArr = profile.getJSONArray("vehicles");
	// success = profile.getString("status");
	//
	// mess = profile.getString("message");
	// user_id = jsonMainArr.getJSONObject(0).getString("user_id");
	// fName = jsonMainArr.getJSONObject(0).getString("first_name");
	// lName = jsonMainArr.getJSONObject(0).getString("last_name");
	// email = jsonMainArr.getJSONObject(0).getString("email");
	// mobileNumber = jsonMainArr.getJSONObject(0).getString(
	// "mobile_number");
	// String[] datespilt = jsonMainArr.getJSONObject(0)
	// .getString("dob").split("\\s+");
	// dob = String.valueOf(datespilt[0]);
	// gender = jsonMainArr.getJSONObject(0).getString("gender");
	// licenseNo = jsonMainArr.getJSONObject(0)
	// .getString("license_no");
	// // street = jsonMainArr.getJSONObject(0).getString("street");
	// // suburb = jsonMainArr.getJSONObject(0).getString("suburb");
	// postcode = jsonMainArr.getJSONObject(0).getString("postcode");
	// dtModified = jsonMainArr.getJSONObject(0).getString(
	// "modified_at");
	// fbId = jsonMainArr.getJSONObject(0).getString("fb_id");
	// fbToken = jsonMainArr.getJSONObject(0).getString("fb_token");
	// // cname = jsonMainArr.getJSONObject(0).getString(
	// // "emergency_contact");
	// // cnumber = jsonMainArr.getJSONObject(0).getString(
	// // "emergency_contact_number");
	// sques = jsonMainArr.getJSONObject(0).getString(
	// "security_question");
	// sans = jsonMainArr.getJSONObject(0)
	// .getString("security_answer");
	// profilecom = jsonMainArr.getJSONObject(0).getInt(
	// "profile_completed");
	// pin = jsonMainArr.getJSONObject(0).getString("pin");
	// points = jsonMainArr.getJSONObject(0).getString(
	// "samaritan_points");
	// photourl = jsonMainArr.getJSONObject(0).getString("photo_url");
	// int pos = photourl.lastIndexOf("/");
	// photoname = photourl.substring(pos + 1);
	//
	// // vehicle info
	//
	// } catch (ClientProtocolException e) {
	// System.out.println("ClientProtocolException");
	// e.printStackTrace();
	// } catch (IOException e) {
	// System.out.println("IOException");
	// e.printStackTrace();
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	//
	// if (success.equals("success")) {
	//
	// runOnUiThread(new Runnable() {
	//
	// public void run() {
	// db = new DatabaseHandler(VehicleInfo.this);
	// PersonalData data = new PersonalData(user_id, fName,
	// lName, email, mobileNumber, dob, gender,
	// licenseNo, street, suburb, postcode,
	// dtModified, fbId, fbToken, cname, cnumber, pin,
	// sques, sans, points);
	//
	// db.updateprofileData(data);
	// atPrefs.edit()
	// .putInt(SplashscreenActivity.progress,
	// profilecom).commit();
	//
	// dbb.execSQL("delete from Vehicle_info");
	// for (int i = 0; i < jsonVehicleArr.length(); i++) {
	//
	// try {
	// vid = jsonVehicleArr.getJSONObject(i).getInt(
	// "vehicle_id");
	// vtype = jsonVehicleArr.getJSONObject(i)
	// .getString("vehicle_type");
	// vmake = jsonVehicleArr.getJSONObject(i)
	// .getString("vehicle_make");
	// vmodel = jsonVehicleArr.getJSONObject(i)
	// .getString("vehicle_model");
	// reg = jsonVehicleArr.getJSONObject(i)
	// .getString("registration_serial_no");
	// vstatus = jsonVehicleArr.getJSONObject(i)
	// .getString("vehicle_status");
	//
	// db = new DatabaseHandler(VehicleInfo.this);
	//
	// SQLiteDatabase dbbb = db.getReadableDatabase();
	// dbbb.execSQL("UPDATE Vehicle_info SET vehicle_type = '"
	// + vtype
	// + "',vehicle_make = '"
	// + vmake
	// + "',vehicle_model = '"
	// + vmodel
	// + "',vehicle_reg = '"
	// + reg
	// + "',vehicle_status = '"
	// + vstatus
	// + "' WHERE vehicle_id= '" + vid + "'");
	//
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	//
	// Intent nextscreen = new Intent(VehicleInfo.this,
	// ProfileScreen.class);
	// startActivity(nextscreen);
	//
	// finish();
	//
	// }
	// });
	// }
	// // response failure
	// else if (success.equals("failure")) {
	//
	// runOnUiThread(new Runnable() {
	//
	// public void run() {
	// final AlertDialog Dialog = new AlertDialog.Builder(
	// VehicleInfo.this).create();
	// Dialog.setTitle("Incorrect Email");
	// Dialog.setMessage(mess);
	// Dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog,
	// int which) {
	// pDialog.dismiss();
	// }
	// });
	// Dialog.setCancelable(true);
	// Dialog.show();
	// }
	// });
	//
	// } else if (success.equals("error")) {
	// runOnUiThread(new Runnable() {
	//
	// public void run() {
	// final AlertDialog Dialog = new AlertDialog.Builder(
	// VehicleInfo.this).create();
	// Dialog.setTitle("Error");
	// Dialog.setMessage(mess);
	// Dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog,
	// int which) {
	// pDialog.dismiss();
	// }
	// });
	// Dialog.setCancelable(true);
	// Dialog.show();
	// }
	// });
	//
	// }
	//
	// return null;
	//
	// }
	//
	// @Override
	// protected void onPostExecute(Void notUsed) {
	// pDialog.dismiss();
	//
	// }
	// }

	class ffAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return vehicles.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View vv;
			TextView make, reg, status;
			// final TextView ids;
			ImageView icon, simage;

			if (convertView == null) {
				vv = getLayoutInflater().inflate(R.layout.vehicles, null);
			} else {
				vv = convertView;
			}
			// vv.setBackgroundResource(R.drawable.box_whiteb);
			// ids = (TextView) vv.findViewById(R.id.ids);
			make = (TextView) vv.findViewById(R.id.model);
			reg = (TextView) vv.findViewById(R.id.reg);
			icon = (ImageView) vv.findViewById(R.id.imageView1);
			status = (TextView) vv.findViewById(R.id.status);
			simage = (ImageView) vv.findViewById(R.id.statusimage);
			if (!vehicles.get(position).getstatus().isEmpty()) {
				simage.setVisibility(View.VISIBLE);
				status.setVisibility(View.VISIBLE);
				status.setText(vehicles.get(position).getstatus());
			}

			if (status.getVisibility() == View.VISIBLE) {
				make.setTextColor(getResources().getColor(R.color.yellow));

			}
			// ids.setText(String.valueOf(vehicles.get(position).getvehicle_id()));
			make.setText(vehicles.get(position).getvehicle_make() + " "
					+ vehicles.get(position).getvehicle_model());
			reg.setText(vehicles.get(position).getvehicle_reg());
			if (vehicles.get(position).getvehicle_type()
					.equalsIgnoreCase("Car")) {
				icon.setImageResource(R.drawable.ic_car);
			} else if (vehicles.get(position).getvehicle_type()
					.equalsIgnoreCase("Bicycle")) {
				icon.setImageResource(R.drawable.ic_cycle);
			} else if (vehicles.get(position).getvehicle_type()
					.equalsIgnoreCase("MotorCycle")) {
				icon.setImageResource(R.drawable.ic_bike);
			} else {

				icon.setImageResource(R.drawable.ic_other);

			}

			vv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					getVehicle = new detail().execute();
					id = String.valueOf(vehicles.get(position).getvehicle_id());
					info.vehicleInfo(getApplicationContext(), id);
					// Intent next = new Intent(getApplicationContext(),
					// VehicleProfile.class);
					// next.putExtra("id", String.valueOf(vehicles.get(position)
					// .getvehicle_id()));
					// startActivity(next);
					// finish();

				}
			});
			return vv;
		}

	}

	private class detail extends AsyncTask<Void, Void, Void> {
		String success, mess, response;
		String vtype, vmake, vmodel, reg, vstatus, vbody, vexp, vpol, vins,
				vacc, vcol, veng, vch, pic1, pic2, pic3;
		int vid;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(VehicleInfo.this);
			pDialog.setMessage("Updating Details");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(getVehicle_url);
			System.out.println(getVehicle_url);
			JSONArray jsonMainArr;
			JSONObject json = new JSONObject();
			try {
				gps = new GPSTracker(VehicleInfo.this);
				info.device();
				info.showInfo(getApplicationContext());
				json.put("userId", info.user_id);
				json.put("pin", info.pin);
				json.put("vehicleId", id);
				json.put("make", info.manufacturer);
				json.put("os", "Android" + " " + info.Version);
				json.put("model", info.model);
				if (gps.canGetLocation()) {
					double LATITUDE = gps.getLatitude();
					double LONGITUDE = gps.getLongitude();
					json.put("latitude", LATITUDE);
					json.put("longitude", LONGITUDE);

				} else {
					json.put("latitude", 0);
					json.put("longitude", 0);
				}
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
				vid = jsonMainArr.getJSONObject(0).getInt("vehicle_id");
				vtype = jsonMainArr.getJSONObject(0).getString("vehicle_type");
				vmake = jsonMainArr.getJSONObject(0).getString("make");
				vmodel = jsonMainArr.getJSONObject(0).getString("model");
				reg = jsonMainArr.getJSONObject(0).getString(
						"registration_serial_no");
				vstatus = jsonMainArr.getJSONObject(0).getString("status");
				vbody = jsonMainArr.getJSONObject(0).getString("body_type");
				veng = jsonMainArr.getJSONObject(0).getString("engine_no");
				vch = jsonMainArr.getJSONObject(0).getString("vin_chasis_no");
				vcol = jsonMainArr.getJSONObject(0).getString("colour");
				vacc = jsonMainArr.getJSONObject(0).getString(
						"accessories_unique_features");
				vins = jsonMainArr.getJSONObject(0).getString(
						"insurance_company_name");
				vpol = jsonMainArr.getJSONObject(0).getString(
						"insurance_policy_no");
				vexp = jsonMainArr.getJSONObject(0).getString(
						"insurance_expiry_date");
				pic1 = jsonMainArr.getJSONObject(0).getString("photo_1");
				pic2 = jsonMainArr.getJSONObject(0).getString("photo_2");
				pic3 = jsonMainArr.getJSONObject(0).getString("photo_3");

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
						String[] date = vexp.split("\\s+");
						SQLiteDatabase dbbb = db.getReadableDatabase();
						dbbb.execSQL("UPDATE Vehicle_info SET vehicle_type = '"
								+ vtype + "',vehicle_make = '" + vmake
								+ "',vehicle_model = '" + vmodel
								+ "',vehicle_body = '" + vbody
								+ "',vehicle_eng = '" + veng
								+ "',vehicle_ch = '" + vch
								+ "',vehicle_colour = '" + vcol
								+ "',vehicle_acc = '" + vacc
								+ "',vehicle_reg = '" + reg
								+ "',vehicle_insname = '" + vins
								+ "',vehicle_insno = '" + vpol
								+ "',vehicle_insexp = '" + date[0]
								+ "',vehicle_status = '" + vstatus
								+ "' WHERE vehicle_id='" + id + "'");

						Thread thread = new Thread(new Runnable() {
							@Override
							public void run() {
								sdRoot = Environment
										.getExternalStorageDirectory();
								dir = "My Wheel/" + id + "/";

								folder = new File(sdRoot + "/" + dir);
								boolean success = true;

								if (!folder.exists()) {
									success = folder.mkdir();
									if (!pic1.isEmpty() || !pic2.isEmpty()
											|| !pic3.isEmpty()) {
										String nopic[] = { pic1, pic2, pic3 };

										for (int i = 0; i < nopic.length; i++) {
											int pos = nopic[i].lastIndexOf("/");
											String photoname1 = nopic[i]
													.substring(pos + 1);

											try {
												Log.i("call", "call" + i);
												d.DownloadFromUrl(nopic[i],
														photoname1,
														"/My Wheel/" + id, dir);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								} else {

									if (!pic1.isEmpty() || !pic2.isEmpty()
											|| !pic3.isEmpty()) {
										String nopic[] = { pic1, pic2, pic3 };

										for (int i = 0; i < nopic.length; i++) {
											int pos = nopic[i].lastIndexOf("/");
											String photoname1 = nopic[i]
													.substring(pos + 1);
											File f = new File(sdRoot, dir
													+ photoname1);
											if (f.exists()) {
											} else {

												try {
													Log.i("call", "call" + i);
													d.DownloadFromUrl(nopic[i],
															photoname1,
															"/My Wheel/" + id,
															dir);
												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										}
									}

								}

							}

						});
						thread.start();

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
								VehicleInfo.this).create();
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
								VehicleInfo.this).create();
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
	protected int getLayoutResourceId() {
		// TODO Auto-generated method stub
		return R.layout.vehicle_info;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		db.close();
		super.onPause();
	}
}
