package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.OnErrorListener;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.Logger;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

public class LoginActivity extends Activity implements TextWatcher,
		OnKeyListener {
	Button register, login;
	RelativeLayout main, login_main, forgot_main, forgot_qus;
	Animation slide_in_right, slide_in_left, slide_out_left, slide_out_right;
	TextView skip, pint;
	private AsyncTask<Void, Void, Void> check;
	ProgressDialog pDialog;
	EditText id, pin1, pin2, pin3, pin4;
	EditText forgot_id;
	Connection_Detector cd = new Connection_Detector(this);
	boolean IsInternetPresent, idcheck;
	String forgot_url = Data.url + "forgotPinEmail.php";
	String forgot_qusurl = Data.url + "forgotPinAnswer.php";

	String login_url = Data.url + "login.php";
	String fb_url = Data.url + "fbLoginRegister.php";
	Data info = new Data();
	DatabaseHandler db;
	SQLiteDatabase dbb;
	String fbemail, fbfname, fblname, fbdob, fbgender, fbid, fbtoken;
	SharedPreferences atPrefs;
	IntentFilter apidUpdateFilter;
	String userid;
	EditText ans;

	downlaod d;
	File sdRoot;
	String dir;
	JSONArray jsonVehicleArr;

	// private SimpleFacebook mSimpleFacebook;
	// protected static final String TAG = LoginActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_activity);
		register = (Button) findViewById(R.id.register);
		login = (Button) findViewById(R.id.login);
		// facebook = (Button) findViewById(R.id.facebook);
		info = new Data();
		d = new downlaod();
		main = (RelativeLayout) findViewById(R.id.mainl);
		apidUpdateFilter = new IntentFilter();
		apidUpdateFilter.addAction(PushManager.ACTION_REGISTRATION_FINISHED);
		// mSimpleFacebook = SimpleFacebook.getInstance(LoginActivity.this);
		atPrefs = PreferenceManager
				.getDefaultSharedPreferences(LoginActivity.this);

		db = new DatabaseHandler(LoginActivity.this);
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();
		forgot_main = (RelativeLayout) findViewById(R.id.forgot_main);
		skip = (TextView) findViewById(R.id.skip);
		login_main = (RelativeLayout) findViewById(R.id.login_main);
		forgot_qus = (RelativeLayout) findViewById(R.id.forgot_sub);
		pint = (TextView) findViewById(R.id.textView1);

		slide_in_right = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.slide_in_right);
		slide_in_left = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.slide_in_left);
		slide_out_left = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.slide_out_left);
		slide_out_right = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.slide_out_right);
		skip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent next = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(next);
				finish();
			}
		});

		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent next = new Intent(getApplicationContext(),
						RegisterActivity.class);
				startActivity(next);
				finish();
			}
		});
		// get hash key
		// try {
		// PackageInfo info = getPackageManager().getPackageInfo(
		// "com.emgeesons.crime_stoppers.vehicle_security",
		// PackageManager.GET_SIGNATURES);
		// for (Signature signature : info.signatures) {
		// MessageDigest md = MessageDigest.getInstance("SHA");
		// md.update(signature.toByteArray());
		// String sign = Base64
		// .encodeToString(md.digest(), Base64.DEFAULT);
		// Log.d("KeyHash:", sign);
		// System.out.println(sign);
		// }
		// } catch (NameNotFoundException e) {
		//
		// } catch (NoSuchAlgorithmException e) {
		//
		// }
		LoginButton authButton = (LoginButton) findViewById(R.id.facebook);
		// authButton.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// final Handler handler = new Handler();
		// Runnable runnable = new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// main.setVisibility(View.GONE);
		// skip.setVisibility(View.GONE);
		// handler.postDelayed(this, 10000);
		// }
		// };
		// handler.post(runnable);
		// }
		// });

		authButton.setOnErrorListener(new OnErrorListener() {

			@Override
			public void onError(FacebookException error) {
				Log.i("TAG", "Error " + error.getMessage());
			}
		});
		// set permission list, Don't foeget to add email

		authButton.setReadPermissions(Arrays.asList("public_profile", "email",
				"user_birthday"));

		// authButton.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);

		// session state call back event
		authButton.setSessionStatusCallback(new Session.StatusCallback() {

			@Override
			public void call(final Session session, SessionState state,
					Exception exception) {

				if (session.isOpened()) {
					Log.i("TAG", "Access Token" + session.getAccessToken());

					Request.newMeRequest(session,
							new Request.GraphUserCallback() {

								// callback after Graph API response with user
								// object

								@Override
								public void onCompleted(GraphUser user,
										Response response) {
									System.out.println(response);
									if (user != null) {

										fbemail = user.asMap().get("email")
												.toString();
										fbfname = user.getFirstName();
										fblname = user.getLastName();
										fbdob = String.valueOf(user
												.getBirthday());
										fbgender = user.asMap().get("gender")
												.toString();
										fbid = user.getId();
										fbtoken = session.getAccessToken();
										Log.i("info", fbemail + fbfname
												+ fblname + fbdob + fbgender
												+ fbid + fbtoken);
										check = new fb().execute();

									}
								}

							}).executeAsync();

				}

			}
		});
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				login_main.setVisibility(View.VISIBLE);
				login_main.startAnimation(slide_in_right);
				main.setVisibility(View.GONE);
				main.startAnimation(slide_out_left);
				Loginact();

			}

		});

	}

	@Override
	public void onBackPressed() {

		if (main.getVisibility() == View.VISIBLE) {

			super.onBackPressed();
		}

		if (login_main.getVisibility() == View.VISIBLE) {
			login_main.setVisibility(View.GONE);
			login_main.startAnimation(slide_out_right);
			main.setVisibility(View.VISIBLE);
			main.startAnimation(slide_in_left);

		}

		if (forgot_main.getVisibility() == View.VISIBLE) {
			forgot_main.setVisibility(View.GONE);
			forgot_main.startAnimation(slide_out_right);
			login_main.setVisibility(View.VISIBLE);
			login_main.startAnimation(slide_in_left);

		}

		if (forgot_qus.getVisibility() == View.VISIBLE) {
			forgot_qus.setVisibility(View.GONE);
			forgot_qus.startAnimation(slide_out_right);
			forgot_main.setVisibility(View.VISIBLE);
			forgot_main.startAnimation(slide_in_left);

		}

	};

	// Login listener
	// final OnLoginListener onLoginListener = new OnLoginListener() {
	//
	// @Override
	// public void onFail(String reason) {
	// Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_LONG)
	// .show();
	// Log.w(TAG, "Failed to login");
	// }
	//
	// @Override
	// public void onException(Throwable throwable) {
	// Toast.makeText(getApplicationContext(), throwable.toString(),
	// Toast.LENGTH_LONG).show();
	// Log.e(TAG, "Bad thing happened", throwable);
	// }
	//
	// @Override
	// public void onThinking() {
	// // show progress bar or something to the user while login is
	// // happening
	// Toast.makeText(getApplicationContext(), "Thinking".toString(),
	// Toast.LENGTH_LONG).show();
	// }
	//
	// @Override
	// public void onLogin() {
	// // change the state of the button or do whatever you want
	// mSimpleFacebook.getProfile(onProfileListener);
	// // Intent next = new Intent(getApplicationContext(),
	// // HomescreenActivity.class);
	// // startActivity(next);
	//
	// // Toast.makeText(getApplicationContext(), "UIState".toString(),
	// // Toast.LENGTH_LONG).show();
	//
	// }
	//
	// @Override
	// public void onNotAcceptingPermissions(Permission.Type type) {
	// // toast(String.format("You didn't accept %s permissions",
	// // type.name()));
	// }
	// };
	//
	// final OnProfileListener onProfileListener = new OnProfileListener() {
	// @Override
	// public void onComplete(Profile profile) {
	//
	// System.out.println(profile.getId() + profile.getHometown()
	// + profile.getBirthday() + profile.getGender()
	// + profile.getLocation() + profile.getPicture());
	// Log.i(TAG, "My profile id = " + profile.getId());
	// }
	//
	// /*
	// * You can override other methods here: onThinking(), onFail(String
	// * reason), onException(Throwable throwable)
	// */
	// };

	private void Loginact() {
		TextView cancel, forgot;
		Button login;
		cancel = (TextView) findViewById(R.id.cancel);
		forgot = (TextView) findViewById(R.id.forgot);
		login = (Button) findViewById(R.id.login_btn);
		id = (EditText) findViewById(R.id.id);
		pin1 = (EditText) findViewById(R.id.pin1);
		pin2 = (EditText) findViewById(R.id.pin2);
		pin3 = (EditText) findViewById(R.id.pin3);
		pin4 = (EditText) findViewById(R.id.pin4);
		pin1.addTextChangedListener(this);
		pin2.addTextChangedListener(this);
		pin3.addTextChangedListener(this);
		pin4.addTextChangedListener(this);
		id.addTextChangedListener(this);
		pin1.setOnKeyListener(this);
		pin2.setOnKeyListener(this);
		pin3.setOnKeyListener(this);
		pin4.setOnKeyListener(this);

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				main.setVisibility(View.VISIBLE);
				main.startAnimation(slide_in_left);
				login_main.setVisibility(View.GONE);
				login_main.startAnimation(slide_out_right);
			}
		});
		login.setOnClickListener(new OnClickListener() {

			@SuppressLint("ResourceAsColor")
			@Override
			public void onClick(View v) {
				if (!id.getText()
						.toString()
						.matches(
								"[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")) {
					id.setTextColor(getResources().getColor(R.color.red));
					id.setHintTextColor(getResources().getColor(R.color.red));
					idcheck = false;

				}
				if (pin1.getText().toString().isEmpty()
						|| pin2.getText().toString().isEmpty()
						|| pin3.getText().toString().isEmpty()
						|| pin4.getText().toString().isEmpty()) {

					pint.setTextColor(getResources().getColor(R.color.red));

				}
				if (idcheck == true) {
					IsInternetPresent = cd.isConnectingToInternet();
					if (IsInternetPresent == false) {
						cd.showNoInternetPopup();
					} else {
						check = new Loginid().execute();
					}

				}

			}
		});
		forgot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				forgot_main.setVisibility(View.VISIBLE);
				forgot_main.startAnimation(slide_in_right);
				login_main.setVisibility(View.GONE);
				login_main.startAnimation(slide_out_left);
				forgotact();
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_, menu);
		return true;
	}

	void forgotact() {
		TextView cancel;
		Button Proceed;
		forgot_id = (EditText) findViewById(R.id.forgot_id);
		forgot_id.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				forgot_id.setTextColor(getResources().getColor(R.color.black));
			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
		Proceed = (Button) findViewById(R.id.Proceed);
		cancel = (TextView) findViewById(R.id.forgot_cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				login_main.setVisibility(View.VISIBLE);
				login_main.startAnimation(slide_in_left);
				forgot_main.setVisibility(View.GONE);
				forgot_main.startAnimation(slide_out_right);
			}
		});
		Proceed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!forgot_id
						.getText()
						.toString()
						.matches(
								"[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")) {
					forgot_id
							.setTextColor(getResources().getColor(R.color.red));
					forgot_id.setHintTextColor(getResources().getColor(
							R.color.red));

				} else {

					IsInternetPresent = cd.isConnectingToInternet();
					if (IsInternetPresent == false) {
						cd.showNoInternetPopup();
					} else {
						check = new forgotpin().execute();
					}
				}
			}
		});
	}

	private class forgotpin extends AsyncTask<Void, Void, Void> {
		String success, mess, response, qus;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage("Sending Info");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(forgot_url);
			JSONArray jsonMainArr;
			JSONObject json = new JSONObject();
			try {
				info.device();
				json.put("email", forgot_id.getText().toString());
				json.put("make", info.manufacturer);
				json.put("os", "Android" + " " + info.Version);
				json.put("model", info.model);

				System.out.println("Element1-->" + json);
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
				qus = jsonMainArr.getJSONObject(0).getString(
						"security_question");
				userid = jsonMainArr.getJSONObject(0).getString("userId");

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

						forgot_main.setVisibility(View.GONE);
						forgot_main.startAnimation(slide_out_left);
						forgot_qus.setVisibility(View.VISIBLE);
						forgot_qus.startAnimation(slide_in_right);
						final TextView qustion = (TextView) findViewById(R.id.qus);
						Button submit = (Button) findViewById(R.id.Submit);
						TextView cancel = (TextView) findViewById(R.id.forgotqus_cancel);
						ans = (EditText) findViewById(R.id.answer);
						qustion.setText(qus.toString());
						cancel.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								forgot_main.setVisibility(View.VISIBLE);
								forgot_main.startAnimation(slide_in_left);
								forgot_qus.setVisibility(View.GONE);
								forgot_qus.startAnimation(slide_out_right);
							}
						});
						ans.addTextChangedListener(new TextWatcher() {

							@Override
							public void onTextChanged(CharSequence arg0,
									int arg1, int arg2, int arg3) {

							}

							@Override
							public void beforeTextChanged(CharSequence arg0,
									int arg1, int arg2, int arg3) {
								ans.setTextColor(getResources().getColor(
										R.color.black));
							}

							@Override
							public void afterTextChanged(Editable arg0) {

							}
						});
						submit.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (ans.getText().toString().length() <= 2) {
									ans.setTextColor(getResources().getColor(
											R.color.red));
									ans.setHintTextColor(getResources()
											.getColor(R.color.red));

								} else {

									IsInternetPresent = cd
											.isConnectingToInternet();
									if (IsInternetPresent == false) {
										cd.showNoInternetPopup();
									} else {
										check = new qusans().execute();
									}
								}
							}
						});

					}

				});
			}
			// response failure
			else if (success.equals("failure")) {

				runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								LoginActivity.this).create();
						Dialog.setTitle("Incorrect Email");
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
								LoginActivity.this).create();
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
			pDialog.dismiss();

		}
	}

	private class qusans extends AsyncTask<Void, Void, Void> {
		String success, mess, response;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage("Sending Info...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(forgot_qusurl);
			JSONArray jsonMainArr;
			JSONObject json = new JSONObject();
			try {
				info.device();
				json.put("userId", userid);
				json.put("securityAnswer", ans.getText().toString());
				json.put("make", info.manufacturer);
				json.put("os", "Android" + " " + info.Version);
				json.put("model", info.model);
				System.out.println("Element1-->" + json);
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

				runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								LoginActivity.this).create();
						Dialog.setTitle("Email Sent");
						Dialog.setIcon(R.drawable.ic_action_done);
						Dialog.setMessage(mess);
						Dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										login_main.setVisibility(View.VISIBLE);
										login_main
												.startAnimation(slide_in_right);
										forgot_qus.setVisibility(View.GONE);
										forgot_qus
												.startAnimation(slide_out_left);
									}
								});
						Dialog.setCancelable(true);
						Dialog.show();
					}

				});
			}
			// response failure
			else if (success.equals("failure")) {

				runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								LoginActivity.this).create();
						Dialog.setTitle("Incorrect Answer");

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

			} else if (success.equals("invalid")) {
				runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								LoginActivity.this).create();
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
			pDialog.dismiss();

		}
	}

	private class fb extends AsyncTask<Void, Void, Void> {
		String success, mess, response;
		String user_id, fName, lName, email, mobileNumber, dob, gender,
				licenseNo, street, suburb, postcode, dtModified, fbId, fbToken,
				cname, cnumber, sques, sans, photourl, photoname, pin, points,
				exp;
		int profilecom;
		JSONArray jsonMainArr;

		// vehicle
		int vid;
		String vtype, vmake, vmodel, reg, vstatus;
		String fbdobs = "";
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage("Logging in ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(fb_url);

			JSONObject json = new JSONObject();
			try {
				info.device();
				json.put("firstName", fbfname);
				json.put("lastName", fblname);
				json.put("mobileNumber", "");
				
				try {
					String[] fbdatespilt = fbdob.split("\\/");
					String y = fbdatespilt[2];
					String m = fbdatespilt[1];
					String d = fbdatespilt[0];
					fbdobs = String.valueOf(y + "-" + m + "-" + d);
					json.put("dob", fbdobs);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				json.put("gender", fbgender);
				json.put("fbId", fbid);
				json.put("fbToken", fbtoken);
				json.put("email", fbemail);
				json.put("make", info.manufacturer);
				json.put("os", "Android" + " " + info.Version);
				json.put("model", info.model);

				System.out.println("Element1-->" + json);
				postMethod.setHeader("Content-Type", "application/json");
				postMethod.setEntity(new ByteArrayEntity(json.toString()
						.getBytes("UTF8")));
				String response = httpClient
						.execute(postMethod, resonseHandler);
				Log.e("response :", response);
				JSONObject profile = new JSONObject(response);
				jsonMainArr = profile.getJSONArray("response");
				jsonVehicleArr = profile.getJSONArray("vehicles");
				success = profile.getString("status");
				mess = profile.getString("message");
				try {
					user_id = jsonMainArr.getJSONObject(0).getString("user_id");
					// fName = jsonMainArr.getJSONObject(0)
					// .getString("first_name");
					// lName =
					// jsonMainArr.getJSONObject(0).getString("last_name");
					// email = jsonMainArr.getJSONObject(0).getString("email");
					// mobileNumber = jsonMainArr.getJSONObject(0).getString(
					// "mobile_number");
					// String[] datespilt = jsonMainArr.getJSONObject(0)
					// .getString("dob").split("\\s+");
					// dob = String.valueOf(datespilt[0]);
					// gender =
					// jsonMainArr.getJSONObject(0).getString("gender");
					// licenseNo = jsonMainArr.getJSONObject(0).getString(
					// "license_no");
					// street =
					// jsonMainArr.getJSONObject(0).getString("street");
					// suburb =
					// jsonMainArr.getJSONObject(0).getString("suburb");
					// postcode = jsonMainArr.getJSONObject(0).getString(
					// "postcode");
					// dtModified = jsonMainArr.getJSONObject(0).getString(
					// "modified_at");
					// fbId = jsonMainArr.getJSONObject(0).getString("fb_id");
					// fbToken = jsonMainArr.getJSONObject(0)
					// .getString("fb_token");
					// // cname = jsonMainArr.getJSONObject(0).getString(
					// // "emergency_contact");
					// // cnumber = jsonMainArr.getJSONObject(0).getString(
					// // "emergency_contact_number");
					// sques = jsonMainArr.getJSONObject(0).getString(
					// "security_question");
					// sans = jsonMainArr.getJSONObject(0).getString(
					// "security_answer");
					// profilecom = jsonMainArr.getJSONObject(0).getInt(
					// "profile_completed");
					pin = jsonMainArr.getJSONObject(0).getString("pin");
					// points = jsonMainArr.getJSONObject(0).getString(
					// "samaritan_points");
					// photourl = jsonMainArr.getJSONObject(0).getString(
					// "photo_url");
					// int pos = photourl.lastIndexOf("/");
					// photoname = photourl.substring(pos + 1);
				} catch (JSONException e) {
					e.printStackTrace();
				}

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

						// db = new DatabaseHandler(LoginActivity.this);
						// PersonalData data = new PersonalData(user_id,
						// fbfname,
						// fblname, fbemail, "", fbdob, fbgender, "", "",
						// "", "", "", fbid, fbtoken, "", "", pin, "", "",
						// "");
						// db.updateprofileData(data);
						

						SplashscreenActivity.fblogin = false;
						if (mess.equalsIgnoreCase("New User")) {
							Intent next = new Intent(LoginActivity.this,
									FbNewuser.class);

							db = new DatabaseHandler(LoginActivity.this);
							PersonalData data = new PersonalData(user_id,
									fbfname, fblname, fbemail, "", fbdobs,
									fbgender, "", "", "", "", "", fbId,
									fbToken, "", "", pin, "", "", "");

							db.updateprofileData(data);
							next.putExtra("userid", user_id);
							next.putExtra("email", fbemail);
							next.putExtra("oldpin", pin);

							startActivity(next);
							finish();

						} else {
							try {
								fName = jsonMainArr.getJSONObject(0).getString(
										"first_name");
								lName = jsonMainArr.getJSONObject(0).getString(
										"last_name");
								email = jsonMainArr.getJSONObject(0).getString(
										"email");
								mobileNumber = jsonMainArr.getJSONObject(0)
										.getString("mobile_number");
								String[] datespilt = jsonMainArr
										.getJSONObject(0).getString("dob")
										.split("\\s+");
								dob = String.valueOf(datespilt[0]);
								gender = jsonMainArr.getJSONObject(0)
										.getString("gender");
								licenseNo = jsonMainArr.getJSONObject(0)
										.getString("license_no");
								street = jsonMainArr.getJSONObject(0)
										.getString("street");
								suburb = jsonMainArr.getJSONObject(0)
										.getString("suburb");
								postcode = jsonMainArr.getJSONObject(0)
										.getString("postcode");
								dtModified = jsonMainArr.getJSONObject(0)
										.getString("modified_at");
								fbId = jsonMainArr.getJSONObject(0).getString(
										"fb_id");
								fbToken = jsonMainArr.getJSONObject(0)
										.getString("fb_token");
								// cname =
								// jsonMainArr.getJSONObject(0).getString(
								// "emergency_contact");
								// cnumber =
								// jsonMainArr.getJSONObject(0).getString(
								// "emergency_contact_number");
								sques = jsonMainArr.getJSONObject(0).getString(
										"security_question");
								sans = jsonMainArr.getJSONObject(0).getString(
										"security_answer");
								profilecom = jsonMainArr.getJSONObject(0)
										.getInt("profile_completed");
								points = jsonMainArr.getJSONObject(0)
										.getString("samaritan_points");
								photourl = jsonMainArr.getJSONObject(0)
										.getString("photo_url");
								int pos = photourl.lastIndexOf("/");
								photoname = photourl.substring(pos + 1);
							} catch (JSONException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							db = new DatabaseHandler(LoginActivity.this);
							PersonalData data = new PersonalData(user_id,
									fName, lName, email, mobileNumber, dob,
									gender, licenseNo, street, suburb,
									postcode, dtModified, fbId, fbToken, cname,
									cnumber, pin, sques, sans, points);

							db.updateprofileData(data);

							atPrefs.edit()
									.putInt(SplashscreenActivity.progress,
											profilecom).commit();
							atPrefs.edit().putBoolean(info.checkllogin, false)
									.commit();
							atPrefs.edit()
									.putString(
											SplashscreenActivity.profile_pic,
											"").commit();

							SQLiteDatabase dbb = db.getReadableDatabase();
							dbb.execSQL("delete from Vehicle_info");

							for (int i = 0; i < jsonVehicleArr.length(); i++) {

								try {
									vid = jsonVehicleArr.getJSONObject(i)
											.getInt("vehicle_id");
									vtype = jsonVehicleArr.getJSONObject(i)
											.getString("vehicle_type");
									vmake = jsonVehicleArr.getJSONObject(i)
											.getString("vehicle_make");
									vmodel = jsonVehicleArr.getJSONObject(i)
											.getString("vehicle_model");
									reg = jsonVehicleArr
											.getJSONObject(i)
											.getString("registration_serial_no");
									vstatus = jsonVehicleArr.getJSONObject(i)
											.getString("vehicle_status");
									exp = jsonVehicleArr.getJSONObject(i)
											.getString("insurance_expiry_date");
									String[] date = exp.split("\\s+");
									db = new DatabaseHandler(LoginActivity.this);
									VehicleData datas = new VehicleData(vid,
											vtype, vmake, vmodel, "", "", "",
											"", "", reg, "", "", date[0],
											vstatus, "", "","");
									db.insertvehicleData(datas);

									ParkingData datass = new ParkingData(
											String.valueOf(vid), vmodel, "",
											"", "", "", vtype);
									db.inserparkData(datass);

									if (date[0].equalsIgnoreCase("0000-00-00")) {
									} else {
										String toParse = date[0] + " " + 8
												+ ":" + 00;
										SimpleDateFormat formatter = new SimpleDateFormat(
												"yyyy-MM-dd h:m");
										Date dates = formatter.parse(toParse);
										long millis = dates.getTime();
										long time = millis - 604800000;

										dbb.execSQL("UPDATE vehicle_info SET vehicle_expmil = '"
												+ time
												+ "'WHERE vehicle_id ='"
												+ vid + "'");
										NotificationAlarm
												.CancelAlarm(LoginActivity.this);
										NotificationAlarm
												.SetAlarm(LoginActivity.this);
									}

								} catch (JSONException e) {
									e.printStackTrace();
								} catch (java.text.ParseException e) {
									e.printStackTrace();
								}

							}
							Thread thread = new Thread(new Runnable() {
								@Override
								public void run() {
									sdRoot = Environment
											.getExternalStorageDirectory();
									dir = "My Wheel/";
									String dowlaod = "/My Wheel";
									File photo = new File(sdRoot, dir
											+ photoname);
									if (photo.exists()) {
										atPrefs.edit()
												.putString(
														SplashscreenActivity.profile_pic,
														photoname).commit();
									} else {
										try {
											d.DownloadFromUrl(photourl,
													photoname, dowlaod, dir);
											atPrefs.edit()
													.putString(
															SplashscreenActivity.profile_pic,
															photoname).commit();
										} catch (Exception e) {
											e.printStackTrace();
										}
									}

								}
							});
							thread.start();
							if (mess.equalsIgnoreCase("Complete Profile")) {

								Intent next = new Intent(LoginActivity.this,
										FbNewuser.class);
								next.putExtra("userid", user_id);
								next.putExtra("email", fbemail);
								next.putExtra("oldpin", pin);

								startActivity(next);
								finish();

							} else {
								atPrefs.edit().putBoolean(Data.coach, false)
										.commit();
								AirshipConfigOptions options = AirshipConfigOptions
										.loadDefaultOptions(LoginActivity.this);
								UAirship.takeOff(getApplication(), options);
								PushManager.shared().setAlias(
										String.valueOf(user_id));

								// Tags
								HashSet<String> tags = new HashSet<String>();
								tags.add(fName);
								tags.add(lName);
								PushManager.shared().setTags(tags);
								PushManager.enablePush();
								PushManager.shared().setIntentReceiver(
										IntentReceiver.class);
								String apid = PushManager.shared().getAPID();
								Logger.info("My Application onCreate - App APID: "
										+ apid);
								atPrefs.edit()
										.putBoolean(info.checkllogin, false)
										.commit();
								Intent next = new Intent(
										getApplicationContext(),
										MainActivity.class);

								startActivity(next);
								finish();
							}
						}
					}
				});
			}
			// response failure
			else if (success.equals("failure")) {

				runOnUiThread(new Runnable() {

					public void run() {
						final AlertDialog Dialog = new AlertDialog.Builder(
								LoginActivity.this).create();
						Dialog.setTitle("Email already exists");
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
								LoginActivity.this).create();
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
			pDialog.dismiss();

		}
	}

	private class Loginid extends AsyncTask<Void, Void, Void> {
		String success, mess, response;
		String user_id, fName, lName, email, mobileNumber, dob, gender,
				licenseNo, street, suburb, postcode, dtModified, fbId, fbToken,
				cname, cnumber, sques, sans, photourl, photoname, pin, points,
				exp;
		int profilecom;

		// vehicle
		int vid;
		String vtype, vmake, vmodel, reg, vstatus;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage("Logging in …..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			ResponseHandler<String> resonseHandler = new BasicResponseHandler();
			HttpPost postMethod = new HttpPost(login_url);
			JSONArray jsonMainArr;
			JSONObject json = new JSONObject();
			try {
				info.device();
				json.put("email", id.getText().toString());
				pin = pin1.getText().toString() + pin2.getText().toString()
						+ pin3.getText().toString() + pin4.getText().toString();
				json.put("pin", pin);
				json.put("make", info.manufacturer);
				json.put("os", "Android" + " " + info.Version);
				json.put("model", info.model);

				System.out.println("Element1-->" + json);
				postMethod.setHeader("Content-Type", "application/json");
				postMethod.setEntity(new ByteArrayEntity(json.toString()
						.getBytes("UTF8")));
				String response = httpClient
						.execute(postMethod, resonseHandler);
				Log.e("response :", response);

				JSONObject profile = new JSONObject(response);
				jsonMainArr = profile.getJSONArray("response");
				jsonVehicleArr = profile.getJSONArray("vehicles");
				success = profile.getString("status");

				mess = profile.getString("message");
				user_id = jsonMainArr.getJSONObject(0).getString("user_id");
				fName = jsonMainArr.getJSONObject(0).getString("first_name");
				lName = jsonMainArr.getJSONObject(0).getString("last_name");
				email = jsonMainArr.getJSONObject(0).getString("email");
				mobileNumber = jsonMainArr.getJSONObject(0).getString(
						"mobile_number");
				String[] datespilt = jsonMainArr.getJSONObject(0)
						.getString("dob").split("\\s+");
				dob = String.valueOf(datespilt[0]);
				gender = jsonMainArr.getJSONObject(0).getString("gender");
				licenseNo = jsonMainArr.getJSONObject(0)
						.getString("license_no");
				street = jsonMainArr.getJSONObject(0).getString("street");
				suburb = jsonMainArr.getJSONObject(0).getString("suburb");
				postcode = jsonMainArr.getJSONObject(0).getString("postcode");
				dtModified = jsonMainArr.getJSONObject(0).getString("dob");
				fbId = jsonMainArr.getJSONObject(0).getString("fb_id");
				fbToken = jsonMainArr.getJSONObject(0).getString("fb_token");
				// cname = jsonMainArr.getJSONObject(0).getString(
				// "emergency_contact");
				// cnumber = jsonMainArr.getJSONObject(0).getString(
				// "emergency_contact_number");
				sques = jsonMainArr.getJSONObject(0).getString(
						"security_question");
				sans = jsonMainArr.getJSONObject(0)
						.getString("security_answer");
				profilecom = jsonMainArr.getJSONObject(0).getInt(
						"profile_completed");
				pin = jsonMainArr.getJSONObject(0).getString("pin");
				points = jsonMainArr.getJSONObject(0).getString(
						"samaritan_points");
				photourl = jsonMainArr.getJSONObject(0).getString("photo_url");
				int pos = photourl.lastIndexOf("/");
				photoname = photourl.substring(pos + 1);

			} catch (ClientProtocolException e) {
				System.out.println("ClientProtocolException");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("IOException");
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (success.equals("success")) {

				runOnUiThread(new Runnable() {

					public void run() {
						atPrefs.edit().putBoolean(Data.coach, false).commit();
						db = new DatabaseHandler(LoginActivity.this);
						PersonalData data = new PersonalData(user_id, fName,
								lName, email, mobileNumber, dob, gender,
								licenseNo, street, suburb, postcode,
								dtModified, fbId, fbToken, cname, cnumber, pin,
								sques, sans, points);

						db.updateprofileData(data);
						atPrefs.edit()
								.putInt(SplashscreenActivity.progress,
										profilecom).commit();
						atPrefs.edit().putBoolean(info.checkllogin, false)
								.commit();

						SQLiteDatabase dbb = db.getReadableDatabase();
						for (int i = 0; i < jsonVehicleArr.length(); i++) {

							try {
								vid = jsonVehicleArr.getJSONObject(i).getInt(
										"vehicle_id");
								vtype = jsonVehicleArr.getJSONObject(i)
										.getString("vehicle_type");
								vmake = jsonVehicleArr.getJSONObject(i)
										.getString("vehicle_make");
								vmodel = jsonVehicleArr.getJSONObject(i)
										.getString("vehicle_model");
								reg = jsonVehicleArr.getJSONObject(i)
										.getString("registration_serial_no");
								vstatus = jsonVehicleArr.getJSONObject(i)
										.getString("vehicle_status");
								exp = jsonVehicleArr.getJSONObject(i)
										.getString("insurance_expiry_date");
								String[] date = exp.split("\\s+");
								db = new DatabaseHandler(LoginActivity.this);
								VehicleData datas = new VehicleData(vid, vtype,
										vmake, vmodel, "", "", "", "", "", reg,
										"", "", date[0], vstatus, "", "","");
								db.insertvehicleData(datas);

								ParkingData datass = new ParkingData(
										String.valueOf(vid), vmodel, "", "",
										"", "", vtype);
								db.inserparkData(datass);

								if (date[0].equalsIgnoreCase("0000-00-00")) {
								} else {
									String toParse = date[0] + " " + 8 + ":"
											+ 00;
									SimpleDateFormat formatter = new SimpleDateFormat(
											"yyyy-MM-dd h:m");
									Date dates = formatter.parse(toParse);
									long millis = dates.getTime();
									long time = millis - 604800000;

									dbb.execSQL("UPDATE vehicle_info SET vehicle_expmil = '"
											+ time
											+ "'WHERE vehicle_id ='"
											+ vid + "'");
									NotificationAlarm
											.CancelAlarm(LoginActivity.this);
									NotificationAlarm
											.SetAlarm(LoginActivity.this);
								}

								//
								// NotificationData datass = new
								// NotificationData(
								// vid, vmodel, "");
								// db.insernotifiData(datass);
							} catch (JSONException e) {
								e.printStackTrace();
							} catch (java.text.ParseException e) {
								e.printStackTrace();
							}

						}
						Thread thread = new Thread(new Runnable() {
							@Override
							public void run() {
								sdRoot = Environment
										.getExternalStorageDirectory();
								dir = "My Wheel/";
								String dowlaod = "/My Wheel";
								File photo = new File(sdRoot, dir + photoname);
								if (photo.exists()) {
									atPrefs.edit()
											.putString(
													SplashscreenActivity.profile_pic,
													photoname).commit();
								} else {
									try {
										d.DownloadFromUrl(photourl, photoname,
												dowlaod, dir);
										atPrefs.edit()
												.putString(
														SplashscreenActivity.profile_pic,
														photoname).commit();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}

							}
						});
						AirshipConfigOptions options = AirshipConfigOptions
								.loadDefaultOptions(LoginActivity.this);
						UAirship.takeOff(getApplication(), options);
						PushManager.shared().setAlias(String.valueOf(user_id));

						// Tags
						HashSet<String> tags = new HashSet<String>();
						tags.add(fName);
						tags.add(lName);
						PushManager.shared().setTags(tags);
						PushManager.enablePush();
						PushManager.shared().setIntentReceiver(
								IntentReceiver.class);
						String apid = PushManager.shared().getAPID();
						Logger.info("My Application onCreate - App APID: "
								+ apid);
						thread.start();
						atPrefs.edit().putBoolean(info.checkllogin, false)
								.commit();
						Intent next = new Intent(LoginActivity.this,
								MainActivity.class);
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
								LoginActivity.this).create();
						Dialog.setTitle("Incorrect Email");
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
								LoginActivity.this).create();
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
			pDialog.dismiss();

		}
	}

	@Override
	public void afterTextChanged(Editable s) {

		if (pin1.hasFocus() && pin1.getText().toString().length() == 1) {
			pin2.requestFocus();
		}
		if (pin2.hasFocus() && pin2.getText().toString().length() == 1) {
			pin3.requestFocus();
		}
		if (pin3.hasFocus() && pin3.getText().toString().length() == 1) {
			pin4.requestFocus();
		}
		// hide keyboard
		if (pin4.hasFocus() && pin4.getText().toString().length() == 1) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(pin4.getWindowToken(), 0);
			pin4.clearFocus();

		}

		if (!(pin1.getText().toString().isEmpty()
				|| pin2.getText().toString().isEmpty()
				|| pin3.getText().toString().isEmpty() || pin4.getText()
				.toString().isEmpty())) {
			pint.setTextColor(getResources().getColor(R.color.black));

		}

	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// go back

		if (keyCode == KeyEvent.KEYCODE_DEL) {
			if (pin4.hasFocus()) {
				// pin4.setText("");
				pin3.requestFocus();
			} else if (pin3.hasFocus()) {
				// pin3.setText("");
				pin2.requestFocus();
			} else if (pin2.hasFocus()) {
				// pin2.setText("");
				pin1.requestFocus();

			}

		}

		return false;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		id.setTextColor(getResources().getColor(R.color.black));
		idcheck = true;
		// forgot_id.setTextColor(getResources().getColor(R.color.black));
		// ans.setTextColor(getResources().getColor(R.color.black));

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);

	}

}
