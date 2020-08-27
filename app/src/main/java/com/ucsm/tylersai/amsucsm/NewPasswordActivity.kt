package com.ucsm.tylersai.amsucsm

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class NewPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

        setSupportActionBar(findViewById(R.id.createnewpw_customized_toolbar))

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""

        val edtPassword = findViewById<EditText>(R.id.newpw_edt_pw)
        val edtConfirmPassword = findViewById<EditText>(R.id.newpw_edt_confirm)

        val btCreate = findViewById<Button>(R.id.newpw_bt_create)
        btCreate.setOnClickListener {
            if (edtPassword.text.toString().equals(edtConfirmPassword.text.toString())) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
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
