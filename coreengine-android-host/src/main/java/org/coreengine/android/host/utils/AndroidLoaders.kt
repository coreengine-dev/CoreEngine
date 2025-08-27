package org.coreengine.android.host.utils

fun loadBitmapFromAssets(path: String): android.graphics.Bitmap {
    val am = android.content.res.Resources.getSystem().assets // o inyecta Activity/App
    am.open(path).use { return android.graphics.BitmapFactory.decodeStream(it) }
}
