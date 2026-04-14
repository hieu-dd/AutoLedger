package org.bakarot.autoledger.shared.domain.error

sealed class AppError {
    /**
     * The requested item was not found in the local database.
     */
    data class NotFound(val id: String) : AppError()

    /**
     * Input validation failed (e.g. amount <= 0, blank name).
     */
    data class Validation(val message: String) : AppError()

    /**
     * An unexpected local storage failure.
     */
    data class Storage(val cause: Throwable? = null) : AppError()

    /**
     * Generic unknown error.
     */
    data class Unknown(val cause: Throwable? = null) : AppError()
}
