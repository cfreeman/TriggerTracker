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


public class LocationTrigger implements Trigger {

	public LocationTrigger(TriggerLocation triggerLocation, Action actionToTrigger) {
		location = triggerLocation;
		action = actionToTrigger;
	}

	@Override
	public void testFire() {
		// We only trigger once.
		if (!hasTriggered) {
			if (location.at()) {
				if (action != null) {
					action.trigger();
				}

				hasTriggered = true;
			}
		}
	}

	@Override
	public boolean hasTriggered() {
		return hasTriggered;
	}

	private boolean hasTriggered = false;		// Has this trigger gone off?
	private TriggerLocation location;			// The desired location to trip the trigger.
	private Action action;						// The action to fire if the trigger has been tripped.
}