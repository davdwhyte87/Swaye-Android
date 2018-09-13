package com.example.davidwhyte.swaye

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONObject

class ConfirmCodeActivity : AppCompatActivity() {
    lateinit var codeField:EditText
    lateinit var code:String
    lateinit var btn_confirm:Button
    lateinit var loader_btn: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_code)
        handle_clicks()
    }

    fun handle_clicks(){
        btn_confirm=findViewById(R.id.btn_conf)
        btn_confirm.setOnClickListener {
            if(validate()){
                start_loader()
                send_post()
            }
        }
    }
    fun validate():Boolean{
        codeField=findViewById(R.id.f_code)
        code=codeField.text.toString()
        if(code.isEmpty()){
            Toast.makeText(this,"The code is required", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun start_loader(){
        loader_btn=findViewById(R.id.btn_load)
        loader_btn.visibility= View.VISIBLE
        Glide.with(this).load(R.drawable.load).into(loader_btn)
        btn_confirm.visibility= View.GONE
    }
    fun stop_loader(){
        loader_btn=findViewById(R.id.btn_load)
        loader_btn.visibility= View.GONE
        btn_confirm.visibility= View.VISIBLE
    }
    fun send_post(){
        val queue= Volley.newRequestQueue(this)
        var app_data=AppData()
        val url = app_data.api_url+"confirm"

// Formulate the request and handle the response.
        var req: JSONObject = JSONObject()
        req.put("code",code)
        val jsonObjectRequest: JsonObjectRequest = object: JsonObjectRequest(Request.Method.POST, url, req,
                Response.Listener { response ->
                    if(response["code"]==1){
                        var msg:String=response["message"].toString()
                        if(msg.isEmpty()){
                            msg="An error occured"
                        }
                        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
                        val intent= Intent(this,LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        stop_loader()
                        var msg:String=response["message"].toString()
                        if(msg.isEmpty()){
                            msg="An error occured"
                        }
                        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
                    }
                    Log.v("testserver",response["code"].toString())
                    Log.v("dumpresponse",response.toString())
                },
                Response.ErrorListener { error ->
                    Log.v("testserver",error.toString())
                    stop_loader()
                    Toast.makeText(this,"An error occurred",Toast.LENGTH_SHORT).show()
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
