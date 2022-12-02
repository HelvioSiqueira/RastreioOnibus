package com.example.rastreioonibus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.rastreioonibus.http.HttpUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private val api: HttpUtils by inject()

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scope.launch {
            autenticarCookie()
            Log.d("HSV", api.getPrevChegadas()!!.p.toString())
        }
    }

    private suspend fun autenticarCookie(){
        try{
            val certificado = api.autenticar(applicationContext).headers()["Set-Cookie"]

            if(certificado != null){
                api.setCertificado(certificado)
            } else {
                Log.d("HSV", "Cookie nulo")
            }

        } catch (e: Exception){
            e.printStackTrace()
        }
    }
}