package org.example.presentation.controller.audit

import org.example.entity.AuditedEntityType
import org.example.logic.usecase.audit.GetAuditLogUseCase
import org.example.presentation.io.ConsoleIO

class GetAuditLogController(
    private val io: ConsoleIO,
    private val getAuditLogUseCase: GetAuditLogUseCase
) {

    fun execute() {
        io.printOutput("Retrieve audit logs for:")
        io.printOutput("1. Project")
        io.printOutput("2. Task")
        val entityTypeChoice = io.readInput("Enter your choice (1 or 2): ")

        val entityType: AuditedEntityType = when (entityTypeChoice) {
            "1" -> AuditedEntityType.PROJECT
            "2" -> AuditedEntityType.TASK
            else -> {
                io.printOutput("Invalid choice.")
                return
            }
        }

        io.printOutput("Enter the ID of the ${entityType.toString().lowercase()}:")
        val idInput = io.readInput("${entityType.toString().capitalize()} ID: ")

        if (idInput.isNullOrBlank()) {
            io.printOutput("${entityType.toString().capitalize()} ID cannot be empty.")
            return
        }

        val entityId: Int
        try {
            entityId = idInput.toInt()
        } catch (e: NumberFormatException) {
            io.printOutput("Invalid ${entityType.toString().capitalize()} ID format.")
            return
        }

        val result = getAuditLogUseCase(entityId, entityType)

        result.fold(
            onSuccess = { auditLogs ->
                if (auditLogs.isEmpty()) {
                    io.printOutput("No audit logs found for ${entityType.toString().lowercase()} with ID '$entityId'.")
                } else {
                    io.printOutput("Audit Log History for ${entityType.toString().capitalize()} ID '$entityId':")
                    auditLogs.forEach { log ->
                        io.printOutput("  ID: ${log.id}")
                        io.printOutput("  Entity ID: ${log.entityId}")
                        io.printOutput("  Entity Type: ${log.entityType}")
                        io.printOutput("  Action: ${log.action}")
                        io.printOutput("  Timestamp: ${log.timestamp}")
                        io.printOutput("---")
                    }
                }
            },
            onFailure = { exception ->
                io.printOutput("Failed to retrieve audit logs: ${exception.message}")
            }
        )
    }
}