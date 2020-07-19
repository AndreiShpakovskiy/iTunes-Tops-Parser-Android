package com.shpakovskiy.itunestops

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.shpakovskiy.itunestops.adapter.TracksListItemAdapter
import com.shpakovskiy.itunestops.entity.Track
import com.shpakovskiy.itunestops.parser.TracksRssParser
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import java.nio.charset.StandardCharsets
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private val downloadData by lazy { DownloadData(this, tracksHolder) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=25/xml")
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData.cancel(true)
    }

    companion object {
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

                //val arrayAdapter = ArrayAdapter<Track>(context, R.layout.tracks_list_item, tracksRssParser.tracks)
                //listView.adapter = arrayAdapter

                val tracksListAdapter = TracksListItemAdapter(context, R.layout.tracks_list_item, tracksRssParser.tracks)
                listView.adapter = tracksListAdapter
            }

            private fun downloadRssFeed(urlPath: String?): String {
                return URL(urlPath).readText(StandardCharsets.UTF_8)
            }
        }
    }
}