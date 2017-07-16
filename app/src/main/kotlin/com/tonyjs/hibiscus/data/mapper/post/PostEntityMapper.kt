package com.tonyjs.hibiscus.data.mapper.post

import com.tonyjs.hibiscus.data.database.entity.ImageBodyEntity
import com.tonyjs.hibiscus.data.database.entity.PostEntity
import com.tonyjs.hibiscus.data.database.entity.TextBodyEntity
import com.tonyjs.hibiscus.data.database.entity.UserEntity
import com.tonyjs.hibiscus.data.mapper.ModelMapper
import com.tonyjs.hibiscus.data.model.Body
import com.tonyjs.hibiscus.data.model.ImageBody
import com.tonyjs.hibiscus.data.model.Post
import com.tonyjs.hibiscus.data.model.TextBody
import java.util.*

class PostEntityMapper(private val userEntityMapper: UserEntityMapper)
    : ModelMapper<PostEntity, Post> {

    override fun transform(from: PostEntity): Post {
        val owner = userEntityMapper.transform(from.owner as UserEntity)

        val bodies = mutableListOf<Body>().apply {
            addAll(from.textBodies.map {
                TextBody(it.text, it.seq)
            })
            addAll(from.imageBodies.map {
                ImageBody(it.path, it.externalPath, it.seq,
                        it.contentType, it.width, it.height, it.orientation)
            })
        }.sortedBy { it.seq }

        return Post(from.id, from.title, from.createdTime, owner, bodies.toMutableList())
    }

    override fun reverse(from: Post): PostEntity {
        return PostEntity().apply postEntity@ {
            title = from.title
            createdTime = Date()
            owner = userEntityMapper.reverse(from.owner)

            from.bodies.forEach {
                if (it is TextBody) {
                    textBodies.add(transformTextBody(it, this@postEntity))
                } else if (it is ImageBody) {
                    imageBodies.add(transformImageBody(it, this@postEntity))
                }
            }
        }
    }

    private fun transformTextBody(textBody: TextBody, owner: PostEntity): TextBodyEntity {
        return TextBodyEntity().apply {
            this.seq = textBody.seq
            this.text = textBody.text
            this.type = "text"
            this.post = owner
        }
    }

    private fun transformImageBody(imageBody: ImageBody, owner: PostEntity): ImageBodyEntity {
        return ImageBodyEntity().apply {
            this.seq = imageBody.seq
            this.path = imageBody.path
            this.contentType = imageBody.contentType
            this.width = imageBody.width
            this.height = imageBody.height
            this.orientation = imageBody.orientation
            this.type = "image"
            this.post = owner
        }
    }

}