package pl.sergey.httptest.data.support

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.Build
import android.text.format.Formatter
import java.net.Inet4Address
import java.util.concurrent.LinkedBlockingQueue

class IPHolder(context: Context) {

    private lateinit var ip: String
    private val lock = LinkedBlockingQueue<String>()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val request: NetworkRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCallback = object : NetworkCallback() {

                override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                    val properties = connectivityManager.getLinkProperties(network)
                    lock.put(
                        properties?.linkAddresses?.find { it.address is Inet4Address }?.address?.address?.let {
                            "${it[0].toUByte()}.${it[1].toUByte()}.${it[2].toUByte()}.${it[3].toUByte()}"
                        } ?: "<none>"
                    )
                }
            }
            connectivityManager.registerNetworkCallback(request, networkCallback)
        } else {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            lock.put(Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress))
        }
    }

    fun getIp(): String {
        if (!::ip.isInitialized) {
            ip = lock.take()
        }
        return ip
    }

}