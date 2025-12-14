package com.nikcapko.kotlin_fill_from_json

// def ise-case or point.case to wordCase or WordCase
fun String.toCamelCase(): String {
    val slashRegex = "-[a-zA-Z]".toRegex()
    val pointRegex = "\\.[a-zA-Z]".toRegex()
    val spaceRegex = "\\s[a-zA-Z]".toRegex()
    val firstString = slashRegex.replace(this) {it.value.replace("-", "").uppercase()}
    val delPoint = pointRegex.replace(firstString) { it.value.replace(".", "").uppercase()}
    return spaceRegex.replace(delPoint) { it.value.replace(" ", "").uppercase()}
}

// to wordCase
fun String.snakeToLowerCamelCase(): String {
    val snakeRegex = "_[a-zA-Z]".toRegex()
    return snakeRegex.replace(this.toCamelCase()) {
        it.value.replace("_", "").uppercase()
    }.replaceFirstChar { it.lowercase() }
}
