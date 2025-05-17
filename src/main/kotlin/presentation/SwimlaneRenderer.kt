import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import domain.model.Task
import org.example.presentation.io.ConsoleIO
import java.util.*

class SwimlaneRenderer(private val console: ConsoleIO) {
    private companion object {
        const val COLUMN_WIDTH = 60
        const val SEPARATOR = "+"
        const val HORIZONTAL_LINE = "-"
        const val VERTICAL_LINE = "|"
    }

    fun render(tasks: List<Task>, states: Map<UUID, String>) {
        if (tasks.isEmpty()) {
            console.write("\nNo tasks found in this project.")
            return
        }

        val tasksByState = tasks.groupBy { it.workflowStateId }
        drawHeader(states)
        drawTasks(tasksByState, states)
        drawFooter(states)
    }

    private fun drawHeader(states: Map<UUID, String>) {
        console.write(createSeparatorLine(states.size))

        val stateRow = StringBuilder()
        states.values.forEach { state ->
            stateRow.append("$VERTICAL_LINE ${state.take(COLUMN_WIDTH - 2).padEnd(COLUMN_WIDTH - 2)} ")
        }
        stateRow.append(VERTICAL_LINE)
        console.write(stateRow.toString())

        console.write(createSeparatorLine(states.size))
    }

    private fun drawTasks(
        tasksByState: Map<UUID, List<Task>>,
        states: Map<UUID, String>
    ) {
        val maxTasks = tasksByState.values.maxOfOrNull { it.size } ?: 0

        for (i in 0 until maxTasks) {
            // Task title row
            val taskRow = StringBuilder()
            states.keys.forEach { stateId ->
                val task = tasksByState[stateId]?.getOrNull(i)
                taskRow.append(formatTaskCell(task))
            }
            taskRow.append(VERTICAL_LINE)
            console.write(taskRow.toString())

            // Task description row
            val descriptionRow = StringBuilder()
            states.keys.forEach { stateId ->
                val task = tasksByState[stateId]?.getOrNull(i)
                descriptionRow.append(formatDescriptionCell(task))
            }
            descriptionRow.append(VERTICAL_LINE)
            console.write(descriptionRow.toString())

            // Task metadata row
            val metadataRow = StringBuilder()
            states.keys.forEach { stateId ->
                val task = tasksByState[stateId]?.getOrNull(i)
                metadataRow.append(formatMetadataCell(task))
            }
            metadataRow.append(VERTICAL_LINE)
            console.write(metadataRow.toString())

            console.write(createSeparatorLine(states.size))
        }
    }

    private fun formatTaskCell(task: Task?): String {
        val content = task?.let {
            "📎 ${it.title}".take(COLUMN_WIDTH - 2)
        } ?: ""
        return "$VERTICAL_LINE ${content.padEnd(COLUMN_WIDTH - 2)} "
    }

    private fun formatDescriptionCell(task: Task?): String {
        val content = task?.let {
            "  ${it.description}".take(COLUMN_WIDTH - 2)
        } ?: ""
        return "$VERTICAL_LINE ${content.padEnd(COLUMN_WIDTH - 2)} "
    }

    private fun formatMetadataCell(task: Task?): String {
        val content = task?.let {
            val fullId = it.id.toString()
            val date = formatDateTime(it.createdAt)
            "  #$fullId | $date"
        } ?: ""
        return "$VERTICAL_LINE ${content.padEnd(COLUMN_WIDTH - 2)} "
    }

    private fun formatDateTime(dateTime: LocalDateTime): String {
        val month = dateTime.month.number.toString().padStart(2, '0')
        val day = dateTime.dayOfMonth.toString().padStart(2, '0')
        val hour = dateTime.hour.toString().padStart(2, '0')
        val minute = dateTime.minute.toString().padStart(2, '0')

        return "$month/$day $hour:$minute"
    }

    private fun drawFooter(states: Map<UUID, String>) {
        console.write(createSeparatorLine(states.size))
    }

    private fun createSeparatorLine(stateCount: Int): String {
        val builder = StringBuilder()
        repeat(stateCount) {
            builder.append(SEPARATOR)
            builder.append(HORIZONTAL_LINE.repeat(COLUMN_WIDTH))
        }
        builder.append(SEPARATOR)
        return builder.toString()
    }
}