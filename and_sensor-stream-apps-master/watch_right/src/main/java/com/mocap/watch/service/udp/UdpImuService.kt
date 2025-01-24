package com.mocap.watch.service.udp

import android.content.Intent
import android.util.Log
import com.mocap.watch.DataSingleton
import com.mocap.watch.service.BaseImuService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer


class UdpImuService : BaseImuService() {

    companion object {
        private const val TAG = "UDP IMU Service"  // for logging
    }

    /**
     * Triggers the streaming of IMU data as a service
     * or (if already running) stops the service.
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i(TAG, "Received start id $startId: $intent")
        if (imuStreamState) {
            Log.w(TAG, "stream already started")
            onDestroy() // 停止服务
        } else {
            imuStreamState = true
            // 启动接收器以接收端口信息
            scope.launch { startUdpReceiver() }
        }
        return START_NOT_STICKY
    }


    /**
     * Stream IMU data in a while loop.
     * This function can be suspended to be able to kill the loop by stopping or pausing
     * the scope it was started from.
     */
    private suspend fun susStreamData(port: Int) {
        val ip = DataSingleton.ip.value

        withContext(Dispatchers.IO) {
            try {
                // 使用新的端口打开套接字
                val udpSocket = DatagramSocket(port)
                udpSocket.broadcast = true
                val socketInetAddress = InetAddress.getByName(ip)
                Log.v(TAG, "Opened UDP socket to $ip:$port")

                udpSocket.use {
                    // register all listeners with their assigned codes
                    registerSensorListeners()

                    // start the stream loop
                    while (imuStreamState) {
                        // compose message
                        val lastDat = composeImuMessage()
                        if (lastDat != null) {
                            val buffer = ByteBuffer.allocate(DataSingleton.IMU_MSG_SIZE)
                            for (v in lastDat) buffer.putFloat(v)
                            val dp = DatagramPacket(buffer.array(), buffer.capacity(), socketInetAddress, port)
                            udpSocket.send(dp) // 发送数据
                        }
                        delay(MSGBREAK) // 避免发送过快
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, e)
                stopService() // 停止服务
            }
        }
    }

    private suspend fun startUdpReceiver() {
        withContext(Dispatchers.IO) {
            try {
                val receiverSocket = DatagramSocket(DataSingleton.UDP_IMU_PORT) // 可以是一个默认端口
                val buffer = ByteArray(1024)

                while (imuStreamState) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    receiverSocket.receive(packet) // 接收数据包
                    val message = String(packet.data, 0, packet.length)

                    // 检查消息是否包含端口信息
                    if (message.startsWith("Using port:")) {
                        val port = message.split(":")[1].trim().toInt()
                        DataSingleton.UDP_IMU_PORT = port // 更新端口号
                        Log.i(TAG, "Received port: $port")

                        // 启动 IMU 流服务
                        startImuStreaming(port)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error receiving UDP messages: ${e.message}")
            }
        }
    }
    private fun startImuStreaming(port: Int) {
        // 关闭之前的流
        imuStreamState = false
        // 重新启动流服务，使用新的端口
        scope.launch { susStreamData(port) }
    }


}