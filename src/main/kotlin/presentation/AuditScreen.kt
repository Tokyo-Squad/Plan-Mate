package org.example.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import logic.model.AuditLog
import logic.model.AuditedType
import org.example.logic.usecase.audit.GetAuditLogUseCase
import org.example.presentation.io.ConsoleIO
import org.example.utils.PlanMateException
import java.util.*


class AuditScreen(
    private val console: ConsoleIO,
    private val getAuditLogUseCase: GetAuditLogUseCase,
) {
    suspend fun show() {
        while (true) {
            try {
                console.write("\n=== Audit Log ===")
                when (showMenu()) {
                    1 -> viewProjectAudit()
                    2 -> viewTaskAudit()
                    3 -> return
                    else -> console.writeError("Invalid option. Please try again.")
                }
            } catch (e: PlanMateException) {
                console.writeError("Operation failed: ${e.message}")
            } catch (e: Exception) {
                console.writeError("Unexpected error: ${e.message}")
                e.printStackTrace()
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

    private suspend fun viewProjectAudit() {
        console.write("\nEnter Project ID (UUID format): ")
        val projectId = console.read().trim()

        if (projectId.isBlank()) {
            console.writeError("Project ID cannot be empty")
            return
        }

        try {
            val uuid = parseUUID(projectId) ?: return

            val logs = withContext(Dispatchers.IO) {
                getAuditLogUseCase.invoke(uuid, AuditedType.PROJECT)
            }

            if (logs.isNotEmpty()) {
                displayAuditLogs(logs)
            } else {
                console.write("No audit logs found for this project.")
            }
        } catch (e: PlanMateException) {
            console.writeError("Failed to fetch project audit logs: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error fetching project audit logs: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun viewTaskAudit() {
        console.write("\nEnter Task ID (UUID format): ")
        val taskId = console.read().trim()

        if (taskId.isBlank()) {
            console.writeError("Task ID cannot be empty")
            return
        }

        try {
            val uuid = parseUUID(taskId) ?: return

            val logs = withContext(Dispatchers.IO) {
                getAuditLogUseCase.invoke(uuid, AuditedType.TASK)
            }

            if (logs.isNotEmpty()) {
                displayAuditLogs(logs)
            } else {
                console.write("No audit logs found for this task.")
            }
        } catch (e: PlanMateException) {
            console.writeError("Failed to fetch task audit logs: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error fetching task audit logs: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun parseUUID(input: String): UUID? {
        return try {
            UUID.fromString(input)
        } catch (e: IllegalArgumentException) {
            console.writeError("Invalid UUID format. Please enter a valid UUID.")
            null
        }
    }

    private fun displayAuditLogs(logs: List<AuditLog>) {
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