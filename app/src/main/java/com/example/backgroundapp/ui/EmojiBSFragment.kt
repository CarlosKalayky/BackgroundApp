package com.example.backgroundapp.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backgroundapp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EmojiBSFragment : BottomSheetDialogFragment() {
    private var mEmojiListener: EmojiListener? = null

    fun interface EmojiListener {
        fun onEmojiClick(emojiUnicode: String?)
    }

    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback = object :
        BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            //Empty
        }

    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_bottom_sticker_emoji_dialog, null)
        dialog.setContentView(contentView)
        val params: CoordinatorLayout.LayoutParams =
            (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams

        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior) {
            behavior.addBottomSheetCallback(mBottomSheetBehaviorCallback)
        }

        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))

        val rvEmoji: RecyclerView = contentView.findViewById(R.id.rvEmoji)
        val gridLayoutManager = GridLayoutManager(activity, 5)
        rvEmoji.layoutManager = gridLayoutManager
        val emojiAdapter: EmojiAdapter = EmojiAdapter()
        rvEmoji.adapter = emojiAdapter
    }

    fun setEmojiListener(emojiListener: EmojiListener) {
        mEmojiListener = emojiListener
    }

    inner class EmojiAdapter : RecyclerView.Adapter<EmojiAdapter.ViewHolder?>() {
        private var emojisList = getEmojis(requireContext())


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.row_emoji, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: EmojiAdapter.ViewHolder, position: Int) {
            holder.txtEmoji.text = emojisList[position]
        }

        override fun getItemCount(): Int = emojisList.size

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            var txtEmoji :TextView = v.findViewById(R.id.txtEmoji)

            init {
                v.setOnClickListener{
                    if(mEmojiListener != null){
                        mEmojiListener!!.onEmojiClick(emojisList[layoutPosition])
                    }
                    dismiss()
                }
            }
        }

    }

    companion object {

        /**
         * Provide the list of emoji in form of unicode string
         *
         * @param context context
         * @return list of emoji unicode
         */
        fun getEmojis(context: Context?): ArrayList<String> {
            val convertedEmojiList = ArrayList<String>()
            val emojiList = context!!.resources.getStringArray(R.array.photo_editor_emoji)
            for (emojiUnicode in emojiList) {
                convertedEmojiList.add(convertEmoji(emojiUnicode))
            }
            return convertedEmojiList
        }

        private fun convertEmoji(emoji: String): String {
            return try {
                val convertEmojiToInt = emoji.substring(2).toInt(16)
                String(Character.toChars(convertEmojiToInt))
            } catch (e: NumberFormatException) {
                ""
            }
        }
    }


}