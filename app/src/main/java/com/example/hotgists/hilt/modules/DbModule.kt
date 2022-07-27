package com.example.hotgists.hilt.modules

import android.content.Context
import androidx.room.Room
import com.example.hotgists.app.db.GistDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DbModule {

    @Singleton
    @Provides
    fun provideGistDatabase(
        @ApplicationContext app: Context
    ): GistDatabase = Room.databaseBuilder(
        app,
        GistDatabase::class.java,
        GistDatabase.DATABASE_NAME
    )
        .fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun provideContentDao(db: GistDatabase) = db.gistDao()

}