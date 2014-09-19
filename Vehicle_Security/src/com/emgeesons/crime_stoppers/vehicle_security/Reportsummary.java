package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class Reportsummary extends BaseActivity {
	String vehicle_type, make, model, body_type, engine_no, chassis, colour,
			acc, reg, icom, ipno, iexp, insurance_company_number, location,
			selected_date, selected_time, report_type, comments, spic1, spic2,
			state, spic3;
	TextView name, regs, type, date, time, locations, comment, rtype, vstate;
	TextView ttype, tmake, tmodel, body, eng, vin, color, tacc, cname, policy,
			expiry, status, num;
	ImageView vtype;
	RelativeLayout adv, ins;
	String dir;
	downlaod d;
	ImageView pic1, pic2, pic3;
	DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	Button call;
	List<String> mStrings = new ArrayList<String>();
	SharedPreferences sharedpreferences;
	Data info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.reportsummary);
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#FFFFFF'> Report Summary</font>"));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		getSupportActionBar().setIcon(R.drawable.app_icon);
		Intent intent = getIntent();
		String jsonArray = intent.getStringExtra("data");
		d = new downlaod();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);

		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.showImageOnLoading(R.drawable.add_photos_grey)
				.displayer(new RoundedBitmapDisplayer(50)).build();
		call = (Button) findViewById(R.id.call);
		call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent call = new Intent(Intent.ACTION_CALL);
				call.setData(Uri.parse("tel:" + "131444"));
				startActivity(call);
			}
		});
		sharedpreferences = getSharedPreferences(Data.MyPREFERENCES,
				Context.MODE_PRIVATE);
		String vid = sharedpreferences.getString(Data.vid, "notcall");

		try {

			JSONArray array = new JSONArray(jsonArray);
			vehicle_type = array.getJSONObject(0).getString("vehicle_type");
			make = array.getJSONObject(0).getString("make");
			model = array.getJSONObject(0).getString("model");
			body_type = array.getJSONObject(0).getString("body_type");
			engine_no = array.getJSONObject(0).getString("engine_no");
			chassis = array.getJSONObject(0).getString("vin_chassis_no");
			colour = array.getJSONObject(0).getString("colour");
			acc = array.getJSONObject(0).getString(
					"accessories_unique_features");
			reg = array.getJSONObject(0).getString("registration_serial_no");
			icom = array.getJSONObject(0).getString("insurance_company_name");
			ipno = array.getJSONObject(0).getString("insurance_policy_no");
			iexp = array.getJSONObject(0).getString("insurance_expiry_date");
			insurance_company_number = array.getJSONObject(0).getString(
					"insurance_company_number");
			location = array.getJSONObject(0).getString("location");
			selected_date = array.getJSONObject(0).getString("selected_date");
			selected_time = array.getJSONObject(0).getString("selected_time");
			report_type = array.getJSONObject(0).getString("report_type");
			comments = array.getJSONObject(0).getString("comments");
			state = array.getJSONObject(0).getString("state");

			spic1 = array.getJSONObject(0).getString("photo1");
			spic2 = array.getJSONObject(0).getString("photo2");
			spic3 = array.getJSONObject(0).getString("photo3");
			String[] datespilt = selected_time.split("\\:");
			selected_time = datespilt[0] + ":" + datespilt[1];

		} catch (JSONException e) {
			e.printStackTrace();
		}
		pic1 = (ImageView) findViewById(R.id.pic1);
		pic2 = (ImageView) findViewById(R.id.pic2);
		pic3 = (ImageView) findViewById(R.id.pic3);
		name = (TextView) findViewById(R.id.name);
		regs = (TextView) findViewById(R.id.reg);
		rtype = (TextView) findViewById(R.id.rtype);
		date = (TextView) findViewById(R.id.date);
		time = (TextView) findViewById(R.id.time);
		vstate = (TextView) findViewById(R.id.vstate);
		locations = (TextView) findViewById(R.id.location);
		vtype = (ImageView) findViewById(R.id.vtypeimage);
		adv = (RelativeLayout) findViewById(R.id.adv);
		comment = (TextView) findViewById(R.id.comm);
		type = (TextView) findViewById(R.id.vtype);
		tmake = (TextView) findViewById(R.id.vmake);
		tmodel = (TextView) findViewById(R.id.vmodel);
		body = (TextView) findViewById(R.id.vbody);
		eng = (TextView) findViewById(R.id.vengine);
		vin = (TextView) findViewById(R.id.vvin);
		color = (TextView) findViewById(R.id.vcolor);
		tacc = (TextView) findViewById(R.id.vacc);
		cname = (TextView) findViewById(R.id.vname);
		policy = (TextView) findViewById(R.id.vpolicy);
		expiry = (TextView) findViewById(R.id.vexpiry);
		ins = (RelativeLayout) findViewById(R.id.ins);
		num = (TextView) findViewById(R.id.vnum);
		name.setText(make + " " + model);
		if (vehicle_type.equalsIgnoreCase("Bicycle")) {
			regs.setText("Ser No:" + " " + reg);
		} else {
			regs.setText("Reg No:" + " " + reg);
		}

		rtype.setText(report_type);
		date.setText(dateformate(selected_date));
		time.setText(selected_time);
		locations.setText(location);
		comment.setText(comments);
		if (comments.isEmpty() && spic1.isEmpty()) {
			adv.setVisibility(View.GONE);
		}
		if (comments.isEmpty()) {
			comment.setVisibility(View.GONE);
		}

		if (vehicle_type.equalsIgnoreCase("Car")) {
			vtype.setImageResource(R.drawable.ic_car);
		} else if (vehicle_type.equalsIgnoreCase("Bicycle")) {
			vtype.setImageResource(R.drawable.ic_cycle);
		} else if (vehicle_type.equalsIgnoreCase("MotorCycle")) {
			vtype.setImageResource(R.drawable.ic_bike);
		} else {

			vtype.setImageResource(R.drawable.ic_other);

		}

		//
		type.setText(vehicle_type);
		tmake.setText(make);
		tmodel.setText(model);
		body.setText(body_type);
		eng.setText(engine_no);
		vin.setText(chassis);
		color.setText(colour);
		tacc.setText(acc);
		vstate.setText(state);

		ImageView images[] = { pic1, pic2, pic3 };
		String[] imageUrls = { spic1, spic2, spic3 };
		for (int i = 0; i < imageUrls.length; i++) {
			mStrings.add(imageUrls[i]);

		}
		imageLoader.clearDiscCache();
		imageLoader.clearMemoryCache();
		for (int j = 0; j < mStrings.size(); j++) {
			imageLoader.displayImage(mStrings.get(j), images[j], options);
			images[j].setVisibility(View.VISIBLE);
			if (mStrings.get(j).isEmpty() || mStrings.get(j).length() == 8
					|| mStrings.get(j).equalsIgnoreCase("notcall")) {
				images[j].setVisibility(View.GONE);

			}
		}
		onclick();
		if (body_type.isEmpty()) {
			TextView b = (TextView) findViewById(R.id.body);
			b.setVisibility(View.GONE);
			body.setVisibility(View.GONE);

		}
		if (engine_no.isEmpty()) {
			TextView e = (TextView) findViewById(R.id.engine);
			e.setVisibility(View.GONE);
			eng.setVisibility(View.GONE);
		}
		if (acc.isEmpty()) {
			TextView accc = (TextView) findViewById(R.id.accessories);
			accc.setVisibility(View.GONE);
			tacc.setVisibility(View.GONE);
		}
		if (chassis.isEmpty()) {
			TextView v = (TextView) findViewById(R.id.vin);
			v.setVisibility(View.GONE);
			vin.setVisibility(View.GONE);
		}

		if (iexp.contains("0000-00-00") || iexp.isEmpty()) {
			TextView ex = (TextView) findViewById(R.id.expiry);
			ex.setVisibility(View.GONE);
			expiry.setVisibility(View.GONE);
		}
		if (icom.isEmpty()) {
			ins.setVisibility(View.GONE);

		} else {
			ins.setVisibility(View.VISIBLE);
			cname.setText(icom);
			policy.setText(ipno);
			expiry.setText(iexp);
			num.setText(insurance_company_number);
		}
		if (state.isEmpty()) {
			TextView ee = (TextView) findViewById(R.id.state);
			ee.setVisibility(View.GONE);
			vstate.setVisibility(View.GONE);
		}
		Button in = (Button) findViewById(R.id.inscall);
		if (!insurance_company_number.isEmpty()) {
			in.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent call = new Intent(Intent.ACTION_CALL);
					call.setData(Uri.parse("tel:" + insurance_company_number));
					startActivity(call);
				}
			});
		} else {
			in.setVisibility(View.GONE);
		}

	}

	private void onclick() {
		pic1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Reportsummary.this, Fullimage.class);
				intent.putExtra("IMAGES", mStrings.get(0));
				startActivity(intent);
			}
		});
		pic2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Reportsummary.this, Fullimage.class);
				intent.putExtra("IMAGES", mStrings.get(1));
				startActivity(intent);

			}
		});
		pic3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Reportsummary.this, Fullimage.class);
				intent.putExtra("IMAGES", mStrings.get(2));
				startActivity(intent);

			}
		});

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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			super.onBackPressed();
			break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		mStrings.clear();

		super.onBackPressed();
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.reportsummary;

	}
}
