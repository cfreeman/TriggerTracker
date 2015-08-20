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

import android.content.Context;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONObject;

import org.triggertracker.locationservices.EstimoteCluster;
import org.triggertracker.locationservices.EstimoteLocation;
import org.triggertracker.locationservices.EstimoteManager;
import org.triggertracker.locationservices.GPSLocation;
import org.triggertracker.locationservices.GPSManager;
import org.triggertracker.locationservices.TriggerLocation;

public class TrackerConfigurationTest extends TestCase {

	private TrackerConfiguration mTrackerConfiguration;
	private TriggerService mTriggerService;

	protected void setUp() {
		mTriggerService = new TriggerService();
		mTrackerConfiguration = new TrackerConfiguration(mTriggerService.getEstimoteManager(),
														 mTriggerService.getGPSManager(),
														 mTriggerService.getApplication(),
														 mTriggerService.getApplicationContext());
	}

	// Build location tests.
	public void testEstimoteLocation() throws Exception {
		JSONObject o = new JSONObject("{\"type\" : \"estimote\",\"beacon\" : \"CC:4A:11:09:A2:C3\", \"range\" : 3.5}");
		TriggerLocation l = mTrackerConfiguration.buildLocation(o);

		assertNotNull(l);
		assertEquals(new EstimoteLocation(mTriggerService.getEstimoteManager(), "CC:4A:11:09:A2:C3", 3.5), l);
	}

	public void testEstimoteCluster() throws Exception {
		JSONObject o = new JSONObject("{\"type\" : \"estimote-cluster\", \"range\" : 3.5, \"beacons\" : [\"CC:4A:11:09:A2:C3\", \"CA:4A:11:09:A2:C3\"]}");
		TriggerLocation l = mTrackerConfiguration.buildLocation(o);

		assertNotNull(l);
		EstimoteCluster lhs = new EstimoteCluster(mTriggerService.getEstimoteManager(), 3.5);
		lhs.addAddress("CC:4A:11:09:A2:C3");
		lhs.addAddress("CA:4A:11:09:A2:C3");

		assertEquals(lhs, l);
	}

	public void testGPSLocation() throws Exception {
		JSONObject o = new JSONObject("{\"type\" : \"gps\",\"latitude\" : 15.5, \"longitude\" : 16.6}");
		TriggerLocation l = mTrackerConfiguration.buildLocation(o);

		assertNotNull(l);
		assertEquals(new GPSLocation(mTriggerService.getGPSManager(), 15.5f, 16.6f), l);
	}

	public void testUnknownLocation() {
		try {
			JSONObject o = new JSONObject("{\"type\" : \"foo\",\"latitude\" : 15.5, \"longitude\" : 16.6}");
			TriggerLocation l = mTrackerConfiguration.buildLocation(o);
			fail();	// We expect an exception to be thrown by the above.
		} catch (Exception e) {
		}
	}

	public void testAudioAction() throws Exception {
		JSONObject o = new JSONObject("{\"type\" : \"audio\",\"audioFile\" : \"tunes.wav\", \"volume\" : 0.5}");
		Action a = mTrackerConfiguration.buildAction(o);

		assertNotNull(a);
		assertEquals(new PlayAudioAction("tunes.wav", 0.5f), a);
	}

	public void testDynamicAudioAction() throws Exception {
		JSONObject o = new JSONObject("{\"type\" : \"dynamic-audio\", \"audioFile\" : \"/station1.mp3\", \"looping\" : true, \"volume\" : 0.5, \"fader\" : { \"type\" : \"estimote\", \"beacon\" : \"CC:4A:11:09:A2:C3\", \"range\" : 3.5}}");
		Action a = mTrackerConfiguration.buildAction(o);

		assertNotNull(a);
		assertEquals(new PlayDynamicAudioAction("/station1.mp3", true, 0.5f, new EstimoteLocation(mTriggerService.getEstimoteManager(), "CC:4A:11:09:A2:C3", 3.5)), a);
	}

	public void testVideoAction() {
		try {
			JSONObject o = new JSONObject("{\"type\" : \"video\",\"videoFile\" : \"anime.avi\"}");
			Action a = mTrackerConfiguration.buildAction(o);

			assertNotNull(a);
			assertEquals(new PlayVideoAction(mTriggerService.getApplication(), mTriggerService.getApplicationContext(), "anime.avi"));

		} catch (Exception e) {
		}
	}

