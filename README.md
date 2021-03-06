# AutomaticAttendanceMarker
Android Open Source application to poll attendance in classes/lecture halls using WLAN
<br/><br/>

### Read this paper if you want to know how it works in detail
https://ieeexplore.ieee.org/document/8474885
<br/><br/>

### Go through this presentation file for a cool overview
https://github.com/Vieper1/WLAN-Attendance-Marker/blob/master/WLAN.pdf


### Screenshot taken after the attendance session<br/>
a. The Darkest circles show roll numbers not registered<br/>
b. The Non-Filled circles show registered numbers that are absent during the session<br/>
c. The White-Filled circles show numbers that were present in the session<br/>
![Image Showing Attendance Stats](https://github.com/Vieper1/WLAN-Attendance-Marker/blob/master/Screenshots/WLAN%20Screenshot%201.png)

### Screenshot of the student data page<br/>
![Image Attendance Polling Page Showing Active Attendance](https://github.com/Vieper1/WLAN-Attendance-Marker/blob/master/Screenshots/WLAN%20Screenshot%202.png)

### Screenshot (cropped) of the unique QR code generated for the student
![Image Attendance Polling Page Showing Active Attendance](https://github.com/Vieper1/WLAN-Attendance-Marker/blob/master/Screenshots/WLAN%20Screenshot%203.png)


# How to run project
1. Download zip
2. Extract to convenient location
3. Open Android Studio
4. File > 'Open Project' > browse and select the folder you just extracted
5. Gradle build. Install dependencies if asked

# Overview of the mechanism
Procedure for the Professor's side of the app
1. Create a class
2. Enter size/class strength
3. You'll have a grid view of all roll numbers
4. Add students to the class
5. Create session (Lecture session)
  5.1 You can edit the session length and polling rates from the settings menu
6. Let it poll till the end of lecture
7. Get stats

Procedure for the Student's/Attendee's side of the app
1. Open the app
2. Log into your gmail account
3. Check if your mobile supports SSIDs with special characters
4. Use 'Show QR Code' to enroll into any class
5. When needed/before lecture, turn on the application and let it switch on the WIFI-Hotspot
6. Your attendance polling begins once the host starts the session

# Future work/Contribution scope
1. Database functionality to aggregate attendance data collected and produce stats, e.g. Weekly average of a particular student
2. Automatic mailing in case a weekly/monthly average goes below a threshold
3. Optimize the AttendanceService to improve phone's Stay-ON time
