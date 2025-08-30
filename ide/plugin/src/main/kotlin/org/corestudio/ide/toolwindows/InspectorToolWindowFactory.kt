package org.corestudio.ide.toolwindows

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import org.corestudio.ide.ui.InspectorPanel

class InspectorToolWindowFactory: ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val content = ContentFactory.getInstance().createContent(InspectorPanel(project), "Inspector", false)
        toolWindow.contentManager.addContent(content)
    }
}