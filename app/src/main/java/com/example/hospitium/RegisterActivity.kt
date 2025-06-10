package com.example.hospitium

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hospitium.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    // Firebase Realtime Database bağlantısı
    private val database = FirebaseDatabase.getInstance("https://hospitium-43484-default-rtdb.firebaseio.com/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Kayıt butonuna tıklama
        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        // Geri butonuna tıklama
        binding.btnBack.setOnClickListener {
            goToLogin()
        }
    }

    // 🔙 Sistem geri tuşuna basıldığında login sayfasına dön
    override fun onBackPressed() {
        goToLogin()
    }

    // Kayıt ol
    private fun registerUser() {
        val fullName = binding.etFullName.text.toString().trim()
        val tcNo = binding.etTcNo.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        val genderId = binding.genderGroup.checkedRadioButtonId
        val roleId = binding.roleGroup.checkedRadioButtonId

        // ➤ Giriş Kontrolleri
        if (fullName.isEmpty()) {
            binding.etFullName.error = "Full Name is required"
            binding.etFullName.requestFocus()
            return
        }
        if (tcNo.isEmpty() || tcNo.length != 11) {
            binding.etTcNo.error = "Valid TC Identity No is required (11 digits)"
            binding.etTcNo.requestFocus()
            return
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Valid Email is required"
            binding.etEmail.requestFocus()
            return
        }
        if (phone.isEmpty()) {
            binding.etPhone.error = "Phone Number is required"
            binding.etPhone.requestFocus()
            return
        }
        if (password.isEmpty() || password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            binding.etPassword.requestFocus()
            return
        }
        if (genderId == -1) {
            Toast.makeText(this, "Please select Gender", Toast.LENGTH_SHORT).show()
            return
        }
        if (roleId == -1) {
            Toast.makeText(this, "Please select Role", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedGender = findViewById<RadioButton>(genderId).text.toString()
        val selectedRole = findViewById<RadioButton>(roleId).text.toString()

        // ➤ Firebase Authentication ile kayıt ol
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val user = User(
                        fullName = fullName,
                        tcNo = tcNo,
                        email = email,
                        phone = phone,
                        gender = selectedGender,
                        role = selectedRole
                    )

                    userId?.let {
                        database.getReference("Users").child(it)
                            .setValue(user)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "Registration successful", Toast.LENGTH_LONG).show()
                                    goToLogin()
                                } else {
                                    Toast.makeText(this, "Failed to save user data: ${dbTask.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                } else {
                    // E-posta zaten kayıtlıysa giriş yap
                    val errorMessage = task.exception?.message
                    if (errorMessage?.contains("email address is already in use") == true) {
                        signInExistingUser(email, password)
                    } else {
                        Toast.makeText(this, "Registration failed: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    // ➤ Eğer kullanıcı zaten varsa giriş yap ve Login ekranına git
    private fun signInExistingUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Already registered. Signed in successfully!", Toast.LENGTH_LONG).show()
                    goToLogin()
                } else {
                    Toast.makeText(this, "User exists but login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    // ➤ Login ekranına yönlendir ve bu ekranı kapat
    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

// ✅ Kullanıcı model sınıfı
data class User(
    val fullName: String = "",
    val tcNo: String = "",
    val email: String = "",
    val phone: String = "",
    val gender: String = "",
    val role: String = ""
)
