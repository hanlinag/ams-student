package com.ucsm.tylersai.amsucsm

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.goodiebag.pinview.Pinview

class PinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        val pinView = findViewById<Pinview>(R.id.pin_view)

        pinView.setPinViewEventListener { pinview, fromUser ->

            if (pinview.value.toString().equals("1111")) {
                val intent = Intent(this, NewPasswordActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(applicationContext, "Incorrect Pin!", Toast.LENGTH_LONG).show()
                pinView.clearValue()
            }
        }


    }


}
