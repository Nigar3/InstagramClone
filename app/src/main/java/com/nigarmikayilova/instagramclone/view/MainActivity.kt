package com.nigarmikayilova.instagramclone.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.nigarmikayilova.instagramclone.R
import com.nigarmikayilova.instagramclone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        binding=ActivityMainBinding.inflate(layoutInflater)
                        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }

        auth= Firebase.auth
        val currentUser=auth.currentUser
        if (currentUser!=null){
        val intent=Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


  fun signIn(view: View){
      val email=binding.editTextTextEmailAddress.text.toString()
       val password=binding.editTextTextPassword.text.toString()

      if (email.equals("")||password.equals("")){
          Toast.makeText(this,"Enter mail and password!",Toast.LENGTH_LONG).show()
      }else{
          auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
              val intent=Intent(this@MainActivity, FeedActivity::class.java)
              startActivity(intent)
              finish()
          }.addOnFailureListener{
            Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
          }

      }


  }

  fun signUp(view: View){
      val email=binding.editTextTextEmailAddress.text.toString()
      val password=binding.editTextTextPassword.text.toString()

// bunun meqsedi eger email v e ya password bosdursa toast cixarsin bos deyilse yeni user yaratsin
      if (email.equals("")||password.equals("")){
          Toast.makeText(this,"Enter email and password!",Toast.LENGTH_LONG).show()
      }else{
          //addOnSuccessListener-> qeydiyyat ugurlu olarsa intent ederek diger activitye kec
          auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
              //success
              val intent=Intent(this@MainActivity, FeedActivity::class.java)
              startActivity(intent)
              finish()


              //addOnFailureListener-> qeydiyyat ugursuz olarsa toast mesaji ver burada it.localized.Message-> xetani istifadeciye uygun gostermek
          }.addOnFailureListener{
              //fail
              Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
          }

      }




  }

}