package com.example.hotgists.app.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hotgists.api.models.GistList

@Entity(tableName = "Gist")
data class DbGist(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = false) val id: Int? = 1,
    @ColumnInfo(name = "gistList") val gists: List<GistList>,
)
