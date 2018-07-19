package com.example.android.fitit

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.fitit.misc.*
import com.github.thunder413.datetimeutils.DateTimeUtils
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.test_daily_counter.view.*

class FitnessDataAdapter(private var anotherFitnessData: ArrayList<JsonObject>) : RecyclerView.Adapter<FitnessViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): FitnessViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.test_daily_counter, viewGroup, false)
        return FitnessViewHolder(view)
    }

    override fun getItemCount() = anotherFitnessData.size

    override fun onBindViewHolder(holder: FitnessViewHolder, position: Int) {

        val fitnessItems = anotherFitnessData[position]


//        holder.itemView.count_number_txt.text = "Counting #${position + 1}"

        var heart_rate = fitnessItems.get(average_heartrate).toString()
        heart_rate = heart_rate.replace("\"", "")
        holder.itemView.card_heartrate.text = heart_rate

        var calories = fitnessItems.get(calories).toString()
        calories = calories.replace("\"", "")
        holder.itemView.card_calories.text = calories

        var steps = fitnessItems.get(steps_number).toString()
        steps = steps.replace("\"", "")
        holder.itemView.card_steps.text = steps

        var typeOfWorkout = fitnessItems.get(activity_name).toString()
        typeOfWorkout = typeOfWorkout.replace("\"", "")
        holder.itemView.card_type.text = typeOfWorkout

//        if(!created_date.isEmpty()) {
////            val date = DateTimeUtils.formatWithPattern(created_date, "MMMM dd")
//            val date = DateTimeUtils.formatDate(created_date)
//            holder.itemView.row_date.text = date.toString()
//        } else {
//            holder.itemView.row_date.text = "Empty"
//        }

//        val date = DateTimeUtils.formatDate(created_date)
//        if (date != null) holder?.itemView?.row_date?.text = date.toString()
//        else holder?.itemView?.row_date?.text = "Empty"

//        val styledDate = DateTimeUtils.formatWithPattern(date, "MMMM dd")
//        holder.itemView.row_date.text = date.toString()

//        var activity = fitnessItems.get(created_date).toString()
//        activity = activity.replace("\"", "")
//
        var createdDate = fitnessItems.get(created_date).toString()
        createdDate = createdDate.replace("\"", "")
        val newDate = createdDate.toLong()
        val anotherDate = DateTimeUtils.formatDate(newDate)
//        val date = DateTimeUtils.formatWithPattern(anotherDate, "MMMM dd, yyyy HH:mmm:ss")
        val date = DateTimeUtils.formatWithPattern(anotherDate, "MMM dd, yy HH:mm a")
//        Log.d("Date: ", date!!.toString())

        if (!createdDate.isEmpty()) holder.itemView.card_date?.text = date
        else holder.itemView.card_date?.text = "Null"
    }
}

class FitnessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
