package io.codecrafters

import org.springframework.shell.result.CommandNotFoundMessageProvider
import org.springframework.stereotype.Component

@Component
class NotFoundProvider : CommandNotFoundMessageProvider {
    override fun apply(context: CommandNotFoundMessageProvider.ProviderContext): String = "${context.text()}: command not found"
}
