package com.example.yu_gi_db.di

import android.app.Application
import android.content.Context // Aggiunto per Room
import androidx.room.Room // Aggiunto per Room
import com.example.yu_gi_db.data.local.db.YuGiDatabase // Aggiunto
import com.example.yu_gi_db.data.local.db.dao.YuGiDAO // Aggiunto
import com.example.yu_gi_db.data.remote.repository.YuGiRepo
import com.example.yu_gi_db.domain.repository.YuGiRepoInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext // Aggiunto per ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideYuGiDatabase(@ApplicationContext appContext: Context): YuGiDatabase {
        return Room.databaseBuilder(
            appContext,
            YuGiDatabase::class.java,
            "yugi_database"
        )
            .fallbackToDestructiveMigration(true) // Ricorda: per sviluppo, ok. Per produzione, usa migrazioni.
        .build()
    }

    @Provides
    @Singleton // Il DAO sara' un singleton dato che il Database lo e'
    fun provideYuGiDao(database: YuGiDatabase): YuGiDAO {
        return database.yuGiDao()
    }

    @Provides
    @Singleton
    fun provideYuGiRepo(app: Application, yuGiDAO: YuGiDAO): YuGiRepoInterface { // Aggiunto yuGiDAO come parametro
        // Qui dovrai aggiornare il costruttore di YuGiRepo per accettare YuGiDAO
        // Esempio: return YuGiRepo(app, yuGiDAO)
        // Per ora, lascio il costruttore originale come placeholder, ma dovra' essere modificato
        return YuGiRepo(app, yuGiDAO) // ASSUNZIONE: YuGiRepo verra' modificato per accettare YuGiDAO
    }
}
