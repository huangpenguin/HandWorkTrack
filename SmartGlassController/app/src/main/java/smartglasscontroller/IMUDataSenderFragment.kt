package com.example.smartglasscontroller

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.fragment.app.Fragment

import com.epson.moverio.hardware.sensor.SensorData
import com.epson.moverio.hardware.sensor.SensorDataListener
import com.epson.moverio.hardware.sensor.SensorManager

import org.json.JSONObject

import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class IMUDataSenderFragment : Fragment(), SensorDataListener {

    private var mContext: Context? = null
    private lateinit var mSensorManager: SensorManager
    private lateinit var handler: Handler
    private var isSending = false

    private var udpSocket: DatagramSocket? = null
    private lateinit var ipAddress: InetAddress
    private var port: Int = 0

    private val prefsName = "IMUDataSenderPrefs"
    private val ipKey = "ipAddress"
    private val portKey = "port"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mContext = context
        mSensorManager = SensorManager(requireContext())
        handler = Handler()
        return inflater.inflate(R.layout.fragment_imu_data_sender, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ipEditText: EditText = view.findViewById(R.id.editText_ip)
        val portEditText: EditText = view.findViewById(R.id.editText_port)
        val startButton: Button = view.findViewById(R.id.button_imu_sender)

        loadPreferences(ipEditText, portEditText)

        startButton.setOnClickListener {
            if (isSending) {
                // Stop sending IMU data
                stopSending()
                startButton.text = "Start Sending"
            } else {
                // Validate IP and Port
                val ip = ipEditText.text.toString().trim()
                val portStr = portEditText.text.toString().trim()
                if (ip.isEmpty() || portStr.isEmpty()) {
                    Toast.makeText(context, "Please enter IP and Port", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                try {
                    ipAddress = InetAddress.getByName(ip)
                    port = portStr.toInt()
                    savePreferences(ip, portStr)
                    startSending()

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Invalid IP or Port", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Starts sending IMU data by opening sensors and setting up UDP communication.
     */
    private fun startSending() {
        try {
            udpSocket = DatagramSocket()
            // Open sensors
            mSensorManager.open(SensorManager.TYPE_ACCELEROMETER, this)
            mSensorManager.open(SensorManager.TYPE_GYROSCOPE, this)
            mSensorManager.open(SensorManager.TYPE_MAGNETIC_FIELD, this)
            mSensorManager.open(SensorManager.TYPE_LINEAR_ACCELERATION, this)
            mSensorManager.open(SensorManager.TYPE_GRAVITY, this)
            mSensorManager.open(SensorManager.TYPE_ROTATION_VECTOR, this)
            isSending = true // Update state
            requireActivity().runOnUiThread {
                view?.findViewById<Button>(R.id.button_imu_sender)?.text = "Stop Sending"
            }
            Toast.makeText(context, "Started sending IMU data", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to open sensors: ${e.message}", Toast.LENGTH_SHORT).show()
            isSending = false // Reset state if failed
            requireActivity().runOnUiThread {
                view?.findViewById<Button>(R.id.button_imu_sender)?.text = "Start Sending"
            }
        }
    }

    /**
     * Stops sending IMU data by closing sensors and releasing UDP resources.
     */
    private fun stopSending() {
        try {
            // Close sensors
            mSensorManager.close(SensorManager.TYPE_ACCELEROMETER, this)
            mSensorManager.close(SensorManager.TYPE_GYROSCOPE, this)
            mSensorManager.close(SensorManager.TYPE_MAGNETIC_FIELD, this)
            mSensorManager.close(SensorManager.TYPE_LINEAR_ACCELERATION, this)
            mSensorManager.close(SensorManager.TYPE_GRAVITY, this)
            mSensorManager.close(SensorManager.TYPE_ROTATION_VECTOR, this)

            udpSocket?.close() // Close UDP socket
            udpSocket = null
            isSending = false // Update state
            Toast.makeText(context, "Stopped sending IMU data", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error stopping IMU data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSensorDataChanged(data: SensorData) {
        if (!isSending || udpSocket == null) return

        val elapsedNanos = SystemClock.elapsedRealtimeNanos()
        val seconds = elapsedNanos / 1_000_000_000L
        val nanos = (elapsedNanos % 1_000_000_000L).toFloat() / 1_000_000_000F

        val sensorDataJson = JSONObject()
        sensorDataJson.put("timestamp", seconds)
        sensorDataJson.put("nanos", nanos)

        when (data.type) {
            SensorManager.TYPE_ACCELEROMETER -> {
                sensorDataJson.put("sensor_type", "accelerometer")
                sensorDataJson.put("x", data.values[0])
                sensorDataJson.put("y", data.values[1])
                sensorDataJson.put("z", data.values[2])
            }
            SensorManager.TYPE_GYROSCOPE -> {
                sensorDataJson.put("sensor_type", "gyroscope")
                sensorDataJson.put("x", data.values[0])
                sensorDataJson.put("y", data.values[1])
                sensorDataJson.put("z", data.values[2])
            }
            SensorManager.TYPE_MAGNETIC_FIELD -> {
                sensorDataJson.put("sensor_type", "magnetic_field")
                sensorDataJson.put("x", data.values[0])
                sensorDataJson.put("y", data.values[1])
                sensorDataJson.put("z", data.values[2])
                sensorDataJson.put("accuracy", data.accuracy)
            }
            SensorManager.TYPE_LINEAR_ACCELERATION -> {
                sensorDataJson.put("sensor_type", "linear_acceleration")
                sensorDataJson.put("x", data.values[0])
                sensorDataJson.put("y", data.values[1])
                sensorDataJson.put("z", data.values[2])
            }
            SensorManager.TYPE_GRAVITY -> {
                sensorDataJson.put("sensor_type", "gravity")
                sensorDataJson.put("x", data.values[0])
                sensorDataJson.put("y", data.values[1])
                sensorDataJson.put("z", data.values[2])
            }
            SensorManager.TYPE_ROTATION_VECTOR -> {
                sensorDataJson.put("sensor_type", "rotation_vector")
                sensorDataJson.put("x", data.values[0])
                sensorDataJson.put("y", data.values[1])
                sensorDataJson.put("z", data.values[2])
                sensorDataJson.put("w", data.values[3])
            }
            else -> return
        }

        sendUdpPacket(sensorDataJson)
    }

    private fun sendUdpPacket(sensorDataJson: JSONObject) {
        try {
            val jsonData = sensorDataJson.toString()
            val dataToSend = jsonData.toByteArray(Charsets.UTF_8)

            val packet = DatagramPacket(dataToSend, dataToSend.size, ipAddress, port)
            udpSocket?.send(packet)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadPreferences(ipEditText: EditText, portEditText: EditText) {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val savedIp = sharedPreferences.getString(ipKey, "192.168.8.104") ?: "192.168.8.104"
        val savedPort = sharedPreferences.getInt(portKey, 46002)

        ipEditText.setText(savedIp)
        portEditText.setText(savedPort.toString())
    }

    // Save IP and Port to SharedPreferences
    private fun savePreferences(ip: String, port: String) {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(ipKey, ip)
        editor.putInt(portKey, port.toInt())
        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopSending()
        mSensorManager.release()
    }
}
