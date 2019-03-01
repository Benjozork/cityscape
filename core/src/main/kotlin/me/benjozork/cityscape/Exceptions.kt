package me.benjozork.cityscape

import ktx.log.Logger

import me.benjozork.cityscape.storage.MapPackage

import java.lang.Exception

/**
 * Logs an exception using a [Logger].
 * The [exception][e]'s stacktrace and error message will be printed.
 *
 * @receiver the [Logger] to use
 * @param m the error message to display before the exception
 * @param e the excepton to display
 */
fun Logger.exception(m: String, e: Exception) {
    this.error { m }
    this.error { "-".repeat(m.length) }
    this.error { "${e.javaClass.name.replace("$", ".")}: ${e.message ?: ""}\n" + e.stackTrace.joinToString(prefix = " ".repeat(24), separator = "\n" + " ".repeat(24)) }
}