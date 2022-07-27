package com.example.hotgists.app.backend

import com.example.hotgists.api.models.GistList
import io.reactivex.Completable
import io.reactivex.Single

interface GistUseCase {
    fun loadGistList(timePassed: Boolean): Single<List<GistList>>
    fun loadGistUser(userName: String): Single<List<GistList>>
    fun deleteAll(): Completable
}

class GistUseCaseImpl(
    private val remoteRepo: GistRepository,
    private val localRepo: LocalGistRepository
) : GistUseCase {

    override fun deleteAll(): Completable =
        localRepo.deleteContent()

    override fun loadGistList(timePassed: Boolean): Single<List<GistList>> {
        return when {
            timePassed -> {
                remoteRepo.getGistList().doAfterSuccess {
                    it?.let { it1 ->
                        saveGists(it1)
                    }
                }
            }
            else -> {
                localRepo.loadGists()
            }
        }
    }

    private fun saveGists(list: List<GistList>): Completable =
        localRepo.storeGists(list)

    override fun loadGistUser(userName: String): Single<List<GistList>> =
        remoteRepo.getGistUser(userName)
}
