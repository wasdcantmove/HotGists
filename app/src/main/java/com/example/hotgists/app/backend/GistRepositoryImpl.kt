package com.example.hotgists.app.backend

import com.example.hotgists.api.GistRetrofitApi
import com.example.hotgists.api.models.GistList
import io.reactivex.Single

class GistRepositoryImpl(private val retrofitApi: GistRetrofitApi) :
    GistRepository {

    override fun getGistList(): Single<List<GistList>> =
        retrofitApi.getGist()

    override fun getGistUser(userName: String): Single<List<GistList>> =
        retrofitApi.getUserGist(userName)


}