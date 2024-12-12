package com.example.backgroundapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.backgroundapp.R

class LoadingScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loading_screen)
        val actionBar = supportActionBar
        actionBar?.hide()
        try {
            Handler().postDelayed({
                startActivity(Intent(this@LoadingScreen, PicEditor::class.java))
                finish()
            }, 3000)
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }
}