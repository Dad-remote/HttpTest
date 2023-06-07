package pl.sergey.httptest.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_logs")
data class CallLogEntity (

    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val remoteId: Long,
    val startTime: Long,
    val duration: Long,
    val number: String,
    val name: String,
    val timesQuered: Int

)