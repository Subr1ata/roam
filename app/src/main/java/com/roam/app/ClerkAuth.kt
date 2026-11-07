package com.roam.app

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.clerk.api.Clerk
import com.clerk.api.auth.types.VerificationType
import com.clerk.api.network.model.error.ClerkErrorResponse
import com.clerk.api.network.serialization.ClerkResult
import com.clerk.api.network.serialization.errorMessage
import com.clerk.api.signin.SignIn
import com.clerk.api.signin.verifyCode
import com.clerk.api.signup.SignUp
import com.clerk.api.signup.sendEmailCode as sendSignUpEmailCode
import com.clerk.api.signin.sendEmailCode as sendSignInEmailCode
import com.clerk.api.signup.verifyCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun interface ClerkAuthStateListener {
    fun onStateChanged(initialized: Boolean, signedIn: Boolean)
}

interface ClerkAuthActionListener {
    fun onLoadingChanged(loading: Boolean)

    fun onCodeSent(email: String, signIn: Boolean)

    fun onSignedOut()

    fun onError(message: String)
}

object ClerkAuth {
    @JvmStatic
    fun isConfigured(): Boolean =
        BuildConfig.CLERK_PUBLISHABLE_KEY.startsWith("pk_test_") ||
            BuildConfig.CLERK_PUBLISHABLE_KEY.startsWith("pk_live_")

    @JvmStatic
    fun observe(
        activity: AppCompatActivity,
        listener: ClerkAuthStateListener,
    ): Job = activity.lifecycleScope.launch {
        combine(Clerk.isInitialized, Clerk.userFlow) { initialized, user ->
            initialized to (user != null)
        }.collect { (initialized, signedIn) ->
            listener.onStateChanged(initialized, signedIn)
        }
    }

    @JvmStatic
    fun createAccount(
        activity: AppCompatActivity,
        fullName: String,
        email: String,
        password: String,
        listener: ClerkAuthActionListener,
    ): Job = request(activity, listener) {
        val nameParts = fullName.trim().split(Regex("\\s+"), limit = 2)
        val signUp = Clerk.auth.signUp {
            this.email = email
            this.password = password
            firstName = nameParts.first()
            lastName = nameParts.getOrElse(1) { "" }.ifBlank { null }
        }.valueOrReport(listener) ?: return@request

        signUp.sendSignUpEmailCode().valueOrReport(listener) ?: return@request
        listener.onCodeSent(email, false)
    }

    @JvmStatic
    fun requestSignInCode(
        activity: AppCompatActivity,
        email: String,
        listener: ClerkAuthActionListener,
    ): Job = request(activity, listener) {
        Clerk.auth.signInWithOtp { this.email = email }.valueOrReport(listener) ?: return@request
        listener.onCodeSent(email, true)
    }

    @JvmStatic
    fun verifyEmailCode(
        activity: AppCompatActivity,
        code: String,
        signIn: Boolean,
        listener: ClerkAuthActionListener,
    ): Job = request(activity, listener) {
        if (signIn) {
            val currentSignIn = Clerk.auth.currentSignIn ?: run {
                listener.onError("Your sign-in code expired. Request a new code.")
                return@request
            }
            val verified = currentSignIn.verifyCode(code).valueOrReport(listener) ?: return@request
            if (verified.status != SignIn.Status.COMPLETE) {
                listener.onError("This account needs another verification step. Contact support to continue.")
                return@request
            }
            activateSession(verified.createdSessionId, listener)
        } else {
            val currentSignUp = Clerk.auth.currentSignUp ?: run {
                listener.onError("Your sign-up code expired. Start sign-up again.")
                return@request
            }
            val verified = currentSignUp.verifyCode(code, VerificationType.EMAIL)
                .valueOrReport(listener) ?: return@request
            if (verified.status != SignUp.Status.COMPLETE) {
                listener.onError("Your account needs more information before sign-up can finish.")
                return@request
            }
            activateSession(verified.createdSessionId, listener)
        }
    }

    @JvmStatic
    fun resendEmailCode(
        activity: AppCompatActivity,
        email: String,
        signIn: Boolean,
        listener: ClerkAuthActionListener,
    ): Job = request(activity, listener) {
        if (signIn) {
            val currentSignIn = Clerk.auth.currentSignIn ?: run {
                listener.onError("Your sign-in code expired. Request a new code.")
                return@request
            }
            currentSignIn.sendSignInEmailCode(email).valueOrReport(listener) ?: return@request
        } else {
            val currentSignUp = Clerk.auth.currentSignUp ?: run {
                listener.onError("Your sign-up code expired. Start sign-up again.")
                return@request
            }
            currentSignUp.sendSignUpEmailCode().valueOrReport(listener) ?: return@request
        }
        listener.onCodeSent(email, signIn)
    }

    @JvmStatic
    fun signOut(
        activity: AppCompatActivity,
        listener: ClerkAuthActionListener,
    ): Job = request(activity, listener) {
        Clerk.auth.signOut().valueOrReport(listener) ?: return@request
        listener.onSignedOut()
    }

    private suspend fun activateSession(
        sessionId: String?,
        listener: ClerkAuthActionListener,
    ) {
        if (sessionId.isNullOrBlank()) {
            listener.onError("Clerk verified the code but did not return an active session.")
            return
        }
        Clerk.auth.setActive(sessionId = sessionId).valueOrReport(listener)
    }

    private fun request(
        activity: AppCompatActivity,
        listener: ClerkAuthActionListener,
        action: suspend () -> Unit,
    ): Job = activity.lifecycleScope.launch {
        listener.onLoadingChanged(true)
        try {
            action()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            listener.onError(error.message ?: "The authentication request failed. Please try again.")
        } finally {
            listener.onLoadingChanged(false)
        }
    }

    private fun <T : Any> ClerkResult<T, ClerkErrorResponse>.valueOrReport(
        listener: ClerkAuthActionListener,
    ): T? = when (this) {
        is ClerkResult.Success -> value
        is ClerkResult.Failure -> {
            listener.onError(errorMessage)
            null
        }
    }
}
