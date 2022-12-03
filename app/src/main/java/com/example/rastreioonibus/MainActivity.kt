package com.example.rastreioonibus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.rastreioonibus.http.HttpRepository
import com.example.rastreioonibus.mapsInicio.MapsFragments
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val api: HttpRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scope.launch {
            autenticarCookie()
        }

        showMapsFragment()
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

    private fun showMapsFragment(){
        val fragment = MapsFragments.newInstance()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainLayout, fragment, MapsFragments.TAG_FRAGMENT_MAPS)
            .commit()
    }
}