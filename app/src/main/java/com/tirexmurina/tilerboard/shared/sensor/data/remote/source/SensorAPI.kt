package com.tirexmurina.tilerboard.shared.sensor.data.remote.source

import com.tirexmurina.tilerboard.shared.sensor.data.remote.models.SensorRemoteModelTemp
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SensorAPI {

    @GET("/states/{id}")
    suspend fun getSensorDataById(
        @Path("id") id : String
    ) : Response<SensorRemoteModelTemp> //TODO потом поменять на нормальный

}