package com.chail.yvkari.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState

object SnackbarManager {
    lateinit var hostState: SnackbarHostState

    suspend fun show(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        hostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = duration
        )
    }
}