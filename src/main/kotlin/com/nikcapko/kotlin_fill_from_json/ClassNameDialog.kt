package com.nikcapko.kotlin_fill_from_json

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.awt.GridLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class ClassNameDialog(
    private val project: Project
) : DialogWrapper(project) {

    private val classNameField = JTextField("NewClass", 20)

    init {
        title = "Generate Constructor Call"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(GridLayout(0, 1, 5, 5))
        panel.add(JLabel("Class Name:"))
        panel.add(classNameField)
        return panel
    }

    fun getClassName(): String {
        return classNameField.text.trim().ifEmpty { "NewClass" }
    }
}