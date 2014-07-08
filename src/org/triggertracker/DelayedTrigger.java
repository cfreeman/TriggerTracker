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

public class DelayedTrigger implements Trigger {

	// Has this trigger gone off?
	private boolean hasTriggered = false;

	// The action to fire if the trigger has been tripped.
	private Action action;

	// The number of seconds to wait until triggering action.
	private long seconds;

	// When this trigger was first tested.
	private long startTime = -1;

	/**
	 * Constructor.
	 *
	 * @param secondsToTrigger The number of seconds till the
	 * @param actionToTrigger The action to trigger, when the time is the specified number
	 * of minutes past the hour.
	 */
	public DelayedTrigger(long secondsToTrigger, Action actionToTrigger) {
		seconds = secondsToTrigger;
		action = actionToTrigger;
	}

	@Override
	public void testFire() {
		long currentTime = (System.currentTimeMillis() / 1000);

		if (startTime == -1) {
			startTime = currentTime;
		}

		if (!hasTriggered && (currentTime - startTime) > seconds) {
			if (action != null) {
				action.trigger();
			}

			hasTriggered = true;
		}
	}

	@Override
	public boolean hasTriggered() {
		return hasTriggered;
	}
}
