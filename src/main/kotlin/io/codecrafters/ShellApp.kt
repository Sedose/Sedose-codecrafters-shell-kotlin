package io.codecrafters

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.PrintWriter

@SpringBootApplication
class ShellApp

fun main(args: Array<String>) {
    runApplication<ShellApp>(*args)
    PrintWriter(System.out, true).println()
}
