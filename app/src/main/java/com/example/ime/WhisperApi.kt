import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface WhisperApi {
    @Multipart
    @POST("openai/v1/audio/transcriptions")
    suspend fun transcribe(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("model") model: RequestBody,
        @Part("language") language: RequestBody? = null,
        @Part("response_format") responseFormat: RequestBody? = null,
        @Part("timestamp_granularities[]") timestampGranularities: RequestBody? = null,
        @Part("temperature") temperature: RequestBody? = null
    ): Response<GroqTranscriptionResponse>
}

data class GroqTranscriptionResponse(
    val text: String,
    val x_groq: GroqMeta? = null
)

data class GroqMeta(
    val id: String
)

