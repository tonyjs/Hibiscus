package com.tonyjs.hibiscus.ui.navigation

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class NavigationViewModel : ViewModel() {

    val navigationEvent = MutableLiveData<NavigationTo>()

    fun moveTo(to: NavigationTo, duplicatable: Boolean = false) {
        if (duplicatable) {
            navigationEvent.value = to
            return
        }

        if (to != navigationEvent.value) {
            navigationEvent.value = to
        }
    }

}

enum class NavigationTo {

    READY,
    HOME,
    SIGN_IN,
    GUIDE_TO_CREATE_POST,
    CREATE_POST,
    POST_LIST

}