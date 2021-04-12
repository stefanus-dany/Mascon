package com.example.mascon.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mascon.Model.ListofTopicModel
import com.example.mascon.Podcast.FragmentMakePodcast.SetPodcastNow
import com.example.mascon.R


class ListofTopicAdapter(val listData: MutableList<ListofTopicModel>) :
    RecyclerView.Adapter<ListofTopicAdapter.ViewHolder>() {
    lateinit var mContext : Context
    //buat ganti backgroun saat itemview.setonclicklistener
    var index = -1

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.list_of_topic, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val save = viewHolder.topicName
        save.text = listData[position].topic

        viewHolder.itemView.setOnClickListener {
            //menambahkan background warna saat itemview dipilih
            index=position;
            notifyDataSetChanged();
            SetPodcastNow.nameOfTopicText = save.text.toString()
//            Toast.makeText(viewHolder.itemView.context, save.text.toString(), Toast.LENGTH_SHORT).show()
        }
        if(index==position){
            viewHolder.linearLayoutListIfTopic.setBackgroundColor(Color.parseColor("#F4D995"));
        }
        else
        {
            viewHolder.linearLayoutListIfTopic.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val topicName: TextView = itemView.findViewById(R.id.topicName)
        val linearLayoutListIfTopic : LinearLayout = itemView.findViewById(R.id.linearLayoutListIfTopic)
    }

}