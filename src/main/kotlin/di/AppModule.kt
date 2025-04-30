package org.example.di

import org.example.data.repository.*
import org.example.logic.repository.AuditLogRepository
import org.koin.dsl.module

val appModule = module {
    single<AuditLogRepository> { AuditLogRepositoryImpl(get()) }
}