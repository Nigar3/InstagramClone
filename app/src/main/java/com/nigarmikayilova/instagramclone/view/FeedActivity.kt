package com.nigarmikayilova.instagramclone.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.nigarmikayilova.instagramclone.R
import com.nigarmikayilova.instagramclone.adapter.recylerAdapter
import com.nigarmikayilova.instagramclone.databinding.ActivityFeedBinding
import com.nigarmikayilova.instagramclone.model.Post

class FeedActivity : AppCompatActivity() {
    private lateinit var binding:ActivityFeedBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var db:FirebaseFirestore
   private lateinit var postArrayList:ArrayList<Post?>
   private lateinit var recylerAdapter: recylerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        //setSupportActionBar(binding.toolbar)

        binding=ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setSupportActionBar(binding.toolbar)


        auth= Firebase.auth
        db=Firebase.firestore
        postArrayList= ArrayList()
        getData()
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        recylerAdapter= recylerAdapter(postArrayList)
        binding.recyclerView.adapter=recylerAdapter
        println("test")
    }

    private fun getData(){
        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener{ value, error->
            if (error!=null){
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()

            }else{
                if(value!= null){
                    if (!value.isEmpty){
                       val documents= value.documents

                        postArrayList.clear()
                        for (document in documents){
                            //casting
                          val comment=document.get("comment") as String
                            val userEmail=document.get("userEmail") as String
                            val downloadUrl=document.get("downloadUrl") as String

                            println(comment)

                            val post=Post(userEmail,comment,downloadUrl)
                            postArrayList.add(post)
                        }

                    recylerAdapter.notifyDataSetChanged()
                    }

                }
            }
        }

    }




    fun homeClick (view: View){

    }

    fun addClick(view: View){

    }

    fun profileClick(view: View){

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater= menuInflater
        menuInflater.inflate(R.menu.insta_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId== R.id.add_post){
            val intent=Intent(applicationContext, AddPostActivity::class.java)
            startActivity(intent)
        }else if (item.itemId== R.id.sign_out){
            auth.signOut()
            val intent=Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()


        }

        return super.onOptionsItemSelected(item)
    }



}





