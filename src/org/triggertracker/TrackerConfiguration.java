/*
 * Copyright (c) Clinton Freeman 2015
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONObject;

import org.triggertracker.locationservices.EstimoteLocation;
import org.triggertracker.locationservices.EstimoteManager;
import org.triggertracker.locationservices.GPSLocation;
import org.triggertracker.locationservices.GPSManager;
import org.triggertracker.locationservices.TriggerLocation;

public class TrackerConfiguration {

	private final EstimoteManager mEstimoteManager;
	private final GPSManager mGPSManager;

	public TrackerConfiguration(EstimoteManager eManager, GPSManager gManager) {
		mEstimoteManager = eManager;
		mGPSManager = gManager;
	};

	public TriggerLocation buildLocation(JSONObject lObject) throws Exception {
		String type = lObject.getString("type").toLowerCase();

		if (type.equals("estimote")) {
			return new EstimoteLocation(mEstimoteManager, lObject.getString("beacon"));

		} else if (type.equals("gps")) {
			return new GPSLocation(mGPSManager,
								   (float) lObject.getDouble("latitude"),
								   (float) lObject.getDouble("longitude"));

		} else {
			throw new Exception("Unknown location type: " + type);

		}
	}

	public Action buildAction(JSONObject aObject) throws Exception {
		if (aObject == null) {
			return null;
		}
		String type = aObject.getString("type").toLowerCase();

		if (type.equals("call-back")) {
			return new CallBackAction(aObject.getString("callback"),
									  aObject.getString("number"));

		} else if (type.equals("audio")) {
			return new PlayAudioAction(aObject.getString("audioFile"));

		} else if (type.equals("dynamic-audio")) {
			return new PlayDynamicAudioAction(aObject.getString("audioFile"),
											  buildLocation(aObject.getJSONObject("fader")));

		} else if (type.equals("video")) {
			throw new Exception("video file not implemented yet");

		} else {
			throw new Exception("Unknown action type: " + type);

		}
	}

	public Trigger buildTrigger(JSONObject tObject) throws Exception {
		String type = tObject.getString("type").toLowerCase();

		if (type.equals("chain")) {
			ChainTrigger res = new ChainTrigger(buildAction(tObject.optJSONObject("action")));
			JSONArray children = tObject.getJSONArray("children");
			for (int i = 0; i < children.length(); i++) {
				res.addTrigger(buildTrigger(children.getJSONObject(i)));
			}

			return res;

		} else if (type.equals("delayed")) {
			return new DelayedTrigger(tObject.getLong("seconds"),
								      buildAction(tObject.optJSONObject("action")));

		} else if (type.equals("location")) {
			return new LocationTrigger(buildLocation(tObject.getJSONObject("location")),
									   buildAction(tObject.optJSONObject("action")));

		} else if (type.equals("time")) {
			return new TimeTrigger(tObject.getInt("minutesPast"),
								   buildAction(tObject.optJSONObject("action")));

		} else {
			throw new Exception("Unknown trigger type: " + type);
		}
	}

	public Trigger loadFromYaml(String filepath) throws Exception {
		File configFile = new File(filepath);
		//File configFile = new File(Environment.getExternalStorageDirectory() + filepath);

		InputStream configInput = new FileInputStream(configFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(configInput, "UTF-8"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
		 	sb.append(line + "\n");
		}

		JSONObject jObject = new JSONObject(sb.toString());
		return buildTrigger(jObject.getJSONObject("trigger"));
	}
}
