package org.corestudio.ide.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.corestudio.ide.services.EngineService

class RunAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        e.project?.getService(EngineService::class.java)?.start()
    }
}


