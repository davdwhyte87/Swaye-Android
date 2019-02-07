package com.example.davidwhyte.swaye

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.android.volley.toolbox.*
import com.example.davidwhyte.swaye.Adapters.MenuAdapter
import com.example.davidwhyte.swaye.Contracts.UserContract
import com.example.davidwhyte.swaye.models.Menu
import org.json.JSONObject
import android.R.menu
import android.os.AsyncTask
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.android.volley.*
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.w3c.dom.Text


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    var uid:String=""
    var token:String=""
    var menus=ArrayList<Menu>()
    lateinit var loader:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getMenus()
        displayMenu()
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
        viewAdapter=MenuAdapter(menus,this)
        recyclerView=findViewById<RecyclerView>(R.id.menu_rv).apply {
            layoutManager=viewManager
            adapter=viewAdapter
        }
    }

    fun getMenus(){
        var textvnetwork_err=findViewById<LinearLayout>(R.id.network_err)
        textvnetwork_err.visibility=View.GONE
        start_loader()
        val queue=Volley.newRequestQueue(this)
        val appData=AppData()
        val url = appData.api_urlo+"menu"

// Formulate the request and handle the response.

        val jsonObjectRequest:JsonObjectRequest = object:JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    if(response["code"]==1){
                        Log.v("testserver",response.toString())
                        val obj:JSONArray=response.getJSONArray("data")
                        var count=obj.length()
                        while (count>0){
                            val menu_obj:JSONObject= obj.getJSONObject(count-1)
                            val menu=Menu()
                            menu.image=menu_obj.getString("image")
                            menu.name=menu_obj.getString("name")
                            menu.id=menu_obj.getString("_id")
                            menu.qty=menu_obj.getString("qty").toInt()
                            menu.price=menu_obj.getString("price").toInt()
                            menus.add(menu)
                            count--
                        }
                        Log.v("testserver",menus.toString())
                        stop_loader()
                        displayMenu()
                    }else{
                        logout()
                    }

                },
                Response.ErrorListener { error ->
                    stop_loader()
                    var textvnetwork_err=findViewById<LinearLayout>(R.id.network_err)
                    textvnetwork_err.visibility=View.VISIBLE
                    var retry_txt=findViewById<TextView>(R.id.retry)
                    retry_txt.setOnClickListener {
                        getMenus()
                    }
                    Log.v("testserver",error.toString())
                }
        ){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                var params= HashMap<String,String>(super.getHeaders())
                params.put("Content-Type","application/json")
                params.put("token",token)
                Log.v("reqtoken",token)
                //..add other headers
                return params
            }
        }

        jsonObjectRequest.setRetryPolicy(DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
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
    fun start_loader(){
        loader=findViewById(R.id.loader)
        loader.visibility= View.VISIBLE
        Glide.with(this).asGif().load(R.drawable.load).into(loader)

    }

    fun stop_loader(){
        loader=findViewById(R.id.loader)
        loader.visibility= View.GONE
    }
}
