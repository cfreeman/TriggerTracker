package org.triggertracker;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class org.triggertracker.TriggerTrackerActivityTest \
 * org.triggertracker.tests/android.test.InstrumentationTestRunner
 */
public class TriggerTrackerActivityTest extends ActivityInstrumentationTestCase2<TriggerTrackerActivity> {

    public TriggerTrackerActivityTest() {
        super("org.triggertracker", TriggerTrackerActivity.class);
    }

}
