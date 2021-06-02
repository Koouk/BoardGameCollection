package com.example.boardgamecollector.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.boardgamecollector.dataModels.gameHeader
import com.example.boardgamecollector.databinding.ActivityAddGameBinding

class AddGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val numbersList = intent.getSerializableExtra("GameList") as ArrayList<gameHeader>?

        val xd = 2

        numbersList?.add(gameHeader(10, "The Grey", "d",22,""))
    }
}