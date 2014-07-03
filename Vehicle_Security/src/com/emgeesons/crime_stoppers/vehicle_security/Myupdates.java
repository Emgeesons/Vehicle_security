package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

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

import com.facebook.Session;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.urbanairship.push.PushManager;

public class Myupdates extends Fragment {
	int novehicles;
	static RelativeLayout addetails, main, noupdate, Vrecover;
	Button go;
	List<ParkingData> vehicles;
	DatabaseHandler db;
	SQLiteDatabase dbb;
	ListView data;
	Connection_Detector cd = new Connection_Detector(getActivity());
	boolean IsInternetPresent;
	String myUpdates_url = Data.url + "myUpdates.php";
	ProgressDialog pDialog;
	Data info;
	GPSTracker gps;
	private AsyncTask<Void, Void, Void> vinfo;
	TextView name, reg, type, date, time, location;
	String pos;
	ImageView vtype, rvtype;
	RelativeLayout listHeaderView;
	private ProgressBar pBar;
	String vehicle_type, make, model, rno, inumber, locations, selected_date,
			selected_time, report_type, comments, vehicle_id, report_id;
	JSONArray jsonarr, jsonMainArr;
	Button recover;
	View rootView;
	String recover_url = Data.url + "vehicleRecovered.php";
	private AsyncTask<Void, Void, Void> recoverv;

	DisplayImageOptions options;
	protected ImageLoader imageLoader;
	String spic1, spic2, spic3;;
	double LATITUDE, LONGITUDE;

