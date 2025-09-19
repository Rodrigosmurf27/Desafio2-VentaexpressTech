package com.udb.ventaexpress.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.udb.ventaexpress.controller.AuthController
import com.udb.ventaexpress.databinding.ActivityRegisterBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var b: ActivityRegisterBinding
    private val auth = AuthController()
    private val scope = MainScope()
    private lateinit var callbackManager: CallbackManager

    companion object { private const val TAG = "RegisterActivity" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(b.root)

        // -------- Registro con correo/contraseña --------
        b.btnRegister.setOnClickListener {
            val email = b.etEmail.text.toString().trim()
            val pass  = b.etPassword.text.toString().trim()
            val conf  = b.etConfirm.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty() || conf.isEmpty()) { toast(getString(com.udb.ventaexpress.R.string.msg_fill_all)); return@setOnClickListener }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { toast("Correo inválido"); return@setOnClickListener }
            if (pass.length < 6) { toast("La contraseña debe tener al menos 6 caracteres"); return@setOnClickListener }
            if (pass != conf) { toast(getString(com.udb.ventaexpress.R.string.msg_password_mismatch)); return@setOnClickListener }

            scope.launch {
                try {
                    Log.v(TAG, "register email: $email")
                    auth.register(email, pass)
                    toast(getString(com.udb.ventaexpress.R.string.msg_registered))
                    goToLogin()
                } catch (e: Exception) {
                    Log.v(TAG, "register email FAIL: ${e.localizedMessage}", e)
                    toast("Error: ${e.message}")
                }
            }
        }

        // -------- Registro con GitHub --------
        b.btnRegisterGitHub.setOnClickListener {
            scope.launch {
                try {
                    Log.v(TAG, "register github start")
                    auth.signInWithGitHub(this@RegisterActivity)
                    Log.v(TAG, "register github OK")
                    // Para que sea "registro": salimos y vamos al Login
                    auth.logout()
                    toast("Cuenta creada con GitHub. Inicia sesión.")
                    goToLogin()
                } catch (e: Exception) {
                    Log.v(TAG, "register github FAIL: ${e.localizedMessage}", e)
                    toast("Error con GitHub: ${e.message}")
                }
            }
        }

        // -------- Registro con Facebook --------
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                scope.launch {
                    try {
                        val token = result.accessToken?.token
                        if (token.isNullOrEmpty()) { toast("Token inválido"); return@launch }
                        Log.v(TAG, "register facebook onSuccess: token recibido")
                        auth.signInWithFacebook(token)
                        Log.v(TAG, "register facebook OK")
                        // "Registro": cerramos sesión y vamos a Login
                        auth.logout()
                        toast("Cuenta creada con Facebook. Inicia sesión.")
                        goToLogin()
                    } catch (e: Exception) {
                        Log.v(TAG, "register facebook FAIL: ${e.localizedMessage}", e)
                        toast("Error con Facebook: ${e.message}")
                    }
                }
            }
            override fun onCancel() { toast("Proceso cancelado") }
            override fun onError(error: FacebookException) {
                Log.v(TAG, "facebook SDK error: ${error.localizedMessage}", error)
                toast("Error con Facebook: ${error.message}")
            }
        })

        b.btnRegisterFacebook.setOnClickListener {
            // Permisos mínimos
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
        }
    }

    // Redirigir resultado a Facebook SDK
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (::callbackManager.isInitialized) callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun goToLogin() {
        val i = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(i)
        finish()
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}
