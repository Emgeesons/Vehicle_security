package com.emgeesons.crime_stoppers.vehicle_security;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.emgeesons.crime_stoppers.vehicle_security.TouchImageView.OnTouchImageViewListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class Fullimage extends Activity {
	DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	// for zoomin/out
	TouchImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fullimage);
		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
		String imageUrls = bundle.getString("IMAGES");
		imageView = (TouchImageView) findViewById(R.id.image);

		final ProgressBar spinner = (ProgressBar) findViewById(R.id.loading);
		options = new DisplayImageOptions.Builder()

		.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		imageLoader.displayImage(imageUrls, imageView, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						spinner.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						String message = null;
						switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
						}
						Toast.makeText(Fullimage.this, message,
								Toast.LENGTH_SHORT).show();

						spinner.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						spinner.setVisibility(View.GONE);
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.fullimage, menu);
		return true;
	}

}
