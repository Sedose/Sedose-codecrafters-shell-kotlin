package io.codecrafters.builtins

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@ShellComponent
class TypeBuiltin {

    private val builtins = setOf("echo", "exit", "type")

    @ShellMethod(key = ["type"], value = "Show how a command would be interpreted")
    fun type(@ShellOption command: String): String {
        return if (command in builtins) {
            "$command is a shell builtin"
        } else {
            "$command: not found"
        }
    }
}
