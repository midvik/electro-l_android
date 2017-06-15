package ru.midvikus.electro_l.api

import android.content.Context
import android.graphics.Bitmap
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * Created by midvik on 14.06.17.
 */

fun getLatestImage(context: Context, onSuccess: (image: Bitmap) -> Unit, onError:(message: String) -> Unit) {
    getWallpaperFromApi(context, "60", onSuccess, onError)
}


fun getWallpaperFromApi(context: Context, slider: String, onSuccess: (image: Bitmap) -> Unit, onError:(message: String) -> Unit) {
    val queue: RequestQueue = Volley.newRequestQueue(context)
    val imageRequest = object: ImageRequestPost("http://electro.ntsomz.ru/electro/download",
            onSuccess, 0, 0, null, null, { onError(it.toString()) })
    {
        override fun getParams(): MutableMap<String, String>? {

            val params: MutableMap<String, String> = mutableMapOf()
            params.put("slider", slider)
            return params
        }

        override fun getBodyContentType(): String {
            return "application/x-www-form-urlencoded; charset=UTF-8;"
        }
    }
    queue.add(imageRequest)
}
