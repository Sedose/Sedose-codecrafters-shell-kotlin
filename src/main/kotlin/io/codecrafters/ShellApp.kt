package io.codecrafters

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ShellApp

fun main(args: Array<String>) {
    println("") // <– forces prompt to get flushed after startup
    System.out.flush()
    runApplication<ShellApp>(*args)
}
