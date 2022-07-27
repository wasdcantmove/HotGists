package com.example.hotgists.app.backend

import com.example.hotgists.api.models.GistList
import com.example.hotgists.app.db.GistDatabase
import com.example.hotgists.app.db.models.DbGist
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocalGistRepositoryImpl(private val db: GistDatabase) :
    LocalGistRepository {

    override fun loadSingleGist(selectedGist: String): Single<List<GistList>> =
        db.gistDao().loadGist().map { dbGist ->
            dbGist.gists.filter { it.id == selectedGist }
        }

    override fun loadGists(): Single<List<GistList>> =
        db.gistDao().loadGist().map { it.gists }

    @OptIn(DelicateCoroutinesApi::class)
    override fun storeGists(list: List<GistList>): Completable {
        GlobalScope.launch {
            db.gistDao().insertGist(DbGist(1, list))
        }
        return Completable.complete()
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun deleteContent(): Completable {
        GlobalScope.launch {
            db.gistDao().deleteAll()
        }
        return Completable.complete()
    }

}

