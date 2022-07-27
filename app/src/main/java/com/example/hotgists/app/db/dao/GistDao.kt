package com.example.hotgists.app.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hotgists.app.db.models.DbGist
import io.reactivex.Single

@Dao
interface GistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGist(gist: DbGist)

    @Query("SELECT * FROM Gist ORDER BY id")
    fun loadGist(): Single<DbGist>

    @Query("SELECT * FROM Gist ORDER BY id")
    fun loadGistSingle(): Single<DbGist>

    @Query("DELETE FROM Gist")
    fun deleteAll()

}
