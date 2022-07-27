package com.example.hotgists.app.backend

import com.example.hotgists.api.models.GistList
import io.reactivex.Completable
import io.reactivex.Single

interface LocalGistRepository {

    fun deleteContent(): Completable
    fun storeGists(list: List<GistList>): Completable
    fun loadSingleGist(selectedGist: String): Single<List<GistList>>
    fun loadGists(): Single<List<GistList>>

}