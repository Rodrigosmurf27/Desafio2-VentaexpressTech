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
import com.udb.ventaexpress.databinding.ActivityLoginBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var b: ActivityLoginBinding
    private val auth = AuthController()
    private val scope = MainScope()
    private lateinit var callbackManager: CallbackManager

    companion object { private const val TAG = "LoginActivity" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Si ya está logueado, entra directo
        auth.currentUser()?.let { goMenu(); return }

        // -------- Email/Password --------
        b.btnLogin.setOnClickListener {
            val email = b.etEmail.text.toString().trim()
            val pass  = b.etPassword.text.toString().trim()
            if (email.isEmpty() || pass.isEmpty()) { toast("Completa todos los campos"); return@setOnClickListener }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { toast("Correo inválido"); return@setOnClickListener }
            if (pass.length < 6) { toast("La contraseña debe tener al menos 6 caracteres"); return@setOnClickListener }

            scope.launch {
                try {
                    Log.v(TAG, "Email login: $email")
                    auth.login(email, pass)
                    goMenu()
                } catch (e: Exception) {
                    Log.v(TAG, "Email login FAIL: ${e.localizedMessage}", e)
                    toast("Error: ${e.message}")
                }
            }
        }

        b.tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // -------- GitHub --------
        b.btnLoginGitHub.setOnClickListener {
            scope.launch {
                try {
                    Log.v(TAG, "GitHub login start")
                    auth.signInWithGitHub(this@LoginActivity)
                    Log.v(TAG, "GitHub login OK")
                    goMenu()
                } catch (e: Exception) {
                    Log.v(TAG, "GitHub login FAIL: ${e.localizedMessage}", e)
                    toast("Error con GitHub: ${e.message}")
                }
            }
        }

        // -------- Facebook --------
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                scope.launch {
                    try {
                        val token = result.accessToken?.token
                        if (token.isNullOrEmpty()) { toast("Token inválido"); return@launch }
                        Log.v(TAG, "Facebook onSuccess: token recibido")
                        auth.signInWithFacebook(token)
                        Log.v(TAG, "Facebook login OK")
                        goMenu()
                    } catch (e: Exception) {
                        Log.v(TAG, "Facebook signIn FAIL: ${e.localizedMessage}", e)
                        toast("Error con Facebook: ${e.message}")
                    }
                }
            }
            override fun onCancel() { toast("Inicio de Facebook cancelado") }
            override fun onError(error: FacebookException) {
                Log.v(TAG, "Facebook SDK error: ${error.localizedMessage}", error)
                toast("Error con Facebook: ${error.message}")
            }
        })

        b.btnLoginFacebook.setOnClickListener {
            // Permisos mínimos recomendados
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (::callbackManager.isInitialized) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun goMenu() {
        val i = Intent(this, MenuActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(i)
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}
