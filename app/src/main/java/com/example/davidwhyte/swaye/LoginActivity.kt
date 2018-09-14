package com.example.davidwhyte.swaye

import android.content.ContentValues
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.davidwhyte.swaye.Contracts.UserContract
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var nameField: EditText
    lateinit var phoneField: EditText
    lateinit var passField: EditText
    lateinit var name:String
    lateinit var phone:String
    lateinit var pass:String

    lateinit var login_btn: Button
    lateinit var loader_btn: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        handlers()
    }
    fun handlers(){
        login_btn=findViewById(R.id.btn_login)
        login_btn.setOnClickListener {
            if(validate()){
                start_loader()
                send_post()
            }
        }

        val txtRegister=findViewById<TextView>(R.id.txt_signup)
        txtRegister.setOnClickListener {
            val intent= Intent(this,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        val txtFpass=findViewById<TextView>(R.id.txt_fpass)
        txtFpass.setOnClickListener {
            val intent= Intent(this,ForgotPassActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun start_loader(){
        loader_btn=findViewById(R.id.btn_load)
        loader_btn.visibility= View.VISIBLE
        Glide.with(this).asGif().load(R.drawable.load).into(loader_btn)
        login_btn.visibility= View.GONE
    }
    fun stop_loader(){
        loader_btn=findViewById(R.id.btn_load)
        loader_btn.visibility= View.GONE
        login_btn.visibility= View.VISIBLE
    }
    fun send_post(){
        val queue= Volley.newRequestQueue(this)
        var app_data=AppData()
        val url = app_data.api_url+"signin"

// Formulate the request and handle the response.
        var req: JSONObject = JSONObject()
        req.put("phone",phone)
        req.put("password",pass)
        val jsonObjectRequest: JsonObjectRequest = object: JsonObjectRequest(Request.Method.POST, url, req,
                Response.Listener { response ->
                    if(response["code"]==1){
                        Log.v("dumpresponse",response.toString())
//                        val intent= Intent(this,ConfirmCodeActivity::class.java)
//                        startActivity(intent)
                        //create a user in local db
                        val db=UserContract.RecordEntry.SwayeDbHelper(this)
                        val data=ContentValues().apply {
                            put(UserContract.UserEntry.COLUMN_NAME_NAME=)
                        }
                        finish()
                    }
                    else{
                        stop_loader()
                        var msg:String=response["message"].toString()
                        if(msg.isEmpty()){
                            msg="An error occured"
                        }
                        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
                    }
                    Log.v("testserver",response["code"].toString())
                },
                Response.ErrorListener { error ->
                    Log.v("testserver",error.toString())
                    stop_loader()
                    Toast.makeText(this,"An error occurred", Toast.LENGTH_SHORT).show()
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
    fun validate():Boolean{
        phoneField=findViewById(R.id.f_phone)
        phone=phoneField.text.toString()
        passField=findViewById(R.id.f_pass)
        pass=passField.text.toString()
        Log.v("regdata",pass+phone)

        if(phone.isEmpty()||pass.isEmpty()){
            Toast.makeText(this,"Your phone and password is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if(phone.length<8){
            Toast.makeText(this,"Phone must not be less that 8 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        if(pass.length<5){
            Toast.makeText(this,"Password must not be less that 5 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
