package org.triggertracker;

import java.text.DecimalFormat;

import android.os.Parcel;
import android.os.Parcelable;

public class Player implements Parcelable {
    private int id;
	private float lat, lon;
	private String deviceID;
	
	//Decimal format for making location data more readable
	private DecimalFormat df = new DecimalFormat("####.####");

	/**
	 * Constructor.
	 *
	 * @param newID The ID of the player
	 * @param newLat The current Latitude of the player
	 * @param newLon The current Longitude of the player
	 * @param newLon The device ID of the device the player is using
	 */
    Player(int newID, float newLat, float newLon, String deviceID) {
    	id = newID;
    	lat = newLat;
        lon = newLon;
        this.deviceID = deviceID;
    }
    
    public String toString() {
    	return id + ", " + " ( " + df.format(lat) + ", " + df.format(lon) + " ), " + deviceID;
    }
    
    public String getDeviceID() {
		return this.deviceID;
	}

	public void setDeviceID(String id) {
		this.deviceID = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}

	public float getLon() {
		return lon;
	}

	public void setLon(float lon) {
		this.lon = lon;
	}
	
	@Override
    public int describeContents() {
        return 0;
    }
 
    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	dest.writeInt(this.id);
        dest.writeFloat(this.lat);
        dest.writeFloat(this.lon);
    }
    
    public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        public Player[] newArray(int size) {
            return new Player[size];
        }
    };
 
    private Player(Parcel in) {
    	this.id = in.readInt();
        this.lat = in.readFloat(); 
        this.lon = in.readFloat();
    }
}
