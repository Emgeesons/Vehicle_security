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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class Feedback extends SherlockFragment {

	View view;
	EditText feedback;
	RatingBar rate;
	Button send;
	Connection_Detector cd;
	Boolean IsInternetPresent;
	float rates;
	private AsyncTask<Void, Void, Void> feedb;
	ProgressDialog pDialog;
	String feedback_url = Data.url + "feedback.php";
	TextView ratetext;
	Data info;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.feddback, container, false);
		cd = new Connection_Detector(getActivity());
		info = new Data();
		feedback = (EditText) view.findViewById(R.id.feedback);
		rate = (RatingBar) view.findViewById(R.id.ratingBar1);
		send = (Button) view.findViewById(R.id.send);
		ratetext = (TextView) view.findViewById(R.id.rate);
		rate.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				rates = rating;

				ratetext.setTextColor(getResources().getColor(R.color.black));

			}
		});
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				IsInternetPresent = cd.isConnectingToInternet();
				if (IsInternetPresent == false) {
					cd.showNoInternetPopup();
				} else {
					if (rates == 0.0) {
						ratetext.setTextColor(getResources().getColor(
								R.color.red));
					} else {

						feedb = new feed().execute();
					}
				}
			}
		});

		return view;
	}

	private class feed extends AsyncTask<Void, Void, Void> {
		String success, mess, response, id;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Sending");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(feedback_url);
			System.out.println(feedback_url);
			JSONArray jsonMainArr;
			JSONObject json = new JSONObject();
			try {
				info.device();
				info.showInfo(getActivity());

				json.put("userId", info.user_id);
				json.put("rating", rates);
				json.put("feedback ", feedback.getText().toString());
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

				getActivity().runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								getActivity()).create();
						Dialog.setTitle("Thank You");
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
			// response failure
			else if (success.equals("failure")) {

				getActivity().runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								getActivity()).create();
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

				getActivity().runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								getActivity()).create();
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

}
