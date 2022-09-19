package work.racka.config.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.InputStream

/**
 * Initialization for Firebase application.
 */
object FirebaseAdmin {
    private val serviceAccount: InputStream? = this::class.java.classLoader
        .getResourceAsStream("ktor-server-admin-sdk.json")

    private val options: FirebaseOptions = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build()

    fun init(): FirebaseApp = FirebaseApp.initializeApp(options)
}