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

import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class PlayAudioAction implements Action {

	// The audio to play back when the action is triggered.
	private String audioToTrigger;

	// The parent application for this audio playback action.
	private Application parentApplication;

	// The parent context for this audio playback action.
	private Context parentContext;

	/**
	 * @param audioFile The audio file that you want this action to play
	 */
	public PlayAudioAction(Application app, Context context, String audioFile) {
		parentApplication = app;
		parentContext = context;
		audioToTrigger = audioFile;
	}

	@Override
	public void trigger() {
		System.err.println("Triggering PlayAudio - " + audioToTrigger);
		Intent dialogIntent = new Intent(parentContext, PlayAudioActivity.class);
		dialogIntent.putExtra("audioTrigger", audioToTrigger);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		parentApplication.startActivity(dialogIntent);
	}
}
