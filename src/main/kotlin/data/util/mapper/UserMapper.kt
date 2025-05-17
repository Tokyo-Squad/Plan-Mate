package org.example.data.util.mapper

import org.bson.Document
import org.example.data.remote.dto.UserDto
import org.example.entity.UserEntity
import org.example.entity.UserType
import java.util.UUID

fun UserDto.toUserEntity(): UserEntity = UserEntity(
    id = id,
    username = username,
    password = password,
    type = UserType.valueOf(type)
)

fun UserEntity.toUserDto(): UserDto = UserDto(
    id = id,
    username = username,
    password = password,
    type = type.name
)

fun UserDto.toDocument(): Document = Document().apply {
    put("id", id)
    put("username", username)
    put("password", password)
    put("type", type)
}

fun Document.toUserDto(): UserDto = UserDto(
    id = UUID.fromString(getString("id")),
    username = getString("username"),
    password = getString("password"),
    type = getString("type")
)