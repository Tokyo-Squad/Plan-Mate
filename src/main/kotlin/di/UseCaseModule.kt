package org.example.di

import org.example.logic.usecase.audit.AddAuditLogUseCase
import org.example.logic.usecase.audit.GetAuditLogUseCase
import org.example.logic.usecase.user.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule = module {
    singleOf(::GetUsersUseCase)
    singleOf(::DeleteUserUseCase)
    singleOf(::UpdateUserUseCase)
    singleOf(::GetUserByIdUseCase)
    singleOf(::GetUserByUsernameUseCase)
    singleOf(::GetAuditLogUseCase)
    singleOf(::AddAuditLogUseCase)
    single { AddProjectUseCase(get()) }
    single { UpdateProjectUseCase(get()) }
    single { DeleteProjectUseCase(get()) }
    single { GetProjectUseCase(get()) }
    single { ListProjectsUseCase(get()) }
     singleOf(::CreateTaskUseCase)
    singleOf(::DeleteTaskUseCase)
    singleOf(::GetTaskByIdUseCase)
    singleOf(::GetTasksByProjectIdUseCase)
    singleOf(::UpdateTaskUseCase)
    
}