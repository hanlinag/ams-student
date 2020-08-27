package com.ucsm.tylersai.amsucsm.adapters

import android.content.Context
import android.content.Intent
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.ucsm.tylersai.amsucsm.NotiDetailActivity
import com.ucsm.tylersai.amsucsm.R
import com.ucsm.tylersai.amsucsm.models.Notifications

class NotiListViewAdapter(context: Context, arrayList: ArrayList<Notifications>) : ListAdapter {
    var context: Context? = context
    var arrayList: ArrayList<Notifications>? = arrayList


    override fun isEmpty(): Boolean {

        return false
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var noti: Notifications = arrayList!!.get(position)

        var inflater = LayoutInflater.from(context)
        var rowView = inflater.inflate(R.layout.view_noti_row_item, null, true)


        var titleTv = rowView.findViewById<TextView>(R.id.noti_title)
        var messageTv = rowView.findViewById<TextView>(R.id.noti_body)
        var dateTv = rowView.findViewById<TextView>(R.id.noti_date)

        titleTv.text = noti.title
        messageTv.text = noti.body
        dateTv.text = noti.date

        rowView.setOnClickListener {
            val title = arrayList!!.get(position).title
            val body = arrayList!!.get(position).body
            val date = arrayList!!.get(position).date

            // Toast.makeText(rowView.context, "Hello $text ", Toast.LENGTH_LONG).show()

            val intent = Intent(rowView.context, NotiDetailActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("body", body)
            intent.putExtra("date", date)

            startActivity(rowView.context, intent, null)
        }

        return rowView
    }

    override fun registerDataSetObserver(p0: DataSetObserver?) {

    }

    override fun getItemViewType(position: Int): Int {
        return position

    }

    override fun getItem(position: Int): Any {

        return position
    }

    override fun getViewTypeCount(): Int {

        return arrayList!!.size
    }

    override fun isEnabled(p0: Int): Boolean {

        return true
    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    override fun hasStableIds(): Boolean {

        return false
    }

    override fun areAllItemsEnabled(): Boolean {

        return false
    }

    override fun unregisterDataSetObserver(p0: DataSetObserver?) {

    }

    override fun getCount(): Int {

        return arrayList!!.size
    }


}