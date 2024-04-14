package com.nigarmikayilova.instagramclone.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.nigarmikayilova.instagramclone.R
import com.nigarmikayilova.instagramclone.databinding.ActivityAddPostBinding
import java.util.UUID

class AddPostActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddPostBinding
    private lateinit var activityResultLauncher:ActivityResultLauncher<Intent>
    private lateinit var permissionResultLauncher:ActivityResultLauncher<String>
    private var selectedPicture :Uri?=null
    private lateinit var firestore:FirebaseFirestore
    private lateinit var storage:FirebaseStorage
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityAddPostBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        registerLauncher()
        auth= Firebase.auth
        firestore=Firebase.firestore
        storage=Firebase.storage
    }


// galeriden sekil cekmek ucun icaze->
    fun addPost(view: View){


        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_MEDIA_IMAGES)!=PackageManager.PERMISSION_GRANTED){
              if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_MEDIA_IMAGES)){
                  Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give permission"){
                        permissionResultLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                  }.show()

              }else{
                  //request permission
                  permissionResultLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
              }

            }else{
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                //start activity for result
                activityResultLauncher.launch(intentToGallery)
            }
        }else{
            //Android32- -> READ_EXTERNAL_STORAGE
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                //rational
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", View.OnClickListener {
                        permissionResultLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
                }else{
                    //request permission
                    permissionResultLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)



                }}else{
                val intentToGallery= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }
        }
   }


private fun registerLauncher(){

    activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if (result.resultCode== RESULT_OK){
                val intentFromResult=result.data
                if (intentFromResult!=null){
                   selectedPicture=intentFromResult.data
                    selectedPicture?.let {
                        binding.imageView.setImageURI(it)
                    }
                }
            }
        }

    permissionResultLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
        if (result){
            //permission granted
            val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)

        }else{
            //permission denied
            Toast.makeText(this@AddPostActivity,"Permission needed",Toast.LENGTH_LONG).show()
        }

    }
}

// upload buttonu
    fun upload(view: View){

    val intent=Intent(this@AddPostActivity, FeedActivity::class.java)
    startActivity(intent)

        //sekillere random olaraq id verir ve onlari save edir
        val uuid= UUID.randomUUID()

    val imageName="$uuid.jpg"
        val reference=storage.reference
        val imageReference=reference.child("images").child(imageName)
        if (selectedPicture!=null){
            imageReference.putFile(selectedPicture!!).addOnSuccessListener{

            //download url->firestore
                val uploadPictureReference= storage.reference.child("images").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl=it.toString()

                    println("test")

                    //butun tip deyisenleri elave etmek ucun hashMap istifade edilir


                    val postMap= hashMapOf<String,Any>()
                        postMap.put("downloadUrl", downloadUrl)
                        postMap["userEmail"] = auth.currentUser!!.email!!.toString()
                        postMap.put("comment", binding.comment.text.toString())
                        postMap.put("date", Timestamp.now())
                    println("test1")

                        firestore.collection("Posts").add(postMap).addOnSuccessListener {
                        println("test2")
                            finish()
                        }.addOnFailureListener{
                            Toast.makeText(this@AddPostActivity,it.localizedMessage, Toast.LENGTH_LONG).show()
                            println("test3")
                        }





                }

            }.addOnFailureListener{
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG ).show()
                println("test4")
            }



        }


    }



}