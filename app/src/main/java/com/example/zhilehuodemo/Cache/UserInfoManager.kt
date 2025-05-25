package com.example.zhilehuodemo.Cache

import com.tencent.mmkv.MMKV

object UserInfoManager {
    private val mmkv by lazy { MMKV.mmkvWithID("import_cache") }

    private const val KEY_USERNAME="user_name"
    private const val KEY_USERSEX="user_sex"
    private const val KEY_USERAVATAR="user_avatar"
    private const val KEY_USERBIRTH="user_birth"

    var userName
        get() = mmkv.getString(KEY_USERNAME,"")
        set(value) {
            mmkv.putString(KEY_USERNAME,value)
        }

    /**
     * true 为男生,false 为女生
     */
    var userSex
        get() = mmkv.getBoolean(KEY_USERSEX,true)
        set(value) {
            mmkv.putBoolean(KEY_USERSEX,value)
        }

    var userAvatar
        get() = mmkv.getString(KEY_USERAVATAR,"")
        set(value) {
            mmkv.putString(KEY_USERAVATAR,value)
        }

    var userBirth
        get() = mmkv.getString(KEY_USERBIRTH,"")
        set(value) {
            mmkv.putString(KEY_USERBIRTH,value)
        }
}
