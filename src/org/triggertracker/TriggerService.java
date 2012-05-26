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
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;

public class TriggerService extends Service implements LocationListener {
	private static final int POLL_INTERVAL = 5000;
	private boolean isRunning = true;
	private LocationManager lm;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.err.println("Starting Service");		

		//Enable the GPS - updating every second or if we move more than 5 meters.
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
			
			// Phone-1 450722920
			// Phone-2 499574637
			// Phone-3 499322451
			// Phone-4 499528247
			// Phone-5 499570760
			// Phone-6 499575393
			// Phone-7 410121107
			// Phone-8 410126112
			// Phone-9 410929556
			// Phone-10 410929481
			// Phone-11 410929219
			// Phone-12 410929816
			// Phone-13 410929401

			static final String NUMBER = "410929816";

			public void run() {            	
				Looper.prepare();
				
				ArrayList<Trigger> triggers = new ArrayList<Trigger>();

				triggers.add(createGPSTrigger(-27.472237f, 153.019089f,
											   createCallAction("station1", NUMBER)));
				
				triggers.add(createGPSTrigger(-27.472868f, 153.019455f,
						   					   createCallAction("station2", NUMBER)));
				
				triggers.add(createGPSTrigger(-27.473827f,153.020447f,
	   					   					   createCallAction("station3", NUMBER)));
				
				triggers.add(createGPSTrigger(-27.471861f,153.019196f,
	   					   					   createCallAction("station4", NUMBER)));

				triggers.add(createGPSTrigger(-27.470699f,153.017807f,
	   					   					  createCallAction("station5", NUMBER)));				

				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            	PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "TeethTracker");
				
				while (isRunning) {
					// Prevent the device from going to sleep.
	                wl.acquire();
					
					// For each registered trigger - see if it fires.
	                int trig = 0;
					for (Trigger t : triggers) {
						System.err.println("T" + trig);
						t.testFire();
						trig++;
					}
					
					System.err.println("****");

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