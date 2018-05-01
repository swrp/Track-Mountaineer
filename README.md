# Track-Mountaineer

## Executive Summary
The primary idea behind this project is to develop an application that serves as a great use for **MOUNTAINEERS**. Barometric pressure is directly proportional to pulse rate, higher the altitudes lowers the atmospheric pressure(P) which inturn effects the Heartbeat (HB). Mountaineers find it difficult to track these changes which turns out to be a huge risk for their life. This android app would be handy in tracking temperature and pressure while climbing mountains. A major feature of this application is sending notification during situations of Lowered Barometric Pressure where the heart beat goes below the normal pumping rate. For a better efficiency MetaWear uses Bluetooth low energy device that captures continous activity data. All the data is synced and downloaded to the Mobile device. The downloaded data is represented in a Graphical view for each day using the time timestamp. 

## Project Goals
* Track Temperature and Pressure.
* Push Notifications when the barometric pressure is dangerous for human Heart beat.  
* Save the tracked data onto phone internal memory
* Show a Graphical view of data for each day.


## User stories
As a **mountaineer**, I want to **track drop in pressure as I climb**, so I can **be prepared for the worst**.

**Acceptance Criteria:**
* Track surrounding temperatures
* Track surrounding pressure
* Save all the tracked data in a file

As a **user**, I want to **get notifications on my mobile**, so I can **use oxygen mask to maintain my heartbeat**.

**Acceptance Criteria:**
* Make a threshold pressure value where there is a risk of low pulse rate. 
* Push Notifications to the mobile with a suitable message.

As a **user**, I want to **have a graphical view of all the tracked data**, so I can **check my progress**.

**Acceptance Criteria:**
* Store the captured data in the mobile device.
* Make a graphical representation of the data.

## Misuser stories

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

As a **misuser**, I want to **sniff the traffic between the wearable device and the mobile application** so I can **send different values to the application/perform a man-in-the-middle attack**. 

**Mitigations:**
* Encrypt the Bluetooth channel, so that the adversary cannot figure out the Bluetooth Low Energy(BLE) packets.


## Architectural Diagram
<p align="center">
   <img width="897" alt="screen shot 2018-03-13 at 7 34 49 pm" src="https://user-images.githubusercontent.com/31106457/37377057-be5337c4-26f5-11e8-95c6-de5e2e4ddec9.png">
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

## Security Analysis
| Component Name                                                                                                                      | Category of Vulnerability | Issue Description                                                                                                                           | Mitigation                                                                              | 
|-------------------------------------------------------------------------------------------------------------------------------------|---------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------| 
| Metawear device                                                                                                                     | Data sniffing             | When the data is sending through the BLE channel, attacker can extract the information from the BLE interface between device and smartphone | Encrypting the BLE channel.                                                             | 
|                                                                                                                                     | Denial of Service(DoS)    | DoS attack can be possible which is delivered by fake firmware update.                                                                      | Update the device only  from vendor legitimate website                                  |                                                                                   
|                                                                                                                                     | Tampering                 | Changing sensor components via physical manipulation of the device.                                                                         | Securing the metaware device in a tamper proof box.                                     | 
| Mobile Application                                                                                                                  | User impersonation        | Spoofing the user with unauthorized user credentials                                                                                        | Ensure device mutual authentication is performed for all connections. Implement multi-factor authentication to prevent automated, credential stuffing, brute force, and stolen credential re-use attacks. |                           |                                                                                                                                             |                                                                                         | 
|                                                                                                                                     |                   Data Tampering        | Physical tampering of the data on the mobile application and changing the values                                                            | The data should be resides in encrypted storage in the internal app directory.          | 
|                                                                                                                                      | Phishing                  | The mobile application is prone to phishing attacks through malicious popups or alert.                                                      | User training to prevent clicking on unknown popups and whitelisting of trusted sources | 


## Hardware/Software Requirements
 * Metawear device which can track pressure.
 * Android 4.4+ (API Level 19+) with Bluetooth LE capable smart phone or tablet
 * [Android Studios](https://developer.android.com/studio/)
 * [Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
 
## Installation Directions
 * Clone this repository https://github.com/swrp/Track-Mountaineer.git
 * Open this folder in the Android studios
 * Run the app on the targeted Android device using Android Device Bridge(ADB).
 * If you want to run the application on the Phone or Tablet to skip the installation process [download the application here](https://github.com/swrp/Track-Mountaineer/blob/master/Track-Mountaineer.apk).
 
## Getting Started Directions
 * Connect the Metawear device to the app using BLE.
 * If you want to track the pressure Click on the Start Button, it will continue to track the atmospheric pressure.
 * If you want to download the tracked data on to the phone internal memory click on the download button.
 * The application sends a notification if the pressure is below the threshold, and by clicking on the notification it gives the graph       of the tracked pressure.      
 
## Presentation Slides
   [Slides]
 

 

 


