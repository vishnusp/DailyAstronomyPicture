package `in`.wale.dailyastro.repository

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.LruCache
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection


class DataRepository(private val preferences: SharedPreferences) {

    companion object {
        private const val TAG = "DataRepository"
        private const val KEY_ASTRO_PIC_INFO = "astro_pic_info"
        private const val KEY_ASTRO_PIC_BITMAP_CACHE = "astro_pic_cache"
        private const val URL =
            "https://api.nasa.gov/planetary/apod?api_key=5L2PUF5sc50uLetIYgXGa9ZrfxHRADxWUYuBnoWd"
    }

    private val lru: LruCache<String, Bitmap> = LruCache(1024)


    suspend fun getDailyAstroPicInfo(): AstroPicInfo? {
        return withContext(Dispatchers.IO) {
            val urlConnection: URLConnection?

            try {
                urlConnection = URL(URL).openConnection()
            } catch (e: MalformedURLException) {
                Log.e(TAG, "MalformedURLException $e")
                return@withContext null
            } catch (e: IOException) {
                Log.e(TAG, "IOException $e")
                return@withContext null
            }
            val output: String?
            try {
                val input = BufferedInputStream(urlConnection.inputStream)
                val reader = BufferedReader(InputStreamReader(input))
                val dataBuilder = StringBuilder(input.available())
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    dataBuilder.append(line).append('\n')
                }
                output = dataBuilder.toString()
            } catch (e: IOException) {
                Log.e(TAG, "IOException $e")
                return@withContext null
            }

            try {
                lru.remove(KEY_ASTRO_PIC_BITMAP_CACHE)
                preferences.edit().putString(KEY_ASTRO_PIC_INFO, output).apply()
                return@withContext parseJson(JSONObject(output))
            } catch (e: JSONException) {
                Log.e(TAG, "Error parsing data $e")
            }
            return@withContext null
        }
    }

    @Throws(JSONException::class)
    private suspend fun parseJson(jsonObject: JSONObject): AstroPicInfo =
        AstroPicInfo(
            jsonObject.getString("date"),
            jsonObject.getString("title"),
            jsonObject.getString("explanation"),
            jsonObject.getString("url"),
            getBitmapFromURL(jsonObject.getString("url"))
        )

    private suspend fun getBitmapFromURL(src: String?): Bitmap? {
        val bitmap = lru.get(KEY_ASTRO_PIC_BITMAP_CACHE)
        return bitmap ?: withContext(Dispatchers.IO) {
            return@withContext try {
                val url = URL(src)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val myBitmap = BitmapFactory.decodeStream(connection.inputStream)
                lru.put(KEY_ASTRO_PIC_BITMAP_CACHE, myBitmap)
                myBitmap
            } catch (e: IOException) {
                Log.e(TAG, "IOException $e")
                null
            }
        }
    }

    fun getDailyAstroPicInfoCache(): AstroPicInfo? {
        try {
            val jsonObject =
                JSONObject(preferences.getString(KEY_ASTRO_PIC_INFO, null) ?: return null)
            return AstroPicInfo(
                jsonObject.getString("date"),
                jsonObject.getString("title"),
                jsonObject.getString("explanation"),
                jsonObject.getString("url"),
                lru.get(KEY_ASTRO_PIC_BITMAP_CACHE)
            )
        } catch (e: JSONException) {
            Log.e(TAG, "Error parsing data $e")
        }
        return null
    }

}
