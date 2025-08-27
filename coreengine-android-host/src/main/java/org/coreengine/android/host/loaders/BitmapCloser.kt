package org.coreengine.android.host.loaders

import android.graphics.Bitmap

object BitmapCloser : (Bitmap) -> Unit {
    override fun invoke(b: Bitmap) { if (!b.isRecycled) b.recycle() }
}