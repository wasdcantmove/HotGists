package com.example.hotgists.app.db.converters

import androidx.room.TypeConverter
import com.example.hotgists.api.models.GistList
import com.google.gson.Gson

object GistListConverter {

    @JvmStatic
    @TypeConverter
    fun listToJson(value: List<GistList?>?): String {
        return Gson().toJson(value)
    }

    @JvmStatic
    @TypeConverter
    fun jsonToList(value: String): List<GistList> {
        return Gson()
            .fromJson(value, Array<GistList>::class.java)
            .let { it as Array<GistList> }
            .toList()

    }
}

