package com.example.hospitium

import android.app.AlertDialog
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hospitium.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Giriş butonuna tıklanınca
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailEditText.error = "Enter valid email"
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 6) {
                binding.passwordEditText.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        // TODO: Yönlendirme yapılabilir (main ekran vs.)
                    } else {
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // Kayıt olma yazısına tıklanınca
        binding.registerText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Şifremi unuttum
        binding.forgotPasswordText.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun showForgotPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.activity_forgot_password, null)
        val emailInput = dialogView.findViewById<EditText>(R.id.emailInput)
        val resetBtn = dialogView.findViewById<Button>(R.id.resetPasswordBtn)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        resetBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Reset link sent to email", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        dialog.show()
    }
}
