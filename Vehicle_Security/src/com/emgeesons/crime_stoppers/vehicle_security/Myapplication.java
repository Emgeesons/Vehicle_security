package com.emgeesons.crime_stoppers.vehicle_security;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.Logger;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

//@ReportsCrashes(
//        formKey = "",
//        formUri = "https://test8.cloudant.com/newdatabase/_design/acra-storage/_update/report",
//        reportType = org.acra.sender.HttpSender.Type.JSON,
//        httpMethod = org.acra.sender.HttpSender.Method.PUT,
//        formUriBasicAuthLogin="ongliessuldessupoiluchey",
//        formUriBasicAuthPassword="uWOtNx0NKxkNq3SRwEpEVkjV",
//        mode = ReportingInteractionMode.SILENT
//        )
//@ReportsCrashes(formKey = "", // will not be used
//mailTo = "gaurav@emgeesons.com,pavan@emgeesons.com",
//// my email here
//mode = ReportingInteractionMode.TOAST, resToastText = 1)
public class Myapplication extends Application {

	@Override
	public void onCreate() {
		ACRA.init(this);
		AirshipConfigOptions options = AirshipConfigOptions
				.loadDefaultOptions(Myapplication.this);
		UAirship.takeOff(this, options);
		// PushManager.shared().setAlias("testing");
		//
		// // Tags
		// HashSet<String> tags = new HashSet<String>();
		// tags.add("tag1ss");
		// tags.add("tag2sss");
		// PushManager.shared().setTags(tags);
		// PushManager.enablePush();
		PushManager.shared().setIntentReceiver(IntentReceiver.class);
		String apid = PushManager.shared().getAPID();
		Logger.info("My Application onCreate - App APID: " + apid);

	}
}