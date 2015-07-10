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
 *
 * R.I.P. Gebbers. 2015-06-24
 */
package org.triggertracker.locationservices;

import java.util.ArrayList;

public class EstimoteCluster implements TriggerLocation {

	public EstimoteCluster(EstimoteManager estimoteManager, double range) {
		em = estimoteManager;
		mAddressCluster = new ArrayList<String>();
		mradius = range;
	}

	public void addAddress(String beaconAddress) {		
		mAddressCluster.add(beaconAddress);
	}

	public boolean at() {
		return (distance() < mradius);
	}

	public float distance() {
		float minDistance = Float.MAX_VALUE;

		for (String a : mAddressCluster) {
			float d = (float) em.getLastKnownDistance(a);
			if (d < minDistance) {
				minDistance = d;
			}
		}

		return minDistance;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof EstimoteCluster)) {
			return false;
		}

		if (o == this) {
			return true;
		}

		EstimoteCluster rhs = (EstimoteCluster) o;
		boolean result = true;
		if (em != null) {
			if (rhs.em != null) {
				return false;
			}

			result = result && em.equals(rhs.em);
		}

		if (mAddressCluster == null) {
			if (rhs.mAddressCluster != null) {
				return false;
			}

		} else {
			result = result && mAddressCluster.equals(rhs.mAddressCluster);
		}

		return result && (Math.abs(mradius - rhs.mradius) < 0.001);
	}

	private EstimoteManager em;
	private ArrayList<String> mAddressCluster;
	private double mradius;
}