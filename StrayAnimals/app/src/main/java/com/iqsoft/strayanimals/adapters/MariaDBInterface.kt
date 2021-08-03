package com.iqsoft.strayanimals.adapters

import com.iqsoft.strayanimals.models.Account
import com.iqsoft.strayanimals.models.Post

interface MariaDBInterface {
    fun getAccountCallback(acc: Account) {}
    fun accountBannedCallback(code: Int) {}
    fun createAccountCallback() {}
    fun editAccountCallback() {}
    fun accountNullCallback() {}
    fun postsReadCallback(posts: ArrayList<Post>) {}
    fun postsNotFound() {}
    fun postsReadFromTokenCallback(posts: ArrayList<Post>) {}
    fun accountReadCallback(accounts: ArrayList<Account>) {}
}