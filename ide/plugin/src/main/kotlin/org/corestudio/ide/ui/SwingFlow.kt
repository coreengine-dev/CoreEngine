package org.corestudio.ide.ui

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import java.awt.Component
import javax.swing.SwingUtilities

fun <T> StateFlow<T>.collectInSwing(component: Component, block: (T)->Unit): Job {
    val scope = CoroutineScope(Dispatchers.Default)
    return scope.launch {
        this@collectInSwing.collect { v ->
            SwingUtilities.invokeLater { if (component.isShowing) block(v) }
        }
    }
}
