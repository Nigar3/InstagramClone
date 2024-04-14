package com.nigarmikayilova.instagramclone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nigarmikayilova.instagramclone.databinding.RecyclerRawBinding
import com.nigarmikayilova.instagramclone.model.Post
import com.squareup.picasso.Picasso

class recylerAdapter(val postList: ArrayList<Post?>) : RecyclerView.Adapter<recylerAdapter.PostHolder>() {
    class PostHolder(val binding: RecyclerRawBinding):RecyclerView.ViewHolder(binding.root){



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding=RecyclerRawBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.recylerEmail.text= postList.get(position)?.email
        holder.binding.recyclerComment.text= postList.get(position)?.comment
        Picasso.get().load(postList.get(position)?.downloadUrl).into(holder.binding.recylerViewImage)
    }

}