package com.tonyjs.hibiscus.ui.user

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.tonyjs.hibiscus.data.model.User
import com.tonyjs.hibiscus.data.repository.user.local.UserLocalRepository
import com.tonyjs.hibiscus.data.repository.user.remote.UserRemoteRepository
import io.reactivex.Single

class UserViewModel(private val userRemoteRepository: UserRemoteRepository,
                    private val userLocalRepository: UserLocalRepository) : ViewModel() {

    private val userData = MutableLiveData<User>()

    fun signUp(nickname: String): Single<User> {
        return userRemoteRepository.save(nickname)
                .flatMap { userLocalRepository.saveAndGet(it) }
                .doOnSuccess { userData.value = it }
    }

    fun loadUser(): Single<User> {
        return userData.value?.let { Single.just(it) } ?:
                userLocalRepository.findOne()
                        .doOnSuccess { userData.value = it }
    }

}