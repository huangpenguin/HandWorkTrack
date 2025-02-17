package com.mocap.watch
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager

class PortSettingActivity: ComponentActivity(){
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
        var portText by remember { mutableStateOf(DataSingleton.getPort().toString()) }

        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = portText,
                onValueChange = { portText = it },
                label = { Text("Enter Port Number") },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.LightGray,
                    focusedIndicatorColor = Color.Blue,
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = Color.Black,
                    textColor = Color.Black
                ),
                modifier = Modifier.padding(16.dp)
            )
            Button(onClick = {
                val newPort = portText.toIntOrNull()
                if (newPort != null && newPort in 1024..65535) {
                    val sharedPref = PreferenceManager.getDefaultSharedPreferences(this@PortSettingActivity)
                    sharedPref.edit().putInt(DataSingleton.UDP_IMU_PORT_KEY, newPort).apply()
                    DataSingleton.setPort(newPort)
                    val resultIntent = Intent()
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(this@PortSettingActivity, "Invalid Port", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Save")
            }
        }
    }
}
}