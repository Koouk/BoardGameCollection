package com.example.boardgamecollector.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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
    var gameList: ArrayList<gameHeader> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        println("diemas")
        loadData()
        binding.addGame.setOnClickListener{ addGame() }
    }

    override fun onResume() {
        super.onResume()
        createListJob ?: run {
            loadData()
        }
    }

    override fun onStop() {
        super.onStop()
        //createListJob?.cancel()
        createListJob = null
    }

    private fun loadData() {
        createListJob = lifecycleScope.launch {
            println("xd1")
            withContext(Dispatchers.IO) {
                delay(10000)
                println("xd3")
                val db = AppDatabase.getInstance(applicationContext)
                println("xd4")
                gameList = db.userDao().getAllHeaders() as ArrayList<gameHeader>
                println("xd5")

            }
            println("xd2")
            createGameListAdapter()
        }
    }

    private fun createGameListAdapter(){
        val mAdapter = GameAdapter(this, R.layout.list_game, gameList)
        binding.gameList.adapter = mAdapter
    }

    private fun addGame(){
        val i = Intent(this, AddGameActivity::class.java)
        startActivity(i)
    }


}