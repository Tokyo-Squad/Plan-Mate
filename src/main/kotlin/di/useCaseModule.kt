package org.example.di

import org.example.logic.usecase.*
import org.koin.dsl.module

val useCaseModule = module {
    single { AddAuditLogUseCase(get()) }
    single { GetAuditLogUseCase(get()) }
}