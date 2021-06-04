package com.example.boardgamecollector.activities


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.boardgamecollector.R
import com.example.boardgamecollector.adapters.LocAdapter
import com.example.boardgamecollector.database.AppDatabase
import com.example.boardgamecollector.database.Location
import com.example.boardgamecollector.databinding.ActivityLocationsBinding
import kotlinx.coroutines.*


class LocationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationsBinding
    private var createListJob : Job? = null
    private lateinit var db : AppDatabase
    var locList: ArrayList<Location> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLocationsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view )


        binding.addLoc.setOnClickListener{ addLoc() }
        db = AppDatabase.getInstance(applicationContext)
        createLocList()


    }

    override fun onResume() {
        super.onResume()
        createListJob ?: run {
            createLocList()
        }
    }

    override fun onStop() {
        super.onStop()
        createListJob?.cancel()
        createListJob = null
    }

    private fun createLocListAdapter(){
        val mAdapter = LocAdapter(this, R.layout.list_loc, locList)
        binding.locList.adapter = mAdapter
    }

    private fun createLocList() {
        createListJob = CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {

                locList = db.LocDAO().getAllLocalization() as ArrayList<Location>

            }
            createLocListAdapter()
        }
    }

    private fun addLoc() {


            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            val context: Context = this
            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL

            val titleBox = EditText(context)
            titleBox.hint = "Name"
            layout.addView(titleBox)

            val descriptionBox = EditText(context)
            descriptionBox.hint = "Description"
            layout.addView(descriptionBox)

            builder.setView(layout)



            builder.setPositiveButton("Save", DialogInterface.OnClickListener { dialog, which ->
                saveLoc(titleBox.text.toString(),descriptionBox.text.toString())
            })
            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

            builder.show()

    }

    private fun saveLoc(name: String?, description: String?) {

        createListJob?.cancel()
        createListJob = CoroutineScope(Dispatchers.Main).launch {
            val loc = Location(0,name = name,description = description)
            locList.add(loc)
            withContext(Dispatchers.IO) {

                db.LocDAO().insertAll(loc)

            }
            createLocListAdapter()
        }
    }


}