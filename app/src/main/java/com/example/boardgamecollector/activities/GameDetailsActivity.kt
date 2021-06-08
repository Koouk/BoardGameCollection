package com.example.boardgamecollector.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.boardgamecollector.R
import com.example.boardgamecollector.dataModels.BGGHeader
import com.example.boardgamecollector.dataModels.gameHeader
import com.example.boardgamecollector.database.*
import com.example.boardgamecollector.databinding.ActivityGameDetailsBinding
import com.example.boardgamecollector.utils.BGGapi
import com.example.boardgamecollector.utils.Helpers
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GameDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameDetailsBinding
    private var fillJob : Job? = null
    private var jobSpinner:Job? = null
    private var initJob:Job? = null
    private var fetchJob:Job? = null
    private var locs: ArrayList<Location>? = null
    private var allArtists : ArrayList<Artists>? = null
    private var allDesigners: ArrayList<Designers>? = null
    private var currentArtists : ArrayList<Artists>? = null
    private var currentDesigners: ArrayList<Designers>? = null

    private  var game : Game? = null
    private var extensions : ArrayList<gameHeader>? = ArrayList<gameHeader>()
    private var db : AppDatabase? = null
    private var temp : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.CancelButton.setOnClickListener{val i = Intent(this, MainActivity::class.java)
            startActivity(i)}
        val extras = intent.extras ?: return
        val id = extras.getString("id")?.toLong()
        EditOff()
        binding.imageButton.setOnClickListener{
            if (temp == 0)
                EditOn()
            else
                EditOff()
        }
        binding.SaveButton.setOnClickListener { saveGame() }
        binding.FetchButton.setOnClickListener { fetchData() }
        binding.delButton.setOnClickListener{ delete()}
        binding.rankEnter.setOnClickListener { rankHistory() }
        binding.addArButton.setOnClickListener { addArtist() }
        binding.RMArButton.setOnClickListener { rmArtist() }
        binding.addDeButton.setOnClickListener { addDesigner() }
        binding.RMDeButton.setOnClickListener { rmDesigner() }
        initData(id)


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
                    val ids =  db.ArtistsDAO().insert(ar)
                    ar.id = ids
                    game?.artists?.add(ar)
                }

                artistDesignersFill()
            }}
        alert.setNegativeButton(getString(R.string.cancel) ){ dialog, whichButton -> dialog.cancel() }
        alert.show()
    }

    private fun rmArtist() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.persons))

        game?.artists?.sortBy {  it.surname }


        val gameString = mutableListOf<String>()
        currentArtists = ArrayList<Artists>()

        game?.artists?.forEach {
            gameString.add("${it.name} ${it.surname}")
            currentArtists!!.add(it)
        }
        allArtists?.forEach{
            if (game?.artists?.contains(it)  == false){
            gameString.add("${it.name} ${it.surname}")
            currentArtists!!.add(it)
            }
        }

        val checkedItems = BooleanArray(gameString.size) {false}
        for (i in game?.artists?.indices!!){
            checkedItems[i] = true
        }
        builder.setMultiChoiceItems(gameString.toTypedArray(), checkedItems) { dialog, which, isChecked ->
            // user checked or unchecked a box
            checkedItems[which] = isChecked
        }

// add OK and Cancel buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            val selected = ArrayList<Artists>()
            for (i in checkedItems.indices)
            {
                if (checkedItems[i])
                    selected.add(currentArtists!![i])

            }
            game?.artists = selected
            artistDesignersFill()
            dialog.cancel()

        })
        builder.setNegativeButton("Cancel"){ dialog, _ ->
            dialog.cancel()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun addDesigner() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.persons))

        game?.designers?.sortBy {  it.surname }


        val gameString = mutableListOf<String>()
        currentDesigners= ArrayList<Designers>()

        game?.designers?.forEach {
            gameString.add("${it.name} ${it.surname}")
            currentDesigners!!.add(it)
        }
        allDesigners?.forEach{
            if (game?.designers?.contains(it)  == false){
                gameString.add("${it.name} ${it.surname}")
                currentDesigners!!.add(it)
            }
        }

        val checkedItems = BooleanArray(gameString.size) {false}
        for (i in game?.designers?.indices!!){
            checkedItems[i] = true
        }
        builder.setMultiChoiceItems(gameString.toTypedArray(), checkedItems) { dialog, which, isChecked ->
            // user checked or unchecked a box
            checkedItems[which] = isChecked
        }

