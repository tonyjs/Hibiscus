package com.tonyjs.hibiscus.ui

import android.arch.lifecycle.*
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import com.tonyjs.hibiscus.LOG
import com.tonyjs.hibiscus.R
import com.tonyjs.hibiscus.data.model.User
import com.tonyjs.hibiscus.ui.message.EventType
import com.tonyjs.hibiscus.ui.message.Message
import com.tonyjs.hibiscus.ui.message.adapter.MessageAdapter
import com.tonyjs.hibiscus.ui.message.MessageViewModel
import com.tonyjs.hibiscus.ui.navigation.NavigationTo
import com.tonyjs.hibiscus.ui.navigation.NavigationViewModel
import com.tonyjs.hibiscus.ui.post.PostViewModel
import com.tonyjs.hibiscus.ui.post.create.CreatePostFragment
import com.tonyjs.hibiscus.ui.post.list.PostListFragment
import com.tonyjs.hibiscus.ui.user.UserViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton

class MainActivity : AppCompatActivity(), LifecycleRegistryOwner {

    companion object {
        const val TAG = "MainActivity"
    }

    private val lifecycleRegistry = LifecycleRegistry(this)

    private lateinit var navigationViewModel: NavigationViewModel

    private lateinit var userViewModel: UserViewModel

    private lateinit var postViewModel: PostViewModel

    private lateinit var messageViewModel: MessageViewModel

    private var adapter: MessageAdapter? = null

