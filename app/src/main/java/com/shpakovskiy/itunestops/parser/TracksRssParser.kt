package com.shpakovskiy.itunestops.parser

/**
 * Here is custom implementation of XML parser,
 * because built-in one is not convenient for the purposes it used for in this project.
 */

import android.util.Log
import com.shpakovskiy.itunestops.entity.Track
import java.lang.Exception

class TracksRssParser : Parser {
    private val TAG = "TracksRssParser"

    private val entriesRegex: Regex = Regex("""<entry>((?:(?!<entry>|</entry>)[\s\S])*)</entry>""")
    private val trackNameRegex: Regex = Regex("""<im:name>((?:(?!<im:name|</im:name)[\s\S])*)</im:name>""")
    private val artistNameRegex: Regex = Regex("""<im:artist.*>((?:(?!<im:artist.*|</im:artist>)[\s\S])*)</im:artist>""")
    private val imageUrlRegex: Regex = Regex("""<im:image height="[0-9]{3}">((?:(?!<im:image height="[0-9]{3}">|</im:image)[\s\S])*)</im:image>""")
    private val previewUrlRegex: Regex = Regex("""<link\s+title="Preview".*href="((?:(?!<link\s+title="Preview".*href="|")[\s\S])*)"""")

    private val tracks = ArrayList<Track>()
    
    override fun parse(xmlData: String?): Boolean {
        Log.d(TAG, "Parsing started")

        try {
            val entries = getAllEntries(xmlData!!)
            for (entry: String in entries) {
                tracks.add(entryToTrack(entry))
            }
            Log.d(TAG, "Parsing finished successfully")
            return true
        }
        catch (e: Exception) {
            Log.d(TAG, "Parsing failed")
            e.printStackTrace()
        }
        return false
    }

    private fun getAllEntries(xmlData: String): List<String> {
        return entriesRegex.findAll(xmlData).map { it.groupValues[1] }.toList()
    }

    private fun entryToTrack(entry: String): Track {
        val newTrack = Track()
        newTrack.artist = artistNameRegex.find(entry)?.groupValues?.get(1).toString()
        newTrack.name = trackNameRegex.find(entry)?.groupValues?.get(1).toString()
        newTrack.previewUrl = previewUrlRegex.find(entry)?.groupValues?.get(1).toString()
        newTrack.imageUrl = imageUrlRegex.find(entry)?.groupValues?.get(1).toString()
        return newTrack
    }
}