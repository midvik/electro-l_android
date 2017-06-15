package ru.midvikus.electro_l.activity

import android.app.WallpaperManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import ru.midvikus.electro_l.R
import ru.midvikus.electro_l.api.getLatestImage
import ru.midvikus.electro_l.service.ElectroWallpaperService
import ru.midvikus.electro_l.utils.UPDATE_INTERVAL_NAME
import ru.midvikus.electro_l.utils.setWallpaper

/**
 * Created by midvik on 14.06.17.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val wallpaperManager = WallpaperManager.getInstance(this)

        verticalLayout {
            val image = imageView()

            button(R.string.set_background) {
                onClick {
                    getLatestImage(applicationContext, {
                        setWallpaper(wallpaperManager, it)
                        image.setImageBitmap(it)

                    }, {
                        println(it)
                    })
                }
            }

            val updateInterval = editText("60")
            removeView(updateInterval)

            linearLayout {
                textView(R.string.update_interval)
            }.addView(updateInterval)

            button(R.string.start_service) {
                onClick {
                    val serviceIntent = Intent(applicationContext, ElectroWallpaperService::class.java)
                    serviceIntent.putExtra(UPDATE_INTERVAL_NAME, updateInterval.text.toString())
                    startService(serviceIntent)
                }
            }

            button(R.string.stop_service) {
                onClick {
                    stopService(Intent(applicationContext, ElectroWallpaperService::class.java))
                }
            }
        }
    }
}