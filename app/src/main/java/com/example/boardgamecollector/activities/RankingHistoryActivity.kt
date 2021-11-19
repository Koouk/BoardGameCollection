package com.example.boardgamecollector.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.boardgamecollector.R
import com.example.boardgamecollector.database.AppDatabase
import com.example.boardgamecollector.database.RankHistory
import com.example.boardgamecollector.databinding.ActivityRankingHistoryBinding
import kotlinx.coroutines.*

class RankingHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRankingHistoryBinding
    private var listJob : Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingHistoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val extras = intent.extras
        if(extras != null) {
            val id = extras.getInt("id")
            createRankHistory(id)
        } else {
            this.finish()
        }
    }

    override fun onStop() {
        super.onStop()
        listJob?.cancel()
    }

    private fun createRankHistory(id: Int) {
        val con = this
        listJob = CoroutineScope(Dispatchers.Main).launch {
            var ranks = ArrayList<RankHistory>()
            withContext(Dispatchers.IO) {

                val db = AppDatabase.getInstance(applicationContext)
                ranks = db.RankDAO().getRankById(id) as ArrayList<RankHistory>
            }
            ranks.sortBy{ it.untilDate }
            val rankList = mutableListOf<String>()

            for (i in ranks)
            {
                rankList.add( "${getString(R.string.ranking)}: ${i.rank}, ${getString(R.string.Until)} ${i.untilDate}" )
            }
            if (ranks.size == 0)
            {
                rankList.add( getString(R.string.nohist))
            }
            val adapter = ArrayAdapter<String>(con, android.R.layout.simple_list_item_1, rankList.asReversed())
            binding.rankList.adapter = adapter
        }
    }
}