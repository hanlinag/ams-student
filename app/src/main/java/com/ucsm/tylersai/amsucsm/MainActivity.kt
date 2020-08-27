package com.ucsm.tylersai.amsucsm

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ucsm.tylersai.amsucsm.models.Notifications

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    lateinit var tvName: TextView
    lateinit var tvMkpt: TextView
    lateinit var imgProfile: ImageView

    lateinit var sharedPreferences: SharedPreferences
    lateinit var preferenceEditor: SharedPreferences.Editor

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    private val LOCATION_REQUEST_CODE = 101
    private val CAMERA_REQUEST_CODE = 100
    private val READ_REQUEST_CODE = 102
    private val WRITE_REQUEST_CODE = 103


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.title = "Attendance System"

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            // .setAction("Action", null).show()
            val intent = Intent(this, QRScanActivity::class.java)
            startActivity(intent)
        }


        sharedPreferences = getSharedPreferences(getString(R.string.myPref), Context.MODE_PRIVATE)
        // var prefEditor = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
        displaySelectedFragment(DashboardFragment())
        navView.setCheckedItem(R.id.nav_dashboard)

        val navHeaderMain = navView.getHeaderView(0)

        tvName = navHeaderMain.findViewById(R.id.tv_name_nav)
        tvMkpt = navHeaderMain.findViewById(R.id.tv_mkpt_nav)
        imgProfile = navHeaderMain.findViewById(R.id.img_profile_nav)


        var mkptFromPref = sharedPreferences.getString("mkpt", null)
        var nameFromPref = sharedPreferences.getString("name", null)
        var profileFromPref = sharedPreferences.getString("profileurl", null)

        if (mkptFromPref != null) {
            tvMkpt.text = "mkpt-$mkptFromPref"
            tvName.text = nameFromPref

            Glide.with(this@MainActivity).load(profileFromPref).into(imgProfile)

        } else {
            Toast.makeText(this, "mkpt $mkptFromPref", Toast.LENGTH_LONG).show()
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fatchLastLocation()

        //listen on notifications
        var notiTable = FirebaseDatabase.getInstance().reference.child("ams").child("notifications")
        notiTable.orderByChild("targetUser").equalTo(mkptFromPref).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //Toast.makeText()
            }

            var biggestNoti: Notifications? = Notifications("", "", "0", "", "$mkptFromPref")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    var noti = it.getValue(Notifications::class.java)
                    if (noti!!.date >= biggestNoti!!.date) {
                        biggestNoti = noti
                    }
                }
                //make notification
                // Create an explicit intent for an Activity in your app
                val intent = Intent(applicationContext, NotificationActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

                val builder = NotificationCompat.Builder(applicationContext, "11")
                    .setSmallIcon(R.drawable.logotran)
                    .setContentTitle(biggestNoti!!.title)
                    .setContentText(biggestNoti!!.body)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

                var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(11, builder.build())


            }

        })

    }

    private fun fatchLastLocation() {

        //location permission
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )

            return
        }
        //camera permission
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE
            )

            return
        }
        //storage r/w permission
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_REQUEST_CODE
            )

            return
        }
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_REQUEST_CODE
            )

            return
        }


        var task = fusedLocationProviderClient!!.lastLocation
        task.addOnSuccessListener {
            if (it != null) {

                preferenceEditor = sharedPreferences.edit()
                preferenceEditor.putString(getString(R.string.prefLat), "${it.latitude}")
                preferenceEditor.putString(getString(R.string.prefLon), "${it.longitude}")
                preferenceEditor.commit()
                Log.d("Tyler", "current location is : ${it.longitude} ${it.latitude}")
            }//end of if
        }
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        when (item.itemId) {
            R.id.action_noti -> {
                // Snackbar.make(findViewById(R.id.content_main), "Action noti menu pressed!", Snackbar.LENGTH_LONG).show()
                val intent = Intent(this, NotificationActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_about -> {
                Snackbar.make(findViewById(R.id.content_main), "Action about menu pressed!", Snackbar.LENGTH_LONG)
                    .show()
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)

                return true
            }
            R.id.action_settings -> {
                // Snackbar.make(findViewById(R.id.content_main), "Action settings menu pressed!", Snackbar.LENGTH_LONG).show()
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_my_acc -> {
                Snackbar.make(findViewById(R.id.content_main), "Action my acc is pressed!", Snackbar.LENGTH_LONG).show()
                val intent = Intent(this, MyAccountActivity::class.java)
                startActivity(intent)
                return true
            }

        }

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        //val fragment = Fragment()

        when (item.itemId) {
            R.id.nav_dashboard -> {
                // Handle the camera action
                //Log.d("Tyler","Working before fragment creating")
                val fragment = DashboardFragment()
                //Log.d("Tyler","Working after fragment creating")
                displaySelectedFragment(fragment)


                Snackbar.make(findViewById(R.id.content_main), "Dashboard  ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

            }
            R.id.nav_check_in -> {

                val fragment = CheckInFragment()
                displaySelectedFragment(fragment)
                Snackbar.make(findViewById(R.id.content_main), "Check In  ", Snackbar.LENGTH_LONG).show()
            }
            R.id.nav_medical_leave -> {

                val fragment = MedicalLeaveFragment()
                displaySelectedFragment(fragment)
                //Snackbar.make(findViewById(R.id.content_main), "Medical Leave Fragment working", Snackbar.LENGTH_LONG).show()
            }
            R.id.nav_settings -> {
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)

            }
            R.id.nav_logout -> {

                AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Logout", DialogInterface.OnClickListener { dialogInterface, i ->
                        // Snackbar.make(findViewById(R.id.content_main), "Yes pressed", Snackbar.LENGTH_LONG).show()
                        val intent = Intent(this, StartActivity::class.java)
                        startActivity(intent)
                        finish()
                        var sharedPreferences = getSharedPreferences(getString(R.string.myPref), Context.MODE_PRIVATE)
                        var sharedEditor = sharedPreferences.edit()
                        sharedEditor.clear()
                        sharedEditor.commit()

                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                        // Snackbar.make(findViewById(R.id.content_main), "No Pressed", Snackbar.LENGTH_LONG)
                    })
                    .show()
            }
            R.id.nav_about -> {
                intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }


        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun displaySelectedFragment(fragment: Fragment) {


        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.content_main, fragment)

        fragmentTransaction.commit()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    fatchLastLocation()
                }
            }
            CAMERA_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    fatchLastLocation()
                }
            }
            READ_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    fatchLastLocation()
                }
            }

            WRITE_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    fatchLastLocation()
                }
            }


        }
    }
}