	public void testUnknownAction() {
		try {
			JSONObject o = new JSONObject("{\"type\" : \"fooble\",\"videoFile\" : \"anime.avi\"}");
			Action a = mTrackerConfiguration.buildAction(o);
			fail(); //We expect an exception to be thrown by the above.

		} catch (Exception e) {
		}
	}

	public void testTimeTrigger() throws Exception {
		JSONObject o = new JSONObject("{\"type\" : \"time\", \"minutesPast\" : 4, \"action\" : {\"type\" : \"audio\",\"audioFile\" : \"tunes.wav\", \"volume\" : 0.5}}");
		Trigger t = mTrackerConfiguration.buildTrigger(o);

		assertNotNull(t);
		assertEquals(new TimeTrigger(4, new PlayAudioAction("tunes.wav", 0.5f)), t);
	}

	public void testLocationTrigger() throws Exception {
		JSONObject o = new JSONObject("{\"type\" : \"location\", \"location\" : {\"type\" : \"estimote\",\"beacon\" : \"CC:4A:11:09:A2:C3\", \"range\" : 3.5}, \"action\" : {\"type\" : \"audio\",\"audioFile\" : \"tunes.wav\", \"volume\" : 0.5}}");
		Trigger t = mTrackerConfiguration.buildTrigger(o);

		assertNotNull(t);
		assertEquals(new LocationTrigger(new EstimoteLocation(mTriggerService.getEstimoteManager(), "CC:4A:11:09:A2:C3", 3.5), new PlayAudioAction("tunes.wav", 0.5f)), t);
	}

	public void testDelayedTrigger() throws Exception {
		JSONObject o = new JSONObject("{\"type\" : \"delayed\", \"seconds\" : 5, \"action\" : {\"type\" : \"audio\",\"audioFile\" : \"tunes.wav\", \"volume\" : 0.5}}");
		Trigger t = mTrackerConfiguration.buildTrigger(o);

		assertNotNull(t);
		assertEquals(new DelayedTrigger(5, new PlayAudioAction("tunes.wav", 0.5f)), t);
	}

	public void testChainTrigger() throws Exception {
		JSONObject o = new JSONObject("{\"type\" : \"chain\", \"action\" : null, \"children\" : [{\"type\" : \"delayed\", \"seconds\" : 5, \"action\" : {\"type\" : \"audio\",\"audioFile\" : \"tunes.wav\", \"volume\" : 0.5}}]}");
		Trigger t = mTrackerConfiguration.buildTrigger(o);

		assertNotNull(t);
		ChainTrigger lhs = new ChainTrigger(null);
		lhs.addTrigger(new DelayedTrigger(5, new PlayAudioAction("tunes.wav", 0.5f)));
		assertEquals(lhs, t);
	}

	public void testBranchTrigger() throws Exception {
		JSONObject o = new JSONObject("{\"type\" : \"branch\", \"left\" : {\"type\" : \"delayed\", \"seconds\" : 4, \"action\" : {\"type\" : \"audio\",\"audioFile\" : \"left.wav\", \"volume\" : 0.5}}, \"right\" : {\"type\" : \"delayed\", \"seconds\" : 5, \"action\" : {\"type\" : \"audio\",\"audioFile\" : \"right.wav\", \"volume\" : 0.5}}, \"action\" : {\"type\" : \"audio\", \"audioFile\" : \"tunes.wav\", \"volume\" : 0.5}}");
		Trigger t = mTrackerConfiguration.buildTrigger(o);

		assertNotNull(t);
		DelayedTrigger left = new DelayedTrigger(4, new PlayAudioAction("left.wav", 0.5f));
		DelayedTrigger right = new DelayedTrigger(5, new PlayAudioAction("right.wav", 0.5f));
		assertEquals(new BranchTrigger(left, right, new PlayAudioAction("tunes.wav", 0.5f)), t);
	}

	public void testUnknownTrigger() {
		try {
			JSONObject o = new JSONObject("{\"type\" : \"fooble\",\"videoFile\" : \"anime.avi\"}");
			Trigger t = mTrackerConfiguration.buildTrigger(o);
			fail(); // We expect an exception to be thrown above.

		} catch (Exception e) {

		}
	}
}