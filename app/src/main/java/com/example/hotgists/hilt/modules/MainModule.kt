package com.example.hotgists.hilt.modules

import com.example.hotgists.api.GistRetrofitApi
import com.example.hotgists.app.backend.*
import com.example.hotgists.app.db.GistDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class MainModule {

    @Provides
    fun providesLocalContentRepository(
        database: GistDatabase
    ): LocalGistRepository =
        LocalGistRepositoryImpl(database)

    @Provides
    fun providesContentRepository(
        api: GistRetrofitApi
    ): GistRepository =
        GistRepositoryImpl(api)

    @Provides
    fun provideContentUseCase(
        contentRepository: GistRepository,
        localContentRepository: LocalGistRepository
    ): GistUseCase =
        GistUseCaseImpl(
            contentRepository,
            localContentRepository
        )


}
