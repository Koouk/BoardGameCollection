package com.example.boardgamecollector.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.boardgamecollector.R
import com.example.boardgamecollector.dataModels.BGGHeader
import com.example.boardgamecollector.database.*
import com.example.boardgamecollector.databinding.ActivityAddGameBinding
import com.example.boardgamecollector.utils.BGGapi
import com.example.boardgamecollector.utils.Helpers
import kotlinx.coroutines.*
import java.time.LocalDate


class AddGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGameBinding
    private var title: String? = null
    private var jobHeader: Job? = null
    private var jobForm: Job? = null
    private var jobSpinner: Job? = null
    private var game: Game = Game()
    private var locs: ArrayList<Location>? = null
    private var allArtists: ArrayList<Artists>? = null
    private var allDesigners: ArrayList<Designers>? = null
    private var currentArtists: ArrayList<Artists>? = null
    private var currentDesigners: ArrayList<Designers>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.SaveButton.setOnClickListener { saveGame() }
        binding.CancelButton.setOnClickListener { goBack() }
        binding.addArButton.setOnClickListener { addArtist() }
        binding.RMArButton.setOnClickListener { rmArtist() }
        binding.addDeButton.setOnClickListener { addDesigner() }
        binding.RMDeButton.setOnClickListener { rmDesigner() }

        binding.progressBarAdd.visibility = View.INVISIBLE
        createLists()
        makeTitleDialog()

    }

    private fun addArtist() {

        val alert = AlertDialog.Builder(this)
        alert.setTitle(getString(R.string.input))
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val name = EditText(this)
        name.hint = getString(R.string.name)
        val surname = EditText(this)
        surname.hint = getString(R.string.surname)
        layout.addView(name)
        layout.addView(surname)
        alert.setView(layout)

        alert.setPositiveButton(getString(R.string.Add)) { _, _ ->
            CoroutineScope(Dispatchers.Main).launch {
                val ar = Artists(0, name.text.toString(), surname.text.toString())
                withContext(Dispatchers.IO) {
                    val db = AppDatabase.getInstance(applicationContext)
                    val ids = db.ArtistsDAO().insert(ar)
                    ar.id = ids
                    game.artists.add(ar)
                }

                artistDesignersFill()
            }
        }
        alert.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
        alert.show()
    }

    private fun rmArtist() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.persons))

        game.artists.sortBy { it.surname }


        val gameString = mutableListOf<String>()
        currentArtists = ArrayList<Artists>()

        game.artists.forEach {
            gameString.add("${it.name} ${it.surname}")
            currentArtists!!.add(it)
        }
        allArtists?.forEach {
            if (!game.artists.contains(it)) {
                gameString.add("${it.name} ${it.surname}")
                currentArtists!!.add(it)
            }
        }

        val checkedItems = BooleanArray(gameString.size) { false }
        for (i in game.artists.indices) {
            checkedItems[i] = true
        }
        builder.setMultiChoiceItems(gameString.toTypedArray(), checkedItems) { _, which, isChecked ->
            // user checked or unchecked a box
            checkedItems[which] = isChecked
        }

// add OK and Cancel buttons
        builder.setPositiveButton("OK") { dialog, _ ->
            val selected = ArrayList<Artists>()
            for (i in checkedItems.indices) {
                if (checkedItems[i])
                    selected.add(currentArtists!![i])

            }
            game.artists = selected
            artistDesignersFill()
            dialog.cancel()

        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun addDesigner() {

        val alert = AlertDialog.Builder(this)
        alert.setTitle(getString(R.string.input))
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val name = EditText(this)
        name.hint = getString(R.string.name)
        val surname = EditText(this)
        surname.hint = getString(R.string.surname)
        layout.addView(name)
        layout.addView(surname)
        alert.setView(layout)

        alert.setPositiveButton(getString(R.string.Add)) { _, _ ->
            CoroutineScope(Dispatchers.Main).launch {
                val ar = Designers(0, name.text.toString(), surname.text.toString())
                withContext(Dispatchers.IO) {
                    val db = AppDatabase.getInstance(applicationContext)
                    val ids = db.DesignersDAO().insert(ar)
                    ar.id = ids
                    game.designers.add(ar)
                }
                artistDesignersFill()
            }
        }
        alert.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
        alert.show()
    }

    private fun rmDesigner() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.persons))

        game.designers.sortBy { it.surname }


        val gameString = mutableListOf<String>()
        currentDesigners = ArrayList<Designers>()

        game.designers.forEach {
            gameString.add("${it.name} ${it.surname}")
            currentDesigners!!.add(it)
        }
        allDesigners?.forEach {
            if (!game.designers.contains(it)) {
                gameString.add("${it.name} ${it.surname}")
                currentDesigners!!.add(it)
            }
        }

        val checkedItems = BooleanArray(gameString.size) { false }
        for (i in game.designers.indices) {
            checkedItems[i] = true
        }
        builder.setMultiChoiceItems(gameString.toTypedArray(), checkedItems) { _, which, isChecked ->
            // user checked or unchecked a box
            checkedItems[which] = isChecked
        }

