package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.Builder;
import com.facebook.Session.OpenRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class Share extends BaseActivity {
	CheckBox fb, tw;
	EditText fbtext, twtext;
	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	Button post, skip;
	int i = 1;
	Bundle extras;
	boolean isUseWebViewForAuthentication = false;
	boolean check = true;
	String as;
	// twitter callback
	SharedPreferences atPrefs;
	SharedPreferences sharepreferences;

	static String twittercallback = "true";
	String fphoto, tphoto;
	public static final String Shares = "Shares";
	ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.share);
		fb = (CheckBox) findViewById(R.id.fb);
		tw = (CheckBox) findViewById(R.id.tw);
		fbtext = (EditText) findViewById(R.id.fbtext);
		twtext = (EditText) findViewById(R.id.twtext);
		post = (Button) findViewById(R.id.post);
		skip = (Button) findViewById(R.id.Skip);
		extras = getIntent().getExtras();
		sharepreferences = getSharedPreferences(Shares, Context.MODE_PRIVATE);
		atPrefs = PreferenceManager.getDefaultSharedPreferences(Share.this);
		// if (extras != null && atPrefs.getBoolean(twittercallback, true)) {
		// photourl = extras.getString("link");
		// tphotourl = extras.getString("tlink");
		//
		// }

		if (!sharepreferences.getString("facebook", "").isEmpty()) {
			fbtext.setText(sharepreferences.getString("facebook", "appfb"));
			twtext.setText(sharepreferences.getString("twitter", "apptw"));
			fb.setChecked(sharepreferences.getBoolean("fb", false));
			tw.setChecked(sharepreferences.getBoolean("tw", false));
		}
		fphoto = atPrefs.getString(FilenewReport.fphoto, "fphoto");
		tphoto = atPrefs.getString(FilenewReport.tphoto, "tphoto");
		skip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Editor editor = sharepreferences.edit();

				editor.remove("facebook").commit();
				editor.remove("twitter").commit();
				editor.remove("fb").commit();
				editor.remove("tw").commit();
				Intent next = new Intent(getApplicationContext(),
						ReportSubmit.class);
				// sometime it remain in stackF
				next.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(next);
				finish();
			}
		});

		fb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					atPrefs.edit().putString(callcheck, "True").commit();
					publishStory();
				}
			}
		});
		tw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					atPrefs.edit().putString(callcheck, "True").commit();
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								// Your code goes here

								loginToTwitter();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

					thread.start();

				}
			}
		});
		post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// twitter post

				if (!tw.isChecked() && !fb.isChecked()) {

					AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							Share.this);

					// alertDialog.setTitle("Location Access Required");
					alertDialog
							.setMessage("Please select facebook or twitter to share");

					alertDialog.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});

					// Showing Alert Message
					alertDialog.show();

				} else {
					Editor editor = sharepreferences.edit();

					editor.remove("facebook").commit();
					editor.remove("twitter").commit();
					editor.remove("fb").commit();
					editor.remove("tw").commit();
					// Thread thread = new Thread() {
					// @Override
					// public void run() {

					if (tw.isChecked()) {

						new TwitterUpdateStatusTask().execute(twtext.getText()
								.toString());
					}
					// fb post
					if (fb.isChecked()) {

						Session session = Session.getActiveSession();
						session = Session
								.openActiveSessionFromCache(Share.this);
						Bundle postParams = new Bundle();
						postParams.putString("message", fbtext.getText()
								.toString() + " " + "#MyWheels");
						postParams.putString("name", "My Wheels");
						if (!fphoto.equalsIgnoreCase("fphoto")) {
							postParams.putString("picture", fphoto);
							postParams
									.putString(
											"link",
											"https://play.google.com/store/apps/details?id=com.emgeesons.crime_stoppers.vehicle_security");
						} else {

						}

						final Request request = new Request(session, "me/feed",
								postParams, HttpMethod.POST, callback);
						runOnUiThread(new Runnable() {
							public void run() {

								RequestAsyncTask task = new RequestAsyncTask(
										request);
								task.execute();
							}
						});
					}
					// }
					// };
					//
					// thread.start();
					pDialog = new ProgressDialog(Share.this);
					pDialog.setMessage("Sharing...");
					pDialog.setIndeterminate(false);
					pDialog.setCancelable(true);
					pDialog.show();
				}

			}
		});
		initControl();
	}

	class TwitterUpdateStatusTask extends AsyncTask<String, String, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				// Intent next = new Intent(getApplicationContext(),
				// ReportSubmit.class);
				// startActivity(next);
				// finish();
				gotonext();
				// Toast.makeText(getApplicationContext(), "Tweet successfully",
				// Toast.LENGTH_SHORT).show();
			} else {
				// Toast.makeText(getApplicationContext(), "Tweet failed",
				// Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				String accessTokenString = sharedPreferences.getString(
						ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
				String accessTokenSecret = sharedPreferences.getString(
						ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET,
						"");

				if (!accessTokenString.isEmpty()
						&& !accessTokenSecret.isEmpty()) {
					AccessToken accessToken = new AccessToken(
							accessTokenString, accessTokenSecret);
					// StatusUpdate ad = new StatusUpdate(
					String as = twtext.getText().toString() + " " + "#MyWheels";
					StatusUpdate status = new StatusUpdate(as);
					if (!tphoto.equalsIgnoreCase("tphoto")) {
						System.out.println("photo");
						File imageFile = new File(tphoto);
						status.setMedia(imageFile);
					}

					// twitter.updateStatus(status);

					// twitter4j.Status response = twitter.updateStatus(ad);

					twitter4j.Status statuss = TwitterUtil.getInstance()
							.getTwitterFactory().getInstance(accessToken)
							.updateStatus(status);
					return true;
				}

			} catch (TwitterException e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	private void gotonext() {
		// TODO Auto-generated method stub
		if (check == true) {
			check = false;
			Intent next = new Intent(getApplicationContext(),
					ReportSubmit.class);
			// sometime it remain in stack
			next.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(next);
			pDialog.dismiss();
			finish();

		}

	}

	private void initControl() {
		Uri uri = getIntent().getData();
		if (uri != null
				&& uri.toString().startsWith(
						ConstantValues.TWITTER_CALLBACK_URL)) {
			String verifier = uri
					.getQueryParameter(ConstantValues.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
			new TwitterGetAccessTokenTask().execute(verifier);
		} else
			new TwitterGetAccessTokenTask().execute("");
	}

	class TwitterGetAccessTokenTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPostExecute(String userName) {

		}

		@Override
		protected String doInBackground(String... params) {

			Twitter twitter = TwitterUtil.getInstance().getTwitter();
			RequestToken requestToken = TwitterUtil.getInstance()
					.getRequestToken();
			try {

				if (!params[0].isEmpty()) {
					try {

						AccessToken accessToken = twitter.getOAuthAccessToken(
								requestToken, params[0]);
						SharedPreferences sharedPreferences = PreferenceManager
								.getDefaultSharedPreferences(getApplicationContext());
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putString(
								ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN,
								accessToken.getToken());
						editor.putString(
								ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET,
								accessToken.getTokenSecret());
						editor.putBoolean(
								ConstantValues.PREFERENCE_TWITTER_IS_LOGGED_IN,
								true);
						editor.commit();
						return twitter.showUser(accessToken.getUserId())
								.getName();
					} catch (TwitterException e) {
						e.printStackTrace();
					}
				} else {
					SharedPreferences sharedPreferences = PreferenceManager
							.getDefaultSharedPreferences(getApplicationContext());
					String accessTokenString = sharedPreferences.getString(
							ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
					String accessTokenSecret = sharedPreferences
							.getString(
									ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET,
									"");
					AccessToken accessToken = new AccessToken(
							accessTokenString, accessTokenSecret);
					try {
						TwitterUtil.getInstance()
								.setTwitterFactory(accessToken);
						return TwitterUtil.getInstance().getTwitter()
								.showUser(accessToken.getUserId()).getName();
					} catch (TwitterException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
			}
			return null;
		}
	}

	private void loginToTwitter() {
		// Check if already logged in

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		if (!sharedPreferences.getBoolean(
				ConstantValues.PREFERENCE_TWITTER_IS_LOGGED_IN, false)) {
			new TwitterAuthenticateTask().execute();
		} else {
			// Toast.makeText(getApplicationContext(), "Already",
			// Toast.LENGTH_LONG).show();
			// Intent intent = new Intent(this, TwitterActivity.class);
			// startActivity(intent);
			// finish();
		}

	}

	class TwitterAuthenticateTask extends
			AsyncTask<String, String, RequestToken> {

		@Override
		protected void onPostExecute(RequestToken requestToken) {
			if (requestToken != null) {
				if (!isUseWebViewForAuthentication) {
					// AlertDialog.Builder alert = new AlertDialog.Builder(
					// Share.this);
					// alert.setTitle("Title here");
					//
					// WebView wv = new WebView(Share.this);
					// wv.loadUrl(requestToken.getAuthenticationURL());
					//
					// wv.setWebViewClient(new WebViewClient() {
					// @Override
					// public boolean shouldOverrideUrlLoading(WebView view,
					// String url) {
					// view.loadUrl(url);
					// view.setFocusable(true);
					// view.requestFocusFromTouch();
					// view.requestFocus(View.FOCUS_DOWN);
					// view.setOnTouchListener(new View.OnTouchListener() {
					// @Override
					// public boolean onTouch(View v, MotionEvent event) {
					// switch (event.getAction()) {
					// case MotionEvent.ACTION_DOWN:
					// case MotionEvent.ACTION_UP:
					// if (!v.hasFocus()) {
					// v.requestFocus();
					// }
					// break;
					// }
					// return false;
					// }
					// });
					// return true;
					// }
					// });
					//
					// alert.setView(wv);
					// alert.setNegativeButton("Close",
					// new DialogInterface.OnClickListener() {
					// @Override
					// public void onClick(DialogInterface dialog,
					// int id) {
					// dialog.dismiss();
					// }
					// });
					// alert.show();

					Dialog dialog = new Dialog(Share.this);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.dialog);

					dialog.setCancelable(true);

					WebView vw = (WebView) dialog.findViewById(R.id.webView1);
					vw.loadUrl(requestToken.getAuthenticationURL());
					vw.setScrollbarFadingEnabled(false);
					vw.setFocusable(true);
					vw.setFocusableInTouchMode(true);
					vw.requestFocus(View.FOCUS_DOWN);
					// vw.setWebViewClient(new WebViewClient() {
					// @Override
					// public boolean shouldOverrideUrlLoading(WebView view,
					// String url) {
					// view.loadUrl(url);
					//
					// view.setFocusable(true);
					//
					// return true;
					// }
					//
					// @Override
					// public void onPageStarted(WebView view, String url,
					// Bitmap favicon) {
					// // TODO Auto-generated method stub
					// super.onPageStarted(view, url, favicon);
					// // if (pbar.getVisibility() == View.GONE) {
					// // pbar.setVisibility(View.VISIBLE);
					// // }
					//
					// }
					//
					// @Override
					// public void onPageFinished(WebView view, String url) {
					// // TODO Auto-generated method stub
					// // if (pbar.getVisibility() == View.VISIBLE) {
					// // pbar.setVisibility(View.GONE);
					// // }
					//
					// super.onPageFinished(view, url);
					// }
					//
					// });

					// vw.getSettings().setJavaScriptEnabled(true);
					dialog.show();

					Editor editor = sharepreferences.edit();

					editor.putString("facebook", fbtext.getText().toString());
					editor.putString("twitter", twtext.getText().toString());
					editor.putBoolean("fb", fb.isChecked());
					editor.putBoolean("tw", tw.isChecked());

					editor.commit();

					// Intent intent = new Intent(Intent.ACTION_VIEW,
					// Uri.parse(requestToken.getAuthenticationURL()));
					// startActivity(intent);
				} else {
					// Intent intent = new Intent(getApplicationContext(),
					// OAuthActivity.class);
					// intent.putExtra(
					// ConstantValues.STRING_EXTRA_AUTHENCATION_URL,
					// requestToken.getAuthenticationURL());
					// startActivity(intent);
					// finish();
					// Toast.makeText(getApplicationContext(), "enter",
					// Toast.LENGTH_LONG).show();
				}
			}
		}

		@Override
		protected RequestToken doInBackground(String... params) {
			return TwitterUtil.getInstance().getRequestToken();
		}
	}

	Request.Callback callback = new Request.Callback() {
		public void onCompleted(Response response) {
			JSONObject graphResponse = response.getGraphObject()
					.getInnerJSONObject();
			String postId = null;
			try {
				postId = graphResponse.getString("id");
			} catch (JSONException e) {
				Log.i("TAG", "JSON error " + e.getMessage());
			}
			FacebookRequestError error = response.getError();
			if (error != null) {
				Toast.makeText(getApplicationContext(),
						error.getErrorMessage(), Toast.LENGTH_SHORT).show();
			} else {

				// Toast.makeText(getApplicationContext(), postId,
				// Toast.LENGTH_LONG).show();
				gotonext();
			}
		}
	};

	private void publishStory() {
		Session session = Session.getActiveSession();
		if (session == null && i == 1) {
			session = Session.openActiveSessionFromCache(Share.this);
			i++;
			publishStory();
		}
		if (session != null && session.isOpened()) {

			// Check for publish permissions
			List<String> permissions = session.getPermissions();
			if (!isSubsetOf(PERMISSIONS, permissions)) {
				pendingPublishReauthorization = true;
				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
						this, PERMISSIONS);
				session.requestNewPublishPermissions(newPermissionsRequest);
				return;
			}
		}

		// Bundle postParams = new Bundle();
		// postParams.putString("message", "TEsttttttttttttttt");
		// postParams.putString("name", "Facebook SDK for Android");
		// // postParams.putString("caption",
		// // "Build great social apps and get more installs.");
		// // postParams
		// // .putString(
		// // "description",
		// //
		// "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
		// // postParams.putString("link",
		// // "https://developers.facebook.com/android");
		// postParams
		// .putString("picture",
		// "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

		// Request.Callback callback = new Request.Callback() {
		// public void onCompleted(Response response) {
		// JSONObject graphResponse = response.getGraphObject()
		// .getInnerJSONObject();
		// String postId = null;
		// try {
		// postId = graphResponse.getString("id");
		// } catch (JSONException e) {
		// Log.i("TAG", "JSON error " + e.getMessage());
		// }
		// FacebookRequestError error = response.getError();
		// if (error != null) {
		// Toast.makeText(getApplicationContext(),
		// error.getErrorMessage(), Toast.LENGTH_SHORT)
		// .show();
		// } else {
		// Toast.makeText(getApplicationContext(), postId,
		// Toast.LENGTH_LONG).show();
		// }
		// }
		// };

		// Request request = new Request(session, "me/feed", postParams,
		// HttpMethod.POST, callback);
		//
		// RequestAsyncTask task = new RequestAsyncTask(request);
		// task.execute();

		else {
			// need for login

			Session.openActiveSession(this, true, new StatusCallback() {

				@SuppressWarnings("deprecation")
				@Override
				public void call(final Session session, SessionState state,
						Exception exception) {
					if (session.isOpened()) {
						// List<String> permissions = session
						// .getPermissions();
						// if (!isSubsetOf(PERMISSIONS,
						// permissions)) {
						// pendingPublishReauthorization = true;
						// Session.NewPermissionsRequest newPermissionsRequest =
						// new Session.NewPermissionsRequest(
						// Share.this, PERMISSIONS);
						// session.requestNewPublishPermissions(newPermissionsRequest);
						// return;
						// }
						Request.executeMeRequestAsync(session,
								new Request.GraphUserCallback() {

									// callback after Graph API response with
									// user object
									@Override
									public void onCompleted(GraphUser user,
											Response response) {
										if (user != null) {
											// Toast.makeText(
											// getApplicationContext(),
											// user.getName(),
											// Toast.LENGTH_LONG).show();
											List<String> permissions = session
													.getPermissions();
											if (!isSubsetOf(PERMISSIONS,
													permissions)) {
												pendingPublishReauthorization = true;
												Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
														Share.this, PERMISSIONS);
												session.requestNewPublishPermissions(newPermissionsRequest);
												return;
											}
										}
									}
								});
					}
				}
			});
		}

	}

	private boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);

	}

	@Override
	public void onBackPressed() {

		Intent next = new Intent(getApplicationContext(), ReportSubmit.class);
		startActivity(next);
		// sometime it remain in stackF
		next.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		finish();
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			// app icon @ action bar clicked; go home
			Intent next = new Intent(getApplicationContext(),
					ReportSubmit.class);
			// sometime it remain in stackF
			next.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(next);
			finish();

			break;
		}
		return true;
	}

	@Override
	protected int getLayoutResourceId() {
		// TODO Auto-generated method stub
		return R.layout.share;
	}

}
