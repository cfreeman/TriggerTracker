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

import java.util.ArrayList;

public class ChainTrigger implements Trigger {

	/**
	 * @param actionToTrigger The action to trigger once everything in the chain has triggered.
	 */
	public ChainTrigger(Action actionToTrigger) {
		action = actionToTrigger;
		chainOfTriggers = new ArrayList<Trigger>();
		currentTrigger = 0;
	}

	/**
	 * Adds a trigger to the end of the chain. All other triggers in the chain
	 * must be fired before this trigger will fire.
	 *
	 * @param trigger The trigger to test at the end of the chain.
	 */
	public void addTrigger(Trigger trigger) {
		chainOfTriggers.add(trigger);
	}

	@Override
	public void testFire() {
		if (!hasTriggered) {
			Trigger activeTrigger = chainOfTriggers.get(currentTrigger);

			// Move onto the next trigger in the chain if the
			if (activeTrigger.hasTriggered()) {
				currentTrigger++;
			} else {
				activeTrigger.testFire();
			}

			// If we are the end of the chain - all done.
			if (currentTrigger == chainOfTriggers.size()) {
				if (action != null) {
					action.trigger();
				}

				hasTriggered = true;
			}
		}
	}

	@Override
	public void update() {
		for (Trigger t : chainOfTriggers) {
			t.update();
		}

		if (action != null) {
			action.update();
		}
	}

	@Override
	public boolean hasTriggered() {
		return hasTriggered;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ChainTrigger)) {
			return false;
		}

		if (o == this) {
			return true;
		}

		ChainTrigger rhs = (ChainTrigger) o;
		boolean result = true;

		if (action == null) {
			if (rhs.action != null) {
				return false;
			}
		} else {
			result = result && action.equals(rhs.action);
		}

		result = result && chainOfTriggers.equals(rhs.chainOfTriggers);
		result = result && (currentTrigger == rhs.currentTrigger);
		return result && (hasTriggered == rhs.hasTriggered);
	}

	private ArrayList<Trigger> chainOfTriggers;	// The chain of triggers to fire one at a time.
	private int currentTrigger;					// The index of the current trigger in the chain.
	private boolean hasTriggered = false;		// Has this trigger gone off?
	private Action action;						// The action to fire if the trigger has been tripped.
}
