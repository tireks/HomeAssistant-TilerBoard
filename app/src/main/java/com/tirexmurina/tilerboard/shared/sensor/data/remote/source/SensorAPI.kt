package com.tirexmurina.tilerboard.shared.sensor.data.remote.source

import com.tirexmurina.tilerboard.shared.sensor.data.remote.models.SensorRemoteModelTemp
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SensorAPI {

    @GET("states/{id}")
    suspend fun getSensorDataById(
        @Path("id") id : String
    ) : Response<SensorRemoteModelTemp> //TODO потом поменять на нормальный

    /**
     * посмотреть, точно ли тут такая же модель приходит, по-моему нет.
     * Там кажется приходит что-то другое, нужна будет еще модель, и еще методы в хэлпер
     */
    @GET("states")
    suspend fun getAllSensors() : Response<List<SensorRemoteModelTemp>> //TODO посмотреть, точно ли тут такая же модель приходит, по-моему нет

}