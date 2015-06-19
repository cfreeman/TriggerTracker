/*
 * Copyright (c) Clinton Freeman 2014
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

import android.media.MediaPlayer;
import android.os.Environment;

import org.triggertracker.locationservices.TriggerLocation;

public class PlayDynamicAudioAction implements Action {

	/**
	 * Constructor
	 *
	 * @param audioFile The path to the audio file on the external file storage that you want this action to play.
	 */
	public PlayDynamicAudioAction(String audioFile, boolean looping, float volume, final TriggerLocation audioLocation) {
		mAudioToTrigger = audioFile;
		mAudioLocation = audioLocation;
		mMaxVolume = volume;
		mLooping = looping;
		mPlayer = new MediaPlayer();
	}

	@Override
	public void trigger() {
		System.err.println("Triggering PlayAudio - " + mAudioToTrigger);

        try {
            mPlayer.setDataSource(Environment.getExternalStorageDirectory() + mAudioToTrigger);
            mPlayer.prepare();
            mPlayer.setLooping(mLooping);
            mPlayer.start();

        } catch (Exception e) {
            System.err.println("Unable to play audio action: " + mAudioToTrigger);
        }
	}

	@Override
	public void update() {
		// Volume is the inverse of the distance. The closer to the desired
		// location, the louder the track.
        float targetVolume = mAudioLocation.distance() / MAX_DISTANCE;
        targetVolume = targetVolume * mMaxVolume;
        targetVolume = (float) -Math.log10(targetVolume);

        if (targetVolume < 0.0f) {
            targetVolume = 0.0f;
        }

        if (targetVolume > 1.0f) {
            targetVolume = 1.0f;
        }

        float deltaV = (targetVolume - mCurrentVolume) / INTERPOLATE_STEPS;

        if (Math.abs(deltaV) < MIN_STEP_SIZE) {
           mCurrentVolume = targetVolume;
        } else {
            mCurrentVolume = mCurrentVolume + deltaV;
        }

        // System.err.println("******D:" + mAudioLocation.distance() + ":" + targetVolume + "=" + mCurrentVolume + "+" + deltaV);
        mPlayer.setVolume(mCurrentVolume, mCurrentVolume);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PlayDynamicAudioAction)) {
			return false;
		}

		if (o == this) {
			return true;
		}

		PlayDynamicAudioAction rhs = (PlayDynamicAudioAction) o;
		boolean result = true;
		if (mAudioLocation == null) {
			if (rhs.mAudioLocation != null) {
				return false;
			}
		} else {
			result = result && mAudioLocation.equals(rhs.mAudioLocation);
		}

		if (mAudioToTrigger == null) {
			if (rhs.mAudioToTrigger != null) {
				return false;
			}
		} else {
			result = result && mAudioToTrigger.equals(rhs.mAudioToTrigger);
		}

		result = result && (mLooping == rhs.mLooping);
		result = result && (Math.abs(mMaxVolume - rhs.mMaxVolume) < 0.001);
		result = result && (Math.abs(MAX_DISTANCE - rhs.MAX_DISTANCE) < 0.001);
		result = result && (Math.abs(INTERPOLATE_STEPS - rhs.INTERPOLATE_STEPS) < 0.001);
		return result && (Math.abs(MIN_STEP_SIZE - rhs.MIN_STEP_SIZE) < 0.001);
	}

	private TriggerLocation mAudioLocation;
	private MediaPlayer mPlayer;
	private float mMaxVolume;
	private float mCurrentVolume = 0.0f;
	private static float MAX_DISTANCE = 10.0f;
	private static float INTERPOLATE_STEPS = 10.0f;
	private static float MIN_STEP_SIZE = 0.002f;
	private String mAudioToTrigger;
	private boolean mLooping = false;
}
