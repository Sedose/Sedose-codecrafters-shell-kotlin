package io.codecrafters.builtins

import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.springframework.shell.ExitRequest
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import org.springframework.shell.standard.commands.Quit

@ShellComponent
class ExitBuiltin(
    private val terminal: Terminal,
) : Quit.Command {
    @ShellMethod(key = ["exit", "quit"], value = "Terminate the shell process")
    fun exit(
        @ShellOption(defaultValue = "0") code: Int,
    ): Nothing {
        terminal.writer().println()
        terminal.flush()
        throw ExitRequest(code)
    }
}
