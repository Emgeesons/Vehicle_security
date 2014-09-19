package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

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
import android.database.sqlite.SQLiteDatabase;
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
	String dir, reponse;
	downlaod d;
	JSONArray jsonVehicleArr;
	String profile_url = Data.url + "getProfile.php";
	SharedPreferences atPrefs;
	GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'>My Vehicles </font>"));
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
			reg.setText("Reg No:-" + " "
					+ vehicles.get(position).getvehicle_reg());
			if (vehicles.get(position).getvehicle_type()
					.equalsIgnoreCase("Car")) {
				icon.setImageResource(R.drawable.ic_car);
			} else if (vehicles.get(position).getvehicle_type()
					.equalsIgnoreCase("Bicycle")) {
				reg.setText("Serial No:-" + " "
						+ vehicles.get(position).getvehicle_reg());
				icon.setImageResource(R.drawable.ic_cycle);
			} else if (vehicles.get(position).getvehicle_type()
					.equalsIgnoreCase("Motorcycle")) {
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
				vacc, vcol, veng, vch, pic1, pic2, pic3, vinsnum, state;
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

		@SuppressWarnings("deprecation")
		@Override
		protected Void doInBackground(Void... params) {

			JSONArray jsonMainArr;
			HttpEntity resEntity;
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(
					CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

			HttpPost httppost = new HttpPost(getVehicle_url);
			@SuppressWarnings("deprecation")
			MultipartEntity mpEntity = null;
			mpEntity = new MultipartEntity();
			try {
				gps = new GPSTracker(VehicleInfo.this);
				info.device();
				info.showInfo(getApplicationContext());

				if (gps.canGetLocation()) {
					double LATITUDE = gps.getLatitude();
					double LONGITUDE = gps.getLongitude();
					mpEntity.addPart("latitude",
							new StringBody(String.valueOf(LATITUDE)));
					mpEntity.addPart("longitude",
							new StringBody(String.valueOf(LONGITUDE)));

				} else {
					mpEntity.addPart("latitude",
							new StringBody(String.valueOf(0)));
					mpEntity.addPart("longitude",
							new StringBody(String.valueOf(0)));
				}

				mpEntity.addPart("pin", new StringBody(info.pin));
				mpEntity.addPart("vehicleId", new StringBody(id));

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

			try {
				resEntity = response.getEntity();
			} catch (Exception e) {
				// TODO: handle exception
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
					jsonMainArr = profile.getJSONArray("response");
					success = profile.getString("status");
					mess = profile.getString("message");
					vid = jsonMainArr.getJSONObject(0).getInt("vehicle_id");
					vtype = jsonMainArr.getJSONObject(0).getString(
							"vehicle_type");
					vmake = jsonMainArr.getJSONObject(0).getString("make");
					vmodel = jsonMainArr.getJSONObject(0).getString("model");
					reg = jsonMainArr.getJSONObject(0).getString(
							"registration_serial_no");
					vstatus = jsonMainArr.getJSONObject(0).getString("status");
					vbody = jsonMainArr.getJSONObject(0).getString("body_type");
					veng = jsonMainArr.getJSONObject(0).getString("engine_no");
					vch = jsonMainArr.getJSONObject(0).getString(
							"vin_chasis_no");
					vcol = jsonMainArr.getJSONObject(0).getString("colour");
					vacc = jsonMainArr.getJSONObject(0).getString(
							"accessories_unique_features");
					vins = jsonMainArr.getJSONObject(0).getString(
							"insurance_company_name");
					vpol = jsonMainArr.getJSONObject(0).getString(
							"insurance_policy_no");
					vexp = jsonMainArr.getJSONObject(0).getString(
							"insurance_expiry_date");
					vinsnum = jsonMainArr.getJSONObject(0).getString(
							"insurance_company_number");
					pic1 = jsonMainArr.getJSONObject(0).getString("photo_1");
					pic2 = jsonMainArr.getJSONObject(0).getString("photo_2");
					pic3 = jsonMainArr.getJSONObject(0).getString("photo_3");
					state = jsonMainArr.getJSONObject(0).getString("state");

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
							String[] date = vexp.split("\\s+");
							SQLiteDatabase dbbb = db.getReadableDatabase();
							dbbb.execSQL("UPDATE Vehicle_info SET vehicle_type = '"
									+ vtype
									+ "',vehicle_make = '"
									+ vmake
									+ "',vehicle_model = '"
									+ vmodel
									+ "',vehicle_body = '"
									+ vbody
									+ "',vehicle_eng = '"
									+ veng
									+ "',vehicle_ch = '"
									+ vch
									+ "',vehicle_colour = '"
									+ vcol
									+ "',vehicle_acc = '"
									+ vacc
									+ "',vehicle_reg = '"
									+ reg
									+ "',vehicle_insname = '"
									+ vins
									+ "',vehicle_insno = '"
									+ vpol
									+ "',vehicle_insexp = '"
									+ date[0]
									+ "',vehicle_status = '"
									+ vstatus
									+ "',vehicle_insnum = '"
									+ vinsnum
									+ "',vehicle_state = '"
									+ state
									+ "' WHERE vehicle_id='" + id + "'");

							Thread thread = new Thread(new Runnable() {
								@Override
								public void run() {
									sdRoot = Environment
											.getExternalStorageDirectory();
									dir = "My Wheel/" + id + "/";

									folder = new File(sdRoot + "/" + dir);
									boolean success = true;
									// check photo is exists or not
									if (!folder.exists()) {
										success = folder.mkdir();
										if (!pic1.isEmpty() || !pic2.isEmpty()
												|| !pic3.isEmpty()) {
											String nopic[] = { pic1, pic2, pic3 };

											for (int i = 0; i < nopic.length; i++) {
												int pos = nopic[i]
														.lastIndexOf("/");
												String photoname1 = nopic[i]
														.substring(pos + 1);

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
									} else {

										if (!pic1.isEmpty() || !pic2.isEmpty()
												|| !pic3.isEmpty()) {
											String nopic[] = { pic1, pic2, pic3 };

											for (int i = 0; i < nopic.length; i++) {
												int pos = nopic[i]
														.lastIndexOf("/");
												String photoname1 = nopic[i]
														.substring(pos + 1);
												File f = new File(sdRoot, dir
														+ photoname1);
												if (f.exists()) {
												} else {

													try {
														Log.i("call", "call"
																+ i);
														d.DownloadFromUrl(
																nopic[i],
																photoname1,
																"/My Wheel/"
																		+ id,
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
									VehicleInfo.this).create();
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
