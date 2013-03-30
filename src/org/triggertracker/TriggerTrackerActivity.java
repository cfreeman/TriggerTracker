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

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class TriggerTrackerActivity extends Activity {
	/**
	 * @return True if the trigger service is already running, false otherwise. 
	 */
	private boolean isTrackingServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("org.triggertracker.TriggerService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	/** Messenger for communicating with service. */
	Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
	boolean mIsBound;
	/** Some text view we are using to show state information. */
	String mCallbackText;
	
	private PlayerRadarView radar;
	private ProximitySurfaceView canvas;

	/**
	 * Handler of incoming messages from service.
	 */
	class IncomingHandler extends Handler {
	    @Override
	    public void handleMessage(Message msg) {
	    	System.err.println("Message Received To Activity");
	        switch (msg.what) {
	            case TriggerService.MSG_SET_VALUE:
	            	System.err.println("Value Message Received To Activity");
	            	if(canvas != null){
            			canvas.setProximityValue(msg.arg1);
            			System.err.println("Proximity updated... " + msg.arg1);
            		}
	                break;
	            case TriggerService.MSG_SET_PLAYERS:
	            	System.err.println("Player Locations Message Received To Activity");
	            	
	            	/*if(radar != null){
	            		radar.invalidate();
	            		final Bundle bundle = msg.getData();
	            		bundle.setClassLoader(getClassLoader());
	            		ArrayList<Player> playerList = new ArrayList<Player>();
	            		try{
	            		playerList = bundle.getParcelableArrayList("org.triggertracker.Player");
	            		}catch(BadParcelableException e){
	            			System.err.println(e.getMessage());
	            		}
	            		if(playerList != null){
		            		System.err.println("Player Locations: " + playerList.toString());
		            		radar.setPlayerLocations(playerList);
	            		}
	            	}*/
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
	
	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className,
	            IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  We are communicating with our
	        // service through an IDL interface, so get a client-side
	        // representation of that from the raw service object.
	        mService = new Messenger(service);
	        System.err.println("Attaching Service");

	        // We want to monitor the service for as long as we are
	        // connected to it.
	        try {
	            Message msg = Message.obtain(null,
	            		TriggerService.MSG_REGISTER_CLIENT);
	            msg.replyTo = mMessenger;
	            mService.send(msg);

	            // Give it some value as an example.
	            msg = Message.obtain(null,
	            		TriggerService.MSG_SET_VALUE, this.hashCode(), 0);
	            mService.send(msg);
	            
	            System.err.println("Service Test Sent");
	        } catch (RemoteException e) {
	            // In this case the service has crashed before we could even
	            // do anything with it; we can count on soon being
	            // disconnected (and then reconnected if it can be restarted)
	            // so there is no need to do anything here.
	        	System.err.println("Service Connection Failed");
	        }

	        // As part of the sample, tell the user what happened.
	        //Toast.makeText(Binding.this, R.string.remote_service_connected,
	          //      Toast.LENGTH_SHORT).show();
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        mService = null;
	        System.err.println("Service Disconnected");

	        // As part of the sample, tell the user what happened.
	        //Toast.makeText(Binding.this, R.string.remote_service_disconnected,
	                //Toast.LENGTH_SHORT).show();
	    }
	};
	
	void doBindService(Intent i) {
	    // Establish a connection with the service.  We use an explicit
	    // class name because there is no reason to be able to let other
	    // applications replace our component.
	    bindService(i, mConnection, Context.BIND_AUTO_CREATE);
	    mIsBound = true;
	    System.err.println("Binding Service.");
	}
	
	void doUnbindService() {
	    if (mIsBound) {
	        // If we have received the service, and hence registered with
	        // it, then now is the time to unregister.
	        if (mService != null) {
	            try {
	                Message msg = Message.obtain(null,
	                		TriggerService.MSG_UNREGISTER_CLIENT);
	                msg.replyTo = mMessenger;
	                mService.send(msg);
	            } catch (RemoteException e) {
	                // There is nothing special we need to do if the service
	                // has crashed.
	            }
	        }

	        // Detach our existing connection.
	        unbindService(mConnection);
	        mIsBound = false;
	        System.err.println("Unbinding Service.");
	    }
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
    	//radar = (PlayerRadarView) this.findViewById(R.id.Radar);
    	
    	canvas = new ProximitySurfaceView(this);
    	
    	setContentView(canvas);
        
        // Spawn the trigger service to run in the background, but only if it is not running.        
        if (!isTrackingServiceRunning()) {
        	System.err.println("Spining up service");
        	Intent i= new Intent(this, TriggerService.class);
        	startService(i);
        	doBindService(i);
        }   
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	try {
            doUnbindService();
        } catch (Throwable t) {
            //Log.e("MainActivity", "Failed to unbind from the service", t);
        }
    }
    
}