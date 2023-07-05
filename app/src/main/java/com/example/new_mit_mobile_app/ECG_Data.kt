package com.example.new_mit_mobile_app

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.io.BufferedReader
import java.io.InputStreamReader

class ECG_Data : AppCompatActivity() {

    private lateinit var chart: LineChart
    private lateinit var chart2: LineChart
    private lateinit var chart3: LineChart


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ecg_data)

        chart = findViewById(R.id.chart)
        chart2 = findViewById(R.id.chart2)
        chart3 = findViewById(R.id.chart3)























































































































































































































































































        val button_event = findViewById<Button>(R.id.button3)
        button_event.setOnClickListener(){
            var intent = Intent()
            intent.type = "text/*"
//            intent.action = Intent.ACTION_GET_CONTENT
            intent.action = Intent.ACTION_OPEN_DOCUMENT
            startActivityForResult(intent,1)
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == 1) {
//                val imageview_event = findViewById<ImageView>(R.id.imageView)

                var uri: Uri? = data.data
//                var imageUri : Uri? =data?.data
//                imageview_event.setImageURI(imageUri)
                // CSV 파일을 읽어서 화면에 표시
                //현재 주석처리 풀면 csv 파일 내용 보임
//                val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)
//                val inputStream: InputStream? = uri?.let { contentResolver.openInputStream(it) }
//                inputStream?.use {
//                    val reader = BufferedReader(InputStreamReader(it))
//                    var line: String?
//                    while (reader.readLine().also { line = it } != null) {
//                        // CSV 파일의 각 줄을 파싱하여 데이터를 추출하고, 해당 데이터를 화면에 표시
//                        val values = line!!.split(",").toTypedArray()
//                        val textView = TextView(this)
//                        textView.text = values.joinToString(separator = " ")
//                        linearLayout.addView(textView)
                val inputStream = uri?.let { contentResolver.openInputStream(it) }
                val reader = BufferedReader(InputStreamReader(inputStream))
                val lines = mutableListOf<String>()
                var line: String?
                var linenumber = 1

                while (reader.readLine().also { line = it }!= null){
                    if (linenumber > 14){
                        lines.add(line!!)
                    }
                    linenumber++
                }
//                Log.d("확인용",lines.toString())

                Log.d("확인용",lines[0])

                val yValues = mutableListOf<Float>()
                for (line in lines) {
                    var value = 0f
                    try {
                        value = line.toFloat()
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                    }
                    yValues.add(value)
                }

                Log.d("확인용2",yValues.toString())

                val xValues = mutableListOf<Float>()
                for (i in yValues.indices) {
                    xValues.add((i + 1).toFloat())
                }

                Log.d("확인용3",xValues.toString())


//                val entries = mutableListOf<Entry>()
//                for (i in yValues.indices) {
//                    entries.add(Entry(xValues[i], yValues[i]))
//                }
////
//                Log.d("확인용3",entries.toString())
//
//                val dataSet = LineDataSet(entries, "Line Chart")
//
//                val lineData = LineData(dataSet)
//                chart.data = lineData
//                chart.invalidate()

                val n = yValues.size / 3
                val yValues1 = yValues.subList(0, n)
                val yValues2 = yValues.subList(n, n*2)
                val yValues3 = yValues.subList(n*2, yValues.size)
                val xValues1 = xValues.subList(0, n)
                val xValues2 = xValues.subList(n, n*2)
                val xValues3 = xValues.subList(n*2, xValues.size)

                val entries1 = mutableListOf<Entry>()
                for (i in yValues1.indices) {
                    entries1.add(Entry(xValues1[i], yValues1[i]))
                }

                val entries2 = mutableListOf<Entry>()
                for (i in yValues2.indices) {
                    entries2.add(Entry(xValues2[i], yValues2[i]))
                }

                val entries3 = mutableListOf<Entry>()
                for (i in yValues3.indices) {
                    entries3.add(Entry(xValues3[i], yValues3[i]))
                }

                val dataSet1 = LineDataSet(entries1, "Line Chart")
                val dataSet2 = LineDataSet(entries2, "Line Chart2")
                val dataSet3 = LineDataSet(entries3, "Line Chart3")

                dataSet1.color = Color.parseColor("#FFA500") // 주황색
                dataSet1.setDrawCircles(false)
                dataSet1.setDrawCircleHole(true)
                dataSet1.circleRadius = 3f
                dataSet1.circleHoleRadius = 1.5f
                dataSet1.setCircleColor(Color.parseColor("#FFA500")) // 점 색깔 설정

                dataSet2.color = Color.parseColor("#FFA500") // 주황색
                dataSet2.setDrawCircles(false)
                dataSet2.setDrawCircleHole(true)
                dataSet2.circleRadius = 3f
                dataSet2.circleHoleRadius = 1.5f
                dataSet2.setCircleColor(Color.parseColor("#FFA500")) // 점 색깔 설정

                dataSet3.color = Color.parseColor("#FFA500") // 주황색
                dataSet3.setDrawCircles(false)
                dataSet3.setDrawCircleHole(true)
                dataSet3.circleRadius = 3f
                dataSet3.circleHoleRadius = 1.5f
                dataSet3.setCircleColor(Color.parseColor("#FFA500")) // 점 색깔 설정


                val lineData = LineData(dataSet1)
                val lineData2 = LineData(dataSet2)
                val lineData3 = LineData(dataSet3)


                chart.data = lineData
                chart2.data = lineData2
                chart3.data = lineData3

                chart.axisLeft.axisMinimum = -3f
                chart.axisLeft.axisMaximum = 3f
                chart2.axisLeft.axisMinimum = -3f
                chart2.axisLeft.axisMaximum = 3f
                chart3.axisLeft.axisMinimum = -3f
                chart3.axisLeft.axisMaximum = 3f

                chart.invalidate()
                chart2.invalidate()
                chart3.invalidate()



//                Log.d("확인용4",yValues1.toString())
//                Log.d("확인용5",yValues2.toString())






                inputStream?.close()
                reader.close()





            }
        }

    }
}