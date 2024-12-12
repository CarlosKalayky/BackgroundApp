package com.example.backgroundapp.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backgroundapp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StickerBSFragment : BottomSheetDialogFragment() {
    private var mStickerListener: StickerListener? = null

    fun setStickerListener(stickerListener: StickerListener?) {
        mStickerListener = stickerListener
    }

    fun interface StickerListener {
        fun onStickerClick(bitmap: Bitmap)
    }

    private val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}

    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        val contentView = View.inflate(context, R.layout.fragment_bottom_sticker_emoji_dialog, null)

        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))

        val rvEmoji = contentView.findViewById<RecyclerView>(R.id.rvEmoji)
        val gridLayoutManager = GridLayoutManager(activity, 3)
        rvEmoji.layoutManager = gridLayoutManager

        val stickerAdapter = StickerAdapter()
        rvEmoji.adapter = stickerAdapter


    }

    inner class StickerAdapter : RecyclerView.Adapter<StickerAdapter.ViewHolder>() {
        var stickerList = intArrayOf(R.drawable.aa, R.drawable.bb)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): StickerAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_sticker, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: StickerAdapter.ViewHolder, position: Int) {
            holder.imgSticker.setImageResource(stickerList[position])
        }

        override fun getItemCount(): Int = stickerList.size

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            var imgSticker: ImageView

            init {
                imgSticker = v.findViewById(R.id.ivSticker)
                v.setOnClickListener {
                    if (mStickerListener != null) {
                        mStickerListener!!.onStickerClick(
                            BitmapFactory.decodeResource(resources, stickerList[layoutPosition])
                        )
                    }
                    dismiss()
                }
            }
        }

    }

}