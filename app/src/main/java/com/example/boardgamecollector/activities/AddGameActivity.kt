package com.example.boardgamecollector.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.boardgamecollector.R
import com.example.boardgamecollector.dataModels.BGGHeader
import com.example.boardgamecollector.database.AppDatabase
import com.example.boardgamecollector.database.Game
import com.example.boardgamecollector.databinding.ActivityAddGameBinding
import com.example.boardgamecollector.utils.BGGapi
import com.example.boardgamecollector.utils.Helpers
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AddGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGameBinding
    private var title :String? = null
    private var jobHeader : Job? = null
    private var jobForm : Job? = null
    private var jobSpinner: Job? = null
    private var game : Game? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.SaveButton.setOnClickListener{saveGame()}
        binding.CancelButton.setOnClickListener{goBack()}
        createLocAndTypeList()

        makeTitleDialog()

    }

    private fun goBack() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    private fun createLocAndTypeList() {


        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Helpers.GAME_TYPE)
        binding.typeEnter.adapter = adapter
        binding.typeEnter.setSelection(-1)

        jobSpinner?.cancel()
        jobSpinner = CoroutineScope(Dispatchers.Main).launch {

            withContext(Dispatchers.IO) {
                // TODO locations spinner
            }
        }
    }


    override fun onStop() {
        super.onStop()
        jobForm?.cancel()
        jobHeader?.cancel()
        jobSpinner?.cancel()
    }

    private fun makeTitleDialog() {

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val alertText =resources.getString(R.string.addGameAlertTitle)
        builder.setTitle(alertText)

        val input = EditText(this)
        input.setHint(R.string.addGameAlertEnter)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            title = input.text.toString()
            getGamesByName()
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

    private fun makeChoiceDialog(games : ArrayList<BGGHeader>) {
        val alertDialog = AlertDialog.Builder(this)

        alertDialog.setIcon(R.drawable.ic_launcher_foreground)

        alertDialog.setTitle("Choose an Item")

        //TODO
        //val listItems = arrayOf("Android Development", "WebDevelopme ntDevelopmentDeve lopmentDevelopment Development Development Development", "Machine Learning", "Machine Learning", "Machine Learning", "Machine Learning", "Machine Learning", "Machine Learning", "Machine Learning", "Machine Learning", "Machine Learning", "Machine Learning", "Machine Learning", "Machine Learning")

        val listItems : List <String> = games.map { i ->
            "${i.title} (${i.year})"
        }
        var checkedItem  : Int = -1

        alertDialog.setSingleChoiceItems(
            listItems.toTypedArray(), checkedItem )
        { dialog, which ->

            checkedItem = which
        }

        alertDialog.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            fillForm(games[checkedItem]) //TODO mozna przekazac calosc
            dialog.dismiss()

        })
        alertDialog.setNegativeButton(
            "Skip"
        ) { dialog, which ->
            binding.titleEnter.text = SpannableStringBuilder(title)
            dialog.cancel()
        }


        val customAlertDialog = alertDialog.create()

        customAlertDialog.show()
    }

    private fun fillForm(g: BGGHeader) {
        jobForm?.cancel()
        jobForm = CoroutineScope(Dispatchers.Main).launch {
            game = Game()
            game!!.title = g.title
            game!!.releaseYear = g.year
            game!!.bggId = g.bggID

            withContext(Dispatchers.IO) {
                BGGapi.searchGameById(g.bggID, game!!)
                //TODO SPINNER
            }
            if (game!!.title != null)
                binding.titleEnter.text = SpannableStringBuilder(game!!.title)
            binding.bggIdEnter.text = SpannableStringBuilder(game!!.bggId.toString())
            if (game!!.releaseYear != null)
                binding.releaseYearEnter.text = SpannableStringBuilder(game!!.releaseYear)

            binding.orgTitleEnter.text = SpannableStringBuilder(game!!.originalTitle)

            if (game!!.ThumbURL != null)
                binding.thumbnailEnter.text = SpannableStringBuilder(game!!.ThumbURL)
            if (game!!.ImgURL != null)
                binding.imgEnter.text = SpannableStringBuilder(game!!.ImgURL)

            binding.descriptionEnter.text = SpannableStringBuilder(game!!.description)
            binding.rankingEnter.text = SpannableStringBuilder(game!!.ranking.toString())

            val index = Helpers.GAME_TYPE.indexOf(game!!.gameType)

            binding.typeEnter.setSelection(index)

        }
    }

    private fun saveGame() {
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        CoroutineScope(Dispatchers.Main).launch {
            if (game == null)
                game = Game()
            game?.title = binding.titleEnter.text.toString()
            game?.originalTitle = binding.orgTitleEnter.text.toString()
            game?.releaseYear = binding.releaseYearEnter.text.toString()
            game?.description = binding.descriptionEnter.text.toString()
            val p1 = binding.OrderDatePicker
            game?.orderDate = LocalDate.of(p1.year, p1.month+ 1, p1.dayOfMonth)
            val p2 = binding.AddDatePicker
            game?.addDate = LocalDate.of(p2.year, p2.month+ 1, p2.dayOfMonth)
            game?.cost = binding.costEnter.text.toString()
            game?.scd = binding.SCDEnter.text.toString()
            game?.ean = binding.EANEnter.text.toString()
            try {
                game?.bggId = binding.bggIdEnter.text.toString().toLong()
            }catch (e :Exception){
                Log.e("bggID",e.message.toString())
            }
            try {
                game?.ranking = binding.rankingEnter.text.toString().toInt()
            }catch (e :Exception){
                Log.e("rank",e.message.toString())
            }

            game?.productionCode = binding.productionCodeEnter.text.toString()

            if (binding.typeEnter.selectedItemPosition != -1)
                game?.gameType = Helpers.GAME_TYPE[binding.typeEnter.selectedItemPosition]//TODO tylko konkretne typu


            game?.comment = binding.commentEnter.text.toString()
            game?.ThumbURL = binding.thumbnailEnter.text.toString()
            game?.ImgURL = binding.imgEnter.text.toString()
            //TODO lokalizacje

            withContext(Dispatchers.IO) {

                val db = AppDatabase.getInstance(applicationContext)
                game?.let { db.userDao().insertAll(it) }
            }
        }
    }

    private fun getGamesByName(){
        jobHeader?.cancel()
        val games : ArrayList<BGGHeader> = ArrayList()
        jobHeader = CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                if (title != null)
                    BGGapi.searchGamesByTitle(title!!,games)
            }
            makeChoiceDialog(games)
        }
    }
}