==============
TriggerTracker
==============

An android application that can be configured for use in locative performances.

**Performances powered by TriggerTracker:**

* [Clarity in Transit](http://reprage.com/post/clarity-in-transit/)
* [This is Capital City](http://reprage.com/post/capital-city/)
* [Where's Alice](http://reprage.com/post/alice/)
* [This is Kansas City](http://reprage.com/post/kansas/)

## Building and Installing

### Using the docker development environment:

	docker run --privileged -v /dev/bus/usb:/dev/bus/usb -i -t triggertracker ant debug install test
	docker run --privileged -v /dev/bus/usb:/dev/bus/usb -t -t triggertracker adb install -r bin/TriggerTracker-debug.apk

### Using the android SDK installed natively:

	ant clean debug install test
	ant debug
	adb install -r bin/TriggerTracker-debug.apk

## Connecting for Debug

	adb logcat *:W

## Configuration

TriggerTracker expects a configuration file called config.json and all related media to be residing in the internal storage's root directory.

The JSON configuration file can be used to customise a locative performance.

### Triggers

Triggers are events or real-world conditions that the application checks to see if they have occured. These triggers include:

#### delayed 

A delayed trigger will wait for a certain number of seconds to elapse before invoking the supplied action.

**Parameters:**
*seconds* - The number of seconds to wait before invoking the supplied action.
*action* - The action to invoke after the nominated number of seconds have elapsed.

**Example config snippet:**

	{
		"type" : "delayed", 
		"seconds" : 5, 
		"action" : {
			"type" : "audio",
			"audioFile" : "tunes.wav",
			"volume" : 0.5
		}
	}

#### time

A time trigger will wait till a certain number of minutes past the hour have elapsed before invoking the supplied action.

**Parameters:**
*minutesPast* - The number of minutes past the hour to wait before invoking the supplied action.
*action* - The action to invoke after the number of minutes past the hour have elapsed.

**Example config snippet:**

	{
		"type" : "time", 
		"minutesPast" : 4, 
		"action" : {
			"type" : "audio",
			"audioFile" : "tunes.wav", 
			"volume" : 0.5
		}
	}

#### location

A location will trigger will wait till the mobile device has reached a specified location (either GPS coordinates, or near a nominated iBeacon) before invoking the supplied action.

**Parameters:**
*location* - The location that will trigger the supplied action. This can be either a GPS location or estimote location.
*action* - The action to invoke when the mobile device reaches the nominated location.

**Example config snippet (GPS):**

	{
		"type" : "location", 
		"location" : {
			"type" : "gps",
			"latitude" : -16.077282, 
			"longitude" : 145.470823}, 
		"action" : {
			"type" : "audio",
			"audioFile" : "tunes.wav", 
			"volume" : 0.5
		}
	}

**Example config snippet (Estimote/iBeacon):**

	{
		"type" : "location", 
		"location" : {
			"type" : "estimote",
			"beacon" : "CC:4A:11:09:A2:C3"
		}, 
		"action" : {
			"type" : "audio",
			"audioFile" : "tunes.wav", 
			"volume" : 0.5
		}
	}


#### chain

Chain triggers allow you to specify a set of triggers that must occur in a specific order (one after the other).

**Parameters:**
*action* - The action to invoke after all the children actions have been triggered.
*children* - The list of children triggers that will occur one after the other. The example below has two child triggers, the delayed trigger will go first, followed by the location trigger.

**Example config snippet:**

	{
		"type" : "chain", 
		"action" : null, 
		"children" : [{
			"type" : "delayed",
			"seconds" : 5, 
			"action" : {
				"type" : "audio",
				"audioFile" : "tunes.wav", 
				"volume" : 0.5
			}
		},{
			"type" : "location", 
			"location" : {
				"type" : "estimote",
				"beacon" : "CC:4A:11:09:A2:C3"
			}, 
			"action" : {
				"type" : "audio",
				"audioFile" : "tunes.wav", 
				"volume" : 0.5
			}
		}]
	}

#### branch

Branch triggers allow you to specify two exclusive triggers, only one of which will ever trigger (locking the other pathway out).

**Parrameters:**
*left* - The 'left' pathway that this branch can follow.
*right* - The 'right' pathway that this branch can follow.
*action* - The action after a pathway has been selected / completed.

**Example config snippet:**

	{
		"type" : "branch", 
		"left" : {
			"type" : "delayed",
			"seconds" : 4,
			"action" : {
				"type" : "audio",
				"audioFile" : "left.wav",
				"volume" : 0.5
			}
		},
		"right" : {
			"type" : "delayed", 
			"seconds" : 5,
			"action" : {
				"type" : "audio",
				"audioFile" : "right.wav",
				"volume" : 0.5
			}
		},
		"action" : {
			"type" : "audio",
			"audioFile" : "tunes.wav",
			"volume" : 0.5
		}
	}

### Actions

Actions are things that the application can do when a trigger is activated. These actions include:

#### call-back
#### audio
#### dynamic-audio
#### video

### Soundtracks


## License (MIT)

Copyright (c) Clinton Freeman 2012

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Special thanks to [The Edge](http://edgeqld.org.au/), Daniel Flood and [Sandra Carluccio](http://sandracarluccio.net/), who have all supported the development of TriggerTracker.