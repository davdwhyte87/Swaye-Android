package com.example.davidwhyte.swaye.Adapters

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.davidwhyte.swaye.R
import com.example.davidwhyte.swaye.models.Menu
import kotlinx.android.synthetic.main.menu_item.view.*

class MenuAdapter(val items:ArrayList<Menu>,val context:Context):RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view=LayoutInflater.from(p0.context).inflate(R.layout.menu_item,p0,false) as ConstraintLayout
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.menu_name.text=items[position].name
        holder.view.menu_price.text=items[position].price.toString()
        holder.view.menu_qty.text=items[position].qty.toString()
        Glide.with(context).load(items[position].image).into(holder.view.menu_image)
    }

    class ViewHolder(val view:View):RecyclerView.ViewHolder(view){}
}