package com.ucsm.tylersai.amsucsm.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ucsm.tylersai.amsucsm.SubjectDetailActivity
import com.ucsm.tylersai.amsucsm.models.Subject
import kotlinx.android.synthetic.main.subject_progress_recycler_row.view.*
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SubjectAdapter(val items: ArrayList<Subject>, val context: Context, val mkpt: String) :
    RecyclerView.Adapter<ViewHolder>() {

    //private val DATE_FORMAT = "dd-MM-yyyy"
    private val DATEFORMATT = "dd-MM-yyyy"

    val HOLIDAYS: String = "16-07-2019,19-07-2019"
    val SEMESTERMONTH = "05,06,07,08"

    var attendancePercentage = 0.0

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        var view = ViewHolder(
            LayoutInflater.from(context).inflate(
                com.ucsm.tylersai.amsucsm.R.layout.subject_progress_recycler_row,
                parent,
                false
            )
        )

        view.itemView.setOnClickListener {
            Toast.makeText(context, "${view.tvSubjectName.text}", Toast.LENGTH_LONG).show()

            var intent = Intent(context, SubjectDetailActivity::class.java)
            intent.putExtra("code", view.tvSubjectCode.text)
            intent.putExtra("name", view.tvSubjectName.text)
            intent.putExtra("percent", view.tvSubjectPercent.text)
            intent.putExtra("mkpt", mkpt)
            intent.putExtra("subdays", view.subDay)
            intent.putExtra("subtimes", view.subTime)
            startActivity(context, intent, null)
        }

        return view
    }

    // Binds each animal in the ArrayList to a view,
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //getting the percentage of this month of this subject
        //get the number of days in this month
        //get current day and month
        // val calendar = Calendar.getInstance()
        var dateFormat = SimpleDateFormat(DATEFORMATT)
        var c = Calendar.getInstance().time
        var today = dateFormat.format(c)
        var rawSplit = today.split("-")
        var currentMonth = rawSplit[1]
        var currentYear = rawSplit[2]
        //Log.d("Tyler","Current day is $today and current month is $currentMonth and year $currentYear")

        var semMonthAry = SEMESTERMONTH.split(",")
        var startMonth = semMonthAry[0]

        var endMonth = semMonthAry[semMonthAry.lastIndex]

        //Log.d("Tyler","start and end month : $startMonth, $endMonth")
        var numDaysInMonth = 30

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

        // Log.d("Tyler", "Current month's day in the month is $numDaysInMonth")

        //get current subject's day
        var dayOfSubRaw = items.get(position).day
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
        var totalPeriod = 0
        var totalAttend = 0


        for (a in 0 until eligibleSubDateToCheckFinal.size) {
            //Log.d("Tyler", "Check date result::::: number of eligible date is: ${eligibleSubDateToCheck.size} and ${eligibleSubDateToCheck[a]} for subject ${items.get(position).name}" )
            var dateCheckAttendannce = eligibleSubDateToCheckFinal[a]


            //get the time period of subject of that day
            var timePeriod = items.get(position).time
            var dayofSubject = items.get(position).day

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

            totalPeriod += periodHour

            //search the current date in db
            var attendanceTable = FirebaseDatabase.getInstance().reference.child("ams").child("attendance")
            var monthofCheckDateRaw = dateCheckAttendannce.split("-")
            var monthofCheckDate = monthofCheckDateRaw[1]


            var dateR = dateCheckAttendannce.split("-")
            dateCheckAttendannce = "${dateR[0]}${dateR[1]}${dateR[2]}"
            if (dateR[0].toInt() < 10) {
                dateCheckAttendannce = "0${dateR[0]}${dateR[1]}${dateR[2]}"
            }

            attendanceTable.child(monthofCheckDate).child(dateCheckAttendannce).child(items.get(position).subjectCode)
                .child(mkpt).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(context, "Error occur" + p0.toException().toString(), Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    //Log.d("Tyler", "just checking for ${dataSnapshot.getValue(Attendance::class.java)!!.mkpt}")
                    if (dataSnapshot.exists()) {
                        //get period hour if attend
                        totalAttend += periodHour

                        attendancePercentage = ((totalAttend.toDouble() / totalPeriod.toDouble()) * 100)

                        var decimalFormat = DecimalFormat("#.")
                        attendancePercentage = decimalFormat.format(attendancePercentage).toDouble()

                        //Log.d("Tyler","Period Count in total: $totalPeriodHourofSubjectinMonth attend count $totalPeroidHourofAttend percentage: ${attendancePercentage} subject>> ${items.get(position).subjectCode}  ")


                        holder.tvSubjectPercent?.text = "$attendancePercentage% of 100% this month"

                        //calculating pie chart
                        var attendancePercentage = attendancePercentage.toFloat()
                        val pieData = ArrayList<SliceValue>()
                        pieData.add(SliceValue(attendancePercentage, Color.argb(100, 222, 74, 91)))
                        pieData.add(SliceValue((100F - attendancePercentage), Color.argb(100, 96, 153, 155)))
                        val pieChartData = PieChartData(pieData)
                        pieChartData.setHasCenterCircle(true).centerText1FontSize = 10
                        pieChartData.setHasCenterCircle(true).centerText1 = "$attendancePercentage%"
                        holder.subjectChart?.pieChartData = pieChartData


                    } else {
                        Log.d(
                            "Tyler",
                            "No attendance for date $dateCheckAttendannce with subject code ${items.get(position).subjectCode}"
                        )
                    }
                }//end of onDataChange
            })
        }//end of for loop for eligible subject date


        //finally
        //update to ui
        holder.tvSubjectCode?.text = items.get(position).subjectCode
        holder.tvSubjectName?.text = items.get(position).name
        holder.subDay = items.get(position).day
        holder.subTime = items.get(position).time

        //Log.d("Tyler","percentage for ${items.get(position).subjectCode} is $attendancePercentage")


    }//end of function Bind holder

    fun isLeapYear(year: Int): Boolean {
        return if (year % 4 != 0) {
            false
        } else if (year % 400 == 0) {
            true
        } else year % 100 != 0
    }//end of leap year

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvSubjectCode = view.subject_code_tv
    val tvSubjectName = view.subject_title_detail_tv
    val tvSubjectPercent = view.subject_percent
    val subjectChart = view.subject_pie_chart
    var subDay = ""
    var subTime = ""
}