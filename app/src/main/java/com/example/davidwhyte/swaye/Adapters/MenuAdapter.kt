package com.example.davidwhyte.swaye.Adapters

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.bumptech.glide.Glide
import com.example.davidwhyte.swaye.R
import com.example.davidwhyte.swaye.models.Menu
import kotlinx.android.synthetic.main.menu_item.view.*


class MenuAdapter(val items:ArrayList<Menu>,val context:Context):RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
    lateinit var cart_btn:ImageButton
    lateinit var cart:ArrayList<String>
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view=LayoutInflater.from(p0.context).inflate(R.layout.menu_item,p0,false) as CardView
        cart= ArrayList()
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.menu_name.text=items[position].name
        holder.view.menu_price.text=items[position].price.toString()+"#"
        holder.view.menu_qty.text="Qty:"+items[position].qty.toString()
        Glide.with(context).load(items[position].image).into(holder.view.menu_image)
        cart_btn=holder.view.findViewById(R.id.cart_btn)
        cart_btn.setImageResource(R.drawable.add)
        cart_btn.setOnClickListener {
            if(items[position].state==0){
                Log.v("cart_btnclicked","clicked")
                Glide.with(context).load(R.drawable.remove_cart).into(holder.view.cart_btn)
                cart.add(items[position].id)
                items[position].state=1

            }
            else{
                Log.v("cart_btnclicked","un-clicked")
                Glide.with(context).load(R.drawable.add).into(holder.view.cart_btn)
                cart.remove(items[position].id)
                items[position].state=0
            }
        }
    }

    class ViewHolder(val view:View):RecyclerView.ViewHolder(view){}
}