    private var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.run {
            //FIXME anything?
        } ?: run {
            setupMessageAdapter()

            setupMessageViewModel()

            setupNavigationViewModel()

            setupAccountViewModel()

            setupPostViewModel()

            setupUserActionListeners()

            start()
        }
    }

    private fun setupMessageAdapter() {
        adapter = MessageAdapter()
        list.adapter = adapter
    }

    private fun setupMessageViewModel() {
        messageViewModel = ViewModelProviders.of(this, ViewModelFactory.from(application))
                .get(MessageViewModel::class.java)

        fun addMessage(message: Message) {
            adapter?.run {
                this.addMessage(message)
                notifyItemInserted(itemCount - 1)
            }
        }

        fun removeMessage(message: Message) {
            adapter?.run {
                findMessagePositionByTag(message)?.let {
                    removeItemAt(it)
                    notifyItemRemoved(it)
                }
            }
        }

        messageViewModel.messageEvent
                .observe(this, Observer {
                    if (it == null) {
                        return@Observer
                    }

                    when (it.first) {
                        EventType.WRITE -> addMessage(it.second)
                        EventType.REMOVE -> removeMessage(it.second)
                    }
                })
    }

    private fun setupNavigationViewModel() {
        navigationViewModel = ViewModelProviders.of(this, ViewModelFactory.from(application))
                .get(NavigationViewModel::class.java)
        navigationViewModel.navigationEvent
                .observe(this, Observer {
                    when (it) {
                        NavigationTo.READY ->
                            showReady(it)
                        NavigationTo.SIGN_IN -> {
                            showSignIn()
                        }
                        NavigationTo.GUIDE_TO_CREATE_POST -> {
                            showCreatePost()
                        }
                        NavigationTo.CREATE_POST -> {
                            moveToCreatePost()
                        }
                        NavigationTo.POST_LIST -> {
                            moveToPostList()
                        }
                    }
                })
    }

    private fun moveToPostList() {
        for (i in 0..supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }

        addFragment(PostListFragment(), PostListFragment.TAG, false)
    }

    private fun setupAccountViewModel() {
        userViewModel = ViewModelProviders.of(this, ViewModelFactory.from(application))
                .get(UserViewModel::class.java)
    }

    private fun setupPostViewModel() {
        postViewModel = ViewModelProviders.of(this, ViewModelFactory.from(application))
                .get(PostViewModel::class.java)
    }

    private fun nickname() = etNickname.text.toString()

    private fun setupUserActionListeners() {
        addDisposable(etNickname.textChanges()
                .skipInitialValue()
                .subscribe {
                    btnSignIn.isEnabled = it.isNotBlank()
                })

        addDisposable(btnSignIn.clicks()
                .subscribe {
                    signIn()
                })

        addDisposable(btnCreatePost.clicks()
                .subscribe {
                    navigationViewModel.moveTo(NavigationTo.CREATE_POST, duplicatable = true)
                })
    }

    private fun signIn() {
        btnSignIn.isEnabled = false
        hideSoftInput(etNickname)

        signInWrapper.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        messageViewModel.write(getString(R.string.desc_my_nickname, nickname()), Message.From.HUMAN)

        postToMainThread(runCondition = { isAlive(lifecycle) }, delayMillis = 1000L) {
            messageViewModel.write(getString(R.string.ok_wait_a_minute), Message.From.BOT)
        }

        createAccount()
    }

    private fun createAccount() {
        userViewModel.signUp(nickname())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    showSignInSuccess(it)
                }, { error ->
                    LOG.e(TAG, error)
                    messageViewModel.write(getString(R.string.error_on_sign_in), Message.From.BOT)

                    postToMainThread(runCondition = { isAlive(lifecycle) }, delayMillis = 1000L) {
                        navigationViewModel.moveTo(NavigationTo.SIGN_IN)
                    }
                }).apply { addDisposable(this) }
    }

    private fun showSignInSuccess(user: User) {
        progressBar.visibility = View.GONE

        messageViewModel.write(getString(R.string.welcome_user, user.nickname), Message.From.BOT)

        postToMainThread(runCondition = { isAlive(lifecycle) }, delayMillis = 1000L) {
            navigationViewModel.moveTo(NavigationTo.GUIDE_TO_CREATE_POST)
        }
    }

    private fun showReady(it: NavigationTo?) {
        messageViewModel.write(getString(R.string.wait_a_minute), Message.From.BOT, tag = it)
    }

    private fun showSignIn() {
        messageViewModel.write(getString(R.string.hello_world), Message.From.BOT)

        signInWrapper.visibility = View.VISIBLE
        showSoftInput(etNickname)
    }

    private fun showCreatePost() {
        messageViewModel.write(getString(R.string.click_create_post), Message.From.BOT)
        actionWrapper.visibility = View.VISIBLE
    }

    private fun start() {
        navigationViewModel.moveTo(NavigationTo.READY)

        val startTime = System.currentTimeMillis()

        InitialData.from(userViewModel.loadUser(), postViewModel.hasPosts())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val delay = 1000L - (System.currentTimeMillis() - startTime)

                    postToMainThread(runCondition = { isAlive(lifecycle) }, delayMillis = delay) {
                        guideToNext(it)
                    }
                }, { error ->
                    LOG.e(TAG, error)
                    alert(getString(R.string.error_review_please),
                            getString(R.string.error_on_post_list), init = {
                        okButton {
                            //TODO
                        }
                    })
                })
    }

    private fun guideToNext(initialData: InitialData) {
        messageViewModel.remove(tag = NavigationTo.READY)

        if (initialData.hasAccount && initialData.hasPosts) {
            navigationViewModel.moveTo(NavigationTo.POST_LIST)
        } else {

            if (initialData.hasAccount) {
                navigationViewModel.moveTo(NavigationTo.GUIDE_TO_CREATE_POST)
            } else {
                postViewModel.removeAllPosts()
                        .onErrorComplete()
                        .subscribeOn(Schedulers.io())
                        .subscribe()
                navigationViewModel.moveTo(NavigationTo.SIGN_IN)
            }

        }
    }

    private fun moveToCreatePost() {
        addFragment(CreatePostFragment(), CreatePostFragment.TAG, true)
    }

    private fun addFragment(fragment: Fragment, tag: String, toBackStack: Boolean = false,
                            transition: Int = FragmentTransaction.TRANSIT_FRAGMENT_OPEN) {
        postToMainThread(runCondition = { isAlive(lifecycle) }) {
            supportFragmentManager.findFragmentByTag(tag)?.let { return@postToMainThread }

            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.container, fragment, tag)
            if (toBackStack) {
                ft.addToBackStack(null)
            }
            ft.setTransition(transition)
            if (toBackStack) {
                ft.commitAllowingStateLoss()
            } else {
                ft.commitNowAllowingStateLoss()
            }
        }
    }

    private fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun getLifecycle(): LifecycleRegistry = lifecycleRegistry

}

data class InitialData(var hasAccount: Boolean,
                       var hasPosts: Boolean) {
    companion object {
        fun from(user: Single<User>,
                 hastPosts: Single<Boolean>): Single<InitialData> {
            return Single.zip<User, Boolean, InitialData>(
                    user, hastPosts,
                    BiFunction { account, hastPosts ->
                        InitialData(account != User.EMPTY, hastPosts)
                    })
        }
    }
}