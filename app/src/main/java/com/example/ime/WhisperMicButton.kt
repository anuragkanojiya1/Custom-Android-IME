package com.example.myime.ui

import android.util.Log
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ime.WhisperClient
import com.example.myime.audio.AudioRecorder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.launch

@Composable
fun WhisperMicButton(
    apiKey: String,
    onTranscriptionResult: (String) -> Unit
) {
    val context = LocalContext.current
    val recorder = remember { AudioRecorder(context) }
    val scope = rememberCoroutineScope()

    var recording by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition()

    val animatedRadius by infiniteTransition.animateFloat(
        initialValue = 120f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "radius"
    )

    val animatedOffset1 by infiniteTransition.animateValue(
        initialValue = Offset(180f, 360f),
        targetValue = Offset(360f, 180f),
        typeConverter = TwoWayConverter(
            convertToVector = { AnimationVector2D(it.x, it.y) },
            convertFromVector = { Offset(it.v1, it.v2) }
        ),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset1"
    )

    val animatedOffset2 by infiniteTransition.animateValue(
        initialValue = Offset(180f, 220f),
        targetValue = Offset(220f, 180f),
        typeConverter = TwoWayConverter(
            convertToVector = { AnimationVector2D(it.x, it.y) },
            convertFromVector = { Offset(it.v1, it.v2) }
        ),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset2"
    )

    Column {
        Box(
            modifier = Modifier
                .size(148.dp)
                .align(Alignment.CenterHorizontally)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF8A2BE2), Color.Transparent),
                        center = animatedOffset2,
                        radius = animatedRadius,
                        tileMode = TileMode.Clamp
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(
                onClick = {

                },
                modifier = Modifier.size(120.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Speak",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    recording = true
                                    val audioFile = recorder.startRecording()

                                    val released = tryAwaitRelease()

                                    val stoppedFile = recorder.stopRecording()
                                    recording = false

                                    stoppedFile?.let { file ->
                                        loading = true
                                        scope.launch {
                                            try {
                                                val filePart = MultipartBody.Part.createFormData(
                                                    "file",
                                                    file.name,
                                                    file.asRequestBody("audio/wav".toMediaTypeOrNull())
                                                )
                                                val modelBody =
                                                    "whisper-large-v3-turbo".toRequestBody("text/plain".toMediaTypeOrNull())
                                                val response = WhisperClient.api.transcribe(
                                                    authorization = "Bearer $apiKey",
                                                    file = filePart,
                                                    model = modelBody,
                                                    language = "en".toRequestBody("text/plain".toMediaTypeOrNull()),
                                                    responseFormat = "verbose_json".toRequestBody("text/plain".toMediaTypeOrNull()),
                                                    timestampGranularities = "word".toRequestBody("text/plain".toMediaTypeOrNull()),
                                                    temperature = "0".toRequestBody("text/plain".toMediaTypeOrNull())
                                                )

                                                val raw = response.errorBody()?.string() ?: response.body()?.toString()
                                                Log.d("GroqAPI", "Raw = $raw")

                                                if (response.isSuccessful) {
                                                    val body = response.body()
                                                    onTranscriptionResult(body?.text ?: "")
                                                    Log.d("GroqMicButton", response.toString())
                                                } else {
                                                    val raw = response.errorBody()?.string() ?: response.body()?.toString()
                                                    Log.d("GroqAPI", "Raw = $raw")

                                                    val err = response.errorBody()?.string()
                                                    Log.e(
                                                        "GroqMicButton",
                                                        "Error transcription: $err"
                                                    )
                                                    onTranscriptionResult("Error: $err")
                                                }
                                            } catch (e: Exception) {
                                                Log.e(
                                                    "GroqMicButton",
                                                    "Exception: ${e.localizedMessage}"
                                                )
                                                onTranscriptionResult("Exception: ${e.localizedMessage}")
                                            } finally {
                                                loading = false
                                            }
                                        }
                                    }
                                }
                            )
                        }
                )
            }
        }
    }

//    Icon(
//        imageVector = Icons.Default.Mic,
//        contentDescription = "Hold to speak",
//        modifier = Modifier
//            .size(80.dp)
//            .pointerInput(Unit) {
//                detectTapGestures(
//                    onPress = {
//                        // Begin
//                        recording = true
//                        val audioFile = recorder.startRecording()
//
//                        val released = tryAwaitRelease()  // waits until release
//
//                        // Stop and send
//                        val stoppedFile = recorder.stopRecording()
//                        recording = false
//
//                        stoppedFile?.let { file ->
//                            loading = true
//                            scope.launch {
//                                try {
//                                    val filePart = MultipartBody.Part.createFormData(
//                                        "file",
//                                        file.name,
//                                        file.asRequestBody("audio/wav".toMediaTypeOrNull())
//                                    )
//                                    val modelBody = "whisper-large-v3".toRequestBody("text/plain".toMediaTypeOrNull())
//                                    // optional: language, prompt, etc.
//                                    val response = WhisperClient.api.transcribe(
//                                        authorization = "Bearer $apiKey",
//                                        file = filePart,
//                                        model = modelBody,
//                                        language = "en".toRequestBody("text/plain".toMediaTypeOrNull()),
//                                        responseFormat = "text".toRequestBody("text/plain".toMediaTypeOrNull())
//                                    )
//                                    if (response.isSuccessful) {
//                                        val body = response.body()
//                                        onTranscriptionResult(body?.text ?: "")
//                                    } else {
//                                        val err = response.errorBody()?.string()
//                                        Log.e("GroqMicButton", "Error transcription: $err")
//                                        onTranscriptionResult("Error: $err")
//                                    }
//                                } catch (e: Exception) {
//                                    Log.e("GroqMicButton", "Exception: ${e.localizedMessage}")
//                                    onTranscriptionResult("Exception: ${e.localizedMessage}")
//                                } finally {
//                                    loading = false
//                                }
//                            }
//                        }
//                    }
//                )
//            }
//    )

    if (loading) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp))
    }
}
