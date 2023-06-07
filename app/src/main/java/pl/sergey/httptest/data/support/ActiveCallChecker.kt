package pl.sergey.httptest.data.support

import android.content.Context
import android.media.AudioManager

class ActiveCallChecker(private val context: Context) {

    fun isCallActive(): Boolean {
        val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return manager.mode == AudioManager.MODE_IN_CALL
    }

}