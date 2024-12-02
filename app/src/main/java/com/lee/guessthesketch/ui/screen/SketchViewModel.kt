package com.lee.guessthesketch.ui.screen

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Path
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


data class ColoredPath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float,
)

class SketchViewModel : ViewModel() {
    val paths = mutableStateListOf<ColoredPath>()
    val strokeColor = mutableStateOf(Color.Black)
    val sliderValue = mutableStateOf(0.70f)
    val strokeWidth = mutableStateOf(5f)
    val buttonSelected = mutableStateOf(0)
    val curBase64Image = mutableStateOf("")
    var chatResponse = mutableStateOf("")
    var lastGuess = mutableStateOf("")
    var time = mutableStateOf(0)
    val word = mutableStateOf("")
    var apiLaunched = mutableStateOf(false)
    var showDialog = mutableStateOf(false)
    var winState = mutableStateOf(true)
    var gameStart = mutableStateOf(false)
    var toastMessage = mutableStateOf("")
    var firstGame = mutableStateOf(true)
    var isLoading = mutableStateOf(false)


    fun addPath(path: ColoredPath) {
        paths.add(path)
    }

    fun updateColor(newColor: Color) {
        strokeColor.value = newColor
    }

    fun updateSliderValue(newValue: Float) {
        sliderValue.value = newValue
    }

    fun updateStrokeWidth(newValue: Float) {
        strokeWidth.value = newValue
    }
    fun updateButtonSelected(newValue: Int) {
        buttonSelected.value = newValue
    }
    fun updateBase64Image(newValue: String) {
        curBase64Image.value = newValue
    }

    suspend fun chatCompletionWithBase64Image(base64Image: String): String {
        val url = "https://api.openai.com/v1/chat/completions"

        // JSON payload
        val payload = mapOf(
            "model" to "gpt-4o-mini",
            "messages" to listOf(
                mapOf(
                    "role" to "user",
                    "content" to listOf(
                        mapOf("type" to "text", "text" to "What is the object illustrated in this sketch? list out your top five guesses (guesses only). Example Response : 1.apple 2.orange 3.circle 4.wheel 5.cup"),
                        mapOf(
                            "type" to "image_url",
                            "image_url" to mapOf("url" to "data:image/jpeg;base64,$base64Image")
                        )
                    )
                )
            )
        )

        val gson = Gson()
        val jsonPayload = gson.toJson(payload)

        // Create HTTP client
        val client = OkHttpClient()

        // HTTP request
        val request = Request.Builder()
            .url(url)
            .post(jsonPayload.toRequestBody("application/json".toMediaTypeOrNull()))
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        withContext(Dispatchers.IO) { // Ensure network call is off the main thread
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: "No response"
                    val chatContent = extractChatResponse(responseBody)
                    chatResponse.value = chatContent ?: "No content found"
                    lastGuess.value = chatContent ?: "No content found"
                } else {
                    "Error: ${response.code} - ${response.message}"
                }
            } catch (e: Exception) {
                Log.e("OpenAI Error", "Exception during API call: ${e.message}", e)
                "Exception: ${e.message}"
            }
        }
        return chatResponse.value;
    }

    fun updateChatResponse(newResponse: String) {
        chatResponse.value = newResponse
    }

    fun extractChatResponse(jsonResponse: String): String? {
        // Parse the JSON response
        val jsonElement = JsonParser.parseString(jsonResponse)
        val jsonObject = jsonElement.asJsonObject

        // Navigate to the desired field
        val choices = jsonObject.getAsJsonArray("choices")
        if (choices.size() > 0) {
            val firstChoice = choices[0].asJsonObject
            val message = firstChoice.getAsJsonObject("message")
            return message.get("content").asString
        }
        return null // Handle cases where data is missing
    }



    suspend fun chatCompletion(prompt: String): String {
        val url = "https://api.openai.com/v1/chat/completions"

        // JSON payload
        val payload = mapOf(
            "model" to "gpt-4o-mini",
            "messages" to listOf(
                mapOf(
                    "role" to "user",
                    "content" to listOf(
                        mapOf("type" to "text", "text" to "$prompt"),
                    )
                )
            )
        )

        val gson = Gson()
        val jsonPayload = gson.toJson(payload)

        // Create HTTP client
        val client = OkHttpClient()

        // HTTP request
        val request = Request.Builder()
            .url(url)
            .post(jsonPayload.toRequestBody("application/json".toMediaTypeOrNull()))
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        var result = "No Repsonse"
        withContext(Dispatchers.IO) { // Ensure network call is off the main thread
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: "No response"
                    val chatContent = extractChatResponse(responseBody)
                    result = chatContent ?: "No content found"
                } else {
                    "Error: ${response.code} - ${response.message}"
                }
            } catch (e: Exception) {
                Log.e("OpenAI Error", "Exception during API call: ${e.message}", e)
                "Exception: ${e.message}"
            }
        }
        return result;
    }

    suspend fun checkWin(word: String) {
        var res = chatCompletion("The player is given a word to sketch and the word the player needs to draw is $word. The following is a list of guesses from another player, does the list contain the word? List: $lastGuess. (Answer yes or no only, don't be too strict)")
        res = res.lowercase()
        if (res.contains("yes")){
            winState.value = true
        }
        else{
            winState.value = false
        }
    }

    fun deleteAllPath(){
        paths.clear()
    }

    fun generateNewWord(difficulty: Int){
        word.value = StringUtils.getRandomString(difficulty)
    }

    fun start(difficulty: Int){
        firstGame.value = false;
        if (difficulty == 1){
            time.value = 30
        }
        else if (difficulty == 2){
            time.value = 60
        }
        else {
            time.value = 100
        }
        deleteAllPath()
        gameStart.value = true;
        generateNewWord(difficulty)
        apiLaunched.value = false;
        viewModelScope.launch{ while (time.value > 0){
                time.value -= 1
                delay(1000)
            }
        }
    }

    fun saveBitmapToLocal(bitmap: Bitmap, context: Context, filename: String = "image_${System.currentTimeMillis()}.png"): Boolean {
        val outputStream: OutputStream?
        toastMessage.value = "Error Encountered"
        try {
            // For Android 10 (API 29) and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val contentResolver = context.contentResolver
                val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: return false // Return false if the Uri couldn't be created
                outputStream = contentResolver.openOutputStream(imageUri)
            } else {
                // For older versions of Android
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                if (!imagesDir.exists()) imagesDir.mkdir()
                val imageFile = File(imagesDir, filename)
                outputStream = FileOutputStream(imageFile)
            }
            // Save the bitmap to the output stream
            if (outputStream == null){
                return false
            }
            else{
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream?.close()
                toastMessage.value = "Image Saved"
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

}

