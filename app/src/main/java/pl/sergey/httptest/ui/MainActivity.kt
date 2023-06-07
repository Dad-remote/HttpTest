package pl.sergey.httptest.ui

import android.Manifest
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pl.sergey.httptest.ui.main.Main
import pl.sergey.httptest.data.server.HttpServiceImpl
import pl.sergey.httptest.ui.theme.HttpTestTheme


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var serviceStarted = false
    private var serviceBinded = false

    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            viewModel.connected((service as HttpServiceImpl.LocalBinder).getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.disconnected()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HttpTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Main()
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.active.collect {
                    if (it && !serviceStarted) {
                        val serviceIntent = Intent(this@MainActivity, HttpServiceImpl::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(serviceIntent)
                        } else {
                            startService(serviceIntent)
                        }

                        bindService(Intent(this@MainActivity, HttpServiceImpl::class.java), serviceConnection, Service.BIND_AUTO_CREATE)
                        serviceBinded = true
                    }
                    if (!it && serviceStarted) {
                        unbindService(serviceConnection)
                        stopService(Intent(this@MainActivity, HttpServiceImpl::class.java))
                        serviceBinded = false
                    }
                    serviceStarted = it
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (serviceStarted && !serviceBinded) {
            bindService(Intent(this, HttpServiceImpl::class.java), serviceConnection, Service.BIND_AUTO_CREATE)
            serviceBinded = true
        }

        checkPermissions()
    }

    override fun onStop() {
        super.onStop()
        if (serviceBinded) {
            unbindService(serviceConnection)
            serviceBinded = false
        }
    }

    private fun checkPermissions() {
        val permissions = ArrayList<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissions.add(Manifest.permission.READ_CALL_LOG)
        permissions.add(Manifest.permission.READ_PHONE_STATE)
        permissions.add(Manifest.permission.READ_CONTACTS)
        permissions
            .filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
            .toTypedArray()
            .let {
                requestPermissionLauncher.launch(it)
            }
    }

}