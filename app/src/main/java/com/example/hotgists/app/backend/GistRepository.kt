package com.example.hotgists.app.backend

import com.example.hotgists.api.models.GistList
import io.reactivex.Single

interface GistRepository {
    fun getGistList(): Single<List<GistList>>
    fun getGistUser(userName: String): Single<List<GistList>>
}