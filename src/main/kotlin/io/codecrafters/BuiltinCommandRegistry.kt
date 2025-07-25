package io.codecrafters

import org.springframework.context.ApplicationContext
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component
class BuiltinCommandRegistry(applicationContext: ApplicationContext) {

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
