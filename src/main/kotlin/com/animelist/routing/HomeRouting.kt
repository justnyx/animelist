package com.animelist.routing

import com.animelist.models.parseAnime
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.homeRouting() {
    val animeList = parseAnime()

    routing {
        get("/home") {
            var responseList = animeList
            if (call.request.queryParameters.contains("sortby")) {
                //sorts the list by the given query parameter value
                when (call.request.queryParameters["sortby"]) {
                    "title" -> responseList = responseList.sortedBy { it.title }
                    "year" -> responseList = responseList.sortedByDescending { it.year }
                    "episodes" -> responseList = responseList.sortedBy { it.episodeCount }
                }
            }

            //filters the list by the given query parameter value
            if (call.request.queryParameters.contains("title")) {
                val title = call.request.queryParameters["title"]
                responseList = responseList.filter { it.title.contains(title!!, ignoreCase = true) }
            }

            //filters the list by the given query parameter value
            if (call.request.queryParameters.contains("tag")) {
                val tag = call.request.queryParameters["tag"]
                responseList = responseList.filter { it.tags.contains(tag!!) }
            }

            call.respond(responseList)
        }

        get("/home/{id}") {
            val id = call.parameters["id"]?.toInt() ?: -1
            call.respond(animeList[id])
        }
    }
}