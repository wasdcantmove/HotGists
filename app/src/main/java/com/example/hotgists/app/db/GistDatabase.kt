package com.example.hotgists.app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.hotgists.app.db.converters.GistConverter
import com.example.hotgists.app.db.converters.GistListConverter
import com.example.hotgists.app.db.dao.GistDao
import com.example.hotgists.app.db.models.DbGist

const val DB_VERSION = 1

@TypeConverters(
    GistListConverter::class,
    GistConverter::class
)

@Database(
    entities = [DbGist::class], version = DB_VERSION
)
abstract class GistDatabase : RoomDatabase() {

    abstract fun gistDao(): GistDao

    companion object {
        const val DATABASE_NAME: String = "gist-database"
    }
}