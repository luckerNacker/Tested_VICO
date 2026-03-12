package com.example.zxc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.zxc.ui.theme.ZXCTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.columnSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZXCTheme {
                Greeting()
            }
        }
    }
}

@Composable
fun Greeting() {
    val stops = remember {
        listOf(
            "Центральная",
            "Парковая",
            "Вокзал",
            "Университет",
            "Площадь",
            "Рынок",
            "Больница",
            "Школа",
            "Театральная",
            "Спортивная",
            "Музейная",
            "Библиотека",
            "Завод",
            "Мост",
            "Набережная",
            "Аэропорт",
            "Стадион",
            "Кинотеатр",
            "Автовокзал",
            "Политех"
        )
    }

    val passengerData = remember {
        stops.map { Random.nextInt(50, 200) }
    }

    val chunkedStops = stops.chunked(4)
    val chunkedData = passengerData.chunked(4)

    val chartData = remember {
        chunkedStops.mapIndexed { index, stopsChunk ->
            ChartData(stopsChunk, chunkedData[index])
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(
            items = chartData,
            key = { index, _ -> index }
        ) { _, data ->
            ChartSection(
                stops = data.stops,
                data = data.passengerData
            )
        }
    }
}

data class ChartData(
    val stops: List<String>,
    val passengerData: List<Int>
)

@Composable
fun ChartSection(stops: List<String>, data: List<Int>) {
    val modelProducer = remember(data) { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            columnSeries {
                series(data)
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = CartesianValueFormatter { _, value, _ ->
                    stops.getOrNull(value.toInt())?.take(6) ?: value.toInt().toString()
                }
            )
        ),
        modelProducer = modelProducer,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}
