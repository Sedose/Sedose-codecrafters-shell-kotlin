package io.codecrafters.builtins

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@ShellComponent
class EchoBuiltin {

    @ShellMethod(key = ["echo"], value = "Echo the given arguments")
    fun echo(
        @ShellOption(arity = Int.MAX_VALUE) words: Array<String>
    ): String = words.joinToString(" ")
}
