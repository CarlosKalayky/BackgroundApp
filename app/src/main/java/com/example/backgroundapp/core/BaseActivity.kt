package com.example.backgroundapp.core

import android.R.id.content
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Burhanuddin Rashid on 1/17/2018.
 */
open class BaseActivity : AppCompatActivity() {
    private var mProgressDialog: ProgressDialog? = null
    private var mPermission: String? = null

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isPermissionGranted(it, mPermission)
        }

    fun requestPermission(permission: String): Boolean {
        val isGranted =
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            mPermission = permission
            permissionLauncher.launch(permission)
        }
        return isGranted
    }

    open fun isPermissionGranted(isGranted: Boolean, permission: String?) {
        //Watch out
    }

    protected fun showLoading(message: String) {
        mProgressDialog = ProgressDialog(this)
        mProgressDialog?.run {
            setMessage(message)
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
            setCancelable(false)
            show()
        }
    }

    protected fun hideLoading() {
        mProgressDialog?.dismiss()
    }

    protected fun showSnackbar(message: String) {
        val view = findViewById<View>(content)
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}