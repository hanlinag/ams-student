package com.ucsm.tylersai.amsucsm

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ucsm.tylersai.amsucsm.adapters.NotiListViewAdapter
import com.ucsm.tylersai.amsucsm.models.Notifications

class NotificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        supportActionBar!!.title = "Notifications"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        var sharedPreferences = getSharedPreferences(getString(R.string.myPref), Context.MODE_PRIVATE)
        var mkpt = sharedPreferences.getString(getString(R.string.prefMkpt), null)
        var listData = ArrayList<Notifications>()

        var listView = findViewById<ListView?>(R.id.notification_list_view)

        // var notiAry = ArrayList<Notifications>()
        //get data from db
        var notiTable = FirebaseDatabase.getInstance().reference.child("ams").child("notifications")
        notiTable.orderByChild("targetUser").equalTo(mkpt).addValueEventListener(object : ValueEventListener {


            override fun onCancelled(p0: DatabaseError) {
                //Toast.makeText()
            }

            //var biggestNoti: Notifications? = Notifications("", "", "0", "", "$mkpt")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                listData.clear()
                dataSnapshot.children.forEach {
                    var noti = it.getValue(Notifications::class.java)
                    listData.add(noti!!)

                    listView!!.adapter = NotiListViewAdapter(this@NotificationActivity, listData)
                }
            }
        })

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
