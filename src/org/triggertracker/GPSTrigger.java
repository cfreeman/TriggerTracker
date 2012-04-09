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

import android.location.Location;
import android.location.LocationManager;

public class GPSTrigger implements Trigger {

	// The RADIUS to use around the specified coordinate, whenever we are near the location
	// this trigger will trip.
	private static final float RADIUS = 7.0f; 

	// The location manager that we fetch GPS coordinates from.
	private LocationManager lm;

	// Has this trigger gone off?
	private boolean hasTriggered;

	// The action to fire if the trigger has been tripped.
	private Action action;

	// The latitude that we want to use when triggering the above action.
	private float lat;

	// The longitude that we want to use when triggering the above action.
	private float lon;

	public GPSTrigger(LocationManager locationManager, float latToTrigger, float lonToTrigger, Action actionToTrigger) {
		lm = locationManager;
		hasTriggered = false;
		action = actionToTrigger;
		lat = latToTrigger;
		lon = lonToTrigger;
	}

	@Override
	public void setAction(Action actionToTrigger) {
		action = actionToTrigger;
	}

	@Override
	public void testFire() {
		// We only trigger once.
		if (!hasTriggered) {
			Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER); 
			float[] distance = new float[1];

			if (loc != null) {
				Location.distanceBetween(loc.getLatitude(),
										 loc.getLongitude(),
										 lat,
										 lon,
										 distance);

				System.err.println("Testing Loc - [" + loc.getLatitude() + ", " + loc.getLongitude() + "] - " + distance[0]);

				if (distance[0] < RADIUS) {
					System.err.println("GPS Trigger Fired [" + lat + ", " + lon + "]");
					action.trigger();
					hasTriggered = true;
				}
			}
		}
	}

	@Override
	public boolean hasTriggered() {
		return hasTriggered;
	}
}
