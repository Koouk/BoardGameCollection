package com.example.boardgamecollector.activities


import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.boardgamecollector.dataModels.LocHeader
import com.example.boardgamecollector.database.AppDatabase
import com.example.boardgamecollector.database.Location
import com.example.boardgamecollector.databinding.ActivityLocDetailsBinding
import kotlinx.coroutines.*

class LocDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocDetailsBinding
    private var id: Long? = null
    private var location: Location? = null
    private var createListJob: Job? = null
    var gameList = ArrayList<LocHeader>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val extras = intent.extras ?: return
        id = extras.getString("id").toLong()!!

        if (id != null)
            fillDetails()
        else
            finish()


        binding.Save.setOnClickListener { save() }
        binding.Delete.setOnClickListener { delete() }
    }

    private fun save() {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                location?.name = binding.Name.text.toString()
                location?.description = binding.Comment.text.toString()
                val db = AppDatabase.getInstance(applicationContext)
                location?.let { db.LocDAO().update(it) }
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        applicationContext, com.example.boardgamecollector.R.string.ToastSave,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            finish()
        }
    }

    private fun delete() {

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val db = AppDatabase.getInstance(applicationContext)
                val count = location?.let { db.userDao().getGamesByLocation(it.id) }
                if (count != null) {
                    if (count.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                applicationContext, com.example.boardgamecollector.R.string.Error,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        location?.let { db.LocDAO().delete(it) }
                        finish()
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        createListJob?.cancel()
    }

    private fun fillDetails() {
        createListJob?.cancel()
        createListJob = CoroutineScope(Dispatchers.Main).launch {

            withContext(Dispatchers.IO) {
                val db = AppDatabase.getInstance(applicationContext)
                location = id?.let { db.LocDAO().getLocById(it) }
                gameList = id?.let { db.userDao().getGamesByLocation(it) } as ArrayList<LocHeader>

            }
            binding.Name.text = SpannableStringBuilder(location?.name.toString())
            binding.Comment.text = SpannableStringBuilder(location?.description)

            val list = arrayListOf<String>()
            gameList.forEach {
                list.add("${it.title} (${it.year})")
            }
            val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, list)
            binding.List.adapter = adapter

            binding.List.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    val idT = gameList[position].id
                    val intent = Intent(applicationContext, GameDetailsActivity::class.java)
                    intent.putExtra("id", idT.toString())
                    startActivity(intent)

                }

        }
    }
}