package pl.sergey.httptest.data.support

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
private val timezoneFormat = SimpleDateFormat("Z", Locale.ENGLISH)

fun formatTime(time: Long): String {
    val start = dateFormat.format(Date(time))
    val timezone = timezoneFormat.format(Date(time)).let { it.substring(0, 3) + ":" + it.substring(3)}
    return start + timezone
}