package io.codecrafters.builtins

import io.codecrafters.BuiltinCommandRegistry
import org.springframework.context.annotation.Lazy
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import java.io.File

@ShellComponent
class TypeBuiltin(
    @Lazy private val builtinCommandRegistry: BuiltinCommandRegistry,
) {
    @ShellMethod("type")
    fun type(command: String): String {
        val executablePath = findExecutableInPath(command)
        return when {
            command in builtinCommandRegistry.commands -> "$command is a shell builtin"
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
        .map(::File)
        .map { dir -> dir.resolve(command) }
        .firstOrNull { candidate -> candidate.exists() && candidate.canExecute() }
        ?.absolutePath
