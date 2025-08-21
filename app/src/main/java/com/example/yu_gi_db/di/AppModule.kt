package com.example.yu_gi_db.di

import android.app.Application
import com.example.yu_gi_db.data.remote.YuGiAPI
import com.example.yu_gi_db.data.remote.YuGiAPIInterface
import com.example.yu_gi_db.data.remote.repository.YuGiRepo
import com.example.yu_gi_db.domain.repository.YuGiRepoInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideYuGiAPIInterface(api: YuGiAPI): YuGiAPIInterface = api

    @Provides
    @Singleton
    fun provideYuGiRepo(api: YuGiAPIInterface, app: Application): YuGiRepoInterface = YuGiRepo(api, app)
}
