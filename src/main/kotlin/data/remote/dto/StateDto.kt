package org.example.data.remote.dto

import java.util.UUID

data class StateDto(
    val id: UUID,
    val name: String,
    val projectId: UUID,
)