package com.ucsm.tylersai.amsucsm

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class StartActivity : AppCompatActivity() {
    private var sharedPreferences: SharedPreferences? = null
    private var preferenceEditor: SharedPreferences.Editor? = null

    //lateinit var sharedEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val btLogin = findViewById<Button>(R.id.bt__choose_login)
        val btSignUp = findViewById<Button>(R.id.bt_choose_signup)

        sharedPreferences = getSharedPreferences(getString(R.string.mySignUpPref), Context.MODE_PRIVATE)
        preferenceEditor = sharedPreferences!!.edit()
        preferenceEditor!!.clear()
        preferenceEditor!!.commit()


        btLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)

        }
    }
}
