package org.example.data

import java.util.UUID

interface DataProvider<Entity> {
    suspend fun add(item: Entity)
    suspend fun get(): List<Entity>
    suspend fun getById(id: UUID): Entity?
    suspend fun update(item: Entity)
    suspend fun delete(id: UUID)
}