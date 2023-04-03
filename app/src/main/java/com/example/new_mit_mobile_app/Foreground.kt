package com.example.new_mit_mobile_app

import android.app.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Foreground : Service() {
    val SC = "myService"
    var heart = Heart_Rate().Service_s

    private lateinit var mRunnable: Runnable

    private val delayMillis = 60000L // 1분
    private lateinit var handlerThread: HandlerThread
    private lateinit var handler: Handler





    //ID 변수 저장 내용 코드
    private var id = Login.ID
    private var db_id = id?.replace(".","")







    var timestamp = System.currentTimeMillis()

    //파베 주소 및 저장 경로 설정
    private var database = Firebase.database("https://mitlogin-27e78-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private var myRef = database.getReference("Heart_Rate")


    //db에 입력 되는 데이터 1분 간격으로 조정하는 문항
    private val vital_data = object : Runnable {
        override fun run() {
            Log.d("1초마다 실행","1초마다실행")
            datainput()
            Log.d("2초마다 실행","2초마다실행")
            handler.postDelayed(this, delayMillis)
        }
    }

    private fun datainput(){

        val value = Heart_Rate.MyApp.myVariable
        var long_now =System.currentTimeMillis()
        var t_date = Date(long_now)
        //날짜,시간을 가져오고 싶은 형태 선언
        myRef.child("Heart_rate").child("${db_id.toString()}").child(t_date.toString().toString()).setValue("${value.toString()}")
        Log.d("심박수값입니다","${value.toString()}")
        Log.d("현시간","$t_date")

        // 전역 변수 값 가져오기


        // 가져온 값을 사용하여 작업 수행





    }






    fun Notification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val nc = NotificationChannel(
                SC,
                "My Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val nm = getSystemService(NotificationManager::class.java)

            nm.createNotificationChannel(nc)
        } else {
            Toast.makeText(this, "알림을 실행할 수 없음", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate() {
        super.onCreate()


        handlerThread = HandlerThread("MyHandlerThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        handler.postDelayed(vital_data,delayMillis)

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Notification()

        val nc: Notification =
            NotificationCompat.Builder(this, SC).setContentTitle("Mit앱에서 생체 데이터를 수집하고 있습니다")
                .setSmallIcon(R.drawable.ic_icon).build()
        startForeground(1, nc)

//        val database = Firebase.database("https://mitlogin-27e78-default-rtdb.asia-southeast1.firebasedatabase.app/")
//        val myRef = database.getReference("Heart_Rate")
//
//
//
//        //현재 시간 설정
//        val current = LocalDateTime.now()
//        val formatter = DateTimeFormatter.ofPattern("MM월 dd일 HH시 mm분")
//        val formatted = current.format(formatter)
        // myRef.setValue(binding.etInput.text.toString()) 데이터가 1개가 계속 수정되는 방식
        // myRef.push().setValue(binding.etInput.text.toString()) 데이터가 계속 쌓이는 방식


        //심박수 값 들어가는 지 확인 코드
        //확인결과 심박수 값만 들어감.
        //myRef.push().setValue(s)

//        //아래 코드는 로그인 페이지에서 ID 데이터 가져오는것
//        val value1 = intent!!.getStringExtra("ID")
//        val value2 = value1?.replace(".","")
//
//        val value3 = intent!!.getStringExtra("Heart_rate")


//        //ID 변수 저장 내용 코드
//        var id = Login.ID
//        var db_id = id?.replace(".","")
//        var heart_rate_data = Heart_Rate.Heart_rate_data


//            time 설정 코드 부분
        //현재 시간 가져오기
//        val long_now =System.currentTimeMillis()
//        //현재 시간을 date 탕입으로 변환
//        val t_date = Date(long_now)
//        //날짜,시간을 가져오고 싶은 형태 선언
//        val t_dateFormat = SimpleDateFormat("yyy-MM-dd kk:mm:ss E", Locale("ko","KR"))
//
//        val str_date = t_dateFormat.format(t_date)
//
//        var timestamp = System.currentTimeMillis()
//
//        // 현재 데이터 realtime database에서 입력되는 것들
//
//        val user: MutableMap<String,Any> = HashMap()
//        user["$formatter"] = Heart_Rate
        //myRef.child("2018").updateChildren(user)
        //myRef.push(user)
        //myRef.push().child("심박수").setValue(s)
        //현재 사용하는 코드는 아래꺼이임
//            myRef.push().setValue(user)

        //최종 코드
        //myRef.child("Heart_rate").child("김규민").child(t_date.toString()).setValue(s)


        //타임 스태프형 코드



//        myRef.child("Heart_rate").child("${db_id.toString()}").child(timestamp.toString()).setValue("${heart_rate_data.toString()}")


        // 1분 간격으로 db에 데이터 input 하는 코드













        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }




}
