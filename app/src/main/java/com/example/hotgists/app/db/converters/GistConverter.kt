package com.example.hotgists.app.db.converters

import androidx.room.TypeConverter
import com.example.hotgists.api.models.Gist
import com.google.gson.Gson

object GistConverter {

    @JvmStatic
    @TypeConverter
    fun listToJson(value: Gist?): String {
        return Gson().toJson(value)
    }

    @JvmStatic
    @TypeConverter
    fun jsonToList(value: String): Gist {
        return Gson()
            .fromJson(value, Gist::class.java)
            .let { it as Gist }

    }
}

