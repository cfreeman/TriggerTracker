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
import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;

public class PlayAudioActivity extends Activity {		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Work out if we are already playing this audio or not.
        boolean isPlaying = false;
        if (savedInstanceState != null) {
        	isPlaying = savedInstanceState.getBoolean("playing");
        }

        if (!isPlaying) {
	        MediaPlayer mp = new MediaPlayer();
			try {
				File f = Environment.getExternalStorageDirectory();
				mp.setDataSource(f + "/" + getIntent().getStringExtra("audioTrigger"));
				mp.prepare();
				mp.start();

	            // Close this activity when the audio finishes playing back.
	            mp.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer player) {
						finish();
					}
	            });

			} catch (IllegalArgumentException e) {
				System.err.println("Unable to play audio");
			} catch (IllegalStateException e) {
				System.err.println("Unable to play audio");
			} catch (IOException e) {
				System.err.println("Unable to play audio");
			}
        }
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	outState.putBoolean("playing", true);
    	super.onSaveInstanceState(outState);
    }
}
