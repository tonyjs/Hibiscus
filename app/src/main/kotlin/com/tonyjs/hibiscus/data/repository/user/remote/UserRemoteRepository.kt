package com.tonyjs.hibiscus.data.repository.user.remote

import com.tonyjs.hibiscus.data.model.User
import com.tonyjs.hibiscus.data.network.api.TelegraphApi
import io.reactivex.Single

class UserFromTelegraphRepository(val api: TelegraphApi) : UserRemoteRepository {

    override fun save(nickname: String): Single<User> {
        return api.createAccount(nickname)
                .map {
                    val isSuccess = (it.success ?: false) && it.account != null
                    if (!isSuccess) throw UnknownError()

                    val account = it.account!!
                    User(User.READY_TO_SET_ID, account.shortName, account.accessToken)
                }
    }

}

interface UserRemoteRepository {
    fun save(nickname: String): Single<User>
}