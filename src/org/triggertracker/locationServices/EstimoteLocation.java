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

public class EstimoteLocation implements TriggerLocation {

	public EstimoteLocation(EstimoteManager estimoteManager, String beaconAddress) {
		em = estimoteManager;
		address = beaconAddress;
	}

	public boolean at() {
		return (em.getLastKnownDistance(address) < RADIUS);
	}

	public float distance() {
		return (float) em.getLastKnownDistance(address);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof EstimoteLocation)) {
			return false;
		}

		if (o == this) {
			return true;
		}

		EstimoteLocation rhs = (EstimoteLocation) o;
		boolean result = true;
		if (em != null) {
			result = result && em.equals(rhs.em);
		}

		if (address != null) {
			result = result && address.equals(rhs.address);
		}

		return result && (Math.abs(RADIUS - rhs.RADIUS) < 0.001) ;
	}

	private EstimoteManager em;
	private String address;
	private static final double RADIUS = 3.5;
}