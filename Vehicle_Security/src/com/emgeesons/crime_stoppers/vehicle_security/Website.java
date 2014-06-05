package com.emgeesons.crime_stoppers.vehicle_security;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Website extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_website);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'>About Us</font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.ic_app);
		WebView wv = (WebView) findViewById(R.id.webView1);
		wv.setWebViewClient(new MyWebViewClient());
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setBuiltInZoomControls(true);
		wv.getSettings().setLoadWithOverviewMode(true);
		wv.getSettings().setBuiltInZoomControls(false);
		wv.getSettings().setUseWideViewPort(true);
		wv.loadUrl("https://sa.crimestoppers.com.au");

	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			super.onBackPressed();
			// app icon @ action bar clicked; go home

			break;
		}
		return true;
	}

}
