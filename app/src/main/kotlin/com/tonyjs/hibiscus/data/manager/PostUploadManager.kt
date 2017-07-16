package com.tonyjs.hibiscus.data.manager

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.tonyjs.hibiscus.data.model.ImageBody
import com.tonyjs.hibiscus.data.model.TextBody

//TODO
class PostUploadManager {

    fun toTextContent(textBody: TextBody): String {
        val json = JsonObject().apply {
            addProperty("tag", "p")
            add("children", JsonArray().apply { add(textBody.text) })
        }
        return Gson().toJson(json)
    }

    fun toImageContent(imageBody: ImageBody): String {
        val json = JsonObject().apply {
            addProperty("tag", "img")
            add("attrs", JsonObject().apply { addProperty("src", imageBody.externalPath) })
        }
        return Gson().toJson(json)
    }

}