package com.ucsm.tylersai.amsucsm

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.ucsm.tylersai.amsucsm.models.MedicalLeave
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MedicalLeaveFragment : Fragment() {
    private var tvFileName: TextView? = null
    private var imgPending: ImageView? = null
    private var btDatepicker1: Button? = null
    private var btDatepicker2: Button? = null
    private var btDatepicker3: Button? = null
    private var btDatepicker4: Button? = null
    private var btDatepicker5: Button? = null
    private var checkBoxTerms: CheckBox? = null


    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    lateinit var sharedPreference: SharedPreferences

    lateinit var mStorageReference: StorageReference
    lateinit var studentTable: DatabaseReference

    var askingDate: String? = ""
    var invalidDate: String? = ""


    private val DATE_FORMAT = "dd-MM-yyyy"


    var filePath: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var view = inflater.inflate(R.layout.fragment_medical_leave, container, false)

        mStorageReference = FirebaseStorage.getInstance().reference
        studentTable = FirebaseDatabase.getInstance().reference.child("ams").child("student")


        tvFileName = view?.findViewById<TextView>(R.id.tv_file_name_medical)
        imgPending = view?.findViewById<ImageView>(R.id.img_temp)

        var btUpload = view?.findViewById<TextView>(R.id.bt_upload_medical)
        var btSubmit = view?.findViewById<Button>(R.id.medical_bt_submit)

        checkBoxTerms = view?.findViewById(R.id.term_checkbox)

        btDatepicker1 = view?.findViewById(R.id.bt_date_picker1)
        btDatepicker2 = view?.findViewById(R.id.bt_date_picker2)
        btDatepicker3 = view?.findViewById(R.id.bt_date_picker3)
        btDatepicker4 = view?.findViewById(R.id.bt_date_picker4)
        btDatepicker5 = view?.findViewById(R.id.bt_date_picker5)


        btUpload?.setOnClickListener {
            // Toast.makeText(view?.context, "Hello world", Toast.LENGTH_LONG).show()
            // Log.d("MEDICAL", "UPLOAD working")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }



        btSubmit?.setOnClickListener {

            askingDate = ""
            invalidDate = ""

            var date1 = btDatepicker1!!.text.toString()
            var date2 = btDatepicker2!!.text.toString()
            var date3 = btDatepicker3!!.text.toString()
            var date4 = btDatepicker4!!.text.toString()
            var date5 = btDatepicker5!!.text.toString()

            var information: String? = ""

            if (checkBoxTerms!!.isChecked) {

                if (date1.equals("Date") && date2.equals("Date") && date3.equals("Date") && date4.equals("Date") && date5.equals(
                        "Date"
                    )
                ) {

                    AlertDialog.Builder(context)
                        .setTitle("Error")
                        .setMessage("No date is selected")
                        .setNeutralButton("Okay", DialogInterface.OnClickListener { dialogInterface, i -> })
                        .show()
                } else {
                    /*if (!isEligible(date1)) {
                        information = "${date1} is not eligible\n"
                    } else if (!isEligible(date2)) {
                        information = "$information${date2} is not eligible\n"
                    } else if (!isEligible(date3)) {
                        information = "$information${date3} is not eligible\n"
                    } else if (!isEligible(date4)) {
                        information = "$information${date4} is not eligible\n"
                    } else if (!isEligible(date5)) {
                        information = "$information${date5} is not eligible\n"
                    } else {
                        information = "All the dates are eligible to ask for medical leave"
                    }*/


                    isEligible(date1)
                    isEligible(date2)
                    isEligible(date3)
                    isEligible(date4)
                    isEligible(date5)

                    if (invalidDate.equals("")) {
                        information = "All the dates are eligible to ask for medical leave. Are you sure to submit?"
                    } else {
                        information = "Are you sure to submit?"
                    }

                    AlertDialog.Builder(context)
                        .setTitle("Information")
                        .setMessage(
                            "Valid Dates: $askingDate\nInvalid Dates: $invalidDate\n$information"

                        )
                        .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        })
                        .setPositiveButton("Submit", DialogInterface.OnClickListener { dialogInterface, i ->

                            if (filePath.equals("")) {
                                dialogInterface.dismiss()
                                Toast.makeText(context, "No image is selected!", Toast.LENGTH_LONG).show()
                            } else if (askingDate.equals("")) {
                                dialogInterface.dismiss()
                                Toast.makeText(context, "No valid date...", Toast.LENGTH_LONG).show()
                            } else uploadImageToFirebaseStorage(filePath)

                        })
                        .show()
                }//end of else of check all the date are unselected

            } else {
                AlertDialog.Builder(context)
                    .setTitle("Missing")
                    .setMessage("Please accept terms and conditions")
                    .setNeutralButton("Okay", DialogInterface.OnClickListener { dialogInterface, i -> })
                    .show()

            }//end of else of checkbox term


        }


        btDatepicker1?.setOnClickListener {

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd =
                DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    var dayy = dayOfMonth.toString()
                    var monthh = (monthOfYear + 1).toString()
                    if (dayOfMonth < 10) {
                        dayy = "0$dayOfMonth"
                    }
                    if (monthOfYear + 1 < 10) {
                        monthh = "0${(monthOfYear + 1)}"
                    }
                    // Display Selected date in textbox
                    btDatepicker1?.text = "$dayy-$monthh-$year"
                }, year, month, day)

            dpd.show()
        }

        btDatepicker2?.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd =
                DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    var dayy = dayOfMonth.toString()
                    var monthh = (monthOfYear + 1).toString()

                    if (dayOfMonth < 10) {
                        dayy = "0$dayOfMonth"
                    }
                    if (monthOfYear + 1 < 10) {
                        monthh = "0${(monthOfYear + 1)}"
                    }
                    // Display Selected date in textbox
                    btDatepicker2?.text = "$dayy-${monthh}-$year"
                }, year, month, day)

            dpd.show()
        }

        btDatepicker3?.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd =
                DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    var dayy = dayOfMonth.toString()
                    var monthh = (monthOfYear + 1).toString()

                    if (dayOfMonth < 10) {
                        dayy = "0$dayOfMonth"
                    }
                    if (monthOfYear + 1 < 10) {
                        monthh = "0${(monthOfYear + 1)}"
                    }
                    // Display Selected date in textbox
                    btDatepicker3?.text = "$dayy-${monthh}-$year"
                }, year, month, day)

            dpd.show()
        }
        btDatepicker4?.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd =
                DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    var dayy = dayOfMonth.toString()
                    var monthh = (monthOfYear + 1).toString()

                    if (dayOfMonth < 10) {
                        dayy = "0$dayOfMonth"
                    }
                    if (monthOfYear + 1 < 10) {
                        monthh = "0${(monthOfYear + 1)}"
                    }
                    // Display Selected date in textbox
                    btDatepicker4?.text = "$dayy-${monthh}-$year"
                }, year, month, day)

            dpd.show()
        }
        btDatepicker5?.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd =
                DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    var dayy = dayOfMonth.toString()
                    var monthh = (monthOfYear + 1).toString()

                    if (dayOfMonth < 10) {
                        dayy = "0$dayOfMonth"
                    }
                    if (monthOfYear + 1 < 10) {
                        monthh = "0${(monthOfYear + 1)}"
                    }
                    // Display Selected date in textbox
                    btDatepicker5?.text = "$dayy-${monthh}-$year"
                }, year, month, day)

            dpd.show()
        }

        // Inflate the layout for this fragment
        return view


    }

    private fun isEligible(date: String?) {
        var eligible = true


        // var dateForWeekOfDays = dateFormat.parse(date)

        if (date.equals("Date")) {
            eligible = true
        } else {


            var dateFormat = SimpleDateFormat(DATE_FORMAT)
            var c = Calendar.getInstance().time
            var today = dateFormat.format(c)

            var testDate = dateFormat.parse(date)
            var todayDate = dateFormat.parse(today)

            //var notusedatesample = dateFormat.parse("01/07/2019")
            Log.d("Tyler", "Today date is ::::::: $today")

            var difference = ((todayDate.time - testDate.time) / (1000 * 60 * 60 * 24))
            //  var daysDistance = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
            var daysDistance = difference


            Log.d("Tyler", "Day distince is : $daysDistance")
            if (daysDistance > 14 || daysDistance < 0) {
                eligible = false
            }


            var formatofDate = SimpleDateFormat(DATE_FORMAT)
            var dateForWeekDay = formatofDate.parse(date)
            var calendarObj = Calendar.getInstance()
            calendarObj.time = dateForWeekDay



            Log.d(
                "Tyler",
                "Selected Date is: ${calendarObj.get(Calendar.DAY_OF_WEEK)} and Sunday annd Saturday are ${Calendar.SUNDAY} and ${Calendar.SATURDAY} "
            )


            if (calendarObj.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || calendarObj.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                eligible = false
            }

            if (eligible == true) {
                askingDate = "$askingDate,$date,"
            } else {
                invalidDate = "$invalidDate,$date"
            }
            Log.d("Tyler", "Asking date loop: $askingDate")
            Log.d("Tyler", "Invalid date loop: $invalidDate")

        }//end of else of blank date


        //return eligible
    }


    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    /*override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }*/

    override fun onDetach() {
        super.onDetach()
        listener = null

    }


    interface OnFragmentInteractionListener {

        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MedicalLeaveFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                var selectedImage: Uri = data!!.data


                filePath = getRealPathFromURI(selectedImage)

                Log.d("Tyler", "Filepath is $filePath")
                var fileExtension = filePath?.substring(filePath!!.lastIndexOf(".") + 1)


                Log.d("Tyler", "Filepath extension: $fileExtension")
                tvFileName?.text = "$filePath is pending to upload. "

                try {
                    if (fileExtension.equals("img") || fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals(
                            "png"
                        )
                    ) {

                        imgPending!!.setImageURI(selectedImage)

                    }//end of if
                    else {

                        AlertDialog.Builder(view?.context)
                            .setTitle("Error")
                            .setMessage("File not supported!")
                            .show()
                    }
                } catch (ex: Exception) {
                    Log.d("Tyler", "Exception: ${ex.message.toString()}")
                }
            }
        }
    }

    fun getRealPathFromURI(uri: Uri): String {
        val cursor = context?.contentResolver!!.query(uri, null, null, null, null)
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        Log.d("Filepath", "Filepath: ${cursor.getString(idx)}")
        return cursor.getString(idx)
    }


    private fun uploadImageToFirebaseStorage(url: String?) {

        sharedPreference = this.activity!!.getSharedPreferences(getString(R.string.myPref), Context.MODE_PRIVATE)


        val c = Calendar.getInstance()
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        var day = c.get(Calendar.DAY_OF_MONTH)
        var hour = c.get(Calendar.HOUR_OF_DAY)
        var min = c.get(Calendar.MINUTE)
        var second = c.get(Calendar.SECOND)

        var realDay: String? = ""
        var realMonth: String? = ""
        var realHour: String? = ""
        var realMin: String? = ""
        var realSecond: String? = ""

        if (day < 10) {
            realDay = "0$day"
        } else {

            realDay = day.toString()
        }
        if (month < 10) {
            realMonth = "0$month"
        } else {
            realMonth = month.toString()
        }

        if (hour < 10) {
            realHour = "0$hour"
        } else {
            realHour = hour.toString()
        }

        if (min < 10) {
            realMin = "0$min"
        } else {
            realMin = min.toString()
        }

        if (second < 10) {
            realSecond = "0$second"
        } else {
            realSecond = second.toString()
        }

        var today = "$realDay$realMonth$year:$realHour$realMin$realSecond"

        Log.d("Tyler", "Today date for firebase: $today")
        var dialog = ProgressDialog(context)
        dialog.setMessage("Uploading...")
        dialog.show()
        var downloadUri: String = ""

        var file = Uri.fromFile(File(url))
        var profileLocationRef =
            mStorageReference.child("medicalleave/mkpt_${sharedPreference.getString("mkpt", null)}_$today")


        //var uploadTask = mountainsRef.putBytes(data)
        //  val ref = my.child("images/mountains.jpg")
        var uploadTask = profileLocationRef.putFile(file)

        val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation profileLocationRef?.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
                // Log.d("Tyler", "download link is:::: $downloadUri")

                /*var prefEditor = sharedPreference.edit()
                    prefEditor.putString("profileurl", downloadUri)
                    prefEditor.commit()*/

                //  Log.d("Tyler", "New profile url is: ${sharedPreference.getString("medicalleave", null)}")

                var medicalLeaveID = "mkpt_${sharedPreference.getString("mkpt", null)}_$today"

                var medicalLeave = MedicalLeave(
                    askingDate!!,
                    "${sharedPreference.getString("mkpt", null)}",
                    "$downloadUri",
                    "pending",
                    medicalLeaveID
                )


                var medicalLeaveTable = FirebaseDatabase.getInstance().reference.child("ams").child("medicalleave")
                medicalLeaveTable.child(medicalLeaveID).setValue(medicalLeave)


                dialog.dismiss()
                Toast.makeText(context, "Your medical leave letter is uploaded!", Toast.LENGTH_LONG).show()
            } else {
                // Handle failures
                // ...
            }
        }.addOnFailureListener {
            dialog.dismiss()
            Toast.makeText(context, "Upload Fail!", Toast.LENGTH_LONG).show()
        }

        //return downloadUri

    }

}
