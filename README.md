# ams-student
Android app for student for Attendance Management System project. 

# Introduction 
This project is “Attendance Management System”. This system is provided for  three main parts (the dean, the teacher and the student) to do taking attendance, calculating the percentage of the attendance and receiving the medical leaves. In the dean panel, the dean can accept the student attendance record that the class teacher sent. The dean can also accept the medical leaves and the system can update the corresponding student attendance automatically. She can also inform the underrated students and other important information. In the teacher panel, the teacher can generate QR code for the appropriate class time and he/she can confirm the attendance from the student and then submit to the dean. In the student panel, the student can scan the generated QR code from the teacher to get their attendance. The student can also view their attendance percentage transparently and can take the medical leaves by uploading the medical letter and choosing the dates that they were absent. 

This system supports simplicity, time saving and accuracy. Thus, the attendance management system can be claimed as a tool for universities or offices as different features of our system can aid the users in different ways.

## Students app
For the students, they have to use android application which will allow them to scan the QR code, which is generated authentically by the teacher. A special feature here is that the students are not allowed to scan their QR codes when they are more than 10 meters away from the hosted phone that generated the QR code. They can login to the application with their id, which is MKPT number in this case, and password which need to be already registered. When they enter to the application, students can easily view their attendance percentage of each subject and they can also ask medical leave to the dean for their absence.

## Functions 
- Register (Enter required information)
- Login (Enter student number and password)
- View attendance (by daily percentage or by date)
- Upload medical leaves
- Scan QR code to get attendance
- Logout

## Technologies Used
1. Kotlin 
2. Google Firebase for data storing and realtime database
3. JSON

## Software Specification 
- Android 4.4 Kit Kat minimum

# Daigrams 
## Student app flowchart
Student needs to register with the corresponding subjects he/she take and his information. If he/she has already registered, he can enter the account by using student number and password. After login, he can view the attendance percentage subject by subject and he can also view which day he attended by date. He can get the attendance by scanning the QR code generated from the class teacher. He can upload the medical leaves to the dean. He can also edit all of his information.


# License
[MIT License](LICENSE)