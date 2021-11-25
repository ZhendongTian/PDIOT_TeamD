package com.specknet.pdiotapp.help

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.specknet.pdiotapp.R
import com.specknet.pdiotapp.utils.HelpObject
import com.specknet.pdiotapp.utils.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_help.*

class HelpActivity : AppCompatActivity() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        val helpList = mutableListOf<HelpObject>()

        helpList.add(HelpObject())
        helpList.add(HelpObject())
        helpList.add(HelpObject())
        helpList.add(HelpObject())
        helpList.add(HelpObject())

        for (item in helpList){
            item.setExpanded(false)
        }

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = RecyclerAdapter(helpList)
        recyclerView.adapter = adapter

    }
}