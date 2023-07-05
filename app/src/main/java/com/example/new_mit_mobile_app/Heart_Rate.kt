package com.example.new_mit_mobile_app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.new_mit_mobile_app.Heart_Rate.MyVariables.Companion.myVariable
import com.example.new_mit_mobile_app.databinding.ActivityHeartRateGraphBinding
import com.example.new_mit_mobile_app.databinding.ActivityMainBinding
import com.example.new_mit_mobile_app.databinding.BluetoothBinding
import com.example.new_mit_mobile_app.databinding.HeartRateBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.childEvents
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.lang.Runnable
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Heart_Rate :  AppCompatActivity(), CoroutineScope by MainScope(),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener
{
    //MainActivtiy 는 bluetooth 심박수 view 화면 구성 작업 //

    //변수 선언 //
    var activityContext: Context? = null
    var i : Float = 1.0f


    class MyApp : Application() {

        companion object {
            var myVariable: String = ""

        }

        override fun onCreate() {
            super.onCreate()

            // 전역 변수 초기화
            myVariable = "initial value"
        }
    }

    class Sp02App : Application() {

        companion object{
            var Sp02 : String = ""
        }

        override fun onCreate() {
            super.onCreate()

            // 전역 변수 초기화
            Sp02 = "initial value"
        }
    }




    class MyVariables {
        companion object {
            var myVariable: String = ""
        }
    }










    //Wear os 와 Mobile app 통신 payload 변수 선언 부분 //

    private val wearableAppCheckPayload = "AppOpenWearable"
    private val wearableAppCheckPayloadReturnACK = "AppOpenWearableACK"
    private var wearableDeviceConnected: Boolean = false
    private var currentAckFromWearForAppOpenCheck: String? = null
    private val APP_OPEN_WEARABLE_PAYLOAD_PATH = "/APP_OPEN_WEARABLE_PAYLOAD"
    private val MESSAGE_ITEM_RECEIVED_PATH: String = "/message-item-received"
    private val MESSAGE_ITEM_RECEIVED_PATH2: String = "/message-item-received2"

    //서비스로 넘어가는 변수

    public var Service_s :Any = ""



    // 노드 변수 및 그 외 변수 선언 //

    private val TAG_GET_NODES: String = "getnodes1"
    private val TAG_MESSAGE_RECEIVED: String = "receive1"
    private val TAG_MESSAGE_RECEIVED2: String = "receive2"

    private var messageEvent: MessageEvent? = null
    private var wearableNodeUri: String? = null



    //차트 변수 선언//
    lateinit var linelist : ArrayList<Entry>
    lateinit var lineDataSet: LineDataSet
    lateinit var lineData: LineData




    private lateinit var binding: HeartRateBinding



    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("SetTextI18n")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HeartRateBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        //this는 여기서 MainActivity를 의미함 .
        activityContext = this

        wearableDeviceConnected = false

        linelist = ArrayList()
        Log.d(TAG,"onCreate 작동중")




        //서비스 버튼 테스트
        binding.ServiceButton.setOnClickListener{
            val serviceIntent = Intent(this, Foreground::class.java)
            startService(serviceIntent)
            Toast.makeText(this, "Service start", Toast.LENGTH_LONG).show()


        }

        binding.ecgButton.setOnClickListener{
            val ecgIntent = Intent(this,ECG_Data::class.java)
            startActivity(ecgIntent)
            Toast.makeText(this,"저장소에서 ECG 데이터를 가져와주세요",Toast.LENGTH_LONG).show()
        }



        //차트 옵션 설정


        val database = Firebase.database("https://mitlogin-27e78-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val myRef = database.getReference("Heart_Rate")



        //현재 시간 설정
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("MM월 dd일 HH시 mm분")
        val formatted = current.format(formatter)
        // myRef.setValue(binding.etInput.text.toString()) 데이터가 1개가 계속 수정되는 방식
        // myRef.push().setValue(binding.etInput.text.toString()) 데이터가 계속 쌓이는 방식


        //심박수 값 들어가는 지 확인 코드
        //확인결과 심박수 값만 들어감.
        //myRef.push().setValue(s)

        //아래 코드는 로그인 페이지에서 ID 데이터 가져오는것
        val value1 = intent!!.getStringExtra("ID")
        val value2 = value1?.replace(".","")

        val value3 = intent!!.getStringExtra("Heart_rate")




//            time 설정 코드 부분
        //현재 시간 가져오기
        val long_now =System.currentTimeMillis()
        //현재 시간을 date 탕입으로 변환
        val t_date = Date(long_now)
        //날짜,시간을 가져오고 싶은 형태 선언
        val t_dateFormat = SimpleDateFormat("yyy-MM-dd kk:mm:ss E", Locale("ko","KR"))

        val str_date = t_dateFormat.format(t_date)

        var timestamp = System.currentTimeMillis()



























    }
    // 블루투스 부분
    private fun initialiseDeviceParing(temAct: Activity) {
        //Coroutine
        launch(Dispatchers.Default){
            var getNodesResBool :  BooleanArray? = null

            try {
                getNodesResBool =
                    getNodes(temAct.applicationContext)
            } catch (e: Exception){
                e.printStackTrace()
            }


            //UI thread

            withContext(Dispatchers.Main){
                if(getNodesResBool!![0]){
                    //if message Acknowlegement Recevied
                    if (getNodesResBool[1]){
                        Toast.makeText(
                            activityContext,
                            "Wearable device paired and app is open. Tap the \"Send Message to Wearable\" button to send the message to your wearable device.",
                            Toast.LENGTH_LONG
                        ).show()

                        wearableDeviceConnected = true

                    } else {
                        Toast.makeText(
                            activityContext,
                            "A wearable device is paired but the wearable app on your watch isn't open. Launch the wearable app and try again.",
                            Toast.LENGTH_LONG
                        ).show()

                        wearableDeviceConnected = false

                    }
                } else {
                    Toast.makeText(
                        activityContext,
                        "No wearable device paired. Pair a wearable device to your phone using the Wear OS app and try again.",
                        Toast.LENGTH_LONG
                    ).show()

                    wearableDeviceConnected = false

                }
            }
        }

    }

    private fun getNodes(context: Context?): BooleanArray {
        val nodeResults = HashSet<String>()
        val resBool = BooleanArray(2)
        resBool[0] = false //nodePresent 현재 노드값
        resBool[1] = false //wearableReturnAckReceived 다시 리턴했을때의 ack값
        val nodeListTask =
            Wearable.getNodeClient(context).connectedNodes
        try {
            // Block on a task and get the result synchronously (because this is on a background thread).
            val nodes =
                Tasks.await(
                    nodeListTask
                )
            Log.e(TAG_GET_NODES, "Task fetched nodes")
            for (node in nodes) {
                Log.e(TAG_GET_NODES, "inside loop")
                nodeResults.add(node.id)
                try {
                    val nodeId = node.id
                    // Set the data of the message to be the bytes of the Uri.
                    val payload: ByteArray = wearableAppCheckPayload.toByteArray()
                    // Send the rpc
                    // Instantiates clients without member variables, as clients are inexpensive to
                    // create. (They are cached and shared between GoogleApi instances.)
                    val sendMessageTask =
                        Wearable.getMessageClient(context)
                            .sendMessage(nodeId, APP_OPEN_WEARABLE_PAYLOAD_PATH, payload)
                    try {
                        // Block on a task and get the result synchronously (because this is on a background thread).
                        val result = Tasks.await(sendMessageTask)
                        Log.d(TAG_GET_NODES, "send message result : $result")
                        resBool[0] = true
                        //Wait for 1000 ms/1 sec for the acknowledgement message
                        //Wait 1
                        if (currentAckFromWearForAppOpenCheck != wearableAppCheckPayloadReturnACK) {
                            Thread.sleep(100)
                            Log.d(TAG_GET_NODES, "ACK thread sleep 1")
                        }
                        if (currentAckFromWearForAppOpenCheck == wearableAppCheckPayloadReturnACK) {
                            resBool[1] = true
                            return resBool
                        }
                        //Wait 2
                        if (currentAckFromWearForAppOpenCheck != wearableAppCheckPayloadReturnACK) {
                            Thread.sleep(150)
                            Log.d(TAG_GET_NODES, "ACK thread sleep 2")
                        }
                        if (currentAckFromWearForAppOpenCheck == wearableAppCheckPayloadReturnACK) {
                            resBool[1] = true
                            return resBool
                        }
                        //Wait 3
                        if (currentAckFromWearForAppOpenCheck != wearableAppCheckPayloadReturnACK) {
                            Thread.sleep(200)
                            Log.d(TAG_GET_NODES, "ACK thread sleep 3")
                        }
                        if (currentAckFromWearForAppOpenCheck == wearableAppCheckPayloadReturnACK) {
                            resBool[1] = true
                            return resBool
                        }
                        //Wait 4
                        if (currentAckFromWearForAppOpenCheck != wearableAppCheckPayloadReturnACK) {
                            Thread.sleep(250)
                            Log.d(TAG_GET_NODES, "ACK thread sleep 4")
                        }
                        if (currentAckFromWearForAppOpenCheck == wearableAppCheckPayloadReturnACK) {
                            resBool[1] = true
                            return resBool
                        }
                        //Wait 5
                        if (currentAckFromWearForAppOpenCheck != wearableAppCheckPayloadReturnACK) {
                            Thread.sleep(350)
                            Log.d(TAG_GET_NODES, "ACK thread sleep 5")
                        }
                        if (currentAckFromWearForAppOpenCheck == wearableAppCheckPayloadReturnACK) {
                            resBool[1] = true
                            return resBool
                        }
                        resBool[1] = false
                        Log.d(
                            TAG_GET_NODES,
                            "ACK thread timeout, no message received from the wearable "
                        )
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                } catch (e1: Exception) {
                    Log.d(TAG_GET_NODES, "send message exception")
                    e1.printStackTrace()
                }
            } //end of for loop
        } catch (exception: Exception) {
            Log.e(TAG_GET_NODES, "Task failed: $exception")
            exception.printStackTrace()
        }
        return resBool
    }

    @SuppressLint("SetTextI18n")

    override fun onDataChanged(p0: DataEventBuffer) {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onMessageReceived(p0: MessageEvent) {
        try {

            // 데이터 블루투스로 받아 오는 코드
             val s =
                String(p0.data, StandardCharsets.UTF_8)












            val messageEventPath: String = p0.path
            Log.d(
                TAG_MESSAGE_RECEIVED,
                "onMessageReceived() Received a message from watch:"
                        + p0.requestId
                        + " "
                        + messageEventPath
                        + " "
                        + s

            )



            binding.totalWalk.text =Foreground.walkApp.Total_step.toString()

            //파이어베이스 리얼타임 데이터베이스 데이터 넣기
            val database = Firebase.database("https://mitlogin-27e78-default-rtdb.asia-southeast1.firebasedatabase.app/")
            val myRef = database.getReference("Heart_Rate")


            //현재 시간 설정
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("MM월 dd일 HH시 mm분")
            val formatted = current.format(formatter)
            // myRef.setValue(binding.etInput.text.toString()) 데이터가 1개가 계속 수정되는 방식
            // myRef.push().setValue(binding.etInput.text.toString()) 데이터가 계속 쌓이는 방식


            //심박수 값 들어가는 지 확인 코드
            //확인결과 심박수 값만 들어감.
            //myRef.push().setValue(s)



//            time 설정 코드 부분
            //현재 시간 가져오기
            val long_now =System.currentTimeMillis()
            //현재 시간을 date 탕입으로 변환
            val t_date = Date(long_now)
            //날짜,시간을 가져오고 싶은 형태 선언
            val t_dateFormat = SimpleDateFormat("yyy-MM-dd kk:mm:ss E", Locale("ko","KR"))

            val str_date = t_dateFormat.format(t_date)

            var timestamp = System.currentTimeMillis()

            // 현재 데이터 realtime database에서 입력되는 것들

            Log.d("현재 메세지 보내는중",s)

            val user: MutableMap<String,Any> = HashMap()
            user["$formatter"] = s
            //myRef.child("2018").updateChildren(user)
            //myRef.push(user)
            //myRef.push().child("심박수").setValue(s)
            //현재 사용하는 코드는 아래꺼이임
//            myRef.push().setValue(user)

            //최종 코드
            //myRef.child("Heart_rate").child("김규민").child(t_date.toString()).setValue(s)

            //타임 스태프형 코드
//            myRef.child("Heart_rate").child("김선환").child(timestamp.toString()).setValue(s)











            //bar chart 코드 부분
            val entries = ArrayList<BarEntry>()
            val currentTime : Long = System.currentTimeMillis()
            val dataFormat = SimpleDateFormat("HH:mm:ss")













            if (messageEventPath == APP_OPEN_WEARABLE_PAYLOAD_PATH) {
                currentAckFromWearForAppOpenCheck = s
                Log.d(
                    TAG_MESSAGE_RECEIVED,
                    "Received acknowledgement message that app is open in wear"
                )

//                val sbTemp = StringBuilder()
//                sbTemp.append(binding.messagelogTextView.text.toString())
//                sbTemp.append("\nWearable device connected.")
//                Log.d("receive1", " $sbTemp")
//                binding.messagelogTextView.text = sbTemp

                val real_time_heart_beat : String? = null




                messageEvent = p0
                wearableNodeUri = p0.sourceNodeId
            }else if(messageEventPath == MESSAGE_ITEM_RECEIVED_PATH2){
                currentAckFromWearForAppOpenCheck = s
                Log.d(
                    TAG_MESSAGE_RECEIVED2,
                    "Received acknowledgement message that app is open in wear"
                )

                try{
                    val sbTemp2 = StringBuilder()
                    sbTemp2.append("\n")
                    sbTemp2.append(s)
                    Log.d("receive2","$sbTemp2")
                    Sp02App.Sp02 = "${sbTemp2.toString()}"



                }catch (e:Exception){

                }

            }
            else if (messageEventPath.isNotEmpty() && messageEventPath == MESSAGE_ITEM_RECEIVED_PATH) {

                try {
                    binding.messagelogTextView.visibility = View.VISIBLE


                    val sbTemp = StringBuilder()
                    sbTemp.append("\n")
                    sbTemp.append(s)
                    Log.d("receive1", " $sbTemp")

                    binding.messagelogTextView.text = s





                    var data = Login.ID
                    val value2 = data?.replace(".","")


                    //전역 변수 값 변경
                    MyApp.myVariable = "$sbTemp"


//                    myRef.child("Heart_rate").child("$value2").child(timestamp.toString()).setValue("$sbTemp")
//                    binding.messagelogTextView.append(sbTemp)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("receive1", "Handled")
        }

    }

    override fun onPause() {
        super.onPause()
        try {
            Wearable.getDataClient(activityContext!!).removeListener(this)
            Wearable.getMessageClient(activityContext!!).removeListener(this)
            Wearable.getCapabilityClient(activityContext!!).removeListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d(TAG,"onPasue 작동중")
    }

    override fun onResume(){
        super.onResume()
        try {
            Wearable.getDataClient(activityContext!!).addListener(this)
            Wearable.getMessageClient(activityContext!!).addListener(this)
            Wearable.getCapabilityClient(activityContext!!)
                .addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d(TAG,"onResume 작동중")

    }

    override fun onCapabilityChanged(p0: CapabilityInfo) {

    }

    override fun onStop() {
        super.onStop()
        try {
            Wearable.getDataClient(activityContext!!).addListener(this)
            Wearable.getMessageClient(activityContext!!).addListener(this)
            Wearable.getCapabilityClient(activityContext!!)
                .addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE)
            //심박수 서비스 코드로 넘겨주는 코드 구현\

            val intent2 = Intent(this,Foreground::class.java)
            intent2.putExtra("Heart_rate","$Service_s")
            startService(intent2)

        } catch (e: Exception) {
            e.printStackTrace()
        }







    }



}