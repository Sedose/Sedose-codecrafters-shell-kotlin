package io.codecrafters

import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle
import org.springframework.shell.jline.PromptProvider
import org.springframework.stereotype.Component

@Component
class DollarPromptProvider : PromptProvider {
    override fun getPrompt(): AttributedString =
        AttributedString("$ ", AttributedStyle.DEFAULT)
}
