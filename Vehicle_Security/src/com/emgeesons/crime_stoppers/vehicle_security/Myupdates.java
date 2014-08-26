package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class Myupdates extends Fragment {
	int novehicles;
	static RelativeLayout addetails, main, noupdate, Vrecover;
	Button go;
	List<ParkingData> vehicles;
	DatabaseHandler db;
	SQLiteDatabase dbb;
	ListView data;
	Connection_Detector cd;
	boolean IsInternetPresent;
	String myUpdates_url = Data.url + "myUpdates.php";
	ProgressDialog pDialog;
	Data info;
	GPSTracker gps;
	private AsyncTask<Void, Void, Void> vinfo;
	TextView name, reg, type, date, time, location, rtype;
	String pos = "";
	ImageView vtype, rvtype;
	RelativeLayout listHeaderView;
	private ProgressBar pBar;
	String vehicle_type, make, model, rno, inumber, locations, selected_date,
			selected_time, report_type, comments, vehicle_id, report_id,
			status, rsdate, rstime, rslocation;
	JSONArray jsonarr, jsonMainArr;
	Button recover;
	View rootView;
	String recover_url = Data.url + "vehicleRecovered.php";
	private AsyncTask<Void, Void, Void> recoverv;
	View v;
	DisplayImageOptions options;
	SharedPreferences atPrefs;
	String spic1, spic2, spic3, reponse;
	double LATITUDE, LONGITUDE;

	public static Myupdates newInstance() {

		Myupdates f = new Myupdates();
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.myupdates, container, false);
		cd = new Connection_Detector(getActivity());

		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.showImageOnLoading(R.drawable.add_photos_grey)
				.displayer(new RoundedBitmapDisplayer(50)).build();

		addetails = (RelativeLayout) rootView.findViewById(R.id.adddetails);
		noupdate = (RelativeLayout) rootView.findViewById(R.id.stay);
		main = (RelativeLayout) rootView.findViewById(R.id.main);
		Vrecover = (RelativeLayout) rootView.findViewById(R.id.ress);
		go = (Button) rootView.findViewById(R.id.go);
		pBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
		Updates.imageLoader.clearMemoryCache();
		Updates.imageLoader.clearDiskCache();
		rtype = (TextView) rootView.findViewById(R.id.typess);
		info = new Data();
		db = new DatabaseHandler(getActivity());
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();
		vehicles = db.getparkingData();

		if ((vehicles.size() == 0)) {
			pBar.setVisibility(View.GONE);
			addetails.setVisibility(View.VISIBLE);
			main.setVisibility(View.GONE);
			noupdate.setVisibility(View.GONE);
			Vrecover.setVisibility(View.GONE);
			go.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent next = new Intent(getActivity(), ProfileScreen.class);
					startActivity(next);
					getActivity().finish();
				}
			});
		} else {
			gps = new GPSTracker(getActivity());
			if (gps.canGetLocation()) {
				LATITUDE = gps.getLatitude();
				LONGITUDE = gps.getLongitude();
				pos = getaddress();

			} else {
				LATITUDE = 0;
				LONGITUDE = 0;
				pos = "";
			}
			IsInternetPresent = cd.isConnectingToInternet();
			if (IsInternetPresent == false) {
				cd.showNoInternetPopup();
			} else {
				vinfo = new getinfo().execute();
			}

			data = (ListView) rootView.findViewById(R.id.list);
			v = (RelativeLayout) inflater.inflate(R.layout.updateheader, null);
			name = (TextView) v.findViewById(R.id.name);
			reg = (TextView) v.findViewById(R.id.reg);
			type = (TextView) v.findViewById(R.id.type);
			date = (TextView) v.findViewById(R.id.date);
			time = (TextView) v.findViewById(R.id.time);
			location = (TextView) v.findViewById(R.id.location);
			vtype = (ImageView) v.findViewById(R.id.vtype);

			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					SharedPreferences sharedpreferences;
					sharedpreferences = getActivity().getSharedPreferences(
							Data.MyPREFERENCES, Context.MODE_PRIVATE);
					sharedpreferences.edit().putString(Data.vid, "notcall")
							.commit();
					Intent next = new Intent(getActivity(), Reportsummary.class);
					next.putExtra("data", jsonMainArr.toString());
					startActivity(next);
					// getActivity().finish();
				}
			});

			data.addHeaderView(v);

			recover = (Button) rootView.findViewById(R.id.recover);
			recover.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setMessage("Vehicle Recovered")
							.setCancelable(false)
							.setMessage("Are you sure?")
							.setNegativeButton("No",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {

										}
									})
							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											IsInternetPresent = cd
													.isConnectingToInternet();
											if (IsInternetPresent == false) {
												cd.showNoInternetPopup();
											} else {
												recoverv = new rec().execute();
											}

										}

									});
					builder.show();

				}

			});
		}
		return rootView;
	}

	private class rec extends AsyncTask<Void, Void, Void> {
		String success, mess, response;
		String vid, typevalue;
		String currentTime, currentDate;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("updating..");
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
			HttpPost httppost = new HttpPost(recover_url);
			@SuppressWarnings("deprecation")
			MultipartEntity mpEntity = null;
			mpEntity = new MultipartEntity();

			try {
				info.device();
				info.showInfo(getActivity());
				mpEntity.addPart("latitude",
						new StringBody(String.valueOf(LATITUDE)));
				mpEntity.addPart("longitude",
						new StringBody(String.valueOf(LONGITUDE)));

				mpEntity.addPart("reportId",
						new StringBody(String.valueOf(report_id)));
				mpEntity.addPart("vehicleId",
						new StringBody(String.valueOf(vehicle_id)));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdfs = new SimpleDateFormat("HH:mm");
				currentTime = sdfs.format(new Date());
				currentDate = sdf.format(new Date());
				mpEntity.addPart("date",
						new StringBody(String.valueOf(currentDate)));
				mpEntity.addPart("time",
						new StringBody(String.valueOf(currentTime)));
				mpEntity.addPart("location",
						new StringBody(String.valueOf(pos)));

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
				getActivity().runOnUiThread(new Runnable() {

					public void run() {
						cd.showNoInternetPopup();
					}
				});
				return null;
			}
			;
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

					getActivity().runOnUiThread(new Runnable() {

						public void run() {
							rsdate = currentDate;
							rstime = currentTime;
							rslocation = pos;
							recoverfun();

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
											Dialog.dismiss();
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

	private void recoverfun() {
		main.setVisibility(View.GONE);
		Vrecover.setVisibility(View.VISIBLE);
		TextView rname, rreg, rdate, rtime, rlocation, rrdate, rrtime, rrloc;
		Button done, ins, pol;
		// TextView update = (TextView) rootView
		// .findViewById(R.id.update);
		rname = (TextView) rootView.findViewById(R.id.namess);
		rreg = (TextView) rootView.findViewById(R.id.regss);
		rdate = (TextView) rootView.findViewById(R.id.datess);
		rtime = (TextView) rootView.findViewById(R.id.timess);
		rlocation = (TextView) rootView.findViewById(R.id.locationss);
		done = (Button) rootView.findViewById(R.id.vrech);
		ins = (Button) rootView.findViewById(R.id.ins);
		pol = (Button) rootView.findViewById(R.id.pol);
		rrdate = (TextView) rootView.findViewById(R.id.rdate);
		rrtime = (TextView) rootView.findViewById(R.id.rtime);
		rrloc = (TextView) rootView.findViewById(R.id.rloc);
		rname.setText(make + " " + model);
		rreg.setText("Registration Number:" + " " + rno);
		rtype.setText(report_type);
		rdate.setText(dateformate(selected_date));
		rtime.setText(selected_time);
		rlocation.setText(locations);

		rrdate.setText(dateformate(rsdate));
		if (rstime.equalsIgnoreCase("00:00:00")) {
			rrtime.setVisibility(View.INVISIBLE);
		} else {
			String tim[] = rstime.split(":");
			rrtime.setText(tim[0] + ":" + tim[1]);
		}

		rrloc.setText(rslocation);
		if (rslocation.isEmpty()) {
			ImageView s = (ImageView) rootView.findViewById(R.id.imageView4);
			s.setVisibility(View.INVISIBLE);
		}

		if (rsdate.equalsIgnoreCase("0000-00-00")) {
			rrdate.setVisibility(View.INVISIBLE);
		}

		// if (inumber.isEmpty()) {
		// ins.setVisibility(View.GONE);
		// } else {
		// ins.setVisibility(View.VISIBLE);
		// }
		ins.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent call = new Intent(Intent.ACTION_CALL);
				call.setData(Uri.parse("tel:" + inumber));
				startActivity(call);
			}
		});
		pol.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent call = new Intent(Intent.ACTION_CALL);
				call.setData(Uri.parse("tel:" + "131444"));
				startActivity(call);
			}
		});
		// update.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// main.setVisibility(View.VISIBLE);
		// Vrecover.setVisibility(View.GONE);
		// }
		// });
		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent next = new Intent(getActivity(), MainActivity.class);
				startActivity(next);
				getActivity().finish();
			}
		});
	}

	String getaddress() {
		Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);

		try {
			List<Address> addresses = geocoder.getFromLocation(LATITUDE,
					LONGITUDE, 1);

			if (addresses != null && addresses.size() > 0) {
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder("");
				for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
					strReturnedAddress
							.append(returnedAddress.getAddressLine(i)).append(
									"\n");
				}

				pos = strReturnedAddress.toString();

			}

		} catch (IOException e) {
			e.printStackTrace();
			pos = "";
			getActivity().runOnUiThread(new Runnable() {

				public void run() {
					// checking response and display resp screen

					// onchange();

				}
			});

		}
		return pos;
	}

	public void onchange() {

		name.setText(make + " " + model);
		reg.setText("Registration Number:" + " " + rno);
		type.setText(report_type);
		if (!selected_date.isEmpty()) {
			date.setText(dateformate(selected_date));
		}
		if (!selected_time.isEmpty()) {
			time.setText(selected_time);
		}

		location.setText(locations);
		if (location.getText().toString().isEmpty()) {
			ImageView ii = (ImageView) v.findViewById(R.id.imageView2);
			ii.setVisibility(View.GONE);
		}

		if (vehicle_type.equalsIgnoreCase("Car")) {
			vtype.setImageResource(R.drawable.ic_car);
		} else if (vehicle_type.equalsIgnoreCase("Bicycle")) {
			vtype.setImageResource(R.drawable.ic_cycle);
			reg.setText("Serial Number:" + " " + rno);
		} else if (vehicle_type.equalsIgnoreCase("MotorCycle")) {
			vtype.setImageResource(R.drawable.ic_bike);
		} else {
			vtype.setImageResource(R.drawable.ic_other);
		}
		if (rno.isEmpty()) {
			reg.setVisibility(View.GONE);
		}
		RelativeLayout rline = (RelativeLayout) v.findViewById(R.id.rline);
		reg.setVisibility(View.GONE);

	}

	private class getinfo extends AsyncTask<Void, Void, Void> {
		String success, mess, response, qus;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pBar.setVisibility(View.VISIBLE);

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
				info.showInfo(getActivity());

				mpEntity.addPart("location", new StringBody(pos));
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
				getActivity().runOnUiThread(new Runnable() {

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
					jsonarr = profile.getJSONArray("sightings");
					success = profile.getString("status");
					mess = profile.getString("message");

				} catch (JSONException e) {
					System.out.println("JSONException");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (success.equals("success")) {

					getActivity().runOnUiThread(new Runnable() {

						public void run() {
							pBar.setVisibility(View.GONE);
							if (!(jsonMainArr.length() == 0)) {
								try {
									vehicle_id = jsonMainArr.getJSONObject(0)
											.getString("vehicle_id");
									vehicle_type = jsonMainArr.getJSONObject(0)
											.getString("vehicle_type");
									report_id = jsonMainArr.getJSONObject(0)
											.getString("report_id");
									make = jsonMainArr.getJSONObject(0)
											.getString("make");
									model = jsonMainArr.getJSONObject(0)
											.getString("model");
									rno = jsonMainArr
											.getJSONObject(0)
											.getString("registration_serial_no");
									inumber = jsonMainArr.getJSONObject(0)
											.getString(
													"insurance_company_number");
									locations = jsonMainArr.getJSONObject(0)
											.getString("location");
									selected_date = jsonMainArr
											.getJSONObject(0).getString(
													"selected_date");
									selected_time = jsonMainArr
											.getJSONObject(0).getString(
													"selected_time");
									report_type = jsonMainArr.getJSONObject(0)
											.getString("report_type");
									comments = jsonMainArr.getJSONObject(0)
											.getString("comments");
									status = jsonMainArr.getJSONObject(0)
											.getString("status");
									rsdate = jsonMainArr.getJSONObject(0)
											.getString("recovered_date");
									rstime = jsonMainArr.getJSONObject(0)
											.getString("recovered_time");
									rslocation = jsonMainArr.getJSONObject(0)
											.getString("recovered_location");
									String[] datespilt = selected_time
											.split("\\:");
									selected_time = datespilt[0] + ":"
											+ datespilt[1];
									onchange();
								} catch (JSONException e) {
									e.printStackTrace();
								}

							} else {
								data.removeHeaderView(v);
								recover.setVisibility(View.GONE);
								selected_time = "";
								selected_date = "";
							}

							addetails.setVisibility(View.GONE);
							main.setVisibility(View.VISIBLE);
							if (jsonMainArr.length() == 1) {
								if (status.equalsIgnoreCase("recovered")) {
									report_type = "Recovered";
									rtype.setTextColor(getResources().getColor(
											R.color.green));
									recoverfun();
								}
							}

							if ((jsonarr.length() < 1)) {
								// if (status.equalsIgnoreCase("recovered")) {
								// report_type = "Recovered";
								// rtype.setTextColor(getResources().getColor(
								// R.color.green));
								// recoverfun();
								// }
								TextView title = (TextView) rootView
										.findViewById(R.id.textView1);
								TextView subtitle = (TextView) rootView
										.findViewById(R.id.textView2);
								noupdate.setVisibility(View.VISIBLE);
								title.setText("No Vehicles Reported");
								subtitle.setText("Your reported vehicle updates will be displayed here");
								data.setAdapter(new ffAdapter());
							} else {
								if (status.equalsIgnoreCase("recovered")) {
									report_type = "Recovered";
									rtype.setTextColor(getResources().getColor(
											R.color.green));
									recoverfun();
								}

								noupdate.setVisibility(View.INVISIBLE);
								data.setAdapter(new ffAdapter());
							}

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
			pBar.setVisibility(View.GONE);
		}
	}

	class ffAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return jsonarr.length();
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
			TextView name, type, time, date, location, comm, spot;
			final ImageView pic1;
			final ImageView pic2;
			final ImageView pic3;
			ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
			int j = 0, x = 0;
			if (convertView == null) {
				vv = getActivity().getLayoutInflater().inflate(
						R.layout.updatelistitem, null);
			} else {
				vv = convertView;
			}
			pic1 = (ImageView) vv.findViewById(R.id.pic1);
			pic2 = (ImageView) vv.findViewById(R.id.pic2);
			pic3 = (ImageView) vv.findViewById(R.id.pic3);
			name = (TextView) vv.findViewById(R.id.sname);
			type = (TextView) vv.findViewById(R.id.stype);
			time = (TextView) vv.findViewById(R.id.stime);
			date = (TextView) vv.findViewById(R.id.sdate);
			location = (TextView) vv.findViewById(R.id.slocation);
			comm = (TextView) vv.findViewById(R.id.scomm);
			spot = (TextView) vv.findViewById(R.id.sspot);
			try {
				name.setText(jsonarr.getJSONObject(position).getString(
						"first_name"));

				String[] datespilt = jsonarr.getJSONObject(position)
						.getString("selected_time").split("\\:");
				time.setText(datespilt[0] + ":" + datespilt[1]);
				date.setText(dateformate(jsonarr.getJSONObject(position)
						.getString("selected_date")));
				location.setText(jsonarr.getJSONObject(position).getString(
						"location"));
				comm.setText(jsonarr.getJSONObject(position).getString(
						"comments"));
				type.setText(jsonarr.getJSONObject(position).getString(
						"sighting_type"));
				spic1 = jsonarr.getJSONObject(position).getString("photo1");
				spic2 = jsonarr.getJSONObject(position).getString("photo2");
				spic3 = jsonarr.getJSONObject(position).getString("photo3");
				if (location.getText().toString().isEmpty()) {
					ImageView i = (ImageView) vv.findViewById(R.id.simageView1);
					i.setVisibility(View.GONE);
				}
				if (vehicle_type.equalsIgnoreCase("Car")) {
					spot.setText("spotted your car");
				} else if (vehicle_type.equalsIgnoreCase("Bicycle")) {
					spot.setText("spotted your bicycle");
				} else if (vehicle_type.equalsIgnoreCase("MotorCycle")) {
					spot.setText("spotted your motorcycle");
				} else {
					spot.setText("spotted your vehicle");
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			// for next row pos*no of photo
//			if (!(position == 0)) {
//				j = position * 3;
//			}
			if (comm.getText().toString().isEmpty()) {
				comm.setVisibility(View.GONE);
			}

			String[] imageUrls = { spic1, spic2, spic3 };
			if (!imageUrls[0].isEmpty() && !imageUrls[0].equalsIgnoreCase("")) {
				Updates.imageLoader.displayImage(imageUrls[0], pic1, options,
						animateFirstListener);
				pic1.setVisibility(View.VISIBLE);
				pic1.setTag(imageUrls[0]);

				if (!imageUrls[1].isEmpty()
						&& !imageUrls[1].equalsIgnoreCase("")) {
					Updates.imageLoader.displayImage(imageUrls[1], pic2,
							options, animateFirstListener);
					pic2.setVisibility(View.VISIBLE);
					pic2.setTag(imageUrls[1]);

					if (!imageUrls[2].isEmpty()
							&& !imageUrls[2].equalsIgnoreCase("")) {
						Updates.imageLoader.displayImage(imageUrls[2], pic3,
								options, animateFirstListener);
						pic3.setVisibility(View.VISIBLE);
						pic3.setTag(imageUrls[2]);
					} else {
						pic3.setVisibility(View.GONE);
					}

				} else {
					pic2.setVisibility(View.GONE);
				}

			} else {
				pic1.setVisibility(View.GONE);

				// pic2.setVisibility(View.GONE);

			}
			if (comm.getVisibility() == View.VISIBLE
					|| (pic1.getVisibility() == View.VISIBLE
							|| pic2.getVisibility() == View.VISIBLE || pic3
							.getVisibility() == View.VISIBLE)) {
				RelativeLayout rline = (RelativeLayout) vv
						.findViewById(R.id.rline);
				rline.setVisibility(View.VISIBLE);
			}
//			for (int i = 0; i < imageUrls.length; i++) {
//				Updates.mStrings.add(imageUrls[i]);
//
//			}
//			ImageView arr[] = { pic1, pic2, pic3 };
//			for (int i = j; i < j + 3; i++) {
//
//				Updates.imageLoader.displayImage(Updates.mStrings.get(i),
//						arr[x], options, animateFirstListener);
//				arr[x].setVisibility(View.VISIBLE);
//				if (Updates.mStrings.get(i).isEmpty()) {
//					arr[x].setVisibility(View.GONE);
//				}
//				x++;
//
//			}

			pic1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Fullimage.class);
					// Toast.makeText(getActivity(), "1", Toast.LENGTH_LONG)
					// .show();
					intent.putExtra("IMAGES", String.valueOf(pic1.getTag()));
					startActivity(intent);
				}
			});
			pic2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Fullimage.class);
					intent.putExtra("IMAGES", String.valueOf(pic2.getTag()));

					// Toast.makeText(getActivity(), "2", Toast.LENGTH_LONG)
					// .show();
					startActivity(intent);

				}
			});

			pic3.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Fullimage.class);
					intent.putExtra("IMAGES", String.valueOf(pic3.getTag()));
					startActivity(intent);

					// Toast.makeText(getActivity(), "3", Toast.LENGTH_LONG)
					// .show();

				}
			});
			return vv;
		}
	}

	public String dateformate(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date datef;
		String dateformat = "";
		try {
			datef = sdf.parse(date);
			sdf.applyPattern("E, MMMM dd,yyyy");
			dateformat = sdf.format(datef);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateformat;
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

}
