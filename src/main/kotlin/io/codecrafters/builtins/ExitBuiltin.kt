package io.codecrafters.builtins

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import org.springframework.shell.standard.commands.Quit
import kotlin.system.exitProcess

@ShellComponent
class ExitBuiltin : Quit.Command {
    @ShellMethod(key = ["exit", "quit"], value = "Terminate the shell process")
    fun exit(
        @ShellOption(defaultValue = "0") exitCode: Int,
    ) {
        exitProcess(exitCode)
    }
}
