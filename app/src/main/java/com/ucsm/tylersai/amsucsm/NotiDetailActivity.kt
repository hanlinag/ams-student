package com.ucsm.tylersai.amsucsm

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NotiDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noti_detail)

        val title = intent.getStringExtra("title")
        val body = intent.getStringExtra("body")
        val date = intent.getStringExtra("date")

        supportActionBar!!.title = "$title "
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val tvDate = findViewById<TextView?>(R.id.notidetail_date_tv)
        val tvMessage = findViewById<TextView?>(R.id.notidetail_body_tv)

        tvDate?.text = "Date: $date"
        tvMessage?.text = body
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
