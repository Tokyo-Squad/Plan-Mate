package org.example.data

import java.util.UUID

interface DataProvider<Entity> {
    fun add(item: Entity)
    fun get(): List<Entity>
    fun getById(id: UUID): Entity?
    fun update(item: Entity)
    fun delete(id: UUID)
}