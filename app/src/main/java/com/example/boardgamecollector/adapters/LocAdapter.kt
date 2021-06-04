package com.example.boardgamecollector.adapters


import android.content.Context;
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.boardgamecollector.R
import com.example.boardgamecollector.database.Location


open class LocAdapter(context: Context, resource: Int, list: ArrayList<Location>) :
    ArrayAdapter<Location>(context, resource, list) {

    private val mContext: Context
    private var locList: ArrayList<Location>


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var listItem = convertView
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_loc, parent, false)

        val currentloc = locList[position]

        val nameField = listItem?.findViewById<TextView>(R.id.Loc_name)
        if (nameField != null) {
            nameField.text = currentloc.name
        }


        val descriptionField = listItem?.findViewById<TextView>(R.id.Loc_desc)
        if (descriptionField != null) {
            descriptionField.text = currentloc.description
        }

        return listItem as View
    }

    init {
        mContext = context
        locList = list
    }
}