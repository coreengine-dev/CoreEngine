package org.corestudio.ide

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import org.corestudio.ide.services.EngineService

class CoreStudioPlugin : ProjectActivity {
    override suspend fun execute(project: Project) {
        project.service<EngineService>().init(project)
    }
}
