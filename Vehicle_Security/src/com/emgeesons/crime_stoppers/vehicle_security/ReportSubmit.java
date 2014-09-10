package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;

public class ReportSubmit extends BaseActivity {

	SharedPreferences atPrefs;
	SharedPreferences sharedpreferences;
	Button report;
	TextView call;
	private AsyncTask<Void, Void, Void> vinfo;
	String myUpdates_url = Data.url + "reportSummary.php";
	Connection_Detector cd;
	boolean IsInternetPresent;
	ProgressDialog pDialog;
	GPSTracker gps;
	String reponse;
	double LATITUDE, LONGITUDE;
	String vid;
	JSONArray jsonMainArr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.report_submit);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'>Report Submitted</font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.app_icon);
		atPrefs = PreferenceManager
				.getDefaultSharedPreferences(ReportSubmit.this);
		sharedpreferences = getSharedPreferences(Data.MyPREFERENCES,
				Context.MODE_PRIVATE);
		cd = new Connection_Detector(ReportSubmit.this);

		atPrefs.edit().remove(FilenewReport.fphoto).commit();
		atPrefs.edit().remove(FilenewReport.tphoto).commit();
		report = (Button) findViewById(R.id.rreport);
		call = (TextView) findViewById(R.id.textView6);
		call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent call = new Intent(Intent.ACTION_CALL);
				call.setData(Uri.parse("tel:" + "131444"));
				startActivity(call);
			}
		});
		report.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				IsInternetPresent = cd.isConnectingToInternet();
				if (IsInternetPresent == false) {
					cd.showNoInternetPopup();
				} else {
					vinfo = new getinfo().execute();
				}

			}
		});
		gps = new GPSTracker(ReportSubmit.this);
		if (gps.canGetLocation()) {
			LATITUDE = gps.getLatitude();
			LONGITUDE = gps.getLongitude();

		} else {
			LATITUDE = 0;
			LONGITUDE = 0;
		}
		vid = sharedpreferences.getString(Data.vid, "notcall");

	}

	private class getinfo extends AsyncTask<Void, Void, Void> {
		String success, mess, response, qus;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ReportSubmit.this);
			pDialog.setMessage("Updating ");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}

		@SuppressWarnings("deprecation")
		@Override
		protected Void doInBackground(Void... params) {
			HttpEntity resEntity;
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(
					CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			HttpPost httppost = new HttpPost(myUpdates_url);
			@SuppressWarnings("deprecation")
			MultipartEntity mpEntity = null;
			mpEntity = new MultipartEntity();

			try {
				info.device();
				info.showInfo(ReportSubmit.this);

				mpEntity.addPart("vehicleId", new StringBody(vid));
				mpEntity.addPart("latitude",
						new StringBody(String.valueOf(LATITUDE)));
				mpEntity.addPart("longitude",
						new StringBody(String.valueOf(LONGITUDE)));
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

				} catch (JSONException e) {
					System.out.println("JSONException");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (success.equals("success")) {

					runOnUiThread(new Runnable() {

						public void run() {
							Intent next = new Intent(getApplicationContext(),
									Reportsummary.class);
							next.putExtra("data", jsonMainArr.toString());
							startActivity(next);
//							finish();
						}
					});
				}
				// response failure
				else if (success.equals("failure")) {

					runOnUiThread(new Runnable() {

						public void run() {
							final AlertDialog Dialog = new AlertDialog.Builder(
									ReportSubmit.this).create();
							Dialog.setTitle("Error");
							Dialog.setIcon(R.drawable.ic_action_error);
							Dialog.setMessage(mess);
							Dialog.setButton(DialogInterface.BUTTON_NEUTRAL,
									"OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
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
									ReportSubmit.this).create();
							Dialog.setTitle("Error");
							Dialog.setMessage(mess);
							Dialog.setButton(DialogInterface.BUTTON_NEUTRAL,
									"OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									});
							Dialog.setCancelable(true);
							Dialog.show();
						}
					});

				}
			} else {
				cd.showNoInternetPopup();
			}

			return null;

		}

		@Override
		protected void onPostExecute(Void notUsed) {
			pDialog.dismiss();
		}
	}

	@Override
	public void onBackPressed() {

		Intent next = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(next);
		Editor editor = sharedpreferences.edit();

		editor.putString(Data.vid, "notcall");
		editor.putString(Data.p1, "");
		editor.putString(Data.p2, "");
		editor.putString(Data.p3, "");
		editor.putString(Data.comm, "");

		editor.commit();
		finish();
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			// app icon @ action bar clicked; go home
			Intent next = new Intent(getApplicationContext(),
					MainActivity.class);
			Editor editor = sharedpreferences.edit();

			editor.putString(Data.vid, "notcall");
			editor.putString(Data.p1, "");
			editor.putString(Data.p2, "");
			editor.putString(Data.p3, "");
			editor.putString(Data.comm, "");

			editor.commit();
			startActivity(next);
			finish();

			break;
		}
		return true;
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.report_submit;
	}

}
