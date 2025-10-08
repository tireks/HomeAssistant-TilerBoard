package com.tirexmurina.tilerboard.source.remote

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenDataStore(context: Context) {

    companion object{
        private const val ENCRYPTED_PREFS_NAME = "encrypted_prefs_token"
        private const val LOCAL_TOKEN = "local_token"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        ENCRYPTED_PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getAccessToken(): String? {
        return sharedPreferences.getString(LOCAL_TOKEN, null)
    }

    fun setAccessToken(token: String) {
        sharedPreferences.edit().putString(LOCAL_TOKEN, token).apply()
    }

    fun isAccessTokenSaved(): Boolean {
        val token = getAccessToken()
        return !token.isNullOrEmpty()
    }

    fun clearAccessToken() {
        sharedPreferences.edit().remove(LOCAL_TOKEN).apply()
    }

}