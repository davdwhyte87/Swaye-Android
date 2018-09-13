package com.example.davidwhyte.swaye

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.example.davidwhyte.swaye.Adapters.MenuAdapter
import com.example.davidwhyte.swaye.models.Menu
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent=Intent(this,RegisterActivity::class.java)
        startActivity(intent)
        displayMenu()
        getMenus()
    }


    fun displayMenu(){
        viewManager= LinearLayoutManager(this)
        val menuList=ArrayList<Menu>()
        var menu=Menu()
        menu.image="https://www.seriouseats.com/recipes/images/2016/01/20160206-fried-rice-food-lab-68-1500x1125.jpg"
        menu.name="Fried Rice"
        menu.price=1000
        menu.qty=39
        menuList.add(menu)
        viewAdapter=MenuAdapter(menuList,this)
        recyclerView=findViewById<RecyclerView>(R.id.menu_rv).apply {
            layoutManager=viewManager
            adapter=viewAdapter
        }
    }

    fun getMenus(){
        val queue=Volley.newRequestQueue(this)
        val url = "https://swaye.herokuapp.com/user/signin"

// Formulate the request and handle the response.
        var req:JSONObject= JSONObject()
        req.put("email","yeara@gmail.com")
        req.put("password","12345")
        val jsonObjectRequest:JsonObjectRequest = object:JsonObjectRequest(Request.Method.POST, url, req,
                Response.Listener { response ->
                    Log.v("testserver",response["code"].toString())
                },
                Response.ErrorListener { error ->
                    Log.v("testserver",error.toString())
                }
        ){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                var params= HashMap<String,String>(super.getHeaders())
                params.put("Content-Type","application/json")
                //..add other headers
                return params
            }
        }

// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
    }
}
