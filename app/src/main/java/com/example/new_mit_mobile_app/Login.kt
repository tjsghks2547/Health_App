package com.example.new_mit_mobile_app

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.new_mit_mobile_app.databinding.ActivityLoginBinding
import com.example.new_mit_mobile_app.databinding.BluetoothBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class Login : AppCompatActivity() {



    private lateinit var auth:FirebaseAuth





    private lateinit var binding: ActivityLoginBinding

    companion object{
        var ID :String? = null
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        auth = Firebase.auth

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.register.setOnClickListener{
            val intent = Intent(this,Register::class.java)
            startActivity(intent)
        }

        //let do login now.
        //first we add a login button

        binding.imageButton.setOnClickListener{
            performLogin()
        }




    }

    private fun performLogin(){

        //input data

        val email:EditText = findViewById(R.id.id_real)
        val password:EditText = findViewById(R.id.password_real)




        //null check on inputs
        if(email.text.isEmpty() || password.text.isEmpty()){
            Toast.makeText(this,"아이디와 패스워드를 채워주세요",Toast.LENGTH_SHORT).show()
            return
        }
        val emailInput = email.text.toString()
        val passwordInput = password.text.toString()








        auth.signInWithEmailAndPassword(emailInput,passwordInput)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful){
                    //Sign in success, update UI with the signed-in user's information
                    Log.d(TAG,"로그인 성공")
                    val intent = Intent(this,Bluetooth::class.java)
                    intent.putExtra("key","${emailInput.toString()}")

                    startActivity(intent)


                } else{
                    // If sign in fails, display a message to the user.

                    Toast.makeText(baseContext,"아이디와 비밀번호를 확인해주세요",Toast.LENGTH_SHORT)
                        .show()

            }
        }
            .addOnFailureListener {
                Toast.makeText(baseContext,"로그인 실패.${it.localizedMessage}",Toast.LENGTH_SHORT).show()
            }



    }



    override fun onStop() {
        super.onStop()

        val intent1 = Intent(this,Foreground::class.java)
        val email:EditText = findViewById(R.id.id_real)
        val emailInput = email.text.toString()
        ID =email.text.toString()





        Log.d("실행됨","11")


        }


}