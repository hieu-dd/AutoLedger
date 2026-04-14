package org.bakarot.autoledger.shared.domain.result

import org.bakarot.autoledger.shared.domain.error.AppError

/**
 * Typed result used across repository interfaces and use cases.
 * Avoids throwing exceptions for expected error conditions.
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val error: AppError) : Result<Nothing>()
}

fun <T> Result<T>.getOrNull(): T? = (this as? Result.Success)?.data

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Failure -> this
}
