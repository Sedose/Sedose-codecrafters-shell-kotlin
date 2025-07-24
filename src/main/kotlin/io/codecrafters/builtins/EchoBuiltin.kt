package io.codecrafters.builtins

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class EchoBuiltin {

    @ShellMethod(key = ["echo"], value = "Echo the given arguments")
    fun echo(vararg words: String): String =
        words.joinToString(" ")
}

