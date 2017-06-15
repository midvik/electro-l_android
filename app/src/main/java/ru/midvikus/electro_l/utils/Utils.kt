package ru.midvikus.electro_l.utils

import android.app.WallpaperManager
import android.graphics.Bitmap

/**
 * Created by midvik on 15.06.17.
 */

val UPDATE_INTERVAL_NAME = "UpdateInterval"

fun scaleDown(realImage: Bitmap, maxImageSize: Float,
              filter: Boolean): Bitmap {
    val ratio = Math.min(
            maxImageSize / realImage.width,
            maxImageSize / realImage.height)
    val width = Math.round(ratio * realImage.width)
    val height = Math.round(ratio * realImage.height)

    val newBitmap = Bitmap.createScaledBitmap(realImage, width,
            height, filter)
    return newBitmap
}

fun setWallpaper(wallpaperManager: WallpaperManager, bitmap: Bitmap) {
    wallpaperManager.setBitmap(
            scaleDown(bitmap,
                    minOf(wallpaperManager.desiredMinimumHeight.toFloat(),
                            wallpaperManager.desiredMinimumWidth.toFloat()),
                    false))
}