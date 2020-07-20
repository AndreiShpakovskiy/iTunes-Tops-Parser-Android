package com.shpakovskiy.itunestops

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.shpakovskiy.itunestops.adapter.TracksListAdapter
import com.shpakovskiy.itunestops.entity.Track
import com.shpakovskiy.itunestops.parser.TracksRssParser
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private val downloadData by lazy { DownloadData(this, tracksHolder) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=25/xml")

        tracksHolder.setOnItemClickListener { _, _, _, id ->
            run {
                //openUrl(tracks[id.toInt()].previewUrl)
                //Toast.makeText(this, tracks[id.toInt()].trackPageUrl, 3).show()
                try {
                    val url = tracks[id.toInt()].previewUrl
                    val mediaPlayer = MediaPlayer()
                    mediaPlayer.setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build())
                    mediaPlayer.setDataSource(url)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData.cancel(true)
    }

    private companion object {
        lateinit var tracks: List<Track>

        private class DownloadData(context: Context, listView: ListView) : AsyncTask<String, Void, String>() {
            private val TAG = "DownloadData"

            var context: Context by Delegates.notNull()
            var listView: ListView by Delegates.notNull()

            init {
                this.context = context
                this.listView = listView
            }

            override fun doInBackground(vararg url: String?): String {
                Log.d(TAG, "doInBackground starts with: ${url[0]}")

                val rssFeed = downloadRssFeed(url[0])
                if (rssFeed.isEmpty()) {
                    Log.e(TAG, "doInBackground: Error downloading feed")
                }

                return rssFeed
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)

                val tracksRssParser = TracksRssParser()
                tracksRssParser.parse(result)

                tracks = tracksRssParser.tracks
                val tracksListAdapter = TracksListAdapter(context, R.layout.tracks_list_item, tracksRssParser.tracks)
                listView.adapter = tracksListAdapter
            }

            private fun downloadRssFeed(urlPath: String?): String {
                return URL(urlPath).readText()
            }
        }
    }
}