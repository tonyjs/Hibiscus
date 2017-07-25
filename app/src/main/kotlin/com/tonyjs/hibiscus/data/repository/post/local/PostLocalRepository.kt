package com.tonyjs.hibiscus.data.repository.post.local

import com.tonyjs.hibiscus.data.database.entity.ImageBodyEntity
import com.tonyjs.hibiscus.data.database.entity.PostEntity
import com.tonyjs.hibiscus.data.database.entity.TextBodyEntity
import com.tonyjs.hibiscus.data.database.entity.UserEntity
import com.tonyjs.hibiscus.data.mapper.post.PostEntityMapper
import com.tonyjs.hibiscus.data.mapper.post.UserEntityMapper
import com.tonyjs.hibiscus.data.model.*
import io.reactivex.Completable
import io.reactivex.Single
import io.requery.Persistable
import io.requery.sql.KotlinEntityDataStore
import java.lang.NullPointerException

class PostFromDatabaseRepository(private val db: KotlinEntityDataStore<Persistable>,
                                 private val postEntityMapper: PostEntityMapper)
    : PostLocalRepository {

    override fun saveAndGet(post: Post): Single<Post> {
        return Single.fromCallable {
            db.withTransaction {
                val owner = db.findByKey(UserEntity::class, post.owner.id)
                        ?: throw NullPointerException("owner is null")

                val postEntity = postEntityMapper.reverse(post).apply {
                    this.owner = owner
                }

                db.insert(postEntity)
            }.let {
                postEntityMapper.transform(it)
            }
        }
    }

    override fun count(): Single<Int> {
        return Single.fromCallable { db.count(PostEntity::class).get().value() }
    }

    override fun findLatestOne(withBody: Boolean): Single<Post> {
        val single = Single.fromCallable {
            db.select(PostEntity::class)
                    .orderBy(PostEntity.ID.desc())
                    .limit(1)
                    .get()
                    .firstOrNull()
                    ?.let {
                        val owner = with(it.owner) {
                            User(id, nickname, token)
                        }

                        val bodies = mutableListOf<Body>().apply {
                            addAll(it.textBodies.map {
                                TextBody(it.text, it.seq)
                            })
                            addAll(it.imageBodies.map {
                                ImageBody(it.path, it.externalPath, it.seq,
                                        it.contentType, it.width, it.height, it.orientation)
                            })
                        }.sortedBy { it.seq }

                        Post(it.id, it.title, it.createdTime, owner, bodies.toMutableList())
                    }
                    ?: Post.EMPTY
        }

        return single
    }

    override fun findAll(): Single<List<Post>> {
        return Single.fromCallable {
            null
        }
    }

    override fun findAllLowerThan(offset: Long, limit: Int): Single<List<Post>> {
        return Single.fromCallable {
            db.select(PostEntity::class)
                    .where(PostEntity.ID.lt(offset))
                    .orderBy(PostEntity.ID.desc())
                    .limit(limit)
                    .get()
                    .map {
                        val bodies = mutableListOf<Body>().apply {
                            addAll(it.textBodies.map {
                                TextBody(it.text, it.seq)
                            })
                            addAll(it.imageBodies.map {
                                ImageBody(it.path, it.externalPath, it.seq,
                                        it.contentType, it.width, it.height, it.orientation)
                            })
                        }.sortedBy { it.seq }

                        Post(it.id, it.title, it.createdTime, with(it.owner) {
                            User(id, nickname, token)
                        }, bodies.toMutableList())
                    }
        }
    }

    override fun removeAll(): Completable {
        return Completable.fromCallable {
            db.delete(PostEntity::class)
                    .get()
                    .value()
        }
    }

    override fun delete(post: Post): Completable {
        return Completable.fromCallable {
            db.delete(PostEntity::class)
                    .where(PostEntity.ID.eq(post.id))
                    .get()
                    .value()
        }
    }

}

interface PostLocalRepository {

    fun saveAndGet(post: Post): Single<Post>

    fun count(): Single<Int>

    fun findLatestOne(withBody: Boolean = false): Single<Post>

    fun findAll(): Single<List<Post>>

    fun findAllLowerThan(offset: Long = Long.MAX_VALUE, limit: Int): Single<List<Post>>

    fun removeAll(): Completable

    fun delete(post: Post): Completable

}