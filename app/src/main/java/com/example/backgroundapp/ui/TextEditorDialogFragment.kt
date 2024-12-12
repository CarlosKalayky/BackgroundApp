package com.example.backgroundapp.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backgroundapp.R
import com.example.backgroundapp.data.ColorPickerAdapter

class TextEditorDialogFragment : DialogFragment() {
    private var mAddTextEditText: EditText? = null
    private var mAddTextDoneTextView: TextView? = null
    private var mInputMethodManager: InputMethodManager? = null
    private var mColorCode = 0
    private var mTextEditor: TextEditor? = null

    fun interface TextEditor {
        fun onDone(inputText: String?, colorCode: Int)
    }

    override fun onStart() {
        super.onStart()

        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_text_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAddTextEditText = view.findViewById(R.id.eTAddText)
        mInputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        mAddTextDoneTextView = view.findViewById(R.id.tvDone)

        val addTextColorPickerRecyclerView =
            view.findViewById<RecyclerView>(R.id.add_text_color_picker_recycler_view)

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        addTextColorPickerRecyclerView.layoutManager = layoutManager

        val colorPickerAdapter = ColorPickerAdapter(activity)
        colorPickerAdapter.setOnColorPickerClickListener { color ->
            mColorCode = color
            mAddTextEditText!!.setTextColor(color)
        }

        mAddTextEditText!!.setText(requireArguments().getString(EXTRA_INPUT_TEXT))
        mColorCode = requireArguments().getInt(EXTRA_COLOR_CODE)

        mAddTextEditText!!.setTextColor(mColorCode)
        mInputMethodManager!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        mAddTextDoneTextView!!.setOnClickListener{view ->
            mInputMethodManager!!.hideSoftInputFromWindow(view.windowToken, 0)
            val inputText = mAddTextEditText!!.text.toString()
            if(!TextUtils.isEmpty(inputText) && mTextEditor != null){
                mTextEditor!!.onDone(inputText, mColorCode)
            }
        }
    }

    fun setOnTextEditorListener(textEditor: TextEditor){
        mTextEditor = textEditor
    }
    companion object{
        val TAG = TextEditorDialogFragment :: class.java.simpleName
        const val EXTRA_INPUT_TEXT = "extra_input_text"
        const val EXTRA_COLOR_CODE = "extra_color_code"
        fun show(appCompatActivity: AppCompatActivity,
            inputText: String?,
                 colorCode:Int
        ):TextEditorDialogFragment
        {
            val args = Bundle()
            args.putString(EXTRA_INPUT_TEXT, inputText)
            args.putInt(EXTRA_COLOR_CODE, colorCode)

            val fragment = TextEditorDialogFragment()
            fragment.arguments = args
            fragment.show(appCompatActivity.supportFragmentManager, TAG)
            return fragment
        }
        fun show(appCompatActivity: AppCompatActivity)
        :TextEditorDialogFragment
        {
            return show(appCompatActivity, "", ContextCompat.getColor(appCompatActivity, R.color.white))
        }
    }
}