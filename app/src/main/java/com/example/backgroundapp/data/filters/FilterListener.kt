package com.example.backgroundapp.data.filters

import ja.burhanrashid52.photoeditor.PhotoFilter

fun interface FilterListener {
    fun onFilterSelected(photoFilter:PhotoFilter)
}