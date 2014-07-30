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

import org.triggertracker.locationservices.TriggerLocation;

public class DynamicSoundTrack {

    /**
     * Constructor.
     */
    public DynamicSoundTrack(float maxVolume) {
        mAllTracks = new ArrayList<Track>();
        mMaxVolume = maxVolume;
    }

    /**
     * Adds a new track to the dynamic sound track.
     *
     * @param track Path to audio file on the external storage.
     * @param location The location of the sound source, the closer you are to the source location
     * the louder this track will be.
     */
    public void addTrack(final String track, final TriggerLocation location) {
        try {
            mAllTracks.add(new Track(track, location));
        } catch (Exception e) {
            System.err.println("Unable to add: '" + track + "' to dynamic sound track.");
        }
    }

    /**
     * Update all track levels within the DynamicSoundTrack, based on the current location
     * reported by the GPS unit, make tracks closer to the current GPS location louder
     * than those further away.
     */
    public void updateLevels() {
        for (Track t : mAllTracks) {
            t.updateLevel();
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
        private MediaPlayer mPlayer;
        private TriggerLocation mLocation;

        /**
         * Constructor.
         *
         * @param newTrack Path to the sound file in external storage that you want to loop
         * over for this track.
         * @param location The source location for this track.
         *
         * @throws IllegalArgumentException If unable to create a track from the supplied file path.
         * @throws IllegalStateException If unable to create a track from the supplied file path.
         * @throws IOException If unable to create a track from the supplied file path.
         */
        Track(final String newTrack, final TriggerLocation location)
        throws IllegalArgumentException, IllegalStateException, IOException {
            mLocation = location;

            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(Environment.getExternalStorageDirectory() + newTrack);
            mPlayer.prepare();
            mPlayer.setLooping(true);
            mPlayer.start();
        }

        /**
         * Update the level for this track,
         *
         * @param latitude The current latitude of the person holding the android device.
         * @param longitude The current longitude of the person holding the android device.
         */
        public void updateLevel() {
            // Volume is the inverse of the distance. The closer to the desired
            // location, the louder the track.
            float targetVolume = mLocation.distance() / MAX_DISTANCE;
            targetVolume = targetVolume * mMaxVolume;
            targetVolume = (float) -Math.log10(targetVolume);

            if (targetVolume < 0.0f) {
                targetVolume = 0.0f;
            }

            if (targetVolume > 1.0f) {
                targetVolume = 1.0f;
            }

            float deltaV = (targetVolume - mCurrentVolume) / INTERPOLATE_STEPS;
            mCurrentVolume = mCurrentVolume + deltaV;

            //System.err.println("******D:" + mLocation.distance() + ":" + targetVolume + "=" + mCurrentVolume + "+" + deltaV);
            mPlayer.setVolume(mCurrentVolume, mCurrentVolume);
        }

        /**
         * Stop the track from playing.
         */
        public void stop() {
            mPlayer.stop();
        }
    }

    private List<Track> mAllTracks;
    private float mMaxVolume;
    private float mCurrentVolume = 0.0f;
    private static float MAX_DISTANCE = 15.0f;
    private static float INTERPOLATE_STEPS = 10.0f;
}
