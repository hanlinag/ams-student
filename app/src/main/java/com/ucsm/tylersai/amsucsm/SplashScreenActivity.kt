package com.ucsm.tylersai.amsucsm

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity


class SplashScreenActivity : AppCompatActivity() {
    val SPLASH_LENGTH: Long = 1000
    lateinit var myDelayHandler: Handler

    lateinit var sharedPreferences: SharedPreferences

    internal val mRunnable: Runnable = Runnable {

        if (!isFinishing) {

            sharedPreferences = getSharedPreferences(getString(R.string.myPref), Context.MODE_PRIVATE)
            //sharedEditor = sharedPreferences.edit()
            var mkpt = sharedPreferences.getString("mkpt", null)

            if (mkpt != null) {
                val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {

                val intent = Intent(this@SplashScreenActivity, StartActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        myDelayHandler = Handler()
        myDelayHandler.postDelayed(mRunnable, SPLASH_LENGTH)


    }

    override fun onDestroy() {
        if (myDelayHandler != null) {

            myDelayHandler.removeCallbacks(mRunnable)
        }
        super.onDestroy()

    }
}
