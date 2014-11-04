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

public class PlayVideoAction implements Action {
	public PlayVideoAction(Application app, Context context, String videoName) {
		parentApplication = app;
		parentContext = context;
		videoToTrigger = videoName;
	}

	@Override
	public void trigger() {
		System.err.println("Triggering PlayVideo - " + videoToTrigger);
		Intent dialogIntent = new Intent(parentContext, PlayVideoActivity.class);
		dialogIntent.putExtra("videoTrigger", videoToTrigger);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		parentApplication.startActivity(dialogIntent);
	}

	@Override
	public void update() {
		return;	// Nothing needs updating in this action.
	}

	private Application parentApplication;
	private Context parentContext;
	private String videoToTrigger;
}
