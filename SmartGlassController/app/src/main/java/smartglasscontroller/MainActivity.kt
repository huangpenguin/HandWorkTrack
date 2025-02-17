package com.example.smartglasscontroller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import smartglasscontroller.VideoSenderFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        loadFragment(IMUDataSenderFragment())
//
//        findViewById<Button>(R.id.button_imu).setOnClickListener {
//            loadFragment(IMUDataSenderFragment())
//        }
//
//        findViewById<Button>(R.id.button_record).setOnClickListener {
//            loadFragment(AudioSenderFragment())
//        }
        if (savedInstanceState == null) {
            loadFragment(IMUDataSenderFragment(), R.id.container_imu)
            //loadFragment(AudioSenderFragment(), R.id.container_audio)
            loadFragment(VideoSenderFragment(), R.id.container_video)
        }
    }

//    private fun loadFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, fragment)
//            .commit()
//    }
        private fun loadFragment(fragment: Fragment, containerId: Int) {
    supportFragmentManager.commit {
        replace(containerId, fragment)
    }
}
}
