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
        fun getImage(link: String): Bitmap? {

            var image: Bitmap? = null

            var job = CoroutineScope(Main).async {
                try {

                    withContext(Dispatchers.IO) {
                        val url = URL(link)
                        image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    }

                } catch (e: Exception) {
                    Log.e("Error Message", e.message.toString())
                    e.printStackTrace()
                }
            }
            return image
        }

    }
}