package com.nikcapko.kotlin_fill_from_json

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

class JsonToKotlinAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        showJsonInputDialog(project)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = true
    }

    private fun showJsonInputDialog(project: Project) {
        val inputDialog = JsonInputDialog(project)
        if (inputDialog.showAndGet()) {
            val jsonText = inputDialog.getJsonText()
            val className = inputDialog.getClassName()
            val generator = AdvancedConstructorCallGenerator()

            try {
                val kotlinCode = generator.generateConstructorCall(jsonText, className)
                KotlinResultDialog(project, kotlinCode).show()
            } catch (e: Exception) {
                Messages.showErrorDialog(project, "Invalid JSON: ${e.message}", "JSON Parse Error")
            }
        }
    }
}