package `in`.wale.dailyastro

import `in`.wale.dailyastro.repository.DataRepository
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext


class MainActivityPresenter(private val view: View, preferences: SharedPreferences) : CoroutineScope {

    private val repository = DataRepository(preferences)

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    fun fetchDailyAstroPicInfo(context: Context?) {
        if (!isOnline(context)) {
            val data = repository.getDailyAstroPicInfoCache()
            view.showContentViews(data)
            if (data == null || !isTodayData(data.date)) {
                view.showOldDataAlert()
            }
            return
        }
        view.showProgress()
        launch {
            val data = repository.getDailyAstroPicInfo()
            withContext(Dispatchers.Main) {
                view.showContentViews(data)
            }
        }
    }

    //To know whether the device is online or not.
    private fun isOnline(context: Context?): Boolean {
        if (null == context) {
            return false
        }
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting == true
    }

    private fun isTodayData(value: String): Boolean {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(value)
        date ?: return false
        val today = Date()
        return today.date == date.date && today.month == date.month && today.year == date.year

    }

    fun onDestroy() {
        job.cancel()
    }

    interface View {
        fun showProgress()

        fun showContentViews(dailyAstroPicInfo: AstroPicInfo?)

        fun showOldDataAlert()

    }
}