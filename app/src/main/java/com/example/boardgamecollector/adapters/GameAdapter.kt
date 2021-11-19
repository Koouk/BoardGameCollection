package com.example.boardgamecollector.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import com.example.boardgamecollector.R
import com.example.boardgamecollector.dataModels.gameHeader


open class GameAdapter(context: Context, resource: Int, list: ArrayList<gameHeader>) :
    ArrayAdapter<gameHeader>(context, resource, list) {

    private val mContext: Context = context
    private var moviesList: ArrayList<gameHeader> = list
    private var gamesListFilter : ArrayList<gameHeader> = list

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var listItem = convertView
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_game, parent, false)

        val currentGame = gamesListFilter[position]

        val imageField: ImageView = listItem?.findViewById(R.id.gameImage) as ImageView


        val bitmapImage = currentGame.image
        if (bitmapImage == null) {
            imageField.setImageResource(R.drawable.imgerror)
        }
        else {
            imageField.setImageBitmap(bitmapImage)
        }

        val rankField = listItem.findViewById<TextView>(R.id.textRanking)
        rankField.text = currentGame.ranking.toString()

        val titleField = listItem.findViewById<TextView>(R.id.gameTitleYear)
        val TitleAndYear = currentGame.title + " (" +currentGame.year + ") "
        titleField.text = TitleAndYear

        val descriptionField = listItem.findViewById<TextView>(R.id.gameDescription)
        descriptionField.text = currentGame.description

        return listItem
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                gamesListFilter = results.values as ArrayList<gameHeader> // has the filtered values
                notifyDataSetChanged() // notifies the data with new filtered values
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var constraint = constraint
                val results =
                    FilterResults() // Holds the results of a filtering operation in values
                val FilteredArrList: ArrayList<gameHeader> = ArrayList<gameHeader>()

                if (constraint == null || constraint.length == 0) {

                    // set the Original result to return
                    results.count = moviesList.size
                    results.values = moviesList
                } else {
                    constraint = constraint.toString().toLowerCase()
                    for (i in 0 until moviesList.size) {
                        val data: String? = moviesList.get(i).title
                        if (data != null) {
                            if (data.toLowerCase().contains(constraint.toString())) {
                                FilteredArrList.add(
                                    moviesList[i]
                                )
                            }
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size
                    results.values = FilteredArrList
                }
                return results
            }
        }
    }

    override fun getCount(): Int {
        return gamesListFilter.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}