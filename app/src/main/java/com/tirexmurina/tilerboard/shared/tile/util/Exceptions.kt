package com.tirexmurina.tilerboard.shared.tile.util

class KitTileException(errMsg: String) : Exception(errMsg)

class TileCreationException(errMsg: String) : Exception(errMsg)

class UnexpectedTileTypeOrValue(errMsg: String) : Exception(errMsg)

class FailedToParseDBTileValue(errMsg: String) : Exception(errMsg)
class TileDetachException(errMsg: String) : Exception(errMsg)
