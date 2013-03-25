/*
 * Copyright (c) Clinton Freeman 2013
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Environment;

public class DynamicSoundTrack {

    /**
     * Constructor.
     *
     * @param locMan The LocationManager that will provide position information for updating
     * the levels of the dynamic sound track. When people are close to the sources of tracks
     * the sound levels will be louder, and when people are further away from the sources of 
     * tracks, the sound levels will be lower.
     * @param maxVolume The maximum volume permitted for the dynamic sound track. 
     */
    public DynamicSoundTrack(int maxVolume) {
        mAllTracks = new ArrayList<Track>();        
        //mLocationManager = locMan;
        mMaxVolume = maxVolume;
    }

    /**
     * Adds a new track to the dynamic sound track.
     *
     * @param track Path to audio file on the external storage.
     * @param lat The latitude of the sound source, the closer you are to the source location
     * the louder this track will be.
     * @param lon The longitude of the sound source, the closer you are to the source location
     * the louder this track will be.
     */
    public void addTrack(final String track, float lat, float lon) {
        try {
            mAllTracks.add(new Track(track, lat, lon));
        } catch (Exception e) {
            System.err.println("Unable to add: '" + track + "' to dynamic sound track.");
        }
    }

    /**
     * Update all track levels within the DynamicSoundTrack, based on the current location
     * reported by the GPS unit, make tracks closer to the current GPS location louder
     * than those further away.
     */
    public void updateLevels(Location loc) {
        //Location loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); 
        
        if (loc != null) {
            for (Track t : mAllTracks) {
                t.updateLevel(loc.getLatitude(), loc.getLongitude());
            }        
        }
    }

    /**
     * Stops all tracks.
     */
    public void shutdown() {
        for (Track t : mAllTracks) {
            t.stop();
        }
    }

    /**
     * A Track held within the DynamicSoundTrack.
     */
    private class Track {
        private MediaPlayer player;
        private float lat, lon;

        /**
         * Constructor.
         * 
         * @param newTrack Path to the sound file in external storage that you want to loop
         * over for this track.
         * @param newLat The latitude of the source location for this track.
         * @param newLon The longitude of the source location for this track.
         * 
         * @throws IllegalArgumentException If unable to create a track from the supplied file path.
         * @throws IllegalStateException If unable to create a track from the supplied file path.
         * @throws IOException If unable to create a track from the supplied file path.
         */
        Track(final String newTrack, float newLat, float newLon)
        throws IllegalArgumentException, IllegalStateException, IOException {
            lat = newLat;
            lon = newLon;

            player = new MediaPlayer();
            player.setDataSource(Environment.getExternalStorageDirectory() + newTrack);
            player.prepare();
            player.setLooping(true);
            player.start();
        }

        /**
         * Update the level for this track, 
         * 
         * @param latitude The current latitude of the person holding the android device.
         * @param longitude The current longitude of the person holding the android device.
         */
        public void updateLevel(double latitude, double longitude) {
            float[] distance = new float[1];
            Location.distanceBetween(latitude, longitude, lat, lon, distance);            

            // Volume is the inverse of the distance. The closer to the desired location, the louder the track.
            float volume = Math.max(0.0f, (MAX_DISTANCE - distance[0]));
            volume = volume / MAX_DISTANCE;
            volume = volume * (float) mMaxVolume;
            //System.err.println("Distance:" + distance[0] + ":" + volume);

            player.setVolume(volume, volume);
        }

        /**
         * Stop the track from playing.
         */
        public void stop() {
            player.stop();            
        }
    }

    private List<Track> mAllTracks;
    private int mMaxVolume;
    private LocationManager mLocationManager;
    private static float MAX_DISTANCE = 150.0f;
}
