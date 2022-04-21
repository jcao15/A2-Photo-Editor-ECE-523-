package com.example.a2_photo_editor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editMain: Button = findViewById(R.id.editMain)
        editMain.setOnClickListener {
            val intent = Intent(this, photo_editor::class.java)
            startActivity(intent)
        }

    }

}
