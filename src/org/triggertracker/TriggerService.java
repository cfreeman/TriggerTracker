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
        mDynamicST.addTrack("/trackA.m4a", -27.512920f, 153.034927f);
        mDynamicST.addTrack("/trackB.m4a", -27.511302f, 153.035275f);

		new Thread(new Runnable() {		   

			/**
			 * Helper method for creating GPS triggers.
			 * @param lat The latitude that trips the trigger.
			 * @param lon The longitude that trips the trigger.
			 * @param actionToFire The action to fire when the trigger is tripped.
			 * 
			 * @return The constructed trigger.
			 */
			Trigger createGPSTrigger(float lat, float lon, Action actionToFire) {
				return new GPSTrigger(lm, lat, lon, actionToFire);
			}

			Trigger createTimeTrigger(int minutes, Action actionToFire) {
				return new TimeTrigger(minutes, actionToFire);
			}

			Trigger createDelayTrigger(long seconds, Action actionToFire) {
				return new DelayedTrigger(seconds, actionToFire);
			}

			/**
			 * Helper method for creating video playback actions.
			 *
			 * @param video The name of the video file to playback. 
			 *
			 * @return The constructed action.
			 */
			Action createVideoAction(String video) {
				return new PlayVideoAction(getApplication(), getBaseContext(), video);
			}

			Action createAudioAction(String audio) {
				return new PlayAudioAction(getApplication(), getBaseContext(), audio);
			}

			/**
			 * Helper method for creating call back actions.
			 * 
			 * @param callBack The ID of the callback to trigger.
			 * @param number The phone number to dial.
			 *
			 * @return The constructed action.
			 */
			Action createCallAction(String callBack, String number) {
				return new CallBackAction(callBack, number);
			}

			public void run() {            	
				Looper.prepare();
				ArrayList<Trigger> triggers = new ArrayList<Trigger>();

				// Path 5.
				/*
				ChainTrigger chain = new ChainTrigger(null);
				chain.addTrigger(createGPSTrigger(-27.47166904f, 153.01829696f, createAudioAction("Cafe(ruth).m4a")));
				chain.addTrigger(createDelayTrigger(118, createAudioAction("TimeTriggerCafe.m4a")));
				chain.addTrigger(createGPSTrigger(-27.47081530f, 153.01765859f, createVideoAction("Grassy.m4v")));
				chain.addTrigger(createGPSTrigger(-27.47139597f, 153.01881194f, createVideoAction("Mirror.m4v")));
				chain.addTrigger(createDelayTrigger(590, createAudioAction("Timetriggeroverpass-dropbox(alice).m4a")));
				triggers.add(chain);
				*/

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
