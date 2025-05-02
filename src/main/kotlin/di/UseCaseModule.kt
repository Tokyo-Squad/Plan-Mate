package org.example.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.example.logic.usecase.task.*

val useCaseModule = module {
    singleOf(::CreateTaskUseCase)
    singleOf(::DeleteTaskUseCase)
    singleOf(::GetTaskByIdUseCase)
    singleOf(::GetTasksByProjectIdUseCase)
    singleOf(::UpdateTaskUseCase)
}