	public static Myupdates newInstance() {

		Myupdates f = new Myupdates();
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.myupdates, container, false);
		imageLoader = ImageLoader.getInstance();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getActivity()).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(50)).build();

		addetails = (RelativeLayout) rootView.findViewById(R.id.adddetails);
		noupdate = (RelativeLayout) rootView.findViewById(R.id.stay);
		main = (RelativeLayout) rootView.findViewById(R.id.main);
		Vrecover = (RelativeLayout) rootView.findViewById(R.id.ress);
		go = (Button) rootView.findViewById(R.id.go);
		pBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);

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
				pos = "Not Found";
			}
			vinfo = new getinfo().execute();
			data = (ListView) rootView.findViewById(R.id.list);
			final View v = (RelativeLayout) inflater.inflate(
					R.layout.updateheader, null);
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
					// TODO Auto-generated method stub
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
					// TODO Auto-generated method stub
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setMessage("Vehicle Recovered")
							.setCancelable(false)
							.setMessage("Are you sure")
							.setNegativeButton("No",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub

										}
									})
							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											recoverv = new rec().execute();

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

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Register");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(recover_url);
			System.out.println(recover_url);

			JSONObject json = new JSONObject();
			try {
				info.device();
				info.showInfo(getActivity());
				json.put("userId", info.user_id);
				json.put("pin", info.pin);
				json.put("latitude", LATITUDE);
				json.put("longitude", LONGITUDE);
				json.put("reportId", report_id);
				json.put("vehicleId", vehicle_id);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdfs = new SimpleDateFormat("HH:mm");
				String currentTime = sdfs.format(new Date());
				String currentDate = sdf.format(new Date());
				json.put("date", currentDate);
				json.put("time", currentTime);
				json.put("location", pos);
				// json.put("make", info.manufacturer);
				// json.put("os", "Android" + " " + info.Version);
				// json.put("model", info.model);
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

						main.setVisibility(View.GONE);
						Vrecover.setVisibility(View.VISIBLE);
						TextView rname, rreg, rtype, rdate, rtime, rlocation;
						Button done, ins, pol;
						TextView update = (TextView) rootView
								.findViewById(R.id.update);
						rname = (TextView) rootView.findViewById(R.id.namess);
						rreg = (TextView) rootView.findViewById(R.id.regss);
						rtype = (TextView) rootView.findViewById(R.id.typess);
						rdate = (TextView) rootView.findViewById(R.id.datess);
						rtime = (TextView) rootView.findViewById(R.id.timess);
						rlocation = (TextView) rootView
								.findViewById(R.id.locationss);
						done = (Button) rootView.findViewById(R.id.vrech);
						ins = (Button) rootView.findViewById(R.id.ins);
						pol = (Button) rootView.findViewById(R.id.pol);
						rname.setText(model);
						rreg.setText("Registration Number:" + " " + rno);
						rtype.setText(report_type);
						rdate.setText(dateformate(selected_date));
						rtime.setText(selected_time);
						rlocation.setText(locations);
						if (inumber.isEmpty()) {
							ins.setVisibility(View.GONE);
						} else {
							ins.setVisibility(View.VISIBLE);
						}
						ins.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent call = new Intent(Intent.ACTION_CALL);
								call.setData(Uri.parse("tel:" + inumber));
								startActivity(call);
							}
						});
						pol.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent call = new Intent(Intent.ACTION_CALL);
								call.setData(Uri.parse("tel:" + "131444"));
								startActivity(call);
							}
						});
						update.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								main.setVisibility(View.VISIBLE);
								Vrecover.setVisibility(View.GONE);
							}
						});
						done.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								Intent next = new Intent(getActivity(),
										MainActivity.class);
								startActivity(next);
								getActivity().finish();
							}
						});

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

		name.setText(model);
		reg.setText("Registration Number:" + " " + rno);
		type.setText(report_type);
		date.setText(dateformate(selected_date));
		time.setText(selected_time);
		location.setText(locations);

		if (vehicle_type.equalsIgnoreCase("Car")) {
			vtype.setImageResource(R.drawable.ic_car);
		} else if (vehicle_type.equalsIgnoreCase("Bicycle")) {
			vtype.setImageResource(R.drawable.ic_cycle);
		} else if (vehicle_type.equalsIgnoreCase("MotorCycle")) {
			vtype.setImageResource(R.drawable.ic_bike);
		} else {
			vtype.setImageResource(R.drawable.ic_other);
		}

	}

	private class getinfo extends AsyncTask<Void, Void, Void> {
		String success, mess, response, qus;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pBar.setVisibility(View.VISIBLE);

		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(myUpdates_url);

			JSONObject json = new JSONObject();
			try {
				info.device();
				info.showInfo(getActivity());
				json.put("userId", info.user_id);
				json.put("pin", info.pin);
				json.put("make", info.manufacturer);
				json.put("os", "Android" + " " + info.Version);
				json.put("model", info.model);
				gps = new GPSTracker(getActivity());
				if (gps.canGetLocation()) {
					double LATITUDE = gps.getLatitude();
					double LONGITUDE = gps.getLongitude();
					json.put("latitude", LATITUDE);
					json.put("longitude", LONGITUDE);

				} else {
					json.put("latitude", 0);
					json.put("longitude", 0);
				}
				System.out.println("Element1-->" + json);
				postMethod.setHeader("Content-Type", "application/json");
				postMethod.setEntity(new ByteArrayEntity(json.toString()
						.getBytes("UTF8")));
				String response = httpClient
						.execute(postMethod, resonseHandler);
				Log.e("response :", response);
				JSONObject profile = new JSONObject(response);
				jsonMainArr = profile.getJSONArray("response");
				jsonarr = profile.getJSONArray("sightings");
				success = profile.getString("status");
				mess = profile.getString("message");
				vehicle_id = jsonMainArr.getJSONObject(0).getString(
						"vehicle_id");
				vehicle_type = jsonMainArr.getJSONObject(0).getString(
						"vehicle_type");
				report_id = jsonMainArr.getJSONObject(0).getString("report_id");
				make = jsonMainArr.getJSONObject(0).getString("make");
				model = jsonMainArr.getJSONObject(0).getString("model");
				rno = jsonMainArr.getJSONObject(0).getString(
						"registration_serial_no");
				inumber = jsonMainArr.getJSONObject(0).getString(
						"insurance_company_number");
				locations = jsonMainArr.getJSONObject(0).getString("location");
				selected_date = jsonMainArr.getJSONObject(0).getString(
						"selected_date");
				selected_time = jsonMainArr.getJSONObject(0).getString(
						"selected_time");
				report_type = jsonMainArr.getJSONObject(0).getString(
						"report_type");
				comments = jsonMainArr.getJSONObject(0).getString("comments");

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
						pBar.setVisibility(View.GONE);
						String[] datespilt = selected_time.split("\\:");
						selected_time = datespilt[0] + ":" + datespilt[1];
						addetails.setVisibility(View.GONE);
						main.setVisibility(View.VISIBLE);
						if ((jsonarr.length() < 1)) {
							noupdate.setVisibility(View.VISIBLE);
						} else {
							data.setAdapter(new ffAdapter());
						}
						onchange();

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
			ImageView pic1, pic2, pic3;
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!(position == 0)) {
				j = position * 3;
			}
			if (comm.getText().toString().isEmpty()) {
				comm.setVisibility(View.GONE);
			}

			String[] imageUrls = { spic1, spic2, spic3 };
			for (int i = 0; i < imageUrls.length; i++) {
				Updates.mStrings.add(imageUrls[i]);

			}
			ImageView arr[] = { pic1, pic2, pic3 };
			for (int i = j; i < j + 3; i++) {

				imageLoader.displayImage(Updates.mStrings.get(i), arr[x],
						options, animateFirstListener);
				arr[x].setVisibility(View.VISIBLE);
				if (Updates.mStrings.get(i).isEmpty()) {
					arr[x].setVisibility(View.GONE);
				}
				x++;
			}

			pic1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(), Fullimage.class);
					intent.putExtra("IMAGES",
							Updates.mStrings.get((position * 3)));
					startActivity(intent);
				}
			});
			pic2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(), Fullimage.class);
					intent.putExtra("IMAGES",
							Updates.mStrings.get((position * 3) + 1));
					startActivity(intent);

				}
			});
			pic3.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(), Fullimage.class);
					intent.putExtra("IMAGES",
							Updates.mStrings.get((position * 3) + 2));
					startActivity(intent);

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
			sdf.applyPattern("E,MMMM dd,yyyy");
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
