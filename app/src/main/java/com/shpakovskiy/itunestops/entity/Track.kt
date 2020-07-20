package com.shpakovskiy.itunestops.entity

import android.graphics.Bitmap

class Track {
    var name: String = ""
    var artist: String = ""
    var iconUrl: String = ""
    var icon: Bitmap? = null
    var previewUrl: String = ""

    override fun toString(): String {
        return """name = $name
                  artist = $artist
                  previewUrl = $previewUrl  
                  """.trimIndent()
    }
}