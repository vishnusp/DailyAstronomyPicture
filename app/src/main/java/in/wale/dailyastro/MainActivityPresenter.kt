package `in`.wale.dailyastro

import `in`.wale.dailyastro.repository.AstroPicInfo
import `in`.wale.dailyastro.repository.DataRepository
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivityPresenter(private val view: View, preferences: SharedPreferences) : CoroutineScope {

    private val repository = DataRepository(preferences)

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    fun fetchDailyAstroPicInfo() {
        view.showProgress()
        launch {
            val data = repository.getDailyAstroPicInfo()
            withContext(Dispatchers.Main) {
                view.showContentViews(data)
            }
        }
    }

    interface View {
        fun showProgress()

        fun showContentViews(dailyAstroPicInfo: AstroPicInfo?)

    }
}