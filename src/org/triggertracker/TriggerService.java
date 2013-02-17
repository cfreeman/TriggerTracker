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
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;

public class TriggerService extends Service implements LocationListener {
	private static final int POLL_INTERVAL = 1000;
	private boolean isRunning = true;
	private LocationManager lm;
	private TrackerConfiguration config;
	private DynamicSoundTrack mDynamicST;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.err.println("Starting Service");		

		//config = new TrackerConfiguration();
		//config.loadFromYaml("/trackerConfig.yml");

		//Enable the GPS - updating every second or if we move more than 1 meter.
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, POLL_INTERVAL, 1.0f, this);

        // Scale volume percent by max volume.
        AudioManager amanager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = amanager.getStreamMaxVolume(AudioManager.STREAM_ALARM);

        mDynamicST = new DynamicSoundTrack(lm, maxVolume);
        //mDynamicST.addTrack("/trackA.m4a", -27.514204f, 153.034658f);
        mDynamicST.addTrack("/trackB.m4a", -27.511840f, 153.035415f);

		new Thread(new Runnable() {

			public void run() {
				Looper.prepare();
				ArrayList<Trigger> triggers = new ArrayList<Trigger>();

				ChainTrigger chain = new ChainTrigger(null);
				chain.addTrigger(new DelayedTrigger(10, new PlayAudioAction("/trackA.m4a")));
				triggers.add(chain);

				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            	PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "TeethTracker");

				while (isRunning) {
					// Prevent the device from going to sleep.
	                wl.acquire();

	                // Update the sound levels in the dynamic sound track.
	                mDynamicST.updateLevels();

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
				mDynamicST.shutdown();
		  }
		}).start();

		return(START_NOT_STICKY);
	}

	@Override
  	public void onDestroy() {
	  isRunning = false;	  
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
