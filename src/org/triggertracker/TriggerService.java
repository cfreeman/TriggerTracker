/*
 * Copyright (c) Clinton Freeman 2012
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.triggertracker;

import java.util.ArrayList;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.estimote.sdk.Region;
import com.estimote.sdk.BeaconManager;

import org.json.JSONObject;

import org.triggertracker.locationservices.EstimoteLocation;
import org.triggertracker.locationservices.EstimoteManager;
import org.triggertracker.locationservices.GPSManager;
import org.triggertracker.locationservices.TriggerLocation;

public class TriggerService extends Service implements LocationListener {
	private static final String TAG = TriggerService.class.getSimpleName();
	private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
	private static final int POLL_INTERVAL = 600;

	private LocationManager mLocationManager;
	private GPSManager mGPSManager;

	private BeaconManager mBeaconManager;
	private EstimoteManager mEstimoteManager;

	private boolean mIsRunning = true;

	public GPSManager getGPSManager() {
		return mGPSManager;
	}

	public EstimoteManager getEstimoteManager() {
		return mEstimoteManager;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		final Context text = this;

		// Enable the low power bluetooth locatiion service.
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.enable();
		}

		mBeaconManager = new BeaconManager(this);
		mBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {
			@Override
			public void onServiceReady() {
				try {
					mBeaconManager.setForegroundScanPeriod(POLL_INTERVAL, POLL_INTERVAL);
					mBeaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
				} catch (RemoteException e) {
					Log.e(TAG, "Cannot start ranging", e);
				}
			}
		});
		mEstimoteManager = new EstimoteManager(mBeaconManager);

		// Enable the GPS - updating every second or if we move more than 1 meter.
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, POLL_INTERVAL, 1.0f, this);

		mGPSManager = new GPSManager(mLocationManager);

		final TrackerConfiguration config = new TrackerConfiguration(mEstimoteManager, mGPSManager, getApplication(), getBaseContext());
		final String configFile = Environment.getExternalStorageDirectory() + "/config.json";

		try {
			// If supplied, start static looping background soundtrack.
			JSONObject soundtrack = config.loadSoundtrackFromJSON(configFile);
			if (soundtrack != null) {
				MediaPlayer mPlayer = new MediaPlayer();
				mPlayer.setDataSource(Environment.getExternalStorageDirectory() + soundtrack.getString("audioFile"));
				mPlayer.prepare();
				mPlayer.setVolume((float) soundtrack.getDouble("volume"), (float) soundtrack.getDouble("volume"));
				mPlayer.setLooping(true);
				mPlayer.start();
			}
		} catch (Exception e) {
			CharSequence txt = "Unable to load background soundtrack from config file";
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(getApplicationContext(), txt, duration);
			toast.show();
		}

		new Thread(new Runnable() {
			public void run() {
				Looper.prepare();
				ArrayList<Trigger> triggers = new ArrayList<Trigger>();
				try {
					triggers = config.loadTriggersFromJSON(configFile);
				} catch (Exception e) {
					CharSequence txt = "Bad configuration file. Unable to open it";
					int duration = Toast.LENGTH_LONG;

					Toast toast = Toast.makeText(getApplicationContext(), txt, duration);
					toast.show();
				}

				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "TriggerTracker");

				while (mIsRunning) {
	                wl.acquire();  // Prevent the device from going to sleep.

					// For each registered trigger - see if it fires.
					for (Trigger t : triggers) {
						t.testFire();
					}

					for (Trigger t : triggers) {
						t.update();
					}

					try {
						// Pause so that other things have the chance to do things.
						Thread.sleep(POLL_INTERVAL);
					} catch (InterruptedException e) {
						System.err.println("Running thread interupted - " + e);
					}
				}

				// All done, the device can now go to sleep.
				wl.release();
		  }
		}).start();

		return(START_NOT_STICKY);
	}

	@Override
  	public void onDestroy() {
		try {
			mBeaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
		} catch (RemoteException e) {
			Log.d(TAG, "Error while stopping ranging", e);
		}

		mIsRunning = false;
		super.onDestroy();
  	}

  	@Override
  	public IBinder onBind(Intent intent) {
  		return(null);
  	}

	@Override
	public void onLocationChanged(Location arg0) {
	}

	@Override
	public void onProviderDisabled(String arg0) {
	}

	@Override
	public void onProviderEnabled(String arg0) {
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}
}
