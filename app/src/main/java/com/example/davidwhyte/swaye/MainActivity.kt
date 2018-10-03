package com.example.davidwhyte.swaye

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
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
import com.example.davidwhyte.swaye.Contracts.UserContract
import com.example.davidwhyte.swaye.models.Menu
import org.json.JSONObject
import android.R.menu
import android.view.MenuInflater
import android.view.MenuItem
import org.json.JSONArray


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    var uid:String=""
    var token:String=""
    var menus=ArrayList<Menu>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        displayMenu()
        getMenus()
    }

    override fun onStart() {
        super.onStart()
        Log.v("start","heyy me works")
        val dbhelper=UserContract.UserEntry.SwayeDbHelper(this)
        val db=dbhelper.writableDatabase
        val selection="${BaseColumns._ID} =?"
        val selectionArgs= arrayOf(1)
        val projections= arrayOf(BaseColumns._ID,UserContract.UserEntry.COLUMN_NAME_UID,UserContract.UserEntry.COLUMN_NAME_NAME,UserContract.UserEntry.COLUMN_NAME_TOKEN)
        val cursor=db.query(UserContract.UserEntry.TABLE_NAME,projections,null,null,null,null,null)


        with(cursor) {
            while (moveToNext()) {
                uid = getString(getColumnIndex(UserContract.UserEntry.COLUMN_NAME_UID))
                token=getString(getColumnIndex(UserContract.UserEntry.COLUMN_NAME_TOKEN))
            }
        }
        if(uid.isEmpty()){
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
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
        val appData=AppData()
        val url = appData.api_urlo+"menu"

// Formulate the request and handle the response.

        val jsonObjectRequest:JsonObjectRequest = object:JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    Log.v("testserver",response.toString())
                    val obj:JSONArray=response.getJSONArray("data")
                    var count=obj.length()
                    while (count>0){
                       val menu_obj:JSONObject= obj.getJSONObject(count-1)
                        val menu=Menu()
                        menu.image=menu_obj.getString("image")
                        menu.name=menu_obj.getString("name")
                        menu.qty=menu_obj.getString("qty").toInt()
                        menu.price=menu_obj.getString("price").toInt()
                        menus.add(menu)
                        count--
                    }
                },
                Response.ErrorListener { error ->
                    Log.v("testserver",error.toString())
                }
        ){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                var params= HashMap<String,String>(super.getHeaders())
                params.put("Content-Type","application/json")
                params.put("token",token)
                //..add other headers
                return params
            }
        }

// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.menu_logout->{
                logout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun logout(){
        val dbHelper=UserContract.UserEntry.SwayeDbHelper(this)
        val db=dbHelper.writableDatabase
        val where="${UserContract.UserEntry.COLUMN_NAME_UID} ='${uid}'"
        db.delete(UserContract.UserEntry.TABLE_NAME,where,null)
        val intent=Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}
