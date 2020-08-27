package com.ucsm.tylersai.amsucsm

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.ucsm.tylersai.amsucsm.adapters.SubjectAdapter
import com.ucsm.tylersai.amsucsm.models.Subject
import java.text.SimpleDateFormat
import java.util.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DashboardFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private var tvToday: TextView? = null

    private val DATEFORMAT = "dd MMMM yyyy"


    lateinit var sharedPreferences: SharedPreferences


    private var subjectTable: DatabaseReference? = null

    lateinit var recyclerView: RecyclerView

    // private var studentTable:DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        //activity.title(Navigatio)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Log.d("Tyler","entering subadapter $subAdapter")
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_dashboard)

        // recyclerView?.adapter = subadapter
        //recyclerView?.setLayoutManager(view?.findViewById<RecyclerView>(R.id.recycler_view_dashboard)?.layoutManager)


        var progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading...")
        progressDialog.show()

        sharedPreferences = this.activity!!.getSharedPreferences(getString(R.string.myPref), Context.MODE_PRIVATE)

        var registeredSubject = sharedPreferences.getString(getString(R.string.prefSubject), null)

        subjectTable = FirebaseDatabase.getInstance().reference.child("ams").child("subject")

        val dataset = ArrayList<Subject>()
        dataset.clear()
        //dataset.add(Subject("dd","f","f","f","f","f"))

        var subListFromPreferences = registeredSubject.split(",")

        for (i in 0 until subListFromPreferences.size) {
            // Log.d("Tyler","Subjects: ${subListFromPreferences.get(i).trim()}")
            //check it is "" string or real subject
            if (subListFromPreferences[i].length > 1) {
                var splitedSubject = subListFromPreferences[i]
                var doubleSplitedSubject = splitedSubject.split(":")
                var subjectCode = doubleSplitedSubject[0].trim()
                // var subjectName  = doubleSplitedSubject[1].trim()

                // Log.d("Tyler","Subject code is: $subjectCode and subject name is: $subjectName")
                // dataset.add(Subject(subjectCode, subjectName))
                // var subject:Subject? = null
                subjectTable!!.child(subjectCode).addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                        Toast.makeText(context, "Error occur" + p0.toException().toString(), Toast.LENGTH_LONG).show()
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        //val childern = dataSnapshot.children

                        var subject = dataSnapshot.getValue(Subject::class.java)

                        Log.d(
                            "Tyler",
                            "subject on data snapshot: ${subject!!.subjectCode} ${subject.name} ${subject.day} ${subject.teacherId} ${subject.room} ${subject.time} "
                        )


                        //final step
                        dataset.add(
                            Subject(
                                subject.subjectCode,
                                subject.name,
                                subject.day,
                                subject.teacherId,
                                subject.room,
                                subject.time
                            )
                        )

                        recyclerView.adapter = SubjectAdapter(
                            dataset,
                            view!!.context,
                            sharedPreferences.getString(getString(R.string.prefMkpt), null)
                        )
                        //adapter.notifyDataSetChanged()

                        progressDialog.dismiss()


                    }//end of on data change for subject


                })//end of subject table Value event listener

                // catchDatafromFirebase(subjectCode)


            }//end of if
        }//end if for loop

        var dateFormat = SimpleDateFormat(DATEFORMAT)
        var c = Calendar.getInstance().time
        var today = dateFormat.format(c)

        tvToday = view?.findViewById(R.id.today_date_tv)

        tvToday?.text = today



        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(view!!.context)


        }

        // Inflate the layout for this fragment
        return view

    }


    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {

        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}

