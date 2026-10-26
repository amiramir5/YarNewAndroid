package com.aistudio.konkooryar

import android.app.Application
import com.aistudio.konkooryar.data.local.KonkoorDatabase
import com.aistudio.konkooryar.data.repository.KonkoorRepository

class KonkoorYarApp : Application() {
    val database by lazy { KonkoorDatabase.getDatabase(this) }
    val repository by lazy { KonkoorRepository(database.konkoorDao()) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: KonkoorYarApp
            private set
    }
}
