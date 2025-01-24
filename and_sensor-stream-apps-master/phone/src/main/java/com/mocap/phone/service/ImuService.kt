package com.mocap.phone.service

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mocap.phone.DataSingleton
import com.mocap.phone.modules.SensorListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.round

class ImuService : Service() {

    companion object {
        private const val TAG = "IMU Service"
        private const val NS2S = 1.0f / 1000000000.0f
        private const val MS2S = 0.001f
    }

    private lateinit var _sensorManager: SensorManager
    private val _scope = CoroutineScope(Job() + Dispatchers.IO)
    private var _lastBroadcast = LocalDateTime.now()

    // 手机传感器值
    private var _dpLvel: FloatArray = floatArrayOf(0f, 0f, 0f)
    private var _tsLacc: Long = 0
    private var _tsDLacc: Float = 0f
    private var _dGyro: FloatArray = floatArrayOf(0f, 0f, 0f)
    private var _tsGyro: Long = 0
    private var _tsDGyro: Float = 0f
    private var _rotvec: FloatArray = floatArrayOf(1f, 0f, 0f, 0f, 0f)
    private var _pres: FloatArray = floatArrayOf(0f)
    private var _grav: FloatArray = floatArrayOf(0f, 0f, 0f)

    private val _listeners = listOf(
        SensorListener(Sensor.TYPE_PRESSURE) { onPressureReadout(it) },
        SensorListener(Sensor.TYPE_LINEAR_ACCELERATION) { onLaccReadout(it) },
        SensorListener(Sensor.TYPE_ROTATION_VECTOR) { onRotVecReadout(it) },
        SensorListener(Sensor.TYPE_GRAVITY) { onGravReadout(it) },
        SensorListener(Sensor.TYPE_GYROSCOPE) { onGyroReadout(it) }
    )

    override fun onCreate() {
        _sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        Log.v(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        _scope.launch {
            while (true) {
                broadcastUiUpdate()
                delay(2000L)
                sendUdpImuMessages()
            }
        }
        Log.v(TAG, "Service started")
        return START_NOT_STICKY
    }

    private fun broadcastUiUpdate() {
    }

    private suspend fun recordImuMessages() {
        withContext(Dispatchers.IO) {
            val currentDate = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS").format(LocalDateTime.now())
            val fileName = "rec_phone_pocket_${DataSingleton.recordActivityLabel.value}_${DataSingleton.addFileId.value}_${currentDate}.csv"
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val textFile = File(path, fileName)
            val fOut = FileWriter(textFile)
            fOut.write("timestamp,rotvec_w,rotvec_x,rotvec_y,rotvec_z,gyro_x,gyro_y,gyro_z,lacc_x,lacc_y,lacc_z,pres,grav_x,grav_y,grav_z\n")

            while (true) {
                val phoneData = composeImuMessage()
                if (phoneData != null) {
                    var wstr = ""
                    for (entry in phoneData) {
                        wstr += "$entry,"
                    }
                    fOut.write(wstr + "\n")
                }
                delay(20L)
            }
        }
    }

    private suspend fun sendUdpImuMessages() {
        val port = DataSingleton.imuPort.value
        val ip = DataSingleton.ip.value
        val udpSocket = DatagramSocket(port)
        udpSocket.broadcast = true
        val socketInetAddress = InetAddress.getByName(ip)

        udpSocket.use {
            while (true) {
                val phoneData = composeImuMessage()
                if (phoneData != null) {
                    val buffer = ByteBuffer.allocate(DataSingleton.IMU_MSG_SIZE)
                    for (v in phoneData) {
                        buffer.putFloat(v)
                    }
                    val dp = DatagramPacket(buffer.array(), buffer.capacity(), socketInetAddress, port)
                    udpSocket.send(dp)
                }
                delay(20L)
            }
        }
    }

    private fun composeImuMessage(): FloatArray? {
        val tsNow = LocalDateTime.now()
        val ts = floatArrayOf(tsNow.hour.toFloat(), tsNow.minute.toFloat(), tsNow.second.toFloat(), tsNow.nano.toFloat())

        if ((_tsDLacc == 0f) || (_tsDGyro == 0f)) {
            return null
        }

        val message = ts + _rotvec + _dGyro + _dpLvel + _pres + _grav
        _dpLvel = floatArrayOf(0f, 0f, 0f)
        _tsDLacc = 0f
        _dGyro = floatArrayOf(0f, 0f, 0f)
        _tsDGyro = 0f
        _rotvec = floatArrayOf(1f, 0f, 0f, 0f, 0f)
        return message
    }

    override fun onDestroy() {
        super.onDestroy()
        _scope.cancel()
        for (l in _listeners) {
            _sensorManager.unregisterListener(l)
        }
        Log.v(TAG, "IMU Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun onLaccReadout(event: SensorEvent) {
        if (_tsLacc != 0L) {
            val dT = (event.timestamp - _tsLacc) * NS2S
            if (dT > 1f) {
                _dpLvel = event.values
                _tsDLacc = 1f
            } else {
                _dpLvel[0] += event.values[0] * dT
                _dpLvel[1] += event.values[1] * dT
                _dpLvel[2] += event.values[2] * dT
                _tsDLacc += dT
            }
        }
        _tsLacc = event.timestamp
    }

    fun onGyroReadout(event: SensorEvent) {
        if (_tsGyro != 0L) {
            val dT = (event.timestamp - _tsGyro) * NS2S
            if (dT > 1f) {
                _dGyro = event.values
                _tsDGyro = 1f
            } else {
                _dGyro[0] += event.values[0] * dT
                _dGyro[1] += event.values[1] * dT
                _dGyro[2] += event.values[2] * dT
                _tsDGyro += dT
            }
        }
        _tsGyro = event.timestamp
    }

    fun onRotVecReadout(event: SensorEvent) {
        _rotvec = floatArrayOf(event.values[3], event.values[0], event.values[1], event.values[2], event.values[4])
    }

    fun onPressureReadout(event: SensorEvent) {
        _pres = event.values
    }

    fun onGravReadout(event: SensorEvent) {
        _grav = event.values
    }
}
