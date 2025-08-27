package org.coreengine.android.host.utils

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.coreengine.resource.Loader

/*
class AssetBitmapLoader(private val assets: AssetManager) : Loader<Bitmap> {
    override fun load(id: String): Bitmap =
        assets.open(id).use { BitmapFactory.decodeStream(it) }
}

object BitmapCloser : Closer<Bitmap> {
    override fun close(value: Bitmap) { if (!value.isRecycled) value.recycle() }
}
*/
