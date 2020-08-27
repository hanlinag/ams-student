package com.ucsm.tylersai.amsucsm

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ucsm.tylersai.amsucsm.models.PreStudentList

class SignupActivity : AppCompatActivity() {

    private var sharedPreferences: SharedPreferences? = null
    private var preferenceEditor: SharedPreferences.Editor? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        setSupportActionBar(findViewById(R.id.signup_customized_toolbar))

        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        sharedPreferences = getSharedPreferences(getString(R.string.mySignUpPref), Context.MODE_PRIVATE)
        preferenceEditor = sharedPreferences!!.edit()
        preferenceEditor?.clear()

        val edtmkpt = findViewById<EditText>(R.id.signup_edt_mkpt)
        val edtname = findViewById<EditText>(R.id.signup_edt_name)
        val edtphone = findViewById<EditText>(R.id.signup_edt_phone)
        val edtemail = findViewById<EditText>(R.id.signup_edt_email)
        val edtaddr = findViewById<EditText>(R.id.signup_edt_addr)

        val btCreate = findViewById<Button>(R.id.signup_bt_signup)

        btCreate.setOnClickListener {


            var mkpt = edtmkpt.text.toString()
            val name = edtname.text.toString()
            val phone = edtphone.text.toString()
            val email = edtemail.text.toString()
            val address = edtaddr.text.toString()

            if (mkpt.isEmpty() || name.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty()) {

                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Fill all the forms")
                    .setPositiveButton(
                        "Got it",
                        DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    .show()
            } else if (!mkpt.contains("mkpt-") || mkpt.length < 5) {

                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Wrong Format!\nMust be in mkpt-0000 format.")
                    .setPositiveButton(
                        "Got it",
                        DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    .show()

            } else {

                mkpt = mkpt.substring(5)

                //get mkpt and name check for
                var prestudentlistTable = FirebaseDatabase.getInstance().reference.child("ams").child("prestudentlist")
                prestudentlistTable.child(mkpt).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Toast.makeText(this@SignupActivity, "Error occur" + p0.toException().message, Toast.LENGTH_LONG)
                            .show()
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            //data exist
                            //check name is the same or not
                            var preStudentList = dataSnapshot.getValue(PreStudentList::class.java)
                            Log.d("Tyler", "enter mkpt is ")
                            if (preStudentList!!.name == name) {

                                //do the job


                                preferenceEditor?.putString(getString(R.string.prefName), name)
                                preferenceEditor?.putString(getString(R.string.prefMkpt), mkpt)
                                preferenceEditor?.putString(getString(R.string.prefPhone), phone)
                                preferenceEditor?.putString(getString(R.string.prefEmail), email)
                                preferenceEditor?.putString(getString(R.string.prefAddress), address)
                                preferenceEditor?.putString(getString(R.string.prefMajor), preStudentList.major)
                                preferenceEditor?.commit()

                                val intent = Intent(this@SignupActivity, ChooseSubjectActivity::class.java)
                                startActivity(intent)
                            } else {
                                //name does not match
                                AlertDialog.Builder(this@SignupActivity)
                                    .setTitle("Error")
                                    .setMessage("Name does not match. It should be ${preStudentList.name}. Try again")
                                    .setPositiveButton(
                                        "Ok",
                                        DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                                    .show()
                            }
                        } else {
                            //data not exist mkpt
                            AlertDialog.Builder(this@SignupActivity)
                                .setTitle("Error")
                                .setMessage("You are not in the eligible list to register. Contact your admin")
                                .setPositiveButton(
                                    "Ok",
                                    DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                                .show()
                        }
                    }

                })


            }

        }//end of button action

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
