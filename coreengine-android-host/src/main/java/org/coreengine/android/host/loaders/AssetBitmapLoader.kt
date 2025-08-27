// org.coreengine.android.host.utils.AndroidLoaders.kt
package org.coreengine.android.host.loaders

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.coreengine.resource.Loader

/*class AssetBitmapLoader(private val assets: AssetManager) : Loader<Bitmap> {
    override fun load(id: String): Bitmap =
        assets.open(id).use { BitmapFactory.decodeStream(it) ?: error("Decode failed: $id") }
}

*//*object BitmapCloser : (Bitmap) -> Unit {
    override fun invoke(bmp: Bitmap) { if (!bmp.isRecycled) bmp.recycle() }
}*//*


val BitmapCloser: (Bitmap) -> Unit = { it.recycle() }*/


class AssetBitmapLoader(private val assets: AssetManager) : Loader<Bitmap> {
    override fun load(id: String): Bitmap =
        assets.open(id).use { BitmapFactory.decodeStream(it) }
}

