package com.example.new_mit_mobile_app

import android.Manifest.permission.ACTIVITY_RECOGNITION
import android.Manifest.permission_group.ACTIVITY_RECOGNITION
import android.app.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
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
import java.sql.DriverManager

class Foreground : Service(), SensorEventListener {
    val SC = "myService"

    private lateinit var mRunnable: Runnable

    private val delayMillis = 6000L // 1분
    private lateinit var handlerThread: HandlerThread
    private lateinit var handler: Handler


    //인터넷 연결 확인 변수들

    private lateinit var cm2 : ConnectivityManager

    var internetcheck : Int = 0
    private val networkCallBack = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            // 네트워크가 연결될 때 호출됩니다.
            internetcheck = 1
        }

        override fun onLost(network: Network) {
            // 네트워크가 끊길 때 호출됩니다.
            internetcheck = 2

        }
    }


    //걸음수 코드

    private lateinit var sensorManager: SensorManager
    private var stepCountSensor: Sensor? = null
    private var stepDetectorSensor: Sensor? = null
    private var stepCount: Int = 0
    private var counterstep: Int = 0
    private var Allcounterstep: Int = 0

    private var lastDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)


    class walkApp : Application() {

        companion object {
            var Total_step: Int = 0
        }

        override fun onCreate() {
            super.onCreate()

            // 전역 변수 초기화
            Total_step = 0



        }
    }








    //ID 변수 저장 내용 코드
    private var id = Login.ID
    private var db_id = id?.replace(".","")







    var timestamp = System.currentTimeMillis()

    //파베 주소 및 저장 경로 설정
    private var database = Firebase.database("https://mitlogin-27e78-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private var myRef = database.getReference("Health_data")


    //db에 입력 되는 데이터 1분 간격으로 조정하는 문항
    private val vital_data = object : Runnable {
        override fun run() {
            Log.d("1초마다 실행","1초마다실행")
            datainput()
            Log.d("2초마다 실행","2초마다실행")
            handler.postDelayed(this, delayMillis)
        }
    }

    private fun checkDayChange() {

        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH) + 1
        val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
        val currDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        if (currDay != lastDay) {
            lastDay = currDay
            // 변수값 초기화 또는 다른 작업 수행
            myRef.child("Total_steps").child("${db_id.toString()}").child(dayOfMonth.toString()).setValue("${counterstep.toString()}")
            Allcounterstep = 0
            walkApp.Total_step = 0


        }
    }



    private fun datainput(){

        if (internetcheck == 1) {
            val value = Heart_Rate.MyApp.myVariable
            val value2 = Heart_Rate.Sp02App.Sp02


            var long_now = System.currentTimeMillis()
            var t_date = Date(long_now)
            //날짜,시간을 가져오고 싶은 형태 선언
            myRef.child("heart").child("${db_id.toString()}").child(t_date.toString().toString())
                .setValue("${value.toString()}")
            myRef.child("walk").child("${db_id.toString()}").child(t_date.toString().toString())
                .setValue("${counterstep.toString()}")
            myRef.child("Sp02").child("${db_id.toString()}").child(t_date.toString().toString())
                .setValue("${value2.toString()}")


            Log.d("심박수값입니다", "${value.toString()}")
            Log.d("현시간", "$t_date")

            counterstep = 0
            checkDayChange()


            // 전역 변수 값 가져오기


            // 가져온 값을 사용하여 작업 수행
        }
        else if(internetcheck == 2){
            val nc2: Notification =
                NotificationCompat.Builder(this, SC).setContentTitle("Mit앱과 연결이 끊겼습니다.")
                    .setSmallIcon(R.drawable.ic_icon).build()
            startForeground(2,nc2)
        }




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


        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)


        handlerThread = HandlerThread("MyHandlerThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        handler.postDelayed(vital_data,delayMillis)


        //인터넷 확인 코드

        cm2 = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val builder = NetworkRequest.Builder()
        cm2.registerNetworkCallback(builder.build(),networkCallBack)








    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Notification()

        val nc: Notification =
            NotificationCompat.Builder(this, SC).setContentTitle("Mit앱에서 생체 데이터를 수집하고 있습니다")
                .setSmallIcon(R.drawable.ic_icon).build()
        startForeground(1, nc)






        // 걸음수 코드 입니다~
        if (stepCountSensor == null) {
            Log.e(TAG, "Step count sensor not available")
        } else {
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        if (stepDetectorSensor == null) {
            Log.e(TAG, "Step detector sensor not available")
        } else {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        //
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

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                stepCount = event.values[0].toInt()
                counterstep++
                Allcounterstep++

//                Log.d(TAG, "Step count: $stepCount")
//                Log.d(TAG, "counter count: $counterstep")
            } else if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                stepCount++
                counterstep++
                Allcounterstep++
//                Log.d(TAG, "Step count: $stepCount")
//                Log.d(TAG, "counter count: $counterstep")
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    // 콜백 해제 안하면 계속해서 콜백되어 다른 액티비티에서도 인터넷 연결 체크가 됨
    override fun onDestroy() { // 콜백 해제
        super.onDestroy()
        cm2 = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm2.unregisterNetworkCallback(networkCallBack)
    }


}
