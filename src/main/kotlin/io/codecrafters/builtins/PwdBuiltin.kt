package io.codecrafters.builtins

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class PwdBuiltin {

    @ShellMethod(key = ["pwd"], value = "Print working directory")
    fun pwd(): String = System.getProperty("user.dir")
}
