package com.tonyjs.hibiscus.ui.post

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.tonyjs.hibiscus.data.model.Post
import com.tonyjs.hibiscus.data.model.TextBody
import com.tonyjs.hibiscus.data.model.User
import com.tonyjs.hibiscus.data.repository.post.local.PostLocalRepository
import com.tonyjs.hibiscus.data.repository.post.remote.PostRemoteRepository
import io.reactivex.Completable
import io.reactivex.Single

class PostViewModel(private val postLocalRepository: PostLocalRepository,
                    private val postRemoteRepository: PostRemoteRepository) : ViewModel() {

    val createSuccessEvent = MutableLiveData<Post>()
    val deleteCommand: LiveData<Post> = MutableLiveData<Post>()

    fun createAndGet(post: Post): Single<Post> {
        return postLocalRepository.saveAndGet(post)
    }

    fun hasPosts(): Single<Boolean> {
        return postLocalRepository.findLatestOne()
                .map { it != Post.EMPTY }
    }

    //TODO
    fun export(post: Post): Single<Post> {
        return postRemoteRepository.saveAndGet(post)
    }

    fun getPostsOrderByDesc(offset: Long = Long.MAX_VALUE, limit: Int): Single<List<Post>> {
        return postLocalRepository.findAllLowerThan(offset, limit)
    }

    fun removeAllPosts(): Completable {
        return postLocalRepository.removeAll()
    }

    fun callDeleteCommand(post: Post) {
        (deleteCommand as MutableLiveData).value = post
    }

    fun delete(post: Post): Completable = postLocalRepository.delete(post)

}
