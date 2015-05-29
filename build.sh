#! /bin/bash

docker build -t triggertracker .

docker run --privileged -v /dev/bus/usb:/dev/bus/usb -i -t triggertracker adb devices
sleep 5

docker run --privileged -v /dev/bus/usb:/dev/bus/usb -i -t triggertracker adb uninstall TriggerTracker-debug.apk
docker run --privileged -v /dev/bus/usb:/dev/bus/usb -i -t triggertracker ant install test
