package com.ucsm.tylersai.amsucsm

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.ucsm.tylersai.amsucsm.adapters.SubjectDetailListAdapter
import com.ucsm.tylersai.amsucsm.models.Attendance
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SubjectDetailActivity : AppCompatActivity() {

    var tvname: TextView? = null
    var tvpercentSemester: TextView? = null
    var tvmonthlypercent: TextView? = null
    val SEMESTERMONTH = "05,06,07,08"
    private val DATEFORMATT = "dd-MM-yyyy"
    val HOLIDAYS: String = "16-07-2019,19-07-2019"

    var attendancePercentage = 0.0

    var may = "0"
    var june = "0"
    var july = "0"
    var august = "0"


    lateinit var attendanceTable: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_detail)

        setSupportActionBar(findViewById(R.id.subject_detail_customized_toolbar))

        var progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.show()


        var code = intent.getStringExtra("code")
        var name = intent.getStringExtra("name")
        var percent = intent.getStringExtra("percent")
        var mkpt = intent.getStringExtra("mkpt")
        var subDay = intent.getStringExtra("subdays")
        var subTime = intent.getStringExtra("subtimes")

        tvname = findViewById<TextView>(R.id.detail_subject_name)
        tvpercentSemester = findViewById(R.id.detail_subject_percent)
        tvmonthlypercent = findViewById(R.id.detail_subject_monthly_percent)



        supportActionBar!!.title = "$code"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        var listView = findViewById<ListView>(R.id.subject_detail_daily_list)

        var dataset = ArrayList<Attendance>()


        //getting all the data of the corresponding data from firebase database
        attendanceTable = FirebaseDatabase.getInstance().reference.child("ams").child("attendance")

        //loop for the specific month
        //get month of this sem
        var semMonthsAry = SEMESTERMONTH.split(",")
        for (i in 0 until semMonthsAry.size) {

            attendanceTable.child(semMonthsAry[i]).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(applicationContext, p0.toException().toString(), Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //day in db
                    var dailyDate = ""
                    var mkptfromDB = ""
                    var subjectCodefromDB = ""

                    //specific subject code
                    dataSnapshot.children.forEach {

                        var dailyDate = it.key

                        //get subject
                        it.children.forEach {
                            var subjectCodefromDB = it.key
                            // Log.d("Tyler", "detail subject: dailydate is $dailyDate subject code is $subjectCodefromDB")
                            //check if it's the same subject code
                            if (subjectCodefromDB == code) {
                                it.children.forEach {
                                    mkptfromDB = it.key.toString()
                                    if (mkpt == mkptfromDB) {
                                        var attendance = it.getValue(Attendance::class.java)
                                        var day = attendance!!.date.subSequence(0, 2)
                                        var month = attendance.date.substring(2, 4)
                                        var year = attendance.date.substring(4)
                                        attendance.date = "$day-$month-$year"
                                        dataset.add(attendance)

                                        listView.adapter = SubjectDetailListAdapter(applicationContext, dataset)
                                        progressDialog.dismiss()
                                    }//end of if mkpt

                                }//end of it.children.for each

                            }//end of if
                        }//end of it.children for each

                    }//end of datasnapshot chilren for each
                }

            })
        }//end of for month

