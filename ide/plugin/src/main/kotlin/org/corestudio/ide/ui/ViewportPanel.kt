package org.corestudio.ide.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import org.corestudio.ide.services.TelemetryBus
import java.awt.BorderLayout
import javax.swing.JPanel

class ViewportPanel(project: Project) : JBPanel<ViewportPanel>(BorderLayout()) {
    private val canvas = JPanel() // aquí irá el host GL/Canvas de CoreEngine
    private val hud = JBLabel("FPS: 0  Draws: 0  update:0ms  render:0ms")

    init {
        val splitter = JBSplitter(true, 0.9f)
        splitter.firstComponent = canvas
        splitter.secondComponent = hud
        add(splitter, BorderLayout.CENTER)

        TelemetryBus.state.collectInSwing(this) { t ->
            hud.text = "FPS: ${t.fps}  Draws: ${t.drawCalls}  update:${t.msUpdate}ms  render:${t.msRender}ms"
        }
    }
}
