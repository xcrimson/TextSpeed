TextSpeed 0.0.1
=====

About
-----
This app measures the device speed when an SMS message is being sent. 
It stores the measured speed and then displays it alongside appropriate message in a list.
This app is a proof of concept of using both the GPS and the accelerometer data to enhance the speed measurement of a mobile device.

Setup
-----
1. Clone the project
2. Open Android Studio
3. Import project into the Android Studio
4. Wait for the Gradle build system to synchronize the project
5. Run the project

Usage
-----
Main screen displays a list of recent messages. When speed of outgoing message is available it is displayed in the right bottom corner of message.
Main screen contains button that shows only messages with speed above 15 MPH.

There is a settings screen accessible from drop-down menu in ActionBar. It contains settings for GPS and Accelerometer usage.

Algorithm
-----
It is possible for android application to get called by system when message is send.
It is possible to display current list of messages on a device.
It is possible for android application to work in a background process.
So actually the problem is only how to calculate speed of device in any given moment using accelerometer data.

Accelerometer data alone is not enough to calculate speed. Acceleration is rate of change of speed. Acceleration with value of 5MPH^2 could be acceleration from 5MPH to 10MPH as well as acceleration from 50MPH to 55MPH.

So this app uses GPS and Accelerometer together to calculate speed.
1. App waits until it has two recent GPS coordinates of the device.
2. It calculates speed with which device was moving between these coordinates.
3. It calculates time when device was between these coordinates.
4. Then it uses Accelerometer data to calculate how speed changed from GPS speed measurement time until now.
5. Finally it sums recent speed calculated using GPS with speed increase, calculated with accelerometer.

Precision
-----
Accelerometer is very imprecise and it constantly registers some acceleration even when there is no movement at all.
Thus one can't reliably calculate acceleration for a period of time longer than a few seconds.

Both speed and acceleration have directions. Information about their relation to each other is necessary to calculate resultant speed.
But GPS coordinate system is tied to the planet surface and accelerometer coordinate system is tied to the device.
To figure out acceleration in relation to the planet surface we need to know orientation of the device in relation to it.
This is only achievable using rotation sensor. Which effectively means an increase of already big errors in resulting data.
