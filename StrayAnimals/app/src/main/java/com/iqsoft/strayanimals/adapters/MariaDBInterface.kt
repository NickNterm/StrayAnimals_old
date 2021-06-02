package com.iqsoft.strayanimals.adapters

import com.iqsoft.strayanimals.models.Account
import com.iqsoft.strayanimals.models.Post

interface MariaDBInterface {
    fun getAccountCallback(acc: Account){}
    fun accountBannedCallback(code: Int){}
    fun createAccountCallback(){}
    fun accountNullCallback(){}
    fun postsRead(posts: ArrayList<Post>){}
    fun accountRead(accounts: ArrayList<Account>){}
}