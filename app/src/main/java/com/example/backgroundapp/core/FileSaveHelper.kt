package com.example.backgroundapp.core

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.jvm.Throws

class FileSaveHelper(private val mContentResolver: ContentResolver): LifecycleObserver {
    private val executor: ExecutorService? = Executors.newSingleThreadExecutor()
    private val fileCreatedResult: MutableLiveData<FileMeta> = MutableLiveData()
    private var resultListener: OnFileCreatedResult? = null
    private val observer = Observer { fileMeta: FileMeta ->
        resultListener?.onFileCreateResult(
            fileMeta.isCreated,
            fileMeta.filePath,
            fileMeta.error,
            fileMeta.uri
        )
    }

    constructor(activity: AppCompatActivity) : this(activity.contentResolver){
        addObserver(activity)
    }

    private fun addObserver(lifecycleOwner: LifecycleOwner) {
        fileCreatedResult.observe(lifecycleOwner, observer)
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun release(){
        executor?.shutdownNow()
    }

    fun createFile(fileNameToSave:String, listener:OnFileCreatedResult?){
        resultListener = listener
        executor!!.submit{
            var cursor: Cursor? = null
            try{
                val newImageDetails = ContentValues()
                val imageCollection = buildUriCollection(newImageDetails)
                val editedImageUri = getEditedImageUri(fileNameToSave, newImageDetails,imageCollection)

                cursor = mContentResolver.query(
                    editedImageUri,
                    arrayOf(MediaStore.Images.Media.DATA),
                    null,
                    null,
                    null
                )
                val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                val filePath = cursor.getString(columnIndex)

                updateResult(true, filePath, null, editedImageUri, newImageDetails)
            }catch (e: Exception){
                e.printStackTrace()
                updateResult(false, null, "doesnt work g", null, null)
            }finally {
                cursor?.close()
            }
        }
    }

    @Throws(IOException::class)
    private fun getEditedImageUri(
        fileNameToSave: String,
        newImageDetails: ContentValues,
        imageCollection: Uri):Uri {
        newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, fileNameToSave)
        val editedImageUri = mContentResolver.insert(imageCollection, newImageDetails)
        val outputStream = mContentResolver.openOutputStream(editedImageUri!!)
        outputStream!!.close()
        return editedImageUri
    }

    @SuppressLint("InlinedApi")
    private fun buildUriCollection(newImageDetails: ContentValues):Uri{
        val imageCollection: Uri
        if (isSdkHigherThan28()){
            imageCollection = Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
            newImageDetails.put(Media.IS_PENDING, 1)
        }else{
            imageCollection = Media.EXTERNAL_CONTENT_URI
        }
        return imageCollection
    }

    @SuppressLint("InlinedApi")
    fun notifyThatFileIsNowPubliclyAvailable(contentResolver: ContentResolver) {
        if (isSdkHigherThan28()) {
            executor!!.submit {
                val value = fileCreatedResult.value
                if (value != null) {
                    value.imageDetails!!.clear()
                    value.imageDetails!!.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(value.uri!!, value.imageDetails, null, null)
                }
            }
        }
    }

    fun interface OnFileCreatedResult {
        fun onFileCreateResult(created: Boolean, filePath: String?, error: String?, uri: Uri?)
    }

    private fun updateResult(
        result: Boolean,
        filePath: String?,
        error: String?,
        uri: Uri?,
        newImageDetails: ContentValues?
    ) {
        fileCreatedResult.postValue(FileMeta(result, filePath, uri, error, newImageDetails))
    }


    private class FileMeta(
        var isCreated: Boolean, var filePath: String?,
        var uri: Uri?, var error: String?,
        var imageDetails: ContentValues?
    )

    companion object {
        fun isSdkHigherThan28(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        }
    }
}