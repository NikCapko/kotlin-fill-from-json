package com.nikcapko.kotlin_fill_from_json

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import javax.swing.*

class KotlinResultDialog(
    private val project: Project,
    private val generatedCode: String
) : DialogWrapper(project) {

    private lateinit var editor: EditorEx
    private val copyButton = JButton("Copy to Clipboard")
    private val closeButton = JButton("Close")

    init {
        title = "Generated Kotlin Code"
        setupEditor()
        init()
    }

    private fun setupEditor() {
        val editorFactory = EditorFactory.getInstance()
        val document = editorFactory.createDocument(generatedCode)
        editor = editorFactory.createEditor(document, project) as EditorEx

        // Базовая настройка редактора
        editor.setVerticalScrollbarVisible(true)
        editor.setHorizontalScrollbarVisible(true)
        editor.settings.isLineNumbersShown = true
        editor.settings.isFoldingOutlineShown = true
        editor.settings.isLineMarkerAreaShown = true
        editor.settings.isIndentGuidesShown = true

        // Настраиваем подсветку синтаксиса для Kotlin
        setupSyntaxHighlighting()

        // Настраиваем кнопки
        copyButton.addActionListener { copyToClipboard() }
        closeButton.addActionListener { doOKAction() }

        isModal = false
    }

    private fun setupSyntaxHighlighting() {
        try {
            val fileTypeManager = FileTypeManager.getInstance()
            val kotlinFileType = fileTypeManager.getFileTypeByExtension("kt")
            val colorsScheme = EditorColorsManager.getInstance().globalScheme

            // Используем правильную сигнатуру метода
            val highlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(
                kotlinFileType,
                colorsScheme,
                project
            )
            editor.highlighter = highlighter

        } catch (e: Exception) {
            // Если не удалось загрузить подсветку Kotlin, пробуем plain text
            try {
                val plainTextFileType = FileTypeManager.getInstance().getFileTypeByExtension("txt")
                val colorsScheme = EditorColorsManager.getInstance().globalScheme
                val highlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(
                    plainTextFileType,
                    colorsScheme,
                    project
                )
                editor.highlighter = highlighter
            } catch (e: Exception) {
                // Если даже plain text не работает, просто оставляем без подсветки
                Messages.showWarningDialog(
                    project,
                    "Syntax highlighting is not available",
                    "Highlighting Warning"
                )
            }
        }
    }

    override fun createCenterPanel(): JComponent {
        val mainPanel = JPanel(BorderLayout(10, 10))

        // Заголовок с информацией
        val infoLabel = JBLabel("Generated Kotlin constructor calls:")
        infoLabel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        mainPanel.add(infoLabel, BorderLayout.NORTH)

        // Редактор кода
        val scrollPane = JBScrollPane(editor.component)
        scrollPane.preferredSize = Dimension(700, 500)
        scrollPane.border = BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Kotlin Code"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        )
        mainPanel.add(scrollPane, BorderLayout.CENTER)

        // Панель с кнопками
        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        buttonPanel.add(copyButton)
        buttonPanel.add(closeButton)
        mainPanel.add(buttonPanel, BorderLayout.SOUTH)

        return mainPanel
    }

    override fun createActions(): Array<Action> {
        return emptyArray()
    }

    override fun dispose() {
        super.dispose()
        EditorFactory.getInstance().releaseEditor(editor)
    }

    private fun copyToClipboard() {
        try {
            val text = editor.document.text
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            val stringSelection = StringSelection(text)
            clipboard.setContents(stringSelection, null)

            copyButton.text = "✓ Copied!"
            Timer(2000) {
                copyButton.text = "Copy to Clipboard"
            }.apply {
                isRepeats = false
                start()
            }

        } catch (e: Exception) {
            Messages.showErrorDialog(project, "Failed to copy to clipboard: ${e.message}", "Copy Error")
        }
    }
}