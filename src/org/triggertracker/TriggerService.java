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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;

public class TriggerService extends Service implements LocationListener {
	private static final int POLL_INTERVAL = 1000;
	private boolean isRunning = true;
	private LocationManager lm;
	private TrackerConfiguration config;
		
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.err.println("Starting Service");		

		//config = new TrackerConfiguration();
		//config.loadFromYaml("/trackerConfig.yml");

		//Enable the GPS - updating every second or if we move more than 1 meter.
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, POLL_INTERVAL, 1.0f, this);		

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

				// Path 1.
				triggers.add(createDelayTrigger(30, createAudioAction("juliemsg.m4a")));
				ChainTrigger chain = new ChainTrigger(null);
				chain.addTrigger(createGPSTrigger(-27.47157563f, 153.018449843f, createVideoAction("lamp.m4v")));
				chain.addTrigger(createDelayTrigger(151, createAudioAction("Billymsg.m4a")));
				triggers.add(chain);

				ChainTrigger chain2 = new ChainTrigger(null);
				chain2.addTrigger(createGPSTrigger(-27.47181718f, 153.01791608f, createAudioAction("stairs.m4a")));
				chain2.addTrigger(createDelayTrigger(180, createAudioAction("msglillyfromstairs.m4a")));
				triggers.add(chain2);

				ChainTrigger chain3 = new ChainTrigger(null);
				chain3.addTrigger(createGPSTrigger(-27.470827201f, 153.017663956f, createVideoAction("grassy.m4v")));
				chain3.addTrigger(createDelayTrigger(300, createAudioAction("alicefromgrassy.m4a")));
				triggers.add(chain3);

				// Path 2.
				/*
				ChainTrigger chain = new ChainTrigger(null);
				chain.addTrigger(createGPSTrigger(-27.47184752f, 153.01894940f, createVideoAction("sculpture.m4v")));
				chain.addTrigger(createDelayTrigger(300, createAudioAction("juliemsg.m4a")));
				triggers.add(chain);

				ChainTrigger chain2 = new ChainTrigger(null);
				chain2.addTrigger(createGPSTrigger(-27.47157563f, 153.018449843f, createVideoAction("lamp.m4v")));
				chain2.addTrigger(createDelayTrigger(180, createAudioAction("Billymsg.m4a")));
				triggers.add(chain2);

				triggers.add(createGPSTrigger(-27.47181718f, 153.01791608f, createAudioAction("stairs.m4a")));

				ChainTrigger chain3 = new ChainTrigger(null);
				chain3.addTrigger(createGPSTrigger(-27.470827201f, 153.017663956f, createVideoAction("grassy.m4v")));
				chain3.addTrigger(createDelayTrigger(300, createAudioAction("alicefromgrassy.m4a")));
				triggers.add(chain3);
				*/

				// Path 3.
				/*
				ChainTrigger chain = new ChainTrigger(null);
				chain.addTrigger(createGPSTrigger(-27.47200876f, 153.01884547f, createVideoAction("dropbox.m4v")));
				chain.addTrigger(createDelayTrigger(300, createAudioAction("juliemsg.m4a")));
				triggers.add(chain);

				ChainTrigger chain2 = new ChainTrigger(null);
				chain2.addTrigger(createGPSTrigger(-27.47157563f, 153.018449843f, createVideoAction("lamp.m4v")));
				chain2.addTrigger(createDelayTrigger(180, createAudioAction("Billymsg.m4a")));
				triggers.add(chain2);

				triggers.add(createGPSTrigger(-27.47181718f, 153.01791608f, createAudioAction("stairs.m4a")));

				ChainTrigger chain3 = new ChainTrigger(null);
				chain3.addTrigger(createGPSTrigger(-27.470827201f, 153.017663956f, createVideoAction("grassy.m4v")));
				chain3.addTrigger(createDelayTrigger(300, createAudioAction("alicefromgrassy.m4a")));
				triggers.add(chain3);
				*/

				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            	PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "TeethTracker");

				while (isRunning) {
					// Prevent the device from going to sleep.
	                wl.acquire();

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
