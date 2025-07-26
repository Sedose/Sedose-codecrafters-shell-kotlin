package io.codecrafters

class ExitInterceptedException(
    val status: Int,
) : RuntimeException()
