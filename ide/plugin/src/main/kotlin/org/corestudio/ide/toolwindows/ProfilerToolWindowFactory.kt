package org.corestudio.ide.toolwindows

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import org.corestudio.ide.ui.ProfilerPanel

class ProfilerToolWindowFactory: ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val content = ContentFactory.getInstance().createContent(ProfilerPanel(project), "Profiler", false)
        toolWindow.contentManager.addContent(content)
    }
}