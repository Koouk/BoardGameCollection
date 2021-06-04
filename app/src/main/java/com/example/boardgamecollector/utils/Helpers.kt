package com.example.boardgamecollector.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import kotlinx.coroutines.async

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
                        newLink =  "http://" + link;
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

    }
}