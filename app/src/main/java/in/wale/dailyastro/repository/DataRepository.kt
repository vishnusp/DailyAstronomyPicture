package `in`.wale.dailyastro.repository

import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection


class DataRepository(private val preferences: SharedPreferences) {

    companion object {
        private const val TAG = "DataRepository"
        private const val KEY_ASTRO_PIC_INFO = "astro_pic_info"
        private const val URL =
            "https://api.nasa.gov/planetary/apod?api_key=5L2PUF5sc50uLetIYgXGa9ZrfxHRADxWUYuBnoWd"
    }


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
                preferences.edit().putString(KEY_ASTRO_PIC_INFO, output)
                return@withContext parseJson(JSONObject(output))
            } catch (e: JSONException) {
                Log.e("JSON Parser", "Error parsing data $e")
            }
            return@withContext null
        }
    }

    @Throws(JSONException::class)
    private fun parseJson(jsonObject: JSONObject): AstroPicInfo =
        AstroPicInfo(
            jsonObject.getString("date"),
            jsonObject.getString("title"),
            jsonObject.getString("explanation"),
            jsonObject.getString("url")
        )
}
