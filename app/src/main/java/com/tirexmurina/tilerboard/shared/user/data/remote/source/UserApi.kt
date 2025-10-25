package com.tirexmurina.tilerboard.shared.user.data.remote.source

import com.tirexmurina.tilerboard.shared.user.data.remote.models.ApiAvailabilityResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface UserApi {

    @GET
    suspend fun getAvailability(@Url url: String = "") : Response<ApiAvailabilityResponse>
}