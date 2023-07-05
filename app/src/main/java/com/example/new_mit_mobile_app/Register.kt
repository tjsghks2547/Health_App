package com.example.new_mit_mobile_app

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.example.new_mit_mobile_app.Register.Birth.Companion.Birth_variable
import com.example.new_mit_mobile_app.Register.Firstname.Companion.Firstname_variable
import com.example.new_mit_mobile_app.Register.Gender.Companion.Gender_variable
import com.example.new_mit_mobile_app.Register.Height.Companion.Height_variable
import com.example.new_mit_mobile_app.Register.ID.Companion.ID_variable
import com.example.new_mit_mobile_app.Register.Lastname.Companion.Lastname_variable
import com.example.new_mit_mobile_app.Register.Password.Companion.Password_variable
import com.example.new_mit_mobile_app.Register.Weight.Companion.Weight_variable
import com.example.new_mit_mobile_app.databinding.ActivityLoginBinding
import com.example.new_mit_mobile_app.databinding.BluetoothBinding
import com.example.new_mit_mobile_app.databinding.RegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {


    class Firstname : Application() {

        companion object {
            var Firstname_variable: String = ""

        }

        override fun onCreate() {
            super.onCreate()

            // 전역 변수 초기화
            Firstname_variable = "initial value"
        }
    }

    class Lastname : Application() {

        companion object {
            var Lastname_variable: String = ""

        }

        override fun onCreate() {
            super.onCreate()

            // 전역 변수 초기화
            Lastname_variable = "initial value"
        }
    }

    class ID : Application() {

        companion object {
            var ID_variable: String = ""

        }

        override fun onCreate() {
            super.onCreate()

            // 전역 변수 초기화
            ID_variable = "initial value"
        }
    }

    class Password : Application() {

        companion object {
            var Password_variable: String = ""

        }

        override fun onCreate() {
            super.onCreate()

            // 전역 변수 초기화
            Password_variable = "initial value"
        }
    }

    class Gender : Application() {

        companion object {
            var Gender_variable: String = ""

        }

        override fun onCreate() {
            super.onCreate()

            // 전역 변수 초기화
            Gender_variable = "initial value"
        }
    }

    class Birth : Application() {

        companion object {
            var Birth_variable: String = ""

        }

        override fun onCreate() {
            super.onCreate()

            // 전역 변수 초기화
            Birth_variable = "initial value"
        }
    }

    class Weight : Application() {

        companion object {
            var Weight_variable: String = ""

        }

        override fun onCreate() {
            super.onCreate()

            // 전역 변수 초기화
            Weight_variable = "initial value"
        }
    }

    class Height : Application() {

        companion object {
            var Height_variable: String = ""

        }

        override fun onCreate() {
            super.onCreate()

            // 전역 변수 초기화
            Height_variable = "initial value"
        }
    }



    private lateinit var auth:FirebaseAuth

    private lateinit var binding: RegisterBinding
    val fireStoreDatabase = FirebaseFirestore.getInstance()
    val TAG : String = "FireStroe"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        binding = RegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //initialize Firebase Auth
        auth = Firebase.auth



        //lets get email and password from the user


        binding.hi.setOnClickListener{
            performSignUp()
            Log.d(TAG,"Hi")

        }






    }


    private fun performSignUp() {

        // 변수 선언
        val email = findViewById<EditText>(R.id.id2)
        val password = findViewById<EditText>(R.id.password2)





        Log.d(TAG,"Hello2")

        if (email.text.isEmpty() || password.text.isEmpty()){
            Toast.makeText(this,"빈칸을 다 채워주세요",Toast.LENGTH_SHORT)
            return
        }

        val inputEmail = email.text.toString()
        val inputPassword = password.text.toString()

        auth.createUserWithEmailAndPassword(inputEmail, inputPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val intent3 = Intent(this,Health_Suvey::class.java)
                    startActivity(intent3)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }
            .addOnFailureListener {
                Toast.makeText(this,"Error ${it.localizedMessage}",Toast.LENGTH_SHORT).show()
            }

        var firstname = findViewById<EditText>(R.id.name_first).text.toString()
        var lastname = findViewById<EditText>(R.id.name_last).text.toString()
        var gender = findViewById<EditText>(R.id.edit_gender).text.toString()
        var birth = findViewById<EditText>(R.id.edit_date).text.toString()
        var weight = findViewById<EditText>(R.id.edit_weight).text.toString()
        var Height = findViewById<EditText>(R.id.edit_height).text.toString()


        // firestroe에 db에 회원정보 넣는 칸 key,value 값 조정
        val user: MutableMap<String,Any> = HashMap()
        user["email"] = inputEmail
        user["FirstName"] = firstname
        user["lastName"] = lastname
        user["gender"] = gender
        user["birth"] = birth
        user["weight"] = weight
        user["Height"] = Height


        // 전역 변수 값 저장하는 변수들

        Firstname_variable = firstname
        Lastname_variable = lastname
        ID_variable = inputEmail
        Password_variable = inputPassword
        Gender_variable = gender
        Birth_variable = birth
        Weight_variable = weight
        Height_variable = Height







        Log.d(TAG,"여기까지됨")
        //데이터가 저장되는 장소 명명


        //데이터 document 추가로 지정하기 완료
        fireStoreDatabase.collection("users").document("$inputEmail")
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "added document with ID ")
            }
            .addOnFailureListener {
                Log.w(TAG, "Error adding document")
            }

    }
}