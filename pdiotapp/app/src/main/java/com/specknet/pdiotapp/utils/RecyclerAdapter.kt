package com.specknet.pdiotapp.utils

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.specknet.pdiotapp.R
import android.widget.LinearLayout
import com.specknet.pdiotapp.Animations.Animations


class RecyclerAdapter (hl: MutableList<HelpObject>) :RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var titles = arrayOf("Recording Help", "Live Data Help", "Machine Learning Help", "Connecting Sensors Help", "Calibrate Help")
    private var images = arrayOf(R.drawable.button1, R.drawable.button2, R.drawable.button3, R.drawable.button4, R.drawable.calibrate)
    private var desc = arrayOf(R.string.recording_help_message, R.string.live_help_message,
        R.string.ml_help_message, R.string.bluetooth_help_message, R.string.calibrate_help_message)

    private var helpList = hl

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_view2, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        holder.activityName.text = titles[position]
        holder.icon.setImageResource(images[position])
        holder.description.setText(desc[position])

        holder.expandBtn.setOnClickListener(View.OnClickListener { v ->
            val context: Context = v.context
            val bool = (helpList[position].isExpanded())
            val show = toggleLayout(!bool, v)
            helpList[position].isExpanded = show
            if (show){
                holder.expand.visibility = View.VISIBLE
            }
            else{
                holder.expand.visibility = View.GONE
                holder.resetExpand()

            }

            //val intent = Intent(context, HomeActivity::class.java)
            //context.startActivity(intent)
        })

    }

    override fun getItemCount(): Int {
        return titles.size
    }

    inner class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        var activityName: TextView
        var expandBtn : ImageView
        var icon : ImageView
        var expand : LinearLayout
        var description: TextView

        init{
            activityName = itemView.findViewById(R.id.activityName)
            expandBtn = itemView.findViewById(R.id.viewMoreBtn)
            icon = itemView.findViewById(R.id.image)
            expand = itemView.findViewById(R.id.layoutExpand)
            description = itemView.findViewById(R.id.descText)

            //itemView.setOnClickListener{
                //Toast.makeText(itemView.context, "insert more functions here", Toast.LENGTH_SHORT).show()
                //val intent = Intent(itemView.context, CustomizationActivity::class.java)
                //itemView.context.startActivity(intent)
        }

        fun resetExpand(){
            expand = itemView.findViewById(R.id.layoutExpand)
        }
    }

    private fun toggleLayout(isExpanded: Boolean, v: View): Boolean {
        Animations.toggleArrow(v, isExpanded);
        /*if (isExpanded) {
            Animations.expand(v);
        } else {
            Animations.collapse(v);
        }*/
        Animations.toggleArrow(v, isExpanded)
        return isExpanded
    }

}