package pl.sergey.httptest.data.support

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallDataHolder @Inject constructor() {

    var last = "" to ""

    fun push(number: String, name: String) {
        last = number to name
    }

}