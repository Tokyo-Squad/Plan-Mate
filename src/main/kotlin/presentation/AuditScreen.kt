package org.example.presentation

import kotlinx.datetime.LocalDateTime
import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import org.example.logic.usecase.audit.GetAuditLogUseCase
import org.example.presentation.io.ConsoleIO
import java.util.*

class AuditScreen(
    private val console: ConsoleIO,
    private val getAuditLogUseCase: GetAuditLogUseCase,
) {
    fun show() {
        while (true) {
            try {
                console.write("\n=== Audit Log ===")
                when (showMenu()) {
                    1 -> viewProjectAudit()
                    2 -> viewTaskAudit()
                    3 -> return
                    else -> console.writeError("Invalid option. Please try again.")
                }
            } catch (e: Exception) {
                console.writeError("Error: ${e.message}")
            }
        }
    }

    private fun showMenu(): Int {
        console.write("1. View Project Audit")
        console.write("2. View Task Audit")
        console.write("3. Back")
        console.write("\nSelect an option: ")
        return console.read().toIntOrNull() ?: 0
    }

    private fun viewProjectAudit() {
        console.write("\nEnter Project ID (UUID format): ")
        val projectId = console.read().trim()

        if (projectId.isBlank()) {
            console.writeError("Project ID cannot be empty")
            return
        }

        try {
            val uuid = try {
                UUID.fromString(projectId)
            } catch (e: IllegalArgumentException) {
                console.writeError("Invalid UUID format. Please enter a valid UUID.")
                return
            }


            getAuditLogUseCase(uuid, AuditedEntityType.PROJECT)
                .onSuccess { logs ->
                    displayAuditLogs(logs)
                }
                .onFailure { e ->
                    console.writeError("Failed to fetch project audit logs: ${e.message}")
                }
        } catch (e: Exception) {
            console.writeError("Failed to fetch project audit logs: ${e.message}")
        }
    }

    private fun viewTaskAudit() {
        console.write("\nEnter Task ID (UUID format): ")
        val taskId = console.read().trim()

        if (taskId.isBlank()) {
            console.writeError("Task ID cannot be empty")
            return
        }

        try {
            val uuid = try {
                UUID.fromString(taskId)
            } catch (e: IllegalArgumentException) {
                console.writeError("Invalid UUID format. Please enter a valid UUID.")
                return
            }

            getAuditLogUseCase(uuid, AuditedEntityType.TASK)
                .onSuccess { logs ->
                    displayAuditLogs(logs)
                }
                .onFailure { e ->
                    console.writeError("Failed to fetch task audit logs: ${e.message}")
                }
        } catch (e: Exception) {
            console.writeError("Failed to fetch task audit logs: ${e.message}")
        }
    }

    private fun displayAuditLogs(logs: List<AuditLogEntity>) {
        if (logs.isEmpty()) {
            console.write("No audit logs found.")
            return
        }

        console.write("\n=== Audit History ===")
        logs.forEach { log ->
            console.write("Time: ${formatDateTime(log.timestamp)}")
            console.write("User: ${log.userId}")
            console.write("Action: ${log.action}")
            console.write("Details: ${log.changeDetails}")
            console.write("-------------------")
        }
    }

    private fun formatDateTime(dateTime: LocalDateTime): String {
        return "${dateTime.monthNumber}/${dateTime.dayOfMonth} ${dateTime.hour}:${dateTime.minute}"
    }
}