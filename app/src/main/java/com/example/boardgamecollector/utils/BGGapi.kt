package com.example.boardgamecollector.utils

import android.text.Html
import android.util.Log
import com.example.boardgamecollector.dataModels.BGGHeader
import com.example.boardgamecollector.dataModels.RanksHeader
import com.example.boardgamecollector.database.Artists
import com.example.boardgamecollector.database.Designers
import com.example.boardgamecollector.database.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class BGGapi {
    companion object {

        suspend fun searchGamesByTitle(title: String, gameList: ArrayList<BGGHeader>) {
            val docBuilderFact = DocumentBuilderFactory.newInstance()
            val doc = docBuilderFact.newDocumentBuilder()
            var document: Document
            try {
                withContext(Dispatchers.IO) {
                    document = doc.parse(
                        URL(
                            "https://www.boardgamegeek.com/xmlapi2/search?query=${title}&type=boardgame"
                        ).openStream()
                    )
                }
            } catch (e: Exception) {
                Log.e("Error: ", e.message.toString())
                e.printStackTrace()
                return
            }
            val nodes = document.getElementsByTagName("item")
            for (i in 0 until nodes.length) {
                try {
                    val BGGid = nodes.item(i).attributes.getNamedItem("id").nodeValue.toString().toLong()
                    val name = nodes.item(i).childNodes.item(1).attributes.getNamedItem("value").nodeValue.toString()
                    var year =
                        nodes.item(i).childNodes.item(3)?.attributes?.getNamedItem("value")?.nodeValue?.toString()
                    if (year == null)
                        year = ""

                    gameList.add(BGGHeader(BGGid, name, year))
                } catch (e: Exception) {
                    Log.e("BGG ERROR ", e.message.toString())
                    e.printStackTrace()
                }
            }
        }

        suspend fun searchGameById(id: Long, game: Game) {
            val docBuilderFact = DocumentBuilderFactory.newInstance()
            val doc = docBuilderFact.newDocumentBuilder()
            var document: Document
            try {
                withContext(Dispatchers.IO) {
                    document = doc.parse(
                        URL(
                            "https://www.boardgamegeek.com/xmlapi2/thing?stats=1&id=${id}"
                        ).openStream()
                    )
                }
            } catch (e: Exception) {
                Log.e("Error: ", e.message.toString())
                e.printStackTrace()
                return
            }

            var orgName = ""
            var thumb: String? = null
            var img: String? = null
            var desc = ""
            var rank = 0
            var type = ""
            var baseGameId: Long? = null
            val art: ArrayList<Artists> = ArrayList<Artists>()
            val des: ArrayList<Designers> = ArrayList<Designers>()
            try {
                orgName = document.getElementsByTagName("name").item(0).attributes.getNamedItem("value").nodeValue
                thumb = document.getElementsByTagName("thumbnail")?.item(0)?.textContent
                img = document.getElementsByTagName("image")?.item(0)?.textContent
                desc = document.getElementsByTagName("description").item(0).textContent
                desc = Html.fromHtml(desc).toString()
                type = document.getElementsByTagName("item").item(0).attributes.getNamedItem("type").nodeValue


                for (i in 0 until document.getElementsByTagName("link").length) {
                    val a = document.getElementsByTagName("link").item(i).attributes
                    val v = a.getNamedItem("type")?.nodeValue

                    if (v == "boardgameexpansion") {
                        if (a.getNamedItem("inbound")?.nodeValue?.toString() == "true") {
                            val bid = a.getNamedItem("id")?.nodeValue?.toLong()
                            baseGameId = bid

                        }
                    }
                    if (v == "boardgamedesigner") {
                        val value = a.getNamedItem("value")?.nodeValue
                        val idIem = a.getNamedItem("id")?.nodeValue

                        if (value != null) {
                            val (n, s) = value.toString().split(' ')
                            var idd = -1L
                            if (!idIem.isNullOrBlank())
                                idd = idIem.toString().toLong()
                            des.add(Designers(0, n, s, idd))
                        }
                    }
                    if (v == "boardgameartist") {
                        val value = a.getNamedItem("value")?.nodeValue
                        val idIem = a.getNamedItem("id")?.nodeValue

                        if (value != null) {
                            val (n, s) = value.toString().split(' ')
                            var idd = -1L
                            if (!idIem.isNullOrBlank())
                                idd = idIem.toString().toLong()
                            art.add(Artists(0, n, s, idd))
                        }
                    }
                }

                for (i in 0 until document.getElementsByTagName("rank").length) {
                    val ranks = document.getElementsByTagName("rank").item(i).attributes
                    if (ranks.getNamedItem("name")?.nodeValue == "boardgame" &&
                        ranks.getNamedItem("value").nodeValue.toString() != "Not Ranked"
                    ) {
                        rank = ranks.getNamedItem("value").nodeValue.toString().toInt()
                        break
                    }
                }
            } catch (e: Exception) {
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

            if (game.releaseYear.isNullOrBlank())
                game.releaseYear = document.getElementsByTagName("yearpublished")
                    .item(0)?.attributes?.getNamedItem("value")?.nodeValue.toString()

            if (game.bggId == 0L)
                game.bggId =
                    document.getElementsByTagName("item").item(0).attributes.getNamedItem("id").nodeValue.toString()
                        .toLong()


        }

        suspend fun getGameRank(game: RanksHeader) {
            val docBuilderFact = DocumentBuilderFactory.newInstance()
            val doc = docBuilderFact.newDocumentBuilder()
            var document: Document
            try {
                withContext(Dispatchers.IO) {
                    document = doc.parse(
                        URL(
                            "https://www.boardgamegeek.com/xmlapi2/thing?stats=1&id=${game.bggId}"
                        ).openStream()
                    )
                }
            } catch (e: Exception) {
                Log.e("Error: ", e.message.toString())
                e.printStackTrace()
                return
            }

            for (i in 0 until document.getElementsByTagName("rank").length) {
                val ranks = document.getElementsByTagName("rank").item(i).attributes
                if (ranks.getNamedItem("name")?.nodeValue == "boardgame" &&
                    ranks.getNamedItem("value").nodeValue.toString() != "Not Ranked"
                ) {
                    game.ranking = ranks.getNamedItem("value").nodeValue.toString().toInt()
                    break
                }
            }
        }

        suspend fun getUserGameList(user: String, gameList: ArrayList<BGGHeader>) {
            val docBuilderFact = DocumentBuilderFactory.newInstance()
            val doc = docBuilderFact.newDocumentBuilder()
            var document: Document
            var success = false
            val attempts = 3
            var curr = 0
            while (true) {
                curr += 1
                try {
                    document = withContext(Dispatchers.IO) {
                        doc.parse(
                            URL(
                                "https://www.boardgamegeek.com/xmlapi2/collection?username=${user}&stats=1"
                            ).openStream()
                        )
                    }
                } catch (e: Exception) {
                    Log.e("Error: ", e.message.toString())
                    e.printStackTrace()
                    return
                }
                if (document.getElementsByTagName("message")
                        ?.item(0)?.textContent?.contains("Please try again later for access.") != true
                ) {
                    success = true
                    break
                }
                if (curr == attempts)
                    return
                delay(10000)

            }
            if (!success)
                return

            val games = document.getElementsByTagName("item")
            for (i in 0 until games.length) {
                var id: Long = 0
                var title = ""
                var year = ""

                try {

                    id = games.item(i).attributes.getNamedItem("objectid").nodeValue.toString().toLong()
                    val childrens = games.item(i).childNodes

                    for (j in 0 until childrens.length) {

                        if (childrens.item(j).nodeName == "originalname") {
                            title = childrens.item(j).textContent.toString()
                        } else if (childrens.item(j).nodeName == "yearpublished") {
                            year = childrens.item(j).textContent.toString()
                        } else if (childrens.item(j).nodeName == "name") {
                            if (title.isBlank()) {
                                title = childrens.item(j).textContent.toString()
                            }
                        }
                    }
                    gameList.add(BGGHeader(id, title, year))

                } catch (e: Exception) {
                    Log.e("BGG ERROR ", e.message.toString())
                    e.printStackTrace()
                }


            }

        }
    }
}