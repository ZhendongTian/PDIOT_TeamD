package com.specknet.pdiotapp.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.specknet.pdiotapp.R

class RecyclerAdapter :RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var titles = arrayOf("Recording Help", "Live Data Help", "Machine Learning Help", "Connecting Sensors Help")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        holder.itemDetail.text = titles[position]
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    inner class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        var itemDetail: TextView

        init{
            itemDetail = itemView.findViewById(R.id.item_detail)
            itemView.setOnClickListener{
                Toast.makeText(itemView.context, "insert more functions here", Toast.LENGTH_SHORT).show()
                
            }
        }
    }

}