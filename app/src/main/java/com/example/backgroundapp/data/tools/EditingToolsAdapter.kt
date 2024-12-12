package com.example.backgroundapp.data.tools

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.backgroundapp.R

class EditingToolsAdapter(private val mOnItemSelected:OnItemSelected) :
RecyclerView.Adapter<EditingToolsAdapter.ViewHolder>()
{
    private val mToolList:MutableList<ToolModel> = ArrayList()

    internal inner class ToolModel
        (val mToolName:String,
         val mToolIcon:Int,
         val mToolType: ToolType)

    fun interface OnItemSelected {
        fun onToolSelected(mToolType: ToolType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_editing_tools, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mToolList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mToolList[position]
        holder.txtTool.text = item.mToolName
        holder.imgTool.setImageResource(item.mToolIcon)
    }

    inner class ViewHolder (v: View):RecyclerView.ViewHolder(v){
        val imgTool : ImageView
        val txtTool: TextView
        init {
            imgTool = v.findViewById(R.id.imgTool)
            txtTool = v.findViewById(R.id.txtTool)
            v.setOnClickListener{
                mOnItemSelected.onToolSelected(
                    mToolList[layoutPosition].mToolType
                )
            }
        }
    }
    init {
        mToolList.add(ToolModel("Shape", R.drawable.ic_brush, ToolType.SHAPE))
        mToolList.add(ToolModel("Text", R.drawable.ic_text, ToolType.TEXT))
        mToolList.add(ToolModel("Eraser", R.drawable.ic_eraser, ToolType.ERASER))
        mToolList.add(ToolModel("Filter", R.drawable.ic_photo_filter, ToolType.FILTER))
        mToolList.add(ToolModel("Emoji", R.drawable.ic_insert_emoticon, ToolType.EMOJI))
        mToolList.add(ToolModel("Sticker", R.drawable.ic_sticker, ToolType.STICKER))
    }

}