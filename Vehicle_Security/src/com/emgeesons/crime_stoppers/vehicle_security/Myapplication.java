package com.emgeesons.crime_stoppers.vehicle_security;

import java.util.HashSet;

import android.app.Application;

import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.Logger;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

public class Myapplication extends Application {

	@Override
	public void onCreate() {

		AirshipConfigOptions options = AirshipConfigOptions
				.loadDefaultOptions(Myapplication.this);
		UAirship.takeOff(this, options);
		PushManager.shared().setAlias("test");

		// Tags
		HashSet<String> tags = new HashSet<String>();
		tags.add("tag1");
		tags.add("tag2");
		PushManager.shared().setTags(tags);
		PushManager.enablePush();
		PushManager.shared().setIntentReceiver(IntentReceiver.class);
		String apid = PushManager.shared().getAPID();
		Logger.info("My Application onCreate - App APID: " + apid);

	}
}