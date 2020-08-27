package com.ucsm.tylersai.amsucsm

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AlertDialog
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ucsm.tylersai.amsucsm.models.Student
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class CreatePasswordActivity : AppCompatActivity() {
    private var sharedPreferences: SharedPreferences? = null
    private var preferenceEditor: SharedPreferences.Editor? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_password)

        setSupportActionBar(findViewById(R.id.createpw_customized_toolbar))
        supportActionBar!!.setTitle("")
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        sharedPreferences = getSharedPreferences(getString(R.string.mySignUpPref), Context.MODE_PRIVATE)
        preferenceEditor = sharedPreferences!!.edit()

        var edtPassword: EditText = findViewById(R.id.createpw_edt_pw)
        var edtConfirmPassword: EditText = findViewById(R.id.createpw_edt_confirm)

        val btCreate = findViewById<Button>(R.id.createpw_bt_create)
        btCreate.setOnClickListener {
            var password = edtPassword.text.toString()
            var confirmPassword = edtConfirmPassword.text.toString()
            if(password.equals(confirmPassword)) {

                AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to create account? ")
                    .setPositiveButton("Sign Up", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                        //new dialog

                        var studentTable = FirebaseDatabase.getInstance().reference.child("ams").child("student")
                        studentTable.child(sharedPreferences!!.getString(getString(R.string.prefMkpt), null)).addListenerForSingleValueEvent(object :ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()){
                                   AlertDialog.Builder(this@CreatePasswordActivity)
                                        .setTitle("Error")
                                        .setMessage("User with this mkpt is already exist. Contact admin or check your mkpt and try again!")
                                        .setPositiveButton("Okay", DialogInterface.OnClickListener { dialogInterface, i ->dialogInterface.dismiss()
                                        })
                                        .show()
                                        //Toast.makeText(applicationContext, "User with this mkpt is already exist. Contact admin or check your mkpt and try again!",Toast.LENGTH_LONG).show()
                                }else{
                                    var encryptPassword = confirmPassword.encrypt("amsucsmapp123456")
                                    Log.d("Tyler","Encrypted password is $encryptPassword and decrypt is ${encryptPassword.decrypt("amsucsmapp123456")}")
                                    //do the upload new user task
                                    preferenceEditor!!.putString(getString(R.string.prefPassword), encryptPassword)
                                    var defaultProfileUrl = "https://firebasestorage.googleapis.com/v0/b/attendance-system-245615.appspot.com/o/dprofile.png?alt=media&token=5af77504-a9d6-4294-968e-e7d457247d0d"
                                    preferenceEditor!!.putString(getString(R.string.prefProfileurl), defaultProfileUrl)
                                    preferenceEditor!!.commit()



                                    var studentObj = Student(
                                        sharedPreferences!!.getString(getString(R.string.prefName), null),
                                        sharedPreferences!!.getString(getString(R.string.prefMajor), null),
                                        sharedPreferences!!.getString(getString(R.string.prefSubject), null),
                                        sharedPreferences!!.getString(getString(R.string.prefPhone), null),
                                        sharedPreferences!!.getString(getString(R.string.prefEmail), null),
                                        sharedPreferences!!.getString(getString(R.string.prefAddress), null),
                                        sharedPreferences!!.getString(getString(R.string.prefPassword), null),
                                        sharedPreferences!!.getString(getString(R.string.prefProfileurl), null),
                                        sharedPreferences!!.getString(getString(R.string.prefMkpt), null)
                                    )
                                    studentTable.child(sharedPreferences!!.getString(getString(R.string.prefMkpt), null)).setValue(studentObj)
                                    Toast.makeText(applicationContext, "Account Created!",Toast.LENGTH_LONG).show()

                                    AlertDialog.Builder(this@CreatePasswordActivity)
                                        .setTitle("Success")
                                        .setMessage("Congratulations! Your account is created.")
                                        .setPositiveButton("Okay", DialogInterface.OnClickListener { dialogInterface, i ->
                                            dialogInterface.dismiss()
                                            val intent = Intent(applicationContext, StartActivity::class.java)
                                            startActivity(intent)
                                            finishAffinity()
                                        })
                                        .show()
                                }//end of mkpt exit check
                            }

                        }) //end of data snapshot get data from firebase



                        //progressDialog.dismiss()
                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    .show()
            }else{
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Password doesn't match! ")
                    .setPositiveButton("Okay", DialogInterface.OnClickListener { dialogInterface, i ->dialogInterface.dismiss()
                    })
                    .show()

            }//end of else
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home ->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun String.encrypt(password: String): String {
        val secretKeySpec = SecretKeySpec(password.toByteArray(), "AES")
        val iv = ByteArray(16)
        val charArray = password.toCharArray()
        for (i in 0 until charArray.size){
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
        for (i in 0 until charArray.size){
            iv[i] = charArray[i].toByte()
        }
        val ivParameterSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)

        val decryptedByteValue = cipher.doFinal(Base64.decode(this, Base64.DEFAULT))
        return String(decryptedByteValue)
    }
}
