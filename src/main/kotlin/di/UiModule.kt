package org.example.di

import AdminScreen
import MateScreen
import SwimlaneRenderer
import org.example.presentation.*
import org.example.presentation.io.ConsoleIO
import org.example.presentation.io.ConsoleIOImpl
import org.koin.dsl.module

val uiModule = module {
    // Screens
    single {
        PlanMateConsoleUI(
            console = get(),
            loginUseCase = get(),
            adminScreen = get(),
            mateScreen = get(),
            getCurrentUserUseCase = get(),
            logoutUseCase = get()
        )
    }

    single {
        AdminScreen(
            console = get(),
            getProjectsUseCase = get(),
            createProjectUseCase = get(),
            createUserUseCase = get(),
            getCurrentUser = get(),
            projectScreen = get(),
            projectEditScreen = get(),
            auditScreen = get(),
        )
    }

    single {
        MateScreen(
            console = get(),
            getProjectsUseCase = get(),
            projectScreen = get(),
            auditScreen = get()
        )
    }

//    single {
//        ProjectScreen(
//            console = get(),
//            getProjectUseCase = get(),
//            taskEditScreen = get(),
//        )
//    }

    single {
        ProjectEditScreen(
            console = get(),
            updateProjectUseCase = get(),
            deleteProjectUseCase = get(),
            updateStateUseCase = get(),
            getProjectUseCase = get(),
            getCurrentUserUseCase = get(),
            getStatesByProjectId = get(),
            addStateUseCase = get(),
            deleteStateUseCase = get()
        )
    }

//    single {
//        TaskEditScreen(
//            console = get(),
//            updateTaskUseCase = get(),
//            deleteTaskUseCase = get(),
//            getTaskUseCase = get(),
//            getCurrentUserUseCase = get(),
//            createTaskUseCase = get(),
//            getStatesByProjectId = get(),
//            getProjectUseCase = get(),
//            getTasksByProjectUseCase = get(),
//            swimlaneRenderer = get()
//        )
//    }

    single {
        AuditScreen(
            console = get(),
            getAuditLogUseCase = get()
        )
    }

    // UI Components
    single<ConsoleIO> { ConsoleIOImpl() }
    single { SwimlaneRenderer(get()) }
}