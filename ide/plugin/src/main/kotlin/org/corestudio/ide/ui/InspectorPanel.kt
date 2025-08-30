package org.corestudio.ide.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.components.fields.IntegerField
import java.awt.BorderLayout
import java.awt.GridBagLayout
import javax.swing.JPanel

// ide/plugin/src/main/kotlin/org/corestudio/ide/ui/InspectorPanel.kt
class InspectorPanel(project: Project) : JPanel(BorderLayout()) {

    private val widthField  = IntegerField("Width", 0, 10000).apply { value = 256 }
    private val heightField = IntegerField("Height", 0, 10000).apply { value = 256 }
    private val fpsField    = IntegerField().apply { value = 60 } // sin rango, si no lo necesitas

    init {
        val form = JPanel(GridBagLayout())
        // añade labels y fields a 'form' con tus constraints
        add(form, BorderLayout.CENTER)
    }
}

