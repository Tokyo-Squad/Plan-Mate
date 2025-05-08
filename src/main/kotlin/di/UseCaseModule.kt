package org.example.di

import logic.usecase.auth.CreateDefaultAdminUseCase
import org.example.logic.usecase.audit.AddAuditLogUseCase
import org.example.logic.usecase.audit.GetAuditLogUseCase
import org.example.logic.usecase.auth.GetCurrentUserUseCase
import org.example.logic.usecase.auth.LoginUseCase
import org.example.logic.usecase.auth.LogoutUseCase
import org.example.logic.usecase.auth.RegisterUseCase
import org.example.logic.usecase.project.*
import org.example.logic.usecase.state.AddStateUseCase
import org.example.logic.usecase.state.DeleteStateUseCase
import org.example.logic.usecase.state.GetStatesByProjectId
import org.example.logic.usecase.state.UpdateStateUseCase
import org.example.logic.usecase.task.*
import org.example.logic.usecase.user.*
import org.koin.dsl.module

val useCaseModule = module {
    single { GetUsersUseCase(get()) }
    single { DeleteUserUseCase(get()) }
    single { UpdateUserUseCase(get()) }
    single { GetUserByIdUseCase(get()) }
    single { GetUserByUsernameUseCase(get()) }
    single { GetAuditLogUseCase(get()) }
    single { AddAuditLogUseCase(get()) }
    single { CreateTaskUseCase(get()) }
    single { DeleteTaskUseCase(get()) }
    single { GetTaskByIdUseCase(get()) }
    single { GetTasksByProjectIdUseCase(get()) }
    single { UpdateTaskUseCase(get()) }
    single { AddProjectUseCase(get()) }
    single { UpdateProjectUseCase(get()) }
    single { DeleteProjectUseCase(get()) }
    single { GetProjectUseCase(get()) }
    single { ListProjectsUseCase(get()) }
    single { GetStatesByProjectId(get()) }
    single { LoginUseCase(get()) }
    single { RegisterUseCase(get()) }
    single { GetCurrentUserUseCase(get()) }
    single { LogoutUseCase(get()) }
    single { UpdateStateUseCase(get()) }
    single { AddStateUseCase(get()) }
    single { DeleteStateUseCase(get()) }
    single { CreateDefaultAdminUseCase(get()) }
}