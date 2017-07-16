package com.tonyjs.hibiscus.data.database.entity

import io.requery.*

@Entity(name = "UserEntity", cacheable = false)
@Table(name = "Users")
interface IUserEntity : Persistable {

    @get:Key
    var id: Long

    var nickname: String

    var token : String

    @get:OneToMany(mappedBy = "owner")
    val post: MutableList<IPostEntity>

}