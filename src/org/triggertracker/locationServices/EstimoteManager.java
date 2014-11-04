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
package org.triggertracker.locationservices;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import java.util.Hashtable;
import java.util.List;

public class EstimoteManager {
	public EstimoteManager(BeaconManager locationManager) {
		locationManager.setRangingListener(new BeaconManager.RangingListener() {
			public void onBeaconsDiscovered(Region region, final List<Beacon> rangedBeacons) {
				for (Beacon b : rangedBeacons) {
					ranges.put(b.getMacAddress(), Utils.computeAccuracy(b));
				}
			}
		});
	}

	public double getLastKnownDistance(String beaconAddress) {
		try {
			Double d = ranges.get(beaconAddress);

			if (d == null) {
				return Double.MAX_VALUE;
			} else {
				return d.doubleValue();
			}
		} catch (Exception e) {
			return Double.MAX_VALUE;
		}
	}

	private Hashtable<String, Double> ranges = new Hashtable<String, Double>();
}