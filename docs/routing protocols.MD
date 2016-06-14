#Adding custom routing protocols

Is possible to add custom routing protocols via the creation of a zip file in the sdcard/external memory of the device.

The zip file needs to be in a folder named "routing_protocols". 

The obligatory contents of the zip are:
- start.sh : a bash script that can start the routing protocol;
- stop.sh : a bash script that can stop and clean the routing protocol. It shouldn't except the routing protocol running when is executed, since the OS could kill it for some reason or the device was rebooted without notice (power failure for example);  
- other files : the routing protocol files needed to run;


---
#Existing environment variables

In order to be easier script the run of routing protocols some variables exist in the bash environment that they run.

Currently the existing environment variables are:
- $iwface - the interface name where the ad-hoc network will exist;

Other variables could be added in the future, to ask one open an issue :-)


# Example

A example of a custom routing protocol can be seen [here](https://github.com/eSoares/Routing-Protocol-package-to-Android-Ad-hoc-framework).