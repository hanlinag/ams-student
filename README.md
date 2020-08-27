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

## Will it work for me?
This project is not designed for general usecase. It is designed based on the manual system of [University of Computer Studies, Mandalay](https://www.ucsm.edu.mm). Author is a computer science student who's studying in there majoring in Knowledge Engineering. But you can change some code it work with yours. Hope this can help you in some way, as the reference.

## Project Setup
- I used firebase for realtime database.
- You'll need to add the dependencies according to the [Firebase documentation](https://firebase.google.com/docs/database/android/start?authuser=0)
- Note that you need to create the project in Firebase in order to access the realtime database from there. 
- Then link your firebse project to this source code in [Android Studio](https://developer.android.com/studio)
- After it finishes building the project, you're good to go.

## Technologies Used
1. Kotlin 
2. Google Firebase for data storing and realtime database
3. JSON

## Software Specification 
- Android 4.4 Kit Kat minimum

# System Design 
### Student app flowchart
Student needs to register with the corresponding subjects he/she take and his information. If he/she has already registered, he can enter the account by using student number and password. After login, he can view the attendance percentage subject by subject and he can also view which day he attended by date. He can get the attendance by scanning the QR code generated from the class teacher. He can upload the medical leaves to the dean. He can also edit all of his information.
![alt text](https://github.com/hanlinag/ams-student/blob/master/images/studflowchart.png?raw=true)

### Sequence Diagram
The processes of the system describe that the teacher generates the QR code to take the attendance and the students scan this QR code to get attendance. The teacher checks the attendance data and sends to the dean by submitting the data. The student can view the attendance record and upload the medical leaves. Then the dean accepts the medical leaves and the attendance percentage will be updated. The dean manages the subjects and generates records. 
![alt text](https://github.com/hanlinag/ams-student/blob/master/images/sequence.png?raw=true)

### Usecase Diagram
Student can update his/her information and password, view his/her attendance percentage, upload medical leave letters and scan QR code to get attendance. The teachers can generate QR code for the students to take the attendance and then check the attendance information and submit the attendance record to dean. The dean can notify underrated attendance students, accept medical leave letters and update corresponding student attendance, manage all information about the subjects, students and teachers. The dean can also generate student attendance record with CSV file.
![alt text](https://github.com/hanlinag/ams-student/blob/master/images/usecase.png?raw=true)

### Database Design
Overall Design
![alt text](https://github.com/hanlinag/ams-student/blob/master/images/overalldb.png?raw=true)

Subject Table
![alt text](https://github.com/hanlinag/ams-student/blob/master/images/subject.png?raw=true)

Student Table
![alt text](https://github.com/hanlinag/ams-student/blob/master/images/student.png?raw=true)

Pre-Student Table (who haven't registered yet.)
![alt text](https://github.com/hanlinag/ams-student/blob/master/images/pre-students.png?raw=true)

Pending Attendance Table (Wait for the subject teacher to confirm the attendance.)
![alt text](https://github.com/hanlinag/ams-student/blob/master/images/pending%20attendance.png?raw=true)

Medical Leave Table
![alt text](https://github.com/hanlinag/ams-student/blob/master/images/medical%20leave.png?raw=true)

Class Table
![alt text](https://github.com/hanlinag/ams-student/blob/master/images/class.png?raw=true)

Attendance Record Table
![alt text](https://github.com/hanlinag/ams-student/blob/master/images/addtendance.png?raw=true)

# Encryption Algorithm
This system is used [AES](https://en.wikipedia.org/wiki/Advanced_Encryption_Standard) (Advanced Encryption Standard) algorithms. The features of AES are as follows −
- Symmetric key symmetric block cipher
- 128-bit data, 128/192/256-bit keys
- Stronger and faster than Triple-DES
- Provide full specification and design details
- Software implementable in C and Java


We encrypt user passwords, QR code data, etc.

# Screenshots
![alt text](https://github.com/hanlinag/ams-student/blob/master/images/ss1.png?raw=true)

![alt text](https://github.com/hanlinag/ams-student/blob/master/images/ss2.png?raw=true)

![alt text](https://github.com/hanlinag/ams-student/blob/master/images/ss3.png?raw=true)

![alt text](https://github.com/hanlinag/ams-student/blob/master/images/ss4.png?raw=true)

![alt text](https://github.com/hanlinag/ams-student/blob/master/images/ss5.png?raw=true)

![alt text](https://github.com/hanlinag/ams-student/blob/master/images/ss6.png?raw=true)

![alt text](https://github.com/hanlinag/ams-student/blob/master/images/ss7.png?raw=true)

# Conclusion 
This attendance management system is developed for the dean, the teacher and the student. By using this system, the dean can reduce his/her daily or monthly complex jobs. He/She can easily check the underrated attendance students and then inform them. It is not necessary to manually calculate for monthly attendance. He/She does not need to do any complex task for medical leaves. For the teacher, the teacher does not need to get the attendance record paper from the student affairs and return it. It takes less times than manual system, so the teacher can teach her lesson longer.  For the student, the student can view their attendance transparently and he/she notices that if his/her attendance is underrated. He/she can also view the attended dates on the specific subject.

# License
[MIT License](LICENSE)