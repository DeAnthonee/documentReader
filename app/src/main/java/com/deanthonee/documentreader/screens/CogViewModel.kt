package com.deanthonee.documentreader.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deanthonee.documentreader.networking.MicrosoftServices
import kotlinx.coroutines.launch

class CogViewModel : ViewModel() {

    val api by lazy { MicrosoftServices.create() }

    init {

    }

    fun starthere() {
        val header = getHeader()
        val body = getBodyHeader()

        Log.d("herbo", "head is --- $header")
        Log.d("herbo", "Body is --- $body")
        viewModelScope.launch {
            val something = api.startReadOperation(header, body)
            Log.d("herbo", "We got ---------- $something")
        }
    }

    fun getHeader(): Map<String, Any> {
        val headerMap = mutableMapOf<String, Any>()
        headerMap["Content-Type"] = "application/json"
        headerMap["Ocp-Apim-Subscription-Key"] = MY_KEY
        return headerMap
    }

    fun getBodyHeader():Map<String, Any>{
        val headerMap  = mutableMapOf<String, Any>()
        headerMap["url"] = TEST_PIC_URL
        return headerMap
    }

    companion object {
        const val TEST_PIC_URL =
            "https://firebasestorage.googleapis.com/v0/b/document-reader-3b0f1.appspot.com/o/myuploads%2FuploadTest.jpeg?alt=media&token=7ffc6e20-f1fa-4e4f-bc01-d674cc8c1a85"
        const val BASE_URL = "https://westus.api.cognitive.microsoft.com/"
        const val MY_BASE_URL = "https://deanthoneeking.cognitiveservices.azure.com/"
        const val MY_KEY = "b04207f6f3f64fa1b3ecd3c538db9faf"
    }
}