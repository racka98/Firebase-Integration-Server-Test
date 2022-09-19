package work.racka.plugins

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.*
import java.io.InputStream

fun Application.configureFirebase() {
    val serviceAccount: InputStream? = this::class.java.classLoader
        .getResourceAsStream("ktor-server-admin-sdk.json") // Not In Use
    val firebaseJsonEnv = System.getenv("FIREBASE_JSON_STRING")
    val input = firebaseJsonEnv.byteInputStream()

    val options: FirebaseOptions = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(input))
        .build()

    FirebaseApp.initializeApp(options)
}