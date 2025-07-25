package io.codecrafters

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.shell.jline.PromptProvider
import org.springframework.stereotype.Component

@Component
class InitialPromptPrinter(
    private val promptProvider: PromptProvider,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        print(promptProvider.prompt)
        System.out.flush()
    }
}
