package com.nikcapko.kotlin_fill_from_json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

class AdvancedConstructorCallGenerator {

    fun generateConstructorCall(jsonString: String, className: String): String {
        return try {
            val mapper = ObjectMapper()
            val jsonNode = mapper.readTree(jsonString)

            if (jsonNode.isObject) {
                val result = StringBuilder()
                generateObjectConstructorCall(jsonNode, className, result)
                result.toString()
            } else {
                "// Only JSON objects are supported for constructor calls"
            }
        } catch (e: Exception) {
            "// Error parsing JSON: ${e.message}"
        }
    }

    private fun generateObjectConstructorCall(
        jsonNode: JsonNode,
        className: String,
        result: StringBuilder,
        indent: String = ""
    ) {
        result.append("$className(")

        val fields = jsonNode.fields().asSequence().toList()
        if (fields.isNotEmpty()) {
            result.appendLine()

            fields.forEachIndexed { index, (key, value) ->
                val nestedClassName = key.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                result.append("$indent    $key = ")

                when {
                    value.isObject -> {
                        generateObjectConstructorCall(value, nestedClassName, result, "$indent    ")
                    }

                    value.isArray && value.size() > 0 && value[0].isObject -> {
                        result.append("listOf(")
                        result.appendLine()
                        value.forEachIndexed { arrayIndex, arrayElement ->
                            val itemClassName = "${nestedClassName}Item"
                            generateObjectConstructorCall(arrayElement, itemClassName, result, "$indent        ")
                            if (arrayIndex < value.size() - 1) result.append(",")
                            result.appendLine()
                        }
                        result.append("$indent    )")
                    }

                    else -> {
                        result.append(convertSimpleValue(value))
                    }
                }

                if (index < fields.size - 1) result.append(",")
                result.appendLine()
            }

            result.append("$indent)")
        } else {
            result.append(")")
        }
    }

    private fun convertSimpleValue(jsonNode: JsonNode): String {
        return when {
            jsonNode.isTextual -> "\"${jsonNode.asText().escape()}\""
            jsonNode.isInt -> jsonNode.asText()
            jsonNode.isLong -> "${jsonNode.asText()}L"
            jsonNode.isDouble -> jsonNode.asText()
            jsonNode.isFloat -> "${jsonNode.asText()}f"
            jsonNode.isBoolean -> jsonNode.asText()
            jsonNode.isArray -> {
                val elements = jsonNode.joinToString { convertSimpleValue(it) }
                "listOf($elements)"
            }

            jsonNode.isNull -> "null"
            else -> "\"${jsonNode.asText().escape()}\""
        }
    }

    private fun String.escape(): String {
        return this.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\t", "\\t")
            .replace("\r", "\\r")
    }
}