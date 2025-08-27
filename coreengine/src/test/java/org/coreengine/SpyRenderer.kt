package org.coreengine

import org.coreengine.camera.Camera
import org.coreengine.render.Renderer
import org.coreengine.scene.Scene
import java.util.concurrent.CountDownLatch


class BoomScene(private val latch: CountDownLatch) : Scene() {
    override fun onDraw(r: Renderer, c: Camera) {
        latch.countDown()
        throw RuntimeException("boom")
    }
}



