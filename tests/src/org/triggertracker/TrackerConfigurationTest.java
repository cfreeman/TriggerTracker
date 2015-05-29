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

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONObject;

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
		mTrackerConfiguration = new TrackerConfiguration(mTriggerService.getEstimoteManager(), mTriggerService.getGPSManager());
	}

	// Build location tests.
	public void testEstimoteLocation() throws Exception {
		JSONObject o = new JSONObject("{\"type\" : \"estimote\",\"beacon\" : \"CC:4A:11:09:A2:C3\"}");
		TriggerLocation l = mTrackerConfiguration.buildLocation(o);

		assertNotNull(l);
		assertEquals(new EstimoteLocation(mTriggerService.getEstimoteManager(), "CC:4A:11:09:A2:C3"), l);
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

	// Build action tests.
	public void testCallBackAction() throws Exception {
		JSONObject o = new JSONObject("{\"type\" : \"call-back\",\"callback\" : \"foo\", \"number\" : \"555-balls\"}");
		Action a = mTrackerConfiguration.buildAction(o);

		assertNotNull(a);
		assertEquals(new CallBackAction("foo", "555-balls"), a);
	}

	public void testAudioAction() throws Exception {
		JSONObject o = new JSONObject("{\"type\" : \"audio\",\"audioFile\" : \"tunes.wav\"}");
		Action a = mTrackerConfiguration.buildAction(o);

		assertNotNull(a);
		assertEquals(new PlayAudioAction("tunes.wav"), a);
	}

	public void testDynamicAudioAction() throws Exception {
		JSONObject o = new JSONObject("{\"type\" : \"dynamic-audio\", \"audioFile\" : \"/station1.mp3\", \"fader\" : { \"type\" : \"estimote\", \"beacon\" : \"CC:4A:11:09:A2:C3\"}}");
		Action a = mTrackerConfiguration.buildAction(o);

		assertNotNull(a);
		assertEquals(new PlayDynamicAudioAction("/station1.mp3", new EstimoteLocation(mTriggerService.getEstimoteManager(), "CC:4A:11:09:A2:C3")), a);
	}

	public void testVideoAction() {
		try {
			JSONObject o = new JSONObject("{\"type\" : \"video\",\"videoFile\" : \"anime.avi\"}");
			Action a = mTrackerConfiguration.buildAction(o);
			fail(); //We expect an exception to be thrown by the above.

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
}