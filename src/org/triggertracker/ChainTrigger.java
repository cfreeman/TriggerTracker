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

	// The chain of triggers to fire one at a time.
	private ArrayList<Trigger> chainOfTriggers;

	// The index of the current trigger in the chain.
	private int currentTrigger;

	// Has this trigger gone off?
	private boolean hasTriggered;

	// The action to fire if the trigger has been tripped.
	private Action action;	

	/** 
	 * @param actionToTrigger The action to trigger once everything in the chain has triggered.
	 */
	public ChainTrigger(Action actionToTrigger) {
		action = actionToTrigger;
		chainOfTriggers = new ArrayList<Trigger>();
		hasTriggered = false;
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
	public void setAction(Action actionToTrigger) {
		action = actionToTrigger;
	}

	@Override
	public void testFire() {
		if (!hasTriggered) {
			Trigger activeTrigger = chainOfTriggers.get(currentTrigger);
			activeTrigger.testFire();

			// Move onto the next trigger in the chain if the 
			if (activeTrigger.hasTriggered()) {
				currentTrigger++;
			}

			// If we are the end of the chain - all done.
			if (currentTrigger == chainOfTriggers.size()) {
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
