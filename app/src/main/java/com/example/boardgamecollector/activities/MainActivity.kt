package com.example.boardgamecollector.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.boardgamecollector.R
import com.example.boardgamecollector.adapters.GameAdapter
import com.example.boardgamecollector.dataModels.gameHeader
import com.example.boardgamecollector.database.AppDatabase
import com.example.boardgamecollector.databinding.ActivityMainBinding
import com.example.boardgamecollector.utils.Helpers
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var createListJob : Job? = null
    var gameList: ArrayList<gameHeader> = ArrayList()
    private var db : AppDatabase? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.addGame.setOnClickListener{ addGame() }
        binding.progressBarMain.visibility = View.INVISIBLE


        CoroutineScope(Dispatchers.Main).launch {
            db = AppDatabase.getInstance(applicationContext)
            loadData()
        }
    }


    override fun onResume() {
        super.onResume()
        if (db != null)
            createListJob ?: run {
                loadData()
            }
    }

    override fun onStop() {
        super.onStop()
        createListJob?.cancel()
        createListJob = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.mainmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        when(id){
            R.id.Location -> locationsActivity()
            R.id.BGG -> bggActivity()
            R.id.Name -> sortByName()
            R.id.Date -> sortByYear()
            R.id.Rank -> sortByRank()
        }
        return true
    }

    private fun bggActivity() {
        val i = Intent(this, BGGScreenActivity::class.java)
        startActivity(i)
    }

    private fun locationsActivity()
    {
        val i = Intent(this, LocationsActivity::class.java)
        startActivity(i)
    }

    private fun loadData() {
        createListJob = CoroutineScope(Dispatchers.Main).launch {
            binding.progressBarMain.visibility = View.VISIBLE
            withContext(Dispatchers.IO) {

                gameList = db?.userDao()?.getAllHeaders() as ArrayList<gameHeader>
                for (i in gameList)
                {
                    i.image = i.URL?.let { Helpers.getImage(it) }
                }
            }
            binding.progressBarMain.visibility = View.INVISIBLE
            createGameListAdapter()
        }
    }

    private fun createGameListAdapter(){
        val mAdapter = GameAdapter(this, R.layout.list_game, gameList)
        binding.gameList.adapter = mAdapter

        binding.gameList.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, GameDetailsActivity::class.java)
            val idT = gameList[position].id
            intent.putExtra("id", idT.toString())
            startActivity(intent)
        }
        }


    private fun click(position: Int, convertView: View?, parent: ViewGroup)
    {

        Toast.makeText(this, "Selected : $position",  Toast.LENGTH_SHORT).show()
    }

    private fun addGame(){
        val i = Intent(this, AddGameActivity::class.java)
        startActivity(i)
    }

    fun sortByYear() {
        gameList.sortByDescending { it.year }
        createGameListAdapter()
    }

    fun sortByRank() {
        gameList.sortBy { if (it.ranking!! > 0) it.ranking else Int.MAX_VALUE }
        createGameListAdapter()
    }

    fun sortByName() {
        gameList.sortBy { if( it.title.isNullOrBlank()) "ZZZ" else it.title }
        createGameListAdapter()
    }
}