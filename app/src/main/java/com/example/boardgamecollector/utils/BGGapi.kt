package com.example.boardgamecollector.utils

import android.text.Html
import android.util.Log
import com.example.boardgamecollector.dataModels.BGGHeader
import com.example.boardgamecollector.database.Artists
import com.example.boardgamecollector.database.Designers
import com.example.boardgamecollector.database.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class BGGapi {
    companion object {

        suspend fun searchGamesByTitle(title: String, gameList: ArrayList<BGGHeader>) {
            val docBuilderFact = DocumentBuilderFactory.newInstance()
            val doc = docBuilderFact.newDocumentBuilder()
            var document : Document
            try{
                 withContext(Dispatchers.IO) {
                    document = doc.parse(
                        URL(
                            "https://www.boardgamegeek.com/xmlapi2/search?query=${title}&type=boardgame"
                        ).openStream()
                    )
                }
            }catch(e : Exception){
                Log.e("Error: ", e.message.toString())
                e.printStackTrace()
                return
            }
            val nodes = document.getElementsByTagName("item")
            for (i in 0 until nodes.length) {
                try {
                    val BGGid = nodes.item(i).attributes.getNamedItem("id").nodeValue.toString().toLong()
                    val name = nodes.item(i).childNodes.item(1).attributes.getNamedItem("value").nodeValue.toString()
                    var year = nodes.item(i).childNodes.item(3)?.attributes?.getNamedItem("value")?.nodeValue?.toString()
                    if (year == null)
                        year = ""

                    gameList.add(BGGHeader(BGGid, name, year))
                } catch (e : Exception) {
                    Log.e("BGG ERROR ", e.message.toString())
                    e.printStackTrace()
                }
            }
        }

        suspend fun searchGameById(id : Long, game : Game){
            val docBuilderFact = DocumentBuilderFactory.newInstance()
            val doc = docBuilderFact.newDocumentBuilder()
            var document : Document
            try{
                withContext(Dispatchers.IO) {
                    document = doc.parse(
                        URL(
                            "https://www.boardgamegeek.com/xmlapi2/thing?stats=1&id=${id}"
                        ).openStream()
                    )
                }
            }catch(e : Exception){
                Log.e("Error: ", e.message.toString())
                e.printStackTrace()
                return
            }

            var orgName :String = ""
            var thumb :String? = null
            var img: String? = null
            var desc :String = ""
            var rank :Int = 0
            var type :String = ""
            var baseGameId : Long? = null
            var art : ArrayList<Artists> = ArrayList<Artists>()
            var des : ArrayList<Designers> = ArrayList<Designers>()
            try {
                orgName = document.getElementsByTagName("name").item(0).attributes.getNamedItem("value").nodeValue
                thumb = document.getElementsByTagName("thumbnail")?.item(0)?.textContent
                img = document.getElementsByTagName("image")?.item(0)?.textContent
                desc = document.getElementsByTagName("description").item(0).textContent
                desc = Html.fromHtml(desc).toString()
                type = document.getElementsByTagName("item").item(0).attributes.getNamedItem("type").nodeValue


                for(i in 0 until document.getElementsByTagName("link").length) {
                    val a = document.getElementsByTagName("link").item(i).attributes
                    val v = a.getNamedItem("type")?.nodeValue

                    if(v == "boardgameexpansion") {
                        if (a.getNamedItem("inbound")?.nodeValue?.toString() == "true") {
                            val bid = a.getNamedItem("id")?.nodeValue?.toLong()
                            baseGameId = bid

                        }
                    }
                    if (v == "boardgamedesigner")
                    {
                        val value = a.getNamedItem("value")?.nodeValue
                        val idIem = a.getNamedItem("id")?.nodeValue

                        if(value != null) {
                            val (n, s) = value.toString().split(' ')
                            var idd = -1L
                            if (!idIem.isNullOrBlank())
                                idd = idIem.toString().toLong()
                            des.add(Designers(0,n,s,idd))
                        }
                    }
                    if (v == "boardgameartist")
                    {
                        val value = a.getNamedItem("value")?.nodeValue
                        val idIem = a.getNamedItem("id")?.nodeValue

                        if(value != null) {
                            val (n, s) = value.toString().split(' ')
                            var idd = -1L
                            if (!idIem.isNullOrBlank())
                                idd = idIem.toString().toLong()
                            art.add(Artists(0,n,s,idd))
                        }
                    }
                }

                for(i in 0 until document.getElementsByTagName("rank").length) {
                    val ranks = document.getElementsByTagName("rank").item(i).attributes
                    if(ranks.getNamedItem("name")?.nodeValue == "boardgame" &&
                        ranks.getNamedItem("value").nodeValue.toString() != "Not Ranked") {
                            rank = ranks.getNamedItem("value").nodeValue.toString().toInt()
                            break
                    }
                }
            }
            catch (e : Exception) {
                Log.e("BGG ERROR ", e.message.toString())
                e.printStackTrace()
            }

            game.originalTitle = orgName
            game.ThumbURL = thumb
            game.description = desc
            game.ranking = rank
            game.gameType = type
            game.ImgURL = img
            game.parentBGG = baseGameId
            game.artists = art
            game.designers = des

            if(game.releaseYear.isNullOrBlank())
                game.releaseYear = document.getElementsByTagName("yearpublished").item(0)?.attributes?.getNamedItem("value")?.nodeValue.toString()

            if(game.bggId == 0L)
                game.bggId = document.getElementsByTagName("item").item(0).attributes.getNamedItem("id").nodeValue.toString().toLong()


        }

    }
}