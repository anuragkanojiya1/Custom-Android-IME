package com.example.ime

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import com.example.myime.ui.WhisperMicButton

@SuppressLint("SuspiciousIndentation")
@Composable
fun MyKeyboardUI(onKeyPress: (String) -> Unit) {

            WhisperMicButton(apiKey = BuildConfig.GROQ_API_KEY) { transcription ->
                onKeyPress(transcription)
            }





//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
//            listOf("Q","W","E","R","T","Y","U","I","O","P").forEach { KeyButton(it, onKeyPress) }
//        }
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
//            listOf("A","S","D","F","G","H","J","K","L").forEach { KeyButton(it, onKeyPress) }
//        }
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
//            listOf("Z","X","C","V","B","N","M").forEach { KeyButton(it, onKeyPress) }
//        }
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
//            KeyButton("SPACE", onKeyPress, Modifier.weight(1f))
//            KeyButton("ENTER", onKeyPress)
//            KeyButton("BACKSPACE", onKeyPress)
//        }

}


