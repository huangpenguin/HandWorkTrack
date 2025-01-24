package com.mocap.phone.activity

import android.content.Intent
import android.content.IntentFilter
import android.media.session.MediaSession
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.mocap.phone.DataSingleton
import com.mocap.phone.modules.ServiceBroadcastReceiver
import com.mocap.phone.service.AudioService
import com.mocap.phone.service.ImuService
import com.mocap.phone.service.PpgService
import com.mocap.phone.ui.theme.PhoneTheme
import com.mocap.phone.ui.view.RenderHome
import com.mocap.phone.modules.MediaSessionButtonsCallback
import com.mocap.phone.viewmodel.PhoneViewModel
import java.nio.ByteBuffer

class PhoneMain : ComponentActivity(),
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    companion object {
        private const val TAG = "PhoneMainActivity"
    }

    private val _capabilityClient by lazy { Wearable.getCapabilityClient(this) }
    private val _messageClient by lazy { Wearable.getMessageClient(this) }
    private val _viewModel by viewModels<PhoneViewModel>()

    private val _br = ServiceBroadcastReceiver {
        _viewModel.onServiceUpdate(it)
    }

    private lateinit var _mediaSession: MediaSession
    private val _callback = MediaSessionButtonsCallback {
        _viewModel.onMediaButtonDown()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            setupPreferences()
            setupMediaSession()

            PhoneTheme {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                _viewModel.queryCapabilities()
                _viewModel.regularUiUpdates()

                RenderHome(
                    connectedNodeSF = _viewModel.nodeName,
                    appActiveSF = _viewModel.appActive,
                    imuSF = _viewModel.imuStreamState,
                    imuInHzSF = _viewModel.imuInHz,
                    imuOutHzSF = _viewModel.imuOutHz,
                    imuQueueSizeSF = _viewModel.imuQueueSize,
                    audioSF = _viewModel.audioStreamState,
                    audioBroadcastHzSF = _viewModel.audioBroadcastHz,
                    audioStreamQueueSF = _viewModel.audioStreamQueue,
                    ppgSF = _viewModel.ppgStreamState,
                    ppgInHzSF = _viewModel.ppgInHz,
                    ppgOutHzSF = _viewModel.ppgOutHz,
                    ppgQueueSizeSF = _viewModel.ppgQueueSize,
                    ipSetCallback = {
                        startActivity(Intent("com.mocap.phone.SET_IP"))
                    },
                    imuStreamTrigger = {
                        _viewModel.sendImuTrigger()
                    },
                    labelCycleReset = {
                        _viewModel.resetMediaButtonRecordingSequence()
                    }
                )
            }
        }
    }

    private fun setupPreferences() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        DataSingleton.setIp(sharedPref.getString(DataSingleton.IP_KEY, DataSingleton.IP_DEFAULT) ?: "")
        DataSingleton.setAddFileId(sharedPref.getString(DataSingleton.ADD_FILE_ID_KEY, DataSingleton.ADD_FILE_ID_DEFAULT) ?: "")
        DataSingleton.setImuPort(sharedPref.getInt(DataSingleton.PORT_KEY, DataSingleton.IMU_PORT_DEFAULT))
        DataSingleton.setRecordLocally(sharedPref.getBoolean(DataSingleton.RECORD_LOCALLY_KEY, DataSingleton.RECORD_LOCALLY_DEFAULT))
        DataSingleton.setListenToMediaButtons(sharedPref.getBoolean(DataSingleton.MEDIA_BUTTONS_KEY, DataSingleton.MEDIA_BUTTONS_DEFAULT))
    }

    private fun setupMediaSession() {
        if (DataSingleton.recordLocally.value && DataSingleton.listenToMediaButtons.value) {
            _mediaSession = MediaSession(this, TAG).apply {
                setCallback(_callback)
                isActive = true
            }
            _viewModel.resetMediaButtonRecordingSequence()
            Log.d(TAG, "Media Session Active")
        }
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        _viewModel.onCapabilityChanged(capabilityInfo)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == DataSingleton.CALIBRATION_PATH) {
            val b = ByteBuffer.wrap(messageEvent.data)
            if (b.getInt(20) == 1) {
                val i = Intent("com.mocap.phone.CALIBRATION").apply {
                    putExtra("sourceNodeId", messageEvent.sourceNodeId)
                }
                startActivity(i)
            }
        }
        _viewModel.onMessageReceived(messageEvent)
    }

    private fun registerListeners() {
        val filter = IntentFilter(DataSingleton.BROADCAST_UPDATE)
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(_br, filter)

        _messageClient.addListener(this)
        _capabilityClient.addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE)
        _capabilityClient.addLocalCapability(DataSingleton.PHONE_APP_ACTIVE)
        _viewModel.queryCapabilities()

        startServices()
    }

    private fun startServices() {
        startService(Intent(this, ImuService::class.java))
        startService(Intent(this, PpgService::class.java))
        startService(Intent(this, AudioService::class.java))
    }

    override fun onResume() {
        super.onResume()
        registerListeners()
    }

    override fun onPause() {
        super.onPause()
        unregisterListeners()
        stopServices()
    }

    private fun unregisterListeners() {
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(_br)
        _messageClient.removeListener(this)
        _capabilityClient.removeListener(this)
        _capabilityClient.removeLocalCapability(DataSingleton.PHONE_APP_ACTIVE)
        if (this::_mediaSession.isInitialized) {
            _mediaSession.apply {
                isActive = false
                release()
            }
            Log.d(TAG, "Media Session Inactive")
        }
    }

    private fun stopServices() {
        stopService(Intent(this, ImuService::class.java))
        stopService(Intent(this, PpgService::class.java))
        stopService(Intent(this, AudioService::class.java))
    }
}
