# Track-Mountaineer

## Executive Summary
The primary idea behind this project is to develop an application that serves as a great use for **MOUNTAINEERS**. Barometric pressure is directly proportional to pulse rate, higher the altitudes lowers the atmospheric pressure(P) which inturns effects the Heartbeat (HB). Mountaineers find it difficult to track these changes which turns out to be a huge risk for their life. This android app would be handy in tracking temperature and pressure while climbing mountains. A major feature of this application is sending notification during situations of Lowered Barometric Pressure where the heart beat goes below the normal pumping rate. For a better efficiency MetaWear uses Bluetooth low energy device that captures continous activity data. All the data is synced and downloaded to the Mobile device. The downloaded data is represented in a Graphical view for each day using the time timestamp. 

## Project Goals
* Use Login credentials to avoid access to unauthorized users. 
* Track Temperature/Pressure.
* Push Notifications when the barometric pressure is dangerous for human Heart beat.  
* Show a Graphical view of data for each day.


## User stories
As a **mountaineer**, I want to **see climatic changes as I climb**, so I can **be prepared for the worst**.

**Acceptance Criteria:**
* Track surrounding temperatures
* Track surrounding pressure
* Track surrounding Humidity

As a **user**, I want to **get notifications on my mobile**, so I can **use oxygen mask to maintain my heartbeat**.

**Acceptance Criteria:**
* Make a threshold pressure value where there is a risk of low pulse rate. 
* Push Notifications to the mobile with a suitable message.

As a **user**, I want to **have a graphical view of all the tracked data**, so I can **check my progress**.

**Acceptance Criteria:**
* Store the captured data in the mobile device.
* Make a graphical representation of the data.

## Misuser stories
As a **misuser**, I want to **sniff the traffic between the wearable device and the mobile application** so I can **send different values to the application/perform a man-in-the-middle attack**. 

**Mitigations:**
* Encrypt the Bluetooth channel, so that the adversary cannot figure out the BLE packets.
* The channel should operate using SSL/TLS with certificate verification

As a **malicious user**, I want to **falsify the stored data (pressure and temperature) on the mobile phone** so I can **change the graphical plot the stored pressure and temperature data**.     

**Mitigations:**
* Ensure mutual authentication is provided on all connection.
* Do not click on unwanted and malicious requests in the application.
* Ensure that device should not be physically tampered.

As a **attacker**, I want to **launch a denial of service attack on the wearable device** so I can **crash the device so that the device cannot function as it if required**. 

 **Mitigations:**
* Turn off the Bluetooth discoverable mode after pairing of the devices, since once the devices are paired they can remember each other
* Always turn off the Bluetooth when not in use
* Verify the firmware updates from the vendors website before updating the device.


## Architectural Diagram
<p align="center">
   <img width="726" alt="screen shot 2018-03-10 at 5 40 08 pm" src="https://user-images.githubusercontent.com/31106457/37247954-42049fcc-248a-11e8-8374-e9c35e349d2a.png">
</p>

## Component List
```
1. Mobile Application
The Android app connects with Meta Tracker device to fetch data using the bluetooth Module.
Once the application reaches a threshold pressure value it sends a notification to the user. 
   1.1 User Login
         The Application requires User credentials to secure data from unauthorized users.
   1.2 Home screen View
         A Homescreen that shows the recent tracked data.
   1.3 Graphical View 
         A graphical view of all the captured data for each day.
   
2. MetaWear API
   Mbient has a API Reference that allows to connect and transfer data between client(Mobile device) and Server(Meta Tracker). 
   Metawear API has interfaces which can be used to fetch data from thermistor/barometric pressure sensors. 
   
3. Hardware Devices
   Meta Tracker Sensor has built-in sensors that can capture barometric pressure and temperature. 
   The data is streamed via Bluetooth Low Energy at up to 100Hz.
   3.1 BME280 sensor for Barometer and Humidity 
   3.2 Thermistor Sensor to capture atmospheric temperature. 
```
