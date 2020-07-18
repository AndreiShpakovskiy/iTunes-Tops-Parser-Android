package com.shpakovskiy.itunestops.entity

class Track {
    var name: String = ""
    var artist: String = ""
    var imageUrl: String = ""
    var previewUrl: String = ""

    override fun toString(): String {
        return "Track(name='$name', artist='$artist', imageUrl='$imageUrl', previewUrl='$previewUrl')"
    }
}