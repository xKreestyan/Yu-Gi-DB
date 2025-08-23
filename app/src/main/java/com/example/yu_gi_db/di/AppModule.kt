package com.example.yu_gi_db.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.yu_gi_db.data.local.db.YuGiDatabase
import com.example.yu_gi_db.data.local.db.dao.YuGiDAO
import com.example.yu_gi_db.data.remote.ApiClient // IMPORTA LA VERA INTERFACCIA
import com.example.yu_gi_db.data.remote.VolleyApiClientImpl // IMPORTA LA VERA IMPLEMENTAZIONE
import com.example.yu_gi_db.data.remote.repository.YuGiRepo
import com.example.yu_gi_db.domain.repository.YuGiRepoInterface
import com.google.gson.Gson // NUOVO IMPORT per Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRequestQueue(@ApplicationContext context: Context): RequestQueue {
        return Volley.newRequestQueue(context)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson { // NUOVO METODO per fornire Gson
        return Gson()
    }

    @Provides
    @Singleton
    fun provideApiClient(
        requestQueue: RequestQueue, // Iniettato da Hilt
        gson: Gson // Iniettato da Hilt
    ): ApiClient { // Restituisce l'interfaccia
        // Fornisce l'implementazione reale
        return VolleyApiClientImpl(requestQueue, gson)
    }

    @Provides
    @Singleton
    fun provideYuGiDatabase(@ApplicationContext appContext: Context): YuGiDatabase {
        return Room.databaseBuilder(
            appContext,
            YuGiDatabase::class.java,
            "yugi_database"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideYuGiDao(database: YuGiDatabase): YuGiDAO {
        return database.yuGiDao()
    }

    @Provides
    @Singleton
    fun provideYuGiRepo(
        app: Application,
        yuGiDAO: YuGiDAO,
        apiClient: ApiClient,
        requestQueue: RequestQueue
    ): YuGiRepoInterface {
        return YuGiRepo(app, yuGiDAO, apiClient, requestQueue)
    }
}
