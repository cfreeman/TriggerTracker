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
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.telephony.TelephonyManager;

public class TriggerService extends Service implements LocationListener {
	private static final int POLL_INTERVAL = 1000;
	private boolean isRunning = true;
	private LocationManager lm;
	private TrackerConfiguration config;
	private DynamicSoundTrack mDynamicST;
	
	private String deviceID;
	
	/** Keeps track of all current registered clients. */
    ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    
    /** Holds last value set by a client. */
    int mValue = 0;
    
    /**
     * Command to the service to register a client, receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client where callbacks should be sent.
     */
    static final int MSG_REGISTER_CLIENT = 1;

    /**
     * Command to the service to unregister a client, ot stop receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client as previously given with MSG_REGISTER_CLIENT.
     */
    static final int MSG_UNREGISTER_CLIENT = 2;

    /**
     * Command to service to set a new value.  This can be sent to the
     * service to supply a new value, and will be sent by the service to
     * any registered clients with the new value.
     */
    static final int MSG_SET_VALUE = 3;
    
    /**
     * Command to service to set player locations.
     */
    static final int MSG_SET_PLAYERS = 4;
    
    PlayerLocationManager playerLocMan;
    
    @Override
  	public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
    
    class IncomingHandler extends Handler { // Handler of incoming messages from clients.
        @Override
        public void handleMessage(Message msg) {
        	System.err.println("Message Received to Service");
            switch (msg.what) {
            case MSG_REGISTER_CLIENT:
                mClients.add(msg.replyTo);
                break;
            case MSG_UNREGISTER_CLIENT:
                mClients.remove(msg.replyTo);
                break;
            case MSG_SET_VALUE:
                mValue = msg.arg1;
                for (int i=mClients.size()-1; i>=0; i--) {
                    try {
                        mClients.get(i).send(Message.obtain(null,
                                MSG_SET_VALUE, mValue, 0));
                    } catch (RemoteException e) {
                        // The client is dead.  Remove it from the list;
                        // we are going through the list from back to front
                        // so this is safe to do inside the loop.
                        mClients.remove(i);
                    }
                }
                break;	
            default:
                super.handleMessage(msg);
            }
        }
    }
    
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    private void sendMessageToUI(ArrayList<Player> playerLocations) {
    	//System.err.println("Messaging Code " + mClients.size());
        for (int i=mClients.size()-1; i>=0; i--) {
            try {
                //Send data as a String
                Bundle b = new Bundle();
                b.putParcelableArrayList("org.triggertracker.Player", playerLocations);              
                Message msg = Message.obtain(null, MSG_SET_PLAYERS);
                msg.setData(b);
                mClients.get(i).send(msg);
                System.err.println("Message Sent To Activity");

            } catch (RemoteException e) {
                // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                mClients.remove(i);
                System.err.println("Messaging Error");
            }
        }
    }
    
    private void sendMessageToUI(int closestPlayerProximity) {
        for (int i=mClients.size()-1; i>=0; i--) {
            try {
            	Message msg = Message.obtain(null, TriggerService.MSG_SET_VALUE, closestPlayerProximity, 0);
                mClients.get(i).send(msg);
                System.err.println("Proximity Message Sent To Activity");

            } catch (RemoteException e) {
                // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                mClients.remove(i);
                System.err.println("Messaging Error");
            }
        }
    }
    
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

        mDynamicST = new DynamicSoundTrack(maxVolume);
        // local test.
//        mDynamicST.addTrack("/CreepyKidParkLoop_v01MP3.mp3", -27.511259f, 153.035278f);
//        mDynamicST.addTrack("/DeadTeacherZoneLoop_v01MP3.mp3", -27.512991f, 153.034958f);

        // Urban Village
        mDynamicST.addTrack("/CreepyKidParkLoop_v01MP3.mp3", -27.453684f, 153.012543f);
        mDynamicST.addTrack("/DeadTeacherZoneLoop_v01MP3.mp3", -27.452522f, 153.015427f);
        mDynamicST.addTrack("/HappyParkLoop_v01MP3.mp3", -27.454931f, 153.015015f);
        mDynamicST.addTrack("/HustleBustleCityAtmosLoop_v01MP3.mp3", -27.453665f, 153.014282f);
        mDynamicST.addTrack("/UrbanConstructionLoop_v01MP3.mp3", -27.453779f, 153.015640f);
        mDynamicST.addTrack("/LaBoiteLiftTempLoop_v01MP3.mp3", -27.454552f, 153.013412f);
        
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        
        deviceID = tm.getDeviceId();
        
