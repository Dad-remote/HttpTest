package pl.sergey.httptest.data.support

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import pl.sergey.httptest.data.db.model.CallLogEntity
import java.util.LinkedList

class CallLogFetcher(private val context: Context) {

    private var startTime = System.currentTimeMillis()

    private val projection = arrayOf(
        CallLog.Calls._ID,
        CallLog.Calls.CACHED_NAME,
        CallLog.Calls.CACHED_FORMATTED_NUMBER,
        CallLog.Calls.NUMBER,
        CallLog.Calls.TYPE,
        CallLog.Calls.DATE,
        CallLog.Calls.DURATION
    )

    fun getCallLog() : List<CallLogEntity> {
        val result = LinkedList<CallLogEntity>()
        val cursor: Cursor? = context.contentResolver.query(CallLog.Calls.CONTENT_URI, projection, "${CallLog.Calls.DATE} >= ${startTime}", null, null)
        while (cursor?.moveToNext() == true) {
            result.add(toCallLogEntity(cursor))
        }
        cursor?.close()
        return result
    }

    fun getLast() : CallLogEntity? {
        val cursor: Cursor? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val bundle = Bundle().apply {
                putInt(ContentResolver.QUERY_ARG_LIMIT, 1)
                putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(CallLog.Calls.DATE))
                putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING)
            }
            context.contentResolver.query(CallLog.Calls.CONTENT_URI, projection, bundle, null)
        } else {
            context.contentResolver.query(CallLog.Calls.CONTENT_URI, projection, null, null, "${CallLog.Calls.DATE} DESC LIMIT 1")
        }

        val last = if (cursor?.moveToNext() == true) toCallLogEntity(cursor) else null
        cursor?.close()
        return last
    }

    private fun toCallLogEntity(cursor: Cursor) : CallLogEntity {
        val phoneId = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls._ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME))
        val formattedNumber = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_FORMATTED_NUMBER))
        val number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER))
        val time = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE))
        val duration = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION))

        return CallLogEntity(
            remoteId = phoneId,
            startTime = time,
            name = name ?: "",
            number = formattedNumber?.replace(" ", "")?.replace("-", "") ?: number,
            timesQuered = 0,
            duration = duration
        )
    }

    fun setStartTime(timeMillis: Long) {
        startTime = timeMillis
    }
}