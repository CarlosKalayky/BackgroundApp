package com.example.backgroundapp.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.backgroundapp.R

class ColorPickerAdapter internal constructor(
    private var context: Context?,
    colorPickerColor: List<Int>
) : RecyclerView.Adapter<ColorPickerAdapter.ViewHolder?>() {
    private var inflater: LayoutInflater
    private var colorPickers: List<Int>
    private var onColorPickerClickListener: OnColorPickerClickListener? = null

    internal constructor(context: Context?) : this(context, getDefaultColors(context)) {

        this.context = context
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ColorPickerAdapter.ViewHolder {
        val view = inflater.inflate(R.layout.color_picker_item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorPickerAdapter.ViewHolder, position: Int) {
        holder.colorPickerView.setBackgroundColor(colorPickers[position])
    }

    fun setOnColorPickerClickListener(onColorPickerClickListener: OnColorPickerClickListener) {
        this.onColorPickerClickListener = onColorPickerClickListener
    }

    override fun getItemCount(): Int = colorPickers.size

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var colorPickerView: View

        init {
            colorPickerView = v.findViewById(R.id.color_picker_view)
            v.setOnClickListener {
                if (onColorPickerClickListener != null) onColorPickerClickListener!!.onColorPickerClickListener(
                    colorPickers[adapterPosition]
                )
            }
        }
    }

    fun interface OnColorPickerClickListener {
        fun onColorPickerClickListener(color:Int)
    }
    companion object{
        fun getDefaultColors(context: Context?):List<Int> {
            val colorPickerColors = ArrayList<Int>()
            colorPickerColors.add(ContextCompat.getColor(context!!, R.color.blue_color_picker))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.brown_color_picker))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.green_color_picker))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.orange_color_picker))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.red_color_picker))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.black))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.red_orange_color_picker))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.sky_blue_color_picker))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.violet_color_picker))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.white))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.yellow_color_picker))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.blue_color_picker))
            colorPickerColors.add(
                ContextCompat.getColor(
                    context,
                    R.color.yellow_color_picker
                )
            )
            return colorPickerColors
        }
    }

    init {
        inflater = LayoutInflater.from(context)
        this.colorPickers = colorPickerColor
    }
}