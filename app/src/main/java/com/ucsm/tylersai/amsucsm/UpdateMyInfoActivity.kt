package com.ucsm.tylersai.amsucsm

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.ucsm.tylersai.amsucsm.models.Student
import kotlinx.android.synthetic.main.activity_update_my_info.*
import java.io.File

class UpdateMyInfoActivity : AppCompatActivity() {
    lateinit var edtPhone: EditText
    lateinit var edtEmail: EditText
    lateinit var edtAddress: EditText
    lateinit var imgUpdateProfile: ImageView

    lateinit var btUploadProfilePic: Button

    lateinit var studentTable: DatabaseReference

    lateinit var sharedPreference: SharedPreferences

    private var mStorageReference: StorageReference? = null

    lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_my_info)

        supportActionBar!!.title = "Update Info"
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        edtPhone = findViewById(R.id.edt_phone_myacc)
        edtEmail = findViewById(R.id.edt_email_myacc)
        edtAddress = findViewById(R.id.edt_address_myacc)
        imgUpdateProfile = findViewById(R.id.change_profile_image)

        btUploadProfilePic = findViewById(R.id.bt_upload_profile_pic_update)

        filePath = ""

        studentTable = FirebaseDatabase.getInstance().reference.child("ams").child("student")

        sharedPreference = getSharedPreferences(getString(R.string.myPref), Context.MODE_PRIVATE)

        mStorageReference = FirebaseStorage.getInstance().reference

        edtPhone.setText(sharedPreference.getString("phone", null))
        edtEmail.setText(sharedPreference.getString("email", null))
        edtAddress.setText(sharedPreference.getString("address", null))

        //download image file from firebase storage


        Glide.with(this).load(sharedPreference.getString("profileurl", null)).into(imgUpdateProfile)

        // Picasso.get().load(sharedPreference.getString("profileurl", null)).into(imgUpdateProfile)


        var btChangeProfile = findViewById<ImageView>(R.id.change_profile_image)

        btChangeProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        btUploadProfilePic.setOnClickListener {

            // var downloaduri:String = "c"
            if (filePath != "") {

                uploadImageToFirebaseStorage(filePath)
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("No image selected!")
                    .setNeutralButton("Okay", DialogInterface.OnClickListener { dialogInterface, i ->

                    })
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                var selectedImage: Uri = data!!.data


                filePath = getRealPathFromURI(selectedImage)

                Log.d("Tyler", "Filepath is $filePath")
                var fileExtension = filePath.substring(filePath.lastIndexOf(".") + 1)


                Log.d("Tyler", "Filepath extension: $fileExtension")
                // tvFileName?.text = "$filePath is pending to upload. "

                try {
                    if (fileExtension.equals("img") || fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals(
                            "png"
                        )
                    ) {

                        change_profile_image!!.setImageURI(selectedImage)

                    }//end of if
                    else {

                        AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage("File not supported!")
                            .show()
                    }
                } catch (ex: Exception) {
                    Log.d("Tyler", "Exception: ${ex.message.toString()}")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_update_my_info, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_update_done -> {
                //Snackbar.make(findViewById(R.id.update_info_activity_root_view),"Something", Snackbar.LENGTH_LONG).show()


                var prefEditor = sharedPreference.edit()


                var phone = edtPhone.text.toString()
                var email = edtEmail.text.toString()
                var address = edtAddress.text.toString()

                var student = Student(
                    sharedPreference.getString("name", null),
                    sharedPreference.getString("major", null),
                    sharedPreference.getString("subject", null),
                    phone,
                    email,
                    address,
                    sharedPreference.getString("password", null),
                    sharedPreference.getString("profileurl", null),
                    sharedPreference.getString("mkpt", null)

                )



                studentTable.child(sharedPreference.getString("mkpt", null)).setValue(student)

                prefEditor.putString("phone", phone)
                prefEditor.putString("email", email)
                prefEditor.putString("address", address)
                //prefEditor.clear()
                prefEditor.commit()

                Toast.makeText(this, "Information updated", Toast.LENGTH_LONG).show()
                //  uploadDialog.dismiss()
                finish()
            }
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun uploadImageToFirebaseStorage(url: String?) {

        var dialog = ProgressDialog(this)
        dialog.setMessage("Uploading...")
        dialog.show()
        var downloadUri: String = ""

        var file = Uri.fromFile(File(url))
        var profileLocationRef =
            mStorageReference?.child("profileimages/mkpt_${sharedPreference.getString("mkpt", null)}")


        //var uploadTask = mountainsRef.putBytes(data)
        //  val ref = my.child("images/mountains.jpg")
        var uploadTask = profileLocationRef?.putFile(file)

        val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation profileLocationRef?.downloadUrl
        })?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
                // Log.d("Tyler", "download link is:::: $downloadUri")

                var prefEditor = sharedPreference.edit()
                prefEditor.putString("profileurl", downloadUri)
                prefEditor.commit()

                Log.d("Tyler", "New profile url is: ${sharedPreference.getString("profileurl", null)}")


                var student = Student(
                    sharedPreference.getString("name", null),
                    sharedPreference.getString("major", null),
                    sharedPreference.getString("subject", null),
                    sharedPreference.getString("phone", null),
                    sharedPreference.getString("email", null),
                    sharedPreference.getString("phone", null),
                    sharedPreference.getString("address", null),
                    downloadUri,
                    sharedPreference.getString("mkpt", null)
                )
                studentTable.child(sharedPreference.getString("mkpt", null)).setValue(student)


                dialog.dismiss()
                Toast.makeText(this, "New Profile Picture Upload Complete", Toast.LENGTH_LONG).show()
            } else {
                // Handle failures
                // ...
            }
        }?.addOnFailureListener {
            dialog.dismiss()
            Toast.makeText(this, "Upload Fail!", Toast.LENGTH_LONG).show()
        }

        //return downloadUri

    }


    fun getRealPathFromURI(uri: Uri): String {
        val cursor = this.contentResolver!!.query(uri, null, null, null, null)
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        Log.d("Filepath", "Filepath: ${cursor.getString(idx)}")
        return cursor.getString(idx)
    }
}
