package com.example.new_mit_mobile_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Health_Suvey : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_suvey)


        var retrofit = Retrofit.Builder()
            .baseUrl("http://203.255.56.35:8001")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var register_Service = retrofit.create(registerService::class.java)


        val checkbox1 = findViewById<CheckBox>(R.id.cb1)
        val checkbox2 = findViewById<CheckBox>(R.id.cb2)
        val checkbox3 = findViewById<CheckBox>(R.id.cb3)
        val checkbox4 = findViewById<CheckBox>(R.id.cb4)
        val checkbox5 = findViewById<CheckBox>(R.id.cb5)
        val checkbox6 = findViewById<CheckBox>(R.id.cb6)
        val checkbox7 = findViewById<CheckBox>(R.id.cb7)
        val checkbox8 = findViewById<CheckBox>(R.id.cb8)
        val checkbox9 = findViewById<CheckBox>(R.id.cb9)

        val button = findViewById<Button>(R.id.btn1)
        val button2 = findViewById<Button>(R.id.button2)

        button2.setOnClickListener{
            val loginintent = Intent(this,Login::class.java)
            startActivity(loginintent)
        }



        val cb1_result1 = checkbox1.isChecked
        val cb2_result2 = checkbox2.isChecked
        val cb3_result3 = checkbox3.isChecked
        val cb4_result4 = checkbox4.isChecked
        val cb5_result5 = checkbox5.isChecked
        val cb6_result6 = checkbox6.isChecked
        val cb7_result7 = checkbox7.isChecked
        val cb8_result8 = checkbox8.isChecked
        val cb9_result9 = checkbox9.isChecked


        button.setOnClickListener{

            val params = HashMap<String, String>()
            params["Firstname"] = Register.Firstname.Firstname_variable
            params["Lastname"] = Register.Lastname.Lastname_variable
//            params["email"] = Register.ID.ID_variable
            params["Password"] = Register.Password.Password_variable
            params["gender"] = Register.ID.ID_variable
            params["birth"] = Register.Birth.Birth_variable
            params["weight"] = Register.Weight.Weight_variable
            params["Height"] = Register.Height.Height_variable

            params["High_blood_pressure"] = cb1_result1.toString()
//            checkbox1.setOnCheckedChangeListener { _, isChecked ->
//                params["High_blood_pressure"] = isChecked.toString()
//            }
//
//            checkbox2.setOnCheckedChangeListener { _, isChecked ->
//                params["low_blood_pressure"] = isChecked.toString()
//            }
//            checkbox3.setOnCheckedChangeListener { _, isChecked ->
//                params["myocardial_infarction"] = isChecked.toString()
//            }
//            checkbox4.setOnCheckedChangeListener { _, isChecked ->
//                params["anemia"] = isChecked.toString()
//            }
//            checkbox5.setOnCheckedChangeListener { _, isChecked ->
//                params["diabetes"] = isChecked.toString()
//            }
//            checkbox6.setOnCheckedChangeListener { _, isChecked ->
//                params["brain_stoke"] = isChecked.toString()
//            }
//            checkbox7.setOnCheckedChangeListener { _, isChecked ->
//                params["heart_disease"] = isChecked.toString()
//            }


            params["low_blood_pressure"] = cb2_result2.toString()
            params["obesity"] = cb3_result3.toString()
            params["myocardial_infarction"] = cb4_result4.toString()
            params["anemia"] = cb5_result5.toString()
            params["hyperlipidemia"] = cb6_result6.toString()
            params["diabetes"] = cb7_result7.toString()
            params["brain_stoke"] = cb8_result8.toString()
            params["heart_disease"] = cb9_result9.toString()
//            Log.d("첫이름확인",Register.Firstname.Firstname_variable)
//            Log.d("체크박스확인",cb1_result1.toString())

            val jsonObject = JSONObject(params as Map<*, *>)
            val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

            register_Service.registerData(requestBody).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    // 요청 성공시 처리
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // 요청 실패시 처리
                }
            })

            Toast.makeText(baseContext, "Authentication ok.",
                Toast.LENGTH_LONG).show()
        }






    }


}