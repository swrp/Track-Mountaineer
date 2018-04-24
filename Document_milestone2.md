
# Milestone_2

## Overview
Based on the user stories we came up in the milestone1, we started working on those user stories and we have made progress in acheiving the following tasks of user stories. 
### Completed Tasks:
#### User Story-1: 
As a **mountaineer**, I want to **track drop in pressure as I climb**, so I can **be prepared for the worst**.[code]( https://github.com/swrp/Track-Mountaineer/blob/master/app/src/main/java/com/example/swrp/trackmountaineer/DownloadHandler.java).
*	Tracked Pressure from the MetaWear Sensor using BLE connection. 
*	Convert the tracked data into .csv file 
*	Save the file as METAWEAR.csv in Mobile Internal storage - Downloads Folder

#### User Story-2: 
As a **user**, I want to **get notifications on my mobile**, so I can **use oxygen mask to maintain my heartbeat**.[code](https://github.com/swrp/Track-Mountaineer/blob/master/app/src/main/java/com/example/swrp/trackmountaineer/MainActivity.java)
*	Implemented Notifications using NotificationManager. 
*	By tapping on the Notification it routes to the Graphical representation of the tracked data

#### User Story-3: 
As a **user**, I want to **have a graphical view of all the tracked data**, so I can **check my progress**. [code](https://github.com/swrp/Track-Mountaineer/blob/master/app/src/main/java/com/example/swrp/trackmountaineer/ShowGraph.java)
*	Shows a Graph of tracked data.
*	Implemented Graph View using MPAndroid Chart Library.

### Future Extensions:
*	Represent a better view of the graph
*	Track Atmospheric Temperature
*	Implemented Services to run the application when the mobile is locked - **needs to be tested**.
