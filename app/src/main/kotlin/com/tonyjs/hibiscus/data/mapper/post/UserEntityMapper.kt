package com.tonyjs.hibiscus.data.mapper.post

import com.tonyjs.hibiscus.data.database.entity.UserEntity
import com.tonyjs.hibiscus.data.mapper.ModelMapper
import com.tonyjs.hibiscus.data.model.User

class UserEntityMapper : ModelMapper<UserEntity, User> {

    override fun transform(from: UserEntity): User {
        return User(from.id, from.nickname, from.token)
    }

    override fun reverse(from: User): UserEntity {
        return UserEntity().apply {
            if (from.id != User.READY_TO_SET_ID) {
                id = from.id
            }
            nickname = from.nickname
            token = from.token
        }
    }

}
