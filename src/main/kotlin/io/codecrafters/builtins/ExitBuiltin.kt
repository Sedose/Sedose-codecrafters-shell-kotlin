package io.codecrafters.builtins

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import org.springframework.shell.ExitRequest

@ShellComponent
class ExitBuiltin {

    @ShellMethod(key = ["exit", "quit"], value = "Terminate the shell process")
    fun exit(
        @ShellOption(defaultValue = "0") exitCode: Int,
    ): Nothing {
        throw ExitRequest(exitCode)
    }
}
