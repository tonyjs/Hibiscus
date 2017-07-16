package com.tonyjs.hibiscus.data.database.entity

import io.requery.*
import java.util.*

@Entity(name = "PostEntity", cacheable = false)
@Table(name = "Post")
interface IPostEntity : Persistable {

    @get:Key
    @get:Generated
    var id: Long

    var title: String

    var createdTime: Date

    var url: String

    @get:ManyToOne
    var owner: IUserEntity

    @get:OneToMany
    val textBodies: MutableList<ITextBodyEntity>
    @get:OneToMany
    val imageBodies: MutableList<IImageBodyEntity>

    @Entity(name = "ImageBodyEntity", cacheable = false)
    @Table(name = "ImageBody")
    interface IImageBodyEntity : IBodyEntity {

        @get:Key
        @get:Generated
        var id: Long

        var path: String
        var externalPath: String
        var contentType: String
        var width: Int
        var height: Int
        var orientation: Int

    }

    @Entity(name = "TextBodyEntity", cacheable = false)
    @Table(name = "TextBody")
    interface ITextBodyEntity : IBodyEntity {

        @get:Key
        @get:Generated
        var id: Long

        var text: String
    }

    @Superclass
    interface IBodyEntity : Persistable {

        @get:ManyToOne
        var post: IPostEntity

        var type: String

        var seq: Int

    }
}