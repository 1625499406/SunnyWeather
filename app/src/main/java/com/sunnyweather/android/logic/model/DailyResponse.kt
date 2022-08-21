package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName
import com.sunnyweather.android.R
import java.util.*

data class DailyResponse(val status: String, val result: Result){

    data class Result(val daily: Daily)

    data class Daily(val temperature: List<Temperature>, val skycon: List<Skycon>,
                     @SerializedName("life_index") val lifeindex: Lifeindex)
    data class Temperature(val max: Float, val min: Float)

    data class Skycon(val value: String, val date: Date)

    data class Lifeindex(val coldRisk: List<LifeDescription>, val carWashing: List<LifeDescription>,
    val ultraviolet: List<LifeDescription>, val dressing: List<LifeDescription>)

    data class LifeDescription(val desc: String)
}