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

import org.triggertracker.locationservices.EstimoteLocation;
import org.triggertracker.locationservices.EstimoteManager;
import org.triggertracker.locationservices.TriggerLocation;

public class TriggerService extends Service implements LocationListener {
	private static final String TAG = TriggerService.class.getSimpleName();
	private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
	private static final int POLL_INTERVAL = 500;

	private LocationManager mLocationManager;
	private BeaconManager mBeaconManager;
	private EstimoteManager mEstimoteManager;

	private boolean mIsRunning = true;
	private DynamicSoundTrack mDynamicST;

	//private TrackerConfiguration config;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		final Context text = this;

		// Enable the low power bluetooth locatiion service.
		mBeaconManager = new BeaconManager(this);
		mBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {
			@Override
			public void onServiceReady() {
				try {
					mBeaconManager.setForegroundScanPeriod(POLL_INTERVAL, POLL_INTERVAL * 5);
					mBeaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
				} catch (RemoteException e) {
					Log.e(TAG, "Cannot start ranging", e);
				}
			}
		});

		// Enable the GPS - updating every second or if we move more than 1 meter.
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, POLL_INTERVAL, 1.0f, this);

		mEstimote	Manager = new EstimoteManager(mBeaconManager);

		//config = new TrackerConfiguration();
		//config.loadFromYaml("/trackerConfig.yml");

		// static looping background sound track.
		try {
			MediaPlayer mPlayer = new MediaPlayer();
			mPlayer.setDataSource(Environment.getExternalStorageDirectory() + "/soundTrack.mp3");
			mPlayer.prepare();
			mPlayer.setVolume(0.25f, 0.25f);
			mPlayer.setLooping(true);
			mPlayer.start();
		} catch (Exception e) {
			Log.e(TAG, "Unable to set background sound track", e);
		}

        // Scale volume percent by max volume.
		// AudioManager aManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		// int maxVolume = aManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);

//		System.err.println("****maxVolume:" + maxVolume);
//		mDynamicST = new DynamicSoundTrack(1.0f);

//		final TriggerLocation loc = new EstimoteLocation(mEstimoteManager, "CC:4A:11:09:A2:C3");
//		mDynamicST.addTrack("/QPAC_LaMer_Test.mp3", loc);

//        mDynamicST.addTrack("/CreepyKidParkLoop_v01MP3.mp3", -27.511259f, 153.035278f);
//        mDynamicST.addTrack("/DeadTeacherZoneLoop_v01MP3.mp3", -27.512991f, 153.034958f);

        // Urban Village
//        mDynamicST.addTrack("/CreepyKidParkLoop_v01MP3.mp3", -27.453684f, 153.012543f);
//        mDynamicST.addTrack("/DeadTeacherZoneLoop_v01MP3.mp3", -27.452522f, 153.015427f);
//        mDynamicST.addTrack("/HappyParkLoop_v01MP3.mp3", -27.454931f, 153.015015f);
//        mDynamicST.addTrack("/HustleBustleCityAtmosLoop_v01MP3.mp3", -27.453665f, 153.014282f);
//        mDynamicST.addTrack("/UrbanConstructionLoop_v01MP3.mp3", -27.453779f, 153.015640f);
//        mDynamicST.addTrack("/LaBoiteLiftTempLoop_v01MP3.mp3", -27.454552f, 153.013412f);

		new Thread(new Runnable() {
			public void run() {
				Looper.prepare();
				ArrayList<Trigger> triggers = new ArrayList<Trigger>();
				ChainTrigger chain = new ChainTrigger(null);

				chain.addTrigger(new DelayedTrigger(30, new PlayAudioAction("/timeTrigger.mp3")));

				TriggerLocation loc = new EstimoteLocation(mEstimoteManager, "CC:4A:11:09:A2:C3");
				chain.addTrigger(new LocationTrigger(loc, new PlayAudioAction("/station1.mp3")));

				loc = new EstimoteLocation(mEstimoteManager, "F3:13:F9:66:8B:95");
				chain.addTrigger(new LocationTrigger(loc, new PlayAudioAction("/station2.mp3")));

				loc = new EstimoteLocation(mEstimoteManager, "F5:01:C3:01:18:3E");
				chain.addTrigger(new LocationTrigger(loc, new PlayAudioAction("/station3.mp3")));

				loc = new EstimoteLocation(mEstimoteManager, "D7:CF:78:0F:4B:E2");
				chain.addTrigger(new LocationTrigger(loc, new PlayAudioAction("/station4.mp3")));

				ChainTrigger station5 = new ChainTrigger(null);
				loc = new EstimoteLocation(mEstimoteManager, "EA:83:5B:66:2C:B2");
				station5.addTrigger(new LocationTrigger(loc, new PlayAudioAction("/station5.mp3")));
				station5.addTrigger(new DelayedTrigger(135, new Action() {
					@Override
					public void trigger() {
						try {
							Intent b = new Intent(Intent.ACTION_VIEW, Uri.parse("http://basichuman.com.au"));
							b.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(b);
						} catch (Exception e) {
							Log.e(TAG, "Unable to pop browser: ", e);
						}
					}
				}));
				chain.addTrigger(station5);

				triggers.add(chain);

				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "TriggerTracker");

				while (mIsRunning) {
					// Prevent the device from going to sleep.
	                wl.acquire();

	                // Update the sound levels in the dynamic sound track.
	                //mDynamicST.updateLevels();
	                // Toast.makeText(text, Float.toString(loc.distance()), Toast.LENGTH_SHORT).show();

					// For each registered trigger - see if it fires.
					for (Trigger t : triggers) {
						t.testFire();
					}

					try {
						//Pause so that we have the opportunity to cancel the bonding request.
						Thread.sleep(POLL_INTERVAL);
					} catch (InterruptedException e) {
						System.err.println("Running thread interupted - " + e);
					}
				}

				// All done, the device can now go to sleep.
				wl.release();

				// All done, turn the sound track off.
				//mDynamicST.shutdown();
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
