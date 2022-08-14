package com.slt.guesswho

import android.graphics.Bitmap
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    private lateinit var ivImageStar: ImageView

    private lateinit var btnAnswer1: Button
    private lateinit var btnAnswer2: Button
    private lateinit var btnAnswer3: Button
    private lateinit var btnAnswer4: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ivImageStar = findViewById(R.id.ivImageStar)

        btnAnswer1 =  findViewById(R.id.btnAnswer1)
        btnAnswer2 =  findViewById(R.id.btnAnswer2)
        btnAnswer3 =  findViewById(R.id.btnAnswer3)
        btnAnswer4 =  findViewById(R.id.btnAnswer4)

    }
}