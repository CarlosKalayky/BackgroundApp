package com.example.backgroundapp.ui

import android.Manifest
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.backgroundapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorMatrixFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val dispatcher = Dispatchers.IO
    private lateinit var binding: ActivityMainBinding
    private var storagePermission: Array<String>? = null
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        val galleryUri = it
        try {
            Glide.with(this).load(galleryUri).into(binding.ivWallpaper)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.etURL.setSelectAllOnFocus(true)
        setContentView(binding.root)
        storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.println(Log.ASSERT, "1", "ok")
        } else {
            // Permission is not granted.
            requestPermissions(storagePermission!!, STORAGE_REQUEST)
            ActivityCompat.requestPermissions(
                this, storagePermission!!, READ_EXTERNAL_STORAGE_PERMISSION_CODE
            )
        }
        initUI()
    }

    //I know i could just make a Linear/Frame Layout inside for buttons to just remove them all in
//    line but cba XDDD
    private fun initUI() {
        binding.btnEdit.setOnClickListener {
            binding.gpuimageview.visibility = View.VISIBLE
            Glide.with(applicationContext).asBitmap().load(eText.toString().trim())
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val gpuImage = GPUImage(applicationContext)
                        gpuImage.setImage(resource)
                        gpuImage.setFilter(GPUImageColorMatrixFilter())
                        //Try adding a recycler view of a list of filters to apply to this picture
                        binding.ivWallpaper.setImageBitmap(gpuImage.bitmapWithFilterApplied)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
        binding.btnGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }
        binding.btnWallpaper.setOnClickListener {
            eText = binding.etURL.text
            if (eText.isNullOrEmpty() || !(isValidURL(eText.toString().trim()))) {
                Toast.makeText(this@MainActivity, "Not valid message", Toast.LENGTH_SHORT).show()
            } else {
                Glide.with(this).asBitmap().load(eText.toString().trim()).into(binding.ivWallpaper)
//                Picasso.get().load(eText.toString().trim()).into(binding.ivWallpaper)
                binding.btnWallpaper.visibility = View.GONE
                binding.etURL.visibility = View.GONE
                binding.btnGallery.visibility = View.GONE
                binding.btnNew.visibility = View.VISIBLE
                binding.btnConfirm.visibility = View.VISIBLE
                binding.btnEdit.visibility = View.VISIBLE
                binding.btnConfirm.setOnClickListener {
                    Toast.makeText(this@MainActivity, "Background is set1", Toast.LENGTH_SHORT)
                        .show()
                    lifecycleScope.launch(dispatcher) {
                        val inputStream = URL(eText.toString().trim()).openStream()
                        WallpaperManager.getInstance(applicationContext).setStream(inputStream)
                    }
                }
            }
        }
        binding.btnNew.setOnClickListener {
            Glide.with(this).clear(binding.ivWallpaper)
            binding.btnNew.visibility = View.GONE
            binding.btnGallery.visibility = View.VISIBLE
            binding.etURL.visibility = View.VISIBLE
            binding.btnConfirm.visibility = View.GONE
            binding.btnEdit.visibility = View.GONE
            binding.btnWallpaper.visibility = View.VISIBLE
        }
    }

    private fun isValidURL(url: String): Boolean {
        try {
            URL(url)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    companion object {
        private const val STORAGE_REQUEST = 22
        private const val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 101
        var eText: Editable? = null
    }
}