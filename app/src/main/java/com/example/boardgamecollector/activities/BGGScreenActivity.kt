package com.example.boardgamecollector.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.boardgamecollector.R
import com.example.boardgamecollector.dataModels.BGGHeader
import com.example.boardgamecollector.dataModels.RanksHeader
import com.example.boardgamecollector.database.*
import com.example.boardgamecollector.databinding.ActivityBggscreenBinding
import com.example.boardgamecollector.utils.BGGapi
import kotlinx.coroutines.*
import java.time.LocalDateTime


class BGGScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBggscreenBinding
    private var fetchJob : Job? = null
    private var importedID: ArrayList<Int>?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBggscreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.progressBarBGG.visibility = View.INVISIBLE
        binding.ImportButton.setOnClickListener { fetchCollection() }
        binding.RankingButton.setOnClickListener { updateRankings() }
    }

    private fun updateRankings() {
        CoroutineScope(Dispatchers.Main).launch {
            binding.progressBarBGG.visibility = View.VISIBLE
            var ranks : ArrayList<RanksHeader>
            withContext(Dispatchers.IO) {
                val db = AppDatabase.getInstance(applicationContext)
                ranks = db.userDao().getGamesRanking() as ArrayList<RanksHeader>

                for (i in ranks)
                {
                    if (importedID?.contains(i.id) == true)
                        continue

                    val oldRank = i.ranking
                    BGGapi.getGameRank(i)
                    if (oldRank != i.ranking)
                    {
                        val dRank = RankHistory(0,i.id,oldRank, LocalDateTime.now() )
                        db.RankDAO().insertAll(dRank)
                        db.userDao().updateRanking(i.id,i.ranking)
                    }
                }
            }
            binding.progressBarBGG.visibility = View.INVISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        fetchJob?.cancel()
    }

    private fun fetchCollection() {
        val user = binding.InputUsername.text.toString()
        val games = ArrayList<BGGHeader>()
        fetchJob?.cancel()
        fetchJob = CoroutineScope(Dispatchers.Main).launch {
            binding.progressBarBGG.visibility = View.VISIBLE
            withContext(Dispatchers.IO) {

                BGGapi.getUserGameList(user,games)

            }
            binding.progressBarBGG.visibility = View.INVISIBLE
            createDialog(games)
        }
    }

    private fun createDialog(games: ArrayList<BGGHeader>) {
        // setup the alert builder
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.gammm))

        games.sortBy {  it.title }

        val gameString = mutableListOf<String>()
        games.forEach {
            gameString.add("${it.title} (${it.year})")
        }

        val checkedItems = BooleanArray(gameString.size) {false}

        builder.setMultiChoiceItems(gameString.toTypedArray(), checkedItems) { dialog, which, isChecked ->
                // user checked or unchecked a box
                checkedItems[which] = isChecked
            }

// add OK and Cancel buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            val selected = ArrayList<BGGHeader>()
            for (i in checkedItems.indices)
            {
                if (checkedItems[i])
                    selected.add(games[i])

            }

            dialog.cancel()
            importSelected(selected)
        })
        builder.setNegativeButton("Cancel"){ dialog, _ ->
        dialog.cancel()
        }

        builder.setNeutralButton(R.string.sel,null)


        val dialog: AlertDialog = builder.create()
        dialog.setOnShowListener {
            val b = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL)
            b.setOnClickListener {
                for (i in checkedItems.indices) {
                    checkedItems[i] = !checkedItems[i]
                }
                val dList = dialog.listView
                for (i in 0 until dList.count) {
                    dList.setItemChecked(i, checkedItems[i])
                }
            }
        }
        dialog.show()
    }

    private fun importSelected(selected: ArrayList<BGGHeader>) {

        fetchJob = CoroutineScope(Dispatchers.Main).launch {
            binding.progressBarBGG.visibility = View.VISIBLE
            val db = AppDatabase.getInstance(applicationContext)
            withContext(Dispatchers.IO) {

                for (i in selected)
                {
                    try {
                        val game = Game()
                        BGGapi.searchGameById(i.bggID, game)
                        addArtistDesignersIfNotExists(game)
                        game.title = game.originalTitle
                        val id = db.userDao().insert(game)
                        game.id = id.toInt()
                        importedID?.add(game.id)
                        saveArtistDesigners(game)
                    }
                    catch(e : Exception){

                    }
                }
            }
            binding.progressBarBGG.visibility = View.INVISIBLE
        }

    }

    private suspend fun addArtistDesignersIfNotExists(game : Game) {
        val db = AppDatabase.getInstance(applicationContext)
        for (i in game.artists)
        {
            var count = 0
            withContext(Dispatchers.IO) {

                count = db.ArtistsDAO().checkIfExists(i.bggID)
                if (count <= 0 || i.bggID < 1) {
                    i.id = db.ArtistsDAO().insert(i)
                }else{
                    i.id = db.ArtistsDAO().getID(i.bggID)
                }
            }
        }
        for (i in game.designers)
        {
            var count = 0
            withContext(Dispatchers.IO) {
                count = db.DesignersDAO().checkIfExists(i.bggID)
                if (count <= 0 || i.bggID < 1) {
                    i.id = db.DesignersDAO().insert(i)
                }else{
                    i.id = db.DesignersDAO().getID(i.bggID)
                }
            }
        }
    }

    private suspend fun saveArtistDesigners(game : Game) {
        val db = AppDatabase.getInstance(applicationContext)
        for (i in game.artists){
            val temp = ArtistsGamesRef(game.id,i.id)
            db.ArtistsGameDAO().insert(temp)
        }
        for (i in game.designers){
            val temp = DesignersGamesRef(game.id,i.id)
            db.DesignersGameDAO().insert(temp)
        }
    }
}