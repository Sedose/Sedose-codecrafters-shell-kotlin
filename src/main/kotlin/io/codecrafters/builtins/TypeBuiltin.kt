package io.codecrafters.builtins

import io.codecrafters.builtInCommands
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import java.io.File

@ShellComponent
class TypeBuiltin {

    @ShellMethod("type")
    fun type(command: String): String {
        val executablePath = findExecutableInPath(command)
        return when {
            command in builtInCommands -> "$command is a shell builtin"
            executablePath != null -> "$command is $executablePath"
            else -> "$command: not found"
        }
    }
}

private fun findExecutableInPath(command: String): String? =
    System
        .getenv("PATH")
        .orEmpty()
        .split(':')
        .asSequence()
        .map(::File) // each element is a directory
        .map { dir -> dir.resolve(command) } // directory/command
        .firstOrNull { candidate -> candidate.exists() && candidate.canExecute() }
        ?.absolutePath
