package com.ucsm.tylersai.amsucsm

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity

class ChooseSubjectActivity : AppCompatActivity() {

    var sharedPreferences: SharedPreferences? = null
    var preferenceEditor: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_subject)

        setSupportActionBar(findViewById(R.id.choosesubject_customized_toolbar))

        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        sharedPreferences = getSharedPreferences(getString(R.string.mySignUpPref), Context.MODE_PRIVATE)
        preferenceEditor = sharedPreferences!!.edit()

        var cbJ: CheckBox = findViewById(R.id.japanesec)
        var cbE: CheckBox = findViewById(R.id.englishc)
        var cbAa: CheckBox = findViewById(R.id.aac)
        var cbSecurity: CheckBox = findViewById(R.id.securityc)
        var cbPl: CheckBox = findViewById(R.id.plc)
        var cbProject: CheckBox = findViewById(R.id.projectc)
        var cbAndroid: CheckBox = findViewById(R.id.androidc)
        var cbKEMajor: CheckBox = findViewById(R.id.kemajorc)
        var cbSEMajor: CheckBox = findViewById(R.id.semajorc)
        var cbHPCMajor: CheckBox = findViewById(R.id.hpcmajorc)
        var cbBISMajor: CheckBox = findViewById(R.id.bismajorc)

        var selectedMajor = sharedPreferences!!.getString(getString(R.string.prefMajor), null)

        if (selectedMajor.equals(getString(R.string.KEMajor))) {
            cbSEMajor.isEnabled = false
            cbHPCMajor.isEnabled = false
            cbBISMajor.isEnabled = false

        } else if (selectedMajor.equals(getString(R.string.SEMajor))) {
            cbKEMajor.isEnabled = false
            cbHPCMajor.isEnabled = false
            cbBISMajor.isEnabled = false
        } else if (selectedMajor.equals(getString(R.string.HPCMajor))) {
            cbKEMajor.isEnabled = false
            cbSEMajor.isEnabled = false
            cbBISMajor.isEnabled = false
        } else {
            cbKEMajor.isEnabled = false
            cbSEMajor.isEnabled = false
            cbHPCMajor.isEnabled = false
        }


        cbJ.setOnClickListener {
            if (cbE.isChecked) {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("You can't select 2 language class!")
                    .setPositiveButton("Ok", DialogInterface.OnClickListener { dialogInterface, i ->
                        cbJ.toggle()
                        dialogInterface.dismiss()
                    })
                    .show()
            }
        }

        cbE.setOnClickListener {
            if (cbJ.isChecked) {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("You can't select 2 language class!")
                    .setPositiveButton(
                        "Ok",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            cbE.toggle()
                            dialogInterface.dismiss()
                        })
                    .show()
            }

        }

        cbAndroid.setOnClickListener {
            if (cbProject.isChecked) {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("You can select either Project or Android class. You can't choose both!")
                    .setPositiveButton("Ok", DialogInterface.OnClickListener { dialogInterface, i ->
                        cbAndroid.toggle()
                        dialogInterface.dismiss()
                    })
                    .show()
            }
        }

        cbProject.setOnClickListener {
            if (cbAndroid.isChecked) {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("You can select either Project or Android class. You can't choose both!")
                    .setPositiveButton("Ok", DialogInterface.OnClickListener { dialogInterface, i ->
                        cbProject.toggle()
                        dialogInterface.dismiss()
                    })
                    .show()
            }
        }

        val btContinue = findViewById<Button>(R.id.choosemsub_bt_continue)
        btContinue.setOnClickListener {
            var selectedSubject = ""

            if (cbE.isChecked) {
                selectedSubject = "$selectedSubject, ${cbE.text},"
            }
            if (cbJ.isChecked) {
                selectedSubject = "$selectedSubject, ${cbJ.text},"
            }
            if (cbAa.isChecked) {

                selectedSubject = "$selectedSubject, ${cbAa.text},"
            }
            if (cbSecurity.isChecked) {

                selectedSubject = "$selectedSubject, ${cbSecurity.text},"
            }
            if (cbPl.isChecked) {

                selectedSubject = "$selectedSubject, ${cbPl.text},"
            }
            if (cbProject.isChecked) {

                selectedSubject = "$selectedSubject, ${cbProject.text},"
            }
            if (cbAndroid.isChecked) {

                selectedSubject = "$selectedSubject, ${cbAndroid.text},"
            }
            if (cbKEMajor.isChecked) {

                selectedSubject = "$selectedSubject, ${cbKEMajor.text},"
            }
            if (cbSEMajor.isChecked) {

                selectedSubject = "$selectedSubject, ${cbSEMajor.text},"
            }
            if (cbHPCMajor.isChecked) {

                selectedSubject = "$selectedSubject, ${cbHPCMajor.text},"
            }
            if (cbBISMajor.isChecked) {

                selectedSubject = "$selectedSubject, ${cbBISMajor.text},"
            }

            preferenceEditor!!.putString(getString(R.string.prefSubject), selectedSubject)
            preferenceEditor!!.commit()

            val intent = Intent(this, CreatePasswordActivity::class.java)
            startActivity(intent)
        }
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
