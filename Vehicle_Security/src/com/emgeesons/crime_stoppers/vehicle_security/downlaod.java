package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.os.Environment;
import android.util.Log;

public class downlaod {

	String dir;
	File sdRoot;

	public void DownloadFromUrl(String DownloadUrl, String fileName,
			String Folder, String dirs) {
		sdRoot = Environment.getExternalStorageDirectory();
		// cr8 folder
		File folder = new File(sdRoot + Folder);
		boolean success = true;

		if (!folder.exists()) {
			success = folder.mkdir();
		}
		if (success) {
			// Do something on success
		} else {

		}
		dir = dirs;
		try {
			// File root = android.os.Environment.getExternalStorageDirectory();

			File f = new File(sdRoot, dir);
			if (f.exists() == false) {
				f.mkdirs();
			}

			URL url = new URL(DownloadUrl); // you can write here any link
			File file = new File(f, fileName);

			long startTime = System.currentTimeMillis();
			Log.d("DownloadManager", "download begining");
			// Log.d("DownloadManager", "download url:" + url);

			/* Open a connection to that URL. */
			URLConnection ucon = url.openConnection();

			/*
			 * Define InputStreams to read from the URLConnection.
			 */
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			/*
			 * Read bytes to the Buffer until there is nothing more to read(-1).
			 */
			ByteArrayBuffer baf = new ByteArrayBuffer(5000);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			/* Convert the Bytes read to a String. */
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.flush();
			fos.close();
			Log.d("DownloadManager", "downloaded file name:" + fileName);
			Log.d("DownloadManager",
					"download ready in"
							+ ((System.currentTimeMillis() - startTime) / 1000)
							+ " sec");

		} catch (IOException e) {
//			Log.d("DownloadManager", "Error: " + e);
		}

	}
}
