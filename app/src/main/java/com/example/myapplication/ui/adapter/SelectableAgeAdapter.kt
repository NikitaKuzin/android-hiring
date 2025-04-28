package com.example.myapplication.ui.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.data.model.AgeViewContainer

class SelectableAgeAdapter(context: Context, items: List<AgeViewContainer>) :
    ArrayAdapter<AgeViewContainer>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val viewHolder: ViewHolder
        var currentView: View? = convertView

        if (currentView == null) {
            currentView = LayoutInflater.from(context)
                .inflate(R.layout.checkable_textview_item, parent, false)
            viewHolder = ViewHolder(currentView!!)
            currentView.tag = viewHolder
        } else {
            viewHolder = currentView.tag as ViewHolder
        }

        viewHolder.bind(getItem(position))

        return currentView
    }

    private inner class ViewHolder(
        view: View
    ) {

        val textviewAge: TextView = view.findViewById(R.id.textview_age)
        val imageviewCheckmark: ImageView = view.findViewById(R.id.imageview_checkmark)

        fun bind(age: AgeViewContainer?) {
            if (age == null) return
            textviewAge.text = age.age.toString()
            if (age.isSelected) {
                imageviewCheckmark.visibility =  View.VISIBLE
                textviewAge.setTypeface(textviewAge.typeface, Typeface.BOLD_ITALIC)
            } else {
                imageviewCheckmark.visibility =  View.GONE
                textviewAge.setTypeface(textviewAge.typeface, Typeface.ITALIC)
            }
        }
    }

}