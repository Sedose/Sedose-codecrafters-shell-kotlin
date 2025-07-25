package io.codecrafters

import org.springframework.context.ApplicationContext
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.stereotype.Component
import java.lang.reflect.Method

/**
 * Discovers all shell built-in commands by scanning for @ShellComponent + @ShellMethod at startup.
 *
 * This replaces a hardcoded list like:
 * `val builtInCommands = setOf("echo", "exit", "type", "pwd")`
 *
 * Now, whenever one adds a new built-in command, it’s automatically included
 * in the `commands` set without needing to update constants manually resulting in maintainability that scales naturally with the codebase.
 */
@Component
class BuiltinCommandRegistry(
    applicationContext: ApplicationContext,
) {
    val commands: Set<String> =
        applicationContext
            .shellComponentBeans()
            .flatMap(::shellMethods)
            .flatMap(::methodKeys)
            .toSet()
}

private fun ApplicationContext.shellComponentBeans(): Sequence<Any> =
    getBeansWithAnnotation(ShellComponent::class.java)
        .values
        .asSequence()

private fun shellMethods(bean: Any): Sequence<Method> =
    bean.javaClass.methods
        .asSequence()
        .filter { it.isAnnotationPresent(ShellMethod::class.java) }

private fun methodKeys(m: Method): Sequence<String> {
    val keys = m.getAnnotation(ShellMethod::class.java)?.key.orEmpty()
    return if (keys.isNotEmpty()) keys.asSequence() else sequenceOf(m.name)
}
