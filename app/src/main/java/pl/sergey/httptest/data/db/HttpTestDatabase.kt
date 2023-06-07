package pl.sergey.httptest.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pl.sergey.httptest.data.db.dao.CallLogDao
import pl.sergey.httptest.data.db.model.CallLogEntity

@Database(entities = [CallLogEntity::class], version = 1)
abstract class HttpTestDatabase : RoomDatabase() {

    abstract fun callLogDao(): CallLogDao

    companion object {
        fun build(applicationContext: Context) = Room.databaseBuilder(
            applicationContext,
            HttpTestDatabase::class.java, "http-database"
        ).fallbackToDestructiveMigration().build()
    }

}