//---------------------------------------------------------------------------------------------------------second
        //calculate the total time period to attend at this semester
        var semMonthAry = SEMESTERMONTH.split(",")
        var startMonth = semMonthAry[0]

        var endMonth = semMonthAry[semMonthAry.lastIndex]

        var totalPeriodHourofSubjectinMonth = 0
        var totalPeroidHourofAttend = 0


        //start loop of 4 months here
        for (b in 0 until semMonthAry.size) {//whole month loop
            //Log.d("Tyler","start and end month : $startMonth, $endMonth")
            var numDaysInMonth = 30

            var dateFormat = SimpleDateFormat(DATEFORMATT)
            var c = Calendar.getInstance().time
            var year = Calendar.getInstance().get(Calendar.YEAR)
            var today = dateFormat.format(c)

            //var rawSplit = today.split("-")
            //get the current month of loop and year
            var currentMonth = semMonthAry[b]
            var currentYear = year.toString()

            when (currentMonth.toInt()) {
                1 -> {
                    numDaysInMonth = 31
                }
                2 -> {

                    //cleck leap yar
                    if (isLeapYear(currentYear.toInt())) {
                        numDaysInMonth = 29
                    } else {
                        numDaysInMonth = 28
                    }

                }
                3 -> {

                    numDaysInMonth = 31
                }
                4 -> {

                    numDaysInMonth = 30
                }
                5 -> {

                    numDaysInMonth = 31
                }
                6 -> {

                    numDaysInMonth = 30
                }
                7 -> {

                    numDaysInMonth = 31
                }
                8 -> {

                    numDaysInMonth = 31
                }
                9 -> {

                    numDaysInMonth = 30
                }
                10 -> {

                    numDaysInMonth = 31
                }
                11 -> {

                    numDaysInMonth = 30
                }
                12 -> {

                    numDaysInMonth = 31
                }
            }

            var startDateI = 1

            if (currentMonth.equals(startMonth)) {
                startDateI = 15
                //Log.d("Tyler","Starting from 15 May & num of day in month is $numDaysInMonth ")
            } else if (currentMonth.equals(endMonth)) {
                numDaysInMonth = 13
                // Log.d("Tyler","Starting from 1 but only 13 day")
            }


            // Log.d("Tyler", "Sem ary:: ${semMonthAry[b]}, number of days in month ${numDaysInMonth}, start date is : $startDateI")
            //get the total time period of this subject
            //get the subject data from firebase and get day

            //get current subject's day
            var dayOfSubRaw = subDay
            var dayOfSubAry = dayOfSubRaw.split(",")


            //get the date of day of week of the current subject
            var eligibleSubDateToCheck = java.util.ArrayList<String>()
            var eligibleSubDateToCheckFinal = java.util.ArrayList<String>()

            for (i in startDateI until numDaysInMonth + 1) {//loop the whole month
                var eligible = false


                //check weather the current day is equal week of day of subject. And finally add date into array
                var dateToCheck = "$i-$currentMonth-$currentYear"
                Log.d("Tyler", "i is $i and date $dateToCheck")

                for (j in 0 until dayOfSubAry.size) {//loop of subject teaching day
                    var subTeachingDayToCheck = dayOfSubAry[j]
                    //check if the current date of day of week is equal to subject day
                    var formatofDate = SimpleDateFormat(DATEFORMATT)
                    var dateForWeekDay = formatofDate.parse(dateToCheck)
                    var calendarObj = Calendar.getInstance()
                    calendarObj.time = dateForWeekDay

                    var checkWeekofDay = ""
                    when (calendarObj.get(Calendar.DAY_OF_WEEK)) {

                        Calendar.MONDAY -> {
                            checkWeekofDay = "Monday"
                        }
                        Calendar.TUESDAY -> {
                            checkWeekofDay = "Tuesday"
                        }
                        Calendar.WEDNESDAY -> {
                            checkWeekofDay = "Wednesday"
                        }
                        Calendar.THURSDAY -> {
                            checkWeekofDay = "Thursday"
                        }
                        Calendar.FRIDAY -> {
                            checkWeekofDay = "Friday"
                        }
                        Calendar.SATURDAY -> {
                            checkWeekofDay = "Saturday"
                        }
                        Calendar.SUNDAY -> {
                            checkWeekofDay = "Sunday"
                        }
                    }

                    if (checkWeekofDay == subTeachingDayToCheck) {

                        eligible = true

                        break
                        //Log.d("Tyler", "Eligibleeeee date is $dateToCheck and 16/19 ")
                    } else {
                        eligible = false
                        //Log.d("Tyler", "Check date is $dateToCheck and it's week of day is $checkWeekofDay and equal to holiday")
                    }

                }//end of subject teaching day for

                //add eligible to array
                if (eligible) {
                    eligibleSubDateToCheck.add(dateToCheck)
                } else {
                    Log.d("Tyler", "Detail Non eligible date is : $dateToCheck")
                }
            }//end of for

            //holiday
            var holidayAry = HOLIDAYS.split(",")

            //loop eligible date ary to re-filter holiday
            for (h in 0 until eligibleSubDateToCheck.size) {
                var e = false

                var firstEligibleDatetoCheck = eligibleSubDateToCheck[h]

                for (f in 0 until holidayAry.size) {
                    if (firstEligibleDatetoCheck == holidayAry[f]) {

                        e = false
                        break
                    } else {
                        e = true
                    }

                }//end of holiday loop
                if (e) {
                    eligibleSubDateToCheckFinal.add(firstEligibleDatetoCheck)
                } else {

                }

            }//end of first version of eligible date check
            //check holiday

            //get eligible date and check in attendance table in db whether it exist or not
            for (a in 0 until eligibleSubDateToCheckFinal.size) {
                //Log.d("Tyler", "Check date result::::: number of eligible date is: ${eligibleSubDateToCheck.size} and ${eligibleSubDateToCheck[a]} for subject ${items.get(position).name}" )
                var dateCheckAttendannce = eligibleSubDateToCheckFinal[a]


                //get the time period of subject of that day
                var timePeriod = subTime
                var dayofSubject = subDay

                var timeRaw = timePeriod.split(",")
                var dayRaw = dayofSubject.split(",")
                var formatofDate = SimpleDateFormat(DATEFORMATT)
                var c = Calendar.getInstance()

                var dateForWeekDay = formatofDate.parse(dateCheckAttendannce)
                c.time = dateForWeekDay

                var dayOfWeekofDate = ""
                when (c.get(Calendar.DAY_OF_WEEK)) {

                    Calendar.MONDAY -> {
                        dayOfWeekofDate = "Monday"
                    }
                    Calendar.TUESDAY -> {
                        dayOfWeekofDate = "Tuesday"
                    }
                    Calendar.WEDNESDAY -> {
                        dayOfWeekofDate = "Wednesday"
                    }
                    Calendar.THURSDAY -> {
                        dayOfWeekofDate = "Thursday"
                    }
                    Calendar.FRIDAY -> {
                        dayOfWeekofDate = "Friday"
                    }
                    Calendar.SATURDAY -> {
                        dayOfWeekofDate = "Saturday"
                    }
                    Calendar.SUNDAY -> {
                        dayOfWeekofDate = "Sunday"
                    }
                }

                var timeRangeofSubject = ""
                //check current loop date dayofweek
                for (g in 0 until dayRaw.size) {
                    if (dayRaw[g].equals(dayOfWeekofDate)) {
                        //get the time period of corresponding day of week
                        timeRangeofSubject = timeRaw[g]
                    }
                }//end of for
                var timeRangeofSubjectRaw = timeRangeofSubject.split("-")
                var startTime = timeRangeofSubjectRaw[0]
                var endTime = timeRangeofSubjectRaw[1]
                var periodHour = endTime.toInt() - startTime.toInt()

                totalPeriodHourofSubjectinMonth += periodHour

                //search the current date in db
                var attendanceTable = FirebaseDatabase.getInstance().reference.child("ams").child("attendance")
                var monthofCheckDateRaw = dateCheckAttendannce.split("-")
                var monthofCheckDate = monthofCheckDateRaw[1]


                var dateR = dateCheckAttendannce.split("-")
                dateCheckAttendannce = "${dateR[0]}${dateR[1]}${dateR[2]}"
                if (dateR[0].toInt() < 10) {
                    dateCheckAttendannce = "0${dateR[0]}${dateR[1]}${dateR[2]}"
                }

                attendanceTable.child(monthofCheckDate).child(dateCheckAttendannce).child(code).child(mkpt)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Toast.makeText(
                                this@SubjectDetailActivity,
                                "Error occur" + p0.toException().toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            //Log.d("Tyler", "just checking for ${dataSnapshot.getValue(Attendance::class.java)!!.mkpt}")
                            if (dataSnapshot.exists()) {
                                //get period hour if attend
                                totalPeroidHourofAttend += periodHour

                                attendancePercentage =
                                    ((totalPeroidHourofAttend.toDouble() / totalPeriodHourofSubjectinMonth.toDouble()) * 100)

                                var decimalFormat = DecimalFormat("#.")
                                attendancePercentage = decimalFormat.format(attendancePercentage).toDouble()

                                Log.d(
                                    "Tyler",
                                    "Period Count in total: $totalPeriodHourofSubjectinMonth attend count $totalPeroidHourofAttend percentage: ${attendancePercentage} subject>> ${code}  the whole sem "
                                )

                                //holder?.tvSubjectPercent?.text = "$attendancePercentage% of 100% this month and still counting"


                                //update at UI title
                                tvname?.text = name
                                tvpercentSemester?.text = "$attendancePercentage% of 100% of this semester"
                            } else {
                                Log.d("Tyler", "No attendance for date $dateCheckAttendannce with subject code ${code}")
                            }


                        }//end of onDataChange
                    })
            }//end of for loop for eligible subject date
        }//whole month of this sem loop ending...


        //-------------------------------------------------------------------------------------------third
        //get percentage of each month in the semester
        //getting the percentage of this month of this subject
        //get the number of days in this month
        //get current day and month
        //start loop of 4 months here
        for (b in 0 until semMonthAry.size) {//whole month loop
            // val calendar = Calendar.getInstance()
            var dateFormat = SimpleDateFormat(DATEFORMATT)
            var c = Calendar.getInstance().time
            var today = dateFormat.format(c)
            var rawSplit = today.split("-")
            var currentMonth = rawSplit[1]
            var currentYear = rawSplit[2]
            //Log.d("Tyler","Current day is $today and current month is $currentMonth and year $currentYear")

            var semMonthAryy = SEMESTERMONTH.split(",")
            var startMonth = semMonthAryy[0]

            var endMonth = semMonthAryy[semMonthAryy.lastIndex]

            //Log.d("Tyler","start and end month : $startMonth, $endMonth")
            var numDaysInMonth = 30

            when (semMonthAry[b].toInt()) {
                1 -> {
                    numDaysInMonth = 31
                }
                2 -> {

                    //cleck leap yar
                    if (isLeapYear(currentYear.toInt())) {
                        numDaysInMonth = 29
                    } else {
                        numDaysInMonth = 28
                    }

                }
                3 -> {

                    numDaysInMonth = 31
                }
                4 -> {

                    numDaysInMonth = 30
                }
                5 -> {

                    numDaysInMonth = 31
                }
                6 -> {

                    numDaysInMonth = 30
                }
                7 -> {

                    numDaysInMonth = 31
                }
                8 -> {

                    numDaysInMonth = 31
                }
                9 -> {

                    numDaysInMonth = 30
                }
                10 -> {

                    numDaysInMonth = 31
                }
                11 -> {

                    numDaysInMonth = 30
                }
                12 -> {

                    numDaysInMonth = 31
                }
            }

            var startDateI = 1

            if (semMonthAry[b].toString().equals(startMonth)) {
                startDateI = 15
                //Log.d("Tyler","Starting from 15 May & num of day in month is $numDaysInMonth ")
            } else if (semMonthAry[b].toString().equals(endMonth)) {
                numDaysInMonth = 13
                // Log.d("Tyler","Starting from 1 but only 13 day")
            }

            // Log.d("Tyler", "Current month's day in the month is $numDaysInMonth")

            //get current subject's day
            var dayOfSubRaw = subDay
            var dayOfSubAry = dayOfSubRaw.split(",")


            //get the date of day of week of the current subject
            var eligibleSubDateToCheck = java.util.ArrayList<String>()
            var eligibleSubDateToCheckFinal = java.util.ArrayList<String>()

            for (i in startDateI until numDaysInMonth + 1) {//loop the whole month
                var eligible = false

                //check weather the current day is equal week of day of subject. And finally add date into array
                var dateToCheck = "$i-${semMonthAry[b]}-$currentYear"
                Log.d("Tyler", "i is $i and date $dateToCheck")

                for (j in 0 until dayOfSubAry.size) {//loop of subject teaching day
                    var subTeachingDayToCheck = dayOfSubAry[j]
                    //check if the current date of day of week is equal to subject day
                    var formatofDate = SimpleDateFormat(DATEFORMATT)
                    var dateForWeekDay = formatofDate.parse(dateToCheck)
                    var calendarObj = Calendar.getInstance()
                    calendarObj.time = dateForWeekDay

                    var checkWeekofDay = ""
                    when (calendarObj.get(Calendar.DAY_OF_WEEK)) {

                        Calendar.MONDAY -> {
                            checkWeekofDay = "Monday"
                        }
                        Calendar.TUESDAY -> {
                            checkWeekofDay = "Tuesday"
                        }
                        Calendar.WEDNESDAY -> {
                            checkWeekofDay = "Wednesday"
                        }
                        Calendar.THURSDAY -> {
                            checkWeekofDay = "Thursday"
                        }
                        Calendar.FRIDAY -> {
                            checkWeekofDay = "Friday"
                        }
                        Calendar.SATURDAY -> {
                            checkWeekofDay = "Saturday"
                        }
                        Calendar.SUNDAY -> {
                            checkWeekofDay = "Sunday"
                        }
                    }

                    if (checkWeekofDay == subTeachingDayToCheck) {

                        eligible = true

                        break
                        //Log.d("Tyler", "Eligibleeeee date is $dateToCheck and 16/19 ")
                    } else {
                        eligible = false
                        //Log.d("Tyler", "Check date is $dateToCheck and it's week of day is $checkWeekofDay and equal to holiday")
                    }

                }//end of subject teaching day for

                //add eligible to array
                if (eligible) {
                    eligibleSubDateToCheck.add(dateToCheck)
                } else {
                    Log.d("Tyler", "Non eligible date is : $dateToCheck")
                }
            }//end of for

            //holiday
            var holidayAry = HOLIDAYS.split(",")

            //loop eligible date ary to re-filter holiday
            for (h in 0 until eligibleSubDateToCheck.size) {
                var e = false

                var firstEligibleDatetoCheck = eligibleSubDateToCheck[h]

                for (f in 0 until holidayAry.size) {
                    if (firstEligibleDatetoCheck == holidayAry[f]) {

                        e = false
                        break
                    } else {
                        e = true
                    }

                }//end of holiday loop
                if (e) {
                    eligibleSubDateToCheckFinal.add(firstEligibleDatetoCheck)
                } else {

                }

            }//end of first version of eligible date check
            //check holiday


            //get eligible date and check in attendance table in db whether it exist or not
            var totalPeriodHourofSubjectinMonth = 0
            var totalPeroidHourofAttend = 0


            for (a in 0 until eligibleSubDateToCheckFinal.size) {
                //Log.d("Tyler", "Check date result::::: number of eligible date is: ${eligibleSubDateToCheck.size} and ${eligibleSubDateToCheck[a]} for subject ${items.get(position).name}" )
                var dateCheckAttendannce = eligibleSubDateToCheckFinal[a]


                //get the time period of subject of that day
                var timePeriod = subTime
                var dayofSubject = subDay

                var timeRaw = timePeriod.split(",")
                var dayRaw = dayofSubject.split(",")
                var formatofDate = SimpleDateFormat(DATEFORMATT)
                var c = Calendar.getInstance()

                var dateForWeekDay = formatofDate.parse(dateCheckAttendannce)
                c.time = dateForWeekDay

                var dayOfWeekofDate = ""
                when (c.get(Calendar.DAY_OF_WEEK)) {

                    Calendar.MONDAY -> {
                        dayOfWeekofDate = "Monday"
                    }
                    Calendar.TUESDAY -> {
                        dayOfWeekofDate = "Tuesday"
                    }
                    Calendar.WEDNESDAY -> {
                        dayOfWeekofDate = "Wednesday"
                    }
                    Calendar.THURSDAY -> {
                        dayOfWeekofDate = "Thursday"
                    }
                    Calendar.FRIDAY -> {
                        dayOfWeekofDate = "Friday"
                    }
                    Calendar.SATURDAY -> {
                        dayOfWeekofDate = "Saturday"
                    }
                    Calendar.SUNDAY -> {
                        dayOfWeekofDate = "Sunday"
                    }
                }

                var timeRangeofSubject = ""
                //check current loop date dayofweek
                for (g in 0 until dayRaw.size) {
                    if (dayRaw[g].equals(dayOfWeekofDate)) {
                        //get the time period of corresponding day of week
                        timeRangeofSubject = timeRaw[g]
                    }
                }//end of for
                var timeRangeofSubjectRaw = timeRangeofSubject.split("-")
                var startTime = timeRangeofSubjectRaw[0]
                var endTime = timeRangeofSubjectRaw[1]
                var periodHour = endTime.toInt() - startTime.toInt()

                totalPeriodHourofSubjectinMonth += periodHour

                //search the current date in db
                var attendanceTable = FirebaseDatabase.getInstance().reference.child("ams").child("attendance")
                var monthofCheckDateRaw = dateCheckAttendannce.split("-")
                var monthofCheckDate = monthofCheckDateRaw[1]


                var dateR = dateCheckAttendannce.split("-")
                dateCheckAttendannce = "${dateR[0]}${dateR[1]}${dateR[2]}"
                if (dateR[0].toInt() < 10) {
                    dateCheckAttendannce = "0${dateR[0]}${dateR[1]}${dateR[2]}"
                }

                attendanceTable.child(monthofCheckDate).child(dateCheckAttendannce)
                    .child(code).child(mkpt)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Toast.makeText(
                                this@SubjectDetailActivity,
                                "Error occur" + p0.toException().toString(),
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            //Log.d("Tyler", "just checking for ${dataSnapshot.getValue(Attendance::class.java)!!.mkpt}")
                            if (dataSnapshot.exists()) {
                                //get period hour if attend
                                totalPeroidHourofAttend += periodHour

                                attendancePercentage =
                                    ((totalPeroidHourofAttend.toDouble() / totalPeriodHourofSubjectinMonth.toDouble()) * 100)

                                var decimalFormat = DecimalFormat("#.")
                                attendancePercentage = decimalFormat.format(attendancePercentage).toDouble()


                                if (semMonthAryy[b].equals("05")) {
                                    may = attendancePercentage.toString()
                                } else if (semMonthAryy[b].equals("06")) {
                                    june = attendancePercentage.toString()
                                } else if (semMonthAryy[b].equals("07")) {
                                    july = attendancePercentage.toString()
                                } else {
                                    august = attendancePercentage.toString()
                                }
                                tvmonthlypercent?.text =
                                    "$may% of 100% for May \n$june% of 100% for June\n$july% of 100% for July\n$august% of 100% for August"
                                Log.d(
                                    "Tyler",
                                    "Period Count in total: $totalPeriodHourofSubjectinMonth attend count $totalPeroidHourofAttend percentage: ${attendancePercentage} subject>> ${code} , month: ${semMonthAryy[b]} "
                                )

                            } else {
                                Log.d(
                                    "Tyler",
                                    "No attendance for date $dateCheckAttendannce with subject code ${code}"
                                )
                            }
                        }//end of onDataChange

                    })
            }//end of for loop for eligible subject date

        }//end of loop for 4 months

    }

    fun isLeapYear(year: Int): Boolean {
        return if (year % 4 != 0) {
            false
        } else if (year % 400 == 0) {
            true
        } else year % 100 != 0
    }//end of leap year

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
