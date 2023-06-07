package pl.sergey.httptest.data.server.model

import pl.sergey.httptest.data.db.model.CallLogEntity
import pl.sergey.httptest.data.support.formatTime

class CallLogResponse(item: CallLogEntity) {

    val beginning: String
    val duration: String
    val number: String
    val name: String
    val timesQueried: Int

    init {
        beginning = formatTime(item.startTime)
        duration = item.duration.toString()
        number = item.number
        name = item.name
        timesQueried = item.timesQuered
    }
}