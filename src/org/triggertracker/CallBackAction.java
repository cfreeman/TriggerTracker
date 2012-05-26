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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class CallBackAction implements Action {

	// The ID of the callback to play down the line.
	private String callBackID;

	// The phone number to call back.
	private String number;

	public CallBackAction(String callBackToPlay, String numberToCall) {
		callBackID = callBackToPlay;
		number = numberToCall;
	}

	@Override
	public void trigger() {
		try {			
			System.err.println("Triggering CallBack - " + number + " : " + callBackID);
			URL centralTracker = new URL("http://teethtracker.heroku.com/device_movements/new?type=arrival&node=" + callBackID + "&number=" + number + "&v=2");
			URLConnection trackerConnection = centralTracker.openConnection();
			trackerConnection.getContentLength();

		} catch (MalformedURLException e) {
			System.err.println("malformed URL: " + e);
		} catch (IOException e) {
			System.err.println("io exception: " + e);
		}
		// TODO Auto-generated method stub
		
	}
}
