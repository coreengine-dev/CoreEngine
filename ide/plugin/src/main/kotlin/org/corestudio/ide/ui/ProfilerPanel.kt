package org.corestudio.ide.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBLabel
import javax.swing.JPanel

class ProfilerPanel(project: Project) : JPanel() {
    init { add(JBLabel("Timeline / Captures (WIP)")) }
}
