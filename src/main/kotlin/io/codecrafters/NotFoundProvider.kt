package io.codecrafters

import org.springframework.shell.result.CommandNotFoundMessageProvider
import org.springframework.stereotype.Component

@Component
class NotFoundProvider : CommandNotFoundMessageProvider {
    override fun apply(t: CommandNotFoundMessageProvider.ProviderContext): String {
        return "${t.text()}: command not found"
    }
}