// add OK and Cancel buttons
        builder.setPositiveButton("OK") { dialog, _ ->
            val selected = ArrayList<Designers>()
            for (i in checkedItems.indices) {
                if (checkedItems[i])
                    selected.add(currentDesigners!![i])

            }
            game.designers = selected
            artistDesignersFill()
            dialog.cancel()

        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun goBack() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    private fun createLists() {


        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Helpers.GAME_TYPE)
        binding.typeEnter.adapter = adapter
        binding.typeEnter.setSelection(-1)
        binding.typeEnter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 1)
                    binding.bggParent.visibility = View.VISIBLE
                else
                    binding.bggParent.visibility = View.INVISIBLE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        jobSpinner?.cancel()
        jobSpinner = CoroutineScope(Dispatchers.Main).launch {

            binding.locationsEnter.setSelection(-1)
            withContext(Dispatchers.IO) {
                val db = AppDatabase.getInstance(applicationContext)
                locs = db.LocDAO().getAllLocalization() as ArrayList<Location>?
                allArtists = db.ArtistsDAO().getAll() as ArrayList<Artists>
                allDesigners = db.DesignersDAO().getAll() as ArrayList<Designers>
                allArtists!!.sortBy { it.surname }
                allDesigners!!.sortBy { it.surname }
            }
            val list = arrayListOf<String>()
            list.add(getString(R.string.emptyElement))
            locs?.forEach {
                list.add("${it.name} (${it.description})")
            }
            val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, list)
            binding.locationsEnter.adapter = adapter

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
        val alertText = resources.getString(R.string.addGameAlertTitle)
        builder.setTitle(alertText)

        val input = EditText(this)
        input.setHint(R.string.addGameAlertEnter)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            title = input.text.toString()
            dialog.cancel()
            getGamesByName()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun makeChoiceDialog(games: ArrayList<BGGHeader>) {
        val alertDialog = AlertDialog.Builder(this)

        alertDialog.setIcon(R.drawable.ic_launcher_foreground)

        alertDialog.setTitle("Choose an Item")

        val listItems: List<String> = games.map { i ->
            "${i.title} (${i.year})"
        }
        var checkedItem: Int = -1

        alertDialog.setSingleChoiceItems(
            listItems.toTypedArray(), checkedItem
        )
        { _, which ->

            checkedItem = which
        }

        alertDialog.setPositiveButton("OK") { dialog, _ ->
            if (checkedItem > -1)
                fillForm(games[checkedItem])
            dialog.dismiss()

        }
        alertDialog.setNegativeButton(
            "Skip"
        ) { dialog, _ ->
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
            game.title = g.title
            game.releaseYear = g.year
            game.bggId = g.bggID

            withContext(Dispatchers.IO) {
                BGGapi.searchGameById(g.bggID, game)
                // dodawanie artystow i proj jesli ich nie ma jeszcze

                addArtistDesignersIfNotExists()
                //TODO SPINNER
            }
            if (game.title != null)
                binding.titleEnter.text = SpannableStringBuilder(game.title)
            binding.bggIdEnter.text = SpannableStringBuilder(game.bggId.toString())
            if (game.releaseYear != null)
                binding.releaseYearEnter.text = SpannableStringBuilder(game.releaseYear)

            binding.orgTitleEnter.text = SpannableStringBuilder(game.originalTitle)

            if (game.ThumbURL != null)
                binding.thumbnailEnter.text = SpannableStringBuilder(game.ThumbURL)
            if (game.ImgURL != null)
                binding.imgEnter.text = SpannableStringBuilder(game.ImgURL)

            binding.descriptionEnter.text = SpannableStringBuilder(game.description)
            binding.rankingEnter.text = SpannableStringBuilder(game.ranking.toString())

            val index = Helpers.GAME_TYPE.indexOf(game.gameType)

            binding.typeEnter.setSelection(index)

            if (game.parentBGG != null)
                binding.bggParentEnter.text = SpannableStringBuilder(game.parentBGG.toString())

            artistDesignersFill()
        }
    }

    private suspend fun addArtistDesignersIfNotExists() {
        val db = AppDatabase.getInstance(applicationContext)
        for (i in game.artists) {
            var count = 0
            withContext(Dispatchers.IO) {

                count = db.ArtistsDAO().checkIfExists(i.bggID)
                if (count <= 0 || i.bggID < 1) {
                    i.id = db.ArtistsDAO().insert(i)
                } else {
                    i.id = db.ArtistsDAO().getID(i.bggID)
                }
            }
        }
        for (i in game.designers) {
            var count = 0
            withContext(Dispatchers.IO) {
                count = db.DesignersDAO().checkIfExists(i.bggID)
                if (count <= 0 || i.bggID < 1) {
                    i.id = db.DesignersDAO().insert(i)
                } else {
                    i.id = db.DesignersDAO().getID(i.bggID)
                }
            }
        }
    }

    private fun saveGame() {

        CoroutineScope(Dispatchers.Main).launch {
            binding.progressBarAdd.visibility = View.VISIBLE
            game.title = binding.titleEnter.text.toString()
            game.originalTitle = binding.orgTitleEnter.text.toString()
            game.releaseYear = binding.releaseYearEnter.text.toString()
            game.description = binding.descriptionEnter.text.toString()
            val p1 = binding.OrderDatePicker
            game.orderDate = LocalDate.of(p1.year, p1.month + 1, p1.dayOfMonth)
            val p2 = binding.AddDatePicker
            game.addDate = LocalDate.of(p2.year, p2.month + 1, p2.dayOfMonth)
            game.cost = binding.costEnter.text.toString()
            game.scd = binding.SCDEnter.text.toString()
            game.ean = binding.EANEnter.text.toString()
            try {
                game.bggId = binding.bggIdEnter.text.toString().toLong()
            } catch (e: Exception) {
                Log.e("bggID", e.message.toString())
            }
            try {
                game.ranking = binding.rankingEnter.text.toString().toInt()
            } catch (e: Exception) {
                Log.e("rank", e.message.toString())
            }

            game.productionCode = binding.productionCodeEnter.text.toString()

            if (binding.typeEnter.selectedItemPosition != -1)
                game.gameType = Helpers.GAME_TYPE[binding.typeEnter.selectedItemPosition]

            game.locComment = binding.locCOMEnter.text.toString()
            game.comment = binding.commentEnter.text.toString()
            game.ThumbURL = binding.thumbnailEnter.text.toString()
            game.ImgURL = binding.imgEnter.text.toString()
            val pGG = binding.bggParentEnter.text.toString()
            if (pGG.isBlank())
                game.parentBGG = 0
            else
                game.parentBGG = pGG.toLong()

            if (binding.locationsEnter.selectedItemPosition == 0)
                game.localizationID = null
            else if (locs != null && binding.locationsEnter.selectedItemPosition > 0) {
                game.localizationID = locs!![binding.locationsEnter.selectedItemPosition - 1].id
            }


            withContext(Dispatchers.IO) {

                val db = AppDatabase.getInstance(applicationContext)
                val ids = game.let { db.userDao().insert(it) }
                game.id = ids.toInt()
                saveArtistDesigners()
            }
            binding.progressBarAdd.visibility = View.INVISIBLE
            finish()
        }
    }

    private suspend fun saveArtistDesigners() {
        val db = AppDatabase.getInstance(applicationContext)
        for (i in game.artists) {
            val temp = ArtistsGamesRef(game.id, i.id)
            db.ArtistsGameDAO().insert(temp)
        }
        for (i in game.designers) {
            val temp = DesignersGamesRef(game.id, i.id)
            db.DesignersGameDAO().insert(temp)
        }
    }

    private fun getGamesByName() {
        jobHeader?.cancel()
        val games: ArrayList<BGGHeader> = ArrayList()
        jobHeader = CoroutineScope(Dispatchers.Main).launch {
            binding.progressBarAdd.visibility = View.VISIBLE
            withContext(Dispatchers.IO) {
                if (title != null)
                    BGGapi.searchGamesByTitle(title!!, games)
            }
            binding.progressBarAdd.visibility = View.INVISIBLE
            makeChoiceDialog(games)
        }
    }

    private fun artistDesignersFill() {
        val des = ArrayList<String>()
        for (i in game.designers) {
            des.add("${i.name} ${i.surname}")
        }

        val adapterD = ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_1, des)
        binding.designersEnter.adapter = adapterD

        val art = ArrayList<String>()
        for (i in game.artists) {
            art.add("${i.name} ${i.surname}")
        }

        val adapterA = ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_1, art)
        binding.artistsEnter.adapter = adapterA

        Helpers.strechList(binding.designersEnter)
        Helpers.strechList(binding.artistsEnter)
    }
}