package org.corestudio.ide.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode

class SceneGraphPanel(project: Project) : JPanel() {
    private val root = DefaultMutableTreeNode("Scene")
    private val tree = Tree(root)
    init {
        root.add(DefaultMutableTreeNode("Camera"))
        root.add(DefaultMutableTreeNode("Entity"))
        root.add(DefaultMutableTreeNode("Symbol (TCD)"))
        add(JBScrollPane(tree))
    }
}
