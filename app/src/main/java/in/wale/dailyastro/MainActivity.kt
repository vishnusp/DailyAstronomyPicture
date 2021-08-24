package `in`.wale.dailyastro

import `in`.wale.dailyastro.repository.AstroPicInfo
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
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
        presenter.fetchDailyAstroPicInfo()
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
        astroImage.setImageResource(R.drawable.ic_launcher_background)
        titleTextView.text = dailyAstroPicInfo?.title
        descriptionTextView.text = dailyAstroPicInfo?.description
    }

}