package com.shpakovskiy.itunestops.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.shpakovskiy.itunestops.MainActivity
import com.shpakovskiy.itunestops.entity.Track
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.tracks_list_item.view.*
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import kotlin.properties.Delegates


class TracksListItemAdapter(context: Context, private val resource: Int, private val tracks: List<Track>):
    ArrayAdapter<Track>(context, resource) {

    private val TAG = "TracksListItemAdapter"
    private var iconLoader: IconLoader? = null
    private val layoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: layoutInflater.inflate(resource, parent, false)

        val currentTrack = tracks[position]

        iconLoader = IconLoader(view.trackImage)
        iconLoader!!.execute(currentTrack.iconUrl)
        view.trackName.text = currentTrack.name
        view.artistName.text = currentTrack.artist

        return view
    }

    override fun getCount(): Int {
        return tracks.size
    }

    companion object {
        private class IconLoader(imageView: ImageView) : AsyncTask<String, Void, Bitmap>() {
            private val TAG = "IconLoader"

            var imageView: ImageView by Delegates.notNull()

            init {
                this.imageView = imageView
            }

            override fun doInBackground(vararg url: String?): Bitmap? {
                Log.d(TAG, "doInBackground starts with: ${url[0]}")

                val trackIcon = getPictureByUrl(url[0].toString())
                if (trackIcon == null) {
                    Log.e(TAG, "doInBackground: Error downloading icon")
                }

                return trackIcon
            }

            override fun onPostExecute(result: Bitmap?) {
                super.onPostExecute(result)

                imageView.setImageBitmap(result)
            }

            private fun getPictureByUrl(imageUrl: String): Bitmap? {
                try {
                    return BitmapFactory.decodeStream(URL(imageUrl).content as InputStream)
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return null
            }
        }
    }
}