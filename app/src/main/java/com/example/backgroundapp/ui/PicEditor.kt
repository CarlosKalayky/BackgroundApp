package com.example.backgroundapp.ui

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import com.example.backgroundapp.R
import com.example.backgroundapp.core.BaseActivity
import com.example.backgroundapp.core.FileSaveHelper
import com.example.backgroundapp.data.filters.FilterListener
import com.example.backgroundapp.data.filters.FilterViewAdapter
import com.example.backgroundapp.data.tools.EditingToolsAdapter
import com.example.backgroundapp.data.tools.ToolType
import com.example.backgroundapp.ui.MainActivity.Companion.eText
import com.squareup.picasso.Picasso
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.PhotoFilter
import ja.burhanrashid52.photoeditor.SaveFileResult
import ja.burhanrashid52.photoeditor.SaveSettings
import ja.burhanrashid52.photoeditor.ViewType
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

@Deprecated("Unlucko")
class PicEditor : BaseActivity(), OnPhotoEditorListener, View.OnClickListener,
    PropertiesBSFragment.Properties, EmojiBSFragment.EmojiListener,
    StickerBSFragment.StickerListener,
    EditingToolsAdapter.OnItemSelected, FilterListener {


    private lateinit var mShapeBuilder: ShapeBuilder
    private val dispatcher = Dispatchers.IO
    private lateinit var mPhotoEditor: PhotoEditor
    private var mPhotoEditorView: PhotoEditorView? = null
    private var mPropertiesBSFragment: PropertiesBSFragment? = null
    private var mEmojiBSFragment: EmojiBSFragment? = null
    private var mStickerBSFragment: StickerBSFragment? = null
    private var mTxtCurrentTool: TextView? = null
    private var mWonderFont: Typeface? = null
    private var mRvTools: RecyclerView? = null
    private lateinit var mRvFilters: RecyclerView
    private var mEditingToolsAdapter = EditingToolsAdapter(this)
    private val mFilterViewAdapter = FilterViewAdapter(this)
    private lateinit var mRootView: ConstraintLayout

    private val mConstraintSet = ConstraintSet()
    private var mIsFilterVisible = false

    private lateinit var mSaveFileHelper: FileSaveHelper

    @VisibleForTesting
    var mSaveImageUri:Uri? = null


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pic_editor)
        Picasso.get().load(eText.toString().trim()).into(mPhotoEditorView!!.source)
        Toast.makeText(this, "$eText", Toast.LENGTH_SHORT).show()
        initViews()

        handleIntentImage(mPhotoEditorView!!.source)

        mWonderFont = Typeface.createFromAsset(assets, "beyond_wonderland.ttf")
        mPropertiesBSFragment = PropertiesBSFragment()
        mEmojiBSFragment = EmojiBSFragment()
        mStickerBSFragment = StickerBSFragment()
        mStickerBSFragment!!.setStickerListener(this)
        mEmojiBSFragment!!.setEmojiListener(this)
        mPropertiesBSFragment!!.setPropertiesChangeList(this)

//        val background = findViewById<TextView>(R.id.ivBackground)
//        background.setOnClickListener{
//            lifecycleScope.launch(dispatcher) {
//                val wallpaperManager = WallpaperManager.getInstance(applicationContext)
//                wallpaperManager.setResource(R.id.imgGallery)
//            }
//        }

        val llmTools = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL,
            false
        )
        mRvTools!!.layoutManager = llmTools
        mRvTools!!.adapter = mEditingToolsAdapter

        val llmFilters = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL,
            false
        )
        mRvFilters.layoutManager = llmFilters
        mRvFilters.adapter = mFilterViewAdapter

        val pinchTextScalable = intent.getBooleanExtra(PINCH_TEXT_SCALABLE_INTENT_KEY, true)

        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView!!)
            .setPinchTextScalable(pinchTextScalable).build()

        mPhotoEditor.setOnPhotoEditorListener(this)

