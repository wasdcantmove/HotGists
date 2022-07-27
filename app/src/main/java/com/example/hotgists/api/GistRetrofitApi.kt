package com.example.hotgists.api

import com.example.hotgists.BuildConfig
import com.example.hotgists.api.models.Gist
import com.example.hotgists.api.models.GistList
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface GistRetrofitApi {

    companion object {

        val url = if (BuildConfig.DEBUG) {
            "https://api.github.com/"
        } else {
            //live environment
            ""
        }
    }

    @GET("gists/public?since")
    fun getGist(): Single<List<GistList>>

    @GET("users/{userName}/gists?since")
    fun getUserGist(@Path("userName") userName: String): Single<List<GistList>>

    @GET("users/{userName}")
    fun getUser(@Path("userName") userName: String): Single<Gist>


}