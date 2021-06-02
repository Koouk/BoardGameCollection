package com.example.boardgamecollector.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.boardgamecollector.database.Game
import com.example.boardgamecollector.R
import com.example.boardgamecollector.adapters.GameAdapter
import com.example.boardgamecollector.dataModels.gameHeader
import com.example.boardgamecollector.database.AppDatabase
import com.example.boardgamecollector.database.GameDAO
import com.example.boardgamecollector.databinding.ActivityMainBinding
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var createListJob : Job? = null
    private var i : Int = 10
    var gameList: ArrayList<gameHeader> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        createListJob = loadData()
        binding.addGame.setOnClickListener{ addGame() }
    }

    fun popData() = CoroutineScope(Dispatchers.Main).launch {

        val gameLists: ArrayList<Game> = ArrayList()
        val task = async(Dispatchers.IO){
            val db = AppDatabase.getInstance(applicationContext)
            for (game in gameLists)
                db.userDao().insertAll(game)
        }
       task.await()

    }

    override fun onResume() {
        super.onResume()
        createListJob = loadData()
    }

    override fun onStop() {
        super.onStop()

        createListJob?.cancel()
    }

    private fun loadData(): Job {

     return CoroutineScope(Dispatchers.Main).launch {
        delay(10000)
        val db = AppDatabase.getInstance(applicationContext)
        //gameList = db.userDao().getAllHeaders() as ArrayList<gameHeader>

        val task = async(Dispatchers.IO){
            db.userDao().getAllHeaders()
        }

        gameList = task.await() as ArrayList<gameHeader>
        createGameList()
    }
    }

    private fun createGameList(){

        val mAdapter = GameAdapter(this, R.layout.list_game, gameList)
        binding.gameList.adapter = mAdapter
    }

    private fun addGame(){
        val i = Intent(this, AddGameActivity::class.java)
        startActivity(i)
    }


}