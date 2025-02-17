package com.example.smartglasscontroller

import android.media.AudioManager
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class AudioSenderFragment : Fragment() {

    private var isRecording = false
    private lateinit var audioRecord: AudioRecord
    private lateinit var udpSocket: DatagramSocket
    private lateinit var ipAddress: InetAddress
    private var port: Int = 0
    private val bufferSize = AudioRecord.getMinBufferSize(
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_audio_sender, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ipEditText: EditText = view.findViewById(R.id.editText_ip)
        val portEditText: EditText = view.findViewById(R.id.editText_port)
        val recordButton: Button = view.findViewById(R.id.button_record_audio)

        recordButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
                recordButton.text = "Record Audio"
            } else {
                val ip = ipEditText.text.toString().trim()
                val portStr = portEditText.text.toString()
                if (ip.isEmpty() || portStr.isEmpty()) {
                    Toast.makeText(context, "Please enter IP and Port", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                try {
                    ipAddress = InetAddress.getByName(ip)
                    port = portStr.toInt()
                    startRecording()
                    recordButton.text = "Stop Recording"
                } catch (e: Exception) {
                    Toast.makeText(context, "Invalid IP or Port", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startRecording() {
        isRecording = true
        udpSocket = DatagramSocket()
        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioManager.isMicrophoneMute) {
            Toast.makeText(context, "Microphone is in use by another app", Toast.LENGTH_SHORT).show()
            return
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), 100)
        }

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )


        try {
            audioRecord.startRecording()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to start recording: ${e.message}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Unexpected error: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        CoroutineScope(Dispatchers.IO).launch {
            val buffer = ByteArray(bufferSize)
            while (isRecording) {
                val read = audioRecord.read(buffer, 0, buffer.size)
                if (read > 0) {
                    sendAudioUdp(buffer, read)
                }
            }
        }
        Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show()
    }

    private fun stopRecording() {
        isRecording = false
        audioRecord.stop()
        audioRecord.release()
        udpSocket.close()
        Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
    }

    private fun sendAudioUdp(buffer: ByteArray, length: Int) {
        try {
            val packet = DatagramPacket(buffer, length, ipAddress, port)
            udpSocket.send(packet)
            Log.d("AudioSender", "Sending packet of size: $length")

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
