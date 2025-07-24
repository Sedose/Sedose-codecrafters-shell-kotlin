package io.codecrafters

import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle
import org.springframework.shell.jline.PromptProvider
import org.springframework.stereotype.Component

/**
 * Provides a custom prompt for Spring Shell.
 *
 * Spring Shell displays a prompt string before each command input.
 * By default, it shows `shell:>`. However, CodeCrafters requires the prompt to be exactly `"$ "`
 * (a dollar sign followed by a space). If this prompt is not shown, their test's fails.
 */
@Component
class DollarPromptProvider : PromptProvider {
    override fun getPrompt(): AttributedString =
        AttributedString("$ ", AttributedStyle.DEFAULT)
}
