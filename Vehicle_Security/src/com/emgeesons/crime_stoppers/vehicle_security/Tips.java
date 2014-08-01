package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.actionbarsherlock.view.MenuItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class Tips extends BaseActivity {
	TextView addresss, rates, tip;
	RatingBar ratebar;
	double LATITUDE, LONGITUDE;
	String rate, address, feedback, date, vid, tips;
	List<TipData> tipdata;
	List<TipData> labels = new ArrayList<TipData>();
	ListView data;
	private int visibleThreshold = 5;
	private boolean loading = true;
	private int previousTotal = 0;
	private BaseAdapter mListAdapter;
	Boolean IsInternetPresent;
	Connection_Detector cd;
	private AsyncTask<Void, Void, Void> vinfo;
	String tip_url = Data.url + "getParkingTips.php";
	ProgressDialog pDialog;
	Data info;
	GPSTracker gps;
	int size = 0;
	SharedPreferences atPrefs;
	JSONArray jsonMainArr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.tips);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'>Rating & Tips</font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.app_icon);
		cd = new Connection_Detector(Tips.this);
		info = new Data();
		atPrefs = PreferenceManager.getDefaultSharedPreferences(Tips.this);
		Intent intent = getIntent();
		rate = intent.getStringExtra("Rate");
		address = intent.getStringExtra("Address");
		vid = intent.getStringExtra("vid");
		tips = intent.getStringExtra("notip");
		tip = (TextView) findViewById(R.id.tip);
		addresss = (TextView) findViewById(R.id.address);
		rates = (TextView) findViewById(R.id.rate);
		ratebar = (RatingBar) findViewById(R.id.ratebar);
		IsInternetPresent = cd.isConnectingToInternet();
		if (IsInternetPresent == false) {
			cd.showNoInternetPopup();
		} else {
			vinfo = new getTip().execute();
		}
		addresss.setText(address);
		rates.setText(rate);
		tip.setText(tips + " " + "tips for this location");
		ratebar.setRating(Float.valueOf(rate));
		data = (ListView) findViewById(R.id.data);
		gps = new GPSTracker(Tips.this);
		if (gps.canGetLocation()) {
			LATITUDE = gps.getLatitude();
			LONGITUDE = gps.getLongitude();

		} else {
			LATITUDE = 0;
			LONGITUDE = 0;
		}
		data.setOnScrollListener(new EndlessScrollListener());
	}

	private class getTip extends AsyncTask<Void, Void, Void> {
		String success, mess;
		String feedback, time;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (size == 0) {
				pDialog = new ProgressDialog(Tips.this);
				pDialog.setMessage("Loading Tips");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(true);
				pDialog.show();
			}

		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(tip_url);
			System.out.println(tip_url);

			JSONObject json = new JSONObject();
			try {
				info.device();
				info.showInfo(Tips.this);

				if (!atPrefs.getBoolean(info.checkllogin, true)) {
					json.put("userId", info.user_id);
					json.put("vehicleId", vid);
					json.put("pin", info.pin);
				} else {

					json.put("userId", 0);
					json.put("vehicleId", 0);
					//
					json.put("pin", "0" + "0" + "0" + "0");
				}
				json.put("noTips", size);

				json.put("latitude", LATITUDE);
				json.put("longitude", LONGITUDE);

				// json.put("vehicleId", vid);
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
				tipdata = getContactData();
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
						if (size == 0) {
							mListAdapter = new listAdapter();
							data.setAdapter(mListAdapter);
							visibleThreshold = 5;
							previousTotal = 0;
							loading = true;
							pDialog.dismiss();
						} else {
							mListAdapter.notifyDataSetChanged();

						}
					}
				});
			}

			// response failure
			else if (success.equals("failure")) {

				runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								Tips.this).create();
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
								Tips.this).create();
						Dialog.setTitle("Error");
						Dialog.setIcon(R.drawable.ic_action_error);
						Dialog.setMessage(mess);
						Dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
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

	public List<TipData> getContactData() throws JSONException {
		// labels = new ArrayList<TestimonialData>();

		for (int i = 0; i < jsonMainArr.length(); i++) {
			String feedback = jsonMainArr.getJSONObject(i)
					.getString("feedback");

			String date = jsonMainArr.getJSONObject(i).getString("time");
			TipData med = new TipData(feedback, date);
			labels.add(med);

		}

		System.out.println(labels.size());
		return labels;
	}

	private class listAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return labels.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			if (convertView == null) {
				v = getLayoutInflater().inflate(R.layout.tip_item, null);
			} else {
				v = convertView;
			}
			TextView name = (TextView) v.findViewById(R.id.feedback);
			TextView date = (TextView) v.findViewById(R.id.date);
			name.setText(tipdata.get(position).getFeedback());
			date.setText(tipdata.get(position).getdateformate());
			return v;

		}

	}

	public class EndlessScrollListener implements OnScrollListener {

		public EndlessScrollListener() {
		}

		public EndlessScrollListener(int rvisibleThreshold) {
			visibleThreshold = rvisibleThreshold;
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

			if (loading) {
				if (totalItemCount > previousTotal) {
					loading = false;
					previousTotal = totalItemCount;
					size += 10;

				}
			}
			if (!loading
					&& (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
				if (vinfo != null) {
					vinfo.cancel(true);
				}

				IsInternetPresent = cd.isConnectingToInternet();
				if (IsInternetPresent == false) {
					cd.showNoInternetPopup();
				} else {
					vinfo = new getTip().execute();
				}
				loading = true;

			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}
	}

	class TipData {

		// private variables
		public String _feedback, _date;

		public TipData(String _feedback, String _date) {
			this._feedback = _feedback;
			this._date = _date;

		}

		// getting name
		public String getFeedback() {
			return this._feedback;
		}

		public String getDate() {
			return this._date;
		}

		public String getdateformate() {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date datef;
			String dateformat = "";
			try {
				datef = sdf.parse(_date);
				sdf.applyPattern("E,MMM dd,yyyy");
				dateformat = sdf.format(datef);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return dateformat;
		}
	}

	@Override
	protected int getLayoutResourceId() {
		// TODO Auto-generated method stub
		return R.layout.tips;
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

}