//        mPhotoEditorView!!.source.setImageResource(R.drawable.paris_tower)

        mSaveFileHelper = FileSaveHelper(this)

    }

    private fun handleIntentImage(source: ImageView) {
        if (intent == null) {
            return
        }

        when (intent.action) {
            Intent.ACTION_EDIT, ACTION_NEXTGEN_EDIT -> {
                try {
                    val uri = intent.data
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    source.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            else -> {
                val intentType = intent.type
                if (intentType != null && intentType.startsWith("image/")) {
                    val imageUri = intent.data
                    if (imageUri != null) {
                        source.setImageURI(imageUri)
                    }
                }
            }
        }
    }

    private fun initViews() {
        mPhotoEditorView = findViewById(R.id.photoEditorView)
//        find mPhotoEditorView = findViewById(R.id.photoEditorView)
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool)
        mRvTools = findViewById(R.id.rvConstraintTools)
        mRvFilters = findViewById(R.id.rvFilterView)
        mRootView = findViewById(R.id.rootView)

        val mImgUndo = findViewById<ImageView>(R.id.imgUndo)
        mImgUndo.setOnClickListener(this)

        val mImgRedo = findViewById<ImageView>(R.id.imgRedo)
        mImgRedo.setOnClickListener(this)

        val mImgCamera = findViewById<ImageView>(R.id.imgCamera)
        mImgCamera.setOnClickListener(this)

        val mImgGallery = findViewById<ImageView>(R.id.imgGallery)
        mImgGallery.setOnClickListener(this)

        val mImgSave = findViewById<ImageView>(R.id.imgSave)
        mImgSave.setOnClickListener(this)

        val mImgClose = findViewById<ImageView>(R.id.imgClose)
        mImgClose.setOnClickListener(this)

        val mBackground = findViewById<TextView>(R.id.ivBackground)
        mBackground.setOnClickListener(this)
    }

    override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {
        val textEditorDialogFragment = TextEditorDialogFragment
            .show(this, text, colorCode)
        textEditorDialogFragment.setOnTextEditorListener { inputText, colorCode ->
            mPhotoEditor.editText(rootView, inputText!!, colorCode)
            mTxtCurrentTool!!.setText(R.string.label_text)
        }
    }

    override fun onAddViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onAddViewListener() called with : viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onRemoveViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onRemoveViewListener() called with : viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onStartViewChangeListener(viewType: ViewType) {
        Log.d(
            TAG,
            "onStartViewChangeListener() called with : viewType = [$viewType]"
        )
    }

    override fun onStopViewChangeListener(viewType: ViewType) {
        Log.d(
            TAG,
            "onStopViewChangeListener() called with : viewType = [$viewType]"
        )
    }

    override fun onTouchSourceImage(event: MotionEvent) {
        Log.d(TAG, "onTouchView() called with: event = [$event]")
    }

//    private fun buildFileProviderUri(uri: Uri): Uri {
//        if (FileSaveHelper.isSdkHigherThan28()) {
//            return uri
//        }
//        val path: String = uri.path ?: throw IllegalArgumentException("URI Path Expected")
//
//        return FileProvider.getUriForFile(
//            this,
//            FILE_PROVIDER_AUTHORITY,
//            File(path)
//        )
//    }

    @SuppressLint("ResourceType", "MissingPermission")
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imgUndo -> mPhotoEditor.undo()
            R.id.imgRedo -> mPhotoEditor.redo()
            R.id.imgSave -> {
                saveBitmap()
                onBackPressed()
            }
            R.id.imgClose -> onBackPressed()
            R.id.imgCamera -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }

            R.id.imgGallery -> {

                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST)

            }
        }
    }

    private fun saveBitmap(){
        mPhotoEditor.saveAsBitmap(object : OnSaveBitmap {
            override fun onBitmapReady(saveBitmap: Bitmap) {
                Log.e("PhotoEditor","Bitmap Saved Successfully");
            }
        })
    }

    @RequiresPermission(allOf = [android.Manifest.permission.WRITE_EXTERNAL_STORAGE])
    private fun saveImage() {
        val fileName = System.currentTimeMillis().toString()+".png"
        val hasStoragePermission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        if (hasStoragePermission || FileSaveHelper.isSdkHigherThan28()){
            showLoading("Saving...")
            mSaveFileHelper.createFile(fileName, object : FileSaveHelper.OnFileCreatedResult{
                @SuppressLint("MissingPermission")
                override fun onFileCreateResult(
                    created: Boolean,
                    filePath: String?,
                    error: String?,
                    uri: Uri?
                ) {
                    lifecycleScope.launch {
                        if (created && filePath != null){
                            val saveSettings = SaveSettings.Builder()
                                .setClearViewsEnabled(true)
                                .setTransparencyEnabled(true)
                                .build()

                            val result = mPhotoEditor.saveAsFile(filePath, saveSettings)

                            if (result is SaveFileResult.Success){
                                mSaveFileHelper.notifyThatFileIsNowPubliclyAvailable(contentResolver)
                                hideLoading()
                                showSnackbar("Image saved")
                                mSaveImageUri = uri
                                mPhotoEditorView!!.source.setImageURI(mSaveImageUri)
                            }else{
                                hideLoading()
                                showSnackbar("Failed to save")
                            }
                        }else{
                            hideLoading()
                            error?.let { showSnackbar(error) }
                        }
                    }
                }

            })
        }else{
            requestPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_OK) {
            when(requestCode) {
                CAMERA_REQUEST -> {
                    mPhotoEditor.clearAllViews()
                    val photo = data?.extras?.get("data") as Bitmap?
                    mPhotoEditorView!!.source.setImageBitmap(photo)
                }

                PICK_REQUEST -> try {

                    mPhotoEditor.clearAllViews()
                    val uri = data?.data
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    mPhotoEditorView!!.source.setImageBitmap(bitmap)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onColorChanged(colorCode: Int) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeColor(colorCode))
        mTxtCurrentTool!!.setText(R.string.label_brush)
    }

    override fun onOpacityChanged(opacity: Int) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeOpacity(opacity))
        mTxtCurrentTool!!.setText(R.string.label_brush)
    }

    override fun onBrushSizeChanged(brushSize: Int) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeSize(brushSize.toFloat()))
        mTxtCurrentTool!!.setText(R.string.label_brush)
    }

    override fun onEmojiClick(emojiUnicode: String?) {
        mPhotoEditor.addEmoji(emojiUnicode!!)
        mTxtCurrentTool!!.setText(R.string.label_emoji)
    }

    override fun onStickerClick(bitmap: Bitmap) {
        mPhotoEditor.addImage(bitmap)
        mTxtCurrentTool!!.setText(R.string.label_sticker)
    }

    override fun onToolSelected(mToolType: ToolType) {
        when (mToolType) {
            ToolType.SHAPE -> {
                mPhotoEditor.setBrushDrawingMode(true)
                mTxtCurrentTool!!.setText(R.string.label_brush)
                mPropertiesBSFragment!!.show(
                    supportFragmentManager, mPropertiesBSFragment!!.tag
                )
            }

            //Subject to change ->
            ToolType.TEXT -> {
                val textEditorDialogFragment = TextEditorDialogFragment.show(this)
                textEditorDialogFragment.setOnTextEditorListener { inputText, colorCode ->
                    mPhotoEditor.addText(inputText!!, colorCode)
                    mTxtCurrentTool!!.setText((R.string.label_text))
                }
            }

            ToolType.ERASER -> {
                mPhotoEditor.brushEraser()
                mTxtCurrentTool!!.setText(R.string.label_eraser)
            }

            ToolType.FILTER -> {
                mTxtCurrentTool!!.setText(R.string.label_filter)
                showFilter(true)
            }

            ToolType.EMOJI -> mEmojiBSFragment!!.show(
                supportFragmentManager, mEmojiBSFragment!!.tag
            )

            ToolType.STICKER -> mStickerBSFragment!!.show(
                supportFragmentManager, mStickerBSFragment!!.tag
            )
        }
    }

    override fun onFilterSelected(photoFilter: PhotoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter)

    }

    @SuppressLint("MissingPermission")
    override fun isPermissionGranted(isGranted: Boolean, permission: String?) {
        if (isGranted) {
            saveImage()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showSaveDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.msg_save_image))
        builder.setPositiveButton("Save") { _: DialogInterface?, _: Int -> saveImage() }
        builder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        builder.setNeutralButton("Discard") { _: DialogInterface?, _: Int -> finish() }
        builder.create().show()
    }

    override fun onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false)
            mTxtCurrentTool!!.setText(R.string.app_name)
        } else if (!mPhotoEditor.isCacheEmpty) {
            showSaveDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun showFilter(isVisible: Boolean) {
        mIsFilterVisible = isVisible
        mConstraintSet.clone(mRootView)
        if (isVisible) {
            mConstraintSet.clear(mRvFilters.id, ConstraintSet.START)
            mConstraintSet.connect(
                mRvFilters.id, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START
            )

            mConstraintSet.connect(
                mRvFilters.id, ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
        } else {
            mConstraintSet.connect(
                mRvFilters.id, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
            mConstraintSet.clear(
                mRvFilters.id, ConstraintSet.END
            )
        }
        val changeBounds = ChangeBounds()
        changeBounds.duration = 350
        changeBounds.interpolator = AnticipateOvershootInterpolator(1.0f)
        androidx.transition.TransitionManager.beginDelayedTransition(mRootView, changeBounds)

        mConstraintSet.applyTo(mRootView)
    }

    companion object {
        const val FILE_PROVIDER_AUTHORITY = "com.burhanrashid52.photoediting.fileprovider"
        const val ACTION_NEXTGEN_EDIT = "action_nextgen_edit"
        private val TAG = PicEditor::class.java.simpleName
        private const val CAMERA_REQUEST = 52
        const val PICK_REQUEST = 53
        const val PINCH_TEXT_SCALABLE_INTENT_KEY = "PINCH_TEXT_SCALABLE"
    }
}