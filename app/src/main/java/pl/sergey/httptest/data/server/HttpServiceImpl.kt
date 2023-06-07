package pl.sergey.httptest.data.server

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pl.sergey.httptest.R
import pl.sergey.httptest.data.server.model.ServerState
import pl.sergey.httptest.data.support.IPHolder
import pl.sergey.httptest.ui.MainActivity
import pl.sergey.httptest.data.server.handler.LogRequestHandler
import pl.sergey.httptest.data.server.handler.RootRequestHandler
import pl.sergey.httptest.data.server.handler.StatusRequestHandler
import pl.sergey.net.HttpServer
import java.net.BindException
import javax.inject.Inject

@AndroidEntryPoint
class HttpServiceImpl : Service(), HttpService {

    companion object {
        const val START_PORT = 10000
        const val NOTIFICATION_ID = 1
        const val OPEN_REQUEST_CODE = 123
        const val CHANNEL_ID = "default"
    }

    private var startTime: Long = 0
    private val localBinder = LocalBinder()
    private var _state = MutableStateFlow(ServerState("", 0, true))
    private var httpServer: HttpServer? = null

    @Inject lateinit var statusRequestHandler: StatusRequestHandler
    @Inject lateinit var logRequestHandler: LogRequestHandler
    @Inject lateinit var ipHolder: IPHolder

    override val serverState: Flow<ServerState> = _state

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        startTime = System.currentTimeMillis()
        startHttpServer()
        _state.value = ServerState(ipHolder.getIp(), httpServer?.port ?: 0, true)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder {
        return localBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopHttpServer()
        stopForeground()
    }

    private fun stopForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT)
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
    }

    private fun buildNotification() : Notification {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, OPEN_REQUEST_CODE, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentTitle(getString(R.string.http_service))
            .setContentText(getString(R.string.server_is_running))
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel()
                    setChannelId(CHANNEL_ID)
                }
            }
            .build()
    }

    private fun startHttpServer() {
        val (httpServer, port) = startAndBind()
        httpServer.get("/", RootRequestHandler(startTime, ipHolder, port))
        httpServer.get("/log", logRequestHandler)
        httpServer.get("/status", statusRequestHandler)
        this.httpServer = httpServer
    }

    private fun startAndBind() : Pair<HttpServer, Int> {
        var port = START_PORT
        var httpServer: HttpServer? = null
        while (httpServer == null) {
            httpServer = try {
                HttpServer.build(port)
            } catch (e: BindException) {
                port += 1
                null
            }
        }
        return httpServer to port
    }

    private fun stopHttpServer() {
        httpServer?.stop()
        httpServer = null
    }

    inner class LocalBinder : Binder() {
        fun getService(): HttpService = this@HttpServiceImpl
    }
}