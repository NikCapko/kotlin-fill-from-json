package com.nikcapko.kotlin_fill_from_json

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import java.awt.*
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JScrollPane

class JsonInputDialog(
    private val project: Project
) : DialogWrapper(project) {

    private val jsonTextArea = JBTextArea()
    private val classNameField = JBTextField("NewClass", 20)

    init {
        title = "Generate Kotlin from JSON"
        init()
        setupUI()
    }

    private fun setupUI() {
        // Настраиваем текстовую область для JSON
        jsonTextArea.emptyText.text = "Paste your JSON here..."
        jsonTextArea.font = JBTextArea().font
    }

    override fun createCenterPanel(): JComponent {
        val mainPanel = JPanel(BorderLayout(10, 10))

        // Верхняя панель с настройками
        val settingsPanel = createSettingsPanel()
        mainPanel.add(settingsPanel, BorderLayout.NORTH)

        // Панель для ввода JSON
        val jsonPanel = createJsonInputPanel()
        mainPanel.add(jsonPanel, BorderLayout.CENTER)

        return mainPanel
    }

    private fun createSettingsPanel(): JComponent {
        val panel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints()
        gbc.insets = Insets(5, 5, 5, 5)
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.weightx = 1.0

        // Class name
        gbc.gridx = 0
        gbc.gridy = 0
        panel.add(JBLabel("Class Name:"), gbc)

        gbc.gridy = 1
        panel.add(classNameField, gbc)

        return panel
    }

    private fun createJsonInputPanel(): JComponent {
        val panel = JPanel(BorderLayout())

        val label = JBLabel("JSON Input:")
        label.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        panel.add(label, BorderLayout.NORTH)

        // Текстовая область для JSON с прокруткой
        val scrollPane = JBScrollPane(jsonTextArea)
        scrollPane.preferredSize = Dimension(500, 300)
        scrollPane.border = BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Paste JSON"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        )
        panel.add(scrollPane, BorderLayout.CENTER)

        return panel
    }

    fun getJsonText(): String {
        return jsonTextArea.text.trim()
    }

    fun getClassName(): String {
        return classNameField.text.trim().ifEmpty { "NewClass" }
    }

    override fun doOKAction() {
        val jsonText = getJsonText()
        if (jsonText.isBlank()) {
            Messages.showErrorDialog(project, "Please enter JSON text", "No JSON Input")
            return
        }

        super.doOKAction()
    }
}