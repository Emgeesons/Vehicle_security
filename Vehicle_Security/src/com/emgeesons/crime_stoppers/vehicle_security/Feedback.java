package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	String reponse;

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

		@SuppressWarnings({ "deprecation", "deprecation", "deprecation" })
		@Override
		protected Void doInBackground(Void... params) {
			HttpEntity resEntity = null;
			JSONArray jsonMainArr;
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			// The default value is zero, that means the timeout is not used.
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			httpclient.getParams().setParameter(
					CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

			HttpPost httppost = new HttpPost(feedback_url);
			@SuppressWarnings("deprecation")
			MultipartEntity mpEntity = null;
			mpEntity = new MultipartEntity();
			try {

				info.device();
				info.showInfo(getActivity());
				mpEntity.addPart("os", new StringBody(info.manufacturer));
				mpEntity.addPart("make", new StringBody("Android" + " "
						+ info.Version));
				mpEntity.addPart("model", new StringBody(info.model));
				mpEntity.addPart("userId", new StringBody(info.user_id));

				mpEntity.addPart("pin", new StringBody(info.pin));
				mpEntity.addPart("rating",
						new StringBody(String.valueOf(rates)));
				mpEntity.addPart("feedback", new StringBody(feedback.getText()
						.toString()));

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
				getActivity().runOnUiThread(new Runnable() {

					public void run() {
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(
								getActivity());

						alertDialog
								.setMessage("Please make sure you are connected to the internet and try again.");

						alertDialog.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

									}
								});

						alertDialog.show();
					}
				});
				return null;
			}

			StatusLine status = response.getStatusLine();
			System.out.println(response.getStatusLine());
			if (status.getStatusCode() == HttpStatus.SC_OK) {

				if (resEntity != null) {
					try {
						reponse = EntityUtils.toString(resEntity);
						System.out.println(reponse);
						JSONObject profile = new JSONObject(reponse);

						jsonMainArr = profile.getJSONArray("response");

						success = profile.getString("status");
						mess = profile.getString("message");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (success.equals("success")) {

					getActivity().runOnUiThread(new Runnable() {

						public void run() {
							final AlertDialog Dialog = new AlertDialog.Builder(
									getActivity()).create();
							Dialog.setTitle("Thank You");
							Dialog.setMessage(mess);
							Dialog.setButton(DialogInterface.BUTTON_NEUTRAL,
									"OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											pDialog.dismiss();
											Intent next = new Intent(
													getActivity(),
													MainActivity.class);
											startActivity(next);
											getActivity().finish();
										}
									});
							Dialog.setCancelable(false);
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

					getActivity().runOnUiThread(new Runnable() {

						public void run() {
							final AlertDialog Dialog = new AlertDialog.Builder(
									getActivity()).create();
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

}
