package com.example.backgroundapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backgroundapp.R
import com.example.backgroundapp.data.ColorPickerAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PropertiesBSFragment : BottomSheetDialogFragment(),OnSeekBarChangeListener
{

    private var mProperties: Properties?=null

    interface Properties{
        fun onColorChanged(colorCode:Int)
        fun onOpacityChanged(opacity:Int)
        fun onBrushSizeChanged(brushSize:Int)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?):View? {
        return inflater.inflate(R.layout.fragment_bottom_properties_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvColor : RecyclerView = view.findViewById(R.id.rvColors)
        val sbOpacity = view.findViewById<SeekBar>(R.id.sbOpacity)
        val sbBrushSize = view.findViewById<SeekBar>(R.id.sbSize)
        sbOpacity.setOnSeekBarChangeListener(this)
        sbBrushSize.setOnSeekBarChangeListener(this)

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rvColor.layoutManager = layoutManager
        rvColor.setHasFixedSize(true)
        val colorPickerAdapter = activity?.let { ColorPickerAdapter(it) }
        colorPickerAdapter?.setOnColorPickerClickListener(object :ColorPickerAdapter.OnColorPickerClickListener{
            override fun onColorPickerClickListener(color: Int) {
                if (mProperties != null) {
                    dismiss()
                    mProperties?.onColorChanged(color)
                }
            }

        })
        rvColor.adapter = colorPickerAdapter
    }
    fun setPropertiesChangeList(properties: Properties){
        mProperties = properties
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, p2: Boolean) {
        when(seekBar.id){
            R.id.sbOpacity->if (mProperties != null){
                mProperties?.onOpacityChanged(i)
            }
            R.id.sbSize->if (mProperties != null){
                mProperties?.onBrushSizeChanged(i)
            }
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) = Unit

    override fun onStopTrackingTouch(p0: SeekBar?) = Unit
}