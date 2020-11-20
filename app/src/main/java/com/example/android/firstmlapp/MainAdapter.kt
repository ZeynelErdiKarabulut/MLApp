package com.example.android.firstmlapp

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.ml.vision.label.FirebaseVisionLabel


class MainAdapter(val context: Context, val firebaseVisionLabels: List<FirebaseVisionLabel>): RecyclerView.Adapter<MainAdapter.MyViewHolder>(){


    class MyViewHolder(var view: View): RecyclerView.ViewHolder(view) {

        var textTextView: TextView? = null
        var confidenceTextView: TextView? = null


init{
    textTextView = view.findViewById(R.id.text)
    confidenceTextView = view.findViewById(R.id.confidence)
}


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view=LayoutInflater.from(context).inflate(R.layout.list_items, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var firebaseVisionLabel:FirebaseVisionLabel=firebaseVisionLabels.get(position)
        val label=firebaseVisionLabel.label
        val confidence=firebaseVisionLabel.confidence
        holder.textTextView!!.text=label
        holder.confidenceTextView!!.text=confidence.toString()
    }

    override fun getItemCount(): Int {
        if(firebaseVisionLabels.isEmpty())
            return 0;
        else
          return  firebaseVisionLabels.size

    }

}