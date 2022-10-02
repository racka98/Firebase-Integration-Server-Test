package work.racka.auth.firebase

import com.google.firebase.ErrorCode
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserRecord.CreateRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.slf4j.LoggerFactory

private val firebaseAuthLogger = LoggerFactory.getLogger("io.robothouse.auth.firebase")

class FirebaseAuthenticationProvider internal constructor(config: Configuration) :
    AuthenticationProvider(config) {

    private val token: (ApplicationCall) -> String? = config.token
    private val principle: ((uid: String) -> Principal?)? = config.principal
    private val challenge: suspend () -> Unit = config.onFailedAuth

    class Configuration internal constructor(name: String?) : AuthenticationProvider.Config(name) {
        internal var token: (ApplicationCall) -> String? = { call ->
            call.request.parseAuthorizationToken()
        }

        internal var principal: ((uid: String) -> Principal?)? = null

        internal var onFailedAuth: suspend () -> Unit = {}

        internal fun build() = FirebaseAuthenticationProvider(this)
    }

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        try {
            val token = this.token(context.call) ?: throw FirebaseAuthException(
                FirebaseException(
                    ErrorCode.UNAUTHENTICATED,
                    "No Token can be found",
                    null
                )
            )
            val uid = FirebaseAuth.getInstance().verifyIdToken(token).uid
            principle?.let {
                it.invoke(uid)?.let { principal ->
                    context.principal(principal)
                }
            }
        } catch (e: Throwable) {
            val message = if (e is FirebaseException) {
                "Authentication Failed: ${e.message ?: e.javaClass.simpleName}"
            } else {
                e.message ?: e.javaClass.simpleName
            }
            firebaseAuthLogger.trace(message)
            context.call.respond(HttpStatusCode.Unauthorized, message)
            context.challenge.complete()
            challenge()
        }
    }
}

fun AuthenticationConfig.firebase(
    name: String? = null,
    configure: FirebaseAuthenticationProvider.Configuration.() -> Unit
) {
    val provider = FirebaseAuthenticationProvider.Configuration(name).apply(configure).build()
    register(provider)
}

fun ApplicationRequest.parseAuthorizationToken(): String? = authorization()?.let {
    it.split(" ")[1]
}

fun createUser() {
    val user = CreateRequest().setEmail("")
}