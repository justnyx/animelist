package com.animelist.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

enum class WatchStatus {
    NONE,
    COMPLETED,
    WATCHING,
    PLAN_TO_WATCH
}

//simplified version of AnimeData
@Serializable
data class Anime(
    val id: Int,
    val title: String,
    val type: String,
    val episodeCount: Int,
    val status: String,
    val season: String,
    val year: Int?,
    val picture: String,
    val tags: List<String>,
    var watchStatus: WatchStatus
)

//represents the way the animes are stored in the JSON file
//only used to parse the JSON file
@Serializable
data class AnimeData(
    val data: List<Data>
) {
    @Serializable
    data class Data(
        val sources: List<String>,
        val title: String,
        val type: String,
        val episodes: Int,
        val status: String,
        val animeSeason: AnimeSeason,
        val picture: String,
        val thumbnail: String,
        val synonyms: List<String>,
        val relations: List<String>,
        val tags: List<String>
    ) {
        @Serializable
        data class AnimeSeason(
            val season: String,
            val year: Int?
        )
    }
}

//parses the JSON file
//returns a List<Anime> with all animes, that are stored in the JSON file
fun parseAnime(): List<Anime> {
    val jsonString = File("src/main/resources/anime-offline-database.json").readText()
    val animeData: AnimeData = Json.decodeFromString(jsonString)

    val animeList = mutableListOf<Anime>()
    var id = 0

    animeData.data.forEach {
        val anime = Anime(id, it.title, it.type, it.episodes, it.status,
            it.animeSeason.season, it.animeSeason.year ?: 0, it.picture, it.tags, WatchStatus.NONE
        )
        animeList.add(anime)
        id++
    }

    return animeList
}

//converts a String, in the format of anime_list in the database, to List<Anime>
fun String?.toAnimeList(): List<Anime> {
    var animeList = listOf<Anime>()

    //stores the characters corresponding to the WatchStatuses
    val watchStatusMap = mapOf(
        "c" to WatchStatus.COMPLETED,
        "w" to WatchStatus.WATCHING,
        "p" to WatchStatus.PLAN_TO_WATCH)

    if(!this.isNullOrBlank()) {
        val allAnime = parseAnime()
        val animeMap = mutableMapOf<Int, String>()

        //puts all ids and their corresponding WatchStatus into animeMap
        this.split(",")
            .forEach {
                animeMap[it.split(":")[0].toInt()] = it.split(":")[1]
            }

        //puts all animes, whose id is in animeMap into animeList
        animeList = allAnime
            .filter {
                it.id in animeMap.keys
            }

        //sets all WatchStatuses in animeList, to the corresponding WatchStatus in animeMap
        animeList.forEach {
            it.watchStatus = watchStatusMap[animeMap[it.id]]!!
        }
    }

    return animeList
}

//converts a List<Anime> into a String, in the format of anime_list in the database
fun List<Anime>.toAnimeIds(): String {
    val animeMap = sortedMapOf<Int,String>()
    var ids = ""

    //puts all ids with their corresponding WatchStatus characters into animeMap
    this.forEach {
        animeMap[it.id] = WatchStatusRequest(it.watchStatus).toString()
    }

    //adds each id:WatchStatus character pair to ids
    animeMap.forEach {
        ids += if (it.key != animeMap.lastKey()) {
            "${it.key}:${it.value},"
        } else {
            "${it.key}:${it.value}"
        }
    }

    return ids
}