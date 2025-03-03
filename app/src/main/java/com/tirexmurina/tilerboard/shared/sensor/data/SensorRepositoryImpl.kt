package com.tirexmurina.tilerboard.shared.sensor.data

import com.tirexmurina.tilerboard.shared.sensor.data.remote.models.SensorRemoteModelHelper
import com.tirexmurina.tilerboard.shared.sensor.data.remote.source.SensorAPI
import com.tirexmurina.tilerboard.shared.sensor.domain.entity.Sensor
import com.tirexmurina.tilerboard.shared.sensor.domain.repository.SensorRepository
import com.tirexmurina.tilerboard.shared.sensor.util.SensorDataFault
import com.tirexmurina.tilerboard.shared.util.source.ForbiddenException
import com.tirexmurina.tilerboard.shared.util.source.NetworkFault
import com.tirexmurina.tilerboard.shared.util.source.NotFoundException
import com.tirexmurina.tilerboard.shared.util.source.RequestFault
import com.tirexmurina.tilerboard.shared.util.source.ResponseFault
import com.tirexmurina.tilerboard.shared.util.source.UnauthorizedException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class SensorRepositoryImpl @Inject constructor(
    private val sensorAPI: SensorAPI,
    private val sensorRemoteModelHelper: SensorRemoteModelHelper
) : SensorRepository {
    override suspend fun getSensorDataByNameId(nameId: String): Sensor {
        val sensorDTO = try{
            val response = sensorAPI.getSensorDataById(nameId)
            if (response.isSuccessful) {
                response.body() ?: throw SensorDataFault("Loan with ID $nameId did not sent the data")
            } else {
                handleErrorResponse(response)
            }
        } catch ( e : IOException ){
            throw NetworkFault("Network error")
        } catch (e: Exception) {
            throw RequestFault("Request went wrong")
        }
        return sensorRemoteModelHelper.fromRemoteModel(sensorDTO)

    }

    override suspend fun getAllSensors(): List<Sensor> {
        val sensorDTOList = try {
            val response = sensorAPI.getAllSensors()
            if (response.isSuccessful){
                response.body() ?: throw SensorDataFault("Error acquired, while getting list of sensors")
            } else {
                handleErrorResponse(response)
            }
        } catch ( e : IOException ){
            throw NetworkFault("Network error")
        } catch (e: Exception) {
            throw RequestFault("Request went wrong")
        }

        return sensorDTOList.map { sensorRemoteModelHelper.fromRemoteModel(it) }
    }

    private fun <T> handleErrorResponse(response: Response<T>): Nothing {
        val errorMessage = response.errorBody()?.string()
        when (response.code()) {
            401 -> throw UnauthorizedException("Unauthorized: ${response.code()} $errorMessage")
            403 -> throw ForbiddenException("Forbidden: ${response.code()} $errorMessage")
            404 -> throw NotFoundException("Not Found: ${response.code()} $errorMessage")
            else -> throw ResponseFault("Something went wrong with response: ${response.code()} $errorMessage")
        }
    }

}