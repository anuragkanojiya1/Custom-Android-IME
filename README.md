# 🎤 AI-Powered Custom Keyboard (IME) with Groq Whisper STT

This project implements a **custom Android Input Method Editor (IME)** built with **Jetpack Compose** that allows users to input text by speaking.  
It integrates with [Groq Whisper](https://console.groq.com/docs/speech-to-text) for **speech-to-text transcription**.

---

## Demo

https://github.com/user-attachments/assets/ba010d15-ef60-4055-b332-25c1543a0d58

## ✨ Features
- 🖊️ **Custom Keyboard (IME)** using `InputMethodService` + Jetpack Compose.  
- 🎙️ **Voice Input Button** that records audio and sends it to **Groq Whisper**.  
- ⚡ **Real-time transcription** support via Groq’s `whisper-large-v3-turbo` model.  
- 📝 Inserts transcribed text directly into the current input field (`EditText`, `TextField`, etc).  
- 🎨 Animated mic button with **pulse effects**.  
