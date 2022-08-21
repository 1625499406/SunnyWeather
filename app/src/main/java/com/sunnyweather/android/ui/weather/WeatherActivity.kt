package com.sunnyweather.android.ui.weather

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.R
import com.sunnyweather.android.databinding.ActivityWeatherBinding
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    private lateinit var dataBinding : ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_weather)
        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        dataBinding = DataBindingUtil.setContentView<ActivityWeatherBinding>(this,R.layout.activity_weather)

        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }

        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this,"无法获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }

        })
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
    }

    private fun showWeatherInfo(weather: Weather) {

        val realtime = weather.realtime
        val daily = weather.daily
        dataBinding.apply {
            nowLayout.apply {
                placeName.text = viewModel.placeName
                //填充now.xml布局
                val currentTempText = "${realtime.temperature.toInt()} °C"
                currentTemp.text = currentTempText
                currentSky.text = getSky(realtime.skycon).info
                Log.d("LLL",realtime.temperature.toString())
                val currentPM25Text = "空气指数${realtime.airQuality.aqi.chn.toInt()}"
                currentAQI.text = currentPM25Text
                nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
            }
            //填充forecast.xml布局
            forecastLayout.apply {
                forecastLayout.removeAllViews()
                val days = daily.skycon.size
                for (i in 0 until days) {
                    val skycon = daily.skycon[i]
                    val temperature = daily.temperature[i]
                    val view = LayoutInflater.from(this@WeatherActivity).inflate(R.layout.forecast_item,
                        forecastLayout,false)
                    val dateInfo = view.findViewById(R.id.dataInfo) as TextView
                    val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
                    val skyInfo = view.findViewById(R.id.skyInfo) as TextView
                    val temperatureInfo = view.findViewById(R.id.temperatureIofo) as TextView
                    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    dateInfo.text = simpleDateFormat.format(skycon.date)
                    val sky = getSky(skycon.value)
                    skyIcon.setImageResource(sky.icon)
                    skyInfo.text = sky.info
                    val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} °C"
                    temperatureInfo.text = tempText
                    forecastLayout.addView(view)
                }
            }
            //填充life_index.xml布局数据
            lifeIndexLayout.apply {
                val lifeIndex = daily.lifeindex
                coldRiskText.text = lifeIndex.coldRisk[0].desc
                dressingText.text = lifeIndex.dressing[0].desc
                ultravioletText.text = lifeIndex.ultraviolet[0].desc
                carWashingText.text = lifeIndex.carWashing[0].desc
                weatherLayout.visibility = View.VISIBLE
            }
        }
    }
}