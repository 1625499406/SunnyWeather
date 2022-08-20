package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName
//服务器返回的JSON数据  P603页
data class PlaceResponse(val status: String, val places: List<Place>)

//@SerializedName 注解来对应JSON 字段和Kotlin字段建立映射关系
data class Place(val name: String, val location: Location,
                 @SerializedName("formatted_address") val address: String)

data class Location(val lng: String, val lat: String )