// add OK and Cancel buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            val selected = ArrayList<Designers>()
            for (i in checkedItems.indices)
            {
                if (checkedItems[i])
                    selected.add(currentDesigners!![i])

            }
            game?.designers = selected
            artistDesignersFill()
            dialog.cancel()

        })
        builder.setNegativeButton("Cancel"){ dialog, _ ->
            dialog.cancel()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun rmDesigner() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.persons))

        game?.designers?.sortBy {  it.surname }


        val gameString = mutableListOf<String>()
        currentDesigners = ArrayList<Designers>()

        game?.designers?.forEach {
            gameString.add("${it.name} ${it.surname}")
            currentDesigners!!.add(it)
        }
        allDesigners?.forEach{
            if (game?.designers?.contains(it)  == false){
                gameString.add("${it.name} ${it.surname}")
                currentDesigners!!.add(it)
            }
        }

        val checkedItems = BooleanArray(gameString.size) {false}
        for (i in game?.designers?.indices!!){
            checkedItems[i] = true
        }
        builder.setMultiChoiceItems(gameString.toTypedArray(), checkedItems) { dialog, which, isChecked ->
            // user checked or unchecked a box
            checkedItems[which] = isChecked
        }

// add OK and Cancel buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            val selected = ArrayList<Designers>()
            for (i in checkedItems.indices)
            {
                if (checkedItems[i])
                    selected.add(currentDesigners!![i])

            }
            game?.designers = selected
            artistDesignersFill()
            dialog.cancel()

        })
        builder.setNegativeButton("Cancel"){ dialog, _ ->
            dialog.cancel()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun rankHistory() {
        if (game != null)
            if(game!!.id != null){
                val intent = Intent(this, RankingHistoryActivity::class.java)
                intent.putExtra("id", game!!.id)
                startActivity(intent)
    }
    }

    private fun delete() {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val db = AppDatabase.getInstance(applicationContext)
                game?.let { db.userDao().delete(it) }
                finish()
            }
        }
    }

    private fun fetchData() {
        if (game != null)
        {
            fetchJob = CoroutineScope(Dispatchers.Main).launch {

                game!!.title  = binding.titleEnter.text.toString()
               if (game!!.bggId != 0.toLong())
               {
                   binding.progressBarDetaila.visibility = View.VISIBLE
                   withContext(Dispatchers.IO) {

                       BGGapi.searchGameById(game!!.bggId, game!!)
                       addArtistDesignersIfNotExists()
                   }
                   binding.progressBarDetaila.visibility = View.INVISIBLE
                   fillForm()
               }
                else if (!game!!.title.isNullOrBlank())
               {
                   val gamesH = ArrayList<BGGHeader>()
                   binding.progressBarDetaila.visibility = View.VISIBLE
                   withContext(Dispatchers.IO) {

                       game!!.title?.let { BGGapi.searchGamesByTitle(it, gamesH) }
                       addArtistDesignersIfNotExists()
                   }
                   binding.progressBarDetaila.visibility = View.INVISIBLE
                   makeChoiceDialog(gamesH)

               }
            }
        }
    }

    private fun makeChoiceDialog(games : ArrayList<BGGHeader>) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setIcon(R.drawable.ic_launcher_foreground)
        alertDialog.setTitle("Choose an Item")

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

            CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        if (games[checkedItem].bggID != 0.toLong()) {
                            game?.bggId  = games[checkedItem].bggID
                            BGGapi.searchGameById(games[checkedItem].bggID, game!!)
                        }
                    }
                    fillForm()
                }

            dialog.dismiss()

        })
        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog, which ->
            dialog.cancel()
        }
        val customAlertDialog = alertDialog.create()
        customAlertDialog.show()
    }

    override fun onStop() {
        super.onStop()
        fillJob?.cancel()
        initJob?.cancel()
        fetchJob?.cancel()
        jobSpinner?.cancel()
        fillJob = null
    }

    private fun initData(id: Long?) {
        initJob?.cancel()
        initJob = CoroutineScope(Dispatchers.Main).launch {
            binding.progressBarDetaila.visibility = View.VISIBLE
            withContext(Dispatchers.IO) {

                db = AppDatabase.getInstance(applicationContext)
                game = id?.let { db!!.userDao().getByID(it) }
                extensions = game?.bggId?.let { db!!.userDao().getAllExtensions(it) } as ArrayList<gameHeader>?
                locs = db!!.LocDAO().getAllLocalization() as ArrayList<Location>?
                game?.artists = game?.let { db!!.ArtistsGameDAO().getArtistsOfGame(it.id) } as ArrayList<Artists>
                game?.designers = db!!.DesignersGameDAO().getDesignersOfGame(game!!.id) as ArrayList<Designers>
                allArtists = db!!.ArtistsDAO().getAll() as ArrayList<Artists>
                allDesigners = db!!.DesignersDAO().getAll() as ArrayList<Designers>
                allArtists!!.sortBy { it.surname }
                allDesigners!!.sortBy { it.surname }
            }
            binding.progressBarDetaila.visibility = View.INVISIBLE
            createLocAndTypeList()
            fillForm()
        }
    }

    private fun createLocAndTypeList() {

        val list = arrayListOf<String>()
        locs?.forEach {
            list.add("${it.name} (${it.description})")
        }
        val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, list)
        binding.locationsEnter.adapter = adapter

    }

    private suspend fun fillForm() {

        if (game != null)
        {
            binding.progressBarDetaila.visibility = View.VISIBLE
            //TODO STRING RES
            binding.rankEnter.text = SpannableStringBuilder( "${getString(R.string.rank)} ${game!!.ranking}")

            if (game!!.title != null)
                binding.titleEnter.text = SpannableStringBuilder(game!!.title)
            binding.orgTitleEnter.text = SpannableStringBuilder(game!!.originalTitle)
            if (game!!.releaseYear != null)
                binding.releaseYearEnter.text = SpannableStringBuilder(game!!.releaseYear)

            binding.descriptionEnter.text = SpannableStringBuilder(game!!.description)

            var date = game!!.orderDate
            if(date != null) {
                binding.OrderDatePicker.updateDate(date.year, date.monthValue - 1, date.dayOfMonth)
            }
            date = game!!.addDate
            if(date != null) {
                binding.AddDatePicker.updateDate(date.year, date.monthValue - 1, date.dayOfMonth)
            }

            binding.costEnter.text = SpannableStringBuilder(game!!.cost)
            binding.SCDEnter.text = SpannableStringBuilder(game!!.scd)
            binding.EANEnter.text = SpannableStringBuilder(game!!.ean)
            binding.bggIdEnter.text = SpannableStringBuilder(game!!.bggId.toString())
            binding.productionCodeEnter.text = SpannableStringBuilder(game!!.productionCode)
            binding.typeEnter.text = SpannableStringBuilder(game!!.gameType)
            binding.commentEnter.text = SpannableStringBuilder(game!!.comment)

            if (game!!.ThumbURL != null)
                binding.thumbnailEnter.text = SpannableStringBuilder(game!!.ThumbURL)
            if (game!!.ImgURL != null)
                binding.imgEnter.text = SpannableStringBuilder(game!!.ImgURL)

            val ext = ArrayList<String>()
            if (extensions != null) {
                for (i in extensions!!) {
                    ext.add("${i.title} (${i.year})")
                }

                val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ext)
                binding.ExtEnter.adapter = adapter
                Helpers.strechList(binding.ExtEnter)
                binding.ExtEnter.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                        val intent = Intent(this, GameDetailsActivity::class.java)
                        val id = extensions!![position].id
                        intent.putExtra("id", id.toString())
                        startActivity(intent)
                    }
            }
            if (game!!.gameType == "boardgameexpansion")
                binding.bggParent.visibility = View.VISIBLE
            else
                binding.bggParent.visibility = View.INVISIBLE

            binding.bggParentEnter.text = SpannableStringBuilder(game!!.parentBGG.toString())

            var index = -1
            locs?.forEach{
                if(it.id == game!!.localizationID){
                    index = locs!!.indexOf(it)
                    }
            }
            binding.locationsEnter.setSelection(index)

            artistDesignersFill()

            fillJob?.cancel()
            fillJob = CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {

                game!!.bitmap = game!!.ImgURL?.let { Helpers.getImage(it) }
            }
                binding.imageView2.setImageBitmap(game!!.bitmap)

            }
            binding.progressBarDetaila.visibility = View.INVISIBLE
        }
    }

    private fun artistDesignersFill() {
        val des = ArrayList<String>()
        for (i in game!!.designers) {
            des.add("${i.name} ${i.surname}")
        }

        val adapterD = ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_1, des)
        binding.designersEnter.adapter  = adapterD

        val art = ArrayList<String>()
        for (i in game!!.artists) {
            art.add("${i.name} ${i.surname}")
        }

        val adapterA= ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_1, art)
        binding.artistsEnter.adapter  = adapterA

        Helpers.strechList( binding.designersEnter)
        Helpers.strechList( binding.artistsEnter)
    }

    private fun saveGame() {
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        CoroutineScope(Dispatchers.Main).launch {
            binding.progressBarDetaila.visibility = View.VISIBLE
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

            game?.productionCode = binding.productionCodeEnter.text.toString()
            game?.gameType = binding.typeEnter.text.toString()
            game?.comment = binding.commentEnter.text.toString()
            game?.ThumbURL = binding.thumbnailEnter.text.toString()
            game?.ImgURL = binding.imgEnter.text.toString()

            if (locs != null && binding.locationsEnter.selectedItemPosition > -1)
            {
                game?.localizationID = locs!![binding.locationsEnter.selectedItemPosition].id
            }


            withContext(Dispatchers.IO) {

                val db = AppDatabase.getInstance(applicationContext)
                val ids = game?.let { db.userDao().updateGame(it) }
                saveArtistDesigners()
            }
            binding.progressBarDetaila.visibility = View.INVISIBLE
            Toast.makeText(
                applicationContext, R.string.ToastSave,
                Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun saveArtistDesigners() {
        val db = AppDatabase.getInstance(applicationContext)
        if(game?.artists != null)
            game?.id?.let { db.ArtistsGameDAO().nukeOptionID(it) }
            for (i in game?.artists!!){
                val temp = ArtistsGamesRef(game!!.id,i.id)
                db.ArtistsGameDAO().insert(temp)
            }
        if(game?.designers != null)
            game?.id?.let { db.DesignersGameDAO().nukeOptionID(it) }
            for (i in game?.designers!!){
                val temp = DesignersGamesRef(game!!.id,i.id)
                db.DesignersGameDAO().insert(temp)
            }
    }

    private suspend fun addArtistDesignersIfNotExists() {
        val db = AppDatabase.getInstance(applicationContext)
        if(game?.artists != null )
            for (i in game?.artists!!)
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
        if(game?.designers != null )
            for (i in game?.designers!!)
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

    private fun EditOn(){
        temp = 1
        binding.imageButton.background = getDrawable(R.drawable.red_border)
        binding.titleEnter.isFocusableInTouchMode = true
        binding.orgTitleEnter.isFocusableInTouchMode = true
        binding.releaseYearEnter.isFocusableInTouchMode = true
        binding.descriptionEnter.isFocusableInTouchMode = true
        binding.costEnter.isFocusableInTouchMode = true
        binding.SCDEnter.isFocusableInTouchMode = true
        binding.EANEnter.isFocusableInTouchMode = true
        binding.productionCodeEnter.isFocusableInTouchMode = true
        binding.typeEnter.isFocusableInTouchMode = true
        binding.commentEnter.isFocusableInTouchMode = true
        binding.thumbnailEnter.isFocusableInTouchMode = true
        binding.imgEnter.isFocusableInTouchMode = true

    }

    private fun EditOff(){
        temp = 0
        binding.imageButton.background = null
        binding.titleEnter.isFocusableInTouchMode = false
        binding.orgTitleEnter.isFocusableInTouchMode = false
        binding.releaseYearEnter.isFocusableInTouchMode = false
        binding.descriptionEnter.isFocusableInTouchMode = false
        binding.costEnter.isFocusableInTouchMode = false
        binding.SCDEnter.isFocusableInTouchMode = false
        binding.EANEnter.isFocusableInTouchMode = false
        binding.productionCodeEnter.isFocusableInTouchMode = false
        binding.typeEnter.isFocusableInTouchMode = false
        binding.commentEnter.isFocusableInTouchMode = false
        binding.thumbnailEnter.isFocusableInTouchMode = false
        binding.imgEnter.isFocusableInTouchMode = false

    }



}