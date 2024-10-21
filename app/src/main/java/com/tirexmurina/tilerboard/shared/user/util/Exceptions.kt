package com.tirexmurina.tilerboard.shared.user.util

class SharedPrefsCorruptedException(errMsg: String) : Exception(errMsg)

class DataBaseCorruptedException(errMsg: String) : Exception(errMsg)

class UserAuthException(errMsg: String) : Exception(errMsg)

class TokenException(errMsg: String) : Exception(errMsg)

class UnknownException(errMsg: String) : Exception(errMsg)