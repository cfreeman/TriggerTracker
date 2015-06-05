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

import java.util.ArrayList;

public class BranchTrigger implements Trigger {
	public BranchTrigger(Trigger leftTrigger, Trigger rightTrigger, Action actionToTrigger) {
		mAction = actionToTrigger;
		mLeftTrigger = leftTrigger;
		mRightTrigger = rightTrigger;
	}

	@Override
	public void testFire() {
		if (!mHasTriggered) {
			if (!mLeftTrigger.hasTriggered() && !mRightTrigger.hasTriggered()) {
				mLeftTrigger.testFire();
				mRightTrigger.testFire();
			}

			if (mLeftTrigger.hasTriggered() || mRightTrigger.hasTriggered()) {
				if (mAction != null) {
					mAction.trigger();
				}

				mHasTriggered = true;
			}
		}
	}

	@Override
	public void update() {
		mLeftTrigger.update();
		mRightTrigger.update();

		if (mAction != null) {
			mAction.update();
		}
	}

	@Override
	public boolean hasTriggered() {
		return mHasTriggered;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof BranchTrigger)) {
			return false;
		}

		if (o == this) {
			return true;
		}

		BranchTrigger rhs = (BranchTrigger) o;
		boolean result = false;

		if (mAction == null) {
			if (rhs.mAction != null) {
				return false;
			}
		} else {
			result = result && (mAction.equals(rhs.mAction));
		}

		result = result && (mLeftTrigger.equals(rhs.mLeftTrigger));
		result = result && (mRightTrigger.equals(rhs.mRightTrigger));
		return result && (mHasTriggered == rhs.mHasTriggered);
	}

	private boolean mHasTriggered = false;
	private Trigger mLeftTrigger;
	private Trigger mRightTrigger;
	private Action mAction;
}