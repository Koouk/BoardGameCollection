package com.example.boardgamecollector.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.boardgamecollector.databinding.ActivityAddGameBinding
import com.example.boardgamecollector.databinding.ActivityGameDetailsBinding
import com.example.boardgamecollector.databinding.ActivityMainBinding

class GameDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val extras = intent.extras ?: return
        val id = extras.getString("id")?.toLong()
        binding.textView.text =id.toString()


    }
}