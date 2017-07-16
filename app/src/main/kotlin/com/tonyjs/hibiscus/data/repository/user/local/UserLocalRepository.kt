package com.tonyjs.hibiscus.data.repository.user.local

import com.tonyjs.hibiscus.data.database.entity.UserEntity
import com.tonyjs.hibiscus.data.mapper.post.UserEntityMapper
import com.tonyjs.hibiscus.data.model.User
import io.reactivex.Single
import io.requery.Persistable
import io.requery.sql.KotlinEntityDataStore

class UserFromPreferencesRepository(private val db: KotlinEntityDataStore<Persistable>,
                                    private val userEntityMapper: UserEntityMapper)
    : UserLocalRepository {

    override fun save(user: User) {
        db.insert(userEntityMapper.reverse(user))
    }

    override fun saveAndGet(user: User): Single<User> {
        return Single.fromCallable {
            db.insert(userEntityMapper.reverse(user)).let { userEntityMapper.transform(it) }
        }
    }

    override fun findOne(): Single<User> {
        return Single.fromCallable {
            db.select(UserEntity::class)
                    .orderBy(UserEntity.ID.desc())
                    .limit(1)
                    .get()
                    .firstOrNull()?.let { userEntityMapper.transform(it) }
                    ?: User.EMPTY
        }
    }

}

interface UserLocalRepository {

    fun save(user: User)

    fun saveAndGet(user: User): Single<User>

    fun findOne(): Single<User>

}