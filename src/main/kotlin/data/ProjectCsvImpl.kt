package org.example.data

import org.example.entity.ProjectEntity
import java.util.*

class ProjectCsvImpl(
    fileName: String
) : DataProvider<ProjectEntity> {
    override fun add(item: ProjectEntity) {
        TODO("Not yet implemented")
    }

    override fun get(): List<ProjectEntity> {
        TODO("Not yet implemented")
    }

    override fun getById(id: UUID): ProjectEntity? {
        TODO("Not yet implemented")
    }

    override fun delete(id: UUID) {
        TODO("Not yet implemented")
    }

    override fun update(item: ProjectEntity) {
        TODO("Not yet implemented")
    }
}