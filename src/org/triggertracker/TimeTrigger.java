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

import java.util.Calendar;

public class TimeTrigger implements Trigger {

	// Has this trigger gone off?
	private boolean hasTriggered;

	// The action to fire if the trigger has been tripped.
	private Action action;

	// This trigger trips when the time is the same or greater than the
	// following number of minutes past the hour.
	private int minutes;

	/**
	 * Constructor.
	 *
	 * @param minutesPastHour The number of minutes past the hour to trigger the action.
	 * @param actionToTrigger The action to trigger, when the time is the specified number
	 * of minutes past the hour.
	 */
	public TimeTrigger(int minutesPastHour, Action actionToTrigger) {
		minutes = minutesPastHour;
		action = actionToTrigger;
		hasTriggered = false;
	}

	@Override
	public void setAction(Action actionToTrigger) {
		action = actionToTrigger;
	}

	@Override
	public Action getAction() {
		return action;
	}

	@Override
	public void testFire() {
		if (!hasTriggered) {
			Calendar cal = Calendar.getInstance();
			if (cal.get(Calendar.MINUTE) >= minutes) {
				action.trigger();
				hasTriggered = true;
			}			
		}
	}

	@Override
	public boolean hasTriggered() {
		return hasTriggered;
	}
}
