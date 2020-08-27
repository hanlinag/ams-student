package com.ucsm.tylersai.amsucsm

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.ucsm.tylersai.amsucsm.models.Student
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class ChangePasswordActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var preferencesEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        supportActionBar!!.title = "Change Password"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        sharedPreferences = getSharedPreferences(getString(R.string.myPref), Context.MODE_PRIVATE)
        preferencesEditor = sharedPreferences.edit()

        var edtPassword = findViewById<EditText>(R.id.changepw_edt_pw)
        var edtConfirm = findViewById<EditText>(R.id.changepw_edt_confirm)

        val btReset = findViewById<Button>(R.id.change_pw_bt_create)

        btReset.setOnClickListener {
            var password = edtPassword.text.toString()
            var confirmPassword = edtConfirm.text.toString()

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Password field is empty")
                    .setPositiveButton(
                        "Ok",
                        DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    .show()
            } else {
                if (password != confirmPassword) {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Password does not match")
                        .setPositiveButton(
                            "Ok",
                            DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                        .show()
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Confirm")
                        .setMessage("Are you sure to change your password?")
                        .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        })
                        .setPositiveButton("Change", DialogInterface.OnClickListener { dialogInterface, i ->

                            dialogInterface.dismiss()
                            var progressDialog = ProgressDialog(this)
                            progressDialog.setMessage("Loading...")
                            progressDialog.show()

                            //encrypt password
                            var encryptedPassword = confirmPassword.encrypt("amsucsmapp123456")
                            //teacher table
                            var teacherTable = FirebaseDatabase.getInstance().reference.child("ams").child("teacher")

                            var student = Student(
                                sharedPreferences.getString(getString(R.string.prefName), null),
                                sharedPreferences.getString(getString(R.string.prefMajor), null),
                                sharedPreferences.getString(getString(R.string.prefSubject), null),
                                sharedPreferences.getString(getString(R.string.prefPhone), null),
                                sharedPreferences.getString(getString(R.string.prefEmail), null),
                                sharedPreferences.getString(getString(R.string.prefAddress), null),
                                encryptedPassword,
                                sharedPreferences.getString(getString(R.string.prefProfileurl), null),
                                sharedPreferences.getString(getString(R.string.prefMkpt), null)
                            )
                            preferencesEditor.putString(getString(R.string.prefPassword), confirmPassword)
                            preferencesEditor.commit()

                            teacherTable.child(sharedPreferences.getString(getString(R.string.prefMkpt), null))
                                .setValue(student)

                            progressDialog.dismiss()
                            edtConfirm.text.clear()
                            edtPassword.text.clear()
                            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_LONG).show()


                        }).show()
                }
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
}
