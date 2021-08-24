package `in`.wale.dailyastro.repository

import android.graphics.Bitmap

data class AstroPicInfo(
    val date: String,
    val title: String,
    val description: String?,
    val imgUrl: String,
    val bitmap: Bitmap?
)
