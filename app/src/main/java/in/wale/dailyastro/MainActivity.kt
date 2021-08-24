package `in`.wale.dailyastro

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.Group

class MainActivity : AppCompatActivity(), MainActivityPresenter.View {

    private lateinit var astroImage: AppCompatImageView
    private lateinit var titleTextView: AppCompatTextView
    private lateinit var descriptionTextView: AppCompatTextView
    private lateinit var progressBar: ProgressBar
    private lateinit var contentGroup: Group

    private lateinit var presenter: MainActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = MainActivityPresenter(this, getSharedPreferences())
        astroImage = findViewById(R.id.astroImage)
        titleTextView = findViewById(R.id.titleTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        progressBar = findViewById(R.id.progress)
        contentGroup = findViewById(R.id.contentGroup)
        presenter.fetchDailyAstroPicInfo(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    private fun getSharedPreferences() =
        getSharedPreferences("DailyAstroPic", MODE_PRIVATE)

    override fun showProgress() {
        progressBar.visibility = VISIBLE
        contentGroup.visibility = GONE
    }

    override fun showContentViews(dailyAstroPicInfo: AstroPicInfo?) {
        progressBar.visibility = GONE
        contentGroup.visibility = VISIBLE
        astroImage.setImageBitmap(dailyAstroPicInfo?.bitmap)
        titleTextView.text = dailyAstroPicInfo?.title
        descriptionTextView.text = dailyAstroPicInfo?.description
    }

    override fun showOldDataAlert() {
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setMessage(R.string.alert_msg_old_data_shown)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

}