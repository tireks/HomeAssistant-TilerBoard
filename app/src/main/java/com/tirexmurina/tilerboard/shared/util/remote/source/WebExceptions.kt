package com.tirexmurina.tilerboard.shared.util.remote.source

class NotFoundException(errMsg: String) : Exception(errMsg)

class UnauthorizedException(errMsg: String) : Exception(errMsg)

class ForbiddenException(errMsg: String) : Exception(errMsg)

class ResponseFault(errMsg: String) : Exception(errMsg)

class NetworkFault(message: String) : Exception(message)

class RequestFault(errMsg: String) : Exception(errMsg)

class TokenCorruptedOrUnavailable(errMsg: String) : Exception(errMsg)

class SavedUrlUnavailable(errMsg: String) : Exception(errMsg)