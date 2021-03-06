package com.example.davidwhyte.swaye

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
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
import com.example.davidwhyte.swaye.Contracts.UserContract

import kotlinx.android.synthetic.main.activity_forgot_pass.*
import org.json.JSONObject

class ForgotPassActivity : AppCompatActivity() {

    lateinit var btn_send_code:Button
    lateinit var email_field:EditText
    lateinit var email:String
    lateinit var btn_load:ImageButton
    lateinit var loader_btn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)

    }

    fun handler(){
        btn_send_code=findViewById(R.id.btn_send_code)
        btn_send_code.setOnClickListener {
            //validate
            if(validate()){
                //inflate loader
                start_loader()
            }
        }
    }

    fun send_post(){
        val queue= Volley.newRequestQueue(this)
        var app_data=AppData()
        val url = app_data.api_url+"forgot_pass"

// Formulate the request and handle the response.
        var req: JSONObject = JSONObject()
        req.put("email",email)
        val jsonObjectRequest: JsonObjectRequest = object: JsonObjectRequest(Request.Method.POST, url, req,
                Response.Listener { response ->
                    if(response["code"]==1){
                        Log.v("dumpresponse",response.toString())
                        val intent= Intent(this,FChangePass::class.java)
                        startActivity(intent)
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

    fun start_loader(){
        loader_btn=findViewById(R.id.btn_load)
        loader_btn.visibility= View.VISIBLE
        Glide.with(this).asGif().load(R.drawable.load).into(loader_btn)
        btn_send_code.visibility= View.GONE
    }
    fun stop_loader(){
        loader_btn=findViewById(R.id.btn_load)
        loader_btn.visibility= View.GONE
        btn_send_code.visibility= View.VISIBLE
    }

    fun validate():Boolean{
        email_field=findViewById(R.id.f_email)
        email=email_field.text.toString()
        if(email.isEmpty()){
            Toast.makeText(this,"Your code is required",Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

}