        playerLocMan = new PlayerLocationManager(deviceID);

		new Thread(new Runnable() {

			public void run() {
				Looper.prepare();
				ArrayList<Trigger> triggers = new ArrayList<Trigger>();
				
				//5 min delay
				triggers.add(new DelayedTrigger(300, new PlayAudioAction("/01 Trigger 1 Audio Test.mp3")));
				
				//45 min delay
				triggers.add(new DelayedTrigger(2700, new PlayAudioAction("/01 Trigger 1 Audio Test.mp3")));

				//1
				triggers.add(new GPSTrigger(lm, -27.45440788489501f, 153.01422536373138f, new PlayAudioAction("/01 Trigger 1 Audio Test.mp3")));
				
				//2a
				triggers.add(new GPSTrigger(lm, -27.454536412013972f, 153.01521241664886f, new PlayAudioAction("/02 Test Trigger 2A - happy park.mp3")));
				
				//2b
				triggers.add(new GPSTrigger(lm, -27.453684322022596f, 153.01414489746094f, new PlayAudioAction("/03 trigger 2b - creepy park.mp3")));
				
				//3a
				triggers.add(new GPSTrigger(lm, -27.4534415465741f, 153.01599025726318f, new PlayAudioAction("/04 Test Trigger 3A construction site.mp3")));
				
				//3b
				triggers.add(new GPSTrigger(lm, -27.45352723208754f, 153.01305055618286f, new PlayAudioAction("/05 Trigger 3b - creepy kids park.mp3")));
				
				//4a
				triggers.add(new GPSTrigger(lm, -27.452746539394088f, 153.0151104927063f, new PlayAudioAction("/06 Test Trigger 4b - dead zone.mp3")));

				//5
				triggers.add(new GPSTrigger(lm, -27.453793808814694f, 153.01508098840714f, new PlayAudioAction("/07Test Final Trigger Evacuate .mp3")));
				
				//ChainTrigger chain = new ChainTrigger(null);
				//chain.addTrigger(new DelayedTrigger(10, new PlayAudioAction("/trackA.m4a")));
				//triggers.add(chain);

				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            	PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "TeethTracker");

				while (isRunning) {
					// Prevent the device from going to sleep.
	                wl.acquire();
	                
	                for(int i = 0; i < triggers.size(); i++){
	                	triggers.get(i).testFire();
	                }
	                
	                Location currentLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	                
	                //Update current location to server
	                playerLocMan.sendPlayerLocationData(currentLocation);
	                
	                //Get list of other player locations
	                ArrayList<Player> results = playerLocMan.retrievePlayerLocationData();
	                
	                if(currentLocation != null){
	                
		                float closestPlayer = 255;
		                
		                float[] distance = new float[1];
		                
		                distance[0] = 255;
		                
		                for(int i = 0; i < results.size(); i++){
		                	if(!(results.get(i).getDeviceID().equals(deviceID))){
			        				Location.distanceBetween(currentLocation.getLatitude(),
			        										currentLocation.getLongitude(),
			        										 results.get(i).getLat(),
			        										 results.get(i).getLon(),
			        										 distance);
			                	
			                	System.err.println("Distance between (" 
			                	+ currentLocation.getLatitude() + "," + currentLocation.getLongitude() 
			                			+ ") and (" 
			                	+ results.get(i).getLat()+ "," +  results.get(i).getLon() + ") is: " 
			                			+ (int)distance[0]);
		                	
		                		if(distance[0] < closestPlayer){
		                			closestPlayer = distance[0];
		                		}
		                	}
		                }
		                
		                if(closestPlayer > 255){
		                	closestPlayer = 255;
		                }
		                
		                if(closestPlayer < 0){
		                	closestPlayer = 0;
		                }
		                
		                sendMessageToUI((int)closestPlayer);
	                }
	                
	                //Send location list back to UI process
	                sendMessageToUI(results);

	                // Update the sound levels in the dynamic sound track.
	                mDynamicST.updateLevels(currentLocation);

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
