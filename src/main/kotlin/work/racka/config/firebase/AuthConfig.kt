package work.racka.config.firebase

import com.google.firebase.auth.FirebaseAuth
import work.racka.auth.firebase.FirebaseAuthenticationProvider
import work.racka.model.User

object AuthConfig {
    fun FirebaseAuthenticationProvider.Configuration.configure() {
        principal = { uid ->
            try {
                val userRecord = FirebaseAuth.getInstance().getUser(uid)
                userRecord.displayName
                User(uid, userRecord.displayName)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}