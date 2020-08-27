package com.ucsm.tylersai.amsucsm

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.ucsm.tylersai.amsucsm.models.Student
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class LoginActivity : AppCompatActivity() {
    lateinit var database: FirebaseDatabase
    lateinit var studenttable: DatabaseReference

    lateinit var sharedPreferences: SharedPreferences
    var student: Student? = null
    lateinit var prefEditor: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setSupportActionBar(findViewById(R.id.login_customized_toolbar))

        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        database = FirebaseDatabase.getInstance()
        studenttable = database.reference

        sharedPreferences = getSharedPreferences(getString(R.string.myPref), Context.MODE_PRIVATE)
        prefEditor = sharedPreferences.edit()


        val edtMkpt = findViewById<EditText>(R.id.login_edt_mkpt)
        val edtPw = findViewById<EditText>(R.id.login_edt_password)

        val btLogin = findViewById<Button>(R.id.login_bt_login)

        val tvForgetPw = findViewById<TextView>(R.id.login_forget_pw_tv)

        btLogin.setOnClickListener {


            // var password = edtPw.text.toString()
            if (edtMkpt.text.toString().equals("") || edtPw.text.toString().equals("")) {
                Toast.makeText(applicationContext, "Enter mkpt and password", Toast.LENGTH_LONG).show()
            } else if (edtMkpt.text.length < 5 || !edtMkpt.text.contains("mkpt")) {
                Toast.makeText(applicationContext, "Enter the correct mkpt-0000", Toast.LENGTH_LONG).show()
            } else {

                var mkptRaw = edtMkpt.text.toString()
                var mkpt = mkptRaw.substring(5)

                Log.d("Tyler", "child................${mkpt}")

                var progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Please wait...")
                progressDialog.max = 100
                progressDialog.show()

                studenttable.child("ams").child("student").child(mkpt)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            if (dataSnapshot.exists()) {

                                student = dataSnapshot.getValue(Student::class.java)
                                student?.mkpt = mkpt


                                var decryptPassword = student!!.password.toString().decrypt("amsucsmapp123456")

                                Log.d("Tyler", "name is: ${student!!.name} and password is: ${student!!.password} ")
                                if (edtPw.text.toString().equals(decryptPassword)) {


                                    //Log.d("Tyler", "${student.name}, ${student.mkpt},${student.subject} ,${student.phone}, ${student.email}, ${student.address}, ${student.profileurl}")

                                    //saving current user account for other activity

                                    // var prefEditor = sharedPreferences.edit()
                                    //prefEditor.clear()
                                    prefEditor.putString("name", student?.name)
                                    prefEditor.putString("mkpt", student?.mkpt)
                                    prefEditor.putString("major", student?.major)
                                    prefEditor.putString("subject", student?.subject)
                                    prefEditor.putString("phone", student?.phone)
                                    prefEditor.putString("email", student?.email)
                                    prefEditor.putString("address", student?.address)
                                    prefEditor.putString("profileurl", student?.profileurl)
                                    prefEditor.putString("password", student?.password)
                                    // prefEditor.clear()
                                    // prefEditor.commit()
                                    // prefEditor.apply()

                                    val intent = Intent(applicationContext, MainActivity::class.java)
                                    progressDialog.dismiss()
                                    startActivity(intent)
                                    finishAffinity()
                                } else {

                                    progressDialog.dismiss()
                                    AlertDialog.Builder(this@LoginActivity)
                                        .setTitle("Error")
                                        .setMessage("Incorrect password!")
                                        .setPositiveButton(
                                            "Got it",
                                            DialogInterface.OnClickListener { dialogInterface, i ->

                                                dialogInterface.dismiss()

                                            })
                                        .show()
                                }//end of else in checking mkpt and pw

                            } else {

                                progressDialog.dismiss()
                                AlertDialog.Builder(this@LoginActivity)
                                    .setTitle("Error")
                                    .setMessage("User not exit!")
                                    .setPositiveButton("Okay", DialogInterface.OnClickListener { dialogInterface, i ->

                                        dialogInterface.dismiss()

                                    })
                                    .show()
                            }//end of else in checking user exit
                            prefEditor.clear()
                            prefEditor.commit()
                        }
                    })

            }//end of method click listener

            tvForgetPw.setOnClickListener {
                val intent = Intent(this, ForgetPasswordActivity::class.java)
                startActivity(intent)

            }

            val tvJoin = findViewById<TextView>(R.id.join_tv)
            tvJoin.setOnClickListener {
                val intent = Intent(this, SignupActivity::class.java)
                startActivity(intent)
                finish()
            }

        }//end of if password
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun String.encrypt(password: String): String {
        val secretKeySpec = SecretKeySpec(password.toByteArray(), "AES")
        val iv = ByteArray(16)
        val charArray = password.toCharArray()
        for (i in 0 until charArray.size) {
            iv[i] = charArray[i].toByte()
        }
        val ivParameterSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)

        val encryptedValue = cipher.doFinal(this.toByteArray())
        return Base64.encodeToString(encryptedValue, Base64.DEFAULT)
    }

    fun String.decrypt(password: String): String {
        val secretKeySpec = SecretKeySpec(password.toByteArray(), "AES")
        val iv = ByteArray(16)
        val charArray = password.toCharArray()
        for (i in 0 until charArray.size) {
            iv[i] = charArray[i].toByte()
        }
        val ivParameterSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)

        val decryptedByteValue = cipher.doFinal(Base64.decode(this, Base64.DEFAULT))
        return String(decryptedByteValue)
    }
}
