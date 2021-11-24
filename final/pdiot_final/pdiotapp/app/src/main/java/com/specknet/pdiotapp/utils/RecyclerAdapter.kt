package com.specknet.pdiotapp.utils

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.specknet.pdiotapp.HomeActivity
import com.specknet.pdiotapp.R
import com.specknet.pdiotapp.help.RecordingHelpScreen

class RecyclerAdapter :RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var titles = arrayOf("Recording Help", "Live Data Help", "Machine Learning Help", "Connecting Sensors Help", "Event Log")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        holder.itemDetail.text = titles[position]

        holder.itemDetail.setOnClickListener(View.OnClickListener { v ->
            val context: Context = v.context
            if (titles[position] == ("Recording Help")) {
                val intent = Intent(context, RecordingHelpScreen::class.java)
                context.startActivity(intent)
            } else {
                val intent = Intent(context, HomeActivity::class.java)
                context.startActivity(intent)
            }
        })

    }

    override fun getItemCount(): Int {
        return titles.size
    }

    inner class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        var itemDetail: TextView

        init{
            itemDetail = itemView.findViewById(R.id.item_detail)
            //itemView.setOnClickListener{
                //Toast.makeText(itemView.context, "insert more functions here", Toast.LENGTH_SHORT).show()
                //val intent = Intent(itemView.context, CustomizationActivity::class.java)
                //itemView.context.startActivity(intent)
        }
    }

}