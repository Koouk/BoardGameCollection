package com.example.boardgamecollector.adapters

import android.content.Context;
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.boardgamecollector.database.Game
import com.example.boardgamecollector.R
import com.example.boardgamecollector.dataModels.gameHeader


open class GameAdapter(context: Context,resource : Int , list: ArrayList<gameHeader>) :
    ArrayAdapter<gameHeader>(context, resource, list) {

    private val mContext: Context
    private var moviesList: ArrayList<gameHeader>


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var listItem = convertView
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_game, parent, false)

        val currentGame = moviesList[position]

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

    init {
        mContext = context
        moviesList = list
    }
}