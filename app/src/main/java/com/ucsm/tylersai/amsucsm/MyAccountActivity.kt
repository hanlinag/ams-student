package com.ucsm.tylersai.amsucsm

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.ucsm.tylersai.amsucsm.models.Student
import de.hdodenhof.circleimageview.CircleImageView

class MyAccountActivity : AppCompatActivity() {
    lateinit var tvmkpt: TextView
    lateinit var tvphone: TextView
    lateinit var tvemail: TextView
    lateinit var tvclass: TextView
    lateinit var tvsubject: TextView
    lateinit var tvaddress: TextView
    lateinit var imgProfile: CircleImageView

    lateinit var sharedPreference: SharedPreferences
    lateinit var studentTable: DatabaseReference

    var student: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account)

        var dialog = ProgressDialog(this)
        dialog.setMessage("Please wait...")
        dialog.show()
        studentTable = FirebaseDatabase.getInstance().reference.child("ams").child("student")

        sharedPreference = getSharedPreferences(getString(R.string.myPref), Context.MODE_PRIVATE)
        var mkptFromPref = sharedPreference.getString("mkpt", null)


        studentTable.child(mkptFromPref).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                student = dataSnapshot.getValue(Student::class.java)


                tvmkpt = findViewById(R.id.tv_mkpt_myacc)
                tvphone = findViewById(R.id.tv_phone_myacc)
                tvemail = findViewById(R.id.tv_email_myacc)
                tvclass = findViewById(R.id.tv_major_myacc)
                tvsubject = findViewById(R.id.tv_subject_myacc)
                tvaddress = findViewById(R.id.tv_address_myacc)
                imgProfile = findViewById(R.id.profile_img_myacc)


                tvmkpt.text = "mkpt-${mkptFromPref}"
                tvphone.text = student?.phone
                tvemail.text = student?.email
                tvclass.text = student?.major
                tvsubject.text = student?.subject
                tvaddress.text = student?.address

                Glide.with(this@MyAccountActivity).load(student?.profileurl).into(imgProfile)

                setSupportActionBar(findViewById(R.id.customized_toolbar))

                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.setDisplayShowHomeEnabled(true)

                supportActionBar!!.title = student?.name
                dialog.dismiss()
            }

        })


    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.action_my_acc_edit -> {
                //Snackbar.make(findViewById(R.id.my_acc_root_view), "Hello world", Snackbar.LENGTH_LONG).show()
                val intent = Intent(this, UpdateMyInfoActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_acc_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


}
