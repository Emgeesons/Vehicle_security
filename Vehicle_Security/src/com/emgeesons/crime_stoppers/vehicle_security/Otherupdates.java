package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class Otherupdates extends Fragment {

	DatabaseHandler db;
	SQLiteDatabase dbb;
	static ListView data;
	ListView data1;
	private AsyncTask<Void, Void, Void> vinfo;
	String otherUpdates_url = Data.url + "otherUpdates.php";
	String DetailsUpdates_url = Data.url + "otherUpdatesDetails.php", vid,
			vmake, vmodel, vreg, vdate, vtime, vcomm, vloc, rtype, vtypes,
			spic1, spic2, spic3;
	private ProgressBar pBar;
	Data info;
	GPSTracker gps;
	DisplayImageOptions options;
	JSONArray jsonarrs, jsonMainArrs;
	List<UpdateData> testimonialData;
	List<UpdateData> labels = new ArrayList<UpdateData>();
	int size = 0;
	private int visibleThreshold = 5;
	private boolean loading = true;
	private int previousTotal = 0;
	private BaseAdapter mListAdapter;
	JSONArray jsonarr, jsonMainArr;
	String vehicle_type, make, model, rno, inumber, locations, selected_date,
			selected_time, report_type, comments, vehicle_id, report_id;
	static RelativeLayout vdetails;
	SharedPreferences atPrefs;
	View vv, rootView;
	RelativeLayout noupdate;
	Connection_Detector cd;
	boolean IsInternetPresent;
	int rsize = 0, ssize = 0;
	double LATITUDE, LONGITUDE;
	String pos, reponse;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.otherupdate, container, false);
		cd = new Connection_Detector(getActivity());
		Updates.imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.showImageOnLoading(R.drawable.add_photos_grey)
				.displayer(new RoundedBitmapDisplayer(50)).build();
		atPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		pBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
		noupdate = (RelativeLayout) rootView.findViewById(R.id.stay);
		data = (ListView) rootView.findViewById(R.id.data);
		data1 = (ListView) rootView.findViewById(R.id.data1);
		vdetails = (RelativeLayout) rootView.findViewById(R.id.vdetails);
		data.setOnScrollListener(new EndlessScrollListener());
		IsInternetPresent = cd.isConnectingToInternet();
		Updates.imageLoader.clearMemoryCache();
		Updates.imageLoader.clearDiskCache();
		if (IsInternetPresent == false) {
			cd.showNoInternetPopup();
		} else {
			vinfo = new getinfo().execute();
		}

		info = new Data();
		gps = new GPSTracker(getActivity());
		if (gps.canGetLocation()) {
			LATITUDE = gps.getLatitude();
			LONGITUDE = gps.getLongitude();
			pos = getaddress();
		} else {
			LATITUDE = 0.0;
			LONGITUDE = 0.0;
			Toast.makeText(
					getActivity(),
					"Please allow MyWheels to access Your location . Go back and enable it ",
					Toast.LENGTH_LONG).show();
			pos = "";
		}
		return rootView;
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

	private class getinfo extends AsyncTask<Void, Void, Void> {
		String success, mess;

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
			HttpPost httppost = new HttpPost(otherUpdates_url);
			@SuppressWarnings("deprecation")
			MultipartEntity mpEntity = null;
			mpEntity = new MultipartEntity();

			try {
				info.device();
				info.showInfo(getActivity());
				if (atPrefs.getBoolean(Data.checkllogin, true)) {

					mpEntity.addPart("userId", new StringBody("0"));
					mpEntity.addPart("pin", new StringBody("0000"));
				} else {
					mpEntity.addPart("userId", new StringBody(info.user_id));
					mpEntity.addPart("pin", new StringBody(info.pin));

				}
				mpEntity.addPart("os", new StringBody(info.manufacturer));
				mpEntity.addPart("make", new StringBody("Android" + " "
						+ info.Version));
				mpEntity.addPart("model", new StringBody(info.model));

				mpEntity.addPart("latitude",
						new StringBody(String.valueOf(LATITUDE)));
				mpEntity.addPart("longitude",
						new StringBody(String.valueOf(LONGITUDE)));
				// mpEntity.addPart("latitude",
				// new StringBody(String.valueOf(19.0691108)));
				// mpEntity.addPart("longitude",
				// new StringBody(String.valueOf(72.8279059)));
				mpEntity.addPart("countReports",
						new StringBody(String.valueOf(rsize)));
				mpEntity.addPart("countSightings",
						new StringBody(String.valueOf(ssize)));

			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			httppost.setEntity(mpEntity);
			System.out.println(httppost.getRequestLine());
			HttpResponse response = null;
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				resEntity = response.getEntity();
			} catch (Exception e) {
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
					jsonMainArrs = profile.getJSONArray("reports");
					jsonarrs = profile.getJSONArray("sightings");
					success = profile.getString("status");
					mess = profile.getString("message");

				} catch (JSONException e) {
					System.out.println("JSONException");
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (success.equals("success")) {

					getActivity().runOnUiThread(new Runnable() {

						public void run() {
							pBar.setVisibility(View.GONE);
							try {

								testimonialData = getContactData();
							} catch (JSONException e) {
								e.printStackTrace();
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
											dialog.dismiss();
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
											dialog.dismiss();
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
			pBar.setVisibility(View.GONE);
			if (labels.size() == 0) {
				noupdate.setVisibility(View.VISIBLE);
				data.setVisibility(View.GONE);
			} else {
				if (size == 0) {
					mListAdapter = new listAdapter();
					data.setAdapter(mListAdapter);
					visibleThreshold = 5;
					previousTotal = 0;
					loading = true;
				} else {

					mListAdapter.notifyDataSetChanged();

				}
			}

		}
	}

	private class listAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return labels.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getViewTypeCount() {
			return 100000;
		}

		@Override
		public int getItemViewType(int position) {

			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			TextView name, type, time, date, location, comm, spot, spottype;
			TextView oname, oreg, otype, odate, otime, olocation;
			final ImageView pic1;
			final ImageView pic2;
			final ImageView pic3;
			ImageView ovtype, sloc;
			ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
			// int j = 0, x = 0;
			if (convertView == null) {
				vv = getActivity().getLayoutInflater().inflate(
						R.layout.other_item, null);
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
			// spot = (TextView) vv.findViewById(R.id.sspot);
			spottype = (TextView) vv.findViewById(R.id.textView1);
			oname = (TextView) vv.findViewById(R.id.name);
			oreg = (TextView) vv.findViewById(R.id.reg);
			otype = (TextView) vv.findViewById(R.id.type);
			odate = (TextView) vv.findViewById(R.id.date);
			otime = (TextView) vv.findViewById(R.id.time);
			olocation = (TextView) vv.findViewById(R.id.location);
			ovtype = (ImageView) vv.findViewById(R.id.vtype);
			sloc = (ImageView) vv.findViewById(R.id.imageView2);
			oname.setText(testimonialData.get(position).getmake() + " "
					+ testimonialData.get(position).getmodel());
			if (testimonialData.get(position).gettype()
					.equalsIgnoreCase("report")) {
				oreg.setText("Reg No:" + " "
						+ testimonialData.get(position).getReg());
			} else {
				oreg.setText("Ser No:" + " "
						+ testimonialData.get(position).getReg());
			}

			otype.setText(testimonialData.get(position).getRtype());
			odate.setText(dateformate(testimonialData.get(position).getdate()));
			otime.setText(testimonialData.get(position).getTime());
			olocation.setText(testimonialData.get(position).getsloc());
			comm.setText(testimonialData.get(position).getComm());
			if (testimonialData.get(position).getVtype()
					.equalsIgnoreCase("Car")) {
				ovtype.setImageResource(R.drawable.ic_car);
			} else if (testimonialData.get(position).getVtype()
					.equalsIgnoreCase("Bicycle")) {
				ovtype.setImageResource(R.drawable.ic_cycle);
				oreg.setText("Ser No:" + " "
						+ testimonialData.get(position).getReg());
			} else if (testimonialData.get(position).getVtype()
					.equalsIgnoreCase("MotorCycle")) {
				ovtype.setImageResource(R.drawable.ic_bike);
			} else {
				ovtype.setImageResource(R.drawable.ic_other);
			}
			// if (testimonialData.get(position).getReg().isEmpty()) {
			// oreg.setVisibility(View.GONE);
			// }
			spottype.setText(testimonialData.get(position).getsstat());

			// System.out.println(position);
			name.setText(testimonialData.get(position).getfname());
			// for next row pos*no of photo
			// if (!(position == 0)) {
			// j = position * 3;
			// }
			if (comm.getText().toString().isEmpty()) {
				comm.setVisibility(View.GONE);
			}
			if (olocation.getText().toString().isEmpty()) {
				olocation.setVisibility(View.GONE);
				sloc.setVisibility(View.GONE);
			}
			// if (testimonialData.get(position).getReg().isEmpty()) {
			// olocation.setVisibility(View.GONE);
			// sloc.setVisibility(View.GONE);
			// }

			String[] imageUrls = { testimonialData.get(position).getp1(),
					testimonialData.get(position).getp2(),
					testimonialData.get(position).getp3() };
			// Log.i("name", testimonialData.get(position).getp1() + " "
			// + testimonialData.get(position).getfname());
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

			// for (int i = 0; i < imageUrls.length; i++) {
			// Updates.OStrings.add(imageUrls[i]);
			//
			// }
			// Log.i(String.valueOf(position),
			// String.valueOf(Updates.OStrings.size()));
			// ImageView arr[] = { pic1, pic2, pic3 };
			// for (int i = j; i < j + 3; i++) {
			//
			// Updates.imageLoader.displayImage(Updates.OStrings.get(i),
			// arr[x], options, animateFirstListener);
			//
			// arr[x].setVisibility(View.VISIBLE);
			// if (Updates.OStrings.get(i).isEmpty()) {
			// arr[x].setVisibility(View.GONE);
			// }
			//
			// x++;
			// }
			if (comm.getVisibility() == View.VISIBLE
					|| (pic1.getVisibility() == View.VISIBLE
							|| pic2.getVisibility() == View.VISIBLE || pic3
							.getVisibility() == View.VISIBLE)) {
				RelativeLayout rline = (RelativeLayout) vv
						.findViewById(R.id.rline);
				rline.setVisibility(View.VISIBLE);
			}
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
			vv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (testimonialData.get(position).gettype()
							.equalsIgnoreCase("report")) {
						vid = testimonialData.get(position).getVid();
						vmake = testimonialData.get(position).getmake();
						vmodel = testimonialData.get(position).getmodel();
						vreg = testimonialData.get(position).getReg();
						vdate = testimonialData.get(position).getdate();
						vtime = testimonialData.get(position).getTime();
						vcomm = testimonialData.get(position).getComm();
						vloc = testimonialData.get(position).getsloc();
						vtypes = testimonialData.get(position).getVtype();
						rtype = testimonialData.get(position).getRtype();
						spic1 = testimonialData.get(position).getp1();
						spic2 = testimonialData.get(position).getp2();
						spic3 = testimonialData.get(position).getp3();
						IsInternetPresent = cd.isConnectingToInternet();
						if (IsInternetPresent == false) {
							cd.showNoInternetPopup();
						} else {
							vinfo = new getvinfo().execute();
						}

					} else {

					}
				}
			});
			return vv;

		}
	}

	private class getvinfo extends AsyncTask<Void, Void, Void> {
		String success, mess, response, qus;
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Info...");
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
			HttpPost httppost = new HttpPost(DetailsUpdates_url);
			@SuppressWarnings("deprecation")
			MultipartEntity mpEntity = null;
			mpEntity = new MultipartEntity();

			try {
				info.device();
				info.showInfo(getActivity());

				gps = new GPSTracker(getActivity());

				mpEntity.addPart("latitude",
						new StringBody(String.valueOf(LATITUDE)));
				mpEntity.addPart("longitude",
						new StringBody(String.valueOf(LONGITUDE)));

				mpEntity.addPart("vehicleId", new StringBody(vid));

				mpEntity.addPart("os", new StringBody(info.manufacturer));
				mpEntity.addPart("make", new StringBody("Android" + " "
						+ info.Version));
				mpEntity.addPart("model", new StringBody(info.model));

				if (!atPrefs.getBoolean(info.checkllogin, true)) {
					mpEntity.addPart("userId", new StringBody(info.user_id));
					mpEntity.addPart("pin", new StringBody(info.pin));
				} else {
					mpEntity.addPart("userId", new StringBody("0"));
					mpEntity.addPart("pin", new StringBody("0000"));
				}

			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			httppost.setEntity(mpEntity);
			System.out.println(httppost.getRequestLine());
			HttpResponse response = null;
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				resEntity = response.getEntity();
			} catch (Exception e) {
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
					success = profile.getString("status");
					mess = profile.getString("message");

					jsonarr = profile.getJSONArray("response");

				} catch (JSONException e) {
					System.out.println("JSONException");
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (success.equals("success")) {

					getActivity().runOnUiThread(new Runnable() {

						public void run() {
							ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
							TextView name, reg, type, date, time, location, comm;
							ImageView vtype, locicon, pic1, pic2, pic3;
							Button report;
							pBar.setVisibility(View.GONE);
							// String[] datespilt = selected_time.split("\\:");
							// selected_time = datespilt[0] + ":" +
							// datespilt[1];
							data.setVisibility(View.GONE);

							vdetails.setVisibility(View.VISIBLE);
							report = (Button) rootView
									.findViewById(R.id.report);
							final View v = (RelativeLayout) getActivity()
									.getLayoutInflater().inflate(
											R.layout.updateheader, null);
							name = (TextView) v.findViewById(R.id.name);
							reg = (TextView) v.findViewById(R.id.reg);
							type = (TextView) v.findViewById(R.id.type);
							date = (TextView) v.findViewById(R.id.date);
							time = (TextView) v.findViewById(R.id.time);
							location = (TextView) v.findViewById(R.id.location);
							comm = (TextView) v.findViewById(R.id.comm);
							vtype = (ImageView) v.findViewById(R.id.vtype);
							locicon = (ImageView) v
									.findViewById(R.id.imageView2);
							pic1 = (ImageView) v.findViewById(R.id.pic1);
							pic3 = (ImageView) v.findViewById(R.id.pic3);
							pic2 = (ImageView) v.findViewById(R.id.pic2);
							RelativeLayout rline = (RelativeLayout) v
									.findViewById(R.id.rline);

							data1.setVisibility(View.VISIBLE);
							data1.addHeaderView(v);
							if (jsonarr.length() == 0) {
								RelativeLayout stays = (RelativeLayout) rootView
										.findViewById(R.id.stays);
								stays.setVisibility(View.VISIBLE);

							}
							data1.setAdapter(new Adapter());
							atPrefs = PreferenceManager
									.getDefaultSharedPreferences(getActivity());

							report.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {

									Intent next = new Intent(getActivity(),
											ReportSighting.class);
									next.putExtra("type", rtype);
									next.putExtra("reg", vreg);
									next.putExtra("com", vcomm);
									next.putExtra("make", vmake);
									next.putExtra("model", vmodel);
									// clear prev stackF
									next.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
											| Intent.FLAG_ACTIVITY_CLEAR_TASK);
									startActivity(next);
									getActivity().finish();

								}
							});
							String[] datespilt = vtime.split("\\:");
							selected_time = datespilt[0] + ":" + datespilt[1];
							name.setText(vmake + " " + vmodel);
							reg.setText("Reg No:" + " " + vreg);
							type.setText(rtype);
							date.setText(dateformate(vdate));
							time.setText(selected_time);
							location.setText(vloc);
							comm.setText(vcomm);

							String s[] = { spic1, spic2, spic3 };
							ImageView arr[] = { pic1, pic2, pic3 };
							for (int i = 0; i < s.length; i++) {
								Updates.imageLoader.displayImage(s[i], arr[i],
										options, animateFirstListener);
								arr[i].setVisibility(View.VISIBLE);
								if (s[i].isEmpty()) {
									arr[i].setVisibility(View.GONE);
								}
							}

							if (location.getText().toString().isEmpty()) {
								location.setVisibility(View.GONE);
								locicon.setVisibility(View.GONE);
							}
							if (comm.length() > 1
									|| (pic1.getVisibility() == View.VISIBLE
											|| pic2.getVisibility() == View.VISIBLE || pic3
											.getVisibility() == View.VISIBLE)) {
								rline.setVisibility(View.VISIBLE);
							}
							pic1.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									Intent intent = new Intent(getActivity(),
											Fullimage.class);
									intent.putExtra("IMAGES", spic1);
									startActivity(intent);
								}
							});
							pic2.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									Intent intent = new Intent(getActivity(),
											Fullimage.class);
									intent.putExtra("IMAGES", spic2);
									startActivity(intent);

								}
							});
							pic3.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									Intent intent = new Intent(getActivity(),
											Fullimage.class);
									intent.putExtra("IMAGES", spic3);
									startActivity(intent);

								}
							});
							if (vtypes.equalsIgnoreCase("Car")) {
								vtype.setImageResource(R.drawable.ic_car);
							} else if (vtypes.equalsIgnoreCase("Bicycle")) {
								vtype.setImageResource(R.drawable.ic_cycle);
							} else if (vtypes.equalsIgnoreCase("MotorCycle")) {
								vtype.setImageResource(R.drawable.ic_bike);
							} else {
								vtype.setImageResource(R.drawable.ic_other);
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
											dialog.dismiss();
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

	class Adapter extends BaseAdapter {
		String spic1, spic2, spic3;;

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
		public int getViewTypeCount() {
			return 1000;
		}

		@Override
		public int getItemViewType(int position) {

			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View vv;
			TextView name, type, time, date, location, comm, spot;
			final ImageView pic1;
			final ImageView pic2;
			final ImageView pic3, locicon;
			ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
			// int j = 0, x = 0;
			if (convertView == null) {
				vv = getActivity().getLayoutInflater().inflate(
						R.layout.updatelistitem, null);
			} else {
				vv = convertView;
			}
			locicon = (ImageView) vv.findViewById(R.id.simageView1);
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
				if (vtypes.equalsIgnoreCase("Car")) {
					spot.setText("spotted a car");
				} else if (vtypes.equalsIgnoreCase("Bicycle")) {
					spot.setText("spotted a bicycle");
				} else if (vtypes.equalsIgnoreCase("MotorCycle")) {
					spot.setText("spotted a motorcycle");
				} else {
					spot.setText("spotted a vehicle");
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			// for next row pos*no of photo
			// if (!(position == 0)) {
			// j = position * 3;
			// }
			if (comm.getText().toString().isEmpty()) {
				comm.setVisibility(View.GONE);
			}

			if (location.getText().toString().isEmpty()) {
				location.setVisibility(View.GONE);
				locicon.setVisibility(View.GONE);
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
			// for (int i = 0; i < imageUrls.length; i++) {
			// Updates.imageLists.add(imageUrls[i]);
			//
			// }
			// ImageView arr[] = { pic1, pic2, pic3 };
			// for (int i = j; i < j + 3; i++) {
			//
			// Updates.imageLoader.displayImage(Updates.imageLists.get(i),
			// arr[x], options, animateFirstListener);
			// arr[x].setVisibility(View.VISIBLE);
			// if (Updates.imageLists.get(i).isEmpty()) {
			// arr[x].setVisibility(View.GONE);
			// }
			// x++;
			// }

			if (comm.getVisibility() == View.VISIBLE
					|| (pic1.getVisibility() == View.VISIBLE
							|| pic2.getVisibility() == View.VISIBLE || pic3
							.getVisibility() == View.VISIBLE)) {
				RelativeLayout rline = (RelativeLayout) vv
						.findViewById(R.id.rline);
				rline.setVisibility(View.VISIBLE);
			}

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
			Log.i("pos", String.valueOf(position));
			return vv;
		}
	}

	// class ffAdapter extends BaseAdapter {
	// String spic1, spic2, spic3;;
	//
	// @Override
	// public int getCount() {
	// return 0;
	// }
	//
	// @Override
	// public Object getItem(int position) {
	// return null;
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// return 0;
	// }
	//
	// @Override
	// public View getView(final int position, View convertView,
	// ViewGroup parent) {
	// View vv;
	// TextView name, type, time, date, location, comm, spot;
	// ImageView pic1, pic2, pic3;
	// ImageLoadingListener animateFirstListener = new
	// AnimateFirstDisplayListener();
	// int j = 0, x = 0;
	// if (convertView == null) {
	// vv = getActivity().getLayoutInflater().inflate(
	// R.layout.updatelistitem, null);
	// } else {
	// vv = convertView;
	// }
	// pic1 = (ImageView) vv.findViewById(R.id.pic1);
	// pic2 = (ImageView) vv.findViewById(R.id.pic2);
	// pic3 = (ImageView) vv.findViewById(R.id.pic3);
	// name = (TextView) vv.findViewById(R.id.sname);
	// type = (TextView) vv.findViewById(R.id.stype);
	// time = (TextView) vv.findViewById(R.id.stime);
	// date = (TextView) vv.findViewById(R.id.sdate);
	// location = (TextView) vv.findViewById(R.id.slocation);
	// comm = (TextView) vv.findViewById(R.id.scomm);
	// spot = (TextView) vv.findViewById(R.id.sspot);
	// try {
	// name.setText(jsonarr.getJSONObject(position).getString(
	// "first_name"));
	// String[] datespilt = jsonarr.getJSONObject(position)
	// .getString("selected_time").split("\\:");
	// time.setText(datespilt[0] + ":" + datespilt[1]);
	// date.setText(dateformate(jsonarr.getJSONObject(position)
	// .getString("selected_date")));
	// location.setText(jsonarr.getJSONObject(position).getString(
	// "location"));
	// comm.setText(jsonarr.getJSONObject(position).getString(
	// "comments"));
	// type.setText(jsonarr.getJSONObject(position).getString(
	// "sighting_type"));
	// spic1 = jsonarr.getJSONObject(position).getString("photo1");
	// spic2 = jsonarr.getJSONObject(position).getString("photo2");
	// spic3 = jsonarr.getJSONObject(position).getString("photo3");
	// if (vehicle_type.equalsIgnoreCase("Car")) {
	// spot.setText("spotted a car");
	// } else if (vehicle_type.equalsIgnoreCase("Bicycle")) {
	// spot.setText("spotted a bicycle");
	// } else if (vehicle_type.equalsIgnoreCase("MotorCycle")) {
	// spot.setText("spotted a motorcycle");
	// } else {
	// spot.setText("spotted a vehicle");
	// }
	//
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// // for next row pos*no of photo
	// if (!(position == 0)) {
	// j = position * 3;
	// }
	// if (comm.getText().toString().isEmpty()) {
	// comm.setVisibility(View.GONE);
	// }
	//
	// String[] imageUrls = { spic1, spic2, spic3 };
	// for (int i = 0; i < imageUrls.length; i++) {
	// Updates.imageList.add(imageUrls[i]);
	//
	// }
	// ImageView arr[] = { pic1, pic2, pic3 };
	// for (int i = j; i < j + 3; i++) {
	//
	// Updates.imageLoader.displayImage(Updates.imageList.get(i),
	// arr[x], options, animateFirstListener);
	// arr[x].setVisibility(View.VISIBLE);
	// if (Updates.imageList.get(i).isEmpty()) {
	// arr[x].setVisibility(View.GONE);
	// }
	// x++;
	// }
	//
	// pic1.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// Intent intent = new Intent(getActivity(), Fullimage.class);
	// intent.putExtra("IMAGES",
	// Updates.mStrings.get((position * 3)));
	// startActivity(intent);
	// }
	// });
	// pic2.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// Intent intent = new Intent(getActivity(), Fullimage.class);
	// intent.putExtra("IMAGES",
	// Updates.mStrings.get((position * 3) + 1));
	// startActivity(intent);
	//
	// }
	// });
	// pic3.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// Intent intent = new Intent(getActivity(), Fullimage.class);
	// intent.putExtra("IMAGES",
	// Updates.mStrings.get((position * 3) + 2));
	// startActivity(intent);
	//
	// }
	// });
	//
	// return vv;
	// }
	// }

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

	public List<UpdateData> getContactData() throws JSONException {

		for (int i = 0; i < jsonMainArrs.length(); i++) {
			String report_id = (jsonMainArrs.getJSONObject(i)
					.getString("report_id"));
			String first_name = jsonMainArrs.getJSONObject(i).getString(
					"first_name");
			String vehicle_type = jsonMainArrs.getJSONObject(i).getString(
					"vehicle_type");
			String vehicle_id = (jsonMainArrs.getJSONObject(i)
					.getString("vehicle_id"));
			String make = jsonMainArrs.getJSONObject(i).getString("make");
			String model = jsonMainArrs.getJSONObject(i).getString("model");
			String registration_serial_no = (jsonMainArrs.getJSONObject(i)
					.getString("registration_serial_no"));
			String location = jsonMainArrs.getJSONObject(i).getString(
					"location");
			String selected_date = jsonMainArrs.getJSONObject(i).getString(
					"selected_date");
			String[] datespilt = jsonMainArrs.getJSONObject(i)
					.getString("selected_time").split("\\:");
			String time = datespilt[0] + ":" + datespilt[1];
			String selected_time = time;
			String report_type = jsonMainArrs.getJSONObject(i).getString(
					"report_type");
			String comments = jsonMainArrs.getJSONObject(i).getString(
					"comments");
			String photo1 = (jsonMainArrs.getJSONObject(i).getString("photo1"));
			String photo2 = jsonMainArrs.getJSONObject(i).getString("photo2");
			String photo3 = jsonMainArrs.getJSONObject(i).getString("photo3");
			String status = jsonMainArrs.getJSONObject(i).getString("status");
			String type = "Report";
			String sta = "reported their vehicle";

			//

			UpdateData med = new UpdateData(report_id, first_name,
					vehicle_type, vehicle_id, make, model,
					registration_serial_no, location, selected_date,
					selected_time, report_type, comments, photo1, photo2,
					photo3, status, "", type, sta);
			labels.add(med);
			rsize++;

		}

		System.out.println("Report size " + "  " + rsize);

		for (int i = 0; i < jsonarrs.length(); i++) {
			String sightings_id = (jsonarrs.getJSONObject(i)
					.getString("sightings_id"));
			String first_name = jsonarrs.getJSONObject(i).getString(
					"first_name");
			String registration_number = jsonarrs.getJSONObject(i).getString(
					"registration_number");
			String vehicle_make = (jsonarrs.getJSONObject(i)
					.getString("vehicle_make"));
			String vehicle_model = jsonarrs.getJSONObject(i).getString(
					"vehicle_model");
			String vehicle_colour = jsonarrs.getJSONObject(i).getString(
					"vehicle_colour");
			String selected_date = (jsonarrs.getJSONObject(i)
					.getString("selected_date"));
			String[] datespilt = jsonarrs.getJSONObject(i)
					.getString("selected_time").split("\\:");
			String time = datespilt[0] + ":" + datespilt[1];
			String selected_time = time;

			String location = jsonarrs.getJSONObject(i).getString("location");
			String sighting_type = (jsonarrs.getJSONObject(i)
					.getString("sighting_type"));
			String comments = jsonarrs.getJSONObject(i).getString("comments");
			String photo1 = (jsonarrs.getJSONObject(i).getString("photo1"));
			String photo2 = jsonarrs.getJSONObject(i).getString("photo2");
			String photo3 = jsonarrs.getJSONObject(i).getString("photo3");
			String type = "sighting";
			String sta;
			// if (testimonialData.get(i).getVtype().equalsIgnoreCase("Car")) {
			// sta = "spotted a Car";
			// } else if (testimonialData.get(i).getVtype()
			// .equalsIgnoreCase("Bicycle")) {
			// sta = "spotted a Bicycle";
			// } else if (testimonialData.get(i).getVtype()
			// .equalsIgnoreCase("MotorCycle")) {
			// sta = "spotted a MotorCycle";
			// } else {
			sta = "spotted a vehicle";
			// }
			UpdateData med = new UpdateData(sightings_id, first_name, "", "",
					vehicle_make, vehicle_model, registration_number, location,
					selected_date, selected_time, sighting_type, comments,
					photo1, photo2, photo3, "", vehicle_colour, type, sta);
			labels.add(med);
			ssize++;
		}
		System.out.println("Sighting size " + "  " + ssize);
		System.out.println("labels" + "  " + labels.size());
		return labels;
	}

	class UpdateData {

		// private variables
		public String _report_id, _first_name, _vehicle_type, _vehicle_id,
				_make, _model, _registration_serial_no, _location,
				_selected_date, _selected_time, _report_type, _comments,
				_photo1, _photo2, _photo3, _status, svehicle_colour, _type,
				_stat;

		public UpdateData(String _report_id, String _first_name,
				String _vehicle_type, String _vehicle_id, String _make,
				String _model, String _registration_serial_no,
				String _location, String _selected_date, String _selected_time,
				String _report_type, String _comments, String _photo1,
				String _photo2, String _photo3, String _status,
				String svehicle_colour, String _type, String _stat) {
			this._report_id = _report_id;
			this._first_name = _first_name;
			this._vehicle_type = _vehicle_type;
			this._vehicle_id = _vehicle_id;
			this._make = _make;
			this._model = _model;
			this._registration_serial_no = _registration_serial_no;
			this.svehicle_colour = svehicle_colour;
			this._selected_date = _selected_date;
			this._selected_time = _selected_time;
			this._location = _location;
			this._report_type = _report_type;
			this._comments = _comments;
			this._photo1 = _photo1;
			this._photo2 = _photo2;
			this._photo3 = _photo3;
			this._status = _status;
			this._type = _type;
			this._stat = _stat;

		}

		public String getsstat() {
			return this._stat;
		}

		public String getscolor() {
			return this.svehicle_colour;
		}

		public String gettype() {
			return this._type;
		}

		public String getsloc() {
			return this._location;
		}

		public String getRid() {
			return this._report_id;
		}

		public String getfname() {
			return this._first_name;
		}

		public String getVtype() {
			return this._vehicle_type;
		}

		public String getVid() {
			return this._vehicle_id;
		}

		public String getmake() {
			return this._make;
		}

		public String getmodel() {
			return this._model;
		}

		public String getReg() {
			return this._registration_serial_no;
		}

		public String getdate() {
			return this._selected_date;
		}

		public String getTime() {
			return this._selected_time;
		}

		public String getRtype() {
			return this._report_type;
		}

		public String getComm() {
			return this._comments;
		}

		public String getp1() {
			return this._photo1;
		}

		public String getp2() {
			return this._photo2;
		}

		public String getp3() {
			return this._photo3;
		}

		public String getstatus() {
			return this._status;
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
					size += 15;

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
					vinfo = new getinfo().execute();
				}

				loading = true;

			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}
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
