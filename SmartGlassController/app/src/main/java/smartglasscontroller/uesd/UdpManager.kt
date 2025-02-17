package com.example.smartglasscontroller

import android.util.Log
import kotlinx.coroutines.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpManager {

    private val scope = CoroutineScope(Dispatchers.IO)
    fun sendUdpMessage(message: String, targetIp: String, targetPort: Int) {
        scope.launch {
            try {
                val socket = DatagramSocket()
                val address = InetAddress.getByName(targetIp)
                val buffer = message.toByteArray()
                val packet = DatagramPacket(buffer, buffer.size, address, targetPort)

                socket.send(packet)
                Log.d("UdpManager", "Message sent: $message to $targetIp:$targetPort")
                socket.close()
            } catch (e: Exception) {
                Log.e("UdpManager", "Error sending UDP message: ${e.message}")
            }
        }
    }

    fun receiveUdpMessages(listenPort: Int, callback: (String, String) -> Unit) {
        scope.launch {
            try {
                val socket = DatagramSocket(listenPort)
                val buffer = ByteArray(1024)
                val packet = DatagramPacket(buffer, buffer.size)

                Log.d("UdpManager", "Listening for UDP messages on port $listenPort...")
                while (true) {
                    socket.receive(packet)
                    val receivedMessage = String(packet.data, 0, packet.length)
                    val senderAddress = packet.address.hostAddress

                    Log.d("UdpManager", "Message received: $receivedMessage from $senderAddress")
                    withContext(Dispatchers.Main) {
                        callback(receivedMessage, senderAddress)
                    }
                }
            } catch (e: Exception) {
                Log.e("UdpManager", "Error receiving UDP message: ${e.message}")
            }
        }
    }

    fun stop() {
        scope.cancel()
    }
}
