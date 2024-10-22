package com.example.backgroundapp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import android.app.WallpaperManager
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.backgroundapp.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        binding.btnWallpaper.setOnClickListener {
            var eText = binding.etURL.text
            if (eText.isNullOrEmpty() || !(isValidURL(eText.toString().trim()))) {
                Toast.makeText(this@MainActivity, "Not valid message", Toast.LENGTH_SHORT).show()
            }else{
                Picasso.get().load(eText.toString().trim()).into(binding.ivWallpaper)
                binding.btnWallpaper.setVisibility(View.GONE)
                binding.btnConfirm.setVisibility(View.VISIBLE)
                binding.btnConfirm.setOnClickListener {
                    Toast.makeText(this@MainActivity, "Background is set1", Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch(Dispatchers.IO) {
                        val inputStream = URL(eText.toString().trim()).openStream()
                        WallpaperManager.getInstance(applicationContext).setStream(inputStream)
//                      runOnUiThread {
//                        Picasso.get().load(eText.toString().trim()).into(binding.ivWallpaper)
//                      }
                    }
                }
            }
        }
    }

    private fun isValidURL(url:String): Boolean {
        try {
            URL(url)
            return true
        } catch (e: Exception){
            return false
        }
    }
}
