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

	ant debug
	adb install -r bin/TriggerTracker-debug.apk

## Connecting for Debug

	adb logcat *:W

Special thanks to [The Edge](http://edgeqld.org.au/), Daniel Flood and [Sandra Carluccio](http://sandracarluccio.net/), who have all supported the development of TriggerTracker.

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
