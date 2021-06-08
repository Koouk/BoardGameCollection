package com.example.boardgamecollector.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ListView
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class Helpers :ViewModel(){


    companion object {

        val GAME_TYPE = arrayListOf("boardgame","boardgameexpansion","mixed")

        suspend fun getImage(link: String): Bitmap? {

            var image: Bitmap? = null
            var newLink = link

            withContext(Dispatchers.IO) {
                try{
                    if (!link.startsWith("http://") && !link.startsWith("https://"))
                    {
                        newLink = "http://$link"
                    }
                    val url = URL(newLink)
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                }
                catch (e: Exception) {
                    Log.e("Error Message", e.message.toString())
                    e.printStackTrace()
                }
            }
            return image
        }

        // https://stackoverflow.com/questions/1778485/android-listview-display-all-available-items-without-scroll-with-static-header/1958482
        fun strechList(listView: ListView) {
            val listAdapter = listView.adapter
            if (listAdapter != null) {
                val numberOfItems = listAdapter.count

                // Get total height of all items.
                var totalItemsHeight = 0
                for (itemPos in 0 until numberOfItems) {
                    val item = listAdapter.getView(itemPos, null, listView)
                    item.measure(0, 0)
                    totalItemsHeight += item.measuredHeight
                }

                // Get total height of all item dividers.
                val totalDividersHeight = listView.dividerHeight *
                        (numberOfItems - 1)

                // Set list height.
                val params = listView.layoutParams
                params.height = totalItemsHeight + totalDividersHeight
                listView.layoutParams = params
                listView.requestLayout()
            }
        }

    }
}