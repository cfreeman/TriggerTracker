package org.triggertracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.R.string;
import android.location.Location;
import android.telephony.TelephonyManager;

public class PlayerLocationManager {
	
	/**
     * Constructor.
     *
     * @param loc the current location of the player.
     */
	public PlayerLocationManager(String deviceID){
		this.mDeviceID = deviceID;
	}
		
	public void sendPlayerLocationData(Location loc){
		
		if(loc != null){
			try {
			    URL playerTracker = new URL("http://thisiscapitalcity.heroku.com/players/new?device_id="
			    						     + this.mDeviceID + "&lat=" + loc.getLatitude() + "&lon=" + loc.getLongitude());
			    URLConnection trackerConnection = playerTracker.openConnection();
			    trackerConnection.getContentLength();
	
			} catch (MalformedURLException e) {
				System.err.println("Malformed URL Exception: " + e.toString() + "\n");
			} catch (IOException e) {
				System.err.println("IOException: " + e.toString() + "\n");
			}
		}
		
	}

	public ArrayList<Player> retrievePlayerLocationData() {
		ArrayList<Player> results = new ArrayList<Player>();
		System.err.println("getPlayerList\n");

		try {
			URL playerTracker = new URL("http://thisiscapitalcity.heroku.com/players/list.json");

			BufferedReader in = new BufferedReader(new InputStreamReader(playerTracker.openStream()));
			String JSONBlob = "";
			String inputLine;

			// Read the JSON from the server.
			while ((inputLine = in.readLine()) != null) {
				JSONBlob = JSONBlob + inputLine;
			}
			in.close();

			// Parse the JSON from the server - pulling out the player ID's.
			JSONArray list = (JSONArray) new JSONTokener(JSONBlob).nextValue();
			for (int i = 0; i < list.length(); i++) {
				JSONObject playerData = list.getJSONObject(i);
				
				System.err.println("Player from JSON:");
				
				int playerID = playerData.getInt("id");
				
				String deviceID = playerData.getString("device_id");
				
				float playerLat = (float)playerData.getDouble("lat");
				
				float playerLon = (float)playerData.getDouble("lon");
				
				results.add(new Player(playerID, playerLat, playerLon, deviceID));
			}

		} catch (MalformedURLException e) {			
			System.err.println("Unable to create URL: " + e.toString() + "\n");
		} catch (IOException e) {
			System.err.println("Unable to open connection: " + e.toString() + "\n");
		} catch (JSONException e) {
			System.err.println("Unable to parse JSON: " + e.toString() + "\n");
		}
		
		//Replace the local player list with the new list from the server.
		mPlayers = results;
		return mPlayers;
	}

	private ArrayList<Player> mPlayers;
	final String mDeviceID; 

}
