package com.dicoding.retinova.ui.deteksi

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClassifierHelper {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    fun classifyImage(context: Context, bitmap: Bitmap, callback: (String) -> Unit) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                "image.jpg",
                RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageBytes)
            )
            .build()

        val request = Request.Builder()
            .url("http://ml-api.lifistudio.my.id/predict")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ApiClassifierHelper", "Request failed", e)
                callback("Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseCode = response.code
                    val responseMessage = response.message
                    val responseBody = response.body

                    if (!response.isSuccessful) {
                        callback("Error: $responseCode - $responseMessage")
                        return
                    }

                    val responseString = responseBody?.string() ?: "No response"
                    Log.d("ApiClassifierHelper", "Response: $responseString")

                    val jsonObject = JSONObject(responseString)
                    val predictionArray = jsonObject.getJSONArray("hasil_prediksi").getJSONArray(0)
                    var maxProbability = 0.0
                    var predictedIndex = -1

                    for (i in 0 until predictionArray.length()) {
                        val probability = predictionArray.getDouble(i)
                        if (probability > maxProbability) {
                            maxProbability = probability
                            predictedIndex = i
                        }
                    }

                    val labelMap = mapOf(
                        0 to "ARMD",
                        1 to "Cataract",
                        2 to "Diabetic Retinopathy",
                        3 to "Glaucoma",
                        4 to "Normal"
                    )

                    val predictedLabel = labelMap[predictedIndex] ?: "Unknown"
                    val formattedResult = "$predictedLabel (Probability: ${String.format("%.2f", maxProbability)})"

                    callback(formattedResult)
                } catch (e: Exception) {
                    Log.e("ApiClassifierHelper", "Response processing error", e)
                    callback("Error processing response: ${e.message}")
                } finally {
                    response.close()
                }
            }
        })
    